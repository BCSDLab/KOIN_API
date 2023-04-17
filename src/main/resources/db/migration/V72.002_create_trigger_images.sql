DELIMITER $$

CREATE TRIGGER update_shop_updated_at_images
    AFTER UPDATE
    ON shop_images
    FOR EACH ROW

BEGIN
    UPDATE shops SET updated_at = NEW.updated_at WHERE shops.id = NEW.shop_id;
END $$

DELIMITER ;