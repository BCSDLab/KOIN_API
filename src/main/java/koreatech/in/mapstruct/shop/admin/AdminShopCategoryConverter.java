package koreatech.in.mapstruct.shop.admin;

import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.dto.shop.admin.request.CreateShopCategoryRequest;
import koreatech.in.dto.shop.admin.response.ShopCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminShopCategoryConverter {
    AdminShopCategoryConverter INSTANCE = Mappers.getMapper(AdminShopCategoryConverter.class);

    ShopCategory toShopCategory(CreateShopCategoryRequest request);

    ShopCategoryResponse toShopCategoryResponse(ShopCategory shopCategory);
}
