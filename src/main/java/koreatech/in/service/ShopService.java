package koreatech.in.service;

import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {
    SuccessCreateResponse createShopCategoryForAdmin(CreateShopCategoryDTO dto, MultipartFile image) throws Exception;

    SuccessResponse updateShopCategoryForAdmin(Integer shopCategoryId, UpdateShopCategoryDTO dto, MultipartFile image) throws Exception;

    SuccessResponse deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception;

    AllShopCategoriesResponse getAllShopCategoriesForAdmin() throws Exception;

    SuccessResponse matchShopWithOwner(Integer shopId, MatchShopWithOwnerDTO dto) throws Exception;

    SuccessCreateResponse createShopForAdmin(CreateShopDTO dto, List<MultipartFile> images) throws Exception;

    ShopResponse getShopForAdmin(Integer shopId) throws Exception;

    SuccessResponse updateShopForAdmin(Integer shopId, UpdateShopDTO dto, List<MultipartFile> images) throws Exception;

    SuccessResponse deleteShopForAdmin(Integer shopId) throws Exception;

    SuccessResponse undeleteOfShopForAdmin(Integer shopId) throws Exception;

    ShopsResponse getShopsForAdmin(ShopsConditionDTO dto) throws Exception;

    SuccessCreateResponse createMenuCategoryForAdmin(Integer shopId, CreateShopMenuCategoryDTO dto) throws Exception;

    AllMenuCategoriesResponse getAllMenuCategoriesOfShopForAdmin(Integer shopId) throws Exception;

    SuccessResponse deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception;

    SuccessCreateResponse createMenuForAdmin(Integer shopId, CreateShopMenuDTO dto, List<MultipartFile> images) throws Exception;

    MenuResponse getMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    SuccessResponse updateMenuForAdmin(Integer shopId, Integer menuId, UpdateShopMenuDTO dto, List<MultipartFile> images) throws Exception;

    SuccessResponse deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    SuccessResponse hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hide) throws Exception;

    MenusResponse getAllMenusOfShopForAdmin(Integer shopId) throws Exception;

    ShopResponse getShop(Integer shopId) throws Exception;

    AllShopsResponse getAllShops() throws Exception;

    AllShopCategoriesResponse getAllShopCategories() throws Exception;
}
