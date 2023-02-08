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

    public static EmailAddress from(String fullAddress) {
        return new EmailAddress(LocalParts.from(fullAddress), Domain.from(fullAddress));
    }

    public void validateSendable() {
        if(!domain.canSend()) {
            throw new BaseException(ExceptionInformation.EMAIL_DOMAIN_INVALID);
        }
    }

    static void validates(String fullAddress) {
        if (!isValidEmailForm(getSeparateIndex(fullAddress))) {
            throw new BaseException(ExceptionInformation.EMAIL_ADDRESS_INVALID);
        }
    }

    static int getSeparateIndex(String fullAddress) {
        return fullAddress.lastIndexOf(domainSeparator);
    }

    private static boolean isValidEmailForm(int domainStartIndex) {
        return domainStartIndex != NOT_FOUND_INDEX_VALUE;
    }
}


