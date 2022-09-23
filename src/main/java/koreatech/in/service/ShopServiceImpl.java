package koreatech.in.service;

import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.response.ResponseShopDTO;
import koreatech.in.dto.shop.response.ResponseShopMenuDTO;
import koreatech.in.dto.shop.response.ResponseShopMenusDTO;
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

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service("shopService")
@Validated
public class ShopServiceImpl implements ShopService {
    @Resource(name = "shopMapper")
    private ShopMapper shopMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Autowired
    private JsonConstructor con;

    @Inject
    private UploadFileUtils uploadFileUtils;

    /*@Transactional
    @Override
    public Shop createShopForAdmin(Shop shop) throws Exception {
        if (shopMapper.getShopByNameForAdmin(shop.getName()) != null) {
            throw new ConflictException(new ErrorMessage("shop already exist", 2));
        }
        if (!shop.contain(shop.getCategory())) {
            throw new PreconditionFailedException(new ErrorMessage("No such Category", 2));
        }

        //image_urls 체크
        if (shop.getImage_urls() != null && !con.isJsonArrayWithOnlyString(shop.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        shop.setInternal_name(shop.getName().replace(" ", "").toLowerCase());

        shop.setChosung(shop.getName().substring(0, 1));

        if (shop.getImage_urls() == null) {
            shop.setImage_urls("");
        }
        if (shop.getDelivery() == null) {
            shop.setDelivery(false);
        }
        if (shop.getDelivery_price() == null) {
            shop.setDelivery_price(0);
        }
        if (shop.getPay_bank() == null) {
            shop.setPay_bank(false);
        }
        if (shop.getPay_card() == null) {
            shop.setPay_card(false);
        }
        if (shop.getIs_deleted() == null) {
            shop.setIs_deleted(false);
        }
        if (shop.getIs_event() == null) {
            shop.setIs_event(false);
        }

        shopMapper.createShopForAdmin(shop);

        return shop;
    }

    @Override
    public Map<String, Object> getShopsForAdmin(Criteria criteria) throws Exception {

        List<Shop> shops = shopMapper.getShopListForAdmin(criteria.getCursor(), criteria.getLimit());
        List<Map<String, Object>> convert_shops = new ArrayList<>();
        for (Shop shop : shops) {
            Map<String, Object> map_shop = domainToMap(shop);
            map_shop.replace("image_urls", con.parseJsonArrayWithOnlyString(shop.getImage_urls()));
            convert_shops.add(map_shop);
        }

        double totalCount = shopMapper.totalShopCountForAdmin();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shops", convert_shops);
        map.put("totalPage", totalPage);

        return map;
    }

    @Override
    public Map<String, Object> getShopForAdmin(String id) throws Exception {
        //TODO: 초성으로 검색하는 whereInternalName 차후 구현 필요
        Map<String, Object> map;
        List<Menu> menus;
        Shop shop;

        if (id.matches("^[1-9][0-9]*$")) {
            shop = shopMapper.getShopForAdmin(Integer.parseInt(id));
            try {
                map = domainToMap(shop);
            } catch (Exception e) {
                throw new NotFoundException(new ErrorMessage("There is no shop", 2));
            }
            //TODO:domainToMap 전부 try catch로 널값 접근 검사
            menus = shopMapper.getMenusForAdmin(shop.getId());
        } else {
            shop = shopMapper.getShopByInternalNameForAdmin(id);
            try {
                map = domainToMap(shop);
            } catch (Exception e) {
                throw new NotFoundException(new ErrorMessage("There is no shop", 2));
            }
            //TODO:domainToMap 전부 try catch로 널값 접근 검사
            menus = shopMapper.getMenusForAdmin(shop.getId());
        }

        List<Map<String, Object>> convert_menus = new ArrayList<Map<String, Object>>();

        try {
            for (Menu menu : menus) {
                //menu를 map 객체로 바꾼 후 price_type 값을 array_in_convert_price로 교체, 그것을 다시 convert_menus에 추가
                Map<String, Object> convert_price_menu = domainToMap(menu);
                convert_price_menu.replace("price_type", con.parseJsonArrayWithObject(menu.getPrice_type()));
                convert_menus.add(convert_price_menu);
            }
        } catch (Exception e) {
        }
        map.put("menus", convert_menus);

        //image_urls 컬럼을 list로
        //변환된 list로 값 교체
        map.replace("image_urls", con.parseJsonArrayWithOnlyString(map.get("image_urls").toString()));
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteShopForAdmin(int id) throws Exception {
        Shop shop = shopMapper.getShopForAdmin(id);
        if (shop == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        shopMapper.deleteShopForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public Menu createMenuForAdmin(Menu menu, int shop_id) throws Exception {
        //price_type 검증
        if (menu.getPrice_type() != null && !con.isJsonArrayWithOnlyObject(menu.getPrice_type()))
            throw new PreconditionFailedException(new ErrorMessage("Price_type is not valid", 0));

        menu.setShop_id(shop_id);
        if (menu.getIs_deleted() == null) {
            menu.setIs_deleted(false);
        }
        shopMapper.createMenuForAdmin(menu);

        return menu;
    }

    @Override
    public Map<String, Object> getMenuForAdmin(int shop_id, int menu_id) throws Exception {
        Shop shop = shopMapper.getShopForAdmin(shop_id);
        if (shop == null)
            throw new NotFoundException(new ErrorMessage("There is no such shop", 0));

        Menu menu = shopMapper.getMenuForAdmin(menu_id);
        if (menu == null)
            throw new NotFoundException(new ErrorMessage("There is no such menu", 0));

        Map<String, Object> map = domainToMap(menu);
        map.replace("price_type", con.parseJsonArrayWithObject(menu.getPrice_type()));
        return map;
    }

    @Transactional
    @Override
    public Menu updateMenuForAdmin(Menu menu, int shop_id, int id) throws Exception {
        Menu menu_old = shopMapper.getMenuForAdmin(id);
        //빈 객체인지 체크
        if (menu_old == null)
            throw new NotFoundException(new ErrorMessage("There is no such menu", 0));

        ///price_type 검증
        if (menu.getPrice_type() != null && !con.isJsonArrayWithOnlyObject(menu.getPrice_type()))
            throw new PreconditionFailedException(new ErrorMessage("Price_type is not valid", 0));


        menu_old.update(menu);
        shopMapper.updateMenuForAdmin(menu_old);

        return menu_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteMenuForAdmin(int id) throws Exception {
        Menu menu = shopMapper.getMenuForAdmin(id);
        //빈 객체인지 체크
        if (menu == null)
            throw new NotFoundException(new ErrorMessage("There is no such menu", 0));

        shopMapper.deleteMenuForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Object> getShops() throws Exception {

        Map<String, Object> map = new HashMap<>();
        List<Shop> shops = shopMapper.getShopList();
        List<Map<String, Object>> shopsMapList = new ArrayList<>();

        for (Shop shop : shops) {
            shop.setPermalink(shop.getInternal_name());
            Map<String, Object> shopMap = domainToMap(shop);
            shopMap.replace("image_urls", con.parseJsonArrayWithOnlyString(shopMap.get("image_urls").toString()));
            List<EventArticle> eventArticles = shopMapper.getPendingEventByShopId(shop.getId());
            shopMap.put("event_articles", eventArticles);
            shopsMapList.add(shopMap);
        }

        map.put("shops", shopsMapList);

        return map;
    }

    @Override
    public Map<String, Object> getShop(String id) throws Exception {
        //TODO: 초성으로 검색하는 whereInternalName 차후 구현 필요
        Map<String, Object> map;
        List<Menu> menus;
        List<EventArticle> eventArticles;
        Shop shop;

        if (id.matches("^[1-9][0-9]*$")) {
            shop = shopMapper.getShopById(Integer.parseInt(id));
            try {
                map = domainToMap(shop);
            } catch (Exception e) {
                throw new NotFoundException(new ErrorMessage("There is no shop", 2));
            }
            //TODO:domainToMap 전부 try catch로 널값 접근 검사
            menus = shopMapper.getMenus(shop.getId());
            eventArticles = shopMapper.getPendingEventByShopId(shop.getId());
        } else {
            shop = shopMapper.getShopByInternalName(id);
            try {
                map = domainToMap(shop);
            } catch (Exception e) {
                throw new NotFoundException(new ErrorMessage("There is no shop", 2));
            }
            //TODO:domainToMap 전부 try catch로 널값 접근 검사
            menus = shopMapper.getMenus(shop.getId());
            eventArticles = shopMapper.getPendingEventByShopId(shop.getId());
        }

        // Shop view Logging
        User user = jwtValidator.validate();

        if (user != null) {
            // Get ip address
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = req.getHeader("X-FORWARDED-FOR");
            if (ip == null)
                ip = req.getRemoteAddr();

            if (shouldIncreaseShopHit(shop, user, ip)) {
                shopMapper.increaseHit(shop.getId());
            }
        }

        List<Map<String, Object>> convert_menus = new ArrayList<Map<String, Object>>();

        try {
            for (Menu menu : menus) {
                //menu를 map 객체로 바꾼 후 price_type 값을 array_in_convert_price로 교체, 그것을 다시 convert_menus에 추가
                Map<String, Object> convert_price_menu = domainToMap(menu);
                convert_price_menu.replace("price_type", con.parseJsonArrayWithObject(menu.getPrice_type()));
                convert_menus.add(convert_price_menu);
            }
        } catch (Exception e) {
        }
        map.put("menus", convert_menus);
        map.put("event_articles", eventArticles);

        //image_urls 컬럼을 list로
        //변환된 list로 값 교체
        map.replace("image_urls", con.parseJsonArrayWithOnlyString(map.get("image_urls").toString()));
        return map;
    }

    // Shop view Logging
    public Boolean shouldIncreaseShopHit(Shop shop, User user, String ip) {
        try {
            ShopViewLog viewLog = shopMapper.getViewLog(shop.getId(), user.getId());
            if (viewLog != null && (viewLog.getExpired_at().getTime() - (new Date()).getTime() > 0)) return false;

            Date expiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);

            //TODO: update Or insert 구현시 개선
            if (viewLog == null) {
                viewLog = new ShopViewLog();
                viewLog.setShop_id(shop.getId());
                viewLog.setUser_id(user.getId());
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                shopMapper.createViewLog(viewLog);
            } else {
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                shopMapper.updateViewLog(viewLog);
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }*/

    @Override
    @Transactional
    public Map<String, Object> createShopCategoryForAdmin(CreateShopCategoryDTO dto) throws Exception {
        ShopCategory existingCategory = shopMapper.getShopCategoryByName(dto.getName());
        if (existingCategory != null) {
            throw new ConflictException(new ErrorMessage("이미 존재하는 카테고리입니다.", 0));
        }

        String imageUrl = null;
        if (dto.getImage() != null) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload/shop/categories",
                    dto.getImage().getOriginalFilename(),
                    dto.getImage().getBytes()
            );
            imageUrl = uploadFileUtils.getDomain() + "/upload/shop/categories" + imgPath;
        }

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
        ShopCategory existingCategory = shopMapper.getShopCategoryById(dto.getId());
        if (existingCategory == null) {
            throw new NotFoundException(new ErrorMessage("없는 카테고리입니다.", 0));
        }

        String imageUrl = null;
        if (dto.getImage() != null) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload/shop/categories",
                    dto.getImage().getOriginalFilename(),
                    dto.getImage().getBytes()
            );
            imageUrl = uploadFileUtils.getDomain() + "/upload/shop/categories" + imgPath;
        }

        shopMapper.updateShopCategory(existingCategory.update(dto.getName(), imageUrl));

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteShopCategoryForAdmin(Integer id) throws Exception {
        ShopCategory existingCategory = shopMapper.getShopCategoryById(id);
        if (existingCategory == null) {
            throw new NotFoundException(new ErrorMessage("없는 카테고리입니다.", 0));
        }

        List<Shop> shopsUsingCategory = shopMapper.getShopsUsingCategory(id);
        if (shopsUsingCategory != null && shopsUsingCategory.size() >= 1) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "카테고리를 사용하고 있는 상점들이 있어 삭제할 수 없습니다.", 1
            ));
        }

        shopMapper.deleteShopCategoryById(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopCategory> getShopCategoriesForAdmin() throws Exception {
        return shopMapper.getAllShopCategories();
    }

    @Override
    @Transactional
    public Map<String, Object> createShopForAdmin(CreateShopDTO dto) throws Exception {
        Shop existentShop = shopMapper.getShopByName(dto.getName());

        if (existentShop != null) {
            throw new NotFoundException(new ErrorMessage("이미 존재하는 상점입니다.", 0));
        }

        Shop shop = new Shop(dto);
        shopMapper.createShop(shop);

        for (int day = 1; day <= 7; day++) {
            DayOfWeek dayOfWeek = DayOfWeek.of(day);
            ShopOpen open = new ShopOpen(shop.getId(), dayOfWeek);
            shopMapper.createShopOpen(open);
        }

        dto.getCategory_ids().forEach(categoryId -> {
            ShopCategory existingCategory = shopMapper.getShopCategoryById(categoryId);
            if (existingCategory == null) {
                throw new NotFoundException(new ErrorMessage("없는 카테고리를 선택하셨습니다.", 1));
            }

            shopMapper.createShopCategoryMap(shop.getId(), categoryId);
        });

        return new HashMap<String, Object>() {{
            put("success", true);
            put("id", shop.getId());
        }};
    }

    @Override
    public ResponseShopDTO getShopForAdmin(Integer id) throws Exception {
        ResponseShopDTO responseDto = shopMapper.getResponseShop(id);

        if (responseDto == null) {
            throw new NotFoundException(new ErrorMessage("없는 상점입니다.", 0));
        }

        return responseDto.decideOptionalOfMenus();
    }

    @Override
    @Transactional
    public Map<String, Object> updateShopForAdmin(UpdateShopDTO dto) throws Exception {
        /*
             변경되어야할 것
             - shops
             - shop_open
             - shop_category_map
             - shop_images
         */

        Shop existingShop = shopMapper.getShopById(dto.getId());
        if (existingShop == null) {
            throw new NotFoundException(new ErrorMessage("상점 정보가 없습니다.", 0));
        }

        Shop duplicateNameShop = shopMapper.getShopByName(dto.getName());
        if (duplicateNameShop != null && !dto.getId().equals(duplicateNameShop.getId())) {
            throw new ConflictException(new ErrorMessage("상점명이 이미 존재합니다.", 1));
        }

        shopMapper.updateShopForAdmin(existingShop.update(dto));

        /*dto.getOpen().forEach(open -> {
            DayOfWeek dayOfWeek =
        });*/





        List<ShopCategory> existingCategories = shopMapper.getShopCategoriesOfShopByShopId(dto.getId());

        dto.getCategory_ids().forEach(categoryId -> {
            ShopCategory updatedCategory = shopMapper.getShopCategoryById(categoryId);
            if (updatedCategory == null) {
                throw new NotFoundException(new ErrorMessage("선택하신 상점 카테고리가 존재하지 않습니다.", 2));
            }

            /*if (!existingCategories.contains(updatedCategory)) {
                shopMapper.createShopCategoryMap()
            }*/


        });

        shopMapper.deleteShopImagesByShopId(dto.getId());
        for (MultipartFile image : dto.getImages()) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload/shops",
                    image.getOriginalFilename(),
                    image.getBytes()
            );

            String url = uploadFileUtils.getDomain() + "/upload/shops" + imgPath;
            shopMapper.createShopImage(new ShopImage(dto.getId(), url));
        }

        //shopMapper.updateShop(shopInDatabase.update(dto));

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteShopForAdmin(Integer id) throws Exception {
        /*
             soft delete 대상 테이블 (shop_categories 제외하고 싹다 적용)
             <shop_id로 삭제>
             - shops
             - shop_open
             - shop_images
             - shop_category_map
             - shop_menus
             - shop_menu_categories


             <shop_menu_id로 삭제>
             - shop_menu_details
             - shop_menu_images
             - shop_menu_category_map
         */

        Shop existingShop = shopMapper.getShopById(id);
        if (existingShop == null) {
            throw new NotFoundException(new ErrorMessage("없는 상점입니다.", 0));
        }

        List<ShopMenu> existingMenus = shopMapper.getShopMenusByShopId(id);

        shopMapper.deleteAllForInvolvedWithShop(id);

        return null;
    }

    @Override
    public Object getShopsForAdmin() throws Exception {
        return null;
    }

    @Override
    @Transactional
    public Map<String, Object> createMenuCategoryForAdmin(CreateShopMenuCategoryDTO dto) throws Exception {
        ShopMenuCategory menuCategoryInDatabase = shopMapper.getMenuCategory(
                dto.getShop_id(), dto.getName()
        );

        if (menuCategoryInDatabase != null) {
            throw new ConflictException(new ErrorMessage(
                    "이미 존재하는 카테고리입니다.", 0
            ));
        }

        shopMapper.createMenuCategory(new ShopMenuCategory(
                dto.getShop_id(), dto.getName()
        ));

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuCategoryForAdmin(Integer shopId, Integer categoryId) throws Exception {
        ShopMenuCategory menuCategoryInDatabase = shopMapper.getMenuCategoryById(categoryId);

        if (menuCategoryInDatabase == null) {
            throw new NotFoundException(new ErrorMessage(
                    "없는 카테고리입니다.", 0
            ));
        }

        if (!menuCategoryInDatabase.getShop_id().equals(shopId)) {
            throw new ForbiddenException(new ErrorMessage(
                    "삭제 권한이 없는 카테고리입니다.", 1
            ));
        }

        List<ShopMenu> menusUsingCategory = shopMapper.getMenusUsingCategory(categoryId);
        if (menusUsingCategory != null && menusUsingCategory.size() >= 1) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "특정 메뉴에서 사용하고 있는 카테고리입니다. 삭제하려면 해당 메뉴에서 카테고리를 해제하고 다시 시도해주세요.", 2
            ));
        }

        shopMapper.deleteMenuCategoryById(categoryId);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopMenuCategory> getMenuCategoriesForAdmin(Integer shopId) throws Exception {
        return shopMapper.getMenuCategoriesOfShop(shopId);
    }

    @Override
    @Transactional
    public Map<String, Object> createMenuForAdmin(CreateShopMenuDTO dto) throws Exception {
        ShopMenu menu = new ShopMenu(dto);
        shopMapper.createMenu(menu);

        if (dto.getIs_single()) {
            if (dto.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage(
                        "단일 메뉴는 single_price를 비워둘 수 없습니다.", 0
                ));
            }
            shopMapper.createMenuDetail(new ShopMenuDetail(
                    menu.getId(), dto.getSingle_price()
            ));
        } else {
            if (dto.getOption_prices() == null || dto.getOption_prices().size() == 0) {
                throw new ValidationException(new ErrorMessage(
                        "단일 메뉴가 아니면 option_prices를 비워둘 수 없습니다.", 1
                ));
            }
            if (dto.existOfOptionDuplicate()) {
                throw new PreconditionFailedException(new ErrorMessage(
                        "중복되는 옵션이 있습니다.", 3
                ));
            }

            List<ShopMenuDetail> shopMenuDetails = new LinkedList<>();

            dto.getOption_prices().forEach(optionPrice -> {
                String option = (String) optionPrice.get("option");
                Integer price = (Integer) optionPrice.get("price");

                if (price == null) {
                    throw new PreconditionFailedException(new ErrorMessage(
                            "가격은 비워둘 수 없습니다.", 4
                    ));
                }

                shopMenuDetails.add(new ShopMenuDetail(menu.getId(), option, price));
            });

            if (!shopMenuDetails.isEmpty()) {
                shopMapper.createMenuDetails(shopMenuDetails);
            }
        }

        // TODO: 기획 변경시 최대 업로드 개수 변경하기
        if (dto.getImages().size() > 5) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "메뉴 이미지는 최대 5개까지 등록할 수 있습니다.", 5
            ));
        }

        List<ShopMenuImage> shopMenuImages = new LinkedList<>();

        for (MultipartFile image : dto.getImages()) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload/shop_menus",
                    image.getOriginalFilename(),
                    image.getBytes()
            );

            String url = uploadFileUtils.getDomain() + "/upload/shop_menus" + imgPath;
            shopMenuImages.add(new ShopMenuImage(menu.getId(), url));
        }

        if (!shopMenuImages.isEmpty()) {
            shopMapper.createMenuImages(shopMenuImages);
        }

        List<ShopMenuCategoryMap> shopMenuCategoryMaps = new LinkedList<>();

        dto.getCategory_ids().forEach(categoryId -> {
            ShopMenuCategory existingMenuCategory = shopMapper.getMenuCategoryById(categoryId);

            if (existingMenuCategory == null) {
                throw new NotFoundException(new ErrorMessage(
                        "선택한 카테고리중에 상점에서 설정하지 않은 카테고리가 있습니다.", 6
                ));
            }

            if (!existingMenuCategory.getShop_id().equals(dto.getShop_id())) {
                throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 7));
            }

            shopMenuCategoryMaps.add(new ShopMenuCategoryMap(menu.getId(), categoryId));
        });

        if (!shopMenuCategoryMaps.isEmpty()) {
            shopMapper.createMenuCategoryMaps(shopMenuCategoryMaps);
        }

        return new HashMap<String, Object>() {{
            put("success", true);
            put("id", menu.getId());
        }};
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopMenuDTO getMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        ResponseShopMenuDTO menu = shopMapper.getResponseMenu(menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 0));
        }

        return menu.decideSingleOrOption();
    }

    @Override
    @Transactional
    public Map<String, Object> updateMenuForAdmin(UpdateShopMenuDTO dto) throws Exception {
        ShopMenu existingMenu = shopMapper.getMenu(dto.getId());
        if (existingMenu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 0));
        }

        ShopMenu updatedMenu = new ShopMenu(dto);
        if (!updatedMenu.equals(existingMenu)) {
            updatedMenu.setIs_hidden(existingMenu.getIs_hidden());
            shopMapper.updateMenu(updatedMenu);
        }

        List<ShopMenuDetail> existingMenuDetails = shopMapper.getMenuDetailsByMenuId(dto.getId());
        if (dto.getIs_single()) {
            if (dto.getSingle_price() == null) {
                throw new ValidationException(new ErrorMessage(
                        "단일 메뉴는 single_price를 비워둘 수 없습니다.", 1
                ));
            }

            ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(
                    dto.getId(), dto.getSingle_price()
            );

            if (!existingMenuDetails.contains(updatedMenuDetail)) {
                shopMapper.deleteMenuDetailsByMenuId(dto.getId());
                shopMapper.createMenuDetail(updatedMenuDetail);
            } else {
                existingMenuDetails.remove(updatedMenuDetail);
                if (!existingMenuDetails.isEmpty()) {
                    shopMapper.deleteMenuDetails(existingMenuDetails);
                }
            }
        } else {
            if (dto.getOption_prices() == null || dto.getOption_prices().size() == 0) {
                throw new ValidationException(new ErrorMessage(
                        "단일 메뉴가 아니면 option_prices를 비워둘 수 없습니다.", 2
                ));
            }
            if (dto.existOfOptionDuplicate()) {
                throw new PreconditionFailedException(new ErrorMessage(
                        "중복되는 옵션이 있습니다.", 4
                ));
            }

            List<ShopMenuDetail> shopMenuDetailsToUpdate = new LinkedList<>();

            dto.getOption_prices().forEach(optionPrice -> {
                String option = (String) optionPrice.get("option");
                Integer price = (Integer) optionPrice.get("price");

                if (price == null) {
                    throw new PreconditionFailedException(new ErrorMessage(
                            "가격은 비워둘 수 없습니다.", 5
                    ));
                }

                ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(
                        dto.getId(), option, price
                );

                // 추가할 옵션&가격
                if (!existingMenuDetails.contains(updatedMenuDetail)) {
                    shopMenuDetailsToUpdate.add(updatedMenuDetail);
                }

                existingMenuDetails.remove(updatedMenuDetail);
            });

            if (!shopMenuDetailsToUpdate.isEmpty()) {
                shopMapper.createMenuDetails(shopMenuDetailsToUpdate);
            }
            if (!existingMenuDetails.isEmpty()) {
                shopMapper.deleteMenuDetails(existingMenuDetails);
            }
        }

        // TODO: 기획 변경시 최대 업로드 개수 변경하기
        if (dto.getImages().size() > 5) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "메뉴 이미지는 최대 5개까지 등록할 수 있습니다.", 6
            ));
        }

        shopMapper.deleteMenuImagesByMenuId(dto.getId());

        List<ShopMenuImage> shopMenuImages = new LinkedList<>();

        for (MultipartFile image : dto.getImages()) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload/shop_menus",
                    image.getOriginalFilename(),
                    image.getBytes()
            );

            String url = uploadFileUtils.getDomain() + "/upload/shop_menus" + imgPath;
            shopMenuImages.add(new ShopMenuImage(dto.getId(), url));
        }

        if (!shopMenuImages.isEmpty()) {
            shopMapper.createMenuImages(shopMenuImages);
        }

        List<ShopMenuCategory> existingCategories = shopMapper.getMenuCategoriesOfMenu(dto.getId());
        List<ShopMenuCategoryMap> updatedMenuCategoryMaps = new LinkedList<>();
        dto.getCategory_ids().forEach(categoryId -> {
            ShopMenuCategory updatedMenuCategory = shopMapper.getMenuCategoryById(categoryId);

            if (updatedMenuCategory == null) {
                throw new NotFoundException(new ErrorMessage(
                        "선택하신 카테고리중에 상점에서 설정하지 않은 카테고리가 있습니다.", 7
                ));
            }
            if (!updatedMenuCategory.getShop_id().equals(dto.getShop_id())) {
                throw new ForbiddenException(new ErrorMessage("잘못된 접근입니다.", 8));
            }

            // 새로 선택된 카테고리
            if (!existingCategories.contains(updatedMenuCategory)) {
                updatedMenuCategoryMaps.add(new ShopMenuCategoryMap(
                        dto.getId(), updatedMenuCategory.getId()
                ));
            }

            existingCategories.remove(updatedMenuCategory);
        });

        if (!updatedMenuCategoryMaps.isEmpty()) {
            shopMapper.createMenuCategoryMaps(updatedMenuCategoryMaps);
        }

        List<ShopMenuCategoryMap> menuCategoryMapsToDelete = new LinkedList<>();
        existingCategories.forEach(category -> {
            menuCategoryMapsToDelete.add(new ShopMenuCategoryMap(
                    dto.getId(), category.getId()
            ));
        });

        if (!menuCategoryMapsToDelete.isEmpty()) {
            shopMapper.deleteMenuCategoryMaps(menuCategoryMapsToDelete);
        }

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuForAdmin(Integer shopId, Integer menuId) throws Exception {
        ShopMenu existingMenu = shopMapper.getMenu(menuId);

        if (existingMenu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 0));
        }

        shopMapper.deleteAllForInvolvedWithMenu(menuId);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> hideMenuForAdmin(Integer shopId, Integer menuId, Boolean flag) throws Exception {
        ShopMenu menu = shopMapper.getMenu(menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 0));
        }

        if (flag) {
            if (menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage("이미 숨김 처리된 메뉴입니다.", 1));
            }
        } else {
            if (!menu.getIs_hidden()) {
                throw new ConflictException(new ErrorMessage("이미 숨김 메뉴가 아닙니다.", 2));
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
        ResponseShopMenusDTO responseDto = shopMapper.getResponseMenus(shopId);

        if (responseDto == null) {
            throw new NotFoundException(new ErrorMessage("상점이 없습니다.", 0));
        }

        return responseDto.decideOptionalOfMenus();
    }
}
