package koreatech.in.repository.admin;

import koreatech.in.domain.Shop.*;
import koreatech.in.dto.admin.shop.request.ShopCategoriesCondition;
import koreatech.in.dto.admin.shop.request.ShopsCondition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminShopMapper {
    ShopCategory getShopCategoryByName(@Param("name") String name);

    void createShopCategory(@Param("shopCategory") ShopCategory shopCategory);

    ShopCategory getShopCategoryById(@Param("id") Integer id);

    Integer getTotalCountOfShopCategoriesByCondition(@Param("condition") ShopCategoriesCondition condition);

    List<ShopCategory> getShopCategoriesByCondition(@Param("begin") Integer begin, @Param("condition") ShopCategoriesCondition condition);

    void updateShopCategory(@Param("shopCategory") ShopCategory shopCategory);

    List<Shop> getShopsUsingCategoryByShopCategoryId(@Param("shopCategoryId") Integer shopCategoryId);

    void deleteShopCategoryById(@Param("id") Integer id);

    void createShop(@Param("shop") Shop shop);

    void createShopOpens(@Param("shopOpens") List<ShopOpen> shopOpens);

    void createShopCategoryMaps(@Param("shopCategoryMaps") List<ShopCategoryMap> shopCategoryMaps);

    void createMenuCategories(@Param("menuCategories") List<ShopMenuCategory> menuCategories);

    void createShopImages(@Param("shopImages") List<ShopImage> shopImages);

    ShopProfile getShopProfileByShopId(@Param("shopId") Integer shopId);

    Integer getTotalCountOfShopsByCondition(@Param("condition") ShopsCondition condition);

    List<ShopProfile> getShopProfilesByCondition(@Param("begin") Integer begin, @Param("condition") ShopsCondition condition);

    void updateShop(@Param("shop") Shop shop);

    void updateShopOpens(@Param("shopOpens") List<ShopOpen> shopOpens);

    List<ShopCategoryMap> getShopCategoryMapsByShopId(@Param("shopId") Integer shopId);

    void deleteShopCategoryMaps(@Param("shopCategoryMaps") List<ShopCategoryMap> shopCategoryMaps);

    List<ShopImage> getShopImagesByShopId(@Param("shopId") Integer shopId);

    void deleteShopImages(@Param("shopImages") List<ShopImage> shopImages);

    List<ShopMenuCategoryMap> getMenuCategoryMapsByMenuId(@Param("menuId") Integer menuId);

    void deleteMenuCategoryMaps(@Param("menuCategoryMaps") List<ShopMenuCategoryMap> menuCategoryMaps);

    List<ShopMenuImage> getMenuImagesByMenuId(@Param("menuId") Integer menuId);

    void deleteMenuImages(@Param("menuImages") List<ShopMenuImage> menuImages);

    void deleteShopById(@Param("id") Integer id);

    void undeleteShopById(@Param("id") Integer id);

    Shop getShopById(@Param("id") Integer id);

    ShopMenuCategory getMenuCategoryByShopIdAndName(@Param("shopId") Integer shopId, @Param("name") String name);

    Integer getCountOfMenuCategoriesByShopId(@Param("shopId") Integer shopId);

    void createMenuCategory(@Param("menuCategory") ShopMenuCategory menuCategory);

    List<ShopMenuCategory> getMenuCategoriesByShopId(@Param("shopId") Integer shopId);

    ShopMenuCategory getMenuCategoryById(@Param("id") Integer id);

    List<ShopMenu> getMenusUsingCategoryByMenuCategoryId(@Param("menuCategoryId") Integer menuCategoryId);

    void deleteMenuCategoryById(@Param("id") Integer id);

    void createMenu(@Param("menu") ShopMenu menu);

    void createMenuDetail(@Param("menuDetail") ShopMenuDetail menuDetail);

    void createMenuDetails(@Param("menuDetails") List<ShopMenuDetail> menuDetails);

    void createMenuCategoryMaps(@Param("menuCategoryMaps") List<ShopMenuCategoryMap> menuCategoryMaps);

    void createMenuImages(@Param("menuImages") List<ShopMenuImage> menuImages);

    ShopMenuProfile getMenuProfileByMenuId(@Param("menuId") Integer menuId);

    List<ShopMenuProfile> getMenuProfilesByShopId(@Param("shopId") Integer shopId);

    ShopMenu getMenuById(@Param("id") Integer id);

    void updateMenu(@Param("menu") ShopMenu menu);

    List<ShopMenuDetail> getMenuDetailsByMenuId(@Param("menuId") Integer menuId);

    void deleteMenuDetails(@Param("menuDetails") List<ShopMenuDetail> menuDetails);

    void deleteMenuDetailsByMenuId(@Param("menuId") Integer menuId);

    void deleteMenuById(@Param("menuId") Integer menuId);

    void deleteAllInformationRelatedToMenuByMenuId(@Param("menuId") Integer menuId);

    void hideMenuById(@Param("menuId") Integer menuId);

    void revealMenuById(@Param("menuId") Integer menuId);

    void createShopOwners(@Param("ownerId") Integer ownerId, @Param("shopId") Integer shopId);
}
