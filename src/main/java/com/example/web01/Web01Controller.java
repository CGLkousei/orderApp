package com.example.web01;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class Web01Controller {

    @GetMapping("/restaurant/home")
    public String displayHome(Model model){
        return "restaurant/home";
    }
}
