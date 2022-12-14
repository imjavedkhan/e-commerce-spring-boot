package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateUserWithOneRole(){

        User userJak = new User("javed@gmail.com","$2a$12$7ETRP9StrSMu3/ZiErMWAOUy1jq7VEhuEtoEdlAIBehbtUZtKoRNu","Javed","Khan");
        Optional<Role> adminRole = roleRepository.findById(1);
        userJak.addRole(adminRole.get());
        User saveUser = userRepository.save(userJak);
        assertEquals(userJak,saveUser);
    }

    @Test
    public void testCreateUserWithTwoRole(){

        User userTom = new User("tom@gmil.com","tom","Tom","Hank");
        Role roleEditor = new Role(5);
        Role roleAssistant = new Role(4);
        userTom.addRole(roleEditor);
        userTom.addRole(roleAssistant);
        User savedUser  = userRepository.save(userTom);
        assertEquals(userTom,savedUser);
    }

    @Test
    public void testGetAllUser(){
        Iterable<User> userList = userRepository.findAll();
        userList.forEach(System.out::println);
    }

    @Test
    public void testGetUserById(){
        User userJak = userRepository.findById(1).get();
        assertEquals("Nam", userJak.getFirstName());
    }

    @Test
    public void testUpdateUserDetails(){
        User userJak = userRepository.findById(1).get();
        userJak.setEnabled(true);
        userJak.setEmail("jak@gmail.com");
        User savedUser = userRepository.save(userJak);
        assertTrue(savedUser.isEnabled());
    }

    @Test
    public void testUpdateUserRole(){
        Optional<User> userTom = userRepository.findById(4);

        Role roleEditor = new Role(5);
        Role roleSalesperson = new Role(3);
        if(userTom.isPresent()) {
            userTom.get().getRoles().remove(roleSalesperson);
            userTom.get().addRole(roleEditor);
            User savedUser = userRepository.save(userTom.get());
        }
        //assertNotEquals(roleEditor,savedUser.getRoles())
    }

    @Test
    public void deleteUserById(){
        Optional<User> userTom = userRepository.findById(4);
        if(userTom.isPresent()){
            userRepository.deleteById(4);
        }
    }

    @Test
    public void testUserEmail(){
        String email = "nam@codejava.net";
        boolean exist = userRepository.existsByEmail(email);
        assertTrue(exist);
    }

    @Test
    public void testListFirstPage(){

        Pageable pageable = PageRequest.of(1,4);

        Page<User>  page = userRepository.findAll(pageable);

        List<User> userList = page.getContent();

        userList.forEach(System.out::println);

        assertEquals(userList.size(),4);

    }


}
