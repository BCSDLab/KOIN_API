package koreatech.in.repository;

import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Shop.Menu;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.Shop.ShopViewLog;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    @Select("SELECT * FROM koin.shop_menus WHERE ID = #{id} AND SHOP_ID = #{shop_id}")
    Menu getMenuForAdmin(@Param("shop_id") int shop_id, @Param("id") int id);

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
}