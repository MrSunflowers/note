/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.56.10
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : 192.168.56.10:3306
 Source Schema         : gulimall_oms

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 17/03/2022 22:14:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mq_message
-- ----------------------------
DROP TABLE IF EXISTS `mq_message`;
CREATE TABLE `mq_message`  (
  `message_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `to_exchane` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `routing_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `class_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `message_status` int(1) NULL DEFAULT 0 COMMENT '0-新建 1-已发送 2-错误抵达 3-已抵达',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oms_order
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) NULL DEFAULT NULL COMMENT 'member_id',
  `order_sn` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号',
  `coupon_id` bigint(20) NULL DEFAULT NULL COMMENT '使用的优惠券',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create_time',
  `member_username` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `total_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '订单总额',
  `pay_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '应付总额',
  `freight_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '运费金额',
  `promotion_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '促销优化金额（促销价、满减、阶梯价）',
  `integration_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '积分抵扣金额',
  `coupon_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '优惠券抵扣金额',
  `discount_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '后台调整订单使用的折扣金额',
  `pay_type` int(4) NULL DEFAULT NULL COMMENT '支付方式【PAYPAL中国->101；PAYPAL香港->102；PAYPAL全球->103；支付宝->201；支付宝香港->202；支付宝全球->203；微信->301；微信香港->302；微信全球->303；银联->401；货到付款->501】',
  `source_type` tinyint(4) NULL DEFAULT NULL COMMENT '订单来源[0->PC订单；1->app订单]',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
  `delivery_company` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流公司(配送方式)',
  `delivery_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物流单号',
  `auto_confirm_day` int(11) NULL DEFAULT NULL COMMENT '自动确认时间（天）',
  `integration` int(11) NULL DEFAULT NULL COMMENT '可以获得的积分',
  `growth` int(11) NULL DEFAULT NULL COMMENT '可以获得的成长值',
  `bill_type` tinyint(4) NULL DEFAULT NULL COMMENT '发票类型[0->不开发票；1->电子发票；2->纸质发票]',
  `bill_header` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发票抬头',
  `bill_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发票内容',
  `bill_receiver_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收票人电话',
  `bill_receiver_email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收票人邮箱',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `receiver_post_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人邮编',
  `receiver_province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份/直辖市',
  `receiver_city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '城市',
  `receiver_region` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区',
  `receiver_detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `confirm_status` tinyint(4) NULL DEFAULT NULL COMMENT '确认收货状态[0->未确认；1->已确认]',
  `delete_status` tinyint(4) NULL DEFAULT NULL COMMENT '删除状态【0->未删除；1->已删除】',
  `use_integration` int(11) NULL DEFAULT NULL COMMENT '下单时使用的积分',
  `payment_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime(0) NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime(0) NULL DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime(0) NULL DEFAULT NULL COMMENT '评价时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order
-- ----------------------------
INSERT INTO `oms_order` VALUES (1, 1, '202201042038405851478345132569833474', NULL, '2022-01-04 20:38:41', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 20:38:41');
INSERT INTO `oms_order` VALUES (2, 1, '202201042048344541478347623436922881', NULL, '2022-01-04 20:48:34', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 20:48:34');
INSERT INTO `oms_order` VALUES (3, 1, '202201042051098841478348275353399297', NULL, '2022-01-04 20:51:10', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 20:51:10');
INSERT INTO `oms_order` VALUES (4, 1, '202201042051405291478348403892039681', NULL, '2022-01-04 20:51:41', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 20:51:41');
INSERT INTO `oms_order` VALUES (5, 1, '202201042052489591478348690904068098', NULL, '2022-01-04 20:52:49', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 20:52:49');
INSERT INTO `oms_order` VALUES (8, 1, '202201042104524871478351725596258305', NULL, '2022-01-04 21:04:52', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 21:04:52');
INSERT INTO `oms_order` VALUES (9, 1, '202201042105431671478351938172076033', NULL, '2022-01-04 21:05:43', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 21:05:43');
INSERT INTO `oms_order` VALUES (10, 1, '202201042148297481478362703184642049', NULL, '2022-01-04 21:48:30', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 21:48:30');
INSERT INTO `oms_order` VALUES (11, 1, '202201042342023311478391277237092353', NULL, '2022-01-04 23:42:02', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 23:42:02');
INSERT INTO `oms_order` VALUES (12, 1, '202201042343533341478391742809030658', NULL, '2022-01-04 23:43:53', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 23:43:53');
INSERT INTO `oms_order` VALUES (13, 1, '202201042345074401478392053640515585', NULL, '2022-01-04 23:45:07', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 23:45:07');
INSERT INTO `oms_order` VALUES (14, 1, '202201042347345041478392670471659522', NULL, '2022-01-04 23:47:35', NULL, 7149.0000, 7155.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 7149, 7149, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-04 23:47:35');
INSERT INTO `oms_order` VALUES (15, 1, '202201051152442511478575163577589762', NULL, '2022-01-05 11:52:44', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 11:52:44');
INSERT INTO `oms_order` VALUES (16, 1, '202201051829439681478675070711762946', NULL, '2022-01-05 18:29:44', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 18:29:44');
INSERT INTO `oms_order` VALUES (17, 1, '202201051834083391478676179564113922', NULL, '2022-01-05 18:34:08', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 18:34:08');
INSERT INTO `oms_order` VALUES (18, 1, '202201051835449961478676584973066242', NULL, '2022-01-05 18:35:45', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 18:35:45');
INSERT INTO `oms_order` VALUES (19, 1, '202201051838260521478677260490805250', NULL, '2022-01-05 18:38:26', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 18:38:26');
INSERT INTO `oms_order` VALUES (20, 1, '202201051841268881478678018971959298', NULL, '2022-01-05 18:41:27', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 18:41:27');
INSERT INTO `oms_order` VALUES (21, 1, '202201051844061411478678686923259905', NULL, '2022-01-05 18:44:06', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 18:44:06');
INSERT INTO `oms_order` VALUES (22, 1, '202201051929272671478690100157214721', NULL, '2022-01-05 19:29:27', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 19:29:27');
INSERT INTO `oms_order` VALUES (23, 1, '202201051930024421478690247683469313', NULL, '2022-01-05 19:30:02', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 19:30:02');
INSERT INTO `oms_order` VALUES (24, 1, '202201051940300971478692880267620354', NULL, '2022-01-05 19:40:30', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 19:40:30');
INSERT INTO `oms_order` VALUES (25, 1, '202201051953201481478696110087221249', NULL, '2022-01-05 19:53:20', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 19:53:20');
INSERT INTO `oms_order` VALUES (26, 1, '202201052006407071478699467887652866', NULL, '2022-01-05 20:06:41', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 20:06:41');
INSERT INTO `oms_order` VALUES (27, 1, '202201052014186571478701388656586754', NULL, '2022-01-05 20:14:19', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, 201, NULL, 1, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, '2022-01-05 12:14:42', NULL, NULL, NULL, '2022-01-05 12:14:42');
INSERT INTO `oms_order` VALUES (28, 1, '202201052114372251478716566039175170', NULL, '2022-01-05 21:14:37', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 21:14:37');
INSERT INTO `oms_order` VALUES (29, 1, '202201052116089011478716950560378882', NULL, '2022-01-05 21:16:09', NULL, 28696.0000, 28702.0000, 6.0000, 0.0000, 0.0000, 0.0000, NULL, NULL, NULL, 4, NULL, NULL, 7, 28696, 28696, NULL, NULL, NULL, NULL, NULL, 'wan', '123456', '111', '上海市', '上海市', '静安区', '樱木街道', NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, '2022-01-05 21:16:09');
INSERT INTO `oms_order` VALUES (31, 1, '202201070011390871479123505223118850', NULL, '2022-01-07 00:11:39', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `oms_order` VALUES (32, 1, '202201070025289121479126985753047042', NULL, '2022-01-07 00:25:29', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `oms_order` VALUES (33, 1, '202201070026074131479127147237945345', NULL, '2022-01-07 00:26:07', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `oms_order` VALUES (34, 1, '202201070032386101479128788045414401', NULL, '2022-01-07 00:32:39', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `oms_order` VALUES (35, 1, '202201070033373791479129034536271874', NULL, '2022-01-07 00:33:37', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `oms_order` VALUES (36, 1, '202201070035510341479129595130216450', NULL, '2022-01-07 00:35:51', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `oms_order` VALUES (37, 1, '202201070038431941479130317217398786', NULL, '2022-01-07 00:38:43', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `oms_order` VALUES (38, 1, '202201070041141651479130950435667969', NULL, '2022-01-07 00:41:14', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, 201, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-06 16:41:48', NULL, NULL, NULL, '2022-01-06 16:41:48');
INSERT INTO `oms_order` VALUES (39, 1, '202201081914529021479773596401377281', NULL, '2022-01-08 19:14:53', NULL, 5000.0000, 5000.0000, NULL, NULL, NULL, NULL, NULL, 201, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2022-01-08 11:15:15', NULL, NULL, NULL, '2022-01-08 11:15:15');

-- ----------------------------
-- Table structure for oms_order_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `order_sn` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'order_sn',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spu_name',
  `spu_pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spu_pic',
  `spu_brand` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品牌',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '商品分类id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '商品sku编号',
  `sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品sku名字',
  `sku_pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品sku图片',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品sku价格',
  `sku_quantity` int(11) NULL DEFAULT NULL COMMENT '商品购买的数量',
  `sku_attrs_vals` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品销售属性组合（JSON）',
  `promotion_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品促销分解金额',
  `coupon_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '优惠券优惠分解金额',
  `integration_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '积分优惠分解金额',
  `real_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '该商品经过优惠后的分解金额',
  `gift_integration` int(11) NULL DEFAULT NULL COMMENT '赠送积分',
  `gift_growth` int(11) NULL DEFAULT NULL COMMENT '赠送成长值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单项信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_order_item
-- ----------------------------
INSERT INTO `oms_order_item` VALUES (1, NULL, '202201042038405851478345132569833474', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (2, NULL, '202201042048344541478347623436922881', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (3, NULL, '202201042051098841478348275353399297', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (4, NULL, '202201042051405291478348403892039681', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (5, NULL, '202201042052489591478348690904068098', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (8, NULL, '202201042104524871478351725596258305', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (9, NULL, '202201042105431671478351938172076033', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (10, NULL, '202201042148297481478362703184642049', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (11, NULL, '202201042342023311478391277237092353', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (12, NULL, '202201042343533341478391742809030658', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (13, NULL, '202201042345074401478392053640515585', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (14, NULL, '202201042347345041478392670471659522', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 1, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 7149.0000, 7149, 7149);
INSERT INTO `oms_order_item` VALUES (15, NULL, '202201051152442511478575163577589762', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (16, NULL, '202201051152442511478575163577589762', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (17, NULL, '202201051829439681478675070711762946', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (18, NULL, '202201051829439681478675070711762946', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (19, NULL, '202201051834083391478676179564113922', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (20, NULL, '202201051834083391478676179564113922', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (21, NULL, '202201051835449961478676584973066242', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (22, NULL, '202201051835449961478676584973066242', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (23, NULL, '202201051838260521478677260490805250', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (24, NULL, '202201051838260521478677260490805250', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (25, NULL, '202201051841268881478678018971959298', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (26, NULL, '202201051841268881478678018971959298', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (27, NULL, '202201051844061411478678686923259905', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (28, NULL, '202201051844061411478678686923259905', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (29, NULL, '202201051929272671478690100157214721', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (30, NULL, '202201051929272671478690100157214721', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (31, NULL, '202201051930024421478690247683469313', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (32, NULL, '202201051930024421478690247683469313', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (33, NULL, '202201051940300971478692880267620354', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (34, NULL, '202201051940300971478692880267620354', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (35, NULL, '202201051953201481478696110087221249', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (36, NULL, '202201051953201481478696110087221249', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (37, NULL, '202201052006407071478699467887652866', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (38, NULL, '202201052006407071478699467887652866', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (39, NULL, '202201052014186571478701388656586754', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (40, NULL, '202201052014186571478701388656586754', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (41, NULL, '202201052114372251478716566039175170', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (42, NULL, '202201052114372251478716566039175170', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (43, NULL, '202201052116089011478716950560378882', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 29, '华为Mate40 Pro手机 黑色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/3fc04a6b-c4eb-440a-87cd-22e0fad725b2_black1.jpg', 7199.0000, 2, '颜色:黑色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14398.0000, 14398, 14398);
INSERT INTO `oms_order_item` VALUES (44, NULL, '202201052116089011478716950560378882', 18, '华为Mate40 Pro手机', NULL, '华为Mate40 Pro手机', 225, 31, '华为Mate40 Pro手机 白色 5G版8GB+256G', 'https://gulimall-wan.oss-cn-shanghai.aliyuncs.com/2022-01-04/a4d46269-7fcb-4fdf-87a9-d97635a6e499_white1.jpg', 7149.0000, 2, '颜色:白色;版本:5G版8GB+256G', 0.0000, 0.0000, 0.0000, 14298.0000, 14298, 14298);
INSERT INTO `oms_order_item` VALUES (46, NULL, '202201070011390871479123505223118850', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (47, NULL, '202201070025289121479126985753047042', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (48, NULL, '202201070026074131479127147237945345', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (49, NULL, '202201070032386101479128788045414401', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (50, NULL, '202201070033373791479129034536271874', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (51, NULL, '202201070035510341479129595130216450', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (52, NULL, '202201070038431941479130317217398786', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (53, NULL, '202201070041141651479130950435667969', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);
INSERT INTO `oms_order_item` VALUES (54, NULL, '202201081914529021479773596401377281', 18, '华为Mate40 Pro手机', NULL, '华为', 225, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 5000.0000, NULL, NULL);

-- ----------------------------
-- Table structure for oms_order_operate_history
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_operate_history`;
CREATE TABLE `oms_order_operate_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单id',
  `operate_man` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人[用户；系统；后台管理员]',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  `order_status` tinyint(4) NULL DEFAULT NULL COMMENT '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单操作历史记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oms_order_return_apply
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_return_apply`;
CREATE TABLE `oms_order_return_apply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT 'order_id',
  `sku_id` bigint(20) NULL DEFAULT NULL COMMENT '退货商品id',
  `order_sn` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '申请时间',
  `member_username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会员用户名',
  `return_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '退款金额',
  `return_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '退货人姓名',
  `return_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '退货人电话',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '申请状态[0->待处理；1->退货中；2->已完成；3->已拒绝]',
  `handle_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  `sku_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `sku_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `sku_brand` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品品牌',
  `sku_attrs_vals` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品销售属性(JSON)',
  `sku_count` int(11) NULL DEFAULT NULL COMMENT '退货数量',
  `sku_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品单价',
  `sku_real_price` decimal(18, 4) NULL DEFAULT NULL COMMENT '商品实际支付单价',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原因',
  `description述` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `desc_pics` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '凭证图片，以逗号隔开',
  `handle_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理备注',
  `handle_man` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理人员',
  `receive_man` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `receive_time` datetime(0) NULL DEFAULT NULL COMMENT '收货时间',
  `receive_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货备注',
  `receive_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货电话',
  `company_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司收货地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单退货申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oms_order_return_reason
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_return_reason`;
CREATE TABLE `oms_order_return_reason`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '退货原因名',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '启用状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT 'create_time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '退货原因' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oms_order_setting
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_setting`;
CREATE TABLE `oms_order_setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `flash_order_overtime` int(11) NULL DEFAULT NULL COMMENT '秒杀订单超时关闭时间(分)',
  `normal_order_overtime` int(11) NULL DEFAULT NULL COMMENT '正常订单超时时间(分)',
  `confirm_overtime` int(11) NULL DEFAULT NULL COMMENT '发货后自动确认收货时间（天）',
  `finish_overtime` int(11) NULL DEFAULT NULL COMMENT '自动完成交易时间，不能申请退货（天）',
  `comment_overtime` int(11) NULL DEFAULT NULL COMMENT '订单完成后自动好评时间（天）',
  `member_level` tinyint(2) NULL DEFAULT NULL COMMENT '会员等级【0-不限会员等级，全部通用；其他-对应的其他会员等级】',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单配置信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oms_payment_info
-- ----------------------------
DROP TABLE IF EXISTS `oms_payment_info`;
CREATE TABLE `oms_payment_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_sn` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单号（对外业务号）',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单id',
  `alipay_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付宝交易流水号',
  `total_amount` decimal(18, 4) NULL DEFAULT NULL COMMENT '支付总金额',
  `subject` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易内容',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付状态',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `confirm_time` datetime(0) NULL DEFAULT NULL COMMENT '确认时间',
  `callback_content` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回调内容',
  `callback_time` datetime(0) NULL DEFAULT NULL COMMENT '回调时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_sn`(`order_sn`) USING BTREE,
  UNIQUE INDEX `alipay_trade_no`(`alipay_trade_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支付信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oms_payment_info
-- ----------------------------
INSERT INTO `oms_payment_info` VALUES (1, '202201052006407071478699467887652866', NULL, '2022010522001419520515109216', 28702.0000, '颜色:黑色;版本:5G版8GB+256G', 'TRADE_SUCCESS', '2022-01-05 20:07:02', NULL, NULL, '2022-01-05 20:06:56');
INSERT INTO `oms_payment_info` VALUES (2, '202201052014186571478701388656586754', NULL, '2022010522001419520515100771', 28702.0000, '颜色:黑色;版本:5G版8GB+256G', 'TRADE_SUCCESS', '2022-01-05 20:14:41', NULL, NULL, '2022-01-05 20:14:40');
INSERT INTO `oms_payment_info` VALUES (3, '202201070041141651479130950435667969', NULL, '2022010722001419520515157287', 5000.0000, 'null', 'TRADE_SUCCESS', '2022-01-07 00:41:47', NULL, NULL, '2022-01-07 00:41:47');
INSERT INTO `oms_payment_info` VALUES (4, '202201081914529021479773596401377281', NULL, '2022010822001419520515254391', 5000.0000, 'null', 'TRADE_SUCCESS', '2022-01-08 19:15:14', NULL, NULL, '2022-01-08 19:15:15');

-- ----------------------------
-- Table structure for oms_refund_info
-- ----------------------------
DROP TABLE IF EXISTS `oms_refund_info`;
CREATE TABLE `oms_refund_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_return_id` bigint(20) NULL DEFAULT NULL COMMENT '退款的订单',
  `refund` decimal(18, 4) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '退款交易流水号',
  `refund_status` tinyint(1) NULL DEFAULT NULL COMMENT '退款状态',
  `refund_channel` tinyint(4) NULL DEFAULT NULL COMMENT '退款渠道[1-支付宝，2-微信，3-银联，4-汇款]',
  `refund_content` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '退款信息' ROW_FORMAT = Dynamic;

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

SET FOREIGN_KEY_CHECKS = 1;
