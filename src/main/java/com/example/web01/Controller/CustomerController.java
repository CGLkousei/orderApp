package com.example.web01.Controller;

import com.example.web01.Class.Customer;
import com.example.web01.Data.Order;
import com.example.web01.Data.ParamOrders;
import com.example.web01.Entity.DishEntity;
import com.example.web01.Entity.RestaurantEntity;
import com.example.web01.Service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class CustomerController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CustomerService customerService;

    private static final int effectiveDays = 3;
    private static final String cookieKey = "Token";

    /*
        今後やること
        Tokenを暗号化
        QRコードにセッションIDを持たせる
     */
    @GetMapping("/home")
    public String displayOrderPage(@RequestParam String restaurantId, @RequestParam String seatId,
                                   HttpServletRequest request, HttpServletResponse response,
                                   Model model) {

        Cookie[] cookies = request.getCookies();
        boolean isFirstVisit = true;
        String cookieToken = null;

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(cookieKey + "_" + restaurantId + "_" + seatId)){
                    isFirstVisit = false;
                    cookieToken = cookie.getValue();
                    break;
                }
            }
        }

//        Customer new_customer = new Customer(Integer.parseInt(seatId), Integer.parseInt(restaurantId));
//        customerService.addCustomer(Long.parseLong(restaurantId), new_customer);

        Customer customer = customerService.getCustomer(Long.parseLong(restaurantId), Long.parseLong(seatId));
        if(customer == null){
            model.addAttribute("message", "The link may be incorrect. Please reread the QR code.");
            return "order/errorPage";
        }
        String seatToken = customer.getToken();

        // 初回アクセスの場合、"firstVisitTime"クッキーを設定
//        if (isFirstVisit) {
        if (true) {
            ResponseCookie responseCookie = ResponseCookie.from(cookieKey + "_" + restaurantId + "_" + seatId, seatToken)
                    .httpOnly(true)
                    .secure(true)
//                    .maxAge(60 * 60 * 12 * effectiveDays)
                    .maxAge(0)
                    .path("/")
                    .sameSite("Strict")
                    .build();

            response.addHeader("Set-Cookie", responseCookie.toString());
        }
        else{
            if(!cookieToken.equals(seatToken)){
                model.addAttribute("message", "Your expiration date has expired. Thank you for visiting.");
                return "order/errorPage";
            }
        }

        Optional<RestaurantEntity> restaurantEntity = restaurantService.getRestaurantById(Long.parseLong(restaurantId));
        if(!restaurantEntity.isPresent()){
            model.addAttribute("message", "The link may be incorrect. Please reread the QR code.");
            return "order/errorPage";
        }
        RestaurantEntity restaurant = restaurantEntity.get();

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customer", customer);

        return "order/orderHome";
    }

    @PostMapping("/home")
    public String displayOrderPage(@RequestParam String restaurantId, @RequestParam String seatId, Model model) {
        int restaurantID = Integer.parseInt(restaurantId);
        int seatID = Integer.parseInt(seatId);

        RestaurantEntity restaurant = getRestaurant(restaurantID);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "order/errorPage";
        }

        Customer customer = customerService.getCustomer(restaurantID, seatID);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customer", customer);

        return "order/orderHome";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam String restaurantId, @RequestParam String seatId, @ModelAttribute("paramOrders") ParamOrders po, Model model){
        int restaurantID = Integer.parseInt(restaurantId);
        int seatID = Integer.parseInt(seatId);

        RestaurantEntity restaurant = getRestaurant(restaurantID);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "order/errorPage";
        }

        List<DishEntity> dishes = restaurant.getDishes();
        DishEntity[] order_dishes = getDish(new ArrayList<>(po.getOrders().keySet()), dishes);
        List<Order> new_orders = new ArrayList<>();

        Customer customer = customerService.getCustomer(restaurantID, seatID);
        int total = 0, iter = 0;
        for(Long key: po.getOrders().keySet()){
            Order order = new Order();
            order.setDish(order_dishes[iter]);
            order.setNumber(po.getOrders().get(key));
            order.setTotal(order.getDish().getPrice() * order.getNumber());
            total += order.getTotal();
            new_orders.add(order);
            iter++;
        }



        customer.setTotalMoney(customer.getTotalMoney() + total);
        customer.addOrder(new_orders);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customer", customer);

        return "order/orderHome";
    }

    @PostMapping("/status")
    public String displayOrderStatus(@RequestParam String restaurantId, @RequestParam String seatId,
                                   HttpServletRequest request, HttpServletResponse response,
                                   Model model) {
        Cookie[] cookies = request.getCookies();
        String cookieToken = null;

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(cookieKey + "_" + restaurantId + "_" + seatId)){
                    cookieToken = cookie.getValue();
                    break;
                }
            }
        }

        int restaurantID = Integer.parseInt(restaurantId);
        int seatID = Integer.parseInt(seatId);

        RestaurantEntity restaurant = getRestaurant(restaurantID);
        if(restaurant == null){
            model.addAttribute("message", "An unexpected error has occurred. Please contact the administrator.");
            return "order/errorPage";
        }

        Customer customer = customerService.getCustomer(restaurantID, seatID);
        String seatToken = customer.getToken();

//        if(!seatToken.equals(cookieToken)){
//            model.addAttribute("message", "An unexpected error has occurred. Please read the QR code again");
//            return "order/errorPage";
//        }

        List<DishEntity> dishes = restaurant.getDishes();
        List<Order> orders = customer.getOrder();
        String[] dish_names = new String[orders.size()];
        int[] dish_prices = new int[orders.size()];
        int[] dish_numbers = new int[orders.size()];
        int[] total_prices = new int[orders.size()];

        int index = 0;
        for(Order order : orders){
            dish_names[index] = order.getDish().getName();
            dish_prices[index] = order.getDish().getPrice();
            dish_numbers[index] = order.getNumber();
            total_prices[index] = order.getTotal();
            index++;
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customer", customer);
        model.addAttribute("names", dish_names);
        model.addAttribute("prices", dish_prices);
        model.addAttribute("numbers", dish_numbers);
        model.addAttribute("totals", total_prices);

        return "order/orderStatus";
    }

    public DishEntity[] getDish(List<Long> dish_ids, List<DishEntity> dishes){
        DishEntity[] returnDishes = new DishEntity[dish_ids.size()];
        for(DishEntity dish : dishes){
            int index = dish_ids.indexOf(dish.getId());
            if(index >= 0)
                returnDishes[index] = dish;
        }

        return returnDishes;
    }

    public RestaurantEntity getRestaurant(long restaurant_id){
        Optional<RestaurantEntity> restaurantEntity = restaurantService.getRestaurantById(restaurant_id);
        if(!restaurantEntity.isPresent()){
            return null;
        }

        return restaurantEntity.get();
    }

}
