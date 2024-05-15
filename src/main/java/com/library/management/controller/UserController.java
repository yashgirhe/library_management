package com.library.management.controller;

import com.library.management.models.User;
import com.library.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public List<User> getAllUsers(){
        return this.userService.getAllUser();
    }
    @GetMapping("/{username}")
    public User getUser(@PathVariable("username") String username){
        return this.userService.getUser(username);
    }
    @PostMapping("/")
    public User addUser(@RequestBody User user){
        return this.userService.adduser(user);
    }
}
