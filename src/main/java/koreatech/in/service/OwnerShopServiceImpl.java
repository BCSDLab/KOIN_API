package koreatech.in.service;

import koreatech.in.domain.Shop.*;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.CreateMenuRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.MenuResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.mapstruct.normal.shop.ShopMenuConverter;
import koreatech.in.repository.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void createMenu(Integer shopId, CreateMenuRequest request) {
        checkAuthorityAboutShop(getShopById(shopId));

        /*
             INSERT 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_category_map
             - shop_menu_images
         */


        // ======= shop_menus 테이블 =======
        ShopMenu menu = ShopMenuConverter.INSTANCE.toShopMenu(request, shopId);
        shopMapper.createMenu(menu);


        // ======= shop_menu_details 테이블 =======
        // single menu: 옵션이 없는 메뉴
        if (request.isSingleMenu()) {
            ShopMenuDetail menuDetail = ShopMenuDetail.singleOf(menu.getId(), request.getSingle_price());
            shopMapper.createMenuDetail(menuDetail);
        } else {
            List<ShopMenuDetail> menuDetails = generateMenuDetailsAndGet(menu.getId(), request.getOption_prices());
            shopMapper.createMenuDetails(menuDetails);
        }


        // ======= shop_menu_category_map 테이블 =======
        checkMenuCategoriesExistInDatabase(shopId, request.getCategory_ids());

        List<ShopMenuCategoryMap> menuCategoryMaps = generateMenuCategoryMapsAndGet(menu.getId(), request.getCategory_ids());
        shopMapper.createMenuCategoryMaps(menuCategoryMaps);


        // ======= shop_menu_images 테이블 =======
        if (request.isImageUrlsExist()) {
            List<ShopMenuImage> menuImages = generateMenuImagesAndGet(menu.getId(), request.getImage_urls());
            shopMapper.createMenuImages(menuImages);
        }
    }

    private List<ShopMenuDetail> generateMenuDetailsAndGet(Integer menuId, List<CreateMenuRequest.OptionPrice> optionPrices) {
        return optionPrices.stream()
                .map(optionPrice -> ShopMenuDetail.of(menuId, optionPrice.getOption(), optionPrice.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenu(Integer shopId, Integer menuId) {
        checkAuthorityAboutShop(getShopById(shopId));

        ShopMenuProfile menuProfile = getMenuProfileByMenuIdAndShopId(menuId, shopId);
        menuProfile.decideWhetherSingleOrNot();

        return ShopMenuConverter.INSTANCE.toMenuResponse(menuProfile);
    }

    private ShopMenuProfile getMenuProfileByMenuIdAndShopId(Integer menuId, Integer shopId) {
        ShopMenuProfile menuProfile = Optional.ofNullable(shopMapper.getMenuProfileByMenuId(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));

        if (!menuProfile.hasSameShopId(shopId)) {
            throw new BaseException(SHOP_MENU_NOT_FOUND);
        }

        return menuProfile;
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenusOfShopResponse getAllMenusOfShop(Integer shopId) {
        checkAuthorityAboutShop(getShopById(shopId));

        List<ShopMenuProfile> menuProfiles = shopMapper.getMenuProfilesByShopId(shopId);
        menuProfiles.forEach(ShopMenuProfile::decideWhetherSingleOrNot);

        return AllMenusOfShopResponse.from(menuProfiles);
    }

    @Override
    public void updateMenu(Integer shopId, Integer menuId, UpdateMenuRequest request) {
        checkAuthorityAboutShop(getShopById(shopId));

        /*
             UPDATE 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_category_map
             - shop_menu_images
         */

        ShopMenu existingMenu = getMenuByIdAndShopId(menuId, shopId);

        // ======= shop_menus 테이블 =======
        if (existingMenu.needToUpdate(request)) {
            existingMenu.update(request);
            shopMapper.updateMenu(existingMenu);
        }


        // ======= shop_menu_details 테이블 =======
        List<ShopMenuDetail> existingMenuDetails = shopMapper.getMenuDetailsByMenuId(menuId);

        if (request.isSingleMenu()) {
            ShopMenuDetail requestedMenuDetail = ShopMenuDetail.singleOf(menuId, request.getSingle_price());

            if (existingMenuDetails.contains(requestedMenuDetail)) {
                existingMenuDetails.remove(requestedMenuDetail);
                if (!existingMenuDetails.isEmpty()) {
                    shopMapper.deleteMenuDetails(existingMenuDetails);
                }
            } else {
                shopMapper.deleteMenuDetailsByMenuId(menuId);
                shopMapper.createMenuDetail(requestedMenuDetail);
            }
        }
        // 단일 메뉴가 아닐때
        else {
            List<ShopMenuDetail> requestedMenuDetails = generateMenuDetailsAndGetForUpdate(existingMenu.getId(), request.getOption_prices());
            shopMapper.createMenuDetails(requestedMenuDetails); // 중복되는 (shop_menu_id, option, price)는 IGNORE에 의해 INSERT가 무시됨

            List<ShopMenuDetail> toBeDeletedMenuDetails = getToBeDeletedMenuDetails(existingMenuDetails, requestedMenuDetails);
            if (!toBeDeletedMenuDetails.isEmpty()) {
                shopMapper.deleteMenuDetails(toBeDeletedMenuDetails);
            }
        }


        // ======= shop_menu_category_map 테이블 =======
        checkMenuCategoriesExistInDatabase(shopId, request.getCategory_ids());

        List<ShopMenuCategoryMap> existingMenuCategoryMaps = shopMapper.getMenuCategoryMapsByMenuId(existingMenu.getId());

        List<ShopMenuCategoryMap> requestedMenuCategoryMaps = generateMenuCategoryMapsAndGet(existingMenu.getId(), request.getCategory_ids());
        shopMapper.createMenuCategoryMaps(requestedMenuCategoryMaps); // 중복되는 (shop_menu_id, shop_menu_category_id)는 IGNORE에 의해 INSERT가 무시됨

        List<ShopMenuCategoryMap> toBeDeletedMenuCategoryMaps = getToBeDeletedMenuCategoryMaps(existingMenuCategoryMaps, requestedMenuCategoryMaps);
        if (!toBeDeletedMenuCategoryMaps.isEmpty()) {
            shopMapper.deleteMenuCategoryMaps(toBeDeletedMenuCategoryMaps);
        }


        // ======= shop_menu_images 테이블 =======
        List<ShopMenuImage> existingMenuImages = shopMapper.getMenuImagesByMenuId(menuId);

        List<ShopMenuImage> requestedMenuImages = generateMenuImagesAndGet(existingMenu.getId(), request.getImage_urls());
        if (!requestedMenuImages.isEmpty()) {
            shopMapper.createMenuImages(requestedMenuImages); // 중복되는 (shop_menu_id, image_url)은 IGNORE에 의해 INSERT가 무시됨
        }

        List<ShopMenuImage> toBeDeletedMenuImages = getToBeDeletedMenuImages(existingMenuImages, requestedMenuImages);
        if (!toBeDeletedMenuImages.isEmpty()) {
            shopMapper.deleteMenuImages(toBeDeletedMenuImages);
        }
    }

    private List<ShopMenuDetail> generateMenuDetailsAndGetForUpdate(Integer menuId, List<UpdateMenuRequest.OptionPrice> optionPrices) {
        return optionPrices.stream()
                .map(optionPrice -> ShopMenuDetail.of(menuId, optionPrice.getOption(), optionPrice.getPrice()))
                .collect(Collectors.toList());
    }

    private List<ShopMenuDetail> getToBeDeletedMenuDetails(List<ShopMenuDetail> existingMenuDetails, List<ShopMenuDetail> requestedMenuDetails) {
        existingMenuDetails.removeAll(requestedMenuDetails);
        return existingMenuDetails;
    }

    private List<ShopMenuCategoryMap> getToBeDeletedMenuCategoryMaps(List<ShopMenuCategoryMap> existingMenuCategoryMaps, List<ShopMenuCategoryMap> requestedMenuCategoryMaps) {
        existingMenuCategoryMaps.removeAll(requestedMenuCategoryMaps);
        return existingMenuCategoryMaps;
    }

    private List<ShopMenuImage> getToBeDeletedMenuImages(List<ShopMenuImage> existingMenuImages, List<ShopMenuImage> requestedMenuImages) {
        existingMenuImages.removeAll(requestedMenuImages);
        return existingMenuImages;
    }

    @Override
    public void deleteMenu(Integer shopId, Integer menuId) {
        checkAuthorityAboutShop(getShopById(shopId));
        getMenuByIdAndShopId(menuId, shopId); // 메뉴 존재 여부 체크

        shopMapper.deleteMenuById(menuId);
    }

    private void checkAuthorityAboutShop(Shop shop) {
        Owner owner = (Owner) jwtValidator.validate();
        if (!shop.hasSameOwnerId(owner.getId())) {
            throw new BaseException(FORBIDDEN);
        }
    }

    private Shop getShopById(Integer id) {
        return Optional.ofNullable(shopMapper.getShopById(id))
                .orElseThrow(() -> new BaseException(SHOP_NOT_FOUND));
    }

    private ShopMenu getMenuByIdAndShopId(Integer menuId, Integer shopId) {
        ShopMenu menu = Optional.ofNullable(shopMapper.getMenuById(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));

        if (!menu.hasSameShopId(shopId)) {
            throw new BaseException(SHOP_MENU_NOT_FOUND);
        }

        return menu;
    }

    private void checkMenuCategoriesExistInDatabase(Integer shopId, List<Integer> menuCategoryIds) {
        menuCategoryIds.forEach(menuCategoryId -> {
            ShopMenuCategory menuCategory = Optional.ofNullable(shopMapper.getMenuCategoryById(menuCategoryId))
                    .orElseThrow(() -> new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND));

            if (!menuCategory.hasSameShopId(shopId)) {
                throw new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND);
            }
        });
    }

    private List<ShopMenuCategoryMap> generateMenuCategoryMapsAndGet(Integer menuId, List<Integer> menuCategoryIds) {
        return menuCategoryIds.stream()
                .map(menuCategoryId -> ShopMenuCategoryMap.of(menuId, menuCategoryId))
                .collect(Collectors.toList());
    }

    private List<ShopMenuImage> generateMenuImagesAndGet(Integer menuId, List<String> imageUrls) {
        return imageUrls.stream()
                .map(imageUrl -> ShopMenuImage.of(menuId, imageUrl))
                .collect(Collectors.toList());
    }
}