package koreatech.in.domain;

public enum Redis {
    ownerAuthPrefix("owner@"),
    ownerChangePasswordAuthPrefix("owner_password_change@");

    private final String prefixOfKey;

    Redis(String prefixOfKey) {
        this.prefixOfKey = prefixOfKey;
    }

    public String getKey(String suffix) {
        return prefixOfKey+suffix;
    }
}
