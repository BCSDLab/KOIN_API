package koreatech.in.domain.User.owner;

import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailAddress {

    static final String domainSeparator = "@";
    public static final int NOT_FOUND_INDEX_VALUE = -1;

    private final LocalParts localParts;
    private final Domain domain;

    public String getEmailAddress() {
        return localParts.getValue() + domainSeparator + domain.getValue();
    }

    static void validates(String fullAddress) {
        if (!isValidEmailForm(getSeparateIndex(fullAddress))) {
            // 이메일 형식 X
            throw new BaseException(ExceptionInformation.BAD_ACCESS);
        }
    }

    static int getSeparateIndex(String fullAddress) {
        return fullAddress.lastIndexOf(domainSeparator);
    }

    private static boolean isValidEmailForm(int domainStartIndex) {
        return domainStartIndex != NOT_FOUND_INDEX_VALUE;
    }
}


