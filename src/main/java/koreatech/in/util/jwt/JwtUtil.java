package koreatech.in.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtUtil {

    public String generateToken(Date expiration, SecretKey key) {
        return Jwts.builder()
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateToken(String subject, Date expiration, SecretKey key) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public boolean isExpiredToken(String token, SecretKey key)
            throws UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String getSubject(String token, SecretKey key) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
