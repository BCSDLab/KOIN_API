package koreatech.in.service;

import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.domain.Shop.ShopMenuCategory;
import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.dto.normal.shop.response.*;
import koreatech.in.exception.BaseException;
import koreatech.in.mapstruct.normal.shop.ShopConverter;
import koreatech.in.mapstruct.normal.shop.ShopMenuConverter;
import koreatech.in.repository.ShopMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static koreatech.in.exception.ExceptionInformation.SHOP_MENU_NOT_FOUND;
import static koreatech.in.exception.ExceptionInformation.SHOP_NOT_FOUND;

@Service("shopService")
@Transactional
public class ShopServiceImpl implements ShopService {
    @Resource(name = "shopMapper")
    private ShopMapper shopMapper;

    @Override
    @Transactional(readOnly = true)
    public AllShopCategoriesResponse getAllShopCategories() {
        List<ShopCategory> categories = shopMapper.getAllShopCategories();

        return AllShopCategoriesResponse.from(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShop(Integer shopId) {
        ShopProfile shopProfile = getShopProfileByShopId(shopId);
        return ShopConverter.INSTANCE.toShopResponse(shopProfile);
    }

    private ShopProfile getShopProfileByShopId(Integer shopId) {
        return Optional.ofNullable(shopMapper.getShopProfileByShopId(shopId))
                .orElseThrow(() -> new BaseException(SHOP_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopsResponse getAllShops() {
        List<ShopProfile> allShopProfiles = shopMapper.getAllShopProfiles();
        return AllShopsResponse.from(allShopProfiles);
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenusOfShopResponse getAllMenusOfShop(Integer shopId) {
        checkShopExistById(shopId);

        List<ShopMenuProfile> menuProfiles = shopMapper.getMenuProfilesByShopId(shopId);
        menuProfiles.forEach(ShopMenuProfile::decideWhetherSingleOrNot);

        List<ShopMenuProfile> unhiddenMenuProfiles = getUnhiddenMenuProfilesBy(menuProfiles);
        List<ShopCategory> categoryNames = shopMapper.getMenuCategoryNamesByShopId(shopId);

        return AllMenusOfShopResponse.of(unhiddenMenuProfiles, categoryNames);
    }

    private List<ShopMenuProfile> getUnhiddenMenuProfilesBy(List<ShopMenuProfile> menuProfiles) {
        return menuProfiles.stream()
                .filter(menuProfile -> !menuProfile.isHidden())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenu(Integer shopId, Integer menuId) {
        checkShopExistById(shopId);

        ShopMenuProfile menuProfile = getMenuProfileByMenuIdAndShopId(menuId, shopId);
        if (menuProfile.isHidden()) {
            throw new BaseException(SHOP_MENU_NOT_FOUND);
        }

        menuProfile.decideWhetherSingleOrNot();
        return ShopMenuConverter.INSTANCE.toMenuResponse(menuProfile);
    }

    private ShopMenuProfile getMenuProfileByMenuIdAndShopId(Integer menuId, Integer shopId) {
        ShopMenuProfile menuProfile = Optional.ofNullable(shopMapper.getMenuProfileByMenuId(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));

        if (!menuProfile.hasSameShopId(shopId) ) {
            throw new BaseException(SHOP_MENU_NOT_FOUND);
        }

        return menuProfile;
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId) {
        checkShopExistById(shopId);

        List<ShopMenuCategory> allMenuCategoriesOfShop = shopMapper.getMenuCategoriesByShopId(shopId);
        return AllMenuCategoriesOfShopResponse.from(allMenuCategoriesOfShop);
    }

    private void checkShopExistById(Integer id) {
        Optional.ofNullable(shopMapper.getShopById(id))
                .orElseThrow(() -> new BaseException(SHOP_NOT_FOUND));
    }
}
