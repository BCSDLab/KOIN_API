package koreatech.in.repository;

import koreatech.in.controller.v2.dto.shop.response.ResponseShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.result.ShopMenuResult;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Shop.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("shopMapper")
public interface ShopMapper {
    @Select("SELECT * FROM koin.shops WHERE IS_DELETED = 0")
    List<Shop> getShopList();

    @Select("SELECT * FROM koin.shops WHERE IS_DELETED = 0 AND ID=#{id}")
    Shop getShopById(@Param("id") int id);

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

    @Select("SELECT * FROM koin.shop_menus WHERE NAME=#{name} AND IS_DELETED = 0")
    Shop getShopByName(@Param("name") String name);

    @Select("SELECT * FROM koin.shops WHERE NAME=#{name}")
    Shop getShopByNameForAdmin(@Param("name") String name);

    @Insert("INSERT INTO koin.shops (name, internal_name, chosung, category, phone, open_time, close_time, weekend_open_time, weekend_close_time, image_urls, address, description, delivery, " +
            "delivery_price, pay_card, pay_bank, is_event, remarks, is_deleted) " +
            "VALUES (#{name}, #{internal_name}, #{chosung}, #{category}, #{phone}, #{open_time}, #{close_time}, #{weekend_open_time}, #{weekend_close_time}, #{image_urls}, #{address}, #{description}, #{delivery}, " +
            "#{delivery_price}, #{pay_card}, #{pay_bank}, #{is_event}, #{remarks}, #{is_deleted})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createShopForAdmin(Shop shop);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.shops")
    Integer totalShopCountForAdmin();

    @Select("SELECT * FROM koin.shops ORDER BY created_at DESC LIMIT #{cursor}, #{limit}")
    List<Shop> getShopListForAdmin(@Param("cursor") int cursor, @Param("limit") int limit);

    @Update("UPDATE koin.shops SET NAME=#{name}, INTERNAL_NAME=#{internal_name}, CHOSUNG=#{chosung}, CATEGORY=#{category}, PHONE=#{phone}, OPEN_TIME=#{open_time}, CLOSE_TIME=#{close_time}, weekend_open_time=#{weekend_open_time}, weekend_close_time=#{weekend_close_time}, IMAGE_URLS=#{image_urls}, " +
            "ADDRESS=#{address}, DESCRIPTION=#{description}, DELIVERY=#{delivery}, DELIVERY_PRICE=#{delivery_price}, PAY_CARD=#{pay_card}, PAY_BANK=#{pay_bank}, IS_DELETED=#{is_deleted}, IS_EVENT=#{is_event}, REMARKS=#{remarks} " +
            "WHERE ID = #{id}")
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

    @Select("SELECT name " +
            "FROM koin.shop_menu_categories " +
            "WHERE id IN ( " +
            "SELECT shop_menu_category_id " +
            "FROM koin.shop_shop_menu_category_map " +
            "WHERE shop_id = #{shop_id} AND is_deleted = 0 " +
            ") AND is_deleted = 0")
    List<String> getMenuCategoriesOfShopByShopId(@Param("shop_id") int shopId);

    @Insert("INSERT IGNORE INTO koin.shop_menu_categories " +
            "(`name`) " +
            "VALUES (#{name})")
    @SelectKey(statement = "SELECT id FROM koin.shop_menu_categories " +
                           "WHERE name = #{name}", keyProperty = "id", before = false, resultType = int.class)
    void createMenuCategory(ShopMenuCategory shopMenuCategory);

    @Select("SELECT is_deleted " +
            "FROM koin.shop_shop_menu_category_map " +
            "WHERE shop_id = #{shop_id} AND shop_menu_category_id = #{shop_menu_category_id}")
    Boolean getHistoryOfCategoryDeletion(@Param("shop_id") int shopId, @Param("shop_menu_category_id") int shopMenuCategoryId);

    @Insert("INSERT INTO koin.shop_shop_menu_category_map " +
            "(shop_id, shop_menu_category_id) " +
            "VALUES (#{shop_id}, #{shop_menu_category_id})")
    void createRelationShopAndCategory(@Param("shop_id") int shopId, @Param("shop_menu_category_id") int shopMenuCategoryId);

    @Update("UPDATE koin.shop_shop_menu_category_map " +
            "SET is_deleted = 0 " +
            "WHERE shop_id = #{shop_id} AND shop_menu_category_id = #{shop_menu_category_id}")
    void undoDeletionOfRelationShopAndCategory(@Param("shop_id") int shopId, @Param("shop_menu_category_id") int shopMenuCategoryId);

    @Update("UPDATE koin.shop_shop_menu_category_map " +
            "SET is_deleted = 1 " +
            "WHERE shop_id = #{shop_id} AND shop_menu_category_id = ( " +
            "SELECT id " +
            "FROM koin.shop_menu_categories " +
            "WHERE name = #{name})")
    void deleteRelationShopAndCategory(@Param("shop_id") int shopId, @Param("name") String categoryName);

    @Insert("INSERT INTO koin.shop_menus (shop_id, name, description, is_hidden) " +
            "VALUES (#{shop_id}, #{name}, #{description}, #{is_hidden})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createMenu(ShopMenu shopMenu);

    @Insert("INSERT INTO koin.shop_menu_details (shop_menu_id, `option`, price)" +
            "VALUES (#{shop_menu_id}, #{option}, #{price})")
    void createMenuDetail(ShopMenuDetail shopMenuDetail);

    @Insert("INSERT INTO koin.shop_menu_images (shop_menu_id, image_url) " +
            "VALUES (#{shop_menu_id}, #{image_url})")
    void createMenuImage(ShopMenuImage shopMenuImage);

    @Select("SELECT * FROM koin.shop_menu_categories WHERE name = #{name} AND is_deleted = 0")
    ShopMenuCategory getCategoryByName(@Param("name") String name);

    @Insert("INSERT INTO koin.shop_menu_shop_menu_category_map " +
            "(shop_menu_id, shop_menu_category_id) " +
            "VALUES(#{shop_menu_id}, #{shop_menu_category_id})")
    void createRelationMenuAndCategory(@Param("shop_menu_id") int shopMenuId, @Param("shop_menu_category_id") Integer shopMenuCategoryId);

    @Select("SELECT * FROM koin.shop_menus WHERE id = #{id} AND is_deleted = 0")
    ShopMenu getMenuById(@Param("id") int id);

    @Update("UPDATE koin.shop_menus " +
            "SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "is_hidden = #{is_hidden}, " +
            "is_deleted = #{is_deleted} " +
            "WHERE id = #{id}")
    void updateMenu(ShopMenu shopMenu);

    @Update("UPDATE koin.shop_menu_images " +
            "SET " +
            "is_deleted = 1 " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    void deleteMenuImagesByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Update("UPDATE koin.shop_menu_details " +
            "SET " +
            "is_deleted = 1 " +
            "WHERE shop_menu_id = #{shop_menu_id}")
    void deleteMenuDetailsByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Select("SELECT name " +
            "FROM koin.shop_menu_categories " +
            "WHERE id IN ( " +
            "SELECT shop_menu_category_id " +
            "FROM koin.shop_menu_shop_menu_category_map " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0 " +
            ") AND is_deleted = 0")
    List<String> getMenuCategoriesOfMenuByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Update("UPDATE koin.shop_menu_shop_menu_category_map " +
            "SET is_deleted = 1 " +
            "WHERE shop_menu_id = #{shop_menu_id} AND shop_menu_category_id = ( " +
            "SELECT id " +
            "FROM koin.shop_menu_categories " +
            "WHERE name = #{name})")
    void deleteRelationMenuAndCategory(@Param("shop_menu_id") int shopMenuId, @Param("name") String categoryName);

    @Update("UPDATE " +
            "koin.shop_menus sm, " +
            "koin.shop_menu_details smd, " +
            "koin.shop_menu_images smi, " +
            "koin.shop_menu_shop_menu_category_map smsmcm " +
            "SET " +
            "sm.is_deleted = 1, " +
            "smd.is_deleted = 1, " +
            "smi.is_deleted = 1, " +
            "smsmcm.is_deleted = 1 " +
            "WHERE " +
            "sm.id = #{shop_menu_id} " +
            "AND smd.shop_menu_id = #{shop_menu_id} " +
            "AND smi.shop_menu_id = #{shop_menu_id} " +
            "AND smsmcm.shop_menu_id = #{shop_menu_id}")
    void deleteAllForInvolvedWithMenu(@Param("shop_menu_id") int shop_menu_id);

    ResponseShopMenuDTO getMenuResponse(@Param("shop_menu_id") int shopMenuId);




    @Update("UPDATE koin.shop_menu_categorys SET name = #{name}, is_deleted = #{is_deleted} WHERE id = #{id}")
    void updateMenuCategoryForOwner(ShopMenuCategory shopMenuCategory);

    @Insert("INSERT INTO koin.shop_shop_menu_category_map " +
            "(shop_id, shop_menu_category_id) VALUES (#{shop_id}, #{shop_menu_category_id})")
    void createRelationShopCategory(@Param("shop_id") int shop_id, @Param("shop_menu_category_id") int shop_menu_category_id);

    @Select("SELECT * FROM koin.shop_menu_details " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    List<ShopMenuDetail> getMenuDetailsById(@Param("shop_menu_id") int shop_menu_id);


    @Select("SELECT id FROM koin.shop_menu_categories WHERE name = #{name} AND is_deleted = 0")
    Integer getMenuCategoryIdByName(String name);

    @Select("SELECT name FROM shop_menu_categorys " +
            "WHERE id IN ( " +
            "SELECT shop_menu_category_id " +
            "FROM koin.shop_menu_shop_menu_category_map " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0 " +
            ") ORDER BY id ASC")
    List<String> getSelectedCategoryNamesByMenuId(@Param("shop_menu_id") int shop_menu_id);

    @Select("SELECT shop_menu_id FROM koin.shop_menu_shop_menu_category_map" +
            "WHERE shop_menu_category_id = #{shop_menu_category_id} AND is_deleted = 0")
    List<Integer> get(@Param("shop_menu_category_id") int shop_menu_category_id);

    @Select("SELECT * FROM koin.shop_menu_categorys " +
            "WHERE name = #{name}")
    ShopMenuCategory getCategoryByNameForAdmin(@Param("name") String name);

    @Insert("INSERT INTO koin.shop_menu_categorys (NAME) " +
            "VALUES (#{name})")
    void createCategoryForAdmin(ShopMenuCategory category);

    @Select("SELECT * FROM koin.shop_menu_categorys " +
            "WHERE is_deleted = 0")
    List<ShopMenuCategory> getAllCategoryForAdmin();

    @Select("SELECT * FROM koin.shop_menu_categorys " +
            "WHERE id = #{id}")
    ShopMenuCategory getCategoryByIdForAdmin(@Param("id") int id);

    @Update("UPDATE koin.shop_menu_categorys SET name = #{name}, is_deleted = #{is_deleted} " +
            "WHERE id = #{id}")
    void updateCategoryForAdmin(ShopMenuCategory category);

    @Update("UPDATE koin.shop_menu_categorys SET is_deleted = 1 WHERE id = #{id}")
    void softDeleteCategoryByIdForAdmin(@Param("id") int id);

    @Delete("DELETE FROM koin.shop_menu_categorys WHERE id = #{id}")
    void hardDeleteCategoryByIdForAdmin(@Param("id") int id);

    @Select("SELECT * FROM koin.shop_menus WHERE is_deleted = 0")
    List<Menu> getAllMenusForAdmin();

    @Insert("INSERT INTO koin.shop_menu_category_map (shop_menu_id, shop_menu_category_id) " +
            "VALUES (#{shop_menu_id}, #{shop_menu_category_id})")
    void createMenuCategoryMap(ShopMenuCategoryMap shopMenuCategoryMap);

    @Select("SELECT * FROM koin.shop_menu_details WHERE is_deleted = 0")
    List<ShopMenuDetail> getAllMenuDetailsForAdmin();
}