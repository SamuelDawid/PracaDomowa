package org.yellowflash.zad02Lombok;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    List<Order> orderList;
    public OrderService(){
        this.orderList = new ArrayList<>();
    }
}
