package koreatech.in.service;

import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.CreateMenuRequest;
import koreatech.in.dto.normal.shop.request.CreateShopRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuRequest;
import koreatech.in.dto.normal.shop.request.UpdateShopRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllShopsOfOwnerResponse;
import koreatech.in.dto.normal.shop.response.MenuResponse;
import koreatech.in.dto.normal.shop.response.ShopResponse;

public interface OwnerShopService {
    ShopResponse getShop(Integer shopId);

    AllShopsOfOwnerResponse getAllShopsOfOwner();

    void createShop(CreateShopRequest request);

    void updateShop(Integer shopId, UpdateShopRequest request);

    void createMenuCategory(Integer shopId, CreateMenuCategoryRequest request);

    AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId);

    void updateMenuCategory(Integer menuCategoryId, UpdateMenuCategoryRequest request);

    void deleteMenuCategory(Integer shopId, Integer menuCategoryId);

    void createMenu(Integer shopId, CreateMenuRequest request);

    MenuResponse getMenu(Integer shopId, Integer menuId);

    AllMenusOfShopResponse getAllMenusOfShop(Integer shopId);

    void updateMenu(Integer shopId, Integer menuId, UpdateMenuRequest request);

    void deleteMenu(Integer shopId, Integer menuId);
}
