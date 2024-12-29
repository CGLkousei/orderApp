package com.example.web01.Data;

import com.example.web01.Entity.DishEntity;
import lombok.Data;

@Data
public class Order {
    private DishEntity dish;
    private int number;
    private int total;
}
