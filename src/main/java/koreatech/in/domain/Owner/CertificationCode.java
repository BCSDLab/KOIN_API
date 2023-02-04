package koreatech.in.domain.Owner;

import lombok.Getter;

@Getter
public class CertificationCode {

    private final String value;

    private CertificationCode(String value) {
        this.value = value;
    }

    public String getCode() {
        return value;
    }

    public static CertificationCode from(String value) {
        return new CertificationCode(value);
    }
}
