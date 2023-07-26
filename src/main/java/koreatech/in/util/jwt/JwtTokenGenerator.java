package koreatech.in.util.jwt;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import koreatech.in.exception.BaseException;
import koreatech.in.repository.AuthenticationMapper;
import koreatech.in.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator {
    //TODO Key Generate, Key Validate 분리하기.

    private static final long REFRESH_TOKEN_VALID_DAYS = 14;
    private static final int PREV_ACCESS_TOKEN_VALID_HOUR = 72;
    private static final int ACCESS_TOKEN_VALID_HOUR = 2;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    @Autowired
    private JwtKeyManager jwtKeyManager;

    public String generateAccessToken(int userId) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), PREV_ACCESS_TOKEN_VALID_HOUR);
        return Jwts.builder().setSubject(String.valueOf(userId)).setExpiration(exp).signWith(jwtKeyManager.getAccessKey()).compact();
    }

    public String generateRefreshToken(Integer userId) {
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), (int) TimeUnit.DAYS.toHours(REFRESH_TOKEN_VALID_DAYS));
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(exp)
                .signWith(jwtKeyManager.getRefreshKey())
                .compact();
    }

    public int me(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(jwtKeyManager.getAccessKey())
                    .parseClaimsJws(token)
                    .getBody();

            return Integer.parseInt(body.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        throw new BaseException(BAD_ACCESS);
    }

    public int getIdFromRefreshToken(String refreshToken) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(jwtKeyManager.getRefreshKey())
                    .parseClaimsJws(refreshToken)
                    .getBody();

            int userId = Integer.parseInt(body.getSubject());

            String redisToken = authenticationMapper.getRefreshToken(userId);
            if (refreshToken.equals(redisToken)) {
                return userId;
            }
        } catch (JwtException | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        throw new BaseException(BAD_ACCESS);
    }

    public Boolean isExpired(String token) {
        try {
            Jwts.parser().setSigningKey(jwtKeyManager.getAccessKey()).parseClaimsJws(token);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public Boolean isExpiredRefreshToken(String refreshToken) {
        try {
            Jwts.parser().setSigningKey(jwtKeyManager.getRefreshKey()).parseClaimsJws(refreshToken);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

}
