package koreatech.in.service;

import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.request.inner.Filter;
import koreatech.in.dto.shop.request.inner.Open;
import koreatech.in.dto.shop.response.*;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Shop.*;
import koreatech.in.dto.shop.response.inner.MinimizedShop;
import koreatech.in.exception.*;
import koreatech.in.repository.ShopMapper;
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
    public ResponseSuccessCreateDTO createShopCategoryForAdmin(CreateShopCategoryDTO dto) throws Exception {
        if (shopMapper.getShopCategoryByName(dto.getName()) != null) {
            throw new ConflictException(new ErrorMessage("이름이 중복되는 카테고리가 이미 존재합니다.", 1));
        }

        if (dto.getImage() == null) {
            throw new ValidationException(new ErrorMessage("image 업로드가 필요합니다.", 0));
        }

        String imageUrl = this.uploadImage(dto.getImage(), "upload/shop_categories", 0);

        ShopCategory newCategory = new ShopCategory(dto.getName(), imageUrl);
        shopMapper.createShopCategory(newCategory);

        return ResponseSuccessCreateDTO.builder()
                .id(newCategory.getId())
                .build();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO updateShopCategoryForAdmin(UpdateShopCategoryDTO dto) throws Exception {
        ShopCategory category = this.verifyShopCategoryExists(dto.getId(), 1);

        ShopCategory sameNameCategory = shopMapper.getShopCategoryByName(dto.getName());

        if (sameNameCategory != null && !dto.getId().equals(sameNameCategory.getId())) {
            throw new ConflictException(new ErrorMessage("이름이 중복되는 카테고리가 이미 존재합니다.", 2));
        }

        if (dto.getImage() == null) {
            throw new ValidationException(new ErrorMessage("image 업로드가 필요합니다.", 0));
        }

        String imageUrl = this.uploadImage(dto.getImage(), "upload/shop_categories", 0);
        shopMapper.updateShopCategory(category.update(dto.getName(), imageUrl));

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO deleteShopCategoryForAdmin(Integer shopCategoryId) throws Exception {
        this.verifyShopCategoryExists(shopCategoryId, 1);

        if (!shopMapper.getShopsUsingCategory(shopCategoryId).isEmpty()) {
            throw new PreconditionFailedException(new ErrorMessage("카테고리를 사용하고 있는 상점들이 있어 삭제할 수 없습니다.", 2));
        }

        shopMapper.deleteShopCategoryById(shopCategoryId);

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseAllShopCategoriesDTO getShopCategoriesForAdmin() throws Exception {
        List<koreatech.in.dto.shop.response.inner.ShopCategory> categories = shopMapper.getAllShopCategories();

        return ResponseAllShopCategoriesDTO.builder()
                .total_count(categories.size())
                .shop_categories(categories)
                .build();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO matchShopWithOwner(MatchShopWithOwnerDTO dto) throws Exception {
        Shop shop = this.verifyShopExistsByIgnoreDeletion(dto.getShop_id(), 1);

        // TODO: 사장님의 존재 여부 확인 + 회원가입 후 권한이 주어졌는지 확인

        shopMapper.updateShop(shop.matchOwnerId(dto.getOwner_id()));

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional
    public ResponseSuccessCreateDTO createShopForAdmin(CreateShopDTO dto) throws Exception {
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

        if (shopMapper.getShopByName(dto.getName()) != null) {
            throw new ConflictException(new ErrorMessage("중복되는 이름의 상점이 이미 존재합니다.", 1));
        }

        Integer ownerId = null; // TODO: 수정 필요

        // --- shops 테이블 ---
        Shop shop = new Shop(dto, ownerId);
        shopMapper.createShop(shop);

        // --- shop_opens 테이블 ---
        shopMapper.createShopOpens(this.generateShopOpens(shop.getId(), dto.getOpen()));

        // --- shop_category_map 테이블 ---
        List<ShopCategoryMap> shopCategoryMaps = new ArrayList<>();

        dto.getCategory_ids()
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

        // --- shop_images 테이블 ---
        if (!dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 10) {
                throw new ValidationException(new ErrorMessage("image는 10개 이하만 업로드 가능합니다.", 0));
            }

            shopMapper.createShopImages(this.generateShopImages(dto.getImages(), shop.getId()));
        }

        return ResponseSuccessCreateDTO.builder()
                .id(shop.getId())
                .build();
    }



    @Override
    @Transactional(readOnly = true)
    public ResponseShopDTO getShopForAdmin(Integer shopId) throws Exception {
        /*
             TODO: 사장님 권한으로 요청시 - getResponseShop() 호출
                   어드민 권한으로 요청시 - getResponseShopByIgnoreDeletionStatus() 호출
         */

        ResponseShopDTO shop = shopMapper.getResponseShopByIgnoreDeletion(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return shop.decideOptionalOfMenus();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO updateShopForAdmin(UpdateShopDTO dto) throws Exception {
        /*
             대상 테이블
             - shops
             - shop_open
             - shop_category_map
             - shop_images
         */

        Shop existingShop = this.verifyShopExistsByIgnoreDeletion(dto.getId(), 1);

        Shop sameNameShop = shopMapper.getShopByName(dto.getName());
        if (sameNameShop != null && !dto.getId().equals(sameNameShop.getId())) {
            throw new ConflictException(new ErrorMessage("중복되는 이름의 상점이 이미 존재합니다.", 2));
        }

        // --- shops 테이블 ---
        shopMapper.updateShop(existingShop.update(dto));

        // --- shop_opens 테이블 ---
        shopMapper.updateShopOpens(this.generateShopOpens(dto.getId(), dto.getOpen()));

        // --- shop_category_map 테이블 ---
        List<ShopCategory> existingCategories = shopMapper.getShopCategoriesOfShopByShopId(dto.getId()); // 상점이 속해있는 상점 카테고리 목록
        List<ShopCategoryMap> newShopCategoryMaps = new LinkedList<>();

        dto.getCategory_ids()
                .forEach(categoryId -> {
                    ShopCategory updatedCategory = shopMapper.getShopCategoryById(categoryId);

                    // 카테고리가 db에 존재하는지 검증
                    if (updatedCategory == null) {
                        throw new ValidationException(new ErrorMessage("category_ids에 유효하지 않은 값이 있습니다.", 0));
                    }

                    // 요청된 카테고리중 db에 존재하지 않던 카테고리는 새로운 insert 대상
                    if (!existingCategories.contains(updatedCategory)) {
                        newShopCategoryMaps.add(new ShopCategoryMap(dto.getId(), categoryId));
                    }

                    existingCategories.remove(updatedCategory);
                });

        if (!newShopCategoryMaps.isEmpty()) {
            shopMapper.createShopCategoryMaps(newShopCategoryMaps);
        }

        // 남아있는 existingCategories에 남아있는 카테고리는 삭제 대상
        if (!existingCategories.isEmpty()) {
            List<ShopCategoryMap> shopCategoryMapsToDelete = new LinkedList<>();
            existingCategories
                    .forEach(category -> {
                        shopCategoryMapsToDelete.add(new ShopCategoryMap(dto.getId(), category.getId()));
                    });
            shopMapper.deleteShopCategoryMaps(shopCategoryMapsToDelete);
        }

        // --- shop_images 테이블 ---
        shopMapper.deleteShopImagesByShopId(dto.getId());

        if (!dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 10) {
                throw new ValidationException(new ErrorMessage("image는 10개 이하만 업로드 가능합니다.", 0));
            }

            shopMapper.createShopImages(this.generateShopImages(dto.getImages(), dto.getId()));
        }

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO deleteShopForAdmin(Integer shopId) throws Exception {
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

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO undeleteOfShopForAdmin(Integer shopId) throws Exception {
        Shop shop = this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        if (!shop.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage("soft delete 되어있는 상점이 아닙니다.", 2));
        }

        shopMapper.undeleteShopById(shopId);

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopsDTO getShopsForAdmin(ShopsConditionDTO dto) throws Exception {
        Filter filter = dto.getFilter();
        Integer page = dto.getPage();
        Integer limit = dto.getLimit();

        if (filter != null) {
            if ((filter.getTrues() == null || filter.getTrues().isEmpty()) && filter.getCategory_id() == null) {
                throw new ValidationException(new ErrorMessage("filter가 올바르지 않습니다.", 0));
            }
        }

        dto.removeBlankOfSearchName();

        int totalCount = shopMapper.getTotalCountOfShopsByCondition(dto);

        /*
             (전체 상점 개수 / limit)를 올림한 자연수가 totalPage가 된다.
             만약 전체 상점 개수가 0일 경우라도 페이지는 존재해야 하므로, 그때는 totalPage를 1로 한다.
         */
        Integer totalPage = (totalCount == 0) ? 1 : (int) Math.ceil(((double)totalCount) / limit);

        if (page > totalPage || page < 1) {
            throw new ValidationException(new ErrorMessage("page가 유효하지 않습니다.", 0));
        }

        List<MinimizedShop> shops = shopMapper.getShopsByCondition(limit * page - limit, dto);

        return ResponseShopsDTO.builder()
                .total_page(totalPage)
                .current_page(page)
                .shops(shops)
                .build();
    }

    @Override
    @Transactional
    public ResponseSuccessCreateDTO createMenuCategoryForAdmin(CreateShopMenuCategoryDTO dto) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(dto.getShop_id(), 1);

        if (shopMapper.getMenuCategory(dto.getShop_id(), dto.getName()) != null) {
            throw new ConflictException(new ErrorMessage("중복되는 이름의 카테고리가 이미 존재합니다.", 2));
        }

        if (shopMapper.getCountOfShopCategoriesByShopId(dto.getShop_id()).equals(20)) {
            throw new PreconditionFailedException(new ErrorMessage("메뉴 카테고리는 최대 20개까지 설정 가능합니다.", 3));
        }

        ShopMenuCategory newMenuCategory = new ShopMenuCategory(dto.getShop_id(), dto.getName());
        shopMapper.createMenuCategory(newMenuCategory);

        return ResponseSuccessCreateDTO.builder()
                .id(newMenuCategory.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopMenuCategoriesDTO getMenuCategoriesForAdmin(Integer shopId) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        List<koreatech.in.dto.shop.response.inner.ShopMenuCategory> menuCategories = shopMapper.getMenuCategoriesOfShop(shopId);

        return ResponseShopMenuCategoriesDTO.builder()
                .shop_id(shopId)
                .count(menuCategories.size())
                .menu_categories(menuCategories)
                .build();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO deleteMenuCategoryForAdmin(Integer shopId, Integer menuCategoryId) throws Exception {
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

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional
    public ResponseSuccessCreateDTO createMenuForAdmin(CreateShopMenuDTO dto) throws Exception {
        /*
             대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        this.verifyShopExistsByIgnoreDeletion(dto.getShop_id(), 1);

        // --- shop_menus 테이블 ---
        ShopMenu menu = new ShopMenu(dto);
        shopMapper.createMenu(menu);

        // --- shop_menu_details 테이블 ---
        if (dto.getIs_single()) {
            if (dto.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", 0));
            }
            shopMapper.createMenuDetail(new ShopMenuDetail(menu.getId(), dto.getSingle_price()));
        } else {
            if (dto.getOption_prices() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 false이면 option_prices는 필수입니다.", 0));
            }
            if (dto.existOfOptionDuplicate()) {
                throw new ValidationException(new ErrorMessage("option_prices에서 중복되는 option이 있습니다.", 0));
            }

            List<ShopMenuDetail> menuDetails = new LinkedList<>();

            dto.getOption_prices()
                    .forEach(optionPrice -> {
                        String option = optionPrice.getOption();
                        Integer price = optionPrice.getPrice();

                        if (option.length() > 12) {
                            throw new ValidationException(new ErrorMessage("option_prices의 option은 12자 이하입니다.", 0));
                        }
                        if (price == null) {
                            throw new ValidationException(new ErrorMessage("option_prices의 price는 필수입니다.", 0));
                        }

                        menuDetails.add(new ShopMenuDetail(menu.getId(), option, price));
                    });

            shopMapper.createMenuDetails(menuDetails);
        }

        // --- shop_menu_images 테이블 ---
        if (!dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 3) {
                throw new ValidationException(new ErrorMessage("image는 3개 이하만 업로드 가능합니다.", 0));
            }

            shopMapper.createMenuImages(this.generateMenuImages(dto.getImages(), menu.getId()));
        }

        // --- shop_menu_category_map 테이블 ---
        List<ShopMenuCategoryMap> shopMenuCategoryMaps = new LinkedList<>();

        dto.getCategory_ids()
                .forEach(categoryId -> {
                    ShopMenuCategory category = shopMapper.getMenuCategoryById(categoryId);

                    // 카테고리가 db에 존재하는지, 해당 상점의 메뉴 카테고리가 맞는지 검증
                    if (category == null || !category.getShop_id().equals(dto.getShop_id())) {
                        throw new ValidationException(new ErrorMessage("category_ids에 유효하지 않은 값이 있습니다.", 0));
                    }

                    shopMenuCategoryMaps.add(new ShopMenuCategoryMap(menu.getId(), categoryId));
                });

        shopMapper.createMenuCategoryMaps(shopMenuCategoryMaps);

        return ResponseSuccessCreateDTO.builder()
                .id(menu.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopMenuDTO getMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        ResponseShopMenuDTO menu = shopMapper.getResponseMenu(menuId);
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
    public ResponseSuccessfulDTO updateMenuForAdmin(UpdateShopMenuDTO dto) throws Exception {
        /*
             대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        this.verifyShopExistsByIgnoreDeletion(dto.getShop_id(), 1);

        ShopMenu existingMenu = this.verifyMenuExists(dto.getId(), 2);

        if (!existingMenu.getShop_id().equals(dto.getShop_id())) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        // --- shop_menus 테이블 ---
        ShopMenu updatedMenu = new ShopMenu(dto);
        if (!updatedMenu.equals(existingMenu)) {
            updatedMenu.setIs_hidden(existingMenu.getIs_hidden());
            shopMapper.updateMenu(updatedMenu);
        }

        // --- shop_menu_details 테이블 ---
        List<ShopMenuDetail> existingMenuDetails = shopMapper.getMenuDetailsByMenuId(dto.getId());

        if (dto.getIs_single()) {
            if (dto.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 true이면 single_price는 필수입니다.", 0));
            }

            ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(dto.getId(), dto.getSingle_price());

            if (existingMenuDetails.contains(updatedMenuDetail)) {
                existingMenuDetails.remove(updatedMenuDetail);
                if (!existingMenuDetails.isEmpty()) {
                    shopMapper.deleteMenuDetails(existingMenuDetails);
                }
            } else {
                shopMapper.deleteMenuDetailsByMenuId(dto.getId());
                shopMapper.createMenuDetail(updatedMenuDetail);
            }
        } else {
            if (dto.getOption_prices() == null) {
                throw new ValidationException(new ErrorMessage("is_single이 false이면 option_prices를 비워둘 수 없습니다.", 0));
            }
            if (dto.existOfOptionDuplicate()) {
                throw new ValidationException(new ErrorMessage("option_prices에서 중복되는 option이 있습니다.", 0));
            }

            List<ShopMenuDetail> newMenuDetails = new LinkedList<>();

            dto.getOption_prices()
                    .forEach(optionPrice -> {
                        String option = optionPrice.getOption();
                        Integer price = optionPrice.getPrice();

                        if (option.length() > 12) {
                            throw new ValidationException(new ErrorMessage("option_prices의 option은 12자 이하입니다.", 0));
                        }
                        if (price == null) {
                            throw new ValidationException(new ErrorMessage("option_prices의 price는 필수입니다.", 0));
                        }

                        ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(dto.getId(), option, price);

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

        // --- shop_menu_images 테이블 ---
        shopMapper.deleteMenuImagesByMenuId(dto.getId());

        if (!dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 3) {
                throw new ValidationException(new ErrorMessage("image는 3개 이하만 업로드 가능합니다.", 0));
            }

            shopMapper.createMenuImages(this.generateMenuImages(dto.getImages(), dto.getId()));
        }

        // --- shop_menu_category_map 테이블 ---
        List<ShopMenuCategory> existingCategories = shopMapper.getMenuCategoriesOfMenu(dto.getId());
        List<ShopMenuCategoryMap> newMenuCategoryMaps = new LinkedList<>();

        dto.getCategory_ids()
                .forEach(categoryId -> {
                    ShopMenuCategory updatedMenuCategory = shopMapper.getMenuCategoryById(categoryId);

                    // 카테고리가 db에 존재하는지, 해당 상점의 메뉴 카테고리가 맞는지 검증
                    if (updatedMenuCategory == null || !updatedMenuCategory.getShop_id().equals(dto.getShop_id())) {
                        throw new NotFoundException(new ErrorMessage("category_ids에 유효하지 않은 값이 있습니다.", 0));
                    }

                    // 새로 선택된 카테고리
                    if (!existingCategories.contains(updatedMenuCategory)) {
                        newMenuCategoryMaps.add(new ShopMenuCategoryMap(dto.getId(), updatedMenuCategory.getId()));
                    }

                    existingCategories.remove(updatedMenuCategory);
                });

        if (!newMenuCategoryMaps.isEmpty()) {
            shopMapper.createMenuCategoryMaps(newMenuCategoryMaps);
        }
        if (!existingCategories.isEmpty()) {
            shopMapper.deleteMenuCategoryMaps(existingCategories.stream()
                    .map(category -> new ShopMenuCategoryMap(dto.getId(), category.getId()))
                    .collect(Collectors.toCollection(LinkedList::new)));
        }

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        this.verifyShopExistsByIgnoreDeletion(shopId, 1);

        ShopMenu menu = this.verifyMenuExists(menuId, 2);

        if (!menu.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        shopMapper.deleteAllForInvolvedWithMenu(menuId);

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional
    public ResponseSuccessfulDTO hideMenuForAdmin(Integer shopId, Integer menuId, Boolean hide) throws Exception {
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

        return new ResponseSuccessfulDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopMenusDTO getMenusForAdmin(Integer shopId) throws Exception {
        ResponseShopMenusDTO menus = shopMapper.getResponseMenus(shopId);

        if (menus == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return menus.decideOptionalOfMenus();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopDTO getShop(Integer shopId) throws Exception {
        ResponseShopDTO shop = shopMapper.getResponseShop(shopId);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return shop.decideOptionalOfMenus();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseAllShopsDTO getShops() throws Exception {
        List<koreatech.in.dto.shop.response.inner.Shop> shops = shopMapper.getAllShops();

        return ResponseAllShopsDTO.builder()
                .total_count(shops.size())
                .shops(shops)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseAllShopCategoriesDTO getShopCategories() throws Exception {
        List<koreatech.in.dto.shop.response.inner.ShopCategory> categories = shopMapper.getAllShopCategories();

        return ResponseAllShopCategoriesDTO.builder()
                .total_count(categories.size())
                .shop_categories(categories)
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

    private List<ShopImage> generateShopImages(List<MultipartFile> images, Integer shopId) throws Exception {
        List<ShopImage> shopImages = new LinkedList<>();

        for (MultipartFile image : images) {
            shopImages.add(new ShopImage(shopId, this.uploadImage(image, "upload/shops", 0)));
        }

        return shopImages;
    }

    private List<ShopMenuImage> generateMenuImages(List<MultipartFile> images, Integer menuId) throws Exception {
        List<ShopMenuImage> menuImages = new LinkedList<>();

        for (MultipartFile image : images) {
            menuImages.add(new ShopMenuImage(menuId, this.uploadImage(image, "upload/shop_menus", 0)));
        }

        return menuImages;
    }

    /**
     *
     * @param file
     * @param bucketPath 업로드할 경로 (ex. upload/shop_menus)
     * @param errorCode (이미지 형식이 아닐때 Exception throw 에서의 error code)
     * @return AWS S3 버킷에 업로드된 이미지 URL
     * @throws Exception
     */
    private String uploadImage(MultipartFile file, String bucketPath, Integer errorCode) throws Exception {
        if (!file.getContentType().startsWith("image/")) {
            throw new PreconditionFailedException(new ErrorMessage("이미지 형식 파일만 업로드 가능합니다.", errorCode));
        }

        String imagePath = uploadFileUtils.uploadFile(bucketPath, file.getOriginalFilename(), file.getBytes());
        return uploadFileUtils.getDomain() + "/" + bucketPath + imagePath;
    }
}
