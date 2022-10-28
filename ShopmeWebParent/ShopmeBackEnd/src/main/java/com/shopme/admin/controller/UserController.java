package com.shopme.admin.controller;

import com.shopme.admin.service.UserNotFoundException;
import com.shopme.admin.service.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

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
        model.addAttribute("pageTitle","Create New User");
        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid User user,
                           RedirectAttributes redirectAttributes,
                           BindingResult result,
                           Model model,
                           @RequestParam("image") MultipartFile multipartFile){
        System.out.println(user);
//        if(userService.isEmailUnique(user.getEmail()))
//        {
//           result.rejectValue("email", null,"duplicate email");
//        }

        if(result.hasErrors()) {
            //redirectAttributes.addFlashAttribute("message","Duplicate email");
            List<Role> roleList = userService.listRoles();
            model.addAttribute("user",user);
            model.addAttribute("listRole",roleList);
            model.addAttribute("pageTitle","Edit user (ID: "+ user.getId() +")");
            return "user_form";

        }
        //TODO: handle duplicate user in database.

        userService.saveUser(user,multipartFile);
        redirectAttributes.addFlashAttribute("message","The user has been saved successfully");
        return "redirect:/users";
    }

    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model){
        try {
            User userdetails = userService.getUserById(id).get();
            List<Role> roleList = userService.listRoles();
            model.addAttribute("user",userdetails);
            model.addAttribute("listRole",roleList);
            model.addAttribute("pageTitle","Edit user (ID: "+ id +")");
            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message",e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id,
                             Model model, RedirectAttributes redirectAttributes){
        try{
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message",
                    "The user " + id + " have been deleted");
        }catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message",e.getMessage());
        }
        return "redirect:/users";

        //TODO: handle delete pop up window
    }

    @GetMapping("/user/enable/{id}/enabled/{status}")
    public String enableUser(@PathVariable("id") Integer id,
                             @PathVariable("status") boolean enabled,
                             RedirectAttributes redirectAttributes){
        userService.enableUser(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/users";

    }

}
