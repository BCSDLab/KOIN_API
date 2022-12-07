package koreatech.in.service;

import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.shop.admin.request.*;
import koreatech.in.dto.shop.admin.request.inner.Open;
import koreatech.in.dto.shop.admin.response.*;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Shop.*;
import koreatech.in.dto.shop.admin.response.inner.MinimizedShop;
import koreatech.in.exception.*;
import koreatech.in.repository.ShopMapper;
import koreatech.in.util.S3Bucket;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static koreatech.in.util.ExceptionMessage.*;

@Service("shopService")
@Validated
public class ShopServiceImpl implements ShopService {
    @Resource(name = "shopMapper")
    private ShopMapper shopMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Inject
    private UploadFileUtils uploadFileUtils;

    @Override
    @Transactional
    public SuccessCreateResponse createShopCategoryForAdmin(CreateShopCategoryRequest request) throws Exception {
        if (isDuplicatedShopCategoryName(request.getName())) {
            throw new ConflictException(new ErrorMessage(SHOP_CATEGORY_NAME_DUPLICATE));
        }

        ShopCategory category = new ShopCategory(request);
        shopMapper.createShopCategory(category);

        return SuccessCreateResponse.builder()
                .id(category.getId())
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse updateShopCategoryForAdmin(Integer shopCategoryId, UpdateShopCategoryRequest request) throws Exception {
        ShopCategory existingCategory = findShopCategory(shopCategoryId);

        if (isDuplicatedShopCategoryName(request.getName(), shopCategoryId)) {
            throw new ConflictException(new ErrorMessage(SHOP_CATEGORY_NAME_DUPLICATE));
        }

        if (existingCategory.needToUpdate(request)) {
            existingCategory.update(request);
            shopMapper.updateShopCategory(existingCategory);
        }

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception {
        findShopCategory(shopCategoryId); // 카테고리 존재 여부 체크

        List<Shop> shopsUsingCategory = shopMapper.getShopsUsingCategory(shopCategoryId);

        // 카테고리를 사용하고 있는 상점이 있으면 삭제할 수 없다.
        if (!shopsUsingCategory.isEmpty()) {
            throw new ConflictException(new ErrorMessage(SHOP_USING_CATEGORY_EXIST));
        }

        shopMapper.deleteShopCategoryById(shopCategoryId);

        return new SuccessResponse();
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopCategoriesResponse getAllShopCategoriesForAdmin() throws Exception {
        List<koreatech.in.dto.shop.admin.response.inner.ShopCategory> categories = shopMapper.getAllShopCategories();

        return AllShopCategoriesResponse.builder()
                .total_count(categories.size())
                .shop_categories(categories)
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse matchShopWithOwner(Integer shopId, MatchShopWithOwnerRequest request) throws Exception {
        Shop shop = findShop(shopId);

        // TODO: 사장님의 존재 여부 확인 + 회원가입 후 권한이 주어졌는지 확인

        shop.matchOwnerId(request.getOwner_id());
        shopMapper.updateShop(shop);

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessCreateResponse createShopForAdmin(CreateShopRequest request) throws Exception {
        /*
             INSERT 대상 테이블
             - shops
             - shop_opens
             - shop_category_map
             - shop_menu_categories
             - shop_images
         */

        /*
             TODO: 어드민인지 사장님인지 체크
                   어드민이면 -> 생성하고 owner_id는 매칭 API로 넣기
                   사장님이면 -> 인증 정보를 이용하여 owner_id 얻어 넣기
                              이미 자신의 상점이 1개 이상 존재하면 403??
         */

        if (isDuplicatedShopName(request.getName())) {
            throw new ConflictException(new ErrorMessage(SHOP_NAME_DUPLICATE));
        }

        // ======= shops 테이블 =======
        Integer ownerId = null; // TODO: 수정 필요
        Shop shop = new Shop(request, ownerId);
        shopMapper.createShop(shop);


        // ======= shop_opens 테이블 =======
        List<ShopOpen> shopOpens = generateShopOpens(shop.getId(), request.getOpen());
        shopMapper.createShopOpens(shopOpens);


        // ======= shop_category_map 테이블 =======
        List<ShopCategoryMap> shopCategoryMaps = new LinkedList<>();

        request.getCategory_ids().forEach(categoryId -> {
            ShopCategory category = shopMapper.getShopCategoryById(categoryId);

            // 카테고리가 db에 존재하는지 확인
            if (category == null) {
                throw new NotFoundException(new ErrorMessage(SHOP_CATEGORY_NOT_FOUND));
            }

            ShopCategoryMap shopCategoryMap = new ShopCategoryMap(shop.getId(), categoryId);
            shopCategoryMaps.add(shopCategoryMap);
        });

        shopMapper.createShopCategoryMaps(shopCategoryMaps);


        // ======= shop_menu_categories 테이블 =======
        /*
            기본적으로 제공되는 카테고리 메뉴: 이벤트 메뉴, 대표 메뉴, 사이드 메뉴, 세트 메뉴
         */
        String[] basicMenuCategoryNames = new String[]{"이벤트 메뉴", "대표 메뉴", "사이드 메뉴", "세트 메뉴"};

        List<ShopMenuCategory> menuCategories = Arrays.stream(basicMenuCategoryNames)
                .map(name -> new ShopMenuCategory(shop.getId(), name))
                .collect(Collectors.toCollection(LinkedList::new));

        shopMapper.createMenuCategories(menuCategories);


        // ======= shop_images 테이블 =======
        List<ShopImage> shopImages = request.getImage_urls().stream()
                .map(url -> new ShopImage(shop.getId(), url))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!shopImages.isEmpty()) {
            shopMapper.createShopImages(shopImages);
        }


        return SuccessCreateResponse.builder()
                .id(shop.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShopForAdmin(Integer shopId) throws Exception {
        ShopResponse.Shop shop = shopMapper.getShopForResponse(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_NOT_FOUND));
        }

        shop.decideWhetherSingleOrNotOfMenus();

        return ShopResponse.builder()
                .shop(shop)
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse updateShopForAdmin(Integer shopId, UpdateShopRequest request) throws Exception {
        /*
             UPDATE 대상 테이블
             - shops
             - shop_open
             - shop_category_map
             - shop_images
         */

        Shop existingShop = findShop(shopId);

        if (isDuplicatedShopName(request.getName(), shopId)) {
            throw new ConflictException(new ErrorMessage(SHOP_NAME_DUPLICATE));
        }


        // ======= shops 테이블 =======
        if (existingShop.needToUpdate(request)) {
            existingShop.update(request);
            shopMapper.updateShop(existingShop);
        }


        // ======= shop_opens 테이블 =======
        List<ShopOpen> shopOpens = generateShopOpens(shopId, request.getOpen());
        shopMapper.updateShopOpens(shopOpens);


        // ======= shop_category_map 테이블 =======
        List<ShopCategory> existingCategories = shopMapper.getShopCategoriesOfShopByShopId(shopId); // 상점이 속해있는 상점 카테고리 목록
        List<ShopCategoryMap> shopCategoryMapsToCreate = new LinkedList<>(); // 추가할 관계
        List<ShopCategoryMap> shopCategoryMapsToDelete; // 삭제할 관계

        request.getCategory_ids().forEach(categoryId -> {
            ShopCategory requestedCategory = shopMapper.getShopCategoryById(categoryId);

            // 요청된 카테고리가 db에 존재하는지 검증
            if (requestedCategory == null) {
                throw new NotFoundException(new ErrorMessage(SHOP_CATEGORY_NOT_FOUND));
            }

            // 요청된 카테고리중 db에 존재하지 않던 카테고리는 새로운 insert 대상
            if (!existingCategories.contains(requestedCategory)) {
                ShopCategoryMap shopCategoryMap = new ShopCategoryMap(shopId, categoryId);
                shopCategoryMapsToCreate.add(shopCategoryMap);
            }

            // 삭제 대상을 추리기 위한 remove
            existingCategories.remove(requestedCategory);
        });

        if (!shopCategoryMapsToCreate.isEmpty()) {
            shopMapper.createShopCategoryMaps(shopCategoryMapsToCreate);
        }

        // existingCategories 리스트에 남아있는 카테고리: 기존에는 있었지만 요청에는 없음 --> 삭제 대상
        shopCategoryMapsToDelete = existingCategories.stream()
                .map(category -> new ShopCategoryMap(shopId, category.getId()))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!shopCategoryMapsToDelete.isEmpty()) {
            shopMapper.deleteShopCategoryMaps(shopCategoryMapsToDelete);
        }


        // ======= shop_images 테이블 =======
        List<String> existingImageUrls = shopMapper.getShopImageUrlsByShopId(shopId);

        List<ShopImage> shopImagesToCreate = new LinkedList<>(); // 추가할 shop image list
        List<ShopImage> shopImagesToDelete; // 삭제할 shop image list

        request.getImage_urls().forEach(imageUrl -> {
            // 기존에 없던 url은 새로운 INSERT 대상
            if (!existingImageUrls.contains(imageUrl)) {
                ShopImage shopImage = new ShopImage(shopId, imageUrl);
                shopImagesToCreate.add(shopImage);
            }

            // 삭제 대상을 추리기 위한 remove
            existingImageUrls.remove(imageUrl);
        });

        if (!shopImagesToCreate.isEmpty()) {
            shopMapper.createShopImages(shopImagesToCreate);
        }

        // existingImageUrls 리스트에 남아있는 url: 기존에는 있었지만 요청에는 없음 --> 삭제 대상
        shopImagesToDelete = existingImageUrls.stream()
                .map(imageUrl -> new ShopImage(shopId, imageUrl))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!shopImagesToDelete.isEmpty()) {
            shopMapper.deleteShopImages(shopImagesToDelete);
        }


        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse deleteShopForAdmin(Integer shopId) throws Exception {
        Shop shop = findShop(shopId);

        if (shop.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage(SHOP_ALREADY_DELETED));
        }

        /*
             shop 테이블과 관계가 맺어져 있는 테이블의 레코드도 전부 soft delete 처리하는 것이 아닌,
             shop 테이블의 레코드만 soft delete 한다.
             이유는 어드민페이지에서 상점 삭제 해제 기능을 사용할때 쉽게 복구가 가능해야 하기 때문이다.
         */
        shopMapper.deleteShopById(shopId);

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse undeleteOfShopForAdmin(Integer shopId) throws Exception {
        Shop shop = findShop(shopId);

        if (!shop.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage(SHOP_NOT_DELETED));
        }

        shopMapper.undeleteShopById(shopId);

        return new SuccessResponse();
    }

    @Override
    @Transactional(readOnly = true)
    public ShopsResponse getShopsForAdmin(ShopsCondition condition) throws Exception {
        condition.removeBlankOfSearchName();
        //request.setFilter();

        Integer totalCount = shopMapper.getTotalCountOfShopsByCondition(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new ValidationException(new ErrorMessage(PAGE_NOT_FOUND));
        }

        List<MinimizedShop> shops = shopMapper.getShopsByCondition(condition.extractBegin(), condition);

        return ShopsResponse.builder()
                .total_page(totalPage)
                .current_page(currentPage)
                .shops(shops)
                .build();
    }

    @Override
    @Transactional
    public SuccessCreateResponse createMenuCategoryForAdmin(Integer shopId, CreateShopMenuCategoryRequest request) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        if (isDuplicatedMenuCategoryName(request.getName(), shopId)) {
            throw new ConflictException(new ErrorMessage(SHOP_MENU_CATEGORY_NAME_DUPLICATE));
        }

        Integer existingCount = shopMapper.getCountOfMenuCategoriesByShopId(shopId);
        // 한 상점당 메뉴 카테고리 최대 개수는 20개
        if (existingCount.equals(20)) {
            throw new ConflictException(new ErrorMessage(SHOP_MENU_CATEGORY_MAXIMUM_EXCEED));
        }

        ShopMenuCategory menuCategory = new ShopMenuCategory(shopId, request.getName());
        shopMapper.createMenuCategory(menuCategory);

        return SuccessCreateResponse.builder()
                .id(menuCategory.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShopForAdmin(Integer shopId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        List<koreatech.in.dto.shop.admin.response.inner.ShopMenuCategory> menuCategories = shopMapper.getMenuCategoriesOfShopByShopId(shopId);

        return AllMenuCategoriesOfShopResponse.builder()
                .shop_id(shopId)
                .count(menuCategories.size())
                .menu_categories(menuCategories)
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        ShopMenuCategory category = shopMapper.getMenuCategoryById(menuCategoryId);

        if (category == null || !category.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_CATEGORY_NOT_FOUND));
        }

        // 카테고리를 사용하고 있는 메뉴가 1개라도 있으면 삭제 불가
        List<ShopMenu> menusUsingCategory = shopMapper.getMenusUsingCategory(menuCategoryId);
        if (!menusUsingCategory.isEmpty()) {
            throw new ConflictException(new ErrorMessage(SHOP_MENU_USING_CATEGORY_EXIST));
        }

        shopMapper.deleteMenuCategoryById(menuCategoryId);

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessCreateResponse createMenuForAdmin(Integer shopId, CreateShopMenuRequest request) throws Exception {
        /*
             INSERT 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        findShop(shopId); // 상점 존재 여부 체크

        // ======= shop_menus 테이블 =======
        ShopMenu menu = new ShopMenu(shopId, request);
        shopMapper.createMenu(menu);


        // ======= shop_menu_details 테이블 =======
        // 단일 메뉴일때
        if (request.getIs_single()) {
            if (request.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", REQUEST_DATA_INVALID.getCode()));
            }

            ShopMenuDetail menuDetail = new ShopMenuDetail(menu.getId(), request.getSingle_price());
            shopMapper.createMenuDetail(menuDetail);
        }
        // 단일 메뉴가 아닐때
        else {
            if (request.getOption_prices() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 false이면 option_prices는 필수입니다.", REQUEST_DATA_INVALID.getCode()));
            }

            // 옵션명 중복 체크
            if (request.hasDuplicatedOption()) {
                throw new ValidationException(new ErrorMessage("option_prices에서 중복되는 option이 있습니다.", REQUEST_DATA_INVALID.getCode()));
            }

            List<ShopMenuDetail> menuDetails = request.getOption_prices().stream()
                    .map(optionPrice -> new ShopMenuDetail(menu.getId(), optionPrice.getOption(), optionPrice.getPrice()))
                    .collect(Collectors.toCollection(LinkedList::new));

            shopMapper.createMenuDetails(menuDetails);
        }


        // ======= shop_menu_images 테이블 =======
        List<ShopMenuImage> menuImages = request.getImage_urls().stream()
                .map(imageUrl -> new ShopMenuImage(menu.getId(), imageUrl))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!menuImages.isEmpty()) {
            shopMapper.createMenuImages(menuImages);
        }


        // ======= shop_menu_category_map 테이블 =======
        List<ShopMenuCategoryMap> shopMenuCategoryMaps = new LinkedList<>();

        request.getCategory_ids().forEach(categoryId -> {
            ShopMenuCategory category = shopMapper.getMenuCategoryById(categoryId);

            // 카테고리가 조회되지 않거나 해당 상점의 메뉴 카테고리가 아니라면
            if (category == null || !category.hasSameShopId(shopId)) {
                throw new NotFoundException(new ErrorMessage(SHOP_MENU_CATEGORY_NOT_FOUND));
            }

            shopMenuCategoryMaps.add(new ShopMenuCategoryMap(menu.getId(), categoryId));
        });

        shopMapper.createMenuCategoryMaps(shopMenuCategoryMaps);


        return SuccessCreateResponse.builder()
                .id(menu.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        koreatech.in.dto.shop.admin.response.inner.ShopMenu menu = shopMapper.getMenuForResponse(menuId);

        if (menu == null || !menu.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        menu.decideWhetherSingleOrNot();

        return MenuResponse.builder()
                .menu(menu)
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse updateMenuForAdmin(Integer shopId, Integer menuId, UpdateShopMenuRequest request) throws Exception {
        /*
             UPDATE 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        findShop(shopId); // 상점 존재 여부 체크

        ShopMenu existingMenu = findMenu(menuId);

        if (!existingMenu.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }


        // ======= shop_menus 테이블 =======
        if (existingMenu.needToUpdate(request)) {
            existingMenu.update(request);
            shopMapper.updateMenu(existingMenu);
        }


        // ======= shop_menu_details 테이블 =======
        List<ShopMenuDetail> existingMenuDetails = shopMapper.getMenuDetailsByMenuId(menuId);
        // 단일 메뉴일때
        if (request.getIs_single()) {
            if (request.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", REQUEST_DATA_INVALID.getCode()));
            }

            ShopMenuDetail requestedMenuDetail = new ShopMenuDetail(menuId, request.getSingle_price());

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
            if (request.getOption_prices() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 false이면 option_prices를 비워둘 수 없습니다.", REQUEST_DATA_INVALID.getCode()));
            }
            if (request.hasDuplicatedOption()) {
                throw new ValidationException(new ErrorMessage("option_prices에서 중복되는 option이 있습니다.", REQUEST_DATA_INVALID.getCode()));
            }

            List<ShopMenuDetail> menuDetailsToCreate = new LinkedList<>();

            request.getOption_prices().forEach(optionPrice -> {
                ShopMenuDetail requestedMenuDetail = new ShopMenuDetail(menuId, optionPrice.getOption(), optionPrice.getPrice());

                // 기존에 없던 menu detail은 새로운 INSERT 대상
                if (!existingMenuDetails.contains(requestedMenuDetail)) {
                    menuDetailsToCreate.add(requestedMenuDetail);
                }

                // 삭제 대상을 추리기 위한 remove
                existingMenuDetails.remove(requestedMenuDetail);
            });

            if (!menuDetailsToCreate.isEmpty()) {
                shopMapper.createMenuDetails(menuDetailsToCreate);
            }

            // existingMenuDetails 리스트에 남아있는 menu detail: 기존에는 있었지만 요청에는 없음 --> 삭제 대상
            if (!existingMenuDetails.isEmpty()) {
                shopMapper.deleteMenuDetails(existingMenuDetails);
            }
        }


        // ======= shop_menu_images 테이블 =======
        List<String> existingImageUrls = shopMapper.getMenuImageUrlsByMenuId(menuId);

        List<ShopMenuImage> menuImagesToCreate = new LinkedList<>();
        List<ShopMenuImage> menuImagesToDelete;

        request.getImage_urls().forEach(imageUrl -> {
            if (!existingImageUrls.contains(imageUrl)) {
                ShopMenuImage menuImage = new ShopMenuImage(menuId, imageUrl);
                menuImagesToCreate.add(menuImage);
            }

            // 삭제 대상을 추려내기 위한 remove
            existingImageUrls.remove(imageUrl);
        });

        if (!menuImagesToCreate.isEmpty()) {
            shopMapper.createMenuImages(menuImagesToCreate);
        }

        // existingImageUrls 리스트에 남아있는 url: 기존에는 있었지만 요청에는 없음 --> 삭제 대상
        menuImagesToDelete = existingImageUrls.stream()
                .map(imageUrl -> new ShopMenuImage(menuId, imageUrl))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!menuImagesToDelete.isEmpty()) {
            shopMapper.deleteMenuImages(menuImagesToDelete);
        }


        // --- shop_menu_category_map 테이블 ---
        List<ShopMenuCategory> existingCategories = shopMapper.getMenuCategoriesOfMenuByMenuId(menuId);
        List<ShopMenuCategoryMap> menuCategoryMapsToCreate = new LinkedList<>();
        List<ShopMenuCategoryMap> menuCategoryMapsToDelete;

        request.getCategory_ids().forEach(categoryId -> {
            ShopMenuCategory requestedCategory = shopMapper.getMenuCategoryById(categoryId);

            // 카테고리가 db에 존재하는지, 해당 상점의 메뉴 카테고리가 맞는지 검증
            if (requestedCategory == null || !requestedCategory.hasSameShopId(shopId)) {
                throw new NotFoundException(new ErrorMessage(SHOP_MENU_CATEGORY_NOT_FOUND));
            }

            // 기존에 없던 메뉴 카테고리는 새로운 INSERT 대상
            if (!existingCategories.contains(requestedCategory)) {
                ShopMenuCategoryMap shopMenuCategoryMap = new ShopMenuCategoryMap(menuId, requestedCategory.getId());
                menuCategoryMapsToCreate.add(shopMenuCategoryMap);
            }

            // 삭제 대상을 추리기 위한 remove
            existingCategories.remove(requestedCategory);
        });

        if (!menuCategoryMapsToCreate.isEmpty()) {
            shopMapper.createMenuCategoryMaps(menuCategoryMapsToCreate);
        }

        // existingCategories 리스트에 남아있는 카테고리: 기존에는 있었지만 요청에는 없음 --> 삭제 대상
        menuCategoryMapsToDelete = existingCategories.stream()
                .map(category -> new ShopMenuCategoryMap(menuId, category.getId()))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!menuCategoryMapsToDelete.isEmpty()) {
            shopMapper.deleteMenuCategoryMaps(menuCategoryMapsToDelete);
        }


        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        ShopMenu menu = findMenu(menuId);

        if (!menu.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        shopMapper.deleteAllForInvolvedWithMenu(menuId);

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hidden) throws Exception {
        findShop(shopId);

        ShopMenu menu = findMenu(menuId);

        if (!menu.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        if (hidden) {
            if (menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage(SHOP_MENU_ALREADY_HIDDEN));
            }

            shopMapper.hideMenuById(menuId);
        } else {
            if (!menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage(SHOP_MENU_NOT_HIDDEN));
            }

            shopMapper.revealMenuById(menuId);
        }

        return new SuccessResponse();
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenusOfShopResponse getAllMenusOfShopForAdmin(Integer shopId) throws Exception {
        AllMenusOfShopResponse menus = shopMapper.getAllMenusOfShopForResponse(shopId);

        if (menus == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_NOT_FOUND));
        }

        return menus.decideOptionalOfMenus();
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShop(Integer shopId) throws Exception {
        ShopResponse.Shop shop = shopMapper.getShopForResponse(shopId);

        if (shop == null || shop.getIs_deleted()) {
            throw new NotFoundException(new ErrorMessage(SHOP_NOT_FOUND));
        }

        shop.decideWhetherSingleOrNotOfMenus();

        return ShopResponse.builder()
                .shop(shop)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopsResponse getAllShops() throws Exception {
        List<koreatech.in.dto.shop.admin.response.inner.Shop> shops = shopMapper.getAllShops();

        return AllShopsResponse.builder()
                .total_count(shops.size())
                .shops(shops)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopCategoriesResponse getAllShopCategories() throws Exception {
        List<koreatech.in.dto.shop.admin.response.inner.ShopCategory> categories = shopMapper.getAllShopCategories();

        return AllShopCategoriesResponse.builder()
                .total_count(categories.size())
                .shop_categories(categories)
                .build();
    }

    @Override
    public UploadImageResponse uploadShopCategoryImage(MultipartFile image) throws Exception {
        String imageUrl = uploadImage(image, S3Bucket.SHOP_CATEGORY);

        return UploadImageResponse.builder()
                .image_url(imageUrl)
                .build();
    }

    @Override
    public UploadImagesResponse uploadShopMenuImages(List<MultipartFile> images) throws Exception {
        List<String> imageUrls = uploadImages(images, S3Bucket.SHOP_MENU);

        return UploadImagesResponse.builder()
                .image_urls(imageUrls)
                .build();
    }

    @Override
    public UploadImagesResponse uploadShopImages(List<MultipartFile> images) throws Exception {
        List<String> imageUrls = uploadImages(images, S3Bucket.SHOP);

        return UploadImagesResponse.builder()
                .image_urls(imageUrls)
                .build();
    }

    private boolean isDuplicatedShopCategoryName(String name) {
        return shopMapper.getShopCategoryByName(name) != null;
    }

    private boolean isDuplicatedShopCategoryName(String name, Integer shopCategoryId) {
        ShopCategory shopCategory = shopMapper.getShopCategoryByName(name);

        return shopCategory != null && !shopCategory.hasSameId(shopCategoryId);
    }

    private boolean isDuplicatedShopName(String name) {
        return shopMapper.getShopByName(name) != null;
    }

    private boolean isDuplicatedShopName(String name, Integer shopId) {
        Shop shop = shopMapper.getShopByName(name);

        return shop != null && !shop.hasSameId(shopId);
    }

    private boolean isDuplicatedMenuCategoryName(String name, Integer shopId) {
        return shopMapper.getMenuCategoryByShopIdAndName(shopId, name) != null;
    }

    private ShopCategory findShopCategory(Integer shopCategoryId) {
        ShopCategory category = shopMapper.getShopCategoryById(shopCategoryId);

        if (category == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_CATEGORY_NOT_FOUND));
        }

        return category;
    }

    private Shop findShop(Integer shopId) {
        Shop shop = shopMapper.getShopById(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_NOT_FOUND));
        }

        return shop;
    }

    private ShopMenu findMenu(Integer menuId) {
        ShopMenu menu = shopMapper.getMenuById(menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        return menu;
    }

    private List<ShopOpen> generateShopOpens(Integer shopId, List<Open> opens) throws Exception {
        if (opens.size() != 7) {
            throw new ValidationException(new ErrorMessage("open의 길이는 7이어야 합니다.", REQUEST_DATA_INVALID.getCode()));
        }

        List<DayOfWeek> expected = Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        List<DayOfWeek> actual = new LinkedList<>();
        List<ShopOpen> shopOpens = new LinkedList<>();

        opens.forEach(open -> {
            DayOfWeek dayOfWeek = open.getDay_of_week();
            Boolean closed = open.getClosed();
            String openTime = open.getOpen_time();
            String closeTime = open.getClose_time();

            if (!closed) {
                if (openTime == null || closeTime == null) {
                    throw new ValidationException(
                            new ErrorMessage("open의 closed가 false이면 open_time과 close_time은 필수입니다.", REQUEST_DATA_INVALID.getCode())
                    );
                }
            }

            actual.add(dayOfWeek);
            shopOpens.add(new ShopOpen(shopId, dayOfWeek, closed, openTime, closeTime));
        });

        Collections.sort(actual);

        if (!expected.equals(actual)) {
            throw new ValidationException(new ErrorMessage("open에 올바르지 않은 값이 들어있습니다.", REQUEST_DATA_INVALID.getCode()));
        }

        return shopOpens;
    }

    // 이미지 단건 업로드
    private String uploadImage(MultipartFile image, S3Bucket bucket) throws Exception {
        String uploadedImageName = uploadFileUtils.uploadFile(bucket.getPath(), image.getOriginalFilename(), image.getBytes(), image);

        StringBuilder stringBuilder = new StringBuilder()
                .append("https://")
                .append(uploadFileUtils.getDomain())
                .append("/")
                .append(bucket.getPath())
                .append(uploadedImageName);

        return stringBuilder.toString();
    }

    // 다중 이미지 업로드
    private List<String> uploadImages(List<MultipartFile> images, S3Bucket bucket) throws Exception {
        List<String> imageUrls = new LinkedList<>();

        for (MultipartFile image : images) {
            String uploadedImageName = uploadFileUtils.uploadFile(bucket.getPath(), image.getOriginalFilename(), image.getBytes(), image);

            StringBuilder stringBuilder = new StringBuilder()
                    .append("https://")
                    .append(uploadFileUtils.getDomain())
                    .append("/")
                    .append(bucket.getPath())
                    .append(uploadedImageName);

            imageUrls.add(stringBuilder.toString());
        }

        return imageUrls;
    }
}
