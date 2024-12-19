package com.example.web01.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "店名は必須です")
    @Size(min = 1, max = 200, message = "店名は1文字以上、200文字以内にしてください")
    private String name;

    private String email;

    @NotNull(message = "席数は必須です")
    @Min(value = 0, message = "席数は0以上にしてください")
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
        this(null, name, email, seatNum, new ArrayList<>());
    }

    public RestaurantEntity() {
        this(null, "", "", 0L, new ArrayList<>());
    }
}
