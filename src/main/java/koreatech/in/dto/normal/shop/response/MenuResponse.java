package koreatech.in.dto.normal.shop.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class MenuResponse {
    @ApiModelProperty(notes = "고유 id", example = "1", required = true)
    private Integer id;

    @ApiModelProperty(notes = "메뉴가 소속된 상점의 고유 id", example = "1", required = true)
    private Integer shop_id;

    @ApiModelProperty(notes = "이름", example = "탕수육", required = true)
    private String name;

    @ApiModelProperty(notes = "숨김 여부", example = "false", required = true)
    private Boolean is_hidden;

    @ApiModelProperty(notes = "단일 메뉴 여부", example = "false", required = true)
    private Boolean is_single;

    @ApiModelProperty(notes = "단일 메뉴일때(is_single이 true일때)의 가격", example = "7000")
    private Integer single_price;

    @ApiModelProperty(notes = "옵션이 있는 메뉴일때(is_single이 false일때)의 가격")
    private List<OptionPrice> option_prices;

    @ApiModelProperty(notes = "구성 설명", example = "돼지고기 + 튀김")
    private String description;

    @ApiModelProperty(notes = "소속되어 있는 메뉴 카테고리 고유 id 리스트", required = true)
    private List<Integer> category_ids;

    @ApiModelProperty(notes = "이미지 URL 리스트")
    private List<String> image_urls;

    @Getter @Builder
    @ApiModel("OptionPrice_7")
    public static class OptionPrice {
        @ApiModelProperty(notes = "옵션명", example = "소", required = true)
        private String option;

        @ApiModelProperty(notes = "옵션에 대한 가격", example = "10000", required = true)
        private Integer price;
    }
}
