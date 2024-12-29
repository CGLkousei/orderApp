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
    public String restaurantDisplay(@PathVariable Long restaurantId, Model model){
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("restaurant", restaurant);

        return "restaurant/restaurantHome";
    }

    @GetMapping("/{restaurantId}/customer")
    public String customerDisplay(@PathVariable Long restaurantId, Model model){
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));

        return "restaurant/viewCustomer";
    }

    @PostMapping("/{restaurantId}/handleBtn")
    public String handleBtnFunc(@PathVariable Long restaurantId, @RequestParam("action") String action, Model model){
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        model.addAttribute("restaurant", restaurant);
        if("viewCustomer".equals(action)){
            model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));
            return "restaurant/viewCustomer";
        }
        else if("makeQRcode".equals(action)) {
            String[] qrCodes = new String[restaurant.getSeatNum().intValue()];
            String[] inputs = new String[restaurant.getSeatNum().intValue()];
            List<SeatEntity> seats = restaurant.getSeats();

            for(SeatEntity seat : seats){
                String query = seat.getToken();
                int seat_id = getSeatID(query, "seatId");
                if(seat_id < 0)
                    continue;;
                inputs[seat_id - 1] = query;
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

    @PostMapping("/{restaurantId}/Customer/Control")
    public String controlCustomer(@PathVariable Long restaurantId, @RequestParam("action") String action, Model model) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "restaurant/errorPage";
        }

        String[] parts = action.split("-");
        int seatId = Integer.parseInt(parts[1]);
        if(parts[0].equals("add")){
            Customer new_customer = new Customer(seatId, restaurantId.intValue());
            customerService.addCustomer(restaurantId, new_customer);
        }
        else if(parts[0].equals("remove")){
            customerService.initializeCustomer(restaurantId, seatId);
        }
        else if(parts[0].equals("cancel")){
            int order_index = Integer.parseInt(parts[2]);
            Customer customer = customerService.getCustomer(restaurantId, seatId);
            customer.getOrder().remove(order_index);
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customers", customerService.getCustomersByRestaurant(restaurantId));

        return "restaurant/viewCustomer";
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

        List<Integer> IDs = new ArrayList<>();
        for(String id : form.getSelectedOptions()) {
            IDs.add(Integer.parseInt(id) - 1);
        }

        String[] qrCodes = new String[restaurant.getSeatNum().intValue()];
        String[] inputs = new String[restaurant.getSeatNum().intValue()];
        List<SeatEntity> seats = restaurant.getSeats();
        List<Integer> yetList = new ArrayList<>();

        for(SeatEntity seat : seats){
            String query = seat.getToken();
            int seat_id = getSeatID(query, "seatId");
            if(seat_id < 0)
                continue;;

            int index = seat_id - 1;
            boolean found = IDs.contains(index);
            if(found) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                String formattedDate = LocalDateTime.now().format(formatter);
                inputs[index] = "?restaurantId=" + restaurant.getId() + "&seatId=" + seat_id + "&token=" + formattedDate;

                SeatEntity tmp_seatEntity = seatService.getSeatById(seat.getId()).get();
                tmp_seatEntity.setToken(inputs[index]);
                tmp_seatEntity.setRestaurant(restaurant);
                seatService.saveSeat(tmp_seatEntity);

                yetList.add(index);
            }
            else
                inputs[index] = seat.getToken();

        }

        Set<Integer> difference = new HashSet<>(IDs);
        difference.remove(new HashSet<>(yetList));

        for(int index : difference){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String formattedDate = LocalDateTime.now().format(formatter);
            int seatIndex = index + 1;
            inputs[index] = "?restaurantId=" + restaurant.getId() + "&seatId=" + seatIndex + "&token=" + formattedDate;

            SeatEntity tmp_seatEntity = new SeatEntity();
            tmp_seatEntity.setToken(inputs[index]);
            tmp_seatEntity.setRestaurant(restaurant);
            seatService.saveSeat(tmp_seatEntity);
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

    public int getSeatID(String query, String seatStr){
        String[] params = query.split("&"); // "&"で分割
        for (String param : params) {
            if (param.startsWith(seatStr + "=")) {
                String seatId = param.split("=")[1]; // "="で分割して値を取得
                return Integer.parseInt(seatId);
            }
        }

        return -1;
    }
}
