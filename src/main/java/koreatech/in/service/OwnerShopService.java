package koreatech.in.service;

import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;

public interface OwnerShopService {
    void createMenuCategory(Integer shopId, CreateMenuCategoryRequest request);

    AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId);
}
