package com.example.web01.Class;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Token {
    private static final String SECRET_KEY = "secure";

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
}
