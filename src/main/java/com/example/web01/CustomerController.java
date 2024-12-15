package com.example.web01;

import com.example.web01.Class.Customer;
import com.example.web01.Entity.CategoryEntity;
import com.example.web01.Entity.DishEntity;
import com.example.web01.Entity.RestaurantEntity;
import com.example.web01.Service.CategoryService;
import com.example.web01.Service.DishService;
import com.example.web01.Service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    @GetMapping("/customer/launch")
    public String launchCustomer(){
        return "customer/sample";
    }
    @PostMapping("/customer/home")
    public String displayCustomerHome(Model model){
        List<RestaurantEntity> restaurants = restaurantService.getAllRestaurants();
        List<CategoryEntity> categories = categoryService.getAllCategories();
        List<DishEntity> dishes = dishService.getAllDishes();

        model.addAttribute("restaurants", restaurants);

        return "customer/customerHome";
    }

}
