package me.learning.controller;

import me.learning.model.User;
import me.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers(){
        List<User> users = userService.getUsers();
        return users;
    }

    // principal ek aisa object hota hai jo hamare current user ko point krta hai
    @GetMapping("/currentUser")
    public String getLoggedUser(Principal principal){
        return principal.getName();
    }
}
