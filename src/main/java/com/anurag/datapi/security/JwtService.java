package com.anurag.datapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret.string}")
    private String JWT_SECRET;

    @Value("${jwt.expiration.time}")
    private long JWT_EXPIRY_TIME;

    private SecretKey key;

    @PostConstruct
    private void init() {
//        byte[] keyByte = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
//        this.key = new SecretKeySpec(keyByte,  "HmacSHA256");

        byte[] keyByte = Decoders.BASE64.decode(JWT_SECRET);

        // Size validate
        if (keyByte.length < 32) {
            throw new IllegalStateException(
                    "JWT Size check 8*32 should be atleast "
            );
        }

        // Proper key object banao
        this.key = Keys.hmacShaKeyFor(keyByte);

    }


    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRY_TIME))
                .signWith(key)
                .compact();
    }

    public String getUserNameFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload());

    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUserNameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }


}
