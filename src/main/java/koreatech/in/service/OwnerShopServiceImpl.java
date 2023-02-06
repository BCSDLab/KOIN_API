package koreatech.in.service;

import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.Shop.ShopMenuCategory;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.exception.BaseException;
import koreatech.in.repository.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static koreatech.in.exception.ExceptionInformation.*;

@Service
public class OwnerShopServiceImpl implements OwnerShopService {
    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public void createMenuCategory(Integer shopId, CreateMenuCategoryRequest request) {
        Shop shop = getShopById(shopId);
        
        checkAuthorityAboutShop(shop);

        checkMenuCategoryNameDuplicationAtSameShop(shopId, request.getName());
        checkExceedsMaximumCountOfMenuCategoriesAtShop(shopId);

        ShopMenuCategory menuCategory = ShopMenuCategory.of(shopId, request.getName());
        shopMapper.createMenuCategory(menuCategory);
    }

    private void checkExceedsMaximumCountOfMenuCategoriesAtShop(Integer shopId) {
        Integer existingCount = shopMapper.getCountOfMenuCategoriesByShopId(shopId);
        if (existingCount.equals(20)) {
            throw new BaseException(SHOP_MENU_CATEGORY_MAXIMUM_EXCEED);
        }
    }

    private void checkMenuCategoryNameDuplicationAtSameShop(Integer shopId, String name) {
        Optional.ofNullable(shopMapper.getMenuCategoryByShopIdAndName(shopId, name))
                .ifPresent(menuCategory -> {
                    throw new BaseException(SHOP_MENU_CATEGORY_NAME_DUPLICATE);
                });
    }












    private Shop getShopById(Integer id) {
        return Optional.ofNullable(shopMapper.getShopById(id))
                .orElseThrow(() -> new BaseException(SHOP_NOT_FOUND));
    }

    private void checkAuthorityAboutShop(Shop shop) {
        Owner owner = (Owner) jwtValidator.validate();
        if (!shop.hasSameOwnerId(owner.getId())) {
            throw new BaseException(FORBIDDEN);
        }
    }
}
