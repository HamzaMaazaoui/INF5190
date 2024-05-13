package com.inf5190.chat.auth.session;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Repository
public class SessionManager {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final int TWO_HOURS_IN_MS = 2 * 60 * 60 * 1000;
    private static final String SECRET_KEY_BASE64 = "1bT8T1c40oApahfJTDGrKQK+bZg3sOsDCFhcCamHEVkA=";
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public SessionManager() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.secretKey).build();
    }

    public String addSession(SessionData authData) {
        final Date now = new Date();
        return Jwts.builder()
                .setAudience("inf5190")
                .setIssuedAt(now)
                .setSubject(authData.username())
                .setExpiration(new Date(now.getTime() + TWO_HOURS_IN_MS))
                .signWith(this.secretKey).compact();
    }

    public void removeSession(String token) {
    }

    public SessionData getSession(String token) {
        try {
            return new SessionData(this.jwtParser.parseClaimsJws(token).getBody().getSubject());
        } catch (JwtException e) {
            this.logger.info("Invalid token", e);
            return null;
        }
    }
}
