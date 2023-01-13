package koreatech.in.domain.Upload;

import static koreatech.in.exception.ExceptionInformation.PAGE_NOT_FOUND;

import java.util.Arrays;
import koreatech.in.exception.BaseException;

public enum DomainEnum {
    ITEMS, LANDS, CIRCLES, MARKET, SHOPS, MEMBERS;

    public static void validate(String pathDomain) {
        //TODO 23.01.13. 박한수. 상황에 맞는 예외 추가하기.
        Arrays.stream(DomainEnum.values()).filter(domain -> domain.name().equalsIgnoreCase(pathDomain))
                .findAny()
                .orElseThrow(() -> new BaseException(PAGE_NOT_FOUND));
    }
}
