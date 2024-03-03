package koreatech.in.dto.admin.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.mapstruct.admin.shop.AdminShopConverter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
@ApiModel("AdminShopsResponse")
public class ShopsResponse {
    @ApiModelProperty(notes = "조건에 해당하는 총 상점의 개수", example = "57", required = true)
    private Integer total_count;

    @ApiModelProperty(notes = "조건에 해당하는 상점중에 현재 페이지에서 조회된 개수", example = "10", required = true)
    private Integer current_count;

    @ApiModelProperty(notes = "조건에 해당하는 상점들을 조회할 수 있는 최대 페이지", example = "6", required = true)
    private Integer total_page;

    @ApiModelProperty(notes = "현재 페이지", example = "2", required = true)
    private Integer current_page;

    @ApiModelProperty(notes = "상점 리스트", required = true)
    private List<Shop> shops;

    @Getter @Builder
    @ApiModel("Shop_1")
    public static class Shop {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "홉스", required = true)
        private String name;

        @ApiModelProperty(notes = "전화번호", example = "041-000-0000")
        private String phone;

        @ApiModelProperty(notes = "상점이 소속된 상점 카테고리 이름 리스트", required = true)
        private List<String> category_names;

        @ApiModelProperty(notes = "삭제(soft delete 여부)", example = "false", required = true)
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
