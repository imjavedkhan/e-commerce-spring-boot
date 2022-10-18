package com.shopme.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderTest {

    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pass = "javed";
        String encodedPass = encoder.encode(pass);

        System.out.println(encodedPass);

        boolean matches= encoder.matches(pass,encodedPass);

        assertTrue(matches);
    }
}
