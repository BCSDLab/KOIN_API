package koreatech.in.controller.v2.dto.shop.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateShopMenuCategoryDTO {
    private List<String> categories;
}
