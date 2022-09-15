package koreatech.in.service;

import koreatech.in.controller.v2.dto.shop.request.CreateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuCategoryDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.response.ResponseShopMenuForOwnerDTO;
import koreatech.in.controller.v2.dto.shop.response.ResponseShopMenusForOwnerDTO;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Shop.*;
import koreatech.in.domain.User.User;
import koreatech.in.exception.*;
import koreatech.in.repository.ShopMapper;
import koreatech.in.util.DateUtil;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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

    @Transactional
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
    public Shop updateShopForAdmin(Shop shop, int id) throws Exception {
        Shop shop_old = shopMapper.getShopForAdmin(id);
        if (shop_old == null)
            throw new NotFoundException(new ErrorMessage("There is no shop", 0));

        Shop selectShop = shopMapper.getShopByNameForAdmin(shop.getName());
        if (selectShop != null && !selectShop.getId().equals(id)) {
            throw new ConflictException(new ErrorMessage("duplicate shop name", 0));
        }

        if (shop.getCategory() != null && !shop.contain(shop.getCategory())) {
            throw new PreconditionFailedException(new ErrorMessage("No such Category", 2));
        }

        //image_urls 체크
        if (shop.getImage_urls() != null && !con.isJsonArrayWithOnlyString(shop.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        if (shop.getName() != null) {
            shop.setInternal_name(shop.getName().replace(" ", "").toLowerCase());
            shop.setChosung(shop.getName().substring(0, 1));
        }

        shop_old.update(shop);
        shop_old.setWeekend_open_time(shop.getWeekend_open_time());
        shop_old.setWeekend_close_time(shop.getWeekend_close_time());
        shopMapper.updateShopForAdmin(shop_old);
        return shop_old;
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
    }

    @Override
    @Transactional
    public Map<String, Object> updateMenuCategoriesForOwner(UpdateShopMenuCategoryDTO dto) {
        List<ShopMenuCategory> existingCategories = shopMapper.getMenuCategoriesOfShopByShopId(dto.getShop_id());

        dto.getCategories().forEach(category -> {
            ShopMenuCategory updatedMenuCategory = new ShopMenuCategory(
                    dto.getShop_id(), category
            );

            // 추가할 카테고리
            if (!existingCategories.contains(updatedMenuCategory)) {
                shopMapper.createMenuCategory(updatedMenuCategory);
            }

            existingCategories.remove(updatedMenuCategory);
        });

        // 삭제할 카테고리
        existingCategories.forEach(categoryToDelete -> {
            shopMapper.deleteMenuCategoryById(categoryToDelete.getId());
        });

        return new HashMap<String, Object>() {{
            put("message", "카테고리가 수정되었습니다.");
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> createMenuForOwner(CreateShopMenuDTO dto) throws Exception {
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
                        "중복되는 옵션이 있습니다.", 2
                ));
            }

            // 옵션에 따른 메뉴 가격 저장
            dto.getOption_prices().forEach(optionPrice -> {
                shopMapper.createMenuDetail(new ShopMenuDetail(
                        menu.getId(),
                        (String) optionPrice.get("option"),
                        (Integer) optionPrice.get("price")
                ));
            });
        }

        // TODO: 기획 변경시 최대 업로드 개수 변경하기
        if (dto.getImages().size() > 5) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "메뉴 이미지는 최대 5개까지 등록할 수 있습니다.", 3
            ));
        }
        for (MultipartFile image : dto.getImages()) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload",
                    image.getOriginalFilename(),
                    image.getBytes()
            );

            String url = uploadFileUtils.getDomain() + "/upload" + imgPath;
            shopMapper.createMenuImage(new ShopMenuImage(menu.getId(), url));
        }

        dto.getCategories().forEach(category -> {
            ShopMenuCategory categoryInDatabase = shopMapper.getCategory(
                    dto.getShop_id(), category
            );
            if (categoryInDatabase == null) {
                throw new NotFoundException(new ErrorMessage(
                        String.format("%s(은)는 없는 카테고리입니다.", category), 4
                ));
            }
            shopMapper.createMenuCategoryMap(
                    menu.getId(), categoryInDatabase.getId()
            );
        });

        return new HashMap<String, Object>() {{
            put("message", "메뉴가 새로 등록되었습니다.");
            put("success", true);
        }};
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseShopMenuForOwnerDTO getMenuForOwner(Integer shopId, Integer menuId) throws Exception {
        ResponseShopMenuForOwnerDTO responseDto = shopMapper.getResponseMenuForOwner(shopId, menuId);

        if (responseDto == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 0));
        }

        return responseDto.decideSingleOrOption();
    }

    @Override
    @Transactional
    public Map<String, Object> updateMenuForOwner(UpdateShopMenuDTO dto) throws Exception {
        ShopMenu existingMenu = shopMapper.getMenu(dto.getShop_id(), dto.getId());
        if (existingMenu == null) {
            throw new NotFoundException(new ErrorMessage("없는 메뉴입니다.", 0));
        }

        ShopMenu updatedMenu = new ShopMenu(dto);
        if (updatedMenu.equals(existingMenu)) {
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
                if (existingMenuDetails.size() >= 1) {
                    shopMapper.deleteMenuDetailsByMenuId(dto.getId());
                }
                shopMapper.createMenuDetail(updatedMenuDetail);
            } else {
                existingMenuDetails.remove(updatedMenuDetail);
                existingMenuDetails.forEach(existingMenuDetail -> {
                    shopMapper.deleteMenuDetailsByMenuId(existingMenuDetail.getId());
                });
            }
        } else {
            if (dto.getOption_prices() == null || dto.getOption_prices().size() == 0) {
                throw new ValidationException(new ErrorMessage(
                        "단일 메뉴가 아니면 option_prices를 비워둘 수 없습니다.", 2
                ));
            }

            dto.getOption_prices().forEach(optionPrice -> {
                ShopMenuDetail updatedMenuDetail = new ShopMenuDetail(
                        dto.getId(),
                        (String) optionPrice.get("option"),
                        (Integer) optionPrice.get("price")
                );

                // 추가할 옵션&가격
                if (!existingMenuDetails.contains(updatedMenuDetail)) {
                    shopMapper.createMenuDetail(updatedMenuDetail);
                }

                existingMenuDetails.remove(updatedMenuDetail);
            });

            // 삭제할 옵션&가격
            existingMenuDetails.forEach(menuDetailToDelete -> {
                shopMapper.deleteMenuDetailById(menuDetailToDelete.getId());
            });
        }

        // TODO: 기획 변경시 최대 업로드 개수 변경하기
        if (dto.getImages().size() > 5) {
            throw new PreconditionFailedException(new ErrorMessage(
                    "메뉴 이미지는 최대 5개까지 등록할 수 있습니다.", 3
            ));
        }

        shopMapper.deleteMenuImagesByMenuId(dto.getId());

        for (MultipartFile image : dto.getImages()) {
            String imgPath = uploadFileUtils.uploadFile(
                    "upload",
                    image.getOriginalFilename(),
                    image.getBytes()
            );

            String url = uploadFileUtils.getDomain() + "/upload" + imgPath;
            shopMapper.createMenuImage(new ShopMenuImage(dto.getId(), url));
        }

        List<ShopMenuCategory> existingCategories = shopMapper.getMenuCategoriesOfMenuByMenuId(dto.getId());

        dto.getCategories().forEach(category -> {
            ShopMenuCategory updatedMenuCategory = new ShopMenuCategory(
                    dto.getShop_id(), category
            );

            // 새로 선택된 카테고리
            if (!existingCategories.contains(updatedMenuCategory)) {
                ShopMenuCategory categoryInDatabase = shopMapper.getCategory(dto.getShop_id(), category);
                if (categoryInDatabase == null) {
                    throw new NotFoundException(new ErrorMessage(
                            String.format("%s(은)는 없는 카테고리입니다.", category), 4
                    ));
                }
                shopMapper.createMenuCategoryMap(dto.getId(), categoryInDatabase.getId());
            }

            existingCategories.remove(updatedMenuCategory);
        });

        // 원래 선택됐었지만 이번에 선택되지 않은 카테고리
        existingCategories.forEach(category -> {
            shopMapper.deleteMenuCategoryMap(dto.getId(), category.getId());
        });

        return new HashMap<String, Object>() {{
            put("message", "메뉴가 수정되었습니다.");
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuForOwner(Integer shopId, Integer menuId) throws Exception {
        shopMapper.deleteAllForInvolvedWithMenu(shopId, menuId);

        return new HashMap<String, Object>() {{
            put("message", "메뉴가 삭제되었습니다.");
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> hideMenuForOwner(Integer shopId, Integer menuId, Boolean flag) throws Exception {
        ShopMenu menu = shopMapper.getMenu(shopId, menuId);

        if (menu == null) {
            throw new NotFoundException(new ErrorMessage(
                    "없는 메뉴입니다.", 0
            ));
        }

        if (flag) {
            if (menu.getIs_hidden()) {
                throw new PreconditionFailedException(new ErrorMessage(
                        "이미 숨김 처리된 메뉴입니다.", 1
                ));
            }
            shopMapper.updateMenu(menu.reverseIsHidden());

            return new HashMap<String, Object>() {{
                put("message", String.format("'%s' 메뉴가 정상적으로 숨김 처리 되었습니다.", menu.getName()));
                put("success", true);
            }};
        } else {
            if (!menu.getIs_hidden()) {
                throw new PreconditionFailedException(new ErrorMessage(
                        "이미 숨김 메뉴가 아닙니다.", 2
                ));
            }
            shopMapper.updateMenu(menu.reverseIsHidden());

            return new HashMap<String, Object>() {{
                put("message", String.format("'%s' 메뉴의 숨김 상태가 해제되었습니다.", menu.getName()));
                put("success", true);
            }};
        }
    }

    @Override
    @Transactional
    public ResponseShopMenusForOwnerDTO getMenusForOwner(Integer shopId) throws Exception {
        ResponseShopMenusForOwnerDTO responseDto = shopMapper.getResponseMenusForOwner(shopId);

        if (responseDto == null) {
            throw new NotFoundException(new ErrorMessage("없는 상점입니다.", 0));
        }

        return responseDto.discernSingleOrOption();
    }
}
