package koreatech.in.domain.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailAddress {

    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();
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
        if (!domain.canSend()) {
            throw new BaseException(ExceptionInformation.EMAIL_DOMAIN_INVALID);
        }
    }

    static void validates(String fullAddress) {
        validateEmailForm(fullAddress);
    }

    private static void validateEmailForm(String fullAddress) {
        EMAIL_VALIDATOR.validate(fullAddress);

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

    static class EmailValidator {
        public static final String LOCAL_PARTS_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@";
        public static final String DOMAIN_PATTERN = "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";

        private static final String EMAIL_PATTERN = LOCAL_PARTS_PATTERN + DOMAIN_PATTERN;

        private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        public final void validate(final String email) {
            Matcher matcher = pattern.matcher(email);
            if(!matcher.matches()) {
                throw new BaseException(ExceptionInformation.EMAIL_ADDRESS_INVALID);
            }
        }
    }
}


