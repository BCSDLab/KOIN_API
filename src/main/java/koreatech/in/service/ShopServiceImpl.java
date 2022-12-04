package koreatech.in.service;

import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.request.inner.Open;
import koreatech.in.dto.shop.response.*;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Shop.*;
import koreatech.in.dto.shop.response.inner.MinimizedShop;
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
        boolean duplicateOfName = (shopMapper.getShopCategoryByName(request.getName()) != null);

        if (duplicateOfName) {
            throw new ConflictException(new ErrorMessage("이름이 중복되는 카테고리가 이미 존재합니다.", 1));
        }

        ShopCategory newCategory = new ShopCategory(request);
        shopMapper.createShopCategory(newCategory);

        return SuccessCreateResponse.builder()
                .id(newCategory.getId())
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse updateShopCategoryForAdmin(Integer shopCategoryId, UpdateShopCategoryRequest request) throws Exception {
        ShopCategory existingCategory = this.verifyShopCategoryExists(shopCategoryId, 1);

        ShopCategory sameNameCategory = shopMapper.getShopCategoryByName(request.getName());
        boolean duplicateOfName = sameNameCategory != null && !shopCategoryId.equals(sameNameCategory.getId());
        if (duplicateOfName) {
            throw new ConflictException(new ErrorMessage("이름이 중복되는 카테고리가 이미 존재합니다.", 2));
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
        this.verifyShopCategoryExists(shopCategoryId, 1);

        if (!shopMapper.getShopsUsingCategory(shopCategoryId).isEmpty()) {
            throw new PreconditionFailedException(new ErrorMessage("카테고리를 사용하고 있는 상점들이 있어 삭제할 수 없습니다.", 2));
        }

        shopMapper.deleteShopCategoryById(shopCategoryId);

        return new SuccessResponse();
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopCategoriesResponse getAllShopCategoriesForAdmin() throws Exception {
        List<koreatech.in.dto.shop.response.inner.ShopCategory> categories = shopMapper.getAllShopCategories();

        return AllShopCategoriesResponse.builder()
                .total_count(categories.size())
                .shop_categories(categories)
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse matchShopWithOwner(Integer shopId, MatchShopWithOwnerRequest request) throws Exception {
        Shop shop = this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        // TODO: 사장님의 존재 여부 확인 + 회원가입 후 권한이 주어졌는지 확인

        shopMapper.updateShop(shop.matchOwnerId(request.getOwner_id()));

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessCreateResponse createShopForAdmin(CreateShopRequest request) throws Exception {
        /*
             대상 테이블
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

        if (shopMapper.getShopByName(request.getName()) != null) {
            throw new ConflictException(new ErrorMessage("중복되는 이름의 상점이 이미 존재합니다.", 1));
        }

        Integer ownerId = null; // TODO: 수정 필요

        // --- shops 테이블 ---
        Shop shop = new Shop(request, ownerId);
        shopMapper.createShop(shop);

        // --- shop_opens 테이블 ---
        shopMapper.createShopOpens(this.generateShopOpens(shop.getId(), request.getOpen()));

        // --- shop_category_map 테이블 ---
        List<ShopCategoryMap> shopCategoryMaps = new ArrayList<>();

        request.getCategory_ids()
                .forEach(categoryId -> {
                    ShopCategory category = shopMapper.getShopCategoryById(categoryId);

                    // 카테고리가 db에 존재하는지 검증
                    if (category == null) {
                        throw new ValidationException(new ErrorMessage("category_ids에 유효하지 않은 값이 들어있습니다.", 0));
                    }

                    shopCategoryMaps.add(new ShopCategoryMap(shop.getId(), categoryId));
                });

        shopMapper.createShopCategoryMaps(shopCategoryMaps);

        // --- shop_menu_categories 테이블 ---
        /*
            이벤트 메뉴, 대표 메뉴, 사이드 메뉴, 세트 메뉴는 기본적으로 존재하는 메뉴 카테고리이다.
            기본 메뉴 카테고리가 변경될시 수정 필요.
         */
        shopMapper.createMenuCategories(
                this.generateMenuCategories(shop.getId(), new String[]{"이벤트 메뉴", "대표 메뉴", "사이드 메뉴", "세트 메뉴"})
        );


        // ======= shop_images 테이블 =======
        if (!request.getImage_urls().isEmpty()) {
            List<ShopImage> shopImages = generateShopImages(request.getImage_urls(), shop.getId());
            shopMapper.createShopImages(shopImages);
        }


        return SuccessCreateResponse.builder()
                .id(shop.getId())
                .build();
    }



    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShopForAdmin(Integer shopId) throws Exception {
        /*
             TODO: 사장님 권한으로 요청시 - getResponseShop() 호출
                   어드민 권한으로 요청시 - getResponseShopByIgnoreDeletionStatus() 호출
         */

        ShopResponse shop = shopMapper.getResponseShopByIgnoreDeletion(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return shop.decideOptionalOfMenus();
    }

    @Override
    @Transactional
    public SuccessResponse updateShopForAdmin(Integer shopId, UpdateShopRequest request) throws Exception {
        /*
             대상 테이블
             - shops
             - shop_open
             - shop_category_map
             - shop_images
         */

        Shop existingShop = this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        Shop sameNameShop = shopMapper.getShopByName(request.getName());
        if (sameNameShop != null && !shopId.equals(sameNameShop.getId())) {
            throw new ConflictException(new ErrorMessage("중복되는 이름의 상점이 이미 존재합니다.", 2));
        }

        // --- shops 테이블 ---
        shopMapper.updateShop(existingShop.update(request));

        // --- shop_opens 테이블 ---
        shopMapper.updateShopOpens(this.generateShopOpens(shopId, request.getOpen()));

        // --- shop_category_map 테이블 ---
        List<ShopCategory> existingCategories = shopMapper.getShopCategoriesOfShopByShopId(shopId); // 상점이 속해있는 상점 카테고리 목록
        List<ShopCategoryMap> newShopCategoryMaps = new LinkedList<>();

        request.getCategory_ids()
                .forEach(categoryId -> {
                    ShopCategory updatedCategory = shopMapper.getShopCategoryById(categoryId);

                    // 카테고리가 db에 존재하는지 검증
                    if (updatedCategory == null) {
                        throw new ValidationException(new ErrorMessage("category_ids에 유효하지 않은 값이 있습니다.", 0));
                    }

                    // 요청된 카테고리중 db에 존재하지 않던 카테고리는 새로운 insert 대상
                    if (!existingCategories.contains(updatedCategory)) {
                        newShopCategoryMaps.add(new ShopCategoryMap(shopId, categoryId));
                    }

                    existingCategories.remove(updatedCategory);
                });

        if (!newShopCategoryMaps.isEmpty()) {
            shopMapper.createShopCategoryMaps(newShopCategoryMaps);
        }

        if (!existingCategories.isEmpty()) {
            List<ShopCategoryMap> shopCategoryMapsToDelete = new LinkedList<>();
            existingCategories
                    .forEach(category -> {
                        shopCategoryMapsToDelete.add(new ShopCategoryMap(shopId, category.getId()));
                    });
            shopMapper.deleteShopCategoryMaps(shopCategoryMapsToDelete);
        }


        // ======= shop_images 테이블 =======
        List<String> existingImageUrls = shopMapper.getShopImageUrlsByShopId(shopId);

        List<ShopImage> shopImagesToCreate = new LinkedList<>();
        List<ShopImage> shopImagesToDelete;

        request.getImage_urls().forEach(imageUrl -> {
            if (!existingImageUrls.contains(imageUrl)) {
                shopImagesToCreate.add(new ShopImage(shopId, imageUrl));
            }

            existingImageUrls.remove(imageUrl);
        });

        shopMapper.createShopImages(shopImagesToCreate);

        // existingImageUrls 리스트에 남아있는 url: 기존에는 있었지만 요청에는 없음 --> 삭제 대상
        shopImagesToDelete = existingImageUrls.stream()
                .map(imageUrl -> new ShopImage(shopId, imageUrl))
                .collect(Collectors.toCollection(LinkedList::new));

        shopMapper.deleteShopImages(shopImagesToDelete);


        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse deleteShopForAdmin(Integer shopId) throws Exception {
        Shop shop = this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        if (shop.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage("이미 soft delete 되어있는 상점입니다.", 2));
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
        Shop shop = this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        if (!shop.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage("soft delete 되어있는 상점이 아닙니다.", 2));
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
            throw new ValidationException(new ErrorMessage("page가 유효하지 않습니다.", 0));
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
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        if (shopMapper.getMenuCategory(shopId, request.getName()) != null) {
            throw new ConflictException(new ErrorMessage("중복되는 이름의 카테고리가 이미 존재합니다.", 2));
        }

        if (shopMapper.getCountOfShopCategoriesByShopId(shopId).equals(20)) {
            throw new PreconditionFailedException(new ErrorMessage("메뉴 카테고리는 최대 20개까지 설정 가능합니다.", 3));
        }

        ShopMenuCategory newMenuCategory = new ShopMenuCategory(shopId, request.getName());
        shopMapper.createMenuCategory(newMenuCategory);

        return SuccessCreateResponse.builder()
                .id(newMenuCategory.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllMenuCategoriesResponse getAllMenuCategoriesOfShopForAdmin(Integer shopId) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        List<koreatech.in.dto.shop.response.inner.ShopMenuCategory> menuCategories = shopMapper.getMenuCategoriesOfShop(shopId);

        return AllMenuCategoriesResponse.builder()
                .shop_id(shopId)
                .count(menuCategories.size())
                .menu_categories(menuCategories)
                .build();
    }

    @Override
    @Transactional
    public SuccessResponse deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        ShopMenuCategory category = shopMapper.getMenuCategoryById(menuCategoryId);

        if (category == null) {
            throw new NotFoundException(new ErrorMessage("메뉴 카테고리가 존재하지 않습니다.", 2));
        }
        if (!category.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        if (!shopMapper.getMenusUsingCategory(menuCategoryId).isEmpty()) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "카테고리를 사용하고 있는 메뉴들이 있어 삭제할 수 없습니다.", 3
            ));
        }

        shopMapper.deleteMenuCategoryById(menuCategoryId);

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessCreateResponse createMenuForAdmin(Integer shopId, CreateShopMenuRequest request) throws Exception {
        /*
             대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        // --- shop_menus 테이블 ---
        ShopMenu menu = new ShopMenu(shopId, request);
        shopMapper.createMenu(menu);

        // --- shop_menu_details 테이블 ---
        if (request.getIs_single()) {
            if (request.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", 0));
            }
            shopMapper.createMenuDetail(new ShopMenuDetail(menu.getId(), request.getSingle_price()));
        } else {
            if (request.getOption_prices() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 false이면 option_prices는 필수입니다.", 0));
            }
            if (request.existOfOptionDuplicate()) {
                throw new ValidationException(new ErrorMessage("option_prices에서 중복되는 option이 있습니다.", 0));
            }

            List<ShopMenuDetail> menuDetails = new LinkedList<>();

            request.getOption_prices()
                    .forEach(optionPrice -> {
                        String option = optionPrice.getOption();
                        Integer price = optionPrice.getPrice();

                        menuDetails.add(new ShopMenuDetail(menu.getId(), option, price));
                    });

            shopMapper.createMenuDetails(menuDetails);
        }

        // ======= shop_menu_images 테이블 =======
        if (!request.getImage_urls().isEmpty()) {
            List<ShopMenuImage> menuImages = generateMenuImages(request.getImage_urls(), menu.getId());
            shopMapper.createMenuImages(menuImages);
        }


        // --- shop_menu_category_map 테이블 ---
        List<ShopMenuCategoryMap> shopMenuCategoryMaps = new LinkedList<>();

        request.getCategory_ids()
                .forEach(categoryId -> {
                    ShopMenuCategory category = shopMapper.getMenuCategoryById(categoryId);

                    // 카테고리가 db에 존재하는지, 해당 상점의 메뉴 카테고리가 맞는지 검증
                    if (category == null || !category.getShop_id().equals(shopId)) {
                        throw new ValidationException(new ErrorMessage("category_ids에 유효하지 않은 값이 있습니다.", 0));
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
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        MenuResponse menu = shopMapper.getResponseMenu(menuId);
        if (menu == null) {
            throw new NotFoundException(new ErrorMessage("존재하지 않는 메뉴입니다.", 2));
        }
        if (!menu.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        return menu.decideSingleOrNot();
    }

    @Override
    @Transactional
    public SuccessResponse updateMenuForAdmin(Integer shopId, Integer menuId, UpdateShopMenuRequest request) throws Exception {
        /*
             대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        ShopMenu existingMenu = this.verifyMenuExists(menuId, 2);

        if (!existingMenu.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        // --- shop_menus 테이블 ---
        ShopMenu updatedMenu = new ShopMenu(shopId, menuId, request);
        if (!updatedMenu.equals(existingMenu)) {
            updatedMenu.setIs_hidden(existingMenu.getIs_hidden());
            shopMapper.updateMenu(updatedMenu);
        }

        // --- shop_menu_details 테이블 ---
        List<ShopMenuDetail> existingMenuDetails = shopMapper.getMenuDetailsByMenuId(menuId);

        if (request.getIs_single()) {
            if (request.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", 0));
            }

            ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(menuId, request.getSingle_price());

            if (existingMenuDetails.contains(updatedMenuDetail)) {
                existingMenuDetails.remove(updatedMenuDetail);
                if (!existingMenuDetails.isEmpty()) {
                    shopMapper.deleteMenuDetails(existingMenuDetails);
                }
            } else {
                shopMapper.deleteMenuDetailsByMenuId(menuId);
                shopMapper.createMenuDetail(updatedMenuDetail);
            }
        } else {
            if (request.getOption_prices() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 false이면 option_prices를 비워둘 수 없습니다.", 0));
            }
            if (request.existOfOptionDuplicate()) {
                throw new ValidationException(new ErrorMessage("option_prices에서 중복되는 option이 있습니다.", 0));
            }

            List<ShopMenuDetail> newMenuDetails = new LinkedList<>();

            request.getOption_prices()
                    .forEach(optionPrice -> {
                        String option = optionPrice.getOption();
                        Integer price = optionPrice.getPrice();

                        if (option.length() > 12) {
                            throw new ValidationException(new ErrorMessage("option_prices의 option은 12자 이하입니다.", 0));
                        }
                        if (price == null) {
                            throw new ValidationException(new ErrorMessage("option_prices의 price는 필수입니다.", 0));
                        }

                        ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(menuId, option, price);

                        // 추가할(기존에 없는) menu detail
                        if (!existingMenuDetails.contains(updatedMenuDetail)) {
                            newMenuDetails.add(updatedMenuDetail);
                        }

                        existingMenuDetails.remove(updatedMenuDetail);
                    });

            if (!newMenuDetails.isEmpty()) {
                shopMapper.createMenuDetails(newMenuDetails);
            }
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
                menuImagesToCreate.add(new ShopMenuImage(menuId, imageUrl));
            }

            existingImageUrls.remove(imageUrl);
        });

        shopMapper.createMenuImages(menuImagesToCreate);

        // existingImageUrls 리스트에 남아있는 url: 기존에는 있었지만 요청에는 없음 --> 삭제 대상
        menuImagesToDelete = existingImageUrls.stream()
                .map(imageUrl -> new ShopMenuImage(menuId, imageUrl))
                .collect(Collectors.toCollection(LinkedList::new));

        shopMapper.deleteMenuImages(menuImagesToDelete);


        // --- shop_menu_category_map 테이블 ---
        List<ShopMenuCategory> existingCategories = shopMapper.getMenuCategoriesOfMenu(menuId);
        List<ShopMenuCategoryMap> newMenuCategoryMaps = new LinkedList<>();

        request.getCategory_ids()
                .forEach(categoryId -> {
                    ShopMenuCategory updatedMenuCategory = shopMapper.getMenuCategoryById(categoryId);

                    // 카테고리가 db에 존재하는지, 해당 상점의 메뉴 카테고리가 맞는지 검증
                    if (updatedMenuCategory == null || !updatedMenuCategory.getShop_id().equals(shopId)) {
                        throw new NotFoundException(new ErrorMessage("category_ids에 유효하지 않은 값이 있습니다.", 0));
                    }

                    // 새로 선택된 카테고리
                    if (!existingCategories.contains(updatedMenuCategory)) {
                        newMenuCategoryMaps.add(new ShopMenuCategoryMap(menuId, updatedMenuCategory.getId()));
                    }

                    existingCategories.remove(updatedMenuCategory);
                });

        if (!newMenuCategoryMaps.isEmpty()) {
            shopMapper.createMenuCategoryMaps(newMenuCategoryMaps);
        }
        if (!existingCategories.isEmpty()) {
            shopMapper.deleteMenuCategoryMaps(existingCategories.stream()
                    .map(category -> new ShopMenuCategoryMap(menuId, category.getId()))
                    .collect(Collectors.toCollection(LinkedList::new)));
        }

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        ShopMenu menu = this.verifyMenuExists(menuId, 2);

        if (!menu.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        shopMapper.deleteAllForInvolvedWithMenu(menuId);

        return new SuccessResponse();
    }

    @Override
    @Transactional
    public SuccessResponse hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hide) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        ShopMenu menu = this.verifyMenuExists(menuId, 2);

        if (!menu.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        if (hide) {
            if (menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage("이미 숨김 처리된 메뉴입니다.", 3));
            }
        } else {
            if (!menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage("이미 숨김 메뉴가 아닙니다.", 4));
            }
        }

        shopMapper.updateMenu(menu.reverseIsHidden());

        return new SuccessResponse();
    }

    @Override
    @Transactional(readOnly = true)
    public MenusResponse getAllMenusOfShopForAdmin(Integer shopId) throws Exception {
        MenusResponse menus = shopMapper.getResponseMenus(shopId);

        if (menus == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return menus.decideOptionalOfMenus();
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShop(Integer shopId) throws Exception {
        ShopResponse shop = shopMapper.getResponseShop(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return shop.decideOptionalOfMenus();
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopsResponse getAllShops() throws Exception {
        List<koreatech.in.dto.shop.response.inner.Shop> shops = shopMapper.getAllShops();

        return AllShopsResponse.builder()
                .total_count(shops.size())
                .shops(shops)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AllShopCategoriesResponse getAllShopCategories() throws Exception {
        List<koreatech.in.dto.shop.response.inner.ShopCategory> categories = shopMapper.getAllShopCategories();

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

    private ShopCategory verifyShopCategoryExists(Integer shopCategoryId, Integer errorCode) {
        ShopCategory category = shopMapper.getShopCategoryById(shopCategoryId);

        if (category == null) {
            throw new NotFoundException(new ErrorMessage("상점 카테고리가 존재하지 않습니다.", errorCode));
        }

        return category;
    }

    private Shop verifyShopExists(Integer shopId, Integer errorCode) {
        Shop shop = shopMapper.getShopById(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", errorCode));
        }

        return shop;
    }

    private Shop verifyShopExistsByIgnoreDeletion(Integer shopId, Integer errorCode) {
        Shop shop = shopMapper.getShopByIgnoreDeletionStatus(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", errorCode));
        }

        return shop;
    }

    private ShopMenu verifyMenuExists(Integer menuId, Integer errorCode) {
        ShopMenu menu = shopMapper.getMenuById(menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage("존재하지 않는 메뉴입니다.", errorCode));
        }

        return menu;
    }

    private List<ShopMenuCategory> generateMenuCategories(Integer shopId, String[] names) throws Exception {
        List<ShopMenuCategory> menuCategories = new LinkedList<>();

        Arrays.stream(names)
                .forEach(name -> {
                    menuCategories.add(new ShopMenuCategory(shopId, name));
                });

        return menuCategories;
    }

    private List<ShopOpen> generateShopOpens(Integer shopId, List<Open> opens) throws Exception {
        if (opens.size() != 7) {
            throw new PreconditionFailedException(new ErrorMessage("open의 길이는 7이어야 합니다.", 0));
        }

        List<DayOfWeek> expected = Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        List<DayOfWeek> actual = new ArrayList<>();
        List<ShopOpen> shopOpens = new LinkedList<>();

        opens.forEach(open -> {
            DayOfWeek dayOfWeek = open.getDay_of_week();
            Boolean closed = open.getClosed();
            String openTime = open.getOpen_time();
            String closeTime = open.getClose_time();

            if (!closed) {
                if (openTime == null || closeTime == null) {
                    throw new ValidationException(new ErrorMessage(
                            "open의 closed가 false이면 open_time과 close_time은 필수입니다.", 0
                    ));
                }
            }

            actual.add(dayOfWeek);
            shopOpens.add(new ShopOpen(shopId, dayOfWeek, closed, openTime, closeTime));
        });

        Collections.sort(actual);

        if (!expected.equals(actual)) {
            throw new ValidationException(new ErrorMessage("open에 올바르지 않은 값이 들어있습니다.", 0));
        }

        return shopOpens;
    }

    private List<ShopImage> generateShopImages(List<String> imageUrls, Integer shopId) throws Exception {
        List<ShopImage> shopImages = new LinkedList<>();

        imageUrls.forEach(imageUrl -> {
            shopImages.add(new ShopImage(shopId, imageUrl));
        });

        return shopImages;
    }

    private List<ShopMenuImage> generateMenuImages(List<String> imageUrls, Integer menuId) throws Exception {
        List<ShopMenuImage> menuImages = new LinkedList<>();

        imageUrls.forEach(imageUrl -> {
            menuImages.add(new ShopMenuImage(menuId, imageUrl));
        });

        return menuImages;
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
