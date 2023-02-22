/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : sims

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 04/05/2022 14:32:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for add_package_amount_record
-- ----------------------------
DROP TABLE IF EXISTS `add_package_amount_record`;
CREATE TABLE `add_package_amount_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `package_config_id` bigint NOT NULL COMMENT '盤種config id',
  `create_time` datetime NOT NULL COMMENT '申請時間',
  `creator` int NOT NULL COMMENT '申請者',
  `update_time` datetime NOT NULL COMMENT '更新時間',
  `updater` int NOT NULL COMMENT '更新者',
  `status` tinyint NOT NULL COMMENT '0:全部取消, 1:申請, 2:處理中, 3:完成 //修改 ',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `add_constraint_2`(`package_config_id` ASC) USING BTREE,
  CONSTRAINT `add_constraint_2` FOREIGN KEY (`package_config_id`) REFERENCES `package_config` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of add_package_amount_record
-- ----------------------------
INSERT INTO `add_package_amount_record` VALUES (1, 1, '2022-05-03 17:20:49', 1, '2022-05-03 17:29:53', 1, 3);
INSERT INTO `add_package_amount_record` VALUES (2, 1, '2022-05-04 02:02:08', 1, '2022-05-04 03:24:39', 1, 0);
INSERT INTO `add_package_amount_record` VALUES (3, 2, '2022-05-04 02:06:17', 1, '2022-05-04 03:24:37', 1, 0);
INSERT INTO `add_package_amount_record` VALUES (4, 3, '2022-05-04 02:12:46', 1, '2022-05-04 02:13:51', 1, 3);
INSERT INTO `add_package_amount_record` VALUES (5, 1, '2022-05-04 03:23:08', 1, '2022-05-04 03:24:41', 1, 0);
INSERT INTO `add_package_amount_record` VALUES (6, 1, '2022-05-04 03:25:21', 1, '2022-05-04 03:25:21', 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
