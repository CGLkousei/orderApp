package com.example.web01.Class;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Token {
    private static final String SECRET_KEY = "SECURE_KEY";
    private static final String AES_KEY = "AES_KEY";

    public static String generateToken(String restaurantId, String seatNo, String timestamp) throws Exception {
        String data = restaurantId + seatNo + timestamp;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyToken(String restaurantId, String seatNo, String timestamp, String token) throws Exception {
        String generatedToken = generateToken(restaurantId, seatNo, timestamp);
        return generatedToken.equals(token);
    }

    // AESで暗号化
    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // AESで復号化
    public static String decrypt(String encryptedText) throws Exception {
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decrypted);
    }
}
