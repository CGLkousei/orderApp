package com.example.web01;

import com.example.web01.Class.Customer;
import com.example.web01.Class.Token;
import com.example.web01.Entity.CategoryEntity;
import com.example.web01.Entity.DishEntity;
import com.example.web01.Entity.RestaurantEntity;
import com.example.web01.Service.CategoryService;
import com.example.web01.Service.DishService;
import com.example.web01.Service.RestaurantService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    private static final int effectiveTime = 300;
    private static final String cookieKey = "timeStamp";

    @GetMapping("/customer/launch")
    public String launchCustomer(){
        return "customer/sample";
    }
    @PostMapping("/customer/home")
    public String displayCustomerHome(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cookie : cookies){
                System.out.println("Cookie Name: " + cookie.getName());
                if(cookie.getName().equals(cookieKey + "_1_1")){
                    long timeStamp = Long.parseLong(cookie.getValue());
                    Instant instant = Instant.ofEpochMilli(timeStamp);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Tokyo"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = dateTime.format(formatter);
                    System.out.println("Cookie Value: " + formattedDate);
                }
                else{
                    System.out.println("Cookie Value: " + cookie.getValue());
                }
            }
        }



        return "order/orderHome";
    }

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
    @GetMapping("/order/launch")
    public String displayOrderPage(@RequestParam String restaurantId, @RequestParam String seatId,
                                   HttpServletRequest request, HttpServletResponse response,
                                   Model model) {

        Cookie[] cookies = request.getCookies();
        boolean isFirstVisit = true;
        long firstVisitTime = 0;

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(cookieKey + "_" + restaurantId + "_" + seatId)){
                    isFirstVisit = false;
                    firstVisitTime = Long.parseLong(cookie.getValue());
                    break;
                }
            }
        }

        // 初回アクセスの場合、"firstVisitTime"クッキーを設定
        if (isFirstVisit) {
            long currentTime = Instant.now().toEpochMilli(); // 現在の時間をタイムスタンプとして取得

            ResponseCookie responseCookie = ResponseCookie.from(cookieKey + "_" + restaurantId + "_" + seatId, String.valueOf(currentTime))
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(60 * 60 * 12)
                    .path("/")
                    .sameSite("Strict")
                    .build();

            response.addHeader("Set-Cookie", responseCookie.toString());
        }

        List<RestaurantEntity> restaurants = restaurantService.getAllRestaurants();
        List<CategoryEntity> categories = categoryService.getAllCategories();
        List<DishEntity> dishes = dishService.getAllDishes();

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("categories", categories);
        model.addAttribute("dishes", dishes);

        return "order/orderHome";
    }
}
