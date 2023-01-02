package koreatech.in.mapstruct.shop.admin;

import koreatech.in.domain.Shop.RelatedToShop;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.dto.shop.admin.request.CreateShopRequest;
import koreatech.in.dto.shop.admin.response.ShopResponse;
import koreatech.in.dto.shop.admin.response.ShopsResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminShopMapper {
    AdminShopMapper INSTANCE = Mappers.getMapper(AdminShopMapper.class);

    @Mappings({
            @Mapping(source = "name", target = "internal_name", qualifiedByName = "convertToInternalName"),
            @Mapping(source = "name", target = "chosung", qualifiedByName = "convertToChosung")
    })
    Shop toShop(CreateShopRequest request);

    @Named("convertToInternalName")
    default String convertToInternalName(String name) {
        return name.replace(" ", "").toLowerCase();
    }

    @Named("convertToChosung")
    default String convertToChosung(String name) {
        return name.replace(" ", "").toLowerCase().substring(0, 1);
    }

    ShopResponse toShopResponse(RelatedToShop relatedToShop);

    @Mapping(target = "category_names", expression = "java(relatedToShops.getShopCategoryNames())")
    ShopsResponse.Shop toShopsResponse$Shop(RelatedToShop relatedToShops);
}
