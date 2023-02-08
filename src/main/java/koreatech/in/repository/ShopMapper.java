package koreatech.in.repository;

import koreatech.in.domain.Shop.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("shopMapper")
public interface ShopMapper {
    List<ShopCategory> getAllShopCategories();

    ShopProfile getShopProfileByShopId(@Param("shopId") Integer shopId);

    List<ShopProfile> getAllShopProfiles();

    List<ShopMenuProfile> getMenuProfilesByShopId(@Param("shopId") Integer shopId);

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
}