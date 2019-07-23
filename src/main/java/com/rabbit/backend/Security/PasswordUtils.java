package com.rabbit.backend.Security;

import org.springframework.util.DigestUtils;

import java.util.Random;

public class PasswordUtils {
    public static Boolean checkPassword(String truePassword, String inputPassword, String salt) {
        return DigestUtils.md5DigestAsHex((inputPassword + salt).getBytes()).equals(
                truePassword
        );
    }

    public static String generateSalt() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Random random = new Random();
        StringBuilder saltBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(str.length());
            saltBuilder.append(str.charAt(number));
        }
        return saltBuilder.toString();
    }

    public static String generatePassword(String rawPassword, String salt){
        return DigestUtils.md5DigestAsHex((rawPassword + salt).getBytes());
    }
}
