package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateUserWithOneRole(){

        User userJak = new User("javed@gmail.com","javed","Javed","Khan");
        Optional<Role> adminRole = roleRepository.findById(2);
        userJak.addRole(adminRole.get());
        User saveUser = userRepository.save(userJak);
        assertEquals(userJak,saveUser);
    }

    @Test
    public void testCreateUserWithTwoRole(){

        User userTom = new User("tom@gmil.com","tom","Tom","Hank");
        Role roleEditor = new Role(6);
        Role roleAssistant = new Role(5);
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
        assertEquals("Javed", userJak.getFirstName());
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
        User userTom = userRepository.findById(4).get();

        Role roleEditor = new Role(6);
        Role roleSalesperson = new Role(3);

        userTom.getRoles().remove(roleEditor);

        userTom.addRole(roleSalesperson);

        User savedUser = userRepository.save(userTom);

        //assertNotEquals(roleEditor,savedUser.getRoles())
    }

    @Test
    public void deleteUserById(){
        Optional<User> userTom = userRepository.findById(4);
        if(userTom.isPresent()){
            userRepository.deleteById(4);
        }
    }
}