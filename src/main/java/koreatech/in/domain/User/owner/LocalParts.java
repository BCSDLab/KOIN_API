package koreatech.in.domain.User.owner;

import lombok.Getter;

@Getter
public class LocalParts {
    public static final int BEGIN_INDEX = 0;

    private final String value;

    private LocalParts(String value) {
        this.value = value;
    }

    private static String localPartsFor(String fullAddress) {
        return fullAddress.substring(BEGIN_INDEX, EmailAddress.getSeparateIndex(fullAddress));
    }

    public static LocalParts from(String fullAddress) {
        EmailAddress.validates(fullAddress);

        return new LocalParts(localPartsFor(fullAddress));
    }

}