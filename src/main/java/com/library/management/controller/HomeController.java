package com.library.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String afterLogout() {
        return "login";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "adminhome";
    }

    @GetMapping("/user/home")
    public String userHome() {
        return "userhome";
    }

    @GetMapping("/public/home")
    public String home() {
        return "publichome";
    }
}
