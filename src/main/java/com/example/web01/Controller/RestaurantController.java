package com.example.web01.Controller;

import com.example.web01.Class.Customer;
import com.example.web01.Entity.RestaurantEntity;
import com.example.web01.Entity.SeatEntity;
import com.example.web01.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/{restaurantId}/home")
    public String addCustomerDisplay(@PathVariable Long restaurantId, Model model){
        Optional<RestaurantEntity> restaurantEntity = restaurantService.getRestaurantById(restaurantId);
        if(!restaurantEntity.isPresent()){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }
        RestaurantEntity restaurant = restaurantEntity.get();

        model.addAttribute("restaurant", restaurant);

        return "restaurant/restaurantHome";
    }

    @PostMapping("/{restaurantId}/handleBtn")
    public String handleBtnFunc(@PathVariable Long restaurantId, @RequestParam("action") String action, Model model){
        if("addCustomer".equals(action)){
            return "restaurant/newCustomer";
        }
        else if("popCustomer".equals(action)){
            return "restaurant/checkCustomer";
        }
        else if("modifyDish".equals(action)){
            return "restaurant/modifyDish";
        }

        model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
        return "restaurant/errorPage";
    }
}
