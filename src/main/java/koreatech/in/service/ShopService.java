package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Shop.Menu;
import koreatech.in.domain.Shop.Shop;

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

    Map<String, Object> deleteMenuForAdmin(int shop_id, int id) throws Exception;
}
