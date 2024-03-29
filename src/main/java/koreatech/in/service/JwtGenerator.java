package koreatech.in.service;

import java.util.Date;
import javax.crypto.SecretKey;
import koreatech.in.util.DateUtil;
import koreatech.in.util.jwt.JwtKeyManager;
import koreatech.in.util.jwt.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class JwtGenerator<T> {
    @Autowired
    protected JwtKeyManager jwtKeyManager;

    private final JwtUtil jwtUtil = new JwtUtil();

    public String generate(T data) {
        String subject = castToSubject(data);
        if(StringUtils.isEmpty(subject)) {
            return jwtUtil.generateToken(makeExpiration(), getKey());
        }

        return jwtUtil.generateToken(subject, makeExpiration(), getKey());
    }

    protected abstract String castToSubject(T data);

    protected Date makeExpiration() {
        return DateUtil.addHoursToJavaUtilDate(new Date(), (int) getExpirationHour());
    }

    protected abstract long getExpirationHour();

    protected abstract SecretKey getKey();

}
