package com.github.supercodingteam1.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final String secretKey= Base64.getEncoder()
            .encodeToString("5f8d5e3c7f1d292fb94d7dbccf9e8c8a97e5e4f902c3836724e97a163e77cdbc".getBytes());
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

    private long tokenValidMillisecond=1000L*60*60;

    private final UserDetailsService userDetailsService;

    public String createToken(String email, List<String> roles) {
        Claims claims = Jwts.claims()
                .setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH_TOKEN");
    }

    public boolean validateToken(String jwtToken) {


        try{
            Claims claims= Jwts.parserBuilder()
                            .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            Date now = new Date();

            return claims.getExpiration().after(now);
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserEmail(jwtToken));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    private String getUserEmail(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }
}
