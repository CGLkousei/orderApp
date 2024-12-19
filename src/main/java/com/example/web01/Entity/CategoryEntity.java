package com.example.web01.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@Entity
@Table(name = "Category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

    @OneToMany(mappedBy = "category")
    private List<DishEntity> dishes;

    public CategoryEntity(Long id, String name, RestaurantEntity restaurant, List<DishEntity> dishes) {
        this.id = id;
        this.name = name;
        this.restaurant = restaurant;
        this.dishes = dishes;
    }

    public CategoryEntity(String name) {
        this(-1L, name, new RestaurantEntity(), new ArrayList<>());
    }

    public CategoryEntity() {
        this(-1L, "", new RestaurantEntity(), new ArrayList<>());
    }
}
