package koreatech.in.service;

import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.response.*;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Shop.*;
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
    public Map<String, Object> createShopCategoryForAdmin(CreateShopCategoryDTO dto) throws Exception {
        if (shopMapper.getShopCategoryByName(dto.getName()) != null) {
            throw new ConflictException(new ErrorMessage("이름이 중복되는 카테고리가 있습니다.", 1));
        }

        if (dto.getImage() == null) {
            throw new PreconditionFailedException(new ErrorMessage("이미지 업로드가 필요합니다.", 2));
        }

        String imgPath = uploadFileUtils.uploadFile(
                "upload/shop/categories",
                dto.getImage().getOriginalFilename(),
                dto.getImage().getBytes());
        String imageUrl = uploadFileUtils.getDomain() + "/upload/shop/categories" + imgPath;

        ShopCategory newCategory = new ShopCategory(dto.getName(), imageUrl);
        shopMapper.createShopCategory(newCategory);

        return new HashMap<String, Object>() {{
            put("id", newCategory.getId());
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> updateShopCategoryForAdmin(UpdateShopCategoryDTO dto) throws Exception {
        ShopCategory category = shopMapper.getShopCategoryById(dto.getId());
        if (category == null) {
            throw new NotFoundException(new ErrorMessage("없는 카테고리입니다.", 1));
        }

        if (dto.getImage() == null) {
            throw new PreconditionFailedException(new ErrorMessage("이미지 업로드가 필요합니다.", 2));
        }

        String imgPath = uploadFileUtils.uploadFile(
                "upload/shop/categories",
                dto.getImage().getOriginalFilename(),
                dto.getImage().getBytes());
        String imageUrl = uploadFileUtils.getDomain() + "/upload/shop/categories" + imgPath;

        shopMapper.updateShopCategory(category.update(dto.getName(), imageUrl));

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteShopCategoryForAdmin(Integer id) throws Exception {
        if (shopMapper.getShopCategoryById(id) == null) {
            throw new NotFoundException(new ErrorMessage("없는 카테고리입니다.", 1));
        }

        List<Shop> shopsUsingCategory = shopMapper.getShopsUsingCategory(id);
        if (!shopsUsingCategory.isEmpty()) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "카테고리를 사용하고 있는 상점들이 있어 삭제할 수 없습니다.", 2
            ));
        }

        shopMapper.deleteShopCategoryById(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
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
    public Map<String, Object> createShopForAdmin(CreateShopDTO dto) throws Exception {
        /*
             대상 테이블
             - shops
             - shop_opens
             - shop_category_map
             - shop_menu_categories
             - shop_images
         */

        // TODO: 사장님인지 어드민인지 체크해서 owner_id 얻기

        if (shopMapper.getShopByName(dto.getName()) != null) {
            throw new ConflictException(new ErrorMessage("이름이 중복되는 상점이 있습니다.", 0));
        }


        // --- shops table ---
        Shop shop = new Shop(dto);
        shopMapper.createShop(shop);


        // --- shop_opens table ---
        shopMapper.createShopOpens(generateShopOpens(shop.getId(), dto.getOpen()));


        // --- shop_category_map table ---
        if (dto.getCategory_ids().isEmpty()) {
            throw new PreconditionFailedException(new ErrorMessage("category_ids의 size는 1 이상이어야 합니다.", -1));
        }

        List<ShopCategoryMap> shopCategoryMaps = new ArrayList<>();

        dto.getCategory_ids()
                .forEach(categoryId -> {
                    ShopCategory category = shopMapper.getShopCategoryById(categoryId);
                    if (category == null) {
                        throw new PreconditionFailedException(new ErrorMessage("유효하지 않은 상점 카테고리가 있습니다.", -1));
                    }

                    shopCategoryMaps.add(new ShopCategoryMap(shop.getId(), categoryId));
                });

        shopMapper.createShopCategoryMaps(shopCategoryMaps);


        // --- shop_menu_categories table ---

        shopMapper.createMenuCategories(
                generateMenuCategories(
                        shop.getId(), new String[]{"이벤트 메뉴", "대표 메뉴", "사이드 메뉴", "세트 메뉴"}
                )
        );


        // --- shop_images ---

        /*if (!dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 5) {
                throw new PreconditionFailedException(new ErrorMessage("상점 이미지는 최대 5개까지 등록할 수 있습니다.", 6));
            }

            List<ShopImage> images = new LinkedList<>();

            for (MultipartFile image : dto.getImages()) {
                String imgPath = uploadFileUtils.uploadFile("upload/shop_menus", image.getOriginalFilename(), image.getBytes());
                String url = uploadFileUtils.getDomain() + "/upload/shop_menus" + imgPath;
                menuImages.add(new ShopMenuImage(dto.getId(), url));
            }

            shopMapper.createMenuImages(menuImages);
        }*/

        return new HashMap<String, Object>() {{
            put("success", true);
            put("id", shop.getId());
        }};
    }



    @Override
    public ResponseShopDTO getShopForAdmin(Integer id) throws Exception {
        ResponseShopDTO shop = shopMapper.getResponseShop(id);

        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return shop.decideOptionalOfMenus();
    }

    @Override
    @Transactional
    public Map<String, Object> updateShopForAdmin(UpdateShopDTO dto) throws Exception {
        /*
             대상 테이블
             - shops
             - shop_open
             - shop_category_map
             - shop_images
         */

        // --- shops table ---
        Shop existingShop = shopMapper.getShopById(dto.getId());
        if (existingShop == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        Shop sameNameShop = shopMapper.getShopByName(dto.getName());
        if (sameNameShop != null && !dto.getId().equals(sameNameShop.getId())) {
            throw new ConflictException(new ErrorMessage("상점명이 이미 존재합니다.", 2));
        }

        shopMapper.updateShop(existingShop.update(dto));


        // --- shop_opens table ---
        List<ShopOpen> updatedShopOpens = new LinkedList<>();

        dto.getOpen()
                .forEach(open -> {
                    String dayOfWeekString = (String) open.get("day_of_week");
                    Boolean closed = (Boolean) open.get("closed");

                    if (dayOfWeekString == null || closed == null) {
                        throw new ValidationException(new ErrorMessage("요일과 휴무 여부는 필수입니다.", 0));
                    }

                    DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekString);
                    String openTime = (String) open.get("open_time");
                    String closeTime = (String) open.get("close_time");

                    if (openTime == null || closeTime == null) {
                        throw new ValidationException(new ErrorMessage("오픈 시간과 닫는 시간은 필수입니다.", 0));
                    }

                    updatedShopOpens.add(new ShopOpen(dto.getId(), dayOfWeek, closed, openTime, closeTime));
                });

        shopMapper.updateShopOpens(updatedShopOpens);


        // --- shop_category_map table ---
        List<ShopCategory> existingCategories = shopMapper.getShopCategoriesOfShopByShopId(dto.getId());
        List<ShopCategoryMap> newShopCategoryMaps = new LinkedList<>();

        dto.getCategory_ids()
                .forEach(categoryId -> {
                    ShopCategory updatedCategory = shopMapper.getShopCategoryById(categoryId);
                    if (updatedCategory == null) {
                        throw new NotFoundException(new ErrorMessage("유효하지 않은 상점 카테고리 id가 있습니다.", 2));
                    }

                    if (!existingCategories.contains(updatedCategory)) {
                        newShopCategoryMaps.add(new ShopCategoryMap(dto.getId(), categoryId));
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
                        shopCategoryMapsToDelete.add(new ShopCategoryMap(dto.getId(), category.getId()));
                    });
            shopMapper.deleteShopCategoryMaps(shopCategoryMapsToDelete);
        }


        // --- shop_images table ---
        shopMapper.deleteShopImagesByShopId(dto.getId());
        List<ShopImage> newImages = new LinkedList<>();

        for (MultipartFile image : dto.getImages()) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload/shops",
                    image.getOriginalFilename(),
                    image.getBytes()
            );

            String url = uploadFileUtils.getDomain() + "/upload/shops" + imgPath;
            newImages.add(new ShopImage(dto.getId(), url));
        }

        if (!newImages.isEmpty()) {
            shopMapper.createShopImages(newImages);
        }

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteShopForAdmin(Integer id) throws Exception {
        if (shopMapper.getShopById(id) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 0));
        }

        // 실수로 삭제 요청을 할 수 있으므로 쉽게 복구가 가능하도록 shop 테이블에서만 soft delete 처리
        shopMapper.deleteShopById(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public ResponseShopsDTO getShopsForAdmin(Integer page, Integer limit) throws Exception {
        int totalCount = shopMapper.getTotalCountOfShops();
        Integer totalPage;

        totalPage = (totalCount == 0) ? 1 : (int) Math.ceil(((double)totalCount) / limit);

        if (page > totalPage || page < 1) {
            throw new ValidationException(new ErrorMessage("유효한 페이지가 아닙니다.", 0));
        }

        List<koreatech.in.dto.shop.response.inner.Shop> shops =
                shopMapper.getShops(limit * page - limit, limit);

        return ResponseShopsDTO.builder()
                .total_page(totalPage)
                .current_page(page)
                .shops(shops)
                .build();
    }

    @Override
    @Transactional
    public Map<String, Object> createMenuCategoryForAdmin(CreateShopMenuCategoryDTO dto) throws Exception {
        if (shopMapper.getShopById(dto.getShop_id()) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        if (shopMapper.getMenuCategory(dto.getShop_id(), dto.getName()) != null) {
            throw new ConflictException(new ErrorMessage("이미 존재하는 카테고리입니다.", 2));
        }

        if (shopMapper.getCountOfShopCategoriesByShopId(dto.getShop_id()).equals(20)) {
            throw new ConflictException(new ErrorMessage("메뉴 카테고리는 최대 20개까지 설정 가능합니다.", 3));
        }

        ShopMenuCategory newMenuCategory = new ShopMenuCategory(dto.getShop_id(), dto.getName());
        shopMapper.createMenuCategory(newMenuCategory);

        return new HashMap<String, Object>() {{
            put("success", true);
            put("id", newMenuCategory.getId());
        }};
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopMenuCategoriesDTO getMenuCategoriesForAdmin(Integer shopId) throws Exception {
        if (shopMapper.getShopById(shopId) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        return ResponseShopMenuCategoriesDTO.builder()
                .shop_id(shopId)
                .menu_categories(shopMapper.getMenuCategoriesOfShop(shopId))
                .build();
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuCategoryForAdmin(Integer shopId, Integer categoryId) throws Exception {
        if (shopMapper.getShopById(shopId) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        ShopMenuCategory category = shopMapper.getMenuCategoryById(categoryId);

        if (category == null) {
            throw new NotFoundException(new ErrorMessage("없는 카테고리입니다.", 2));
        }
        if (!category.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        if (!shopMapper.getMenusUsingCategory(categoryId).isEmpty()) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "특정 메뉴에서 사용하고 있는 카테고리입니다. 삭제하려면 해당 메뉴에서 카테고리를 해제하고 다시 시도해주세요.", 3
            ));
        }

        shopMapper.deleteMenuCategoryById(categoryId);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> createMenuForAdmin(CreateShopMenuDTO dto) throws Exception {
        /*
             대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        if (shopMapper.getShopById(dto.getShop_id()) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        // --- shop_menus table ---
        ShopMenu menu = new ShopMenu(dto);
        shopMapper.createMenu(menu);


        // --- shop_menu_details table ---
        if (dto.getIs_single()) {
            if (dto.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("단일 메뉴는 single_price를 비워둘 수 없습니다.", 0));
            }
            shopMapper.createMenuDetail(new ShopMenuDetail(menu.getId(), dto.getSingle_price()));
        } else {
            if (dto.getOption_prices() == null || dto.getOption_prices().isEmpty()) {
                throw new ValidationException(new ErrorMessage("단일 메뉴가 아니면 option_prices를 비워둘 수 없습니다.", 0));
            }
            if (dto.existOfOptionDuplicate()) {
                throw new ValidationException(new ErrorMessage("중복되는 옵션이 있습니다.", 0));
            }

            List<ShopMenuDetail> menuDetails = new LinkedList<>();

            dto.getOption_prices()
                    .forEach(optionPrice -> {
                        String option = (String) optionPrice.get("option");
                        Integer price = (Integer) optionPrice.get("price");

                        if (option.length() > 12) {
                            throw new ValidationException(new ErrorMessage("옵션은 12자 이하입니다.", 0));
                        }
                        if (price == null) {
                            throw new ValidationException(new ErrorMessage("가격은 비워둘 수 없습니다.", 0));
                        }

                        menuDetails.add(new ShopMenuDetail(menu.getId(), option, price));
                    });

            shopMapper.createMenuDetails(menuDetails);
        }


        // --- shop_menu_images table ---
        if (!dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 3) {
                throw new ValidationException(new ErrorMessage("메뉴 이미지는 최대 3개까지 등록할 수 있습니다.", 0));
            }

            shopMapper.createMenuImages(generateMenuImages(dto.getImages(), menu.getId()));
        }


        // --- shop_menu_category_map table ---
        List<ShopMenuCategoryMap> shopMenuCategoryMaps = new LinkedList<>();

        dto.getCategory_ids()
                .forEach(categoryId -> {
                    ShopMenuCategory existingCategory = shopMapper.getMenuCategoryById(categoryId);

                    if (existingCategory == null || !existingCategory.getShop_id().equals(dto.getShop_id())) {
                        throw new NotFoundException(new ErrorMessage("유효하지 않은 메뉴 카테고리가 있습니다.", 2));
                    }

                    shopMenuCategoryMaps.add(new ShopMenuCategoryMap(menu.getId(), categoryId));
                });

        shopMapper.createMenuCategoryMaps(shopMenuCategoryMaps);

        return new HashMap<String, Object>() {{
            put("success", true);
            put("id", menu.getId());
        }};
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopMenuDTO getMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        if (shopMapper.getShopById(shopId) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        ResponseShopMenuDTO menu = shopMapper.getResponseMenu(menuId);
        if (menu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 2));
        }
        if (!menu.getShop_id().equals(shopId)) {
            throw new UnauthorizeException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        return menu.decideSingleOrOption();
    }

    @Override
    @Transactional
    public Map<String, Object> updateMenuForAdmin(UpdateShopMenuDTO dto) throws Exception {
        /*
             대상 테이블
             - shop_menus
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        if (shopMapper.getShopById(dto.getShop_id()) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        ShopMenu existingMenu = shopMapper.getMenu(dto.getId());
        if (existingMenu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 2));
        }
        if (!existingMenu.getShop_id().equals(dto.getShop_id())) {
            throw new ValidationException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        // --- shop_menus table ---
        ShopMenu updatedMenu = new ShopMenu(dto);
        if (!updatedMenu.equals(existingMenu)) {
            updatedMenu.setIs_hidden(existingMenu.getIs_hidden());
            shopMapper.updateMenu(updatedMenu);
        }


        // --- shop_menu_details table ---
        List<ShopMenuDetail> existingMenuDetails = shopMapper.getMenuDetailsByMenuId(dto.getId());

        if (dto.getIs_single()) {
            if (dto.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage("단일 메뉴는 single_price를 비워둘 수 없습니다.", 0));
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
            if (dto.getOption_prices() == null || dto.getOption_prices().isEmpty()) {
                throw new ValidationException(new ErrorMessage("단일 메뉴가 아니면 option_prices를 비워둘 수 없습니다.", 0));
            }
            if (dto.existOfOptionDuplicate()) {
                throw new ValidationException(new ErrorMessage("중복되는 옵션이 있습니다.", 0));
            }

            List<ShopMenuDetail> newMenuDetails = new LinkedList<>();

            dto.getOption_prices()
                    .forEach(optionPrice -> {
                        String option = (String) optionPrice.get("option");
                        Integer price = (Integer) optionPrice.get("price");

                        if (option.length() > 12) {
                            throw new ValidationException(new ErrorMessage("옵션은 12자 이하입니다.", 0));
                        }
                        if (price == null) {
                            throw new ValidationException(new ErrorMessage("가격은 비워둘 수 없습니다.", 0));
                        }

                        ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(dto.getId(), option, price);

                        // 추가할 옵션&가격
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


        // --- shop_menu_images table ---
        shopMapper.deleteMenuImagesByMenuId(dto.getId());

        if (!dto.getImages().isEmpty()) {
            if (dto.getImages().size() > 3) {
                throw new ValidationException(new ErrorMessage("메뉴 이미지는 최대 3개까지 등록할 수 있습니다.", 0));
            }

            shopMapper.createMenuImages(generateMenuImages(dto.getImages(), dto.getId()));
        }


        // --- shop_menu_category_map table ---
        List<ShopMenuCategory> existingCategories = shopMapper.getMenuCategoriesOfMenu(dto.getId());
        List<ShopMenuCategoryMap> newMenuCategoryMaps = new LinkedList<>();

        dto.getCategory_ids()
                .forEach(categoryId -> {
                    ShopMenuCategory updatedMenuCategory = shopMapper.getMenuCategoryById(categoryId);

                    if (updatedMenuCategory == null || !updatedMenuCategory.getShop_id().equals(dto.getShop_id())) {
                        throw new NotFoundException(new ErrorMessage("유효하지 않은 메뉴 카테고리가 있습니다.", 3));
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

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        if (shopMapper.getShopById(shopId) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        ShopMenu menu = shopMapper.getMenu(menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 2));
        }
        if (!menu.getShop_id().equals(shopId)) {
            throw new UnauthorizeException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        shopMapper.deleteAllForInvolvedWithMenu(menuId);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> hideMenuForAdmin(Integer shopId, Integer menuId, Boolean flag) throws Exception {
        if (shopMapper.getShopById(shopId) == null) {
            throw new NotFoundException(new ErrorMessage("상점이 존재하지 않습니다.", 1));
        }

        ShopMenu menu = shopMapper.getMenu(menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 2));
        }
        if (!menu.getShop_id().equals(shopId)) {
            throw new UnauthorizeException(new ErrorMessage("잘못된 접근입니다.", 0));
        }

        if (flag) {
            if (menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage("이미 숨김 처리된 메뉴입니다.", 3));
            }
        } else {
            if (!menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage("이미 숨김 메뉴가 아닙니다.", 4));
            }
        }

        shopMapper.updateMenu(menu.reverseIsHidden());

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
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
    public ResponseShopDTO getShop(Integer id) throws Exception {
        ResponseShopDTO shop = shopMapper.getResponseShop(id);

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

    private List<ShopMenuCategory> generateMenuCategories(Integer shopId, String[] names) {
        List<ShopMenuCategory> menuCategories = new LinkedList<>();

        Arrays.stream(names)
                .forEach(name -> {
                    menuCategories.add(new ShopMenuCategory(shopId, name));
                });

        return menuCategories;
    }

    private List<ShopOpen> generateShopOpens(Integer shopId, List<Map<String, Object>> open) {
        if (open.size() != 7) {
            throw new PreconditionFailedException(new ErrorMessage("opens의 size는 7이어야 합니다.", -1));
        }

        List<ShopOpen> opens = new LinkedList<>();

        int day = 1;
        for (Map<String, Object> o : open) {
            Boolean closed = (Boolean) o.get("closed");
            String openTime = (String) o.get("open_time");
            String closeTime = (String) o.get("close_time");

            if (closed == null) {
                throw new PreconditionFailedException(new ErrorMessage("closed는 필수입니다.", -1));
            }

            opens.add(new ShopOpen(shopId, DayOfWeek.of(day), closed, openTime, closeTime));
            day++;
        }

        return opens;
    }

    private List<ShopImage> generateShopImages(List<MultipartFile> images, Integer shopId) throws Exception {
        List<ShopImage> shopImages = new LinkedList<>();

        for (MultipartFile image : images) {
            String imagePath = uploadFileUtils.uploadFile("upload/shops", image.getOriginalFilename(), image.getBytes());
            shopImages.add(new ShopImage(shopId, uploadFileUtils.getDomain() + "/upload/shops" + imagePath));
        }

        return shopImages;
    }

    private List<ShopMenuImage> generateMenuImages(List<MultipartFile> images, Integer menuId) throws Exception {
        List<ShopMenuImage> menuImages = new LinkedList<>();

        for (MultipartFile image : images) {
            String imagePath = uploadFileUtils.uploadFile("upload/shop_menus", image.getOriginalFilename(), image.getBytes());
            menuImages.add(new ShopMenuImage(menuId, uploadFileUtils.getDomain() + "/upload/shop_menus" + imagePath));
        }

        return menuImages;
    }
}
