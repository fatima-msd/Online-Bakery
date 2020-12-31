package online.bakery;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;



public class SnowSpray implements BirthdayItems {

    private final int itemId ;
    static AtomicInteger atomicInteger = new AtomicInteger(0);
    String name;
    BigDecimal cost ;

    public SnowSpray(String name,BigDecimal cost){
        this.itemId=atomicInteger.incrementAndGet();
        this.name = name;
        this.cost = cost;
    }

    public String getDescription(){
        String s = name + "\n" + cost + " Tooman" ;
        return s;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }


}
