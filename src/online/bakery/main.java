package online.bakery;

import online.bakery.decorators.Decorator;
import online.bakery.decorators.DecoratorToBuild;
import online.bakery.sweets.Cake;
import online.bakery.sweets.Cookie;
import online.bakery.sweets.Sweets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class main {
    public static void main(String[] args) {
        //test1();

        test2();
    }
    
    // test2 -> test note & payment & delivery
    private static void test2() {
        ArrayList<Sweets> ss = new ArrayList<Sweets>();
        ArrayList<DecoratorToBuild> decorators = new ArrayList<DecoratorToBuild>();
        decorators.add(new DecoratorToBuild(Decorator.FLOUR, new BigDecimal(100), new BigDecimal(400)));
        decorators.add(new DecoratorToBuild(Decorator.SUGAR, new BigDecimal(300), new BigDecimal(40)));
        decorators.add(new DecoratorToBuild(Decorator.BACKGROUNDER, new BigDecimal(200), new BigDecimal(500)));
        Sweets s1 = new Cake.CakeBuilder(decorators).build();
        decorators.add(new DecoratorToBuild(Decorator.STRAWBERRY, new BigDecimal(200), new BigDecimal(500)));
        Sweets s2 = new Cookie.CookieBuilder(decorators).build();
        System.out.println(s1.getTOTAL_COST());
        ss.add(s1);
        ss.add(s2);
        //System.out.println(ss);

        ArrayList conf = new ArrayList<Confectioner>();
        Bakery b1 = new Bakery("شب شیرینی","لحظات زندگی خود را با کمک ما شیرین کنید" , "07131111111" , "تاچارا");
        b1.setScore(3);
        System.out.printf(b1.getProfile());

        Discount d1 = new Discount("تخفیف یلدایی" , 20 , new Date(1399,9,20),new Date(1399,10,1),b1.getId());
        b1.addDiscount(d1.getId());

        b1.addMenu(s1.getSweetId());
        
        //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~

        // create customer-1 with wallet
        Wallet w = new Wallet();
        Customer c = new Customer(w);

        // customer-1 order sweet 1
        ConfectionerStatus cs1 = b1.sweetToOrder(s1);
        System.out.println(cs1.toString());
        if(cs1 == ConfectionerStatus.ACCEPT){
            c.createNewSweet(s2.getSweetId(),b1.getId());
        }
        
        
        // print customer1 order-ids
        List<Integer> orderIdC = c.getOrdersID();
        for (int i : orderIdC){
            System.out.println(i+"");
        }
        
        //~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~
                
        // create employee
        Employee e1 = new Employee("سارا", "لیمویی");        
        
        // request for a delivery for customer-1 order
        DeliveryInformation c1_delivery1 = DeliveryInformation.createNewDelivery(c.getOrdersID().get(0), e1.getId(), "معالی آباد، کوچه ی سوم", new Date(2021, 1, 15, 16, 30));
        System.out.println(c1_delivery1.getDeliveryInformation());
        
        // create a note for the order
        Note order1_note = new Note(c.getOrdersID().get(0), b1.getId(), c.getID(), e1.getId());
        // employee add a text to note
        e1.addNote(order1_note, "تحویل داده خواهد شد توسط اینجانب");        
        System.out.println(order1_note.getNoteInformation());
        
        // pay the order
        PaymentType paymentType = Payment.howToPay();
        Payment payment = new Payment(c.getOrdersID().get(0), c.getID(), new Date(), new BigDecimal(400), "خرید کیک از شب شیرینی", paymentType);

        Payment walletChargePay = Payment.chargeWallet(c, "شارژ کیف پول");
        System.out.println(walletChargePay.getPaymentInformation());
        
        System.out.println(c.getWallet().getWalletInformation());
        //System.out.println(payment.getPaymentInformation());

        if (payment.pay(payment, c) == true) {
            System.out.println(payment.getPaymentInformation());
            
            // deliver the order
            e1.deliverOrder(c1_delivery1);
            System.out.println(c1_delivery1.getDeliveryInformation());
        }

        //System.out.println(payment.getPaymentInformation());
        System.out.println(c.getWallet().getWalletInformation());
        
        
    }
    
    
    // test1 -> test sweets and bakery and confectionary
    private static void test1() {
        ArrayList<Sweets> ss = new ArrayList<Sweets>();
        ArrayList<DecoratorToBuild> decorators = new ArrayList<DecoratorToBuild>();
        decorators.add(new DecoratorToBuild(Decorator.FLOUR, new BigDecimal(100), new BigDecimal(400)));
        decorators.add(new DecoratorToBuild(Decorator.SUGAR, new BigDecimal(300), new BigDecimal(40)));
        decorators.add(new DecoratorToBuild(Decorator.BACKGROUNDER, new BigDecimal(200), new BigDecimal(500)));
        Sweets s1 = new Cake.CakeBuilder(decorators).build();
        decorators.add(new DecoratorToBuild(Decorator.STRAWBERRY, new BigDecimal(200), new BigDecimal(500)));
        Sweets s2 = new Cookie.CookieBuilder(decorators).build();
        System.out.println(s1.getTOTAL_COST());
        ss.add(s1);
        ss.add(s2);
        //System.out.println(ss);

        ArrayList conf = new ArrayList<Confectioner>();
        Bakery b1 = new Bakery("شب شیرینی","لحظات زندگی خود را با کمک ما شیرین کنید" , "07131111111" , "تاچارا");
        b1.setScore(3);
        System.out.printf(b1.getProfile());

        Discount d1 = new Discount("تخفیف یلدایی" , 20 , new Date(1399,9,20),new Date(1399,10,1),b1.getId());
        b1.addDiscount(d1.getId());

        b1.addMenu(s1.getSweetId());

        Wallet w = new Wallet();
        Customer c = new Customer(w);

        ConfectionerStatus cs1 = b1.sweetToOrder(s2);
        System.out.println(cs1.toString());
        if(cs1 == ConfectionerStatus.ACCEPT){
            c.createNewSweet(s2.getSweetId(),b1.getId());

        }
        List<Integer> orderIdC = c.getOrdersID();
        for (int i : orderIdC){
            System.out.println(i+"");
        }
    }
}
