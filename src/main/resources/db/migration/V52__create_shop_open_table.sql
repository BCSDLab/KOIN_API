CREATE TABLE `shop_open` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'shop_open 고유 id',
  `shop_id` int unsigned NOT NULL COMMENT 'shops 고유 id',
  `day_of_week` varchar(10) NOT NULL COMMENT '요일',
  `is_closed` tinyint(1) NOT NULL COMMENT '휴무 여부',
  `open_time` varchar(10) DEFAULT NULL COMMENT '오픈 시간',
  `close_time` varchar(10) DEFAULT NULL COMMENT '마감 시간',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '삭제 여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '업데이트 일자',
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8
COLLATE = utf8_bin;