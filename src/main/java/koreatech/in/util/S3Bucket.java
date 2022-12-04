package koreatech.in.util;

import lombok.Getter;

@Getter
public enum S3Bucket {
    SHOP("upload/shops"),
    SHOP_MENU("upload/shop_menus"),
    SHOP_CATEGORY("upload/shop_categories");

    S3Bucket(String path) {
        this.path = path;
    }

    private final String path;
}
