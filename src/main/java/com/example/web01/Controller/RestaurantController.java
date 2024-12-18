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
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("restaurant", restaurant);

        return "restaurant/restaurantHome";
    }

    @PostMapping("/{restaurantId}/handleBtn")
    public String handleBtnFunc(@PathVariable Long restaurantId, @RequestParam("action") String action, Model model){
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("restaurant", restaurant);
        if("addCustomer".equals(action)){
            return "restaurant/newCustomer";
        }
        else if("popCustomer".equals(action)){
            return "restaurant/checkCustomer";
        }
        else if("modifyDish".equals(action)){
            return "restaurant/modifyDish";
        }
        else if("modifyInfo".equals(action)){
            model.addAttribute("isEditMode", false);
            return "restaurant/modifyInformation";
        }

        model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
        return "restaurant/errorPage";
    }

    @GetMapping("/{restaurantId}/Edit/Info")
    public String editRestaurantInfo(@PathVariable Long restaurantId, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("isEditMode", true);
        model.addAttribute("restaurant", restaurant);

        return "restaurant/modifyInformation";
    }

    @PostMapping("/{restaurantId}/Update/Info")
    public String updateRestaurantInfo(@PathVariable Long restaurantId, @ModelAttribute RestaurantEntity restaurant, Model model){
        RestaurantEntity restaurant_query = getRestaurant(restaurantId);
        if(restaurant_query == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }
        if(restaurant_query.getId() != restaurant.getId()){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        RestaurantEntity updatedRestaurant = restaurantService.updateRestaurant(restaurant);
        if(updatedRestaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("isEditMode", false);
        model.addAttribute("restaurant", updatedRestaurant);
        return "restaurant/modifyInformation";
    }

    @GetMapping("/{restaurantId}/Confirm/Info")
    public String confirmRestaurantInfo(@PathVariable Long restaurantId, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("isEditMode", false);
        model.addAttribute("restaurant", restaurant);

        return "restaurant/modifyInformation";
    }


    public RestaurantEntity getRestaurant(long restaurant_id){
        Optional<RestaurantEntity> restaurantEntity = restaurantService.getRestaurantById(restaurant_id);
        if(!restaurantEntity.isPresent()){
            return null;
        }

        RestaurantEntity restaurant = restaurantEntity.get();
        return restaurant;
    }
}
