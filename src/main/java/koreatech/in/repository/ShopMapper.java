package koreatech.in.repository;

import koreatech.in.domain.Shop.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("shopMapper")
public interface ShopMapper {
    List<ShopCategory> getAllShopCategories();

    ShopProfile getShopProfileByShopId(@Param("shopId") Integer shopId);

    List<ShopProfile> getAllShopProfiles();

    List<ShopProfile> getShopProfilesByOwnerId(@Param("ownerId") Integer ownerId);

    List<ShopMenuProfile> getMenuProfilesByShopId(@Param("shopId") Integer shopId);

    Shop getShopById(@Param("id") Integer id);

    List<Shop> getShopByOwnerId(@Param("ownerId") Integer ownerId);

    void createShop(@Param("shop") Shop shop);

    void updateShop(@Param("shop") Shop shop);

    void createShopOpens(@Param("shopOpens") List<ShopOpen> shopOpens);

    void updateShopOpens(@Param("shopOpens") List<ShopOpen> shopOpens);

    ShopCategory getShopCategoryById(@Param("id") Integer id);

    List<ShopCategoryMap> getShopCategoryMapsByShopId(@Param("shopId") Integer shopId);

    void createShopCategoryMaps(@Param("shopCategoryMaps") List<ShopCategoryMap> shopCategoryMaps);

    void deleteShopCategoryMaps(@Param("shopCategoryMaps") List<ShopCategoryMap> shopCategoryMaps);

    List<ShopImage> getShopImagesByShopId(@Param("shopId") Integer shopId);

    void createShopImages(@Param("shopImages") List<ShopImage> shopImages);

    void deleteShopImages(@Param("shopImages") List<ShopImage> shopImages);

    ShopMenuCategory getMenuCategoryByShopIdAndName(@Param("shopId") Integer shopId, @Param("name") String name);

    Integer getCountOfMenuCategoriesByShopId(@Param("shopId") Integer shopId);

    void createMenuCategory(@Param("menuCategory") ShopMenuCategory menuCategory);

    List<ShopMenuCategory> getMenuCategoriesByShopId(@Param("shopId") Integer shopId);

    ShopMenuCategory getMenuCategoryById(@Param("id") Integer id);

    List<ShopCategory> getMenuCategoryNamesByShopId(@Param("shopId") Integer id);

    List<ShopMenu> getMenusUsingCategoryByMenuCategoryId(@Param("menuCategoryId") Integer menuCategoryId);

    void deleteMenuCategoryById(@Param("id") Integer id);

    void createMenu(@Param("menu") ShopMenu menu);

    void createMenuDetail(@Param("menuDetail") ShopMenuDetail menuDetail);

    void createMenuDetails(@Param("menuDetails") List<ShopMenuDetail> menuDetails);

    void createMenuCategoryMaps(@Param("menuCategoryMaps") List<ShopMenuCategoryMap> menuCategoryMaps);

    void createMenuImages(@Param("menuImages") List<ShopMenuImage> menuImages);

    ShopMenuProfile getMenuProfileByMenuId(@Param("menuId") Integer menuId);

    void updateMenu(@Param("menu") ShopMenu menu);

    List<ShopMenuDetail> getMenuDetailsByMenuId(@Param("menuId") Integer menuId);

    void deleteMenuDetails(@Param("menuDetails") List<ShopMenuDetail> menuDetails);

    void deleteMenuDetailsByMenuId(@Param("menuId") Integer menuId);

    List<ShopMenuCategoryMap> getMenuCategoryMapsByMenuId(@Param("menuId") Integer menuId);

    void deleteMenuCategoryMaps(@Param("menuCategoryMaps") List<ShopMenuCategoryMap> menuCategoryMaps);

    List<ShopMenuImage> getMenuImagesByMenuId(@Param("menuId") Integer menuId);

    void deleteMenuImages(@Param("menuImages") List<ShopMenuImage> menuImages);

    ShopMenu getMenuById(@Param("id") Integer id);

    void deleteMenuById(@Param("id") Integer id);
}
