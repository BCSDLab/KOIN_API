package koreatech.in.service;

import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.shop.admin.request.*;
import koreatech.in.dto.shop.admin.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {
    SuccessCreateResponse createShopCategoryForAdmin(CreateShopCategoryRequest request) throws Exception;

    SuccessResponse updateShopCategoryForAdmin(Integer shopCategoryId, UpdateShopCategoryRequest request) throws Exception;

    SuccessResponse deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception;

    AllShopCategoriesResponse getAllShopCategoriesForAdmin() throws Exception;

    SuccessResponse matchShopWithOwner(Integer shopId, MatchShopWithOwnerRequest request) throws Exception;

    SuccessCreateResponse createShopForAdmin(CreateShopRequest request) throws Exception;

    ShopResponse getShopForAdmin(Integer shopId) throws Exception;

    SuccessResponse updateShopForAdmin(Integer shopId, UpdateShopRequest request) throws Exception;

    SuccessResponse deleteShopForAdmin(Integer shopId) throws Exception;

    SuccessResponse undeleteOfShopForAdmin(Integer shopId) throws Exception;

    ShopsResponse getShopsForAdmin(ShopsCondition condition) throws Exception;

    SuccessCreateResponse createMenuCategoryForAdmin(Integer shopId, CreateShopMenuCategoryRequest request) throws Exception;

    AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShopForAdmin(Integer shopId) throws Exception;

    SuccessResponse deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception;

    SuccessCreateResponse createMenuForAdmin(Integer shopId, CreateShopMenuRequest request) throws Exception;

    MenuResponse getMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    SuccessResponse updateMenuForAdmin(Integer shopId, Integer menuId, UpdateShopMenuRequest request) throws Exception;

    SuccessResponse deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    SuccessResponse hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hidden) throws Exception;

    AllMenusOfShopResponse getAllMenusOfShopForAdmin(Integer shopId) throws Exception;

    ShopResponse getShop(Integer shopId) throws Exception;

    AllShopsResponse getAllShops() throws Exception;

    AllShopCategoriesResponse getAllShopCategories() throws Exception;

    UploadImageResponse uploadShopCategoryImage(MultipartFile image) throws Exception;

    UploadImagesResponse uploadShopMenuImages(List<MultipartFile> images) throws Exception;

    UploadImagesResponse uploadShopImages(List<MultipartFile> images) throws Exception;
}
