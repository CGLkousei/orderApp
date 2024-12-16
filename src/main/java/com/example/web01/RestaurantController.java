package com.example.web01;

import com.example.web01.Service.CategoryService;
import com.example.web01.Service.DishService;
import com.example.web01.Service.RestaurantService;
import com.example.web01.Service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SeatService seatService;

    @GetMapping("/restaurant/bill")
    public String launchCustomer(){
        return "restaurant/finishOrder";
    }
}
