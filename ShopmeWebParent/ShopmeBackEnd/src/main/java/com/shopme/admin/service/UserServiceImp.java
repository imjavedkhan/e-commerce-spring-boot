package com.shopme.admin.service;

import com.shopme.admin.user.RoleRepository;
import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService{

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
    public void saveUser(User user) {
        encodePassword(user);
        userRepository.save(user);
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

}
