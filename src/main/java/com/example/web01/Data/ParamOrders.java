package com.example.web01.Data;

import com.example.web01.Entity.CategoryEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ParamOrders {
    private Map<Long, Integer> orders = new HashMap<>();

}
