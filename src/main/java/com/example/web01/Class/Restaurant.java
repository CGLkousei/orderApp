package com.example.web01.Class;
import java.util.ArrayList;
import java.util.List;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}
