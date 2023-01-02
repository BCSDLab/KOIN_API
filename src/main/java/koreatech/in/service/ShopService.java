package koreatech.in.service;

import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.shop.admin.request.*;
import koreatech.in.dto.shop.admin.response.*;
import koreatech.in.dto.shop.normal.response.AllShopsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {
    void createShopCategoryForAdmin(CreateShopCategoryRequest request) throws Exception;

    ShopCategoryResponse getShopCategoryForAdmin(Integer shopCategoryId) throws Exception;

    ShopCategoriesResponse getShopCategoriesForAdmin(ShopCategoriesCondition condition) throws Exception;

    void updateShopCategoryForAdmin(Integer shopCategoryId, UpdateShopCategoryRequest request) throws Exception;

    void deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception;

    void matchShopWithOwner(Integer shopId, MatchShopWithOwnerRequest request) throws Exception;

    void createShopForAdmin(CreateShopRequest request) throws Exception;

    ShopResponse getShopForAdmin(Integer shopId) throws Exception;

    ShopsResponse getShopsForAdmin(ShopsCondition condition) throws Exception;

    void updateShopForAdmin(Integer shopId, UpdateShopRequest request) throws Exception;

    void deleteShopForAdmin(Integer shopId) throws Exception;

    void undeleteOfShopForAdmin(Integer shopId) throws Exception;

    void createMenuCategoryForAdmin(Integer shopId, CreateShopMenuCategoryRequest request) throws Exception;

    AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShopForAdmin(Integer shopId) throws Exception;

    void deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception;

    void createMenuForAdmin(Integer shopId, CreateShopMenuRequest request) throws Exception;

    MenuResponse getMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    AllMenusOfShopResponse getAllMenusOfShopForAdmin(Integer shopId) throws Exception;

    void updateMenuForAdmin(Integer shopId, Integer menuId, UpdateShopMenuRequest request) throws Exception;

    void deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception;

    void hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hidden) throws Exception;

    koreatech.in.dto.shop.normal.response.AllShopCategoriesResponse getAllShopCategories() throws Exception;

    koreatech.in.dto.shop.normal.response.ShopResponse getShop(Integer shopId) throws Exception;

    AllShopsResponse getAllShops() throws Exception;

    koreatech.in.dto.shop.normal.response.AllMenusOfShopResponse getAllMenusOfShop(Integer shopId) throws Exception;

    UploadImageResponse uploadShopCategoryImage(MultipartFile image) throws Exception;

    UploadImagesResponse uploadShopMenuImages(List<MultipartFile> images) throws Exception;

    UploadImagesResponse uploadShopImages(List<MultipartFile> images) throws Exception;
}
