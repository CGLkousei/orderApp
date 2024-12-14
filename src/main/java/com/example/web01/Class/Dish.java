package com.example.web01.Class;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dish {
    private String name;
    private int price;
    private String description;

    public Dish(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Dish(){
        this("", 0, "");
    }
}
