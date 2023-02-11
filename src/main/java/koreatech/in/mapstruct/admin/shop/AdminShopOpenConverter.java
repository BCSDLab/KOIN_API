package koreatech.in.mapstruct.admin.shop;

import koreatech.in.domain.Shop.ShopOpen;
import koreatech.in.dto.admin.shop.request.CreateShopRequest;
import koreatech.in.dto.admin.shop.request.UpdateShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminShopOpenConverter {
    AdminShopOpenConverter INSTANCE = Mappers.getMapper(AdminShopOpenConverter.class);

    @Mapping(source = "shopId", target = "shop_id")
    ShopOpen toShopOpen(CreateShopRequest.Open open, Integer shopId);

    @Mapping(source = "shopId", target = "shop_id")
    ShopOpen toShopOpen(UpdateShopRequest.Open open, Integer shopId);
}
