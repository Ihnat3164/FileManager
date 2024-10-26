package org.mainservice.component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;


@Component
public class JWTUtils {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.lifetime}")
    private Duration lifetime;

    private SecretKey getSigningKey(String secret){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails){
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");
        Date issueDate=new Date();
        Date expiredDate = new Date(issueDate.getTime()+lifetime.toMillis());
        return Jwts.builder()
                .claim("role",role)
                .subject(userDetails.getUsername())
                .issuedAt(issueDate)
                .expiration(expiredDate)
                .signWith(getSigningKey(secret))
                .compact();
    }

    public String getUserEmail(String token){
        return getAllClaimsFromToken(token).getSubject();
    }
    public String getUserRole(String token){
        return getAllClaimsFromToken(token).get("role",String.class);
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
