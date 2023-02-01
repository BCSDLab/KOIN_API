package koreatech.in.service;

import koreatech.in.dto.shop.normal.response.*;

public interface ShopService {
    AllShopCategoriesResponse getAllShopCategories();

    ShopResponse getShop(Integer shopId);

    AllShopsResponse getAllShops();

    AllMenusOfShopResponse getAllMenusOfShop(Integer shopId);
}
