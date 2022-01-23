package com.matrimony.identity.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;

public class PasswordProcessor {
    private static final String ALGO = "PBKDF2WithHmacSHA1";
    private byte[] hashKey;

    public String hashedPassword(String password){
        if(password == null){
            throw new RuntimeException("password cannot be null");
        }

        /* TODO -> generate seperate salt for each user and store them in db
        so that can use them while login.

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
         */

        try {
        /*
        The third parameter (65536) is effectively the strength parameter. It indicates how many iterations that
        this algorithm run for, increasing the time it takes to produce the hash.
         */
            KeySpec spec = new PBEKeySpec(password.toCharArray(), hashKey, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] encPassword = factory.generateSecret(spec).getEncoded();

            return new String(encPassword, "UTF-8");
        } catch (Exception e){
            throw new RuntimeException("error while hashing the password", e);
        }

    }

    public byte[] getHashKey() {
        return hashKey;
    }

    public void setHashKey(byte[] hashKey) {
        this.hashKey = hashKey;
    }
}
