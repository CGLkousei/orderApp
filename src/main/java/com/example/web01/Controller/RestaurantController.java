package com.example.web01.Controller;

import com.example.web01.Class.Customer;
import com.example.web01.Data.ParamDishes;
import com.example.web01.Entity.CategoryEntity;
import com.example.web01.Entity.DishEntity;
import com.example.web01.Entity.RestaurantEntity;
import com.example.web01.Entity.SeatEntity;
import com.example.web01.Service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/{restaurantId}/Confirm/Dishes")
    public String confirmDish(@PathVariable Long restaurantId, @RequestParam(required = false) String selectedCategoryId, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        long categoryId = Long.parseLong(selectedCategoryId);
        List<CategoryEntity> categories = restaurant.getCategories();

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", getCategory(categoryId, categories));

        // 選択されたカテゴリに応じた料理を取得
        if (selectedCategoryId != null) {
            List<DishEntity> dishes = dishService.getDishesByCategoryId(Long.parseLong(selectedCategoryId));

            model.addAttribute("selectedCategoryId", selectedCategoryId);
            model.addAttribute("dishes", dishes);
        } else {
            model.addAttribute("selectedCategoryId", null);
            model.addAttribute("dishes", List.of());
        }

        return "/restaurant/modifyDish";
    }

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

        List<CategoryEntity> categories = pd.getCategories();
        model.addAttribute("categories", categories);

        return "/restaurant/sample";
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

        return restaurantEntity.get();
    }

    public CategoryEntity getCategory(long category_id, List<CategoryEntity> categories){
        for(CategoryEntity c : categories){
            if(c.getId() == category_id)
                return c;
        }

        return null;
    }
}
