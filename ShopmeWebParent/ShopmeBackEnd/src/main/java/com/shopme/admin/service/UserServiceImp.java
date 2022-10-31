package com.shopme.admin.service;

import com.shopme.admin.config.FileUploadUtil;
import com.shopme.admin.user.RoleRepository;
import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService{

    public static final int USER_PAGE_SIZE = 4;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void saveUser(User user, MultipartFile multipartFile) {
        encodePassword(user);

        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhoto(fileName);

            User savedUser = userRepository.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();

            try {
                FileUploadUtil.cleanDir(uploadDir);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void encodePassword(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public boolean isEmailUnique(String email){
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> getUserById(Integer id) throws UserNotFoundException {
     try{
         return userRepository.findById(id);
     }catch (NoSuchElementException ex){
         throw new UserNotFoundException("user not found"+ id);
     }
    }

    public void deleteUser(Integer id) throws UserNotFoundException {

        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()) {
            throw new UserNotFoundException("user not present" + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void enableUser(Integer id, boolean enabled) {
        /*Optional<User> user = userRepository.findById(id);

        user.ifPresent(value -> value.setEnabled(!value.isEnabled()));

        userRepository.save(user.get());*/

        userRepository.updateEnableStatus(id,enabled);
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir){

        Sort sort = sortDir.equals("asc") ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNum-1,USER_PAGE_SIZE,sort);
        Page<User> userPage = userRepository.findAll(pageable);

        System.out.println("PageNum: "+ pageNum);
        System.out.println("Total Elements: "+ userPage.getTotalElements());
        System.out.println("Total page: "+ userPage.getTotalPages());
        return userPage;
    }

}
