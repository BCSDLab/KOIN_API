package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Criteria.SearchCriteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Shop.Menu;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.Shop.ShopViewLog;
import koreatech.in.domain.User.User;
import koreatech.in.exception.*;
import koreatech.in.repository.ShopMapper;
import koreatech.in.repository.UserMapper;
import koreatech.in.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service("shopService")
public class ShopServiceImpl implements ShopService {
    @Resource(name="shopMapper")
    private ShopMapper shopMapper;
    @Autowired
    JwtValidator jwtValidator;

    @Transactional
    @Override
    public Shop createShopForAdmin(Shop shop) throws Exception {
        if (shopMapper.getShopByNameForAdmin(shop.getName()) != null) {
            throw new ConflictException(new ErrorMessage("shop already exist", 2));
        }
        if (!shop.contain(shop.getCategory())) {
            throw new PreconditionFailedException(new ErrorMessage("No such Category", 2));
        }

        JsonConstructor con = new JsonConstructor();
        //image_urls 체크
        if(StringUtils.hasText(shop.getImage_urls())) {
            if(!con.isArrayStringParse(shop.getImage_urls()))
                throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));
        }

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
        JsonConstructor con = new JsonConstructor();

        List<Shop> shops = shopMapper.getShopListForAdmin(criteria.getCursor(), criteria.getLimit());
        List<Map<String, Object>> convert_shops = new ArrayList<>();
        for(Shop shop : shops) {
            Map<String, Object> map_shop = domainToMap(shop);
            //image_urls 변환
            if(StringUtils.hasText(shop.getImage_urls())) {
                map_shop.replace("image_urls", con.arrayStringParse(shop.getImage_urls()));
            }
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

        JsonConstructor con = new JsonConstructor();
        try {
            for (Menu menu : menus) {
                //menu를 map 객체로 바꾼 후 price_type 값을 array_in_convert_price로 교체, 그것을 다시 convert_menus에 추가
                Map<String, Object> convert_price_menu = domainToMap(menu);
                try {
                    convert_price_menu.replace("price_type", con.arrayObjectParse(menu.getPrice_type()));
                } catch (Exception e) {
                }
                convert_menus.add(convert_price_menu);
            }
        } catch (Exception e) {
        }
        map.put("menus", convert_menus);

        //image_urls 컬럼을 list로
        try {
            //변환된 list로 값 교체
            map.replace("image_urls", con.arrayStringParse(map.get("image_urls").toString()));
        } catch (Exception e) {
            return map;
        }
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

        JsonConstructor con = new JsonConstructor();
        //image_urls 체크
        if(StringUtils.hasText(shop.getImage_urls())) {
            if(!con.isArrayStringParse(shop.getImage_urls()))
                throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));
        }

        if (shop.getName() != null) {
            shop.setInternal_name(shop.getName().replace(" ", "").toLowerCase());
            shop.setChosung(shop.getName().substring(0,1));
        }

        shop_old.update(shop);
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
        JsonConstructor con = new JsonConstructor();
        //price_type 검증
        if (StringUtils.hasText(menu.getPrice_type()))
            if (!con.isArrayObjectParse(menu.getPrice_type()))
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
        JsonConstructor con = new JsonConstructor();
        //price_type 컬럼을 list로
        if(StringUtils.hasText(menu.getPrice_type())) {
            try {
                map.replace("price_type", con.arrayObjectParse(menu.getPrice_type()));
            } catch (Exception e) {
            }
        }
        return map;
    }

    @Transactional
    @Override
    public Menu updateMenuForAdmin(Menu menu, int shop_id, int id) throws Exception {
        Menu menu_old = shopMapper.getMenuForAdmin(shop_id, id);
        //빈 객체인지 체크
        if (menu_old == null)
            throw new NotFoundException(new ErrorMessage("There is no such menu", 0));

        JsonConstructor con = new JsonConstructor();
        ///price_type 검증
        if (StringUtils.hasText(menu.getPrice_type()))
            if (!con.isArrayObjectParse(menu.getPrice_type()))
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

        JsonConstructor con = new JsonConstructor();

        for (Shop shop : shops) {
            shop.setPermalink(shop.getInternal_name());
            Map<String, Object> shopMap = domainToMap(shop);
            if (shopMap.get("image_urls") != null) {
                shopMap.replace("image_urls", con.arrayStringParse(shopMap.get("image_urls").toString()));
            }
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

        JsonConstructor con = new JsonConstructor();
        try {
            for (Menu menu : menus) {
                //menu를 map 객체로 바꾼 후 price_type 값을 array_in_convert_price로 교체, 그것을 다시 convert_menus에 추가
                Map<String, Object> convert_price_menu = domainToMap(menu);
                try {
                    convert_price_menu.replace("price_type", con.arrayObjectParse(menu.getPrice_type()));
                } catch (Exception e) {
                }
                convert_menus.add(convert_price_menu);
            }
        } catch (Exception e) {
        }
        map.put("menus", convert_menus);
        map.put("event_articles", eventArticles);

        //image_urls 컬럼을 list로
        try {
            //변환된 list로 값 교체
            map.replace("image_urls", con.arrayStringParse(map.get("image_urls").toString()));
        } catch (Exception e) {
            return map;
        }
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
}
