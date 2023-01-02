package koreatech.in.mapstruct.shop.normal;

import koreatech.in.domain.Shop.RelatedToShopMenu;
import koreatech.in.dto.shop.normal.response.AllMenusOfShopResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopMenuMapper {
    ShopMenuMapper INSTANCE = Mappers.getMapper(ShopMenuMapper.class);

    AllMenusOfShopResponse.Menu toAllMenusOfShopResponse$Menu(RelatedToShopMenu relatedToShopMenu);
}
