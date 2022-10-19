package com.shopme.admin.service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> listAll();

    List<Role> listRoles();

    void saveUser(User user);

    boolean isEmailUnique(String email);

    Optional<User> getUserById(Integer id) throws UserNotFoundException;
}
