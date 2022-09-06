package koreatech.in.controller.v2.dto.shop;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class ShopMenuRequestDTO {
    @NotNull(groups = ValidationGroups.Update.class, message = "상점 메뉴 고유 id는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "상점 메뉴 고유 id", example = "1", hidden = true)
    private Integer id;

    @NotNull(groups = ValidationGroups.Create.class, message = "상점 고유 id는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "상점 고유 id", example = "1")
    private Integer shop_id;

    @NotNull(groups = ValidationGroups.Create.class, message = "메뉴 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "메뉴 이름", example = "탕수육")
    private String name;

    @NotNull(groups = ValidationGroups.Create.class, message = "단일 메뉴 여부는 비워둘 수 없습니다")
    @ApiModelProperty(notes = "단일 메뉴 여부", example = "true")
    private Boolean is_single;

    @ApiModelProperty(notes = "단일 메뉴일때의 가격", example = "12000")
    private Integer single_price;

    @ApiModelProperty(notes = "단일 메뉴가 아닐때의 옵션에 따른 가격")
    private List<Map<String, Integer>> option_prices;

    @NotNull(groups = ValidationGroups.Create.class, message = "존재하는 카테고리명 리스트는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "존재하는 카테고리명 리스트")
    private List<String> existent_categories;

    @NotNull(groups = ValidationGroups.Create.class, message = "선택된 카테고리명 리스트는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "선택된 카테고리명 리스트")
    private List<String> selected_categories;

    @ApiModelProperty(notes = "메뉴 구성 설명")
    private String description;

    @ApiModelProperty(notes = "메뉴 이미지 리스트", hidden = true)
    private List<String> image_urls;

    public void setSingleMenu(Integer single_price) {
        this.is_single = true;
        this.single_price = single_price;
    }

    public void addOptionPrice(List<Map<String, Integer>> option_prices) {
        this.option_prices = option_prices;
    }

    public void setExistentCategories(List<String> categories) {
        if (this.existent_categories == null) {
            this.existent_categories = new ArrayList<>();
        }
        this.existent_categories.addAll(categories);
    }

    public void setSelectedCategories(List<String> categories) {
        if (this.selected_categories == null) {
            this.selected_categories = new ArrayList<>();
        }
        this.selected_categories.addAll(categories);
    }

    public void setImages(List<String> image_urls) {
        if (this.image_urls == null) {
            this.image_urls = new ArrayList<>();
        }
        this.image_urls.addAll(image_urls);
    }
}
