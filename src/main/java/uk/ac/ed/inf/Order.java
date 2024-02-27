package uk.ac.ed.inf;
import java.util.List;

public class Order {

    public String orderNo;
    public List<String> items;
    public List<String> shops;
    public String deliverTo;
    public int cost;

    public Order(String orderNo, List<String> items, List<String> shops, String deliverTo, int cost){
        this.orderNo = orderNo;
        this.items = items;
        this.shops = shops;
        this.deliverTo = deliverTo;
        this.cost = cost;
    }

}
