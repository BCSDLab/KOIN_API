package koreatech.in.service;

import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllShopCategoriesResponse;
import koreatech.in.dto.normal.shop.response.AllShopsResponse;
import koreatech.in.dto.normal.shop.response.ShopResponse;

public interface ShopService {
    AllShopCategoriesResponse getAllShopCategories();

    ShopResponse getShop(Integer shopId);

    AllShopsResponse getAllShops();

    AllMenusOfShopResponse getAllMenusOfShop(Integer shopId);
}
