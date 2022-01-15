package com.matrimony.common.matrimonytoken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JjwtImpl {
    private static final String SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

    public static String generateJwt(Map<String, String> claims, Long expireDuration){

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(SECRET), SignatureAlgorithm.HS256.getJcaName());


        Instant now = Instant.now();
        String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expireDuration, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .compact();

        return jwtToken;
    }

    public static Jws<Claims> validateJwt(String jwtString) {

        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(SECRET), SignatureAlgorithm.HS256.getJcaName());

        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);

        return jwt;
    }
}
