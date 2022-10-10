package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.Shop;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class ResponseShopsDTO {
    private Integer total_page;
    private Integer current_page;
    private List<Shop> shops;
}
