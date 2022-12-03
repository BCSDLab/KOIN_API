package koreatech.in.repository;

import koreatech.in.dto.shop.request.ShopsCondition;
import koreatech.in.dto.shop.response.ShopResponse;
import koreatech.in.dto.shop.response.MenuResponse;
import koreatech.in.dto.shop.response.MenusResponse;
import koreatech.in.domain.Shop.*;
import koreatech.in.dto.shop.response.inner.MinimizedShop;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("shopMapper")
public interface ShopMapper {
    @Insert("INSERT INTO koin.shops " +
            "(owner_id, `name`, internal_name, chosung, phone, address, `description`, " +
            "delivery, delivery_price, pay_card, pay_bank, is_event, remarks, hit) " +
            "VALUES (#{owner_id}, #{name}, #{internal_name}, #{chosung}, #{phone}, " +
            "#{address}, #{description}, #{delivery}, #{delivery_price}, #{pay_card}, " +
            "#{pay_bank}, #{is_event}, #{remarks}, #{hit})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createShop(Shop shop);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE id = #{id} AND is_deleted = 0")
    Shop getShopById(@Param("id") int id);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE id = #{id}")
    Shop getShopByIgnoreDeletionStatus(@Param("id") int id);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE `name` = #{name} AND is_deleted = 0")
    Shop getShopByName(@Param("name") String name);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE id IN (" +
                "SELECT shop_id " +
                "FROM koin.shop_category_map " +
                "WHERE shop_category_id = #{shop_category_id} AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<Shop> getShopsUsingCategory(@Param("shop_category_id") int shopCategoryId);

    Integer getTotalCountOfShopsByCondition(@Param("condition") ShopsCondition dto);

    @Update("UPDATE koin.shops " +
            "SET " +
                "`owner_id` = #{owner_id}, " +
                "`name` = #{name}, " +
                "internal_name = #{internal_name}, " +
                "chosung = #{chosung}, " +
                "phone = #{phone}, " +
                "address = #{address}, " +
                "`description` = #{description}, " +
                "delivery = #{delivery}, " +
                "delivery_price = #{delivery_price}, " +
                "pay_card = #{pay_card}, " +
                "pay_bank = #{pay_bank}, " +
                "is_event = #{is_event}, " +
                "remarks = #{remarks}, " +
                "hit = #{hit} " +
            "WHERE " +
                "id = #{id}")
    void updateShop(Shop shop);

    @Update("UPDATE koin.shops " +
            "SET is_deleted = 1 " +
            "WHERE id = #{id}")
    void deleteShopById(@Param("id") int id);

    @Update("UPDATE koin.shops " +
            "SET is_deleted = 0 " +
            "WHERE id = #{id}")
    void undeleteShopById(@Param("id") int id);

    @Insert("INSERT INTO koin.shop_categories " +
            "(`name`, image_url) " +
            "VALUES (#{name}, #{image_url})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createShopCategory(ShopCategory shopCategory);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE id = #{id} AND is_deleted = 0")
    ShopCategory getShopCategoryById(@Param("id") int id);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE `name` = #{name} AND is_deleted = 0")
    ShopCategory getShopCategoryByName(String name);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE id IN (" +
                "SELECT shop_category_id " +
                "FROM koin.shop_category_map " +
                "WHERE shop_id = #{shop_id} AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<ShopCategory> getShopCategoriesOfShopByShopId(@Param("shop_id") int shopId);

    @Select("SELECT COUNT(*) " +
            "FROM koin.shop_menu_categories " +
            "WHERE shop_id = #{shop_id} AND is_deleted = 0")
    Integer getCountOfShopCategoriesByShopId(@Param("shop_id") int shopId);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE is_deleted = 0")
    List<koreatech.in.dto.shop.response.inner.ShopCategory> getAllShopCategories();

    @Update("UPDATE koin.shop_categories " +
            "SET " +
                "`name` = #{name}, " +
                "image_url = #{image_url} " +
            "WHERE id = #{id}")
    void updateShopCategory(ShopCategory updatedCategory);

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

    @Update("UPDATE koin.shop_images " +
            "SET is_deleted = 1 " +
            "WHERE shop_id = #{shop_id} AND is_deleted = 0")
    void deleteShopImagesByShopId(@Param("shop_id") int shopId);

    @Insert("INSERT INTO koin.shop_menus " +
            "(shop_id, `name`, `description`, is_hidden) " +
            "VALUES (#{shop_id}, #{name}, #{description}, #{is_hidden})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createMenu(ShopMenu shopMenu);

    @Select("SELECT * " +
            "FROM koin.shop_menus " +
            "WHERE id = #{shop_menu_id} AND is_deleted = 0")
    ShopMenu getMenuById(@Param("shop_menu_id") int shopMenuId);

    @Select("SELECT * " +
            "FROM koin.shop_menus " +
            "WHERE id IN (" +
                "SELECT shop_menu_id " +
                "FROM koin.shop_menu_category_map " +
                "WHERE shop_menu_category_id = #{shop_menu_category_id} AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<ShopMenu> getMenusUsingCategory(@Param("shop_menu_category_id") int shopMenuCategoryId);

    @Update("UPDATE koin.shop_menus " +
            "SET " +
                "`name` = #{name}, " +
                "`description` = #{description}, " +
                "is_hidden = #{is_hidden} " +
            "WHERE id = #{id}")
    void updateMenu(ShopMenu shopMenu);

    @Insert("INSERT INTO koin.shop_menu_categories " +
            "(shop_id, `name`) " +
            "VALUES (#{shop_id}, #{name})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createMenuCategory(ShopMenuCategory shopMenuCategory);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE id = #{id} AND is_deleted = 0")
    ShopMenuCategory getMenuCategoryById(@Param("id") int id);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE shop_id = #{shop_id} AND `name` = #{name} AND is_deleted = 0")
    ShopMenuCategory getMenuCategory(@Param("shop_id") int shopId, @Param("name") String name);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE id IN (" +
                "SELECT shop_menu_category_id " +
                "FROM koin.shop_menu_category_map " +
                "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<ShopMenuCategory> getMenuCategoriesOfMenu(@Param("shop_menu_id") int shopMenuId);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE shop_id = #{shop_id} AND is_deleted = 0")
    List<koreatech.in.dto.shop.response.inner.ShopMenuCategory> getMenuCategoriesOfShop(@Param("shop_id") int shopId);

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

    @Insert("INSERT INTO koin.shop_menu_details " +
            "(shop_menu_id, `option`, price)" +
            "VALUES (#{shop_menu_id}, #{option}, #{price})")
    void createMenuDetail(ShopMenuDetail shopMenuDetail);

    @Select("SELECT * " +
            "FROM koin.shop_menu_details " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    List<ShopMenuDetail> getMenuDetailsByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Update("UPDATE koin.shop_menu_details " +
            "SET is_deleted = 1 " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    void deleteMenuDetailsByMenuId(@Param("shop_menu_id") int shopMenuId);

    @Update("UPDATE koin.shop_menu_images " +
            "SET is_deleted = 1 " +
            "WHERE shop_menu_id = #{shop_menu_id} AND is_deleted = 0")
    void deleteMenuImagesByMenuId(@Param("shop_menu_id") int shopMenuId);

    void createShopOpens(List<ShopOpen> shopOpen);

    void createShopCategoryMaps(List<ShopCategoryMap> shopCategoryMaps);

    void createShopImages(List<ShopImage> shopImages);

    void createMenuCategories(List<ShopMenuCategory> shopMenuCategories);

    void createMenuDetails(List<ShopMenuDetail> shopMenuDetails);

    void createMenuImages(List<ShopMenuImage> shopMenuImages);

    void createMenuCategoryMaps(List<ShopMenuCategoryMap> shopMenuCategoryMaps);

    MenuResponse getResponseMenu(@Param("shop_menu_id") int shopMenuId);

    MenusResponse getResponseMenus(@Param("shop_id") int shopId);

    ShopResponse getResponseShop(@Param("shop_id") int shopId);

    ShopResponse getResponseShopByIgnoreDeletion(@Param("shop_id") int shopId);

    List<koreatech.in.dto.shop.response.inner.Shop> getShops(@Param("begin") int begin, @Param("limit") int limit);

    List<MinimizedShop> getShopsByCondition(@Param("begin") Integer begin, @Param("condition") ShopsCondition condition);

    List<koreatech.in.dto.shop.response.inner.Shop> getAllShops();

    void updateShopOpens(List<ShopOpen> updatedShopOpens);

    void deleteShopCategoryMaps(List<ShopCategoryMap> shopCategoryMapsToDelete);

    void deleteMenuDetails(List<ShopMenuDetail> existingMenuDetails);

    void deleteMenuCategoryMaps(List<ShopMenuCategoryMap> menuCategoryMapsToDelete);

    void deleteAllForInvolvedWithMenu(@Param("shop_menu_id") int shopMenuId);
}