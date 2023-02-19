package koreatech.in.dto.normal.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllShopsOfOwnerResponse {
    @ApiModelProperty(notes = "개수", example = "2", required = true)
    private Integer count;

    @ApiModelProperty(notes = "상점 목록", required = true)
    private List<Shop> shops;

    @Getter @AllArgsConstructor
    @ApiModel("Shop_3")
    private static class Shop {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "김밥천국", required = true)
        private String name;
    }

    public static AllShopsOfOwnerResponse from(List<ShopProfile> shopProfiles) {
        return AllShopsOfOwnerResponse.builder()
                .count(shopProfiles.size())
                .shops(shopProfiles.stream()
                        .map(shopProfile -> new Shop(shopProfile.getId(), shopProfile.getName()))
                        .collect(Collectors.toList())
                )
                .build();
    }
}
