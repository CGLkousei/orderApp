package com.example.web01.Controller;

import com.example.web01.Class.Customer;
import com.example.web01.Data.ParamDishes;
import com.example.web01.Entity.CategoryEntity;
import com.example.web01.Entity.DishEntity;
import com.example.web01.Entity.RestaurantEntity;
import com.example.web01.Entity.SeatEntity;
import com.example.web01.Service.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            model.addAttribute("customers", new HashMap<Long, Customer>());
            return "restaurant/newCustomer";
        }
        else if("popCustomer".equals(action)){
            return "restaurant/checkCustomer";
        }
        else if("modifyDish".equals(action)){
            ParamDishes pd = new ParamDishes();
            pd.setCategories(restaurant.getCategories());
//           List<CategoryEntity> categories = restaurant.getCategories();
           model.addAttribute("paramDishes", pd);
//           model.addAttribute("categories", categories);
            return "restaurant/modifyDish";
        }
        else if("modifyInfo".equals(action)){
            model.addAttribute("isEditMode", false);
            return "restaurant/modifyInformation";
        }

        model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
        return "restaurant/errorPage";
    }

    @Transactional
    @PostMapping("/{restaurantId}/Update/Dishes")
    public String updateDish(@PathVariable Long restaurantId, @ModelAttribute("paramDishes")@Validated ParamDishes pd, BindingResult bindingResult, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }
        model.addAttribute("restaurant", restaurant);

        if(bindingResult.hasErrors()){
            model.addAttribute("paramDishes", pd);
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                System.out.println("エラー発生フィールド: " + fieldError.getField());
                System.out.println("エラーメッセージ: " + fieldError.getDefaultMessage());
            }
            return "restaurant/modifyDish";
        }

        List<CategoryEntity> newCategories = pd.getCategories();
        for(CategoryEntity category : newCategories){
            CategoryEntity addCategory = null;
            if(category.getRestaurant().getId() == null){
                category.setRestaurant(restaurant);
                category.setId(null);
                addCategory = categoryService.saveCategory(category);
            }

            for(DishEntity dish : category.getDishes()){
                if(dish.getId() == null){
                    if(addCategory != null){
                        dish.setCategory(addCategory);
                    }
                    else{
                        dish.setCategory(category);
                    }
                }

                dishService.saveDish(dish);
            }

            List<DishEntity> existDishes = dishService.getDishesByCategoryId(category.getId());
            for(DishEntity dish : existDishes){
                if(!isExistDishesList(category.getDishes(), dish.getId())){
                    dishService.deleteDishById(dish.getId());
                }
            }
        }

        List<CategoryEntity> existCategories = categoryService.getCategoriesByRestaurantId(restaurantId);
        for(CategoryEntity delete_category : existCategories){
            if(!isExistCategoriesList(newCategories, delete_category.getId())){
                for(DishEntity delete_dish : delete_category.getDishes()){
                    dishService.deleteDishById(delete_dish.getId());
                }
                categoryService.deleteCategoryById(delete_category.getId());
            }
        }

        model.addAttribute("message", "Update Successful.");

        return "/restaurant/restaurantHome";
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

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("message", "Update Successful.");
        return "restaurant/restaurantHome";
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

    @PostMapping("/{restaurantId}/Customer/Add")
    public String addNewCustomer(@PathVariable Long restaurantId, @RequestParam("seatId") int seatId, Model model) {
        Customer new_customer = new Customer(seatId, restaurantId.intValue());
        customerService.addCustomer(restaurantId, new_customer);

        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));

        return "restaurant/newCustomer";
    }

    public RestaurantEntity getRestaurant(long restaurant_id){
        Optional<RestaurantEntity> restaurantEntity = restaurantService.getRestaurantById(restaurant_id);
        if(!restaurantEntity.isPresent()){
            return null;
        }

        return restaurantEntity.get();
    }

    public CategoryEntity getCategory(long category_id, List<CategoryEntity> categories){
        for(CategoryEntity c : categories){
            if(c.getId() == category_id)
                return c;
        }

        return null;
    }

    public boolean isExistDishesList(List<DishEntity> dishes, long id){
        for(DishEntity dish : dishes){
            if(dish.getId() == id){
                return true;
            }
        }

        return false;
    }

    public boolean isExistCategoriesList(List<CategoryEntity> categories, long id){
        for(CategoryEntity category : categories){
            if(category.getId() == id){
                return true;
            }
        }

        return false;
    }
}
