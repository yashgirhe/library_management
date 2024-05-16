package com.library.management.controller;

import com.library.management.entities.User;
import com.library.management.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ConditionalOnExpression("${user.controller.enabled}")
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
    public User getUserByName(@PathVariable("username") String username){
        return this.userService.getUser(username);
    }

    @PostMapping("/")
    public User addUser(@RequestBody User user){
        return this.userService.adduser(user);
    }
}
