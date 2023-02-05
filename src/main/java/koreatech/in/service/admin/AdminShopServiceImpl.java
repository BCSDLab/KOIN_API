package koreatech.in.service.admin;

import koreatech.in.domain.Shop.*;
import koreatech.in.dto.admin.shop.request.*;
import koreatech.in.dto.admin.shop.response.*;
import koreatech.in.exception.*;
import koreatech.in.mapstruct.admin.shop.AdminShopCategoryConverter;
import koreatech.in.mapstruct.admin.shop.AdminShopConverter;
import koreatech.in.mapstruct.admin.shop.AdminShopMenuConverter;
import koreatech.in.mapstruct.admin.shop.AdminShopOpenConverter;
import koreatech.in.repository.admin.AdminShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static koreatech.in.exception.ExceptionInformation.*;

@Service
@Transactional
public class AdminShopServiceImpl implements AdminShopService {
    @Autowired
    private AdminShopMapper adminShopMapper;

    @Override
    public void createShopCategory(CreateShopCategoryRequest request) {
        ShopCategory sameNameCategory = adminShopMapper.getShopCategoryByName(request.getName());
        if (sameNameCategory != null) {
            throw new BaseException(SHOP_CATEGORY_NAME_DUPLICATE);
        }

        ShopCategory category = AdminShopCategoryConverter.INSTANCE.toShopCategory(request);
        adminShopMapper.createShopCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopCategoryResponse getShopCategory(Integer shopCategoryId) {
        ShopCategory shopCategory = getShopCategoryById(shopCategoryId);
        return AdminShopCategoryConverter.INSTANCE.toShopCategoryResponse(shopCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopCategoriesResponse getShopCategories(ShopCategoriesCondition condition) {
        Integer totalCount = adminShopMapper.getTotalCountOfShopCategoriesByCondition(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new BaseException(PAGE_NOT_FOUND);
        }

        List<ShopCategory> categories = adminShopMapper.getShopCategoriesByCondition(condition.getCursor(), condition);

        return ShopCategoriesResponse.of(totalCount, totalPage, currentPage, categories);
    }

    @Override
    public void updateShopCategory(Integer shopCategoryId, UpdateShopCategoryRequest request) {
        ShopCategory existingCategory = getShopCategoryById(shopCategoryId);

        checkShopCategoryNameDuplicationByExcludingSelf(request.getName(), existingCategory.getId());

        if (existingCategory.needToUpdate(request)) {
            existingCategory.update(request);
            adminShopMapper.updateShopCategory(existingCategory);
        }
    }

    private void checkShopCategoryNameDuplicationByExcludingSelf(String name, Integer selfId) {
        ShopCategory sameNameCategory = adminShopMapper.getShopCategoryByName(name);

        if (sameNameCategory != null && !sameNameCategory.hasSameId(selfId)) {
            throw new BaseException(SHOP_CATEGORY_NAME_DUPLICATE);
        }
    }

    @Override
    public void deleteShopCategory(Integer shopCategoryId) {
        getShopCategoryById(shopCategoryId); // 카테고리 존재 여부 체크

        // 카테고리를 사용하고 있는 상점 리스트
        List<Shop> shopsUsingCategory = adminShopMapper.getShopsUsingCategoryByShopCategoryId(shopCategoryId);
        if (!shopsUsingCategory.isEmpty()) {
            throw new BaseException(SHOP_USING_CATEGORY_EXIST);
        }

        adminShopMapper.deleteShopCategoryById(shopCategoryId);
    }

    @Override
    public void createShop(CreateShopRequest request) {
        /*
             INSERT 대상 테이블
             - shops
             - shop_opens
             - shop_category_map
             - shop_menu_categories
             - shop_images
         */

        // ======= shops 테이블 =======
        Shop shop = AdminShopConverter.INSTANCE.toShop(request);
        adminShopMapper.createShop(shop);


        // ======= shop_opens 테이블 =======
        List<ShopOpen> shopOpens = generateShopOpensAndGet(request.getOpen(), shop.getId());
        adminShopMapper.createShopOpens(shopOpens);


        // ======= shop_category_map 테이블 =======
        List<Integer> categoryIds = request.getCategory_ids();

        checkShopCategoriesExistInDatabase(categoryIds);

        List<ShopCategoryMap> shopCategoryMaps = generateShopCategoryMapsAndGet(shop.getId(), request.getCategory_ids());
        adminShopMapper.createShopCategoryMaps(shopCategoryMaps);


        // ======= shop_menu_categories 테이블 =======
        /*
            기본적으로 제공되는 카테고리 메뉴: 이벤트 메뉴, 대표 메뉴, 사이드 메뉴, 세트 메뉴
         */
        String[] basicMenuCategoryNames = getDefaultMenuCategoryNames();

        List<ShopMenuCategory> menuCategories = generateMenuCategoriesAndGet(shop.getId(), basicMenuCategoryNames);
        adminShopMapper.createMenuCategories(menuCategories);


        // ======= shop_images 테이블 =======
        List<String> imageUrls = request.getImage_urls();

        if (!imageUrls.isEmpty()) {
            List<ShopImage> shopImages = generateShopImagesAndGet(shop.getId(), request.getImage_urls());
            adminShopMapper.createShopImages(shopImages);
        }
    }

    private List<ShopOpen> generateShopOpensAndGet(List<CreateShopRequest.Open> opens, Integer shopId) {
        return opens.stream()
                .map(open -> AdminShopOpenConverter.INSTANCE.toShopOpen(open, shopId))
                .collect(Collectors.toList());
    }

    private String[] getDefaultMenuCategoryNames() {
        return new String[]{"이벤트 메뉴", "대표 메뉴", "사이드 메뉴", "세트 메뉴"};
    }

    private List<ShopMenuCategory> generateMenuCategoriesAndGet(Integer shopId, String[] menuCategoryNames) {
        return Arrays.stream(menuCategoryNames)
                .map(name -> ShopMenuCategory.of(shopId, name))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShop(Integer shopId) {
        ShopProfile shopProfile = getShopProfileByShopId(shopId);
        return AdminShopConverter.INSTANCE.toShopResponse(shopProfile);
    }

    private ShopProfile getShopProfileByShopId(Integer shopId) {
        return Optional.ofNullable(adminShopMapper.getShopProfileByShopId(shopId))
                .orElseThrow(() -> new BaseException(SHOP_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public ShopsResponse getShops(ShopsCondition condition) {
        Integer totalCount = adminShopMapper.getTotalCountOfShopsByCondition(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new BaseException(PAGE_NOT_FOUND);
        }

        List<ShopProfile> shops = adminShopMapper.getShopProfilesByCondition(condition.getCursor(), condition);

        return ShopsResponse.of(totalCount, totalPage, currentPage, shops);
    }

    @Override
    public void updateShop(Integer shopId, UpdateShopRequest request) {
        /*
             UPDATE 대상 테이블
             - shops
             - shop_opens
             - shop_category_map
             - shop_images
         */

        Shop existingShop = getShopById(shopId);

        // ======= shops 테이블 =======
        if (existingShop.needToUpdate(request)) {
            existingShop.update(request);
            adminShopMapper.updateShop(existingShop);
        }


        // ======= shop_opens 테이블 =======
        List<ShopOpen> shopOpens = generateShopOpensAndGetForUpdate(request.getOpen(), existingShop.getId());
        adminShopMapper.updateShopOpens(shopOpens);


        // ======= shop_category_map 테이블 =======
        checkShopCategoriesExistInDatabase(request.getCategory_ids());

        List<ShopCategoryMap> existingShopCategoryMaps = adminShopMapper.getShopCategoryMapsByShopId(existingShop.getId());

        // IGNORE에 의하여 (shop_id, shop_category_id)가 중복일 경우는 insert가 무시된다.
        List<ShopCategoryMap> requestedCategoryMaps = generateShopCategoryMapsAndGet(existingShop.getId(), request.getCategory_ids());
        adminShopMapper.createShopCategoryMaps(requestedCategoryMaps);

        // 기존에 있던 관계들에서 요청된 관계들을 제거하면 삭제해야할 관계들을 알아낼 수 있다.
        List<ShopCategoryMap> toBeDeletedShopCategoryMaps = getToBeDeletedShopCategoryMaps(existingShopCategoryMaps, requestedCategoryMaps);
        if (!toBeDeletedShopCategoryMaps.isEmpty()) {
            adminShopMapper.deleteShopCategoryMaps(toBeDeletedShopCategoryMaps);
        }


        // ======= shop_images 테이블 =======
        List<ShopImage> existingShopImages = adminShopMapper.getShopImagesByShopId(existingShop.getId());

        List<ShopImage> requestedShopImages = generateShopImagesAndGet(existingShop.getId(), request.getImage_urls());
        if (!requestedShopImages.isEmpty()) {
            adminShopMapper.createShopImages(requestedShopImages);
        }

        List<ShopImage> toBeDeletedShopImages = getToBeDeletedShopImages(existingShopImages, requestedShopImages);
        if (!toBeDeletedShopImages.isEmpty()) {
            adminShopMapper.deleteShopImages(toBeDeletedShopImages);
        }
    }

    private List<ShopOpen> generateShopOpensAndGetForUpdate(List<UpdateShopRequest.Open> opens, Integer shopId) {
        return opens.stream()
                .map(open -> AdminShopOpenConverter.INSTANCE.toShopOpen(open, shopId))
                .collect(Collectors.toList());
    }

    private List<ShopCategoryMap> getToBeDeletedShopCategoryMaps(List<ShopCategoryMap> existingShopCategoryMaps, List<ShopCategoryMap> requestedShopCategoryMaps) {
        existingShopCategoryMaps.removeAll(requestedShopCategoryMaps);
        return existingShopCategoryMaps;
    }

    private List<ShopImage> getToBeDeletedShopImages(List<ShopImage> existingShopImages, List<ShopImage> requestedShopImages) {
        existingShopImages.removeAll(requestedShopImages);
        return existingShopImages;
    }

    @Override
    public void deleteShop(Integer shopId) {
        Shop shop = getShopById(shopId);

        if (shop.isDeleted()) {
            throw new BaseException(SHOP_ALREADY_DELETED);
        }

        /*
             shop 테이블과 관계가 맺어져 있는 테이블의 레코드도 전부 soft delete 처리하는 것이 아닌,
             shop 테이블의 레코드만 soft delete 한다.
             이유는 어드민페이지에서 상점 삭제 해제 기능을 사용할때 쉽게 복구가 가능해야 하기 때문이다.
         */
        adminShopMapper.deleteShopById(shopId);
    }

    @Override
    public void undeleteOfShop(Integer shopId) {
        Shop shop = getShopById(shopId);

        if (!shop.isDeleted()) {
            throw new BaseException(SHOP_NOT_DELETED);
        }

        adminShopMapper.undeleteShopById(shopId);
    }

    @Override
    public void createMenuCategory(Integer shopId, CreateShopMenuCategoryRequest request) {
        getShopById(shopId); // 상점 존재 여부 체크

        checkMenuCategoryNameDuplicationAtSameShop(shopId, request.getName());
        checkExceedsMaximumCountOfMenuCategoriesAtShop(shopId);

        ShopMenuCategory menuCategory = ShopMenuCategory.of(shopId, request.getName());
        adminShopMapper.createMenuCategory(menuCategory);
    }

    private void checkMenuCategoryNameDuplicationAtSameShop(Integer shopId, String name) {
        Optional.ofNullable(adminShopMapper.getMenuCategoryByShopIdAndName(shopId, name))
                .ifPresent(menuCategory -> {
                    throw new BaseException(SHOP_MENU_CATEGORY_NAME_DUPLICATE);
                });
    }

    private void checkExceedsMaximumCountOfMenuCategoriesAtShop(Integer shopId) {
        Integer existingCount = adminShopMapper.getCountOfMenuCategoriesByShopId(shopId);
        if (existingCount.equals(20)) {
            throw new BaseException(SHOP_MENU_CATEGORY_MAXIMUM_EXCEED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShop(Integer shopId) {
        getShopById(shopId); // 상점 존재 여부 체크

        List<ShopMenuCategory> allMenuCategoriesOfShop = adminShopMapper.getMenuCategoriesByShopId(shopId);
        return AllMenuCategoriesOfShopResponse.from(allMenuCategoriesOfShop);
    }

    @Override
    public void deleteMenuCategory(Integer shopId, Integer menuCategoryId) {
        getShopById(shopId); // 상점 존재 여부 체크
        checkMenuCategoryExistIdAndShopId(menuCategoryId, shopId);

        // 카테고리를 사용하고 있는 메뉴가 1개라도 있으면 삭제 불가
        List<ShopMenu> menusUsingCategory = adminShopMapper.getMenusUsingCategoryByMenuCategoryId(menuCategoryId);
        if (!menusUsingCategory.isEmpty()) {
            throw new BaseException(SHOP_MENU_USING_CATEGORY_EXIST);
        }

        adminShopMapper.deleteMenuCategoryById(menuCategoryId);
    }

    private void checkMenuCategoryExistIdAndShopId(Integer menuCategoryId, Integer shopId) {
        ShopMenuCategory menuCategory = Optional.ofNullable(adminShopMapper.getMenuCategoryById(menuCategoryId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND));

        if (!menuCategory.hasSameShopId(shopId)) {
            throw new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND);
        }
    }

    @Override
    public void createMenu(Integer shopId, CreateShopMenuRequest request) {
        /*
             INSERT 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_category_map
             - shop_menu_images
         */

        getShopById(shopId); // 상점 존재 여부 체크

        // ======= shop_menus 테이블 =======
        ShopMenu menu = AdminShopMenuConverter.INSTANCE.toShopMenu(request, shopId);
        adminShopMapper.createMenu(menu);


        // ======= shop_menu_details 테이블 =======
        // single menu: 옵션이 없는 메뉴
        if (request.isSingleMenu()) {
            ShopMenuDetail menuDetail = ShopMenuDetail.singleOf(menu.getId(), request.getSingle_price());
            adminShopMapper.createMenuDetail(menuDetail);
        } else {
            List<ShopMenuDetail> menuDetails = generateMenuDetailsAndGet(menu.getId(), request.getOption_prices());
            adminShopMapper.createMenuDetails(menuDetails);
        }


        // ======= shop_menu_category_map 테이블 =======
        checkMenuCategoriesExistInDatabase(shopId, request.getCategory_ids());

        List<ShopMenuCategoryMap> menuCategoryMaps = generateMenuCategoryMapsAndGet(menu.getId(), request.getCategory_ids());
        adminShopMapper.createMenuCategoryMaps(menuCategoryMaps);


        // ======= shop_menu_images 테이블 =======
        if (request.isImageUrlsExist()) {
            List<ShopMenuImage> menuImages = generateMenuImagesAndGet(menu.getId(), request.getImage_urls());
            adminShopMapper.createMenuImages(menuImages);
        }
    }

    private List<ShopMenuDetail> generateMenuDetailsAndGet(Integer menuId, List<CreateShopMenuRequest.OptionPrice> optionPrices) {
        return optionPrices.stream()
                .map(optionPrice -> ShopMenuDetail.of(menuId, optionPrice.getOption(), optionPrice.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenu(Integer shopId, Integer menuId) {
        getShopById(shopId); // 상점 존재 여부 체크

        ShopMenuProfile menuProfile = getMenuProfileByMenuIdAndShopId(menuId, shopId);
        menuProfile.decideWhetherSingleOrNot();

        return AdminShopMenuConverter.INSTANCE.toMenuResponse(menuProfile);
    }

    private ShopMenuProfile getMenuProfileByMenuIdAndShopId(Integer menuId, Integer shopId) {
        ShopMenuProfile menuProfile = Optional.ofNullable(adminShopMapper.getMenuProfileByMenuId(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));

        if (!menuProfile.hasSameShopId(shopId)) {
            throw new BaseException(SHOP_MENU_NOT_FOUND);
        }

        return menuProfile;
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenusOfShopResponse getAllMenusOfShop(Integer shopId) {
        getShopById(shopId); // 상점 존재 여부 체크

        List<ShopMenuProfile> menuProfilesOfShop = adminShopMapper.getMenuProfilesByShopId(shopId);
        menuProfilesOfShop.forEach(ShopMenuProfile::decideWhetherSingleOrNot);

        return AllMenusOfShopResponse.from(menuProfilesOfShop);
    }

    @Override
    public void updateMenu(Integer shopId, Integer menuId, UpdateShopMenuRequest request) {
        /*
             UPDATE 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_category_map
             - shop_menu_images
         */

        getShopById(shopId); // 상점 존재 여부 체크

        ShopMenu existingMenu = getMenuByIdAndShopId(menuId, shopId);

        // ======= shop_menus 테이블 =======
        if (existingMenu.needToUpdate(request)) {
            existingMenu.update(request);
            adminShopMapper.updateMenu(existingMenu);
        }


        // ======= shop_menu_details 테이블 =======
        List<ShopMenuDetail> existingMenuDetails = adminShopMapper.getMenuDetailsByMenuId(menuId);

        if (request.isSingleMenu()) {
            ShopMenuDetail requestedMenuDetail = ShopMenuDetail.singleOf(menuId, request.getSingle_price());

            if (existingMenuDetails.contains(requestedMenuDetail)) {
                existingMenuDetails.remove(requestedMenuDetail);
                if (!existingMenuDetails.isEmpty()) {
                    adminShopMapper.deleteMenuDetails(existingMenuDetails);
                }
            } else {
                adminShopMapper.deleteMenuDetailsByMenuId(menuId);
                adminShopMapper.createMenuDetail(requestedMenuDetail);
            }
        }
        // 단일 메뉴가 아닐때
        else {
            List<ShopMenuDetail> requestedMenuDetails = generateMenuDetailsAndGetForUpdate(existingMenu.getId(), request.getOption_prices());
            adminShopMapper.createMenuDetails(requestedMenuDetails); // 중복되는 (shop_menu_id, option, price)는 IGNORE에 의해 INSERT가 무시됨

            List<ShopMenuDetail> toBeDeletedMenuDetails = getToBeDeletedMenuDetails(existingMenuDetails, requestedMenuDetails);
            if (!toBeDeletedMenuDetails.isEmpty()) {
                adminShopMapper.deleteMenuDetails(toBeDeletedMenuDetails);
            }
        }


        // ======= shop_menu_category_map 테이블 =======
        checkMenuCategoriesExistInDatabase(shopId, request.getCategory_ids());

        List<ShopMenuCategoryMap> existingMenuCategoryMaps = adminShopMapper.getMenuCategoryMapsByMenuId(existingMenu.getId());

        List<ShopMenuCategoryMap> requestedMenuCategoryMaps = generateMenuCategoryMapsAndGet(existingMenu.getId(), request.getCategory_ids());
        adminShopMapper.createMenuCategoryMaps(requestedMenuCategoryMaps); // 중복되는 (shop_menu_id, shop_menu_category_id)는 IGNORE에 의해 INSERT가 무시됨

        List<ShopMenuCategoryMap> toBeDeletedMenuCategoryMaps = getToBeDeletedMenuCategoryMaps(existingMenuCategoryMaps, requestedMenuCategoryMaps);
        if (!toBeDeletedMenuCategoryMaps.isEmpty()) {
            adminShopMapper.deleteMenuCategoryMaps(toBeDeletedMenuCategoryMaps);
        }


        // ======= shop_menu_images 테이블 =======
        List<ShopMenuImage> existingMenuImages = adminShopMapper.getMenuImagesByMenuId(menuId);

        List<ShopMenuImage> requestedMenuImages = generateMenuImagesAndGet(existingMenu.getId(), request.getImage_urls());
        if (!requestedMenuImages.isEmpty()) {
            // 중복되는 (shop_menu_id, image_url)은 IGNORE에 의해 INSERT가 무시됨
            adminShopMapper.createMenuImages(requestedMenuImages);
        }

        List<ShopMenuImage> toBeDeletedMenuImages = getToBeDeletedMenuImages(existingMenuImages, requestedMenuImages);
        if (!toBeDeletedMenuImages.isEmpty()) {
            adminShopMapper.deleteMenuImages(toBeDeletedMenuImages);
        }
    }

    private List<ShopMenuDetail> generateMenuDetailsAndGetForUpdate(Integer menuId, List<UpdateShopMenuRequest.OptionPrice> optionPrices) {
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

    // (기존에 존재하던 menu image 리스트에서 요청된 menu image들을 제거 --> 삭제할 menu image 리스트를 얻어낼 수 있다.)
    private List<ShopMenuImage> getToBeDeletedMenuImages(List<ShopMenuImage> existingMenuImages, List<ShopMenuImage> requestedMenuImages) {
        existingMenuImages.removeAll(requestedMenuImages);
        return existingMenuImages;
    }

    @Override
    public void deleteMenu(Integer shopId, Integer menuId) {
        getShopById(shopId); // 상점 존재 여부 체크
        getMenuByIdAndShopId(menuId, shopId); // 메뉴 존재 여부 체크

        adminShopMapper.deleteMenuById(menuId);
        adminShopMapper.deleteAllInformationRelatedToMenuByMenuId(menuId);
    }

    @Override
    public void hideMenu(Integer shopId, Integer menuId) {
        getShopById(shopId); // 상점 존재 여부 체크

        ShopMenu menu = getMenuByIdAndShopId(menuId, shopId);

        menu.checkPossibilityOfAlreadyHidden();

        adminShopMapper.hideMenuById(menuId);
    }

    @Override
    public void revealMenu(Integer shopId, Integer menuId) {
        getShopById(shopId); // 상점 존재 여부 체크

        ShopMenu menu = getMenuByIdAndShopId(menuId, shopId);

        menu.checkPossibilityOfAlreadyUnhidden();

        adminShopMapper.revealMenuById(menuId);
    }

    private ShopCategory getShopCategoryById(Integer id) {
        return Optional.ofNullable(adminShopMapper.getShopCategoryById(id))
                .orElseThrow(() -> new BaseException(SHOP_CATEGORY_NOT_FOUND));
    }

    private Shop getShopById(Integer id) {
        return Optional.ofNullable(adminShopMapper.getShopById(id))
                .orElseThrow(() -> new BaseException(SHOP_NOT_FOUND));
    }

    private ShopMenu getMenuByIdAndShopId(Integer menuId, Integer shopId) {
        ShopMenu menu = Optional.ofNullable(adminShopMapper.getMenuById(menuId))
                .orElseThrow(() -> new BaseException(SHOP_MENU_NOT_FOUND));

        if (!menu.hasSameShopId(shopId)) {
            throw new BaseException(SHOP_MENU_NOT_FOUND);
        }

        return menu;
    }

    private void checkShopCategoriesExistInDatabase(List<Integer> shopCategoryIds) {
        shopCategoryIds.forEach(categoryId -> {
            Optional.ofNullable(adminShopMapper.getShopCategoryById(categoryId))
                    .orElseThrow(() -> new BaseException(SHOP_CATEGORY_NOT_FOUND));
        });
    }

    private void checkMenuCategoriesExistInDatabase(Integer shopId, List<Integer> menuCategoryIds) {
        menuCategoryIds.forEach(menuCategoryId -> {
            ShopMenuCategory menuCategory = Optional.ofNullable(adminShopMapper.getMenuCategoryById(menuCategoryId))
                    .orElseThrow(() -> new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND));

            if (!menuCategory.hasSameShopId(shopId)) {
                throw new BaseException(SHOP_MENU_CATEGORY_NOT_FOUND);
            }
        });
    }

    private List<ShopCategoryMap> generateShopCategoryMapsAndGet(Integer shopId, List<Integer> shopCategoryIds) {
        return shopCategoryIds.stream()
                .map(shopCategoryId -> ShopCategoryMap.of(shopId, shopCategoryId))
                .collect(Collectors.toList());
    }

    private List<ShopImage> generateShopImagesAndGet(Integer shopId, List<String> imageUrls) {
        return imageUrls.stream()
                .map(url -> ShopImage.of(shopId, url))
                .collect(Collectors.toList());
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
