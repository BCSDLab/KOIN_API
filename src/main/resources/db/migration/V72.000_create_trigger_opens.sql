DELIMITER $$

CREATE TRIGGER update_shop_updated_at_opens
    AFTER UPDATE
    ON shop_opens
    FOR EACH ROW

BEGIN
    UPDATE shops SET updated_at = NEW.updated_at WHERE shops.id = NEW.shop_id;
END $$

DELIMITER ;