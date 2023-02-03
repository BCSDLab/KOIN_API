package koreatech.in.domain.Owner;

import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;

public class EmailAddress {

    private static final String domainSeparator = "@";
    public static final int NOT_FOUND_INDEX_VALUE = -1;
    public static final int BEGIN_INDEX = 0;

    private final LocalParts localParts;
    private final Domain domain;

    public EmailAddress(LocalParts localParts, Domain domain) {
        this.localParts = localParts;
        this.domain = domain;
    }

    public String getEmailAddress() {
        return localParts.getValue() + domainSeparator + domain.getValue();
    }

    public static EmailAddress from(String fullAddress) {
        validates(fullAddress);

        String localPartsValue = fullAddress.substring(BEGIN_INDEX, getSeparateIndex(fullAddress));
        String domainValue = fullAddress.substring(getSeparateIndex(fullAddress) + domainSeparator.length());

        return new EmailAddress(LocalParts.from(localPartsValue), Domain.from(domainValue));
    }

    private static void validates(String fullAddress) {
        if (!isValidEmailForm(getSeparateIndex(fullAddress))) {
            // 이메일 형식 X
            throw new BaseException(ExceptionInformation.BAD_ACCESS);
        }
    }

    private static int getSeparateIndex(String fullAddress) {
        return fullAddress.lastIndexOf(domainSeparator);
    }

    private static boolean isValidEmailForm(int domainStartIndex) {
        return domainStartIndex != NOT_FOUND_INDEX_VALUE;
    }
}


