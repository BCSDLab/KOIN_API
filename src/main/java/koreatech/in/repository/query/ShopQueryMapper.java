package koreatech.in.repository.query;

import koreatech.in.dto.shop.admin.request.ShopsCondition;
import koreatech.in.dto.shop.admin.response.AllMenusOfShopResponse;
import koreatech.in.dto.shop.admin.response.MenuResponse;
import koreatech.in.dto.shop.admin.response.ShopResponse;
import koreatech.in.dto.shop.admin.response.ShopsResponse;
import koreatech.in.dto.shop.normal.response.AllShopsResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  상점 관련 DTO 조회 전용 Mapper
 */

@Repository
public interface ShopQueryMapper {
    ShopResponse.Shop getShopInShopResponseForAdmin(@Param("shopId") Integer shopId);

    MenuResponse.Menu getMenuInMenuResponseForAdmin(@Param("shopMenuId") Integer shopMenuId);

    List<AllMenusOfShopResponse.Menu> getMenusInAllMenusOfShopResponseForAdmin(@Param("shopId") Integer shopId);

    Integer getTotalCountOfShopsByConditionForAdmin(@Param("condition") ShopsCondition condition);

    List<ShopsResponse.Shop> getShopsInShopsResponseByConditionForAdmin(@Param("begin") Integer begin, @Param("condition") ShopsCondition condition);

    koreatech.in.dto.shop.normal.response.ShopResponse.Shop getShopInShopResponse(@Param("shopId") Integer shopId);

    List<AllShopsResponse.Shop> getShopsInAllShopsResponse();
}
