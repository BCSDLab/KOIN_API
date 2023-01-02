package koreatech.in.dto.shop.admin.response;

import koreatech.in.domain.Shop.RelatedToShop;
import koreatech.in.mapstruct.shop.admin.AdminShopMapper;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class ShopsResponse {
    private Integer total_count;
    private Integer current_count;
    private Integer total_page;
    private Integer current_page;
    private List<Shop> shops;

    @Getter @Builder
    public static final class Shop {
        private Integer id;
        private String name;
        private String phone;
        private List<String> category_names;
        private Boolean is_deleted;
    }

    public static ShopsResponse of(Integer totalCount, Integer totalPage, Integer currentPage, List<RelatedToShop> relatedToShops) {
        return ShopsResponse.builder()
                .total_count(totalCount)
                .current_count(relatedToShops.size())
                .total_page(totalPage)
                .current_page(currentPage)
                .shops(
                        relatedToShops.stream()
                                .map(AdminShopMapper.INSTANCE::toShopsResponse$Shop)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
