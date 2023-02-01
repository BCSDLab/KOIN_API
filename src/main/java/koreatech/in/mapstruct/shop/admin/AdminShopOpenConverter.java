package koreatech.in.mapstruct.shop.admin;

import koreatech.in.domain.Shop.ShopOpen;
import koreatech.in.dto.shop.admin.request.CreateShopRequest;
import koreatech.in.dto.shop.admin.request.UpdateShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminShopOpenConverter {
    AdminShopOpenConverter INSTANCE = Mappers.getMapper(AdminShopOpenConverter.class);

    ShopOpen toShopOpen(CreateShopRequest.Open open);

    ShopOpen toShopOpen(UpdateShopRequest.Open open);
}
