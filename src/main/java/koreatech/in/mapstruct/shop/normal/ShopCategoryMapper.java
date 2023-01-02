package koreatech.in.mapstruct.shop.normal;

import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.dto.shop.normal.response.AllShopCategoriesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopCategoryMapper {
    ShopCategoryMapper INSTANCE = Mappers.getMapper(ShopCategoryMapper.class);

    AllShopCategoriesResponse.Category toAllShopCategoriesResponse$Category(ShopCategory shopCategory);
}
