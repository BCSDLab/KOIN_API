DELIMITER $$

CREATE TRIGGER update_shop_updated_at_category
    AFTER UPDATE
    ON shop_category_map
    FOR EACH ROW

BEGIN
    UPDATE shops SET updated_at = NEW.updated_at WHERE shops.id = NEW.shop_id;
END $$

DELIMITER ;