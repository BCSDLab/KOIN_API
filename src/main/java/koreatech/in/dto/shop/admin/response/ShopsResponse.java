package koreatech.in.dto.shop.admin.response;

import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.mapstruct.shop.admin.AdminShopConverter;
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
    public static class Shop {
        private Integer id;
        private String name;
        private String phone;
        private List<String> category_names;
        private Boolean is_deleted;
    }

    public static ShopsResponse of(Integer totalCount, Integer totalPage, Integer currentPage, List<ShopProfile> shopProfiles) {
        return ShopsResponse.builder()
                .total_count(totalCount)
                .current_count(shopProfiles.size())
                .total_page(totalPage)
                .current_page(currentPage)
                .shops(
                        shopProfiles.stream()
                                .map(AdminShopConverter.INSTANCE::toShopsResponse$Shop)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
