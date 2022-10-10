package koreatech.in.dto.shop.response;

import koreatech.in.dto.shop.response.inner.Shop;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class ResponseAllShopsDTO {
    private Integer total_count;
    private List<Shop> shops;
}
