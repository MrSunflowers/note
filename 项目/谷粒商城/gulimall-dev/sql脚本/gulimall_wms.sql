/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.56.10
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : 192.168.56.10:3306
 Source Schema         : gulimall_wms

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 17/03/2022 22:14:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime(0) NOT NULL,
  `log_modified` datetime(0) NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for wms_purchase
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assignee_id` bigint(20) NULL DEFAULT NULL,
  `assignee_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` char(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `priority` int(4) NULL DEFAULT NULL,
  `status` int(4) NULL DEFAULT NULL,
  `ware_id` bigint(20) NULL DEFAULT NULL,
  `amount` decimal(18, 4) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '采购单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_purchase
-- ----------------------------
INSERT INTO `wms_purchase` VALUES (3, 1, 'admin', '13612345678', 1, 3, NULL, NULL, NULL, '2021-10-13 01:33:13');
INSERT INTO `wms_purchase` VALUES (6, 1, 'admin', '13612345678', NULL, 3, NULL, NULL, '2021-10-13 01:45:46', '2021-10-13 01:48:48');
INSERT INTO `wms_purchase` VALUES (7, 1, 'admin', '13612345678', 1, 3, NULL, NULL, '2022-01-04 16:23:16', '2022-01-04 20:36:35');

-- ----------------------------
-- Table structure for wms_purchase_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint(20) NULL DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int(11) NULL DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '采购需求' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_purchase_detail
-- ----------------------------
INSERT INTO `wms_purchase_detail` VALUES (1, 3, 1, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (2, 3, 2, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (7, 6, 1, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (8, 6, 3, 10, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (9, 7, 28, 100, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (10, 7, 29, 100, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (11, 7, 30, 100, NULL, 1, 3);
INSERT INTO `wms_purchase_detail` VALUES (12, 7, 31, 100, NULL, 1, 3);

-- ----------------------------
-- Table structure for wms_ware_info
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '仓库信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_info
-- ----------------------------
INSERT INTO `wms_ware_info` VALUES (1, '上海仓库', '上海市浦东区', '10001');

-- ----------------------------
-- Table structure for wms_ware_order_task
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) NULL DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint(2) NULL DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create_time',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存工作单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_order_task
-- ----------------------------
INSERT INTO `wms_ware_order_task` VALUES (1, NULL, '202201042038405851478345132569833474', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 12:38:41', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (2, NULL, '202201042048344541478347623436922881', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 12:48:35', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (3, NULL, '202201042051098841478348275353399297', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 12:51:10', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (4, NULL, '202201042051405291478348403892039681', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 12:52:01', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (5, NULL, '202201042052489591478348690904068098', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 12:52:49', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (7, NULL, '202201042103206101478351340240384001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 13:03:23', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (8, NULL, '202201042104524871478351725596258305', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 13:04:53', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (9, NULL, '202201042105431671478351938172076033', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 13:05:43', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (10, NULL, '202201042148297481478362703184642049', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 13:48:30', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (11, NULL, '202201042342023311478391277237092353', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 15:42:03', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (12, NULL, '202201042343533341478391742809030658', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 15:43:53', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (13, NULL, '202201042345074401478392053640515585', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 15:45:08', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (14, NULL, '202201042347345041478392670471659522', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-04 15:47:35', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (15, NULL, '202201051152442511478575163577589762', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 03:52:45', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (16, NULL, '202201051829439681478675070711762946', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 10:29:45', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (17, NULL, '202201051834083391478676179564113922', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 10:34:09', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (18, NULL, '202201051835449961478676584973066242', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 10:35:46', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (19, NULL, '202201051838260521478677260490805250', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 10:38:27', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (20, NULL, '202201051841268881478678018971959298', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 10:41:28', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (21, NULL, '202201051844061411478678686923259905', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 10:44:07', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (22, NULL, '202201051929272671478690100157214721', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 11:29:28', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (23, NULL, '202201051930024421478690247683469313', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 11:30:03', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (24, NULL, '202201051940300971478692880267620354', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 11:40:31', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (25, NULL, '202201051953201481478696110087221249', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 11:53:21', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (26, NULL, '202201052006407071478699467887652866', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 12:06:42', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (27, NULL, '202201052014186571478701388656586754', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 12:14:19', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (28, NULL, '202201052114372251478716566039175170', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 13:14:38', NULL, NULL);
INSERT INTO `wms_ware_order_task` VALUES (29, NULL, '202201052116089011478716950560378882', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-05 13:16:10', NULL, NULL);

-- ----------------------------
-- Table structure for wms_ware_order_task_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int(11) NULL DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint(20) NULL DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `lock_status` int(1) NULL DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存工作单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_order_task_detail
-- ----------------------------
INSERT INTO `wms_ware_order_task_detail` VALUES (1, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 1, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (2, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 2, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (3, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 3, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (4, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 4, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (5, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 5, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (7, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 7, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (8, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 8, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (9, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 9, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (10, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 10, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (11, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 11, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (12, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 12, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (13, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 13, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (14, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 1, 14, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (15, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 15, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (16, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 15, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (17, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 16, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (18, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 16, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (19, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 17, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (20, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 17, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (21, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 18, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (22, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 18, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (23, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 19, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (24, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 19, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (25, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 20, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (26, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 20, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (27, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 21, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (28, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 21, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (29, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 22, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (30, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 22, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (31, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 23, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (32, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 23, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (33, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 24, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (34, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 24, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (35, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 25, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (36, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 25, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (37, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 26, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (38, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 26, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (39, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 27, 1, 1);
INSERT INTO `wms_ware_order_task_detail` VALUES (40, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 27, 1, 1);
INSERT INTO `wms_ware_order_task_detail` VALUES (41, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 28, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (42, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 28, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (43, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2, 29, 1, 2);
INSERT INTO `wms_ware_order_task_detail` VALUES (44, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2, 29, 1, 2);

-- ----------------------------
-- Table structure for wms_ware_sku
-- ----------------------------
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `stock` int(11) NULL DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int(11) NULL DEFAULT 0 COMMENT '锁定库存',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sku_id`(`sku_id`) USING BTREE,
  INDEX `ware_id`(`ware_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品库存' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wms_ware_sku
-- ----------------------------
INSERT INTO `wms_ware_sku` VALUES (8, 28, 1, 100, '华为Mate40 Pro手机 黑色 4G版8GB+256G', 0);
INSERT INTO `wms_ware_sku` VALUES (9, 29, 1, 100, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 2);
INSERT INTO `wms_ware_sku` VALUES (10, 30, 1, 100, '华为Mate40 Pro手机 白色 4G版8GB+256G', 0);
INSERT INTO `wms_ware_sku` VALUES (11, 31, 1, 100, '华为Mate40 Pro手机 白色 5G版8GB+256G', 2);

SET FOREIGN_KEY_CHECKS = 1;
