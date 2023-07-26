package com.green.nowon.util.passwordMatcher;


import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordMatchUtils {    
    public static boolean verifyPassword(PasswordEncoder encoder, String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
