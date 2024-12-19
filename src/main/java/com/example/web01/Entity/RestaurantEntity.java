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
@Table(name = "Restaurant")
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private Long seatNum;

    @OneToMany(mappedBy = "restaurant")
    private List<CategoryEntity> categories;

    public RestaurantEntity(Long id, String name, String email, Long seatNum, List<CategoryEntity> categories) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.seatNum = seatNum;
        this.categories = categories;
    }

    public RestaurantEntity(String name, String email, Long seatNum) {
        this(-1L, name, email, seatNum, new ArrayList<>());
    }

    public RestaurantEntity() {
        this(-1L, "", "", 0L, new ArrayList<>());
    }
}
