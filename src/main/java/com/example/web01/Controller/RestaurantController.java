package com.example.web01.Controller;

import com.example.web01.Class.Customer;
import com.example.web01.Data.MultiCheckBoxForm;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Autowired
    private QRCodeService qrCodeService;

    private static final int QRCodeResolution = 200;
    private static final String HOME_URL = "http://localhost:8080/order/home";

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
            model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));
            return "restaurant/newCustomer";
        }
        else if("popCustomer".equals(action)){
            model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));
            return "restaurant/checkCustomer";
        }
        else if("makeQRcode".equals(action)) {
            String[] qrCodes = new String[restaurant.getSeatNum().intValue()];
            String[] inputs = new String[restaurant.getSeatNum().intValue()];
            List<SeatEntity> seats = restaurant.getSeats();
            int index = 0;
            for(SeatEntity seat : seats){
                inputs[index] = seat.getToken();
                index++;
            }
            try {
                for(int i = 0; i < inputs.length; i++){
                    if(inputs[i] == null)
                        continue;;

                    // QRコードを生成
                    byte[] qrCodeImage = qrCodeService.generateQRCodeImage(HOME_URL + inputs[i], QRCodeResolution, QRCodeResolution);
                    // Base64エンコード
                    String encodedQRCode = Base64.getEncoder().encodeToString(qrCodeImage);
                    // モデルにQRコードを渡す
                    qrCodes[i] = encodedQRCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            model.addAttribute("QRCodes", qrCodes);
            return "restaurant/confirmQRcode";
        }
        else if("modifyDish".equals(action)){
            ParamDishes pd = new ParamDishes();
            pd.setCategories(restaurant.getCategories());
            model.addAttribute("paramDishes", pd);
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
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        Customer new_customer = new Customer(seatId, restaurantId.intValue());
        customerService.addCustomer(restaurantId, new_customer);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));

        return "restaurant/newCustomer";
    }

    @PostMapping("/{restaurantId}/Customer/Delete")
    public String deleteCustomer(@PathVariable Long restaurantId, @RequestParam("seatId") int seatId, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        customerService.initializeCustomer(restaurantId, seatId);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));

        return "restaurant/checkCustomer";
    }

    @GetMapping("/{restaurantId}/Update/QRCode")
    public String updateDish(@PathVariable Long restaurantId, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("form", new MultiCheckBoxForm());
        return "restaurant/updateQRcode";
    }

    @PostMapping("/{restaurantId}/Update/QRCode")
    public String submit(@PathVariable Long restaurantId, @ModelAttribute MultiCheckBoxForm form, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        int[] IDs = new int[form.getSelectedOptions().size()];
        int index = 0;
        for(String id : form.getSelectedOptions()) {
            IDs[index] = Integer.parseInt(id) - 1;
            index++;
        }

        String[] qrCodes = new String[restaurant.getSeatNum().intValue()];
        String[] inputs = new String[restaurant.getSeatNum().intValue()];
        List<SeatEntity> seats = restaurant.getSeats();
        index = 0;
        for(SeatEntity seat : seats){
            final int tmp_index = index;
            boolean found = Arrays.stream(IDs).anyMatch(id -> id == tmp_index);
            if(found) {
                System.out.println("index: " + index);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = LocalDateTime.now().format(formatter);
                inputs[index] = "?restaurantId=" + restaurant.getId() + "&seatId=" + index + 1 + "&token=" + formattedDate;
            }
            else
                inputs[index] = seat.getToken();

            index++;
        }

        for(;index < inputs.length; index++){
            final int tmp_index = index;
            boolean found = Arrays.stream(IDs).anyMatch(id -> id == tmp_index);
            if(found) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = LocalDateTime.now().format(formatter);
                int seatIndex = index + 1;
                inputs[index] = "?restaurantId=" + restaurant.getId() + "&seatId=" + seatIndex + "&token=" + formattedDate;
            }
        }
        try {
            for(int i = 0; i < inputs.length; i++){
                if(inputs[i] == null)
                    continue;;

                // QRコードを生成
                byte[] qrCodeImage = qrCodeService.generateQRCodeImage(HOME_URL + inputs[i], QRCodeResolution, QRCodeResolution);
                // Base64エンコード
                String encodedQRCode = Base64.getEncoder().encodeToString(qrCodeImage);
                // モデルにQRコードを渡す
                qrCodes[i] = encodedQRCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("QRCodes", qrCodes);
        model.addAttribute("restaurant", restaurant);

        return "restaurant/confirmQRcode";
    }

    public RestaurantEntity getRestaurant(long restaurant_id){
        Optional<RestaurantEntity> restaurantEntity = restaurantService.getRestaurantById(restaurant_id);
        if(!restaurantEntity.isPresent()){
            return null;
        }

        return restaurantEntity.get();
    }

    public CategoryEntity getCategory(long category_id, List<CategoryEntity> categories){
        for(CategoryEntity category : categories){
            if(category.getId() == category_id)
                return category;
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
