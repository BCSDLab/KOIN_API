package koreatech.in.service;

import koreatech.in.dto.normal.shop.response.*;

public interface ShopService {
    AllShopCategoriesResponse getAllShopCategories();

    ShopResponse getShop(Integer shopId);

    AllShopsResponse getAllShops();

    AllMenusOfShopResponse getAllMenusOfShop(Integer shopId);

    MenuResponse getMenu(Integer shopId, Integer menuId);
}
