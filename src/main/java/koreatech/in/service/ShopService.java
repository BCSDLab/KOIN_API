package koreatech.in.service;

import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.response.ResponseShopDTO;
import koreatech.in.dto.shop.response.ResponseShopMenuDTO;
import koreatech.in.dto.shop.response.ResponseShopMenusDTO;
import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.domain.Shop.ShopMenuCategory;

import java.util.List;
import java.util.Map;

public interface ShopService {
    Map<String, Object> createShopCategoryForAdmin(CreateShopCategoryDTO dto) throws Exception;

    Map<String, Object> updateShopCategoryForAdmin(UpdateShopCategoryDTO dto) throws Exception;

    Map<String, Object> deleteShopCategoryForAdmin(Integer id) throws Exception;

    List<ShopCategory>  getShopCategoriesForAdmin() throws Exception;

    Map<String, Object> createShopForAdmin(CreateShopDTO dto) throws Exception;

    ResponseShopDTO getShopForAdmin(Integer shopId) throws Exception;

    Map<String, Object> updateShopForAdmin(UpdateShopDTO init) throws Exception;

    Map<String, Object> deleteShopForAdmin(Integer id) throws Exception;

    Object getShopsForAdmin() throws Exception;

    Map<String, Object> createMenuCategoryForAdmin(CreateShopMenuCategoryDTO dto) throws Exception;

    Map<String, Object> deleteMenuCategoryForAdmin(Integer shopId, Integer categoryId) throws Exception;

    List<ShopMenuCategory> getMenuCategoriesForAdmin(Integer shopId) throws Exception;

    Map<String, Object> createMenuForAdmin(CreateShopMenuDTO dto) throws Exception;

    ResponseShopMenuDTO getMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    Map<String, Object> updateMenuForAdmin(UpdateShopMenuDTO dto) throws Exception;

    Map<String, Object> deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    Map<String, Object> hideMenuForAdmin(Integer shopId, Integer menuId, Boolean flag) throws Exception;

    ResponseShopMenusDTO getMenusForAdmin(Integer shopId) throws Exception;
}
