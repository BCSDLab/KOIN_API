package koreatech.in.mapstruct.normal.shop;

import koreatech.in.domain.Shop.ShopOpen;
import koreatech.in.dto.normal.shop.request.UpdateShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopOpenConverter {
    ShopOpenConverter INSTANCE = Mappers.getMapper(ShopOpenConverter.class);

    @Mapping(source = "shopId", target = "shop_id")
    ShopOpen toShopOpen(UpdateShopRequest.Open open, Integer shopId);
}
