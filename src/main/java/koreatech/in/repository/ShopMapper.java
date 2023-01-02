package koreatech.in.repository;

import koreatech.in.domain.Shop.*;
import koreatech.in.dto.shop.admin.request.ShopCategoriesCondition;
import koreatech.in.dto.shop.admin.request.ShopsCondition;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("shopMapper")
public interface ShopMapper {
    // ------------------------------------------ Annotation ------------------------------------------
    // ============================================= 상점 ==============================================
    @Insert("INSERT INTO koin.shops " +
            "(owner_id, `name`, internal_name, chosung, phone, address, `description`, " +
            "delivery, delivery_price, pay_card, pay_bank) " +
            "VALUES (#{shop.owner_id}, #{shop.name}, #{shop.internal_name}, #{shop.chosung}, #{shop.phone}, " +
            "#{shop.address}, #{shop.description}, #{shop.delivery}, #{shop.delivery_price}, #{shop.pay_card}, " +
            "#{shop.pay_bank})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "shop.id", before = false, resultType = Integer.class)
    void createShopForAdmin(@Param("shop") Shop shop);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE id = #{id}")
    Shop getShopByIdForAdmin(@Param("id") Integer id);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE " +
                "`name` = #{name} " +
                "AND is_deleted = 0")
    Shop getShopByNameForAdmin(@Param("name") String name);

    @Select("SELECT * " +
            "FROM koin.shops " +
            "WHERE id IN (" +
                "SELECT shop_id " +
                "FROM koin.shop_category_map " +
                "WHERE " +
                    "shop_category_id = #{shopCategoryId} " +
                    "AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<Shop> getShopsUsingCategoryForAdmin(@Param("shopCategoryId") Integer shopCategoryId);

    @Select("SELECT image_url " +
            "FROM koin.shop_images " +
            "WHERE " +
                "shop_id = #{shopId} " +
                "AND is_deleted = 0")
    List<String> getShopImageUrlsByShopIdForAdmin(@Param("shopId") Integer shopId);

    @Update("UPDATE koin.shops " +
            "SET " +
                "`owner_id` = #{shop.owner_id}, " +
                "`name` = #{shop.name}, " +
                "internal_name = #{shop.internal_name}, " +
                "chosung = #{shop.chosung}, " +
                "phone = #{shop.phone}, " +
                "address = #{shop.address}, " +
                "`description` = #{shop.description}, " +
                "delivery = #{shop.delivery}, " +
                "delivery_price = #{shop.delivery_price}, " +
                "pay_card = #{shop.pay_card}, " +
                "pay_bank = #{shop.pay_bank}, " +
                "is_event = #{shop.is_event}, " +
                "remarks = #{shop.remarks}, " +
                "hit = #{shop.hit} " +
            "WHERE " +
                "id = #{shop.id}")
    void updateShopForAdmin(@Param("shop") Shop shop);

    @Update("UPDATE koin.shops " +
            "SET is_deleted = 1 " +
            "WHERE id = #{id}")
    void deleteShopByIdForAdmin(@Param("id") Integer id);

    @Update("UPDATE koin.shops " +
            "SET is_deleted = 0 " +
            "WHERE id = #{id}")
    void undeleteShopByIdForAdmin(@Param("id") Integer id);


    // =========================================== 상점 카테고리 ===========================================
    @Insert("INSERT INTO koin.shop_categories " +
            "(`name`, image_url) " +
            "VALUES (#{shopCategory.name}, #{shopCategory.image_url})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "shopCategory.id", before = false, resultType = Integer.class)
    void createShopCategoryForAdmin(@Param("shopCategory") ShopCategory shopCategory);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE " +
                "id = #{id} " +
                "AND is_deleted = 0")
    ShopCategory getShopCategoryByIdForAdmin(@Param("id") Integer id);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE " +
                "`name` = #{name} " +
                "AND is_deleted = 0")
    ShopCategory getShopCategoryByNameForAdmin(@Param("name") String name);

    @Select("SELECT id " +
            "FROM koin.shop_categories " +
            "WHERE id IN (" +
                "SELECT shop_category_id " +
                "FROM koin.shop_category_map " +
                "WHERE " +
                    "shop_id = #{shopId} " +
                    "AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<Integer> getShopCategoryIdsOfShopByShopIdForAdmin(@Param("shopId") Integer shopId);

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE is_deleted = 0")
    List<ShopCategory> getAllShopCategoriesForAdmin();

    @Select("SELECT * " +
            "FROM koin.shop_categories " +
            "WHERE is_deleted = 0")
    List<ShopCategory> getAllShopCategories();

    @Select("SELECT COUNT(*) " +
            "FROM koin.shop_menu_categories " +
            "WHERE " +
                "shop_id = #{shopId} " +
                "AND is_deleted = 0")
    Integer getCountOfMenuCategoriesByShopIdForAdmin(@Param("shopId") Integer shopId);

    @Update("UPDATE koin.shop_categories " +
            "SET " +
                "`name` = #{shopCategory.name}, " +
                "image_url = #{shopCategory.image_url} " +
            "WHERE id = #{shopCategory.id}")
    void updateShopCategoryForAdmin(@Param("shopCategory") ShopCategory shopCategory);

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
    void deleteShopCategoryByIdForAdmin(@Param("id") Integer id);


    // ============================================= 메뉴 =============================================
    @Insert("INSERT INTO koin.shop_menus " +
            "(shop_id, `name`, `description`, is_hidden) " +
            "VALUES (#{shopMenu.shop_id}, #{shopMenu.name}, #{shopMenu.description}, #{shopMenu.is_hidden})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "shopMenu.id", before = false, resultType = Integer.class)
    void createMenuForAdmin(@Param("shopMenu") ShopMenu shopMenu);

    @Select("SELECT * " +
            "FROM koin.shop_menus " +
            "WHERE " +
                "id = #{shopMenuId} " +
                "AND is_deleted = 0")
    ShopMenu getMenuByIdForAdmin(@Param("shopMenuId") Integer shopMenuId);

    @Select("SELECT * " +
            "FROM koin.shop_menus " +
            "WHERE id IN (" +
                "SELECT shop_menu_id " +
                "FROM koin.shop_menu_category_map " +
                "WHERE " +
                    "shop_menu_category_id = #{shopMenuCategoryId} " +
                    "AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<ShopMenu> getMenusUsingCategoryForAdmin(@Param("shopMenuCategoryId") Integer shopMenuCategoryId);

    @Select("SELECT image_url " +
            "FROM koin.shop_menu_images " +
            "WHERE " +
                "shop_menu_id = #{shop_menu_id} " +
                "AND is_deleted = 0")
    List<String> getMenuImageUrlsByMenuIdForAdmin(Integer menuId);

    @Update("UPDATE koin.shop_menus " +
            "SET " +
                "`name` = #{shopMenu.name}, " +
                "`description` = #{shopMenu.description} " +
            "WHERE id = #{shopMenu.id}")
    void updateMenuForAdmin(@Param("shopMenu") ShopMenu shopMenu);

    @Update("UPDATE koin.shop_menus " +
            "SET is_hidden = 1 " +
            "WHERE id = #{shopMenuId}")
    void hideMenuByIdForAdmin(@Param("shopMenuId") Integer shopMenuId);

    @Update("UPDATE koin.shop_menus " +
            "SET is_hidden = 0 " +
            "WHERE id = #{shopMenuId}")
    void revealMenuByIdForAdmin(@Param("shopMenuId") Integer shopMenuId);


    // ========================================= 메뉴 카테고리 =========================================
    @Insert("INSERT INTO koin.shop_menu_categories " +
            "(shop_id, `name`) " +
            "VALUES (#{shopMenuCategory.shop_id}, #{shopMenuCategory.name})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "shopMenuCategory.id", before = false, resultType = Integer.class)
    void createMenuCategoryForAdmin(@Param("shopMenuCategory") ShopMenuCategory shopMenuCategory);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE " +
                "id = #{id} " +
                "AND is_deleted = 0")
    ShopMenuCategory getMenuCategoryByIdForAdmin(@Param("id") Integer id);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE shop_id = #{shopId} " +
                "AND `name` = #{name} " +
                "AND is_deleted = 0")
    ShopMenuCategory getMenuCategoryByShopIdAndNameForAdmin(@Param("shopId") Integer shopId, @Param("name") String name);

    @Select("SELECT * " +
            "FROM koin.shop_menu_categories " +
            "WHERE " +
                "shop_id = #{shopId} " +
                "AND is_deleted = 0")
    List<ShopMenuCategory> getMenuCategoriesOfShopByShopIdForAdmin(@Param("shopId") Integer shopId);

    @Select("SELECT id " +
            "FROM koin.shop_menu_categories " +
            "WHERE id IN (" +
                "SELECT shop_menu_category_id " +
                "FROM koin.shop_menu_category_map " +
                "WHERE " +
                    "shop_menu_id = #{shopMenuId} " +
                    "AND is_deleted = 0" +
            ") AND is_deleted = 0")
    List<Integer> getMenuCategoryIdsOfMenuByMenuIdForAdmin(@Param("shopMenuId") Integer shopMenuId);

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
    void deleteMenuCategoryByIdForAdmin(@Param("id") Integer id);

    @Insert("INSERT INTO koin.shop_menu_details " +
            "(shop_menu_id, `option`, price)" +
            "VALUES (#{shopMenuDetail.shop_menu_id}, #{shopMenuDetail.option}, #{shopMenuDetail.price})")
    void createMenuDetailForAdmin(@Param("shopMenuDetail") ShopMenuDetail shopMenuDetail);

    @Select("SELECT * " +
            "FROM koin.shop_menu_details " +
            "WHERE " +
                "shop_menu_id = #{shopMenuId} " +
                "AND is_deleted = 0")
    List<ShopMenuDetail> getMenuDetailsByMenuIdForAdmin(@Param("shopMenuId") Integer shopMenuId);

    @Update("UPDATE koin.shop_menu_details " +
            "SET is_deleted = 1 " +
            "WHERE shop_menu_id = #{shopMenuId}")
    void deleteMenuDetailsByMenuIdForAdmin(@Param("shopMenuId") Integer shopMenuId);


    // ------------------------------------------ xml Mapper ------------------------------------------
    // ============================================== 상점 =============================================
    void createShopOpensForAdmin(@Param("shopOpens") List<ShopOpen> shopOpens);

    void createShopImagesForAdmin(@Param("shopImages") List<ShopImage> shopImages);

    void updateShopOpensForAdmin(@Param("shopOpens") List<ShopOpen> shopOpens);

    void deleteShopImagesForAdmin(@Param("shopImages") List<ShopImage> shopImages);

    Integer getTotalCountOfShopsByConditionForAdmin(@Param("condition") ShopsCondition condition);

    List<RelatedToShop> getRelatedToShopsByConditionForAdmin(@Param("begin") Integer begin, @Param("condition") ShopsCondition condition);

    RelatedToShop getRelatedToShopByShopId(@Param("shopId") Integer shopId);

    List<RelatedToShop> getRelatedToShops();

    RelatedToShopMenu getRelatedToShopMenuByMenuId(@Param("shopMenuId") Integer shopMenuId);

    List<RelatedToShopMenu> getRelatedToShopMenusOfShopByShopId(@Param("shopId") Integer shopId);


    // ========================================== 상점 카테고리 =========================================
    void createShopCategoryMapsForAdmin(@Param("shopCategoryMaps") List<ShopCategoryMap> shopCategoryMaps);

    void deleteShopCategoryMapsForAdmin(@Param("shopCategoryMaps") List<ShopCategoryMap> shopCategoryMaps);

    Integer getTotalCountOfShopCategoriesByConditionForAdmin(@Param("condition") ShopCategoriesCondition condition);

    List<ShopCategory> getShopCategoriesByConditionForAdmin(@Param("begin") Integer begin, @Param("condition") ShopCategoriesCondition condition);


    // ============================================= 메뉴 =============================================
    void createMenuDetailsForAdmin(@Param("shopMenuDetails") List<ShopMenuDetail> shopMenuDetails);

    void createMenuImagesForAdmin(@Param("shopMenuImages") List<ShopMenuImage> shopMenuImages);

    void deleteMenuDetailsForAdmin(@Param("shopMenuDetails") List<ShopMenuDetail> shopMenuDetails);

    void deleteAllForInvolvedWithMenuForAdmin(@Param("shopMenuId") Integer shopMenuId);

    void deleteMenuImagesForAdmin(@Param("shopMenuImages") List<ShopMenuImage> shopMenuImages);


    // ========================================== 메뉴 카테고리 =========================================
    void createMenuCategoriesForAdmin(@Param("shopMenuCategories") List<ShopMenuCategory> shopMenuCategories);

    void createMenuCategoryMapsForAdmin(@Param("shopMenuCategoryMaps") List<ShopMenuCategoryMap> shopMenuCategoryMaps);

    void deleteMenuCategoryMapsForAdmin(@Param("shopMenuCategoryMaps") List<ShopMenuCategoryMap> shopMenuCategoryMaps);
}