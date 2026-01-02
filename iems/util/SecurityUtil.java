package iems.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class SecurityUtil {
    public static String hashPassword(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));

            // Convert to hexadecimal (matching UserDAO)
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verify(String raw, String hash) {
        if (hash == null)
            return false;
        return hashPassword(raw).equals(hash);
    }
}