package koreatech.in.mapstruct.shop.normal;

import koreatech.in.domain.Shop.RelatedToShop;
import koreatech.in.dto.shop.normal.response.AllShopsResponse;
import koreatech.in.dto.shop.normal.response.ShopResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopMapper {
    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    ShopResponse toShopResponse(RelatedToShop relatedToShop);

    @Mapping(target = "category_ids", expression = "java(relatedToShop.getShopCategoryIds())")
    AllShopsResponse.Shop toAllShopsResponse$Shop(RelatedToShop relatedToShop);
}
