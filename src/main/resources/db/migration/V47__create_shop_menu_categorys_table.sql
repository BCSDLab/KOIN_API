CREATE TABLE `shop_menu_categorys` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'shop_menu_categorys 고유 id',
  `name` varchar(255) NOT NULL COMMENT '카테고리 이름',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '삭제 여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '업데이트 일자',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8
COLLATE = utf8_bin;

INSERT INTO `koin`.`shop_menu_categorys` (`name`) VALUES ('대표 메뉴');
INSERT INTO `koin`.`shop_menu_categorys` (`name`) VALUES ('전체 메뉴');
INSERT INTO `koin`.`shop_menu_categorys` (`name`) VALUES ('세트 메뉴');
INSERT INTO `koin`.`shop_menu_categorys` (`name`) VALUES ('사이드 메뉴');