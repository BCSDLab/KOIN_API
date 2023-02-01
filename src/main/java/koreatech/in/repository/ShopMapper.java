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
}