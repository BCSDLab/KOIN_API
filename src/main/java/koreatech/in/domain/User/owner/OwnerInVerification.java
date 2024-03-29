package koreatech.in.domain.User.owner;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.util.DateUtil;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerInVerification extends Owner {

    @JsonProperty("certification_code")
    private final String certificationCode;

    @JsonCreator
    private OwnerInVerification(@JsonProperty("certification_code") String certificationCode,
                                @JsonProperty("is_authed") Boolean isAuthed,
                                @JsonProperty("auth_expired_at") Date authExpiredAt,
                                @JsonProperty("email") String emailAddress) {
        this.certificationCode = certificationCode;
        this.is_authed = isAuthed;
        this.auth_expired_at = authExpiredAt;
        this.email = emailAddress;
    }

    public static OwnerInVerification of(CertificationCode certificationCode, EmailAddress emailAddress) {
        return new OwnerInVerification(certificationCode.getValue(), false, DateUtil.addMinute(new Date(), 5), emailAddress.getEmailAddress());
    }

    public void validateFields() {
        if (this.certificationCode == null) {
            throw new NullPointerException("Redis에 있는 OwnerInVerification 객체의 certificationCode 필드가 비어있습니다.");
        }
        if (this.is_authed == null) {
            throw new NullPointerException("Redis에 있는 OwnerInVerification 객체의 is_authed 필드가 비어있습니다.");
        }
        if (this.auth_expired_at == null) {
            throw new NullPointerException("Redis에 있는 OwnerInVerification 객체의 auth_expired_at 필드가 비어있습니다.");
        }
    }

    public void validateFor(OwnerInCertification ownerInCertification) {
        validateDuplication();
        validateExpiration();
        validateCode(ownerInCertification.getCertificationCode());
    }

    public void validateCertificationComplete() {
        if (this.getIs_authed().equals(false)) {
            throw new BaseException(ExceptionInformation.CERTIFICATION_CODE_NOT_COMPLETED);
        }
    }

    private void validateDuplication() {
        if (this.getIs_authed().equals(true)) {
            throw new BaseException(ExceptionInformation.CERTIFICATION_CODE_ALREADY_COMPLETED);
        }
    }

    private void validateExpiration() {
        if (DateUtil.isExpired(this.auth_expired_at, new Date())) {
            throw new BaseException(ExceptionInformation.CERTIFICATION_CODE_EXPIRED);
        }
    }

    private void validateCode(String certificationCode) {
        if (!this.getCertificationCode().equals(certificationCode)) {
            throw new BaseException(ExceptionInformation.CERTIFICATION_CODE_INVALID);
        }
    }
}
