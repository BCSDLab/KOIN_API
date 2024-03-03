package koreatech.in.mapstruct.admin.shop;

import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.dto.admin.shop.request.CreateShopCategoryRequest;
import koreatech.in.dto.admin.shop.response.ShopCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminShopCategoryConverter {
    AdminShopCategoryConverter INSTANCE = Mappers.getMapper(AdminShopCategoryConverter.class);

    ShopCategory toShopCategory(CreateShopCategoryRequest request);

    ShopCategoryResponse toShopCategoryResponse(ShopCategory shopCategory);
}
