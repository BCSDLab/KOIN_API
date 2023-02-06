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
}