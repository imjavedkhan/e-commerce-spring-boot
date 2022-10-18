package com.shopme.admin.controller;

import com.shopme.admin.service.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getAllUsers(Model model){

        List<User> userList = userService.listAll();
        model.addAttribute("listUsers",userList);
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model){
        User user =new User();
        user.setEnabled(true);
        List<Role> roleList = userService.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("listRole",roleList);
        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes){
        System.out.println(user);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message","The user has been saved successfully");
        return "redirect:/users";
    }
}
