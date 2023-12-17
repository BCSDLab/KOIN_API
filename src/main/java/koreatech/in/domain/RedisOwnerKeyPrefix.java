package koreatech.in.domain;

public enum RedisOwnerKeyPrefix {
    ownerAuthPrefix("owner@"),
    ownerChangePasswordAuthPrefix("owner_password_change@");

    private final String prefixOfKey;

    RedisOwnerKeyPrefix(String prefixOfKey) {
        this.prefixOfKey = prefixOfKey;
    }

    public String getKey(String suffix) {
        return prefixOfKey+suffix;
    }
}
