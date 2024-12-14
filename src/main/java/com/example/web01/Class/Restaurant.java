package com.example.web01.Class;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Restaurant {
    private int id;
    private String name;
    private String mail;
    private List<Dish> dishes;

    public Restaurant(int id, String name, String mail, List<Dish> dishes) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.dishes = dishes;
    }

    public Restaurant(int id, String name, String mail) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.dishes = new ArrayList<>();
    }

    public Restaurant(){
        this(0, "", "");
    }
}
