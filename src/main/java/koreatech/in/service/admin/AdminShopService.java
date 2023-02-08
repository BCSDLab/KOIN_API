package koreatech.in.service.admin;

import koreatech.in.dto.admin.shop.request.*;
import koreatech.in.dto.admin.shop.response.*;

public interface AdminShopService {
    void createShopCategory(CreateShopCategoryRequest request);

    ShopCategoryResponse getShopCategory(Integer shopCategoryId);

    ShopCategoriesResponse getShopCategories(ShopCategoriesCondition condition);

    void updateShopCategory(Integer shopCategoryId, UpdateShopCategoryRequest request);

    void deleteShopCategory(Integer shopCategoryId);

    void createShop(CreateShopRequest request);

    ShopResponse getShop(Integer shopId);

    ShopsResponse getShops(ShopsCondition condition);

    void updateShop(Integer shopId, UpdateShopRequest request);

    void deleteShop(Integer shopId);

    void undeleteOfShop(Integer shopId);

    void createMenuCategory(Integer shopId, CreateShopMenuCategoryRequest request);

    AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId);

    void deleteMenuCategory(Integer shopId, Integer menuCategoryId);

    void createMenu(Integer shopId, CreateShopMenuRequest request);

    ShopMenuResponse getMenu(Integer shopId, Integer menuId);

    AllMenusOfShopResponse getAllMenusOfShop(Integer shopId);

    void updateMenu(Integer shopId, Integer menuId, UpdateShopMenuRequest request);

    void deleteMenu(Integer shopId, Integer menuId);

    void hideMenu(Integer shopId, Integer menuId);

    void revealMenu(Integer shopId, Integer menuId);
}
