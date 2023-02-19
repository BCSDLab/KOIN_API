package koreatech.in.service;

import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.CreateMenuRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuRequest;
import koreatech.in.dto.normal.shop.request.UpdateShopRequest;
import koreatech.in.dto.normal.shop.response.*;

public interface OwnerShopService {
    ShopResponse getShop(Integer shopId);

    AllShopsOfOwnerResponse getAllShopsOfOwner();

    void updateShop(Integer shopId, UpdateShopRequest request);

    void createMenuCategory(Integer shopId, CreateMenuCategoryRequest request);

    AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId);

    void deleteMenuCategory(Integer shopId, Integer menuCategoryId);

    void createMenu(Integer shopId, CreateMenuRequest request);

    MenuResponse getMenu(Integer shopId, Integer menuId);

    AllMenusOfShopResponse getAllMenusOfShop(Integer shopId);

    void updateMenu(Integer shopId, Integer menuId, UpdateMenuRequest request);

    void deleteMenu(Integer shopId, Integer menuId);
}
