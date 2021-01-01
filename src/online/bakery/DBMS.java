/*
 * DBMS using singletone pattern
 */
package online.bakery;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author salidtoir
 */
public class DBMS {
    
    private static DBMS dbms = new DBMS();
    
    /**
    * Map of usernames to their password hashes.
    */
    private Map<String, String> usernamePasswordTable;

    /**
     * Map of usernames to their salts.
     */
    private Map<String, Integer> userSaltMap;

    private List<Bakery> bakeries;
    private List<Person> bakers;
    private List<Customer> customers;
    private List<Employee> employees;
    
    private List<Admin> admins;
    
    private List<Order> orders;
   
    private List<String> securityQuestions;
    private Map<String, List<String>> usernameAnswersTable; 
    
    private DBMS() {
        this.customers = new ArrayList<Customer>();
        this.employees = new ArrayList<Employee>();
        this.bakeries = new ArrayList<Bakery>();
        this.bakers = new ArrayList<Person>();  
        this.orders = new ArrayList<Order>();
        this.usernamePasswordTable = new HashMap<String, String>();
        this.userSaltMap = new HashMap<String, Integer>();
        this.securityQuestions = new ArrayList<String>();
        this.usernameAnswersTable = new HashMap<String, List<String>>();
    }
    
    // function to give access to dbms only for admin
    public static DBMS getDBMS(Account account) {
        if(account.role == Role.ADMIN) {
            return DBMS.dbms;
        }
        return null;
    }
    
    // function to give access to dbms for functions in the same class DBMS
    private static DBMS getDBMS() {
        return DBMS.dbms;
    }
    
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    /**
    * Returns a random number between 0 and 1000.
    */
    public int getRandomSalt() {
        return (int)(Math.random() * 1000);
    }

    /**
    * In real life this would probably read from a config file,
    * so you could check your code into a repo without the config file.
    */
    public String getPepper() {
        return "this is a very long random string";
    }

    public String getSimpleHash(String saltedAndPepperedPassword) {
        StringBuilder hash = new StringBuilder();
        try {
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                byte[] hashedBytes = sha.digest(saltedAndPepperedPassword.getBytes());
                char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                'a', 'b', 'c', 'd', 'e', 'f' };
                for(int idx = 0; idx < hashedBytes.length ; idx++){
                    byte b = hashedBytes[idx];
                    hash.append(digits[(b & 0xf0) >> 4]);
                    hash.append(digits[b & 0x0f]);
                }
        } catch (NoSuchAlgorithmException e) {
                // handle error here.
        }

        return hash.toString();
    }
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    // functions for Security Questions
    
    public void setQuestions(List<String> questions){
        DBMS.getDBMS().securityQuestions = questions;
    }
    
    public boolean setAnswer(String username, List<String> answers){
        if (!DBMS.getDBMS().usernameAnswersTable.containsKey(username)){
            DBMS.getDBMS().usernameAnswersTable.put(username, answers);
            return true;
        }else
            return false;
    }
    
    public List<String> getQuestions(){
        return DBMS.getDBMS().securityQuestions;
    }
    
    public List<String> getAnswers(String username){
        return DBMS.getDBMS().usernameAnswersTable.get(username);
    }
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    // functions to get orders and payments of a specific customer

    public List<Order> getCustomerOrders(Customer customer) {
        List<Order> result = new ArrayList<Order>();
        for(Order order:DBMS.getDBMS().orders) {
            if(order.getCustomerId() == customer.getID()) {
                result.add(order);
            }
        }
        return result;
    }
    
    public List<Payment> getCustomerPayments(Customer customer) {
        List<Payment> result = new ArrayList<Payment>();
        for(Order order:DBMS.getDBMS().orders) {
            if(order.getCustomerId() == customer.getID()) {
                result.add(order.getPayment());
            }
        }
        return result;
    }

    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    
    // function to find a free employee to assign a delivery to.
    public Employee getFirstFreeEmployee() {
        for(Employee employee:DBMS.getDBMS().employees) {
            if (employee.isIsBusy() == false) {
                return employee;
            }
        }
        // could not find any free employee
        return null;
    }
    
    // function to call when an employee delivers an order -> when deliverOrder in employee is called
    public boolean setEmployeeIsBusyFalse(Employee employee) {
        int index = DBMS.getDBMS().employees.indexOf(employee);
        DBMS.getDBMS().employees.get(index).setIsBusy(false);
        return true;
    }
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    // functions to edit informations in bakeries, bakers, customers & employee
    
    // edit bakery information -> gets all fileds again and set them all again
    public boolean editBakery(int id, String name, String discreption, String firstName, String lastName, String address, String contactNo) {
        int index = -1;
        for(int i= 0; i < DBMS.getDBMS().bakeries.size(); i++) {
            if(DBMS.getDBMS().bakeries.get(i).getID() == id) {
                index = i;
                break;
            }
        }
        if(index == -1) {
            return false;
        }
        
        // change fields
        DBMS.getDBMS().bakeries.get(index).setName(name);
        DBMS.getDBMS().bakeries.get(index).setDescription(discreption);
        DBMS.getDBMS().bakeries.get(index).setFirstname(firstName);
        DBMS.getDBMS().bakeries.get(index).setLastname(lastName);
        DBMS.getDBMS().bakeries.get(index).setAddress(address);
        DBMS.getDBMS().bakeries.get(index).setContactNo(contactNo);
        return true;
    }
    
    // edit baker information
    public boolean editBaker(int id, String discreption, String firstName, String lastName, String address, String contactNo) {
        int index = -1;
        for(int i= 0; i < DBMS.getDBMS().bakers.size(); i++) {
            if(DBMS.getDBMS().bakers.get(i).getID() == id) {
                index = i;
                break;
            }
        }
        if(index == -1) {
            return false;
        }
        
        // change fields
        DBMS.getDBMS().bakers.get(index).setDescription(discreption);
        DBMS.getDBMS().bakers.get(index).setFirstname(firstName);
        DBMS.getDBMS().bakers.get(index).setLastname(lastName);
        DBMS.getDBMS().bakers.get(index).setAddress(address);
        DBMS.getDBMS().bakers.get(index).setContactNo(contactNo);
        return true;
    }
    
    // edit customer information
    public boolean editCustomer(int id, String firstName, String lastName, String address, String contactNo) {
        int index = -1;
        for(int i= 0; i < DBMS.getDBMS().customers.size(); i++) {
            if(DBMS.getDBMS().customers.get(i).getID() == id) {
                index = i;
                break;
            }
        }
        if(index == -1) {
            return false;
        }
        
        // change fields
        DBMS.getDBMS().customers.get(index).setFirstname(firstName);
        DBMS.getDBMS().customers.get(index).setLastname(lastName);
        DBMS.getDBMS().customers.get(index).setAddress(address);
        DBMS.getDBMS().customers.get(index).setContactNo(contactNo);
        return true;
    }
    
    // edit employee information
    public boolean editEmployee(int id, String firstName, String lastName, String address, String contactNo) {
        int index = -1;
        for(int i= 0; i < DBMS.getDBMS().employees.size(); i++) {
            if(DBMS.getDBMS().employees.get(i).getID() == id) {
                index = i;
                break;
            }
        }
        if(index == -1) {
            return false;
        }
        
        // change fields
        DBMS.getDBMS().employees.get(index).setFirstname(firstName);
        DBMS.getDBMS().employees.get(index).setLastname(lastName);
        DBMS.getDBMS().employees.get(index).setAddress(address);
        DBMS.getDBMS().employees.get(index).setContactNo(contactNo);
        return true;
    }
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    // functions to change some fields later in lists
    
    // set actual delivery time for a specific order in delivery information list
    public boolean setActualDeliveryTime(Order order, Date actualDeliveryTime) {
        int index = DBMS.getDBMS().orders.indexOf(order);
        DBMS.getDBMS().orders.get(index).getDelivery().setActualDeliveryTime(actualDeliveryTime);
        return true;
    }
    
    // set payment status for a specific order in payments list
    public boolean setPaymentStatus(Order order, PaymentStatus paymentStatus) {
        for(Order ord : DBMS.getDBMS().orders) {
            if(ord.getOrderId() == order.getOrderId()) {
                ord.getPayment().setPaymentStatus(paymentStatus);
                return true;
            }
        }
        return false;
    }
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    
    // search for delivery information of a specific order
    public DeliveryInformation getOrderDeliverInformation(Order order) {
        return order.getDelivery();
    }
    
    // search for payment information of a specific order
    public Payment getOrderPaymentInformation(Order order) {
        return order.getPayment();
    }

    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    
    // search a bakery in its name
    public List<Bakery> searchBakeryByName(String subString) {
        List<Bakery> result = new ArrayList<Bakery>();
        for(Bakery bakery:DBMS.getDBMS().bakeries) {
            if(bakery.getName().toLowerCase().contains(subString.toLowerCase())) {
                result.add(bakery);
            }
        }
        return result;
    }
    
    // search a baker in its firstname & lastname
    public List<Person> searchBakerByName(String subString) {
        List<Person> result = new ArrayList<Person>();
        for(Person baker:DBMS.getDBMS().bakers) {
            if(baker.getFirstname().toLowerCase().contains(subString.toLowerCase()) || baker.getLastname().toLowerCase().contains(subString.toLowerCase())) {
                result.add(baker);
            }
        }
        return result;
    }
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    
    public boolean checkPasword(String username, String password){
        int salt = DBMS.getDBMS().userSaltMap.get(username);
        String saltedAndPepperedPassword = password + salt + getPepper();
        String passwordHash = getSimpleHash(saltedAndPepperedPassword);
        String storedPasswordHash = DBMS.getDBMS().usernamePasswordTable.get(username);
        return passwordHash == storedPasswordHash;
    }
    
    public boolean hasEntry(String username, String password){
        return DBMS.getDBMS().usernamePasswordTable.containsKey(username);
    }
    
    public boolean hasSalt(String username){
        return DBMS.getDBMS().userSaltMap.containsKey(username);
    }
    
    public String addEntry(String username, String password){
        int salt = getRandomSalt();
        String saltedAndPepperedPassword = password + salt + getPepper();
        String passwordHash = getSimpleHash(saltedAndPepperedPassword);
        DBMS.getDBMS().userSaltMap.put(username, salt);
        return DBMS.getDBMS().usernamePasswordTable.put(username, passwordHash);
    }
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~ 
    
    public List<Bakery> getListOfBakeries() {
        return DBMS.getDBMS().bakeries;
    }
    
    public boolean addBakery(Bakery bakery) {
        DBMS.getDBMS().bakeries.add(bakery);
        return true;
    }
    
    public boolean removeBakery(Bakery bakery) {
        DBMS.getDBMS().bakeries.remove(bakery);
        return true;
    }
    
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~    
    
    public List<Person> getListOfBakers() {
        return DBMS.getDBMS().bakers;
    }
    
    public boolean addBaker(Person baker) {
        DBMS.getDBMS().bakers.add(baker);
        return true;
    }
    
    public boolean removeBaker(Person baker) {
        DBMS.getDBMS().bakers.remove(baker);
        return true;
    }

    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~    
    // we do not delete any customer, just make it inactive.
    public List<Customer> getListOfCustomers() {
        return DBMS.getDBMS().customers;
    }
    
    public boolean addCustomer(Customer customer) {
        if (DBMS.getDBMS().customers.contains(customer) == true) {
            int index = DBMS.getDBMS().customers.indexOf(customer);
            DBMS.getDBMS().customers.get(index).setActiveness("Active");
            return true;
        }
        DBMS.getDBMS().customers.add(customer);
        return true;
    }
    
    public boolean removeCustomer(Customer customer) {
        int index = DBMS.getDBMS().customers.indexOf(customer);
        DBMS.getDBMS().customers.get(index).setActiveness("Inactive");
        DBMS.getDBMS().customers.remove(index);
        return true;
    }    
    
    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~    
    
    public List<Employee> getListOfEmployees() {
        return DBMS.getDBMS().employees;
    }
    
    public boolean addEmployee(Employee employee) {
        DBMS.getDBMS().employees.add(employee);
        return true;
    }
    
    public boolean removeEmployee(Employee employee) {
        DBMS.getDBMS().employees.remove(employee);
        return true;
    }

    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~    
    
    public List<Order> getListOfOrders() {
        return DBMS.getDBMS().orders;
    }
    
    public boolean addOrder(Order order) {
        DBMS.getDBMS().orders.add(order);
        return true;
    }

    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~    
    
    public List<Payment> getListOfPayments() {
        List<Payment> res = new ArrayList<>();
        for(Order order : DBMS.getDBMS().orders) {
            res.add(order.getPayment());
        }

        return res;
    }

    //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~    
    
    public List<DeliveryInformation> getListOfDeliveryInformations() {
        List<DeliveryInformation> res = new ArrayList<>();
        for(Order order : DBMS.getDBMS().orders) {
            res.add(order.getDelivery());
        }

        return res;
    }
    
    public List<Admin> getAdmins() {
        return DBMS.getDBMS().admins;
    }
    
    public boolean addNewAdmin(Admin admin) {
        DBMS.getDBMS().admins.add(admin);
        return true;
    }
 
}