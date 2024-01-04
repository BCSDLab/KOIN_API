package koreatech.in.mapstruct.normal.shop;

import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.dto.normal.shop.request.CreateShopRequest;
import koreatech.in.dto.normal.shop.response.AllShopsResponse;
import koreatech.in.dto.normal.shop.response.ShopResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopConverter {

    ShopConverter INSTANCE = Mappers.getMapper(ShopConverter.class);

    @Mapping(target = "description", defaultValue = "-")
    ShopResponse toShopResponse(ShopProfile shopProfile);

    @Mapping(target = "category_ids", expression = "java(shopProfile.getShopCategoryIds())")
    AllShopsResponse.Shop toAllShopsResponse$Shop(ShopProfile shopProfile);

    @Mapping(source = "ownerId", target = "owner_id")
    Shop toShop(CreateShopRequest request, Integer ownerId);
}
