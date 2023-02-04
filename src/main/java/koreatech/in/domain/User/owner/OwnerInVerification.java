package koreatech.in.domain.User.owner;

import java.util.Date;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
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

    public void validateFor(OwnerInCertification ownerInCertification) {
        validateDuplication();
        validateExpiration();
        validateCode(ownerInCertification.getCertificationCode());
    }

    private void validateDuplication() {
        if(this.getIs_authed().equals(true)) {
            //TODO DuplicatedCertification
            throw  new BaseException(ExceptionInformation.FORBIDDEN);
        }
    }

    private void validateExpiration() {
        if (DateUtil.isExpired(this.auth_expired_at, new Date())) {
            //TODO ExpiredCertification
            throw new BaseException(ExceptionInformation.FORBIDDEN);
        }
    }

    private void validateCode(String certificationCode) {
        if (!this.getCertificationCode().equals(certificationCode)) {
            //TODO InvalidCertificationCode
            throw new BaseException(ExceptionInformation.FORBIDDEN);
        }
    }
}
