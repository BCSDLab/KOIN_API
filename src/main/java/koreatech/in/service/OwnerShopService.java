package koreatech.in.service;

import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.CreateMenuRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.MenuResponse;

public interface OwnerShopService {
    void createMenuCategory(Integer shopId, CreateMenuCategoryRequest request);

    AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId);

    void deleteMenuCategory(Integer shopId, Integer menuCategoryId);

    void createMenu(Integer shopId, CreateMenuRequest request);

    MenuResponse getMenu(Integer shopId, Integer menuId);

    AllMenusOfShopResponse getAllMenusOfShop(Integer shopId);

    void deleteMenu(Integer shopId, Integer menuId);
}
