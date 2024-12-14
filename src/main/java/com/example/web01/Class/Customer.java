package com.example.web01.Class;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Map<Integer, Integer> getOrder() {
        return order;
    }

    public void setOrder(Map<Integer, Integer> order) {
        this.order = order;
    }
}
