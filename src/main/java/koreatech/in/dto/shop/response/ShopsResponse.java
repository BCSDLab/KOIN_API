package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.MinimizedShop;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class ShopsResponse {
    private Integer total_page;
    private Integer current_page;
    private List<MinimizedShop> shops;
}
