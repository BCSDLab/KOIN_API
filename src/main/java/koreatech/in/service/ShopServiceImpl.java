package koreatech.in.service;

import koreatech.in.controller.v2.dto.shop.request.CreateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuCategoryDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.response.ResponseShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.result.ResultShopMenuDTO;
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
    public Map<String, Object> createMenuCategoryForAdmin(String categoryName) throws Exception {
        // 이름으로 카테고리 조회
        ShopMenuCategory selectedCategory = shopMapper.getCategoryByNameForAdmin(categoryName);

        // db에 존재하는 경우
        if (selectedCategory != null) {
            // soft delete 되지 않았다면
            if (!selectedCategory.getIs_deleted()) {
                throw new ConflictException(new ErrorMessage("카테고리 이름이 이미 존재합니다.", 0));
            }
            // soft delete 되었다면
            else {
                // soft delete 해제후 업데이트
                selectedCategory.changeDeleteStatus();
                shopMapper.updateCategoryForAdmin(selectedCategory);

                return new HashMap<String, Object>() {{
                    put("success", true);
                }};
            }
        }

        // db에 존재하지 않는 경우 새로 insert
        shopMapper.createCategoryForAdmin(new ShopMenuCategory(categoryName));

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> getAllMenuCategoryForAdmin() throws Exception {
        // soft delete 되지 않은 카테고리들만 조회한다.
        List<ShopMenuCategory> categories = shopMapper.getAllCategoryForAdmin();

        if (categories == null) {
            throw new NotFoundException(new ErrorMessage("카테고리 목록이 없습니다.", 0));
        }
        return new HashMap<String, Object>() {{
            put("categories", categories);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> getMenuCategoryForAdmin(Integer id) throws Exception {
        // id로 카테고리 조회
        ShopMenuCategory category = shopMapper.getCategoryByIdForAdmin(id);

        // DB에 없거나, soft delete 되었다면
        if ((category == null) || (category.getIs_deleted())) {
            throw new NotFoundException(new ErrorMessage("해당 카테고리가 없습니다.", 0));
        }
        return new HashMap<String, Object>() {{
            put("category", category);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> updateMenuCategoryForAdmin(Integer id, String categoryName) throws Exception {
        ShopMenuCategory category = shopMapper.getCategoryByIdForAdmin(id);

        if ((category == null) || (category.getIs_deleted())) {
            throw new NotFoundException(new ErrorMessage("없는 카테고리입니다.", 0));
        }

        // 중복되는 이름의 카테고리가 있는지 확인하기 위해 조회해본다.
        ShopMenuCategory selectedCategoryByName = shopMapper.getCategoryByNameForAdmin(categoryName);
        // 이미 DB에 존재할 경우
        if (selectedCategoryByName != null) {
            // id가 같을때
            if (id.equals(selectedCategoryByName.getId())) {
                throw new ConflictException(new ErrorMessage(String.format("이미 '%s'(으)로 설정되어 있습니다.", categoryName), 1));
            }
            // id가 다를때
            else {
                // soft delete 되어있다면
                if (selectedCategoryByName.getIs_deleted()) {
                    // unique 충돌 방지를 위해 hard delete
                    shopMapper.hardDeleteCategoryByIdForAdmin(selectedCategoryByName.getId());
                }
                // soft delete 안되어있다면
                else {
                    throw new ConflictException(new ErrorMessage("이미 이름이 중복되는 카테고리가 있습니다.", 2));
                }
            }
        }

        shopMapper.updateCategoryForAdmin(new ShopMenuCategory(categoryName));

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuCategoryForAdmin(Integer id) throws Exception {
        // id로 카테고리 조회
        ShopMenuCategory category = shopMapper.getCategoryByIdForAdmin(id);

        // DB에 없거나, soft delete 되었다면
        if ((category == null) || (category.getIs_deleted())) {
            throw new NotFoundException(new ErrorMessage("없는 카테고리입니다.", 0));
        }

        // soft delete
        shopMapper.softDeleteCategoryByIdForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public List<String> getMenuCategoriesOfShopForOwner(Integer shopId) throws Exception {
        return shopMapper.getMenuCategoriesByShopId(shopId);
    }

    @Override
    @Transactional
    public Map<String, Object> updateMenuCategoriesOfShopForOwner(Integer shopId, UpdateShopMenuCategoryDTO dto) {
        List<String> existingCategories = shopMapper.getMenuCategoriesByShopId(shopId);

        dto.getCategories().forEach(category -> {
            // 추가할 카테고리
            if (!existingCategories.contains(category)) {
                ShopMenuCategory newCategory = new ShopMenuCategory(category);
                shopMapper.createMenuCategory(newCategory);

                Boolean isDeleted = shopMapper.getHistoryOfCategoryDeletion(shopId, newCategory.getId());
                if (isDeleted == null) {
                    shopMapper.createRelationShopAndCategory(shopId, newCategory.getId());
                } else {
                    if (isDeleted) {
                        shopMapper.undoDeletionOfRelationShopAndCategory(shopId, newCategory.getId());
                    }
                }
            }
            existingCategories.remove(category);
        });

        // 삭제할 카테고리
        existingCategories.forEach(categoryName -> {
            shopMapper.deleteRelationShopAndCategory(shopId, categoryName);
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
                throw new ValidationException(new ErrorMessage("단일 메뉴는 single_price를 비워둘 수 없습니다.", 0));
            }
            shopMapper.createMenuDetail(new ShopMenuDetail(menu.getId(), dto.getSingle_price()));
        } else {
            if (dto.getOption_prices() == null) {
                throw new ValidationException(new ErrorMessage("단일 메뉴가 아니면 option_prices를 비워둘 수 없습니다.", 1));
            }

            List<Map<String, Integer>> optionPrices = dto.getOption_prices();
            // 옵션에 따른 메뉴 가격 저장
            optionPrices.forEach(optionPrice ->
                optionPrice.forEach((option, price) -> {
                    shopMapper.createMenuDetail(new ShopMenuDetail(menu.getId(), option, price));
                }));
        }

        // TODO: 기획 변경시 최대 업로드 개수 변경하기
        if (dto.getImages().size() > 5) {
            throw new PreconditionFailedException(new ErrorMessage("메뉴 이미지는 최대 5개까지 등록할 수 있습니다.", 2));
        }
        String uploadPath = "upload";
        for (MultipartFile image : dto.getImages()) {
            String imgPath = uploadFileUtils.uploadFile(uploadPath, image.getOriginalFilename(), image.getBytes());
            String url = uploadFileUtils.getDomain() + "/" + uploadPath + imgPath;
            shopMapper.createMenuImage(new ShopMenuImage(menu.getId(), url));
        }

        dto.getCategories().forEach(category -> {
            ShopMenuCategory categoryInDatabase = shopMapper.getCategoryByName(category);
            if (categoryInDatabase == null) {
                throw new NotFoundException(new ErrorMessage(String.format("%s(은)는 없는 카테고리입니다.", category), 3));
            }

            shopMapper.createRelationMenuAndCategory(menu.getId(), categoryInDatabase.getId());
        });

        return new HashMap<String, Object>() {{
            put("message", "메뉴가 새로 등록되었습니다.");
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public ResponseShopMenuDTO getShopMenu(Integer menuId) throws Exception {
        ResponseShopMenuDTO responseDto = new ResponseShopMenuDTO();
        // TODO : SubQuery로 List<Map<>> 조회가 가능한지 알아보기
        ResultShopMenuDTO getResult = shopMapper.getShopMenu(menuId);

        System.out.println(getResult.toString());

        /*
             String name;
             String description;
             Integer single_price;
             List option_prices;
         */

        /*for (ShopMenuResponseDTO dto : getResult) {
            System.out.println(dto.toString());
        }*/

        /*
            String name;                              // shop_menus.name
            Boolean is_single;                        // 코드에서 setting
            Integer single_price;                     // shop_menu_details.price
            List<Map<String, Integer>> option_prices; // shop_menu_details.price
            List<String> existent_categories;         // shop_shop_menu_categorys.name
            List<String> selected_categories;         // shop_menu_shop_menu_categorys.name
            String description;                       // shop_menus.name
            List<String> image_urls;                  // shop_menu_images.image_url
        */

        /*if (getResult == null) {
            throw new NotFoundException(new ErrorMessage("잘못된 요청입니다.", 0));
        }

        // 단일메뉴일 경우
        if (getResult.size() == 1 && getResult.get(0).getOption() == null) {
            responseDTO.setSingleMenu(getResult.get(0).getPrice());
        } else {
            List<Map<String, Integer>> optionPrices = new ArrayList<>();
            getResult.forEach(dto -> {
                if (dto.getShop_id() == null) {
                    throw new NotFoundException(new ErrorMessage("상점 정보가 없습니다.", 1));
                }
                if (dto.getId() == null) {
                    throw new NotFoundException(new ErrorMessage("메뉴 정보가 없습니다.", 2));
                }
                optionPrices.add(new HashMap<String, Integer>() {{
                    put(dto.getOption(), dto.getPrice());
                }});
            });
            responseDTO.addOptionPrice(optionPrices);
        }

        responseDTO.setExistentCategories(shopMapper.getExistentCategoryNamesByShopId(getResult.get(0).getShop_id()));
        responseDTO.setSelectedCategories(shopMapper.getSelectedCategoryNamesByMenuId(menuId));
        responseDTO.setImages(shopMapper.getMenuImageUrls(menuId));*/

        return null;
    }

    @Override
    @Transactional
    public Map<String, Object> updateMenuForOwner(UpdateShopMenuDTO dto) throws Exception {
        /*
            private Integer id;
            private String name; (shop_menus)
            private Boolean is_single;
            private Integer single_price; (shop_menu_details)
            private List<Map<String, Integer>> option_prices; (shop_menu_details)
            private List<String> existent_prices; (shop_menu_categorys, shop_shop_menu_category_map)
            private List<String> selected_prices; (shop_menu_categorys, shop_menu_shop_menu_category_map)
            private String description; (shop_menus)
        */

        //ShopMenu menu = new ShopMenu()

        return null;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuForOwner(Integer menuId) throws Exception {
        shopMapper.deleteAllForInvolvedWithMenuForOwner(menuId);

        return new HashMap<String, Object>() {{
            put("message", "메뉴가 삭제되었습니다.");
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public Map<String, Object> migratePriceType() {
        List<Menu> menus = shopMapper.getAllMenusForAdmin();

        // shop_menus 테이블의 레코드들을 하나씩 순회한다.
        for (Menu menu : menus) {
            Integer shop_menu_id = menu.getId();

            /*
                shop_menus 테이블의 price_type 컬럼 데이터는 JSON의 리스트 형태로 되어있다.
                ex) [{"size":"소","price":"16000"},{"size":"중","price":"20000"},{"size":"대","price":"24000"}]

                이것을 파싱하여 List<Map> 타입으로 가져온다.
             */
            List<Map<String, Object>> priceTypes = con.parseJsonArrayWithObject(menu.getPrice_type());

            /*
                가져온 데이터들을 하나씩 순회하면서 size(shop_menus 테이블에서는 옵션의 의미를 가지고있음)와 price를 추출하여
                shop_menu_details 테이블에 insert 한다.
             */
            for (Map<String, Object> priceType : priceTypes) {
                String option = (String) priceType.get("size");
                String priceString = (String) priceType.get("price");
                int priceInt = Integer.parseInt(priceString);
                Integer price = priceInt;

                ShopMenuDetail shopMenuDetail = new ShopMenuDetail();
                shopMenuDetail.setShop_menu_id(shop_menu_id);
                shopMenuDetail.setPrice(price);

                // 단일 메뉴 (옵션이 '기본'인 경우)는 option을 null로 저장하도록 한다.
                if (option.equals("기본")) {
                    shopMenuDetail.setOption(null);
                } else {
                    shopMenuDetail.setOption(option);
                }

                shopMapper.createMenuDetail(shopMenuDetail);
            }
        }

        // 정상적으로 insert되었는지 확인하기 위해 조회해서 response body에 담아준다.
        List<ShopMenuDetail> menuDetails = shopMapper.getAllMenuDetailsForAdmin();
        return new HashMap<String, Object>() {{
            put("menu_details", menuDetails);
        }};
    }
}
