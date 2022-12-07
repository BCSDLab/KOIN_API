package koreatech.in.dto.shop.admin.response;

import koreatech.in.dto.shop.admin.response.inner.ShopMenu;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class MenuResponse {
    private ShopMenu menu;
}
