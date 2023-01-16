package koreatech.in.domain.Upload;

import static koreatech.in.exception.ExceptionInformation.DOMAIN_NOT_FOUND;

import java.util.Arrays;
import koreatech.in.exception.BaseException;

public enum DomainEnum {
    ITEMS, LANDS, CIRCLES, MARKET, SHOPS, MEMBERS;

    public static void validate(String pathDomain) {
        Arrays.stream(DomainEnum.values()).filter(domain -> domain.name().equalsIgnoreCase(pathDomain))
                .findAny()
                .orElseThrow(() -> new BaseException(DOMAIN_NOT_FOUND));
    }
}
