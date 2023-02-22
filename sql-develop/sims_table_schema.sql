DROP TABLE IF EXISTS `ADMIN`;
CREATE TABLE `ADMIN` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `account` varchar(255) NOT NULL COMMENT '帳號',
  `password` varchar(128) NOT NULL COMMENT '密碼',
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `privilege` int NOT NULL COMMENT '權限',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:正常,1:停權,2:刪除',
  `is_default_pwd` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否為預設密碼，0: 否, 1: 是',
  `user_id` varchar(32) DEFAULT NULL COMMENT '員編',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `last_login_time` datetime DEFAULT NULL COMMENT '最近登入時間',
  `updater` bigint NOT NULL COMMENT '更新人員',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `admin_constraint_1` UNIQUE (`account`),
  CONSTRAINT `admin_constraint_2` UNIQUE (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `PRIVILEGE`;
CREATE TABLE `PRIVILEGE` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `name` varchar(32) NOT NULL COMMENT '角色名稱',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:正常,1:停用,2:刪除',
  `privilege_level` tinyint(4) NOT NULL COMMENT '角色層級: 1: Admin; 2: Leader; 3: normal',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `updater` varchar(32) NOT NULL COMMENT '更新人員',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `privilege_constraint_1` UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `DIVISION`;
CREATE TABLE `DIVISION` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `name` varchar(32) NOT NULL COMMENT '名稱',
  `code` varchar(20) NOT NULL COMMENT '科別代碼',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:正常,1:停用,2:刪除',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `updater` varchar(32) NOT NULL COMMENT '更新人員',
  PRIMARY KEY (`id`),
  CONSTRAINT `division_constraint_1` UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `BRANCH`;
CREATE TABLE `BRANCH` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `name` varchar(32) NOT NULL COMMENT '院區名稱',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:正常,1:停用,2:刪除',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `updater` varchar(32) NOT NULL COMMENT '更新人員',
  PRIMARY KEY (`id`),
  CONSTRAINT `branch_constraint_1` UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `FILE_RESOURCE`;
CREATE TABLE `FILE_RESOURCE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `upload_ip` varchar(32) NOT NULL COMMENT '上傳的 ip address',
  `resource_name` varchar(128) NOT NULL COMMENT '資源自動產生的名稱',
  `local_path` varchar(256) NOT NULL COMMENT 'server 端檔案路徑, 相對於 resource.basepath',
  `file_size` bigint(20) NOT NULL COMMENT '檔案大小，單位為 byte',
  `content_type` varchar(128) NOT NULL DEFAULT '' COMMENT '檔案類型',
  `status` int(11) NOT NULL COMMENT '檔案狀態',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `updater` varchar(32) COMMENT '更新人員',
  PRIMARY KEY (`id`),
  CONSTRAINT `file_resource_constraint_1` UNIQUE (`local_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `DEVICE_TYPE`;
CREATE TABLE `DEVICE_TYPE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '器械名稱',
  `name_scientific` varchar(100) NOT NULL COMMENT '器械英文學名',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `updater` int(11) NOT NULL COMMENT '更新人員',
  `spec` varchar(150) DEFAULT NULL COMMENT '規格',
  `description` varchar(300) DEFAULT NULL COMMENT '說明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `DEVICE_IMAGE`;
CREATE TABLE `DEVICE_IMAGE` (
  `type_id` bigint NOT NULL COMMENT '器械種類ID',
  `file_id` bigint NOT NULL COMMENT '器械圖片',
  `is_main` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否為主圖 0:否 1:是',
  PRIMARY KEY (`type_id`,`file_id`),
  KEY `idx_type_id` (`type_id`),
  KEY `device_image_constraint_2` (`file_id`),
  CONSTRAINT `device_image_constraint_1` FOREIGN KEY (`type_id`) REFERENCES `DEVICE_TYPE` (`id`),
  CONSTRAINT `device_image_constraint_2` FOREIGN KEY (`file_id`) REFERENCES `FILE_RESOURCE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `PACKAGE_CONFIG`;
CREATE TABLE `PACKAGE_CONFIG` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_name` varchar(30) NOT NULL COMMENT '包盤名稱',
  `division_id` int NOT NULL COMMENT 'FK',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL COMMENT '更新時間',
  `creator` int NOT NULL COMMENT '建立人員',
  `updater` int NOT NULL COMMENT '更新時間',
  `config_code` varchar(15) NOT NULL COMMENT '包盤編碼',
  `is_active` tinyint NOT NULL DEFAULT '1' COMMENT '0 關 1 開',
  PRIMARY KEY (`id`),
  KEY `package_config_constraint_1` (`division_id`),
  CONSTRAINT `package_config_constraint_1` FOREIGN KEY (`division_id`) REFERENCES `DIVISION` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PACKAGE_CONFIG_DETAIL`;
CREATE TABLE `PACKAGE_CONFIG_DETAIL` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_config_id` bigint NOT NULL COMMENT 'FK',
  `device_type_id` bigint NOT NULL COMMENT 'FK',
  `amount` int NOT NULL COMMENT '數量',
  PRIMARY KEY (`id`),
  KEY `package_config_detail_constraint_1` (`package_config_id`),
  KEY `package_config_detail_constraint_2` (`device_type_id`),
  CONSTRAINT `package_config_detail_constraint_1` FOREIGN KEY (`package_config_id`) REFERENCES `PACKAGE_CONFIG` (`id`),
  CONSTRAINT `package_config_detail_constraint_2` FOREIGN KEY (`device_type_id`) REFERENCES `DEVICE_TYPE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `ADD_PACKAGE_AMOUNT_RECORD`;
CREATE TABLE `ADD_PACKAGE_AMOUNT_RECORD` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_config_id` bigint NOT NULL COMMENT '盤種config id',
  `create_time` datetime NOT NULL COMMENT '申請時間',
  `creator` int NOT NULL COMMENT '申請者',
  `update_time` datetime NOT NULL COMMENT '更新時間',
  `updater` int NOT NULL COMMENT '更新者',
  `status` tinyint NOT NULL COMMENT '0:全部取消, 1:申請, 2:處理中, 3:完成 //修改 ',
  PRIMARY KEY (`id`),
  KEY `add_constraint_2` (`package_config_id`),
  CONSTRAINT `add_constraint_2` FOREIGN KEY (`package_config_id`) REFERENCES `PACKAGE_CONFIG` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PACKAGE`;
CREATE TABLE `PACKAGE` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `add_id` bigint NOT NULL COMMENT 'FK REF TO ADD_PACKAGE_AMOUNT_RECORD',
  `package_config_id` bigint NOT NULL COMMENT 'FK',
  `status` tinyint NOT NULL COMMENT '-1:申請中\r\n0:包盤完成\r\n1:包盤完成交滅菌\r\n2:滅菌中心領取\r\n3:滅菌中\r\n4:滅菌完成\r\n5:滅菌滅好交庫存\r\n6:庫存\r\n7:發放完成\r\n8:使用中\r\n9:使用完成\r\n10:回收處理\r\n11:清洗交付包盤\r\n12:包盤接收清洗/庫存\r\n13:重新滅菌\r\n14:滅菌失敗\r\n15:清洗失敗\r\n16:滅菌過期領取\r\n17:包內指示劑失敗\r\n99:拆盤',
  `device_box_qrcode` varchar(100) COMMENT '器械盒qrcode',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `tracking_id` bigint DEFAULT NULL COMMENT 'tracking id',
  `serial_no` varchar(20) NOT NULL COMMENT '包盤序號',
  `code` varchar(20) NOT NULL COMMENT '包盤碼',
  `position` varchar(10) DEFAULT NULL COMMENT '包盤入庫位置',
  `used_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '包盤使用次數',
  PRIMARY KEY (`id`),
  UNIQUE KEY `PACKAGE_UN_2` (`code`),
  UNIQUE KEY `PACKAGE_UN` (`device_box_qrcode`),
  KEY `package_constraint_1` (`add_id`),
  KEY `package_constraint_2` (`package_config_id`),
  KEY `package_constraint_5` (`tracking_id`),
  CONSTRAINT `package_constraint_1` FOREIGN KEY (`add_id`) REFERENCES `ADD_PACKAGE_AMOUNT_RECORD` (`id`),
  CONSTRAINT `package_constraint_2` FOREIGN KEY (`package_config_id`) REFERENCES `PACKAGE_CONFIG` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `DEVICE_MODEL`;
CREATE TABLE `DEVICE_MODEL` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `brand` varchar(50) NOT NULL COMMENT '廠牌',
  `manufacture_id` varchar(50) NOT NULL COMMENT '型號',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `DEVICE`;
CREATE TABLE `DEVICE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:全新待包盤;1:在包盤中;2:待包盤;3:已回收清點;4:維修中;6:無法掃描;7:遺失;9:報廢',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `scrap_time` datetime DEFAULT NULL COMMENT '報廢時間',
  `updater` int(11) NOT NULL COMMENT '更新人員',
  `udi` varchar(32) DEFAULT NULL COMMENT '醫療器材唯一識別碼',
  `branch` int(11) NOT NULL COMMENT '所屬院區編碼',
  `division` int(11) NOT NULL COMMENT '科別',
  `qrcode` varchar(64) NOT NULL COMMENT '二維碼',
  `cost` float(10,2) DEFAULT '0.00' COMMENT '成本',
  `used_count` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '器械被使用次數',
  `type_id` bigint(20) NOT NULL COMMENT '器械種類ID',
  `used_time` datetime DEFAULT NULL COMMENT '第一次使用時間',
  `device_model_id` bigint NOT NULL COMMENT 'FK 器械型號ID',
  `package_id` bigint DEFAULT NULL COMMENT 'FK 器械目前在哪個包盤',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_constraint_3` (`qrcode`),
  KEY `idx_device_status` (`status`),
  CONSTRAINT `device_constraint_4` FOREIGN KEY (`branch`) REFERENCES `BRANCH` (`id`),
  CONSTRAINT `device_constraint_5` FOREIGN KEY (`division`) REFERENCES `DIVISION` (`id`),
  CONSTRAINT `device_constraint_6` FOREIGN KEY (`type_id`) REFERENCES `DEVICE_TYPE` (`id`),  
  CONSTRAINT `device_constraint_7` FOREIGN KEY (`package_id`) REFERENCES `PACKAGE` (`id`),
  CONSTRAINT `device_constraint_8` FOREIGN KEY (`device_model_id`) REFERENCES `DEVICE_MODEL` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `DIVISION_STAFF_MAPPING`;
CREATE TABLE `DIVISION_STAFF_MAPPING` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
  `admin_id` int(11) NOT NULL COMMENT 'ADMIN_ID',
  `division_id` int(11) COMMENT 'DIVISION_ID',
  `branch_id` int(11) COMMENT 'BRANCH_ID',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `updater` varchar(32) NULL COMMENT '更新人員',
  CONSTRAINT `mapping_constraint_1` FOREIGN KEY (`admin_id`) REFERENCES `ADMIN` (`id`),
  CONSTRAINT `mapping_constraint_2` FOREIGN KEY (`branch_id`) REFERENCES `BRANCH` (`id`),
  CONSTRAINT `mapping_constraint_3` FOREIGN KEY (`division_id`) REFERENCES `DIVISION` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `DEVICE_REPAIR_RECORD`;
CREATE TABLE `DEVICE_REPAIR_RECORD` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `device_id` bigint(20) NOT NULL COMMENT 'FK',
  `status` int(11) NOT NULL COMMENT '4:維修;5:維修完成;9:報廢',
  `previous_used_count` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '送修前使用次數',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `updater` int(11) NOT NULL COMMENT '更新人員',
  `description` varchar(255) COMMENT '備註',
  `finish_time` datetime COMMENT '維修完成時間',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  CONSTRAINT `repair_constraint_1` FOREIGN KEY (`device_id`) REFERENCES `DEVICE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `DEVICE_BOX`;
CREATE TABLE `DEVICE_BOX`(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `qrcode` varchar(64) NOT NULL COMMENT 'QRCode',
  `cost` float(10,2) DEFAULT '0.00' COMMENT '成本',
  `udi` varchar(32) DEFAULT NULL COMMENT '醫療器材唯一識別碼',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:全新待包盤;1:在包盤中;2:待包盤;4:維修中;9:報廢',
  `updater` int(11) NOT NULL COMMENT '更新人員',
  `used_count` bigint(11) UNSIGNED DEFAULT 0 COMMENT '總使用次數',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime COMMENT '更新時間',
  `used_time` datetime DEFAULT NULL COMMENT '第一次使用時間',
  `scrap_time` datetime DEFAULT NULL COMMENT '報廢時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_box_constraint_1` (`qrcode`),
  KEY `idx_device_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `DEVICE_BOX_REPAIR`;
CREATE TABLE `DEVICE_BOX_REPAIR`(
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `device_box_id` bigint(20) NOT NULL COMMENT 'FK',
  `status` tinyint(4) NOT NULL COMMENT '4:維修;5:維修完成;9:報廢',
  `previous_used_count` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '維修前使用次數',
  `comments` varchar(255) DEFAULT NULL COMMENT '原因',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `finish_time` datetime COMMENT '維修完成時間',
  `updater` int(11) NOT NULL COMMENT '更新人員',
  PRIMARY KEY (`id`),
  KEY `idx_device_box_id` (`device_box_id`),
  CONSTRAINT `box_repair_constraint_1` FOREIGN KEY (`device_box_id`) REFERENCES `DEVICE_BOX` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `STATION`;
CREATE TABLE `STATION` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `station_name` varchar(100) NOT NULL COMMENT '站名',
  `is_active` tinyint NOT NULL COMMENT '是否啟用(0:否;1:是)',
  `type` varchar(20) NOT NULL COMMENT 'supply, packing, sterilization, circulation',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PACKAGE_DEVICE_RECORD`;
CREATE TABLE `PACKAGE_DEVICE_RECORD` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_id` bigint NOT NULL COMMENT 'FK',
  `device_id` bigint NOT NULL COMMENT 'FK',
  `device_in_time` datetime NOT NULL COMMENT '進入包盤時間',
  `device_out_time` datetime DEFAULT NULL COMMENT '離開包盤時間',
  `creator` int NOT NULL COMMENT '建立人員',
  `updater` int NOT NULL COMMENT '更新人員',
  PRIMARY KEY (`id`),
  KEY `package_device_record_constraint_1` (`package_id`),
  KEY `package_device_record_constraint_2` (`device_id`),
  CONSTRAINT `package_device_record_constraint_1` FOREIGN KEY (`package_id`) REFERENCES `PACKAGE` (`id`),
  CONSTRAINT `package_device_record_constraint_2` FOREIGN KEY (`device_id`) REFERENCES `DEVICE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `TRACKING`;
CREATE TABLE `TRACKING` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_id` bigint NOT NULL COMMENT 'FK',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `finish_time` datetime DEFAULT NULL COMMENT '生命週期結束時間',
  `expire_time` datetime DEFAULT NULL COMMENT '有效時間，滅菌完產生',
  `use_time` datetime DEFAULT NULL COMMENT '包盤發放時間',
  PRIMARY KEY (`id`),
  KEY `tracking_constraint_1` (`package_id`),
  CONSTRAINT `tracking_constraint_1` FOREIGN KEY (`package_id`) REFERENCES `PACKAGE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `TRACKING_RECORD`;
CREATE TABLE `TRACKING_RECORD` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `tracking_id` bigint NOT NULL COMMENT 'tracking id',
  `status` tinyint NOT NULL COMMENT '0:包盤完成\r\n1:包盤完成交滅菌\r\n2:滅菌中心領取\r\n3:滅菌中\r\n4:滅菌完成\r\n5:滅菌滅好交庫存\r\n6:庫存\r\n7:發放完成\r\n8:使用中\r\n9:使用完成\r\n10:回收處理\r\n11:清洗交付包盤\r\n12:包盤接收清洗/庫存\r\n13:重新滅菌\r\n99: 拆盤',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `creator` int NOT NULL COMMENT '建立者',
  PRIMARY KEY (`id`),
  KEY `tracking_record_constraint_1` (`tracking_id`),
  CONSTRAINT `tracking_record_constraint_1` FOREIGN KEY (`tracking_id`) REFERENCES `TRACKING` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `DELIVER_BATCH`;
CREATE TABLE `DELIVER_BATCH` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `title` varchar(50) NOT NULL COMMENT 'title',
  `status` tinyint NOT NULL COMMENT '0:交付中; 1:交付完成',
  `deliver_from` bigint NOT NULL COMMENT '來源站',
  `deliver_to` bigint NOT NULL COMMENT '目的站',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `DELIVER_BATCH_DETAIL`;
CREATE TABLE `DELIVER_BATCH_DETAIL` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_id` bigint NOT NULL COMMENT 'FK',
  `tracking_id` bigint NOT NULL COMMENT 'FK',
  `deliver_batch_id` bigint NOT NULL COMMENT 'FK',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `receive_time` datetime NOT NULL COMMENT '接收時間',
  PRIMARY KEY (`id`),
  KEY `deliver_batch_detail_constraint_1` (`package_id`),
  KEY `deliver_batch_detail_constraint_2` (`tracking_id`),
  KEY `deliver_batch_detail_constraint_3` (`deliver_batch_id`),
  CONSTRAINT `deliver_batch_detail_constraint_1` FOREIGN KEY (`package_id`) REFERENCES `PACKAGE` (`id`),
  CONSTRAINT `deliver_batch_detail_constraint_2` FOREIGN KEY (`tracking_id`) REFERENCES `TRACKING` (`id`),
  CONSTRAINT `deliver_batch_detail_constraint_3` FOREIGN KEY (`deliver_batch_id`) REFERENCES `DELIVER_BATCH` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `STERILIZED_BATCH`;
CREATE TABLE `STERILIZED_BATCH` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `name` varchar(50) NOT NULL COMMENT '滅菌批次名稱',
  `status` tinyint DEFAULT NULL COMMENT '0:滅菌失敗; 1:滅菌成功',
  `finish_time` datetime DEFAULT NULL COMMENT '滅菌完成時間',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `creator` int NOT NULL COMMENT '建立人員',
  `start_time` datetime NOT NULL COMMENT '開始滅菌時間',
  `sterilizer` varchar(15) NOT NULL COMMENT '滅菌鍋名稱',
  `petri_dish` varchar(30) NOT NULL COMMENT '培養皿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `STERILIZED_BATCH_DETAIL`;
CREATE TABLE `STERILIZED_BATCH_DETAIL` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_id` bigint NOT NULL COMMENT 'FK',
  `sterilized_batch_id` bigint NOT NULL COMMENT 'FK',
  `tracking_id` bigint NOT NULL COMMENT 'FK',
  `status` tinyint DEFAULT NULL COMMENT '0:滅菌失敗; 1:滅菌成功',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `creator` bigint NOT NULL COMMENT '建立人員',
  PRIMARY KEY (`id`),
  KEY `sterilized_batch_detail_constraint_1` (`package_id`),
  KEY `sterilized_batch_detail_constraint_2` (`sterilized_batch_id`),
  KEY `sterilized_batch_detail_constraint_3` (`tracking_id`),
  CONSTRAINT `sterilized_batch_detail_constraint_1` FOREIGN KEY (`package_id`) REFERENCES `PACKAGE` (`id`),
  CONSTRAINT `sterilized_batch_detail_constraint_2` FOREIGN KEY (`sterilized_batch_id`) REFERENCES `STERILIZED_BATCH` (`id`),
  CONSTRAINT `sterilized_batch_detail_constraint_3` FOREIGN KEY (`tracking_id`) REFERENCES `TRACKING` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `SURGICAL_APPLY`;
CREATE TABLE `SURGICAL_APPLY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `operating_number` varchar(50) NOT NULL COMMENT '手術單號',
  `division_id` int(11) NOT NULL COMMENT 'FK',
  `operating_room` varchar(32) NOT NULL COMMENT '手術室(API待入)',
  `status` tinyint(4) NOT NULL  COMMENT '0:申請中; 1:已審核; 2:已發放; 3:使用中; 4:使用完成; 5:回收清點完成; 9:取消申請;',
  `medical_record_number` varchar(50) NOT NULL COMMENT '病歷號碼',
  `name` varchar(50) NOT NULL COMMENT '手術名稱',
  `doctor` varchar(32) NOT NULL COMMENT '手術醫生',
  `operating_date` datetime NOT NULL COMMENT '手術日期(不含時間)',
  `operating_order` int(11) NOT NULL COMMENT '排刀順序',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL COMMENT '更新時間', 
  `creator` int(11) NOT NULL COMMENT '建立人員',
  `updater` int(11) NOT NULL COMMENT '更新人員',
  PRIMARY KEY (`id`),
  KEY `surgical_apply_constraint_2` (`division_id`),
  CONSTRAINT `surgical_apply_constraint_2` FOREIGN KEY (`division_id`) REFERENCES `DIVISION` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PACKAGE_ORDER`;
CREATE TABLE `PACKAGE_ORDER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `surgical_apply_id` bigint(20) NOT NULL COMMENT 'FK',
  `status` tinyint(4) NOT NULL  COMMENT '0:未領用; 1:已領用; 2:使用中; 3:使用完成; 4:回收清點完成; 9:取消訂單;',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  PRIMARY KEY (`id`),
  KEY `package_order_constraint_1` (`surgical_apply_id`),
  CONSTRAINT `package_order_constraint_1` FOREIGN KEY (`surgical_apply_id`) REFERENCES `SURGICAL_APPLY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PACKAGE_ORDER_DETAIL`;
CREATE TABLE `PACKAGE_ORDER_DETAIL` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_order_id` bigint(20) NOT NULL COMMENT 'FK',
  `package_config_id` bigint(20) NOT NULL COMMENT 'FK',
  `amount` int(11) NOT NULL COMMENT 'order 數量',
  PRIMARY KEY (`id`),
  KEY `package_order_detail_constraint_1` (`package_order_id`),
  KEY `package_order_detail_constraint_2` (`package_config_id`),
  CONSTRAINT `package_order_detail_constraint_1` FOREIGN KEY (`package_order_id`) REFERENCES `PACKAGE_ORDER` (`id`),
  CONSTRAINT `package_order_detail_constraint_2` FOREIGN KEY (`package_config_id`) REFERENCES `PACKAGE_CONFIG` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PACKAGE_ORDER_MAPPING`;
CREATE TABLE `PACKAGE_ORDER_MAPPING` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_order_id` bigint(20) NOT NULL COMMENT 'FK',
  `package_id` bigint(20) NOT NULL COMMENT 'FK',
  `tracking_id` bigint(20) NOT NULL COMMENT 'FK',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0: 未清點; 1:使用中; 2:使用完成; 3:回收清點完成',
  PRIMARY KEY (`id`),
  KEY `package_order_mapping_constraint_1` (`package_order_id`),
  KEY `package_order_mapping_constraint_2` (`package_id`),
  KEY `package_order_mapping_constraint_3` (`tracking_id`),
  CONSTRAINT `package_order_mapping_constraint_1` FOREIGN KEY (`package_order_id`) REFERENCES `PACKAGE_ORDER` (`id`),
  CONSTRAINT `package_order_mapping_constraint_2` FOREIGN KEY (`package_id`) REFERENCES `PACKAGE` (`id`),
  CONSTRAINT `package_order_mapping_constraint_3` FOREIGN KEY (`tracking_id`) REFERENCES `TRACKING` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PACKAGE_DEVICE_CHECK`;
CREATE TABLE `PACKAGE_DEVICE_CHECK` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `tracking_id` bigint(20) NOT NULL COMMENT 'FK',
  `step` tinyint(4) NOT NULL COMMENT '清點階段 1:使用中 2:使用完成 3:回收完成',
  `creator` int(11) NOT NULL COMMENT '建立人員',
  `updater` int(11) NOT NULL COMMENT '更新人員',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `update_time` datetime NOT NULL COMMENT '更新時間',
  PRIMARY KEY (`id`),
  KEY `package_device_check_constraint_1` (`tracking_id`),
  CONSTRAINT `package_device_check_constraint_1` FOREIGN KEY (`tracking_id`) REFERENCES `TRACKING` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `DEVICE_CHECK_DIFF`;
CREATE TABLE `DEVICE_CHECK_DIFF` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_device_check_id` bigint(20) NOT NULL COMMENT 'FK',
  `device_type_id` bigint(20) NOT NULL COMMENT 'FK',
  `amount` int(11) NOT NULL COMMENT '清點數量',
  `difference` int(11) NOT NULL COMMENT '差值',
  PRIMARY KEY (`id`),
  KEY `device_check_diff_constraint_1` (`package_device_check_id`),
  KEY `device_check_diff_constraint_2` (`device_type_id`),
  CONSTRAINT `device_check_diff_constraint_1` FOREIGN KEY (`package_device_check_id`) REFERENCES `PACKAGE_DEVICE_CHECK` (`id`),
  CONSTRAINT `device_check_diff_constraint_2` FOREIGN KEY (`device_type_id`) REFERENCES `DEVICE_TYPE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `WASHING_BATCH`;
CREATE TABLE `WASHING_BATCH` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `name` varchar(50) NOT NULL COMMENT '洗批次名稱',
  `washing_machine` varchar(15) NOT NULL COMMENT '清洗機名稱',
  `status` tinyint(4) DEFAULT NULL COMMENT '清洗狀態，0: 失敗; 1: 成功',
  `finish_time` datetime DEFAULT NULL COMMENT '清洗完成時間',
  `start_time` datetime NOT NULL COMMENT '開始清洗時間',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `creator` int(11) NOT NULL COMMENT '建立人員',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `WASHING_BATCH_DETAIL`;
CREATE TABLE `WASHING_BATCH_DETAIL` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_id` bigint(20) NOT NULL COMMENT 'FK',
  `washing_batch_id` bigint(20) NOT NULL COMMENT 'FK',
  `tracking_id` bigint(20) NOT NULL COMMENT 'FK',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `creator` int(11) NOT NULL COMMENT '建立人員',
  PRIMARY KEY (`id`),
  KEY `washing_batch_detail_constraint_1` (`package_id`),
  KEY `washing_batch_detail_constraint_2` (`washing_batch_id`),
  KEY `washing_batch_detail_constraint_3` (`tracking_id`),
  CONSTRAINT `washing_batch_detail_constraint_1` FOREIGN KEY (`package_id`) REFERENCES `PACKAGE` (`id`),
  CONSTRAINT `washing_batch_detail_constraint_2` FOREIGN KEY (`washing_batch_id`) REFERENCES `WASHING_BATCH` (`id`),
  CONSTRAINT `washing_batch_detail_constraint_3` FOREIGN KEY (`tracking_id`) REFERENCES `TRACKING` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `DEVICE_CHECK_LOG`;
CREATE TABLE `DEVICE_CHECK_LOG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `device_id` bigint NOT NULL COMMENT 'FK器械ID',
  `status` tinyint(4) NOT NULL COMMENT '6:無法掃描, 7:遺失',
  `tracking_id` bigint(20) NOT NULL COMMENT 'FK 包盤追蹤ID',
  `type` tinyint(4) NOT NULL COMMENT '0:回收清點, 1:重新打包',
  `create_time` datetime NOT NULL COMMENT '建立時間',
  `creator` int(11) NOT NULL COMMENT '建立者',
  `return_time` datetime DEFAULT NULL COMMENT '遺失或恢復返還時間',
  PRIMARY KEY (`id`),
  KEY `device_check_log_constraint_1` (`device_id`),
  KEY `device_check_log_constraint_2` (`tracking_id`),
  CONSTRAINT `device_check_log_constraint_1` FOREIGN KEY (`device_id`) REFERENCES `DEVICE` (`id`),
  CONSTRAINT `device_check_log_constraint_2` FOREIGN KEY (`tracking_id`) REFERENCES `TRACKING` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `PRIVILEGE_FUNC_PERMISSION_MAPPING`;
CREATE TABLE `PRIVILEGE_FUNC_PERMISSION_MAPPING` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `privilege_id` bigint(20) NOT NULL COMMENT 'Privilege ID',
  `func_permission_id` bigint(20) NOT NULL COMMENT 'FuncPermission ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `FUNC_PERMISSION`;
CREATE TABLE `FUNC_PERMISSION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `name_cn` varchar(100) NOT NULL COMMENT '權限(功能)名稱_中文',
  `name_en` varchar(100) NOT NULL COMMENT '權限(功能)名稱_英文',
  `func_level` tinyint(4) NOT NULL COMMENT '功能層級, 1: Admin, 2: Leader, 3: normal',
  `create_time` datetime NOT NULL COMMENT '建立時間' DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO 
  FUNC_PERMISSION(name_cn, name_en, func_level)
VALUES
  ('器械','device',3), 
  ('器械盒','deviceBox',3), 
  ('維修','repair',3),
  ('管理區-總覽','managementOverview',3), 
  ('管理區-建立包盤類型','createPackageConfig',3), 
  ('管理區-申請包盤製作','packageApplication',3),
  ('管理區-包盤使用紀錄','packageUsingRecord',3), 
  ('管理區-滅菌鍋次紀錄','sterilizationBatchRecord',3), 
  ('管理區-清洗批次紀錄','washingBatchRecord',3),
  ('庫存區-領取包盤','stockReceivePackage',3), 
  ('庫存區-滅菌失效','stockSterilizeFailed',3), 
  ('庫存區-包盤過期','stockPackageExpired',3),
  ('配盤區-領取包盤','packReceivePackage',3), 
  ('配盤區-包盤製作','packCreatePackage',3), 
  ('配盤區-重新打包','packRePackaging',3),
  ('配盤區-交付滅菌','packDeliverToSterilization',3), 
  ('回收清洗區-回收器械清點','circulationDeviceCheck',3), 
  ('回收清洗區-建立清洗批次','circulationWashingBatchCreate',3),
  ('回收清洗區-清洗批次','circulationWashingBatchList',3),
  ('滅菌站-領取包盤','sterilizationReceivePackage',3), 
  ('滅菌站-建立滅菌鍋次','sterilizationBatchCreate',3),
  ('滅菌站-滅菌鍋次','sterilizationBatchList',3),
  ('滅菌站-交付庫存','sterilizationDeliverToStock',3), 
  ('手術器械申請','surgeryDeviceApplication',3),
  ('手術器械補申請','surgeryDeviceApplicationAgain',3), 
  ('手術器械發放','surgeryDeviceRelease',3), 
  ('器械使用前清點','surgeryDeviceCheckBefore',3),
  ('器械使用完成清點','surgeryDeviceCheckAfter',3), 
  ('包盤-製作包盤分析','statisticCreatePackage',3), 
  ('滅菌-滅菌鍋分析','statisticSterilizer',3),
  ('清洗-清洗機分析','statisticWasher',3), 
  ('人員管理-使用者列表','userManagementUserList',3), 
  ('人員管理-角色列表','userManagementRoleList',3),
  ('科別','division',3);

DROP TABLE IF EXISTS `SYSTEM_PROPERTIES`;
CREATE TABLE `SYSTEM_PROPERTIES` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`name` varchar(50) NOT NULL COMMENT '參數名稱(UNIQUE)',
	`value` varchar(500) NOT NULL COMMENT '參數內容',
	`description` varchar(50) COMMENT '參數說明',
	`updater` int(11) NOT NULL COMMENT '更新人員',
	`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新時間(yyyy-MM-dd HH:mm:ss)',
	PRIMARY KEY (`id`),
	CONSTRAINT `UK_NAME` UNIQUE (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3;

DROP TABLE IF EXISTS `STERILIZED_MONITOR`;
CREATE TABLE `STERILIZED_MONITOR` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`sterilized_batch_id` bigint(20) NOT NULL COMMENT '滅菌批次ID',
	`type` int(4) NOT NULL COMMENT '監測項目; 0:BD TEST, 1:滅菌時間, 2:滅菌溫度, 3:包外指示劑, 4:第五級整合型CI, 5:生物培養',
	`indicator` varchar(20) COMMENT '監測指標',
	`result` tinyint(1) COMMENT '結果;0:false,1:true',
	PRIMARY KEY (`id`),
	KEY `sterilized_monitor_constraint_1` (`sterilized_batch_id`),
	CONSTRAINT `sterilized_monitor_constraint_1` FOREIGN KEY (`sterilized_batch_id`) REFERENCES `STERILIZED_BATCH` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3;
