CREATE TABLE `shop_shop_menu_category_map` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'shop_shop_menu_category_map 고유 id',
  `shop_id` int unsigned NOT NULL COMMENT 'shops 고유 id',
  `shop_menu_category_id` int unsigned NOT NULL COMMENT 'shop_menu_categorys 고유 id',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '삭제 여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '업데이트 일자',
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8
COLLATE = utf8_bin;

INSERT INTO `koin`.`shop_shop_menu_category_map`
    (`shop_id`, `shop_menu_category_id`)
SELECT `id`, 1
FROM koin.shops;

INSERT INTO `koin`.`shop_shop_menu_category_map`
(`shop_id`, `shop_menu_category_id`)
SELECT `id`, 2
FROM koin.shops;

INSERT INTO `koin`.`shop_shop_menu_category_map`
(`shop_id`, `shop_menu_category_id`)
SELECT `id`, 3
FROM koin.shops;

INSERT INTO `koin`.`shop_shop_menu_category_map`
(`shop_id`, `shop_menu_category_id`)
SELECT `id`, 4
FROM koin.shops;