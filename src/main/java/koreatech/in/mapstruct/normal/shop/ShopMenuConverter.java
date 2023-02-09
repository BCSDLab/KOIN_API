package koreatech.in.mapstruct.normal.shop;

import koreatech.in.domain.Shop.ShopMenu;
import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.dto.normal.shop.request.CreateMenuRequest;
import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.MenuResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopMenuConverter {
    ShopMenuConverter INSTANCE = Mappers.getMapper(ShopMenuConverter.class);

    AllMenusOfShopResponse.Menu toAllMenusOfShopResponse$Menu(ShopMenuProfile shopMenuProfile);

    @Mappings({
            @Mapping(target = "is_hidden", constant = "false"),
            @Mapping(source = "shopId", target = "shop_id")
    })
    ShopMenu toShopMenu(CreateMenuRequest request, Integer shopId);

    MenuResponse toMenuResponse(ShopMenuProfile shopMenuProfile);
}
