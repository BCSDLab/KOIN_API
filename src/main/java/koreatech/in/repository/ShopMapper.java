package koreatech.in.repository;

import koreatech.in.dto.shop.response.ResponseShopDTO;
import koreatech.in.dto.shop.response.ResponseShopMenuDTO;
import koreatech.in.dto.shop.response.ResponseShopMenusDTO;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Shop.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("shopMapper")
public interface ShopMapper {
    @Select("SELECT * FROM koin.shops WHERE IS_DELETED = 0")
    List<Shop> getShopList();

    @Select("SELECT * FROM koin.shops WHERE IS_DELETED = 0 AND INTERNAL_NAME=#{id}")
    Shop getShopByInternalName(@Param("id") String id);

    @Select("SELECT * FROM koin.shops WHERE INTERNAL_NAME=#{id}")
    Shop getShopByInternalNameForAdmin(@Param("id") String id);

    @Select("SELECT * FROM koin.shops WHERE ID=#{id}")
    Shop getShopForAdmin(@Param("id") int id);

    @Select("SELECT id, shop_id, title, event_title, content, user_id, nickname, start_date, end_date FROM koin.event_articles WHERE SHOP_ID=#{id} AND date(now()) BETWEEN date(START_DATE) AND date(END_DATE) AND IS_DELETED = 0")
    List<EventArticle> getPendingEventByShopId(@Param("id") int id);

    @Select("SELECT * FROM koin.shop_menus WHERE SHOP_ID=#{shop_id} AND IS_DELETED = 0")
    List<Menu> getMenus(@Param("shop_id") int shop_id);

    @Select("SELECT * FROM koin.shop_menus WHERE SHOP_ID=#{shop_id}")
    List<Menu> getMenusForAdmin(@Param("shop_id") int shop_id);

    @Select("SELECT * FROM koin.shops WHERE NAME=#{name}")
    Shop getShopByNameForAdmin(@Param("name") String name);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.shops")
    Integer totalShopCountForAdmin();

    @Select("SELECT * FROM koin.shops ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Shop> getShopListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Update("UPDATE koin.shops " +
            "SET " +
                "name = #{name}, " +
                "internal_name = #{internal_name}, " +
                "chosung = #{chosung}, " +
                "phone = #{phone}, " +
                "address = #{address}, " +
                "description = #{description}, " +
                "delivery = #{delivery}, " +
                "delivery_price = #{delivery_price}, " +
                "pay_card = #{pay_card}, " +
                "pay_bank = #{pay_bank}, " +
                "is_event = #{is_event}, " +
                "remarks = #{remarks}, " +
                "hit = #{hit}" +
            "WHERE " +
                "id = #{id}")
    void updateShopForAdmin(Shop shop);

    @Delete("DELETE FROM koin.shops WHERE ID = #{id}")
    void deleteShopForAdmin(@Param("id") int id);

    @Insert("INSERT INTO koin.shop_menus (shop_id, name, price_type, is_deleted)" +
            "VALUES (#{shop_id}, #{name}, #{price_type}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createMenuForAdmin(Menu menu);

    @Select("SELECT * FROM koin.shop_menus WHERE ID = #{id}")
    Menu getMenuForAdmin(@Param("id") int id);

    @Update("UPDATE koin.shop_menus SET SHOP_ID=#{shop_id}, NAME=#{name}, PRICE_TYPE=#{price_type}, IS_DELETED=#{is_deleted} WHERE ID = #{id}")
    void updateMenuForAdmin(Menu menu);

    @Delete("DELETE FROM koin.shop_menus WHERE ID = #{id}")
    void deleteMenuForAdmin(@Param("id") int id);

    // Shop view Logging
    @Select("SELECT * FROM koin.shop_view_logs WHERE SHOP_ID = #{shop_id} AND USER_ID = #{user_id} ORDER BY ID DESC LIMIT 1")
    ShopViewLog getViewLog(@Param("shop_id") int shop_id, @Param("user_id") int user_id);

    @Insert("INSERT INTO koin.shop_view_logs (SHOP_ID, USER_ID, EXPIRED_AT, IP)" +
            "VALUES (#{shop_id}, #{user_id}, #{expired_at}, #{ip})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createViewLog(ShopViewLog viewLog);

    @Update("UPDATE koin.shop_view_logs SET SHOP_ID=#{shop_id}, USER_ID=#{user_id}, EXPIRED_AT=#{expired_at}, IP=#{ip} WHERE ID = #{id}")
    void updateViewLog(ShopViewLog viewLog);

    @Update("UPDATE koin.shops SET HIT = HIT + 1 WHERE IS_DELETED = 0 AND ID = #{id}")
    void increaseHit(@Param("id") int id);

    // -------------------- 아래부터 코인 리뉴얼시 개발된 메소드 ----------------------
    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE name = #{name} AND is_deleted = 0")
    Shop getShopByName(@Param("name") String name);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE id = #{id} AND is_deleted = 0")
    Shop getShopById(@Param("id") int id);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE shop_id = #{shop_id} AND is_deleted = 0")
    List<ShopMenuCategory> getMenuCategoriesOfShop(@Param("shop_id") int shopId);

    @Insert("INSERT INTO koin.shop_menu_categories " +
            "(shop_id, `name`) " +
            "VALUES (#{shop_id}, #{name})")
    void createMenuCategory(ShopMenuCategory shopMenuCategory);

    @Update("UPDATE (" +
                "koin.shop_menu_categories smc " +
                "LEFT JOIN koin.shop_menu_category_map smcm " +
                "ON smc.id = smcm.shop_menu_category_id" +
            ")" +
            "SET " +
                "smc.is_deleted = 1, " +
                "smcm.is_deleted = 1 " +
            "WHERE " +
                "smc.id = #{id}")
    void deleteMenuCategoryById(@Param("id") int id);

    @Insert("INSERT INTO koin.shop_menus " +
            "(shop_id, name, description, is_hidden) " +
            "VALUES (#{shop_id}, #{name}, #{description}, #{is_hidden})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createMenu(ShopMenu shopMenu);

    @Insert("INSERT INTO koin.shop_menu_details " +
            "(shop_menu_id, `option`, price)" +
            "VALUES (#{shop_menu_id}, #{option}, #{price})")
    void createMenuDetail(ShopMenuDetail shopMenuDetail);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE shop_id = #{shop_id} AND name = #{name} AND is_deleted = 0")
    ShopMenuCategory getMenuCategory(@Param("shop_id") int shopId, @Param("name") String name);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE id = #{id} AND is_deleted = 0")
    ShopMenuCategory getMenuCategoryById(@Param("id") int id);

    ResponseShopMenuDTO getResponseMenu(@Param("shop_menu_id") int shopMenuId);

    void deleteAllForInvolvedWithMenu(@Param("shop_menu_id") int shopMenuId);

    @Select("SELECT * " +
            "FROM koin.shop_menus " +
            "WHERE id = #{shop_menu_id} AND is_deleted = 0")
    ShopMenu getMenu(@Param("shop_menu_id") int shopMenuId);

    @Update("UPDATE koin.shop_menus " +
            "SET " +
                "name = #{name}, " +
                "description = #{description}, " +
                "is_hidden = #{is_hidden} " +
            "WHERE id = #{id}")
    void updateMenu(ShopMenu shopMenu);

    ResponseShopMenusDTO getResponseMenus(@Param("shop_id") int shopId);

    @Select("SELECT * " +
            "FROM koin.shop_menu_details " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    List<ShopMenuDetail> getMenuDetailsByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Update("UPDATE koin.shop_menu_details " +
            "SET " +
            "is_deleted = 1 " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    void deleteMenuDetailsByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Update("UPDATE koin.shop_menu_images " +
            "SET " +
            "is_deleted = 1 " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    void deleteMenuImagesByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE id IN (" +
                "SELECT shop_menu_category_id " +
                "FROM koin.shop_menu_category_map " +
                "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<ShopMenuCategory> getMenuCategoriesOfMenu(@Param("shop_menu_id") int shopMenuId);

    @Select("SELECT * " +
            "FROM koin.shop_menus " +
            "WHERE id IN (" +
                "SELECT shop_menu_id " +
                "FROM koin.shop_menu_category_map " +
                "WHERE shop_menu_category_id = #{shop_menu_category_id} AND is_deleted = 0" +
            ")")
    List<ShopMenu> getMenusUsingCategory(@Param("shop_menu_category_id") int shopMenuCategoryId);

    @Update("UPDATE koin.shop_images " +
            "SET is_deleted = 1 " +
            "WHERE shop_id = #{shop_id} AND is_deleted = 0")
    void deleteShopImagesByShopId(@Param("shop_id") int shopId);

    @Insert("INSERT INTO shop_images " +
            "(shop_id, image_url) " +
            "VALUES (#{shop_id}, #{image_url})")
    void createShopImage(ShopImage shopImage);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE id = #{id} AND is_deleted = 0")
    ShopCategory getShopCategoryById(@Param("id") int id);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE id IN (" +
                "SELECT shop_category_id " +
                "FROM koin.shop_category_map " +
                "WHERE shop_id = #{shop_id} AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<ShopCategory> getShopCategoriesOfShopByShopId(@Param("shop_id") int shopId);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE name = #{name} AND is_deleted = 0")
    ShopCategory getShopCategoryByName(String name);

    @Insert("INSERT INTO koin.shop_categories " +
            "(name, image_url) " +
            "VALUES (#{name}, #{image_url})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createShopCategory(ShopCategory shopCategory);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE id IN (" +
                "SELECT shop_id " +
                "FROM koin.shop_category_map " +
                "WHERE shop_category_id = #{shop_category_id} AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<Shop> getShopsUsingCategory(@Param("shop_category_id") int shopCategoryId);

    @Update("UPDATE (" +
                "koin.shop_categories sc " +
                "LEFT JOIN koin.shop_category_map scm " +
                "ON sc.id = scm.shop_category_id" +
            ") " +
            "SET " +
                "sc.is_deleted = 1, " +
                "scm.is_deleted = 1 " +
            "WHERE " +
                "sc.id = #{id}")
    void deleteShopCategoryById(@Param("id") int id);

    @Update("UPDATE koin.shop_categories " +
            "SET " +
                "name = #{name}, " +
                "image_url = #{image_url} " +
            "WHERE id = #{id}")
    void updateShopCategory(ShopCategory updatedCategory);

    ResponseShopDTO getResponseShop(@Param("shop_id") int shopId);


    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE is_deleted = 0")
    List<ShopCategory> getAllShopCategories();

    @Insert("INSERT INTO koin.shops " +
            "(owner_id, `name`, internal_name, chosung, phone, address, `description`, " +
            "delivery, delivery_price, pay_card, pay_bank, is_event, remarks, hit) " +
            "VALUES (#{owner_id}, #{name}, #{internal_name}, #{chosung}, #{phone}, " +
            "#{address}, #{description}, #{delivery}, #{delivery_price}, #{pay_card}, " +
            "#{pay_bank}, #{is_event}, #{remarks}, #{hit})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createShop(Shop shop);

    @Insert("INSERT INTO koin.shop_open " +
            "(shop_id, day_of_week, is_closed, open_time, close_time) " +
            "VALUES (#{shop_id}, #{day_of_week}, #{is_closed}, #{open_time}, #{close_time})")
    void createShopOpen(ShopOpen shopOpen);

    @Insert("INSERT INTO koin.shop_category_map " +
            "(shop_id, shop_category_id) " +
            "VALUES (#{shop_id}, #{shop_category_id})")
    void createShopCategoryMap(@Param("shop_id") int shopId, @Param("shop_category_id") int shopCategoryId);

    List<ShopMenu> getShopMenusByShopId(@Param("shop_id") int shopId);

    void deleteAllForInvolvedWithShop(@Param("shop_id") int shopId);

    void createMenuImages(List<ShopMenuImage> shopMenuImages);

    void createMenuDetails(List<ShopMenuDetail> shopMenuDetails);

    void createMenuCategoryMaps(List<ShopMenuCategoryMap> shopMenuCategoryMaps);

    void deleteMenuDetails(List<ShopMenuDetail> existingMenuDetails);

    void deleteMenuCategoryMaps(List<ShopMenuCategoryMap> menuCategoryMapsToDelete);
}