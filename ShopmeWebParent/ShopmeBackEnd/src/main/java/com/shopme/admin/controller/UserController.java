package com.shopme.admin.controller;

import com.shopme.admin.service.UserCsvExporter;
import com.shopme.admin.service.UserNotFoundException;
import com.shopme.admin.service.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.shopme.admin.service.UserServiceImp.USER_PAGE_SIZE;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getAllUsers(Model model){

//        List<User> userList = userService.listAll();
//        model.addAttribute("listUsers",userList);
        return listUserByPage(1,model,"firstName","asc",null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listUserByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                                 @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                                 @Param("keyword") String keyword){
        Page<User> userList = userService.listByPage(pageNum,sortField,sortDir,keyword);

        List<User> listUser = userList.getContent();

        int startCount = (pageNum-1) * USER_PAGE_SIZE + 1;
        long endCount = (long) startCount + USER_PAGE_SIZE - 1;
        if(endCount > userList.getTotalElements()){
            endCount = userList.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage",pageNum);
        model.addAttribute("totalPages",userList.getTotalPages());
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalItems",userList.getTotalElements());
        model.addAttribute("listUsers",listUser);
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",reverseSortDir);
        model.addAttribute("keyword",keyword);
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
        String message = "The User ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/users";

    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(userList,response);
    }

}
