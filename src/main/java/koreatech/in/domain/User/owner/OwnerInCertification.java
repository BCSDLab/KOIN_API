package koreatech.in.domain.User.owner;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerInCertification extends Owner {

    private final String certificationCode;

    private OwnerInCertification(String emailFullPath, String certificationCode) {
        this.email = emailFullPath;
        this.certificationCode = certificationCode;
    }

    public static OwnerInCertification from(EmailAddress emailAddress, CertificationCode certificationCode) {
        return new OwnerInCertification(emailAddress.getEmailAddress(), certificationCode.getValue());
    }
}
