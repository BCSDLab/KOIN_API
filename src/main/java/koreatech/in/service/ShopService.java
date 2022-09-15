package koreatech.in.service;

import koreatech.in.controller.v2.dto.shop.request.CreateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuCategoryDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.response.ResponseShopMenuForOwnerDTO;
import koreatech.in.controller.v2.dto.shop.response.ResponseShopMenusForOwnerDTO;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Shop.Menu;
import koreatech.in.domain.Shop.Shop;

import java.util.List;
import java.util.Map;

public interface ShopService {
    Map<String, Object> getShops() throws Exception;

    Map<String, Object> getShop(String id) throws Exception;

    Shop createShopForAdmin(Shop article) throws Exception;

    Map<String, Object> getShopsForAdmin(Criteria criteria) throws Exception;

    Map<String, Object> getShopForAdmin(String id) throws Exception;

    Shop updateShopForAdmin(Shop item, int id) throws Exception;

    Map<String, Object> deleteShopForAdmin(int id) throws Exception;

    Menu createMenuForAdmin(Menu menu, int item_id) throws Exception;

    Map<String, Object> getMenuForAdmin(int shop_id, int menu_id) throws Exception;

    Menu updateMenuForAdmin(Menu menu, int shop_id, int id) throws Exception;

    Map<String, Object> deleteMenuForAdmin(int id) throws Exception;

    Map<String, Object> updateMenuCategoriesForOwner(UpdateShopMenuCategoryDTO dto);

    Map<String, Object> createMenuForOwner(CreateShopMenuDTO dto) throws Exception;

    ResponseShopMenuForOwnerDTO getMenuForOwner(Integer shopId, Integer menuId) throws Exception;

    Map<String, Object> updateMenuForOwner(UpdateShopMenuDTO dto) throws Exception;

    Map<String, Object> deleteMenuForOwner(Integer shopId, Integer menuId) throws Exception;

    Map<String, Object> hideMenuForOwner(Integer shopId, Integer menuId, Boolean flag) throws Exception;

    ResponseShopMenusForOwnerDTO getMenusForOwner(Integer shopId) throws Exception;
}
