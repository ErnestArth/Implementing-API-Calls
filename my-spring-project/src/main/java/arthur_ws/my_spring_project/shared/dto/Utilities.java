package arthur_ws.my_spring_project.shared.dto;

import arthur_ws.my_spring_project.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Component
public class Utilities {

    private final Random rand = new SecureRandom();

    @Value("${tokenSecret}")
    private String tokenSecret;


    public String generateUserId(int length) {
        return generateRandomString(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            randomString.append(alphabet.charAt(rand.nextInt(alphabet.length())));
        }
        return new String(randomString);
    }

    public static boolean hasTokenExpired(String token) {

        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.getTokenSecret().getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

//        JwtParser parser = (JwtParser) Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret());
        JwtParser parser =  Jwts.parser().verifyWith(secretKey).build();

        Claims claims = parser.parseClaimsJws(token).getBody();

        Date tokenExpirationDate = claims.getExpiration();
        Date todayDate =  new Date();

        return tokenExpirationDate.before(todayDate);
    }

    public String generateEmailVerificationToken(String userId) {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(
                        new Date(System.currentTimeMillis() + SecurityConstants.Expiration_Time_In_Seconds))
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
        return token;
    }

    public static String generatePasswordResetToken(String userId) {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(
                        new Date(System.currentTimeMillis() + SecurityConstants.Password_Reset_Expiration_Time_In_Seconds))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }

}
