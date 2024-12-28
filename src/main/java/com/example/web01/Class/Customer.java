package com.example.web01.Class;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Customer {
    private int seatId;
    private int numOfPeople;
    private LocalDateTime registrationDate;
    private int totalMoney;
    private int restaurantId;
    private Map<Long, Integer> order;
    private String token;

    public Customer(int seatId, int restaurantId) {
        this.seatId = seatId;
        this.restaurantId = restaurantId;

        this.numOfPeople = 0;
        this.totalMoney = 0;
        this.registrationDate = LocalDateTime.now();
        this.order = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = registrationDate.format(formatter);
        try {
            this.token = Token.generateToken(String.valueOf(restaurantId), String.valueOf(seatId), formattedDate);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public Customer(){
        this(0, 0);
    }

    public void addOrder(Map<Long, Integer> new_orders){
        for(long id : new_orders.keySet()){
            if(this.order.containsKey(id)){
                this.order.put(id, this.order.get(id) + new_orders.get(id));
            }
            else{
                this.order.put(id, new_orders.get(id));
            }
        }
    }

}
