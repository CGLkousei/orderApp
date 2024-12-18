package com.example.web01.Controller;

import com.example.web01.Class.Customer;
import com.example.web01.Entity.SeatEntity;
import com.example.web01.Service.CategoryService;
import com.example.web01.Service.DishService;
import com.example.web01.Service.RestaurantService;
import com.example.web01.Service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    List<Customer> customers;

    @GetMapping("/{id}/bill")
    public String launchCustomer(@PathVariable Long id, Model model){
        List<SeatEntity> seats = seatService.getSeatsByRestaurantId(id);
        Customer c = new Customer(1, 1);
        c.setTotalMoney(c.getTotalMoney() + 100);
        this.customers.add(c);
        model.addAttribute("customers", customers);

        return "restaurant/finishOrder";
    }
}
