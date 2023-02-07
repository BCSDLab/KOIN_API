package koreatech.in.service;

import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.Shop.ShopMenu;
import koreatech.in.domain.Shop.ShopMenuCategory;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.repository.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static koreatech.in.exception.ExceptionInformation.*;

@Service
@Transactional
public class OwnerShopServiceImpl implements OwnerShopService {
    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public void createMenuCategory(Integer shopId, CreateMenuCategoryRequest request) {
        checkAuthorityAboutShop(getShopById(shopId));

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

    @Override
    @Transactional(readOnly = true)
    public AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId) {
        checkAuthorityAboutShop(getShopById(shopId));

        List<ShopMenuCategory> allMenuCategoriesOfShop = shopMapper.getMenuCategoriesByShopId(shopId);
        return AllMenuCategoriesOfShopResponse.from(allMenuCategoriesOfShop);
    }

    @Override
    public void deleteMenuCategory(Integer shopId, Integer menuCategoryId) {
        checkAuthorityAboutShop(getShopById(shopId));

        checkMenuCategoryExistByIdAndShopId(menuCategoryId, shopId);

        // 카테고리를 사용하고 있는 메뉴가 1개라도 있으면 삭제 불가
        List<ShopMenu> menusUsingCategory = shopMapper.getMenusUsingCategoryByMenuCategoryId(menuCategoryId);
        if (!menusUsingCategory.isEmpty()) {
            throw new BaseException(SHOP_MENU_USING_CATEGORY_EXIST);
        }

        shopMapper.deleteMenuCategoryById(menuCategoryId);
    }

    private void checkMenuCategoryExistByIdAndShopId(Integer menuCategoryId, Integer shopId) {
        ShopMenuCategory menuCategory = Optional.ofNullable(shopMapper.getMenuCategoryById(menuCategoryId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND));

        if (!menuCategory.hasSameShopId(shopId)) {
            throw new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND);
        }
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
