DELIMITER $$

CREATE TRIGGER update_shop_updated_at_menus_category
    AFTER UPDATE
    ON shop_menu_category_map
    FOR EACH ROW

BEGIN
    UPDATE shop_menus SET updated_at = NEW.updated_at WHERE shop_menus.id = NEW.shop_menu_id;
END $$

DELIMITER ;