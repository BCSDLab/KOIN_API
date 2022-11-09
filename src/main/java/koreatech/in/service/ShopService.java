package koreatech.in.service;

import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {
    ResponseSuccessCreateDTO createShopCategoryForAdmin(CreateShopCategoryDTO dto, MultipartFile image) throws Exception;

    ResponseSuccessfulDTO updateShopCategoryForAdmin(Integer shopCategoryId, UpdateShopCategoryDTO dto, MultipartFile image) throws Exception;

    ResponseSuccessfulDTO deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception;

    ResponseAllShopCategoriesDTO getAllShopCategoriesForAdmin() throws Exception;

    ResponseSuccessfulDTO matchShopWithOwner(Integer shopId, MatchShopWithOwnerDTO dto) throws Exception;

    ResponseSuccessCreateDTO createShopForAdmin(CreateShopDTO dto, List<MultipartFile> images) throws Exception;

    ResponseShopDTO getShopForAdmin(Integer shopId) throws Exception;

    ResponseSuccessfulDTO updateShopForAdmin(Integer shopId, UpdateShopDTO dto, List<MultipartFile> images) throws Exception;

    ResponseSuccessfulDTO deleteShopForAdmin(Integer shopId) throws Exception;

    ResponseSuccessfulDTO undeleteOfShopForAdmin(Integer shopId) throws Exception;

    ResponseShopsDTO getShopsForAdmin(ShopsConditionDTO dto) throws Exception;

    ResponseSuccessCreateDTO createMenuCategoryForAdmin(Integer shopId, CreateShopMenuCategoryDTO dto) throws Exception;

    ResponseShopMenuCategoriesDTO getAllMenuCategoriesOfShopForAdmin(Integer shopId) throws Exception;

    ResponseSuccessfulDTO deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception;

    ResponseSuccessCreateDTO createMenuForAdmin(Integer shopId, CreateShopMenuDTO dto, List<MultipartFile> images) throws Exception;

    ResponseShopMenuDTO getMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    ResponseSuccessfulDTO updateMenuForAdmin(Integer shopId, Integer menuId, UpdateShopMenuDTO dto, List<MultipartFile> images) throws Exception;

    ResponseSuccessfulDTO deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    ResponseSuccessfulDTO hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hide) throws Exception;

    ResponseShopMenusDTO getAllMenusOfShopForAdmin(Integer shopId) throws Exception;

    ResponseShopDTO getShop(Integer shopId) throws Exception;

    ResponseAllShopsDTO getAllShops() throws Exception;

    ResponseAllShopCategoriesDTO getAllShopCategories() throws Exception;
}
