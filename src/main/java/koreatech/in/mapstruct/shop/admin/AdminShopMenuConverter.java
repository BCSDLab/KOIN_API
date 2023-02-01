package koreatech.in.mapstruct.shop.admin;

import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.domain.Shop.ShopMenu;
import koreatech.in.dto.shop.admin.request.CreateShopMenuRequest;
import koreatech.in.dto.shop.admin.response.AllMenusOfShopResponse;
import koreatech.in.dto.shop.admin.response.MenuResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminShopMenuConverter {
    AdminShopMenuConverter INSTANCE = Mappers.getMapper(AdminShopMenuConverter.class);

    @Mapping(target = "is_hidden", constant = "false")
    ShopMenu toShopMenu(CreateShopMenuRequest request);

    MenuResponse toMenuResponse(ShopMenuProfile shopMenuProfile);

    AllMenusOfShopResponse.Menu toAllMenusOfShopResponse$Menu(ShopMenuProfile shopMenuProfile);
}
