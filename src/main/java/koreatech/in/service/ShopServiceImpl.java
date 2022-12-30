package koreatech.in.service;

import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.shop.admin.request.*;
import koreatech.in.dto.shop.admin.request.inner.Open;
import koreatech.in.dto.shop.admin.response.*;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Shop.*;
import koreatech.in.dto.shop.normal.response.AllShopsResponse;
import koreatech.in.exception.*;
import koreatech.in.repository.ShopMapper;
import koreatech.in.repository.query.ShopQueryMapper;
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
    private ShopQueryMapper shopQueryMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Inject
    private UploadFileUtils uploadFileUtils;

    @Override
    @Transactional
    public void createShopCategoryForAdmin(CreateShopCategoryRequest request) throws Exception {
        if (isDuplicatedShopCategoryName(request.getName())) {
            throw new ConflictException(new ErrorMessage(SHOP_CATEGORY_NAME_DUPLICATE));
        }

        ShopCategory category = ShopCategory.create(request);
        shopMapper.createShopCategoryForAdmin(category);
    }

    @Override
    @Transactional
    public void updateShopCategoryForAdmin(Integer shopCategoryId, UpdateShopCategoryRequest request) throws Exception {
        ShopCategory existingCategory = findShopCategory(shopCategoryId);

        if (isDuplicatedShopCategoryName(request.getName(), shopCategoryId)) {
            throw new ConflictException(new ErrorMessage(SHOP_CATEGORY_NAME_DUPLICATE));
        }

        if (existingCategory.needToUpdate(request)) {
            existingCategory.update(request);
            shopMapper.updateShopCategoryForAdmin(existingCategory);
        }
    }

    @Override
    @Transactional
    public void deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception {
        findShopCategory(shopCategoryId); // 카테고리 존재 여부 체크

        List<Shop> shopsUsingCategory = shopMapper.getShopsUsingCategoryForAdmin(shopCategoryId);

        // 카테고리를 사용하고 있는 상점이 있으면 삭제할 수 없다.
        if (!shopsUsingCategory.isEmpty()) {
            throw new ConflictException(new ErrorMessage(SHOP_USING_CATEGORY_EXIST));
        }

        shopMapper.deleteShopCategoryByIdForAdmin(shopCategoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopCategoriesResponse getAllShopCategoriesForAdmin() throws Exception {
        List<ShopCategory> allShopCategories = shopMapper.getAllShopCategoriesForAdmin();

        return AllShopCategoriesResponse.from(allShopCategories);
    }

    @Override
    @Transactional
    public void matchShopWithOwner(Integer shopId, MatchShopWithOwnerRequest request) throws Exception {
        Shop shop = findShop(shopId);

        // TODO: 사장님의 존재 여부 확인 + 회원가입 후 권한이 주어졌는지 확인

        shop.matchOwnerId(request.getOwner_id());
        shopMapper.updateShopForAdmin(shop);
    }

    @Override
    @Transactional
    public void createShopForAdmin(CreateShopRequest request) throws Exception {
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
        Shop shop = Shop.create(request, ownerId);
        shopMapper.createShopForAdmin(shop);


        // ======= shop_opens 테이블 =======
        List<ShopOpen> shopOpens = generateShopOpens(shop.getId(), request.getOpen());
        shopMapper.createShopOpensForAdmin(shopOpens);


        // ======= shop_category_map 테이블 =======
        List<ShopCategoryMap> shopCategoryMaps = new ArrayList<>();

        request.getCategory_ids().forEach(categoryId -> {
            ShopCategory category = shopMapper.getShopCategoryByIdForAdmin(categoryId);

            // 카테고리가 db에 존재하는지 확인
            if (category == null) {
                throw new NotFoundException(new ErrorMessage(SHOP_CATEGORY_NOT_FOUND));
            }

            ShopCategoryMap shopCategoryMap = ShopCategoryMap.create(shop.getId(), categoryId);
            shopCategoryMaps.add(shopCategoryMap);
        });

        shopMapper.createShopCategoryMapsForAdmin(shopCategoryMaps);


        // ======= shop_menu_categories 테이블 =======
        /*
            기본적으로 제공되는 카테고리 메뉴: 이벤트 메뉴, 대표 메뉴, 사이드 메뉴, 세트 메뉴
         */
        String[] basicMenuCategoryNames = new String[]{"이벤트 메뉴", "대표 메뉴", "사이드 메뉴", "세트 메뉴"};

        List<ShopMenuCategory> menuCategories = Arrays.stream(basicMenuCategoryNames)
                .map(name -> ShopMenuCategory.create(shop.getId(), name))
                .collect(Collectors.toCollection(ArrayList::new));

        shopMapper.createMenuCategoriesForAdmin(menuCategories);


        // ======= shop_images 테이블 =======
        List<ShopImage> shopImages = request.getImage_urls().stream()
                .map(url -> ShopImage.create(shop.getId(), url))
                .collect(Collectors.toCollection(ArrayList::new));

        if (!shopImages.isEmpty()) {
            shopMapper.createShopImagesForAdmin(shopImages);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShopForAdmin(Integer shopId) throws Exception {
        ShopResponse.Shop shop = shopQueryMapper.getShopInShopResponseForAdmin(shopId);

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
    public void updateShopForAdmin(Integer shopId, UpdateShopRequest request) throws Exception {
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
        // 불필요한 커넥션을 방지하기 위해 수정 필요 여부 검사
        if (existingShop.needToUpdate(request)) {
            existingShop.update(request);
            shopMapper.updateShopForAdmin(existingShop);
        }


        // ======= shop_opens 테이블 =======
        List<ShopOpen> shopOpens = generateShopOpens(shopId, request.getOpen());
        shopMapper.updateShopOpensForAdmin(shopOpens);


        // ======= shop_category_map 테이블 =======
        List<Integer> existingCategoryIds = shopMapper.getShopCategoryIdsOfShopByShopIdForAdmin(shopId); // 상점이 속해있는 상점 카테고리 목록
        List<Integer> copiedExistingCategoryIds = new ArrayList<>(existingCategoryIds);

        List<Integer> requestedCategoryIds = request.getCategory_ids();

        requestedCategoryIds.forEach(categoryId -> {
            ShopCategory category = shopMapper.getShopCategoryByIdForAdmin(categoryId);

            if (category == null) {
                throw new NotFoundException(new ErrorMessage(SHOP_CATEGORY_NOT_FOUND));
            }
        });

        // shop_category_map 테이블에서 삭제할 관계(레코드)의 카테고리 id 리스트 = (기존 카테고리 id 리스트)에서 (요청된 카테고리 id)들을 제거한 리스트
        existingCategoryIds.removeAll(requestedCategoryIds);
        List<ShopCategoryMap> shopCategoryMapsToDelete = existingCategoryIds.stream()
                .map(categoryId -> ShopCategoryMap.create(shopId, categoryId))
                .collect(Collectors.toList());

        // shop_category_map 테이블에 추가할 관계(레코드)의 카테고리 id 리스트 = (요청된 카테고리 id 리스트)에서 (기존 카테고리 id)들을 제거한 리스트
        requestedCategoryIds.removeAll(copiedExistingCategoryIds);
        List<ShopCategoryMap> shopCategoryMapsToCreate = requestedCategoryIds.stream()
                .map(categoryId -> ShopCategoryMap.create(shopId, categoryId))
                .collect(Collectors.toList());

        if (!shopCategoryMapsToDelete.isEmpty()) {
            shopMapper.deleteShopCategoryMapsForAdmin(shopCategoryMapsToDelete);
        }
        if (!shopCategoryMapsToCreate.isEmpty()) {
            shopMapper.createShopCategoryMapsForAdmin(shopCategoryMapsToCreate);
        }


        // ======= shop_images 테이블 =======
        List<String> existingImageUrls = shopMapper.getShopImageUrlsByShopIdForAdmin(shopId);
        List<String> copiedExistingImageUrls = new ArrayList<>(existingImageUrls);

        List<String> requestedImageUrls = request.getImage_urls();

        // 삭제할 이미지 url 리스트 = (기존 이미지 url 리스트)에서 (요청된 이미지 url)들을 제거한 리스트
        existingImageUrls.removeAll(requestedImageUrls);
        List<ShopImage> shopImagesToDelete = existingImageUrls.stream()
                .map(imageUrl -> ShopImage.create(shopId, imageUrl))
                .collect(Collectors.toList());

        // 추가할 이미지 url 리스트 = (요청된 이미지 url 리스트)에서 (기존 이미지 url)들을 제거한 리스트
        requestedImageUrls.removeAll(copiedExistingImageUrls);
        List<ShopImage> shopImagesToCreate = requestedImageUrls.stream()
                .map(imageUrl -> ShopImage.create(shopId, imageUrl))
                .collect(Collectors.toList());

        if (!shopImagesToDelete.isEmpty()) {
            shopMapper.deleteShopImagesForAdmin(shopImagesToDelete);
        }
        if (!shopImagesToCreate.isEmpty()) {
            shopMapper.createShopImagesForAdmin(shopImagesToCreate);
        }
    }

    @Override
    @Transactional
    public void deleteShopForAdmin(Integer shopId) throws Exception {
        Shop shop = findShop(shopId);

        if (shop.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage(SHOP_ALREADY_DELETED));
        }

        /*
             shop 테이블과 관계가 맺어져 있는 테이블의 레코드도 전부 soft delete 처리하는 것이 아닌,
             shop 테이블의 레코드만 soft delete 한다.
             이유는 어드민페이지에서 상점 삭제 해제 기능을 사용할때 쉽게 복구가 가능해야 하기 때문이다.
         */
        shopMapper.deleteShopByIdForAdmin(shopId);
    }

    @Override
    @Transactional
    public void undeleteOfShopForAdmin(Integer shopId) throws Exception {
        Shop shop = findShop(shopId);

        if (!shop.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage(SHOP_NOT_DELETED));
        }

        shopMapper.undeleteShopByIdForAdmin(shopId);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopsResponse getShopsForAdmin(ShopsCondition condition) throws Exception {
        Integer totalCount = shopQueryMapper.getTotalCountOfShopsByConditionForAdmin(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new ValidationException(new ErrorMessage(PAGE_NOT_FOUND));
        }

        List<ShopsResponse.Shop> shops = shopQueryMapper.getShopsInShopsResponseByConditionForAdmin(condition.getCursor(), condition);

        return ShopsResponse.builder()
                .total_count(totalCount)
                .total_page(totalPage)
                .current_count(shops.size())
                .current_page(currentPage)
                .shops(shops)
                .build();
    }

    @Override
    @Transactional
    public void createMenuCategoryForAdmin(Integer shopId, CreateShopMenuCategoryRequest request) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        if (isDuplicatedMenuCategoryName(request.getName(), shopId)) {
            throw new ConflictException(new ErrorMessage(SHOP_MENU_CATEGORY_NAME_DUPLICATE));
        }

        Integer existingCount = shopMapper.getCountOfMenuCategoriesByShopIdForAdmin(shopId);
        // 한 상점당 메뉴 카테고리 최대 개수는 20개
        if (existingCount.equals(20)) {
            throw new ConflictException(new ErrorMessage(SHOP_MENU_CATEGORY_MAXIMUM_EXCEED));
        }

        ShopMenuCategory menuCategory = ShopMenuCategory.create(shopId, request.getName());
        shopMapper.createMenuCategoryForAdmin(menuCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenuCategoriesOfShopResponse getAllMenuCategoriesOfShopForAdmin(Integer shopId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        List<ShopMenuCategory> menuCategoriesOfShop = shopMapper.getMenuCategoriesOfShopByShopIdForAdmin(shopId);

        return AllMenuCategoriesOfShopResponse.from(menuCategoriesOfShop);
    }

    @Override
    @Transactional
    public void deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        ShopMenuCategory category = shopMapper.getMenuCategoryByIdForAdmin(menuCategoryId);

        if (category == null || !category.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_CATEGORY_NOT_FOUND));
        }

        // 카테고리를 사용하고 있는 메뉴가 1개라도 있으면 삭제 불가
        List<ShopMenu> menusUsingCategory = shopMapper.getMenusUsingCategoryForAdmin(menuCategoryId);
        if (!menusUsingCategory.isEmpty()) {
            throw new ConflictException(new ErrorMessage(SHOP_MENU_USING_CATEGORY_EXIST));
        }

        shopMapper.deleteMenuCategoryByIdForAdmin(menuCategoryId);
    }

    @Override
    @Transactional
    public void createMenuForAdmin(Integer shopId, CreateShopMenuRequest request) throws Exception {
        /*
             INSERT 대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        findShop(shopId); // 상점 존재 여부 체크

        // ======= shop_menus 테이블 =======
        ShopMenu menu = ShopMenu.create(shopId, request);
        shopMapper.createMenuForAdmin(menu);


        // ======= shop_menu_details 테이블 =======
        // 단일 메뉴일때
        if (request.getIs_single()) {
            if (request.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", REQUEST_DATA_INVALID.getCode()));
            }

            ShopMenuDetail menuDetail = ShopMenuDetail.create(menu.getId(), null, request.getSingle_price());
            shopMapper.createMenuDetailForAdmin(menuDetail);
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
                    .map(optionPrice -> ShopMenuDetail.create(menu.getId(), optionPrice.getOption(), optionPrice.getPrice()))
                    .collect(Collectors.toCollection(ArrayList::new));

            shopMapper.createMenuDetailsForAdmin(menuDetails);
        }


        // ======= shop_menu_images 테이블 =======
        List<ShopMenuImage> menuImages = request.getImage_urls().stream()
                .map(imageUrl -> ShopMenuImage.create(menu.getId(), imageUrl))
                .collect(Collectors.toCollection(ArrayList::new));

        if (!menuImages.isEmpty()) {
            shopMapper.createMenuImagesForAdmin(menuImages);
        }


        // ======= shop_menu_category_map 테이블 =======
        List<ShopMenuCategoryMap> shopMenuCategoryMaps = new ArrayList<>();

        request.getCategory_ids().forEach(categoryId -> {
            ShopMenuCategory category = shopMapper.getMenuCategoryByIdForAdmin(categoryId);

            // 카테고리가 조회되지 않거나 해당 상점의 메뉴 카테고리가 아니라면
            if (category == null || !category.hasSameShopId(shopId)) {
                throw new NotFoundException(new ErrorMessage(SHOP_MENU_CATEGORY_NOT_FOUND));
            }

            ShopMenuCategoryMap shopMenuCategoryMap = ShopMenuCategoryMap.create(menu.getId(), categoryId);
            shopMenuCategoryMaps.add(shopMenuCategoryMap);
        });

        shopMapper.createMenuCategoryMapsForAdmin(shopMenuCategoryMaps);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        MenuResponse.Menu menu = shopQueryMapper.getMenuInMenuResponseForAdmin(menuId);

        if (menu == null || !menu.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        menu.decideWhetherSingleOrNot();

        List<ShopMenuCategory> allMenuCategoriesOfShop = shopMapper.getMenuCategoriesOfShopByShopIdForAdmin(shopId);

        MenuResponse menuResponse = new MenuResponse();
        menuResponse.setMenu(menu);
        menuResponse.setMenuCategoriesOfShopFrom(allMenuCategoriesOfShop);

        return menuResponse;
    }

    @Override
    @Transactional
    public void updateMenuForAdmin(Integer shopId, Integer menuId, UpdateShopMenuRequest request) throws Exception {
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
            shopMapper.updateMenuForAdmin(existingMenu);
        }


        // ======= shop_menu_details 테이블 =======
        List<ShopMenuDetail> existingMenuDetails = shopMapper.getMenuDetailsByMenuIdForAdmin(menuId);
        // 단일 메뉴일때
        if (request.getIs_single()) {
            if (request.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", REQUEST_DATA_INVALID.getCode()));
            }

            ShopMenuDetail requestedMenuDetail = ShopMenuDetail.create(menuId, null, request.getSingle_price()); // 단일 메뉴는 option이 null

            if (existingMenuDetails.contains(requestedMenuDetail)) {
                existingMenuDetails.remove(requestedMenuDetail);
                if (!existingMenuDetails.isEmpty()) {
                    shopMapper.deleteMenuDetailsForAdmin(existingMenuDetails);
                }
            } else {
                shopMapper.deleteMenuDetailsByMenuIdForAdmin(menuId);
                shopMapper.createMenuDetailForAdmin(requestedMenuDetail);
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

            List<ShopMenuDetail> copiedExistingMenuDetails = new ArrayList<>(existingMenuDetails);

            // OptionPrice 타입을 ShopMenuDetail 타입으로 변환
            List<ShopMenuDetail> requestedMenuDetails = request.getOption_prices().stream()
                    .map(optionPrice -> ShopMenuDetail.create(menuId, optionPrice.getOption(), optionPrice.getPrice()))
                    .collect(Collectors.toList());

            // 삭제할 menu detail 리스트 = (기존 menu detail 리스트)에서 (요청된 menu detail)들을 제거한 리스트
            existingMenuDetails.removeAll(requestedMenuDetails);
            // 추가할 menu detail 리스트 = (요청된 menu detail 리스트)에서 (기존 menu detail)들을 제거한 리스트
            requestedMenuDetails.removeAll(copiedExistingMenuDetails);

            if (!existingMenuDetails.isEmpty()) {
                shopMapper.deleteMenuDetailsForAdmin(existingMenuDetails);
            }
            if (!requestedMenuDetails.isEmpty()) {
                shopMapper.createMenuDetailsForAdmin(requestedMenuDetails);
            }
        }


        // ======= shop_menu_category_map 테이블 =======
        List<Integer> existingCategoryIds = shopMapper.getMenuCategoryIdsOfMenuByMenuIdForAdmin(menuId);
        List<Integer> copiedExistingCategoryIds = new ArrayList<>(existingCategoryIds);

        List<Integer> requestedCategoryIds = request.getCategory_ids();
        requestedCategoryIds.forEach(categoryId -> {
            ShopMenuCategory menuCategory = shopMapper.getMenuCategoryByIdForAdmin(categoryId);

            if (menuCategory == null || !menuCategory.hasSameShopId(shopId)) {
                throw new NotFoundException(new ErrorMessage(SHOP_MENU_CATEGORY_NOT_FOUND));
            }
        });

        // shop_menu_category_map 테이블에서 삭제할 관계(레코드)의 카테고리 id 리스트 = (기존 카테고리 id 리스트)에서 (요청된 카테고리 id)들을 제거한 리스트
        existingCategoryIds.removeAll(requestedCategoryIds);
        List<ShopMenuCategoryMap> menuCategoryMapsToDelete = existingCategoryIds.stream()
                .map(categoryId -> ShopMenuCategoryMap.create(menuId, categoryId))
                .collect(Collectors.toList());

        // shop_menu_category_map 테이블에 추가할 관계(레코드)의 카테고리 id 리스트 = (요청된 카테고리 id 리스트)에서 (기존 카테고리 id)들을 제거한 리스트
        requestedCategoryIds.removeAll(copiedExistingCategoryIds);
        List<ShopMenuCategoryMap> menuCategoryMapsToCreate = requestedCategoryIds.stream()
                .map(categoryId -> ShopMenuCategoryMap.create(menuId, categoryId))
                .collect(Collectors.toList());

        if (!menuCategoryMapsToDelete.isEmpty()) {
            shopMapper.deleteMenuCategoryMapsForAdmin(menuCategoryMapsToDelete);
        }
        if (!menuCategoryMapsToCreate.isEmpty()) {
            shopMapper.createMenuCategoryMapsForAdmin(menuCategoryMapsToCreate);
        }


        // ======= shop_menu_images 테이블 =======
        List<String> existingImageUrls = shopMapper.getMenuImageUrlsByMenuIdForAdmin(menuId);
        List<String> copiedExistingImageUrls = new ArrayList<>(existingImageUrls);

        List<String> requestedImageUrls = request.getImage_urls();

        // 삭제할 이미지 url 리스트 = (기존 이미지 url 리스트)에서 (요청된 이미지 url)들을 제거한 리스트
        existingImageUrls.removeAll(requestedImageUrls);
        List<ShopMenuImage> menuImagesToDelete = existingImageUrls.stream()
                .map(imageUrl -> ShopMenuImage.create(menuId, imageUrl))
                .collect(Collectors.toList());

        // 추가할 이미지 url 리스트 = (요청된 이미지 url 리스트)에서 (기존 이미지 url)들을 제거한 리스트
        requestedImageUrls.removeAll(copiedExistingImageUrls);
        List<ShopMenuImage> menuImagesToCreate = requestedImageUrls.stream()
                .map(imageUrl -> ShopMenuImage.create(menuId, imageUrl))
                .collect(Collectors.toList());

        if (!menuImagesToDelete.isEmpty()) {
            shopMapper.deleteMenuImagesForAdmin(menuImagesToDelete);
        }
        if (!menuImagesToCreate.isEmpty()) {
            shopMapper.createMenuImagesForAdmin(menuImagesToCreate);
        }
    }

    @Override
    @Transactional
    public void deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        ShopMenu menu = findMenu(menuId);

        if (!menu.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        shopMapper.deleteAllForInvolvedWithMenuForAdmin(menuId);
    }

    @Override
    @Transactional
    public void hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hidden) throws Exception {
        findShop(shopId);

        ShopMenu menu = findMenu(menuId);

        if (!menu.hasSameShopId(shopId)) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        if (hidden) {
            if (menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage(SHOP_MENU_ALREADY_HIDDEN));
            }

            shopMapper.hideMenuByIdForAdmin(menuId);
        } else {
            if (!menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage(SHOP_MENU_NOT_HIDDEN));
            }

            shopMapper.revealMenuByIdForAdmin(menuId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenusOfShopResponse getAllMenusOfShopForAdmin(Integer shopId) throws Exception {
        findShop(shopId); // 상점 존재 여부 체크

        List<AllMenusOfShopResponse.Menu> menus = shopQueryMapper.getMenusInAllMenusOfShopResponseForAdmin(shopId);

        AllMenusOfShopResponse response = AllMenusOfShopResponse.builder()
                .count(menus.size())
                .menus(menus)
                .build();

        response.decideWhetherSingleOrNotOfMenus();

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public koreatech.in.dto.shop.normal.response.ShopResponse getShop(Integer shopId) throws Exception {
        koreatech.in.dto.shop.normal.response.ShopResponse.Shop shop = shopQueryMapper.getShopInShopResponse(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_NOT_FOUND));
        }

        shop.decideWhetherSingleOrNotOfMenus();

        return koreatech.in.dto.shop.normal.response.ShopResponse.builder()
                .shop(shop)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopsResponse getAllShops() throws Exception {
        List<AllShopsResponse.Shop> shops = shopQueryMapper.getShopsInAllShopsResponse();

        return AllShopsResponse.builder()
                .total_count(shops.size())
                .shops(shops)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public koreatech.in.dto.shop.normal.response.AllShopCategoriesResponse getAllShopCategories() throws Exception {
        List<ShopCategory> allShopCategories = shopMapper.getAllShopCategories();

        return koreatech.in.dto.shop.normal.response.AllShopCategoriesResponse.from(allShopCategories);
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
        return shopMapper.getShopCategoryByNameForAdmin(name) != null;
    }

    private boolean isDuplicatedShopCategoryName(String name, Integer shopCategoryId) {
        ShopCategory shopCategory = shopMapper.getShopCategoryByNameForAdmin(name);

        return shopCategory != null && !shopCategory.hasSameId(shopCategoryId);
    }

    private boolean isDuplicatedShopName(String name) {
        return shopMapper.getShopByNameForAdmin(name) != null;
    }

    private boolean isDuplicatedShopName(String name, Integer shopId) {
        Shop shop = shopMapper.getShopByNameForAdmin(name);

        return shop != null && !shop.hasSameId(shopId);
    }

    private boolean isDuplicatedMenuCategoryName(String name, Integer shopId) {
        return shopMapper.getMenuCategoryByShopIdAndNameForAdmin(shopId, name) != null;
    }

    private ShopCategory findShopCategory(Integer shopCategoryId) {
        ShopCategory category = shopMapper.getShopCategoryByIdForAdmin(shopCategoryId);

        if (category == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_CATEGORY_NOT_FOUND));
        }

        return category;
    }

    private Shop findShop(Integer shopId) {
        Shop shop = shopMapper.getShopByIdForAdmin(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_NOT_FOUND));
        }

        return shop;
    }

    private ShopMenu findMenu(Integer menuId) {
        ShopMenu menu = shopMapper.getMenuByIdForAdmin(menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage(SHOP_MENU_NOT_FOUND));
        }

        return menu;
    }

    private List<ShopOpen> generateShopOpens(Integer shopId, List<Open> opens) throws Exception {
        if (opens.size() != 7) {
            throw new ValidationException(new ErrorMessage("open의 길이는 7이어야 합니다.", REQUEST_DATA_INVALID.getCode()));
        }

        List<DayOfWeek> expected = Arrays.asList(DayOfWeek.values());
        List<DayOfWeek> actual = new ArrayList<>();
        List<ShopOpen> shopOpens = new ArrayList<>();

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
            shopOpens.add(ShopOpen.create(shopId, dayOfWeek, closed, openTime, closeTime));
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
        List<String> imageUrls = new ArrayList<>();

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
