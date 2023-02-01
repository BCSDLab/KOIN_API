package koreatech.in.mapstruct.shop.normal;

import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.dto.shop.normal.response.AllMenusOfShopResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopMenuConverter {
    ShopMenuConverter INSTANCE = Mappers.getMapper(ShopMenuConverter.class);

    AllMenusOfShopResponse.Menu toAllMenusOfShopResponse$Menu(ShopMenuProfile shopMenuProfile);
}
