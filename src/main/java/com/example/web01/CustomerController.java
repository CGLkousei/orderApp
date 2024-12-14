package com.example.web01;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerController {

    @GetMapping("/customer/home")
    public String displayCustomerHome(){
        return "customerHome";
    }
}
