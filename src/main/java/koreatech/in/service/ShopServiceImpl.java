package koreatech.in.service;

import koreatech.in.controller.v2.dto.ShopMenuDTO;
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
import javax.validation.Valid;
import java.util.*;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service("shopService")
public class ShopServiceImpl implements ShopService {
    @Resource(name="shopMapper")
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
        if(shop.getImage_urls() != null && !con.isJsonArrayWithOnlyString(shop.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        shop.setInternal_name(shop.getName().replace(" ","").toLowerCase());

        shop.setChosung(shop.getName().substring(0,1));

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
        for(Shop shop : shops) {
            Map<String, Object> map_shop = domainToMap(shop);
            map_shop.replace("image_urls", con.parseJsonArrayWithOnlyString(shop.getImage_urls()));
            convert_shops.add(map_shop);
        }

        double totalCount = shopMapper.totalShopCountForAdmin();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage<0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("shops", convert_shops);
        map.put("totalPage",totalPage);

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
        }
        else {
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
        if(shop.getImage_urls() != null && !con.isJsonArrayWithOnlyString(shop.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        if (shop.getName() != null) {
            shop.setInternal_name(shop.getName().replace(" ", "").toLowerCase());
            shop.setChosung(shop.getName().substring(0,1));
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

        Menu menu = shopMapper.getMenuForAdmin(shop_id, menu_id);
        if (menu == null)
            throw new NotFoundException(new ErrorMessage("There is no such menu", 0));

        Map<String, Object> map = domainToMap(menu);
        map.replace("price_type", con.parseJsonArrayWithObject(menu.getPrice_type()));
        return map;
    }

    @Transactional
    @Override
    public Menu updateMenuForAdmin(Menu menu, int shop_id, int id) throws Exception {
        Menu menu_old = shopMapper.getMenuForAdmin(shop_id, id);
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
    public Map<String, Object> deleteMenuForAdmin(int shop_id, int id) throws Exception {
        Menu menu = shopMapper.getMenuForAdmin(shop_id, id);
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
        }
        else {
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
            }
            else {
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
                selectedCategory.setIs_deleted(false);
                shopMapper.updateCategoryForAdmin(selectedCategory);

                return new HashMap<String, Object>() {{
                    put("success", true);
                }};
            }
        }

        // db에 존재하지 않는 경우 새로 insert
        ShopMenuCategory newCategory = new ShopMenuCategory();
        newCategory.setName(categoryName);
        shopMapper.createCategoryForAdmin(newCategory);

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
            if (id == selectedCategoryByName.getId()) {
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

        category.setName(categoryName);
        shopMapper.updateCategoryForAdmin(category);

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
    public Map<String, Object> createMenuForOwner(ShopMenuDTO dto, List<MultipartFile> images) throws Exception {
        // TODO: ShopMenuDTO 데이터 유효성 검증

        // shop_menus 테이블에 메뉴 정보 저장
        ShopMenu menu = new ShopMenu();
        menu.init(dto);
        shopMapper.createMenuForOwner(menu);

        // shop_menu_details 테이블에 옵션에 따른 가격 정보 저장
        ShopMenuDetail menuDetail = new ShopMenuDetail();
        // 단일 메뉴일 경우
        if (dto.getIs_single()) {
            menuDetail.initForSingleMenu(menu.getId(), dto.getSingle_price());
            shopMapper.createMenuDetailForAdmin(menuDetail);
        }
        // 단일 메뉴가 아닐 경우
        else {
            List<Map<String, Integer>> optionPrices = dto.getOption_prices();
            for (Map<String, Integer> optionPrice : optionPrices) {
                optionPrice.forEach((option, price) -> {
                    menuDetail.initForOptionMenu(menu.getId(), option, price);
                    shopMapper.createMenuDetailForAdmin(menuDetail);
                });
            }
        }

        // shop_menu_categorys 테이블에서 카테고리 존재 여부 확인 후 shop_menu_category_map에 관계 정보 저장
        List<Integer> menuCategoryIds = dto.getCategory_ids();
        for (Integer menuCategoryId : menuCategoryIds) {
            ShopMenuCategory menuCategory = shopMapper.getCategoryByIdForAdmin(menuCategoryId);
            if ((menuCategory == null) || (menuCategory.getIs_deleted())) {
                throw new NotFoundException(new ErrorMessage(String.format("%d는(은) 존재하지 않는 카테고리 id 입니다.", menuCategoryId), 0));
            }

            ShopMenuCategoryMap menuCategoryMap = new ShopMenuCategoryMap();
            menuCategoryMap.setMapping(menu.getId(), menuCategoryId);
            shopMapper.createMenuCategoryMap(menuCategoryMap);
        }

        // 상점 메뉴 이미지 5개 이상 등록 불가
        if (images.size() > 5) {
            throw new PreconditionFailedException(new ErrorMessage("메뉴 이미지는 5개 이상 등록할 수 없습니다.", 1));
        }

        // 상점 메뉴 이미지 AWS S3에 업로드 후 shop_menu_images 테이블에 정보 저장
        String uploadPath = "upload";
        for (MultipartFile image : images) {
            String img_path = uploadFileUtils.uploadFile(uploadPath, image.getOriginalFilename(), image.getBytes());
            String url = uploadFileUtils.getDomain() + "/" + uploadPath + img_path;
            ShopMenuImage menuImage = new ShopMenuImage();
            menuImage.setImage(menu.getId(), url);
            shopMapper.createMenuImageForOwner(menuImage);
        }

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    @Transactional
    public ShopMenuDTO getShopMenu(Integer shopId, Integer menuId) throws Exception {
        ShopMenuDTO responseDTO = new ShopMenuDTO();
        /*
            < 단일 메뉴일때 응답 DTO >
            id               : Integer  O
            shop_id          : Integer  O
            name             : String   O
            is_single        : Boolean  O
            single_price     : Integer  O
            category_ids : List<Integer> O
            description      : String   O
            image_urls   : List<String> O

            < 단일 메뉴 아닐때 응답 DTO>
            id               : Integer  O
            shop_id          : Integer  O
            name             : String   O
            is_single        : Boolean  O
            option_prices    : List<Map<String, Integer>> O
            category_ids     : List<Integer> O
            description      : String   O
            image_urls       : List<String> O
        */

        Shop shop = shopMapper.getShopById(shopId);
        if (shop == null) {
            throw new NotFoundException(new ErrorMessage("상점 정보가 없습니다.", 0));
        }
        responseDTO.setShop_id(shop.getId());

        ShopMenu menu = shopMapper.getMenuByIdForOwner(menuId);
        if ((menu == null) || (menu.getIs_deleted())) {
            throw new NotFoundException(new ErrorMessage("메뉴 정보가 없습니다.", 1));
        }
        responseDTO.setId(menu.getId());
        responseDTO.setName(menu.getName());
        responseDTO.setDescription(menu.getDescription());

        List<ShopMenuDetail> menuDetails = shopMapper.getMenuDetailByIdForOwner(menuId);
        if (menuDetails.size() == 0) {
            throw new NotFoundException(new ErrorMessage("메뉴 가격 정보가 없습니다.", 2));
        }

        // shop_menu_details 테이블에 menuId와 대응하는 레코드가 단일 메뉴일때
        if ((menuDetails.size() == 1) && (menuDetails.get(0).getOption() == null)) {
            responseDTO.setIs_single(true);
            responseDTO.setSingle_price(menuDetails.get(0).getPrice());
        }
        // shop_menu_details 태이블에 menuId와 대응하는 레코드가 2개 이상 존재할때
        else {
            responseDTO.setIs_single(false);
            List<Map<String, Integer>> optionPrices = new ArrayList<>();
            for (ShopMenuDetail menuDetail : menuDetails) {
                optionPrices.add(new HashMap<String, Integer>() {{
                    put(menuDetail.getOption(), menuDetail.getPrice());
                }});
            }
            responseDTO.setOption_prices(optionPrices);
        }

        List<ShopMenuCategoryMap> menuCategoryMaps = shopMapper.getMenuCategoryMapsForOwner(menuId);
        List<Integer> categoryIds = new ArrayList<>();
        for (ShopMenuCategoryMap menuCategoryMap : menuCategoryMaps) {
            categoryIds.add(menuCategoryMap.getShop_menu_category_id());
        }
        responseDTO.setCategory_ids(categoryIds);

        List<ShopMenuImage> menuImages = shopMapper.getMenuImagesForOwner(menuId);
        List<String> urls = new ArrayList<>();
        for (ShopMenuImage menuImage : menuImages) {
            urls.add(menuImage.getImage_url());
        }
        responseDTO.setImage_urls(urls);

        return responseDTO;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteMenuForOwner(Integer shopId, Integer menuId) {
        return null;
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

                shopMapper.createMenuDetailForAdmin(shopMenuDetail);
            }
        }

        // 정상적으로 insert되었는지 확인하기 위해 조회해서 response body에 담아준다.
        List<ShopMenuDetail> menuDetails = shopMapper.getAllMenuDetailsForAdmin();
        return new HashMap<String, Object>() {{
            put("menu_details", menuDetails);
        }};
    }
}
