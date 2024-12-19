package com.example.web01.Data;

import com.example.web01.Entity.CategoryEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParamDishes {
    private List<CategoryEntity> categories = new ArrayList<>();

}
