package koreatech.in.service;

import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;

public interface OwnerShopService {
    void createMenuCategory(Integer shopId, CreateMenuCategoryRequest request);
}
