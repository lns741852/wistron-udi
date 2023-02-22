package com.surgical.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider{

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /** Key */
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /** 時效(ms) */
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    /**產生JWT
     * @param authentication authentication
     * @return String JWT
     */
    public String generateToken(String accountId, String account){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        StringBuilder source = new StringBuilder();
        source.append(accountId).append(";").append(account);
        return Jwts.builder().setSubject(source.toString())
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    /**由Token中取出使用者accountId, account
     * @param token JWT Token
     */
    public String getUserAccountInfoFromJWT(String token){
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /** 驗證Token合法性
     * @param token JWT Token
     * @return boolean 是否為合法Token
     */
    public boolean validateToken(String token){
        try{
            // logger.info("validateToken authToken : {}", token);
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch(SignatureException ex){
            logger.error("Invalid JWT signature");
        }catch(MalformedJwtException ex){
            logger.error("Invalid JWT token");
        }catch(ExpiredJwtException ex){
            logger.error("Expired JWT token");
        }catch(UnsupportedJwtException ex){
            logger.error("Unsupported JWT token");
        }catch(IllegalArgumentException ex){
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}
