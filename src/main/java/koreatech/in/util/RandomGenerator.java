package koreatech.in.util;

import java.util.concurrent.ThreadLocalRandom;
import koreatech.in.domain.Owner.CertificationCode;

public class RandomGenerator {
    public static final int CERTIFICATION_NUMBER_ORIGIN = 0;
    private static final int CERTIFICATION_NUMBER_BOUND = 1_000_000;

    private static int getCertificationCodeNumber() {
        return ThreadLocalRandom.current().nextInt(CERTIFICATION_NUMBER_ORIGIN, CERTIFICATION_NUMBER_BOUND);
    }

    public static CertificationCode getCertificationCode() {
        return CertificationCode.from(String.format("%03d", getCertificationCodeNumber()));
    }
}
