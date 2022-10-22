package koreatech.in.service;

import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.response.*;

public interface ShopService {
    ResponseSuccessCreateDTO createShopCategoryForAdmin(CreateShopCategoryDTO dto) throws Exception;

    ResponseSuccessfulDTO updateShopCategoryForAdmin(UpdateShopCategoryDTO dto) throws Exception;

    ResponseSuccessfulDTO deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception;

    ResponseAllShopCategoriesDTO getShopCategoriesForAdmin() throws Exception;

    ResponseSuccessfulDTO matchShopWithOwner(MatchShopWithOwnerDTO dto) throws Exception;

    ResponseSuccessCreateDTO createShopForAdmin(CreateShopDTO dto) throws Exception;

    ResponseShopDTO getShopForAdmin(Integer shopId) throws Exception;

    ResponseSuccessfulDTO updateShopForAdmin(UpdateShopDTO dto) throws Exception;

    ResponseSuccessfulDTO deleteShopForAdmin(Integer shopId) throws Exception;

    ResponseSuccessfulDTO undeleteOfShopForAdmin(Integer shopId) throws Exception;

    ResponseShopsDTO getShopsForAdmin(ShopsConditionDTO dto) throws Exception;

    ResponseSuccessCreateDTO createMenuCategoryForAdmin(CreateShopMenuCategoryDTO dto) throws Exception;

    ResponseShopMenuCategoriesDTO getMenuCategoriesForAdmin(Integer shopId) throws Exception;

    ResponseSuccessfulDTO deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception;

    ResponseSuccessCreateDTO createMenuForAdmin(CreateShopMenuDTO dto) throws Exception;

    ResponseShopMenuDTO getMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    ResponseSuccessfulDTO updateMenuForAdmin(UpdateShopMenuDTO dto) throws Exception;

    ResponseSuccessfulDTO deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    ResponseSuccessfulDTO hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hide) throws Exception;

    ResponseShopMenusDTO getMenusForAdmin(Integer shopId) throws Exception;

    ResponseShopDTO getShop(Integer shopId) throws Exception;

    ResponseAllShopsDTO getShops() throws Exception;

    ResponseAllShopCategoriesDTO getShopCategories() throws Exception;
}
