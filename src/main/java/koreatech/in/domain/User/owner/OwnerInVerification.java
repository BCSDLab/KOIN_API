package koreatech.in.domain.User.owner;

import java.util.Date;
import koreatech.in.util.DateUtil;
import lombok.Getter;

@Getter
public class OwnerInVerification extends Owner {

    private final String certificationCode;

    private OwnerInVerification(String certificationCode, Boolean isAuthed, Date authExpiredAt) {
        this.certificationCode = certificationCode;
        this.is_authed = isAuthed;
        this.auth_expired_at = authExpiredAt;
    }

    public static OwnerInVerification from(CertificationCode certificationCode) {
        return new OwnerInVerification(certificationCode.getValue(), false, DateUtil.addMinute(new Date(), 5));
    }
}
