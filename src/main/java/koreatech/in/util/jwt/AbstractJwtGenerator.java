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
import javax.crypto.SecretKey;
import koreatech.in.exception.BaseException;
import koreatech.in.repository.AuthenticationMapper;
import koreatech.in.util.DateUtil;

public abstract class AbstractJwtGenerator implements JwtGenerator<Integer> {

    private AuthenticationMapper authenticationMapper;

    private JwtKeyManager jwtKeyManager;

    protected abstract long getTokenValidHour();

    public String generateToken(Integer userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(makeExpiration())
                .signWith(getKey())
                .compact();
    }

    private Date makeExpiration() {
        return DateUtil.addHoursToJavaUtilDate(new Date(), (int) getTokenValidHour());
    }

    protected abstract SecretKey getKey();

    public Integer getFromToken(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(getKey())
                    .parseClaimsJws(token)
                    .getBody();

            int userId = Integer.parseInt(body.getSubject());

            String redisToken = authenticationMapper.getRefreshToken(userId);
            if (token.equals(redisToken)) {
                return userId;
            }
        } catch (JwtException | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }

        throw new BaseException(BAD_ACCESS);
    }

    public Boolean isExpired(String token) {
        try {
            Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token);
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BAD_ACCESS);
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
