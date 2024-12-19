package com.example.web01.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@Table(name = "Dish")
public class DishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public DishEntity(Long id, String name, int price, String description, CategoryEntity category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
    }

    public DishEntity(String name, int price, String description) {
        this(-1L, name, price, description, new CategoryEntity());
    }

    public DishEntity() {
        this(-1L, "", 0, "", new CategoryEntity());
    }
}
