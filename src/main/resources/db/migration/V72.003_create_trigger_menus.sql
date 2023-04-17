DELIMITER $$

CREATE TRIGGER update_shop_updated_at_menus
    AFTER UPDATE
    ON shop_menus
    FOR EACH ROW

BEGIN
    UPDATE shops SET updated_at = NEW.updated_at WHERE shops.id = NEW.shop_id;
END $$

DELIMITER ;