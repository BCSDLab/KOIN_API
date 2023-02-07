package koreatech.in.dto.normal.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.mapstruct.normal.shop.ShopConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Builder
public class AllShopsResponse {
    @ApiModelProperty(notes = "개수", example = "100", required = true)
    private Integer count;

    @ApiModelProperty(notes = "모든 상점 리스트", required = true)
    private List<Shop> shops;

    @Getter @Builder
    @ApiModel("Shop_2")
    public static class Shop {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "수신반점", required = true)
        private String name;

        @ApiModelProperty(notes = "전화번호", example = "041-000-0000", required = true)
        private String phone;

        @ApiModelProperty(notes = "배달 가능 여부", example = "true", required = true)
        private Boolean delivery;

        @ApiModelProperty(notes = "카드 계산 가능 여부", example = "false", required = true)
        private Boolean pay_card;

        @ApiModelProperty(notes = "계좌 이체 가능 여부", example = "true", required = true)
        private Boolean pay_bank;

        @ApiModelProperty(notes = "요일별 휴무 여부 및 장사 시간", required = true)
        private List<Open> open;

        @ApiModelProperty(notes = "속해있는 상점 카테고리들의 고유 id 리스트", required = true)
        private List<Integer> category_ids;

        @Getter @Builder
        @ApiModel("Open_5")
        public static class Open {
            @ApiModelProperty(notes = "요일", example = "MONDAY", required = true)
            private DayOfWeek day_of_week;

            @ApiModelProperty(notes = "휴무 여부 \n" +
                    "- true: 휴무 O \n" +
                    "- false: 휴무 X", example = "false", required = true)
            private Boolean closed;

            @ApiModelProperty(notes = "오픈 시간", example = "16:00", required = true)
            private String open_time;

            @ApiModelProperty(notes = "마감 시간", example = "02:00", required = true)
            private String close_time;
        }
    }

    public static AllShopsResponse from(List<ShopProfile> shopProfiles) {
        return AllShopsResponse.builder()
                .count(shopProfiles.size())
                .shops(
                        shopProfiles.stream()
                                .map(ShopConverter.INSTANCE::toAllShopsResponse$Shop)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
