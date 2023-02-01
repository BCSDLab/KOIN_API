package koreatech.in.mapstruct.shop.normal;

import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.dto.shop.normal.response.AllShopsResponse;
import koreatech.in.dto.shop.normal.response.ShopResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopConverter {
    ShopConverter INSTANCE = Mappers.getMapper(ShopConverter.class);

    ShopResponse toShopResponse(ShopProfile shopProfile);

    @Mapping(target = "category_ids", expression = "java(shopProfile.getShopCategoryIds())")
    AllShopsResponse.Shop toAllShopsResponse$Shop(ShopProfile shopProfile);
}
