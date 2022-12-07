package koreatech.in.dto.shop.admin.response;

import koreatech.in.dto.shop.admin.response.inner.Shop;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class AllShopsResponse {
    private Integer total_count;
    private List<Shop> shops;
}
