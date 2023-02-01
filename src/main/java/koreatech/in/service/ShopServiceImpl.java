package koreatech.in.service;

import koreatech.in.domain.Shop.*;
import koreatech.in.dto.shop.normal.response.AllMenusOfShopResponse;
import koreatech.in.dto.shop.normal.response.AllShopCategoriesResponse;
import koreatech.in.dto.shop.normal.response.AllShopsResponse;
import koreatech.in.dto.shop.normal.response.ShopResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.mapstruct.shop.normal.ShopConverter;
import koreatech.in.repository.ShopMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static koreatech.in.exception.ExceptionInformation.*;

@Service("shopService")
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
        List<ShopMenuProfile> menuProfiles = shopMapper.getMenuProfilesByShopId(shopId);

        menuProfiles.forEach(ShopMenuProfile::decideWhetherSingleOrNot);

        return AllMenusOfShopResponse.from(menuProfiles);
    }
}
