package com.ecommerce.productservice.Utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.NoSuchAlgorithmException;


public class PasswordHash {

    public static String hashPin(String password) throws NoSuchAlgorithmException {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }

    public static Boolean checkPin(String password, String hashedPassword) throws NoSuchAlgorithmException {
        return BCrypt.checkpw(password, hashedPassword);
    }

}
