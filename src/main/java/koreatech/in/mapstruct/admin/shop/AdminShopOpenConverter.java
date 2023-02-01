package koreatech.in.mapstruct.admin.shop;

import koreatech.in.domain.Shop.ShopOpen;
import koreatech.in.dto.admin.shop.request.CreateShopRequest;
import koreatech.in.dto.admin.shop.request.UpdateShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminShopOpenConverter {
    AdminShopOpenConverter INSTANCE = Mappers.getMapper(AdminShopOpenConverter.class);

    ShopOpen toShopOpen(CreateShopRequest.Open open);

    ShopOpen toShopOpen(UpdateShopRequest.Open open);
}
