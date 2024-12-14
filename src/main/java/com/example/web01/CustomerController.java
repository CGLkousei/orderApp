package com.example.web01;

import com.example.web01.Class.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {

    @GetMapping("/customer/launch")
    public String launchCustomer(){
        return "customer/sample";
    }
    @PostMapping("/customer/home")
    public String displayCustomerHome(Model model){
        Customer c = new Customer();
        model.addAttribute("customer", c);

        return "customer/customerHome";
    }

}
