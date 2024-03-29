package koreatech.in.service;

import static koreatech.in.exception.ExceptionInformation.FORBIDDEN;
import static koreatech.in.exception.ExceptionInformation.SHOP_CATEGORY_NOT_FOUND;
import static koreatech.in.exception.ExceptionInformation.SHOP_MENU_CATEGORY_MAXIMUM_EXCEED;
import static koreatech.in.exception.ExceptionInformation.SHOP_MENU_CATEGORY_NAME_DUPLICATE;
import static koreatech.in.exception.ExceptionInformation.SHOP_MENU_CATEGORY_NOT_FOUND;
import static koreatech.in.exception.ExceptionInformation.SHOP_MENU_NOT_FOUND;
import static koreatech.in.exception.ExceptionInformation.SHOP_MENU_USING_CATEGORY_EXIST;
import static koreatech.in.exception.ExceptionInformation.SHOP_NOT_FOUND;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.Shop.ShopCategory;
import koreatech.in.domain.Shop.ShopCategoryMap;
import koreatech.in.domain.Shop.ShopImage;
import koreatech.in.domain.Shop.ShopMenu;
import koreatech.in.domain.Shop.ShopMenuCategory;
import koreatech.in.domain.Shop.ShopMenuCategoryMap;
import koreatech.in.domain.Shop.ShopMenuDetail;
import koreatech.in.domain.Shop.ShopMenuImage;
import koreatech.in.domain.Shop.ShopMenuProfile;
import koreatech.in.domain.Shop.ShopOpen;
import koreatech.in.domain.Shop.ShopProfile;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.CreateMenuRequest;
import koreatech.in.dto.normal.shop.request.CreateShopRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuRequest;
import koreatech.in.dto.normal.shop.request.UpdateShopRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllShopsOfOwnerResponse;
import koreatech.in.dto.normal.shop.response.MenuResponse;
import koreatech.in.dto.normal.shop.response.ShopResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.mapstruct.OwnerConverter;
import koreatech.in.mapstruct.normal.shop.ShopConverter;
import koreatech.in.mapstruct.normal.shop.ShopMenuConverter;
import koreatech.in.mapstruct.normal.shop.ShopOpenConverter;
import koreatech.in.repository.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OwnerShopServiceImpl implements OwnerShopService {

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private ShopMapper shopMapper;

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShop(Integer shopId) {
        checkAuthorityAboutShop(getShopById(shopId));

        ShopProfile shopProfile = shopMapper.getShopProfileByShopId(shopId);
        return ShopConverter.INSTANCE.toShopResponse(shopProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopsOfOwnerResponse getAllShopsOfOwner() {
        Owner owner = (Owner) jwtValidator.validate();

        return AllShopsOfOwnerResponse.from(shopMapper.getShopProfilesByOwnerId(owner.getId()));
    }

    @Override
    public void createShop(CreateShopRequest request) {
        Shop shop = createShopsTable(request);

        List<ShopMenuCategory> menuCategories = getMenuCategories(shop);
        shopMapper.createMenuCategories(menuCategories);

        List<ShopOpen> shopOpens = generateShopOpens(request.getOpen(), shop.getId());
        shopMapper.createShopOpens(shopOpens);

        request.addToAllCategory();
        shopCategoriesExist(request.getCategory_ids());
        List<ShopCategoryMap> shopCategoryMaps = generateShopCategoryMaps(shop.getId(), request.getCategory_ids());
        shopMapper.createShopCategoryMaps(shopCategoryMaps);

        List<ShopImage> shopImages = generateShopImages(shop.getId(), request.getImage_urls());
        shopMapper.createShopImages(shopImages);
    }

    private static List<ShopMenuCategory> getMenuCategories(Shop shop) {
        String[] menuCategoryNames = {"이벤트 메뉴", "대표 메뉴", "사이드 메뉴", "세트 메뉴"};
        return Arrays.stream(menuCategoryNames)
            .map(name -> ShopMenuCategory.of(shop.getId(), name))
            .collect(Collectors.toList());
    }

    private Shop createShopsTable(CreateShopRequest request) {
        Owner owner = (Owner) jwtValidator.validate();
        Shop shop = ShopConverter.INSTANCE.toShop(request, owner.getId());
        shop.nameUpdate();
        shopMapper.createShop(shop);

        return shop;
    }

    private List<ShopOpen> generateShopOpens(List<CreateShopRequest.Open> opens, Integer shopId) {
        return opens.stream()
            .map(open -> ShopOpenConverter.INSTANCE.toShopOpenForCreate(open, shopId))
            .collect(Collectors.toList());
    }

    @Override
    public void updateShop(Integer shopId, UpdateShopRequest request) {
        Shop existingShop = getShopById(shopId);
        checkAuthorityAboutShop(existingShop);

        /*
             UPDATE 대상 테이블
             - shops
             - shop_opens
             - shop_category_map
             - shop_images
         */

        // ======= shops 테이블 =======
        if (existingShop.needToUpdate(request)) {
            existingShop.update(request);
            shopMapper.updateShop(existingShop);
        }

        // ======= shop_opens 테이블 =======
        List<ShopOpen> shopOpens = generateShopOpensAndGetForUpdate(request.getOpen(), existingShop.getId());
        shopMapper.updateShopOpens(shopOpens);

        // ======= shop_category_map 테이블 =======
        shopCategoriesExist(request.getCategory_ids());

        List<ShopCategoryMap> existingShopCategoryMaps = shopMapper.getShopCategoryMapsByShopId(existingShop.getId());

        // IGNORE에 의하여 (shop_id, shop_category_id)가 중복일 경우는 insert가 무시된다.
        List<ShopCategoryMap> requestedCategoryMaps = generateShopCategoryMaps(existingShop.getId(),
            request.getCategory_ids());
        shopMapper.createShopCategoryMaps(requestedCategoryMaps);

        // 기존에 있던 관계들에서 요청된 관계들을 제거하면 삭제해야할 관계들을 알아낼 수 있다.
        List<ShopCategoryMap> toBeDeletedShopCategoryMaps = getToBeDeletedShopCategoryMaps(existingShopCategoryMaps,
            requestedCategoryMaps);
        if (!toBeDeletedShopCategoryMaps.isEmpty()) {
            shopMapper.deleteShopCategoryMaps(toBeDeletedShopCategoryMaps);
        }

        // ======= shop_images 테이블 =======
        List<ShopImage> existingShopImages = shopMapper.getShopImagesByShopId(existingShop.getId());

        List<ShopImage> requestedShopImages = generateShopImages(existingShop.getId(), request.getImage_urls());
        if (!requestedShopImages.isEmpty()) {
            shopMapper.createShopImages(requestedShopImages);
        }

        List<ShopImage> toBeDeletedShopImages = getToBeDeletedShopImages(existingShopImages, requestedShopImages);
        if (!toBeDeletedShopImages.isEmpty()) {
            shopMapper.deleteShopImages(toBeDeletedShopImages);
        }
    }

    private List<ShopOpen> generateShopOpensAndGetForUpdate(List<UpdateShopRequest.Open> opens, Integer shopId) {
        return opens.stream()
            .map(open -> ShopOpenConverter.INSTANCE.toShopOpenForUpdate(open, shopId))
            .collect(Collectors.toList());
    }

    private void shopCategoriesExist(List<Integer> shopCategoryIds) {
        shopCategoryIds.forEach(categoryId -> {
            Optional.ofNullable(shopMapper.getShopCategoryById(categoryId))
                .orElseThrow(() -> new BaseException(SHOP_CATEGORY_NOT_FOUND));
        });
    }

    private List<ShopCategoryMap> generateShopCategoryMaps(Integer shopId, List<Integer> shopCategoryIds) {
        return shopCategoryIds.stream()
            .map(shopCategoryId -> ShopCategoryMap.of(shopId, shopCategoryId))
            .collect(Collectors.toList());
    }

    private List<ShopCategoryMap> getToBeDeletedShopCategoryMaps(List<ShopCategoryMap> existingShopCategoryMaps,
                                                                 List<ShopCategoryMap> requestedShopCategoryMaps) {
        existingShopCategoryMaps.removeAll(requestedShopCategoryMaps);
        return existingShopCategoryMaps;
    }

    private List<ShopImage> generateShopImages(Integer shopId, List<String> imageUrls) {
        return imageUrls.stream()
            .map(url -> ShopImage.of(shopId, url))
            .collect(Collectors.toList());
    }

    private List<ShopImage> getToBeDeletedShopImages(List<ShopImage> existingShopImages,
                                                     List<ShopImage> requestedShopImages) {
        existingShopImages.removeAll(requestedShopImages);
        return existingShopImages;
    }

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
    public void updateMenuCategory(Integer menuCategoryId, UpdateMenuCategoryRequest request) {
        ShopMenuCategory menuCategoryById = Optional.ofNullable(shopMapper.getMenuCategoryById(menuCategoryId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND));
        ShopMenuCategory menuCategory = OwnerConverter.INSTANCE.toMenuCategory(menuCategoryById.getShop_id(), request);

        checkAuthorityAboutShop(getShopById(menuCategory.getShop_id()));
        validatesExist(menuCategory);

        shopMapper.updateMenuCategory(menuCategory);
    }

    private void validatesExist(ShopMenuCategory menuCategory) {
        validatesExist(menuCategory.getShop_id());
        checkMenuCategoryExistByIdAndShopId(menuCategory.getId(), menuCategory.getShop_id());
    }

    private void validatesExist(Integer shopId) {
        getShopById(shopId);
    }

    @Override
    public void deleteMenuCategory(Integer menuCategoryId) {
        ShopMenuCategory menuCategoryById = Optional.ofNullable(shopMapper.getMenuCategoryById(menuCategoryId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND));

        checkAuthorityAboutShop(getShopById(menuCategoryById.getShop_id()));

        checkMenuCategoryExistByIdAndShopId(menuCategoryId, menuCategoryById.getShop_id());

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

        List<ShopMenuCategoryMap> menuCategoryMaps = generateMenuCategoryMapsAndGet(menu.getId(),
            request.getCategory_ids());
        shopMapper.createMenuCategoryMaps(menuCategoryMaps);

        // ======= shop_menu_images 테이블 =======
        if (request.isImageUrlsExist()) {
            List<ShopMenuImage> menuImages = generateMenuImagesAndGet(menu.getId(), request.getImage_urls());
            shopMapper.createMenuImages(menuImages);
        }
    }

    private List<ShopMenuDetail> generateMenuDetailsAndGet(Integer menuId,
                                                           List<CreateMenuRequest.OptionPrice> optionPrices) {
        return optionPrices.stream()
            .map(optionPrice -> ShopMenuDetail.of(menuId, optionPrice.getOption(), optionPrice.getPrice()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenu(Integer menuId) {
        ShopMenu existingMenu = Optional.ofNullable(shopMapper.getMenuById(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));
        checkAuthorityAboutShop(getShopById(existingMenu.getShop_id()));

        ShopMenuProfile menuProfile = Optional.ofNullable(shopMapper.getMenuProfileByMenuId(menuId))
            .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));

        menuProfile.decideWhetherSingleOrNot();

        return ShopMenuConverter.INSTANCE.toMenuResponse(menuProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenusOfShopResponse getAllMenusOfShop(Integer shopId) {
        checkAuthorityAboutShop(getShopById(shopId));

        List<ShopMenuProfile> menuProfiles = shopMapper.getMenuProfilesByShopId(shopId);
        menuProfiles.forEach(ShopMenuProfile::decideWhetherSingleOrNot);
        List<ShopCategory> categoryNames = shopMapper.getMenuCategoryNamesByShopId(shopId);

        return AllMenusOfShopResponse.of(menuProfiles, categoryNames);
    }

    @Override
    public void updateMenu(Integer menuId, UpdateMenuRequest request) {
        ShopMenu existingMenu = Optional.ofNullable(shopMapper.getMenuById(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));
        checkAuthorityAboutShop(getShopById(existingMenu.getShop_id()));

        /*
             UPDATE 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_category_map
             - shop_menu_images
         */


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
            List<ShopMenuDetail> requestedMenuDetails = generateMenuDetailsAndGetForUpdate(existingMenu.getId(),
                request.getOption_prices());
            shopMapper.createMenuDetails(
                requestedMenuDetails); // 중복되는 (shop_menu_id, option, price)는 IGNORE에 의해 INSERT가 무시됨

            List<ShopMenuDetail> toBeDeletedMenuDetails = getToBeDeletedMenuDetails(existingMenuDetails,
                requestedMenuDetails);
            if (!toBeDeletedMenuDetails.isEmpty()) {
                shopMapper.deleteMenuDetails(toBeDeletedMenuDetails);
            }
        }

        // ======= shop_menu_category_map 테이블 =======
        checkMenuCategoriesExistInDatabase(existingMenu.getShop_id(), request.getCategory_ids());

        List<ShopMenuCategoryMap> existingMenuCategoryMaps = shopMapper.getMenuCategoryMapsByMenuId(
            existingMenu.getId());

        List<ShopMenuCategoryMap> requestedMenuCategoryMaps = generateMenuCategoryMapsAndGet(existingMenu.getId(),
            request.getCategory_ids());
        shopMapper.createMenuCategoryMaps(
            requestedMenuCategoryMaps); // 중복되는 (shop_menu_id, shop_menu_category_id)는 IGNORE에 의해 INSERT가 무시됨

        List<ShopMenuCategoryMap> toBeDeletedMenuCategoryMaps = getToBeDeletedMenuCategoryMaps(existingMenuCategoryMaps,
            requestedMenuCategoryMaps);
        if (!toBeDeletedMenuCategoryMaps.isEmpty()) {
            shopMapper.deleteMenuCategoryMaps(toBeDeletedMenuCategoryMaps);
        }

        // ======= shop_menu_images 테이블 =======
        List<ShopMenuImage> existingMenuImages = shopMapper.getMenuImagesByMenuId(menuId);

        List<ShopMenuImage> requestedMenuImages = generateMenuImagesAndGet(existingMenu.getId(),
            request.getImage_urls());
        if (!requestedMenuImages.isEmpty()) {
            shopMapper.createMenuImages(requestedMenuImages); // 중복되는 (shop_menu_id, image_url)은 IGNORE에 의해 INSERT가 무시됨
        }

        List<ShopMenuImage> toBeDeletedMenuImages = getToBeDeletedMenuImages(existingMenuImages, requestedMenuImages);
        if (!toBeDeletedMenuImages.isEmpty()) {
            shopMapper.deleteMenuImages(toBeDeletedMenuImages);
        }
    }

    private List<ShopMenuDetail> generateMenuDetailsAndGetForUpdate(Integer menuId,
                                                                    List<UpdateMenuRequest.OptionPrice> optionPrices) {
        return optionPrices.stream()
            .map(optionPrice -> ShopMenuDetail.of(menuId, optionPrice.getOption(), optionPrice.getPrice()))
            .collect(Collectors.toList());
    }

    private List<ShopMenuDetail> getToBeDeletedMenuDetails(List<ShopMenuDetail> existingMenuDetails,
                                                           List<ShopMenuDetail> requestedMenuDetails) {
        existingMenuDetails.removeAll(requestedMenuDetails);
        return existingMenuDetails;
    }

    private List<ShopMenuCategoryMap> getToBeDeletedMenuCategoryMaps(List<ShopMenuCategoryMap> existingMenuCategoryMaps,
                                                                     List<ShopMenuCategoryMap> requestedMenuCategoryMaps) {
        existingMenuCategoryMaps.removeAll(requestedMenuCategoryMaps);
        return existingMenuCategoryMaps;
    }

    private List<ShopMenuImage> getToBeDeletedMenuImages(List<ShopMenuImage> existingMenuImages,
                                                         List<ShopMenuImage> requestedMenuImages) {
        existingMenuImages.removeAll(requestedMenuImages);
        return existingMenuImages;
    }

    @Override
    public void deleteMenu(Integer menuId) {
        ShopMenu existingMenu = Optional.ofNullable(shopMapper.getMenuById(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));
        checkAuthorityAboutShop(getShopById(existingMenu.getShop_id()));

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
