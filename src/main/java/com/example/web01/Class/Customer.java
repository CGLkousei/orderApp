package com.example.web01.Class;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Customer {
    private int seatId;
    private int numOfPeople;
    private LocalDate registrationDate;
    private int totalMoney;
    private int restaurantId;
    private Map<Integer, Integer> order;

    public Customer(int seatId, int restaurantId) {
        this.seatId = seatId;
        this.restaurantId = restaurantId;

        this.numOfPeople = 0;
        this.totalMoney = 0;
        this.registrationDate = LocalDate.now();
        this.order = new HashMap<>();
    }

    public Customer(){
        this(0, 0);
    }
}
