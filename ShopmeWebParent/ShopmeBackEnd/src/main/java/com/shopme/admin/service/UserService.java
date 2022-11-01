package com.shopme.admin.service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> listAll();

    List<Role> listRoles();

    void saveUser(User user, MultipartFile multipartFile);

    boolean isEmailUnique(String email);

    Optional<User> getUserById(Integer id) throws UserNotFoundException;

    void deleteUser(Integer id) throws UserNotFoundException;

    void enableUser(Integer id, boolean enabled);

    Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword);
}
