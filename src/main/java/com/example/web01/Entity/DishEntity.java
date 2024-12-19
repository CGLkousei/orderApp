package com.example.web01.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "料理名は必須です")
    @Size(min = 1, max = 200, message = "料理名は1文字以上、200文字以内にしてください")
    private String name;

    @NotNull(message = "価格は必須です")
    @Min(value = 0, message = "価格は0円以上にしてください")
    private Integer price;

    @Size(max = 200, message = "説明は200文字以内にしてください")
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
        this(null, name, price, description, new CategoryEntity());
    }

    public DishEntity() {
        this(null, "", 0, "", new CategoryEntity());
    }
}
