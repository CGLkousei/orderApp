package com.example.web01.Controller;

import com.example.web01.Class.Customer;
import com.example.web01.Class.Dish;
import com.example.web01.Data.ParamOrders;
import com.example.web01.Entity.CategoryEntity;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

//    @GetMapping("/customer/launch")
//    public String launchCustomer(){
//        return "customer/sample";
//    }
//    @PostMapping("/customer/home")
//    public String displayCustomerHome(HttpServletRequest request){
//        Cookie[] cookies = request.getCookies();
//
//        if(cookies != null){
//            for(Cookie cookie : cookies){
//                System.out.println("Cookie Name: " + cookie.getName());
//                if(cookie.getName().equals(cookieKey + "_1_1")){
//                    long timeStamp = Long.parseLong(cookie.getValue());
//                    Instant instant = Instant.ofEpochMilli(timeStamp);
//                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Tokyo"));
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    String formattedDate = dateTime.format(formatter);
//                    System.out.println("Cookie Value: " + formattedDate);
//                }
//                else{
//                    System.out.println("Cookie Value: " + cookie.getValue());
//                }
//            }
//        }
//
//        return "order/orderHome";
//    }

//    @GetMapping("/order/home")
//    public String displayOrderPage(@RequestParam String restaurantId, @RequestParam String seatNo,
//                                   @RequestParam(required = false) String token, @RequestParam(required = false) Long timeStamp,
//                                   Model model){
//
//        //現在時刻を取得
//        long now = Instant.now().toEpochMilli();
//        //現在時刻とtimeStampの差が有効時間(s)以上または、timeStampがないなら不正アクセスと判断
//        if(timeStamp == null || (now - timeStamp > effectiveTime * 1000)){
//            model.addAttribute("message", "tokenが無効または期限切れです。再度QRコードをスキャンしてください。");
//            return "order/error";
//        }
//
//        try{
//            //初回アクセスの場合tokenを作成する
//            if(token == null){
//                String newToken = Token.generateToken(restaurantId, seatNo, timeStamp == null ? now : timeStamp);
//                String redirectUrl = "/order/home?restaurantId=" + restaurantId + "&seatNo=" + seatNo + "&token=" + newToken + "&timeStamp=" + now;
//                return "order/home";
//            }
//
//            //tokenを検証する
//            if(!Token.verifyToken(restaurantId, seatNo, timeStamp, token)){
//                return ResponseEntity.status(401).body("Invalid token.");
//            }
//
//            String redirectUrl = "restaurantId=" + restaurantId + "&seatNo=" + seatNo;
//            return ResponseEntity.ok("Welcome to the order page for " + redirectUrl);
//        } catch (Exception e) {
//            e.printStackTrace();;
//            return ResponseEntity.status(500).body("Internal server error.");
//        }
//    }

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

        Customer new_customer = new Customer(Integer.parseInt(seatId), Integer.parseInt(restaurantId));
        customerService.addCustomer(Long.parseLong(restaurantId), new_customer);

        Customer customer = customerService.getCustomer(Long.parseLong(restaurantId), Long.parseLong(seatId));
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

        Customer customer = customerService.getCustomer(restaurantID, seatID);
        int total = 0, iter = 0;
        for(Long key: po.getOrders().keySet()){
            total += order_dishes[iter].getPrice() * po.getOrders().get(key);
            iter++;
        }

        customer.setTotalMoney(customer.getTotalMoney() + total);
        customer.addOrder(po.getOrders());

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
        List<Long> id_list = new ArrayList<>(customer.getOrder().keySet());
//        DishEntity[] order_dishes = getDish(new ArrayList<>(customer.getOrder().keySet()), dishes);
        DishEntity[] order_dishes = new DishEntity[id_list.size()];
        int[] dish_numbers = new int[order_dishes.length];
        int[] prices = new int[order_dishes.length];

        for(DishEntity dish : dishes){
            int index = id_list.indexOf(dish.getId());
            if(index >= 0){
                order_dishes[index] = dish;
                dish_numbers[index] = customer.getOrder().get(dish.getId());
                prices[index] = dish.getPrice() * dish_numbers[index];
            }
        }

        for(int i = 0; i < order_dishes.length; i++){
            System.out.println("Dish: " + order_dishes[i].getName() + ", Num: " + dish_numbers[i] + ", Price: " + prices[i]);
        }

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("customer", customer);
        model.addAttribute("dishes", order_dishes);
        model.addAttribute("numbers", dish_numbers);
        model.addAttribute("prices", prices);

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
