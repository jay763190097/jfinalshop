/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50716
 Source Host           : localhost
 Source Database       : jfinalshop4

 Target Server Type    : MySQL
 Target Server Version : 50716
 File Encoding         : utf-8

 Date: 06/12/2017 14:18:52 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ad`
-- ----------------------------
DROP TABLE IF EXISTS `ad`;
CREATE TABLE `ad` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `begin_date` datetime DEFAULT NULL COMMENT '起始日期',
  `content` longtext COMMENT '内容',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `path` varchar(255) DEFAULT NULL COMMENT '路径',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
  `type` int(11) NOT NULL COMMENT '类型',
  `url` varchar(255) DEFAULT NULL COMMENT '链接地址',
  `ad_position_id` bigint(20) NOT NULL COMMENT '广告位ID',
  PRIMARY KEY (`id`),
  KEY `ind_ad_ad_position` (`ad_position_id`),
  CONSTRAINT `fk_ad_ad_position` FOREIGN KEY (`ad_position_id`) REFERENCES `ad_position` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ad_position`
-- ----------------------------
DROP TABLE IF EXISTS `ad_position`;
CREATE TABLE `ad_position` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `height` int(11) NOT NULL COMMENT '高度',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `template` longtext NOT NULL COMMENT '模版',
  `width` int(11) NOT NULL COMMENT '宽度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `department` varchar(255) DEFAULT NULL COMMENT '部门',
  `email` varchar(255) NOT NULL DEFAULT '' COMMENT 'email',
  `is_enabled` bit(1) NOT NULL COMMENT '是否启用',
  `is_locked` bit(1) NOT NULL COMMENT '是否锁定',
  `lock_key` varchar(255) NOT NULL DEFAULT '' COMMENT '锁定KEY',
  `locked_date` datetime DEFAULT NULL COMMENT '锁定日期',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `login_failure_count` int(11) NOT NULL COMMENT '连续登录失败次数',
  `login_ip` varchar(255) DEFAULT NULL COMMENT '最后登录IP',
  `hasher` varchar(255) DEFAULT NULL COMMENT '加密类型',
  `salt` varchar(255) DEFAULT NULL COMMENT '加密盐',
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_admin_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `admin_role`
-- ----------------------------
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role` (
  `admins` bigint(20) NOT NULL,
  `roles` bigint(20) NOT NULL,
  PRIMARY KEY (`admins`,`roles`),
  KEY `ind_admin_role_roles` (`roles`),
  CONSTRAINT `fk_admin_role_admins` FOREIGN KEY (`admins`) REFERENCES `admin` (`id`),
  CONSTRAINT `fk_admin_role_roles` FOREIGN KEY (`roles`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `area`
-- ----------------------------
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单',
  `full_name` longtext NOT NULL COMMENT '全称',
  `grade` int(11) NOT NULL COMMENT '层级',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `tree_path` varchar(255) NOT NULL DEFAULT '' COMMENT '树路径',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级地区ID',
  PRIMARY KEY (`id`),
  KEY `ind_area_parent` (`parent_id`),
  CONSTRAINT `fk_area_parent` FOREIGN KEY (`parent_id`) REFERENCES `area` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3223 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `article`
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `author` varchar(255) DEFAULT NULL COMMENT '作者',
  `content` longtext COMMENT '内容',
  `generate_method` int(11) NOT NULL COMMENT '静态生成方式',
  `hits` bigint(20) NOT NULL COMMENT '点击数',
  `is_publication` bit(1) NOT NULL COMMENT '是否发布',
  `is_top` bit(1) NOT NULL COMMENT '是否置顶',
  `seo_description` varchar(255) DEFAULT NULL COMMENT '页面描述',
  `seo_keywords` varchar(255) DEFAULT NULL COMMENT '页面关键词',
  `seo_title` varchar(255) DEFAULT NULL COMMENT '页面标题',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
  `article_category_id` bigint(20) NOT NULL COMMENT '文章分类ID',
  PRIMARY KEY (`id`),
  KEY `ind_article_article_category` (`article_category_id`),
  CONSTRAINT `fk_article_article_category` FOREIGN KEY (`article_category_id`) REFERENCES `article_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `article_category`
-- ----------------------------
DROP TABLE IF EXISTS `article_category`;
CREATE TABLE `article_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '排序',
  `grade` int(11) NOT NULL COMMENT '层级',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `seo_description` varchar(255) DEFAULT NULL COMMENT '页面描述',
  `seo_keywords` varchar(255) DEFAULT NULL COMMENT '页面关键词',
  `seo_title` varchar(255) DEFAULT NULL COMMENT '页面标题',
  `tree_path` varchar(255) NOT NULL DEFAULT '' COMMENT '树路径',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级分类ID',
  PRIMARY KEY (`id`),
  KEY `ind_article_category_parent` (`parent_id`),
  CONSTRAINT `fk_article_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `article_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `article_tag`
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
  `articles` bigint(20) NOT NULL,
  `tags` bigint(20) NOT NULL,
  PRIMARY KEY (`articles`,`tags`),
  KEY `ind_article_tag_tags` (`tags`),
  CONSTRAINT `fk_article_tag_articles` FOREIGN KEY (`articles`) REFERENCES `article` (`id`),
  CONSTRAINT `fk_article_tag_tags` FOREIGN KEY (`tags`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `attribute`
-- ----------------------------
DROP TABLE IF EXISTS `attribute`;
CREATE TABLE `attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '排序',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `options` longtext NOT NULL COMMENT '可选项',
  `property_index` int(11) NOT NULL COMMENT '属性序号',
  `product_category_id` bigint(20) NOT NULL COMMENT '绑定分类ID',
  PRIMARY KEY (`id`),
  KEY `ind_attribute_product_category` (`product_category_id`),
  CONSTRAINT `fk_attribute_product_category` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `brand`
-- ----------------------------
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '排序',
  `introduction` longtext COMMENT '介绍',
  `logo` varchar(255) DEFAULT NULL COMMENT 'logo',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `type` int(11) NOT NULL COMMENT '类型',
  `url` varchar(255) DEFAULT NULL COMMENT '网址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `cart`
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `expire` datetime NOT NULL COMMENT '过期时间',
  `cart_key` varchar(255) NOT NULL DEFAULT '' COMMENT '密钥',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_cart_cart_key` (`cart_key`),
  KEY `ind_cart_member` (`member_id`),
  CONSTRAINT `fk_cart_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `cart_item`
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `cart_id` bigint(20) NOT NULL COMMENT '购物车ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  KEY `ind_cart_item_cart` (`cart_id`),
  KEY `ind_cart_item_product` (`product_id`),
  CONSTRAINT `fk_cart_item_cart` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `consultation`
-- ----------------------------
DROP TABLE IF EXISTS `consultation`;
CREATE TABLE `consultation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '内容',
  `ip` varchar(255) NOT NULL DEFAULT '' COMMENT 'IP',
  `is_show` bit(1) NOT NULL COMMENT '是否显示',
  `for_consultation_id` bigint(20) DEFAULT NULL COMMENT '咨询ID',
  `goods_id` bigint(20) NOT NULL COMMENT '货品ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  PRIMARY KEY (`id`),
  KEY `ind_consultation_for_c` (`for_consultation_id`),
  KEY `ind_consultation_goods` (`goods_id`),
  KEY `ind_consultation_member` (`member_id`),
  CONSTRAINT `fk_consultation_for_c` FOREIGN KEY (`for_consultation_id`) REFERENCES `consultation` (`id`),
  CONSTRAINT `fk_consultation_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`),
  CONSTRAINT `fk_consultation_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `coupon`
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `begin_date` datetime DEFAULT NULL COMMENT '使用起始日期',
  `end_date` datetime DEFAULT NULL COMMENT '使用结束日期',
  `introduction` longtext COMMENT '介绍',
  `is_enabled` bit(1) NOT NULL COMMENT '是否启用',
  `is_exchange` bit(1) NOT NULL COMMENT '是否允许积分兑换',
  `maximum_price` decimal(21,6) DEFAULT NULL COMMENT '最大商品价格',
  `maximum_quantity` int(11) DEFAULT NULL COMMENT '最大商品数量',
  `minimum_price` decimal(21,6) DEFAULT NULL COMMENT '最小商品价格',
  `minimum_quantity` int(11) DEFAULT NULL COMMENT '最小商品数量',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `point` bigint(20) DEFAULT NULL COMMENT '积分兑换数',
  `prefix` varchar(255) NOT NULL DEFAULT '' COMMENT '前缀',
  `price_expression` varchar(255) DEFAULT NULL COMMENT '价格运算表达式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `coupon_code`
-- ----------------------------
DROP TABLE IF EXISTS `coupon_code`;
CREATE TABLE `coupon_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `code` varchar(255) NOT NULL DEFAULT '' COMMENT '号码',
  `is_used` bit(1) NOT NULL COMMENT '是否已使用',
  `used_date` datetime DEFAULT NULL COMMENT '使用日期',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_coupon_code_code` (`code`),
  KEY `ind_coupon_code_coupon` (`coupon_id`),
  KEY `ind_coupon_code_member` (`member_id`),
  CONSTRAINT `fk_coupon_code_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`),
  CONSTRAINT `fk_coupon_code_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `delivery_center`
-- ----------------------------
DROP TABLE IF EXISTS `delivery_center`;
CREATE TABLE `delivery_center` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '地址',
  `area_name` varchar(255) NOT NULL DEFAULT '' COMMENT '地区名称',
  `contact` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人',
  `is_default` bit(1) NOT NULL COMMENT '是否默认',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `zip_code` varchar(255) DEFAULT NULL COMMENT '邮编',
  `area_id` bigint(20) DEFAULT NULL COMMENT '地区',
  PRIMARY KEY (`id`),
  KEY `ind_delivery_center_area` (`area_id`),
  CONSTRAINT `fk_delivery_center_area` FOREIGN KEY (`area_id`) REFERENCES `area` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `delivery_corp`
-- ----------------------------
DROP TABLE IF EXISTS `delivery_corp`;
CREATE TABLE `delivery_corp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '排序',
  `code` varchar(255) DEFAULT NULL COMMENT '代码',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `url` varchar(255) DEFAULT NULL COMMENT '网址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `delivery_template`
-- ----------------------------
DROP TABLE IF EXISTS `delivery_template`;
CREATE TABLE `delivery_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `background` varchar(255) DEFAULT NULL COMMENT '背景图',
  `content` longtext NOT NULL COMMENT '内容',
  `height` int(11) NOT NULL COMMENT '高度',
  `is_default` bit(1) NOT NULL COMMENT '是否默认',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `offsetx` int(11) NOT NULL COMMENT '偏移量X',
  `offsety` int(11) NOT NULL COMMENT '偏移量Y',
  `width` int(11) NOT NULL COMMENT '宽度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `deposit_log`
-- ----------------------------
DROP TABLE IF EXISTS `deposit_log`;
CREATE TABLE `deposit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `balance` decimal(21,6) NOT NULL COMMENT '当前余额',
  `credit` decimal(21,6) NOT NULL COMMENT '收入金额',
  `debit` decimal(21,6) NOT NULL COMMENT '支出金额',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作员',
  `type` int(11) NOT NULL COMMENT '类型',
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  PRIMARY KEY (`id`),
  KEY `ind_deposit_log_member` (`member_id`),
  CONSTRAINT `fk_deposit_log_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `feedback`
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `suggestion` varchar(300) DEFAULT NULL COMMENT '反馈内容',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机',
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  PRIMARY KEY (`id`),
  KEY `fk_feedback_member` (`member_id`),
  CONSTRAINT `fk_feedback_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `freight_config`
-- ----------------------------
DROP TABLE IF EXISTS `freight_config`;
CREATE TABLE `freight_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `continue_price` decimal(21,6) NOT NULL COMMENT '续重价格',
  `first_price` decimal(21,6) NOT NULL COMMENT '首重价格',
  `area_id` bigint(20) NOT NULL COMMENT '地区ID',
  `shipping_method_id` bigint(20) NOT NULL COMMENT '配送方式',
  PRIMARY KEY (`id`),
  KEY `ind_freight_config_area` (`area_id`),
  KEY `ind_freight_config_shipping_m` (`shipping_method_id`),
  CONSTRAINT `fk_freight_config_area` FOREIGN KEY (`area_id`) REFERENCES `area` (`id`),
  CONSTRAINT `fk_freight_config_shipping_m` FOREIGN KEY (`shipping_method_id`) REFERENCES `shipping_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `friend_link`
-- ----------------------------
DROP TABLE IF EXISTS `friend_link`;
CREATE TABLE `friend_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '排序',
  `logo` varchar(255) DEFAULT NULL COMMENT 'logo',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `type` int(11) NOT NULL COMMENT '类型',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '网址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `goods`
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `attribute_value0` varchar(255) DEFAULT NULL COMMENT '属性值0',
  `attribute_value1` varchar(255) DEFAULT NULL COMMENT '属性值1',
  `attribute_value10` varchar(255) DEFAULT NULL COMMENT '属性值10',
  `attribute_value11` varchar(255) DEFAULT NULL COMMENT '属性值11',
  `attribute_value12` varchar(255) DEFAULT NULL COMMENT '属性值12',
  `attribute_value13` varchar(255) DEFAULT NULL COMMENT '属性值13',
  `attribute_value14` varchar(255) DEFAULT NULL COMMENT '属性值14',
  `attribute_value15` varchar(255) DEFAULT NULL COMMENT '属性值15',
  `attribute_value16` varchar(255) DEFAULT NULL COMMENT '属性值16',
  `attribute_value17` varchar(255) DEFAULT NULL COMMENT '属性值17',
  `attribute_value18` varchar(255) DEFAULT NULL COMMENT '属性值18',
  `attribute_value19` varchar(255) DEFAULT NULL COMMENT '属性值19',
  `attribute_value2` varchar(255) DEFAULT NULL COMMENT '属性值2',
  `attribute_value3` varchar(255) DEFAULT NULL COMMENT '属性值3',
  `attribute_value4` varchar(255) DEFAULT NULL COMMENT '属性值4',
  `attribute_value5` varchar(255) DEFAULT NULL COMMENT '属性值5',
  `attribute_value6` varchar(255) DEFAULT NULL COMMENT '属性值6',
  `attribute_value7` varchar(255) DEFAULT NULL COMMENT '属性值7',
  `attribute_value8` varchar(255) DEFAULT NULL COMMENT '属性值8',
  `attribute_value9` varchar(255) DEFAULT NULL COMMENT '属性值9',
  `caption` varchar(255) DEFAULT NULL COMMENT '副标题',
  `generate_method` int(11) NOT NULL COMMENT '静态生成方式',
  `hits` bigint(20) NOT NULL COMMENT '点击数',
  `image` varchar(255) DEFAULT NULL COMMENT '展示图片',
  `introduction` longtext COMMENT '介绍',
  `is_delivery` bit(1) NOT NULL COMMENT '是否需要物流',
  `is_list` bit(1) NOT NULL COMMENT '是否列出',
  `is_marketable` bit(1) NOT NULL COMMENT '是否上架',
  `is_top` bit(1) NOT NULL COMMENT '是否置顶',
  `keyword` varchar(255) DEFAULT NULL COMMENT '搜索关键词',
  `market_price` decimal(21,6) NOT NULL COMMENT '市场价',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `month_hits` bigint(20) NOT NULL COMMENT '月点击数',
  `month_hits_date` datetime NOT NULL COMMENT '月点击数更新日期',
  `month_sales` bigint(20) NOT NULL COMMENT '月销量',
  `month_sales_date` datetime NOT NULL COMMENT '月销量更新日期',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `parameter_values` longtext COMMENT '参数值',
  `price` decimal(21,6) NOT NULL COMMENT '价格',
  `product_images` longtext COMMENT '商品图片',
  `sales` bigint(20) NOT NULL COMMENT '销量',
  `score` float NOT NULL COMMENT '评分',
  `score_count` bigint(20) NOT NULL COMMENT '评分数',
  `seo_description` varchar(255) DEFAULT NULL COMMENT '页面描述',
  `seo_keywords` varchar(255) DEFAULT NULL COMMENT '页面关键词',
  `seo_title` varchar(255) DEFAULT NULL COMMENT '页面标题',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `specification_items` longtext COMMENT '规格项',
  `total_score` bigint(20) NOT NULL COMMENT '总评分',
  `type` int(11) NOT NULL COMMENT '类型',
  `unit` varchar(255) DEFAULT NULL COMMENT '单位',
  `week_hits` bigint(20) NOT NULL COMMENT '周点击数',
  `week_hits_date` datetime NOT NULL COMMENT '周点击数更新时间',
  `week_sales` bigint(20) NOT NULL COMMENT '周销量',
  `week_sales_date` datetime NOT NULL COMMENT '周销量更新时间',
  `weight` int(11) DEFAULT NULL COMMENT '宽度',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌',
  `product_category_id` bigint(20) NOT NULL COMMENT '商品分类',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_goods_sn` (`sn`),
  KEY `ind_goods_brand` (`brand_id`),
  KEY `ind_goods_product_category` (`product_category_id`),
  CONSTRAINT `fk_goods_brand` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
  CONSTRAINT `fk_goods_product_category` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `goods_project`
-- ----------------------------
DROP TABLE IF EXISTS `goods_project`;
CREATE TABLE `goods_project` (
  `goods` bigint(20) NOT NULL,
  `projects` bigint(20) NOT NULL,
  PRIMARY KEY (`goods`,`projects`),
  KEY `fk_goods_project_projects` (`projects`),
  CONSTRAINT `fk_goods_project_goods` FOREIGN KEY (`goods`) REFERENCES `goods` (`id`),
  CONSTRAINT `fk_goods_project_projects` FOREIGN KEY (`projects`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `goods_promotion`
-- ----------------------------
DROP TABLE IF EXISTS `goods_promotion`;
CREATE TABLE `goods_promotion` (
  `goods` bigint(20) NOT NULL,
  `promotions` bigint(20) NOT NULL,
  PRIMARY KEY (`goods`,`promotions`),
  KEY `ind_goods_promotion_promotions` (`promotions`),
  CONSTRAINT `fk_goods_promotion_goods` FOREIGN KEY (`goods`) REFERENCES `goods` (`id`),
  CONSTRAINT `fk_goods_promotion_promotions` FOREIGN KEY (`promotions`) REFERENCES `promotion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `goods_tag`
-- ----------------------------
DROP TABLE IF EXISTS `goods_tag`;
CREATE TABLE `goods_tag` (
  `goods` bigint(20) NOT NULL,
  `tags` bigint(20) NOT NULL,
  PRIMARY KEY (`goods`,`tags`),
  KEY `ind_goods_tag_tags` (`tags`),
  CONSTRAINT `fk_goods_tag_goods` FOREIGN KEY (`goods`) REFERENCES `goods` (`id`),
  CONSTRAINT `fk_goods_tag_tags` FOREIGN KEY (`tags`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `log`
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `content` longtext COMMENT '内容',
  `ip` varchar(255) NOT NULL DEFAULT '' COMMENT 'IP',
  `operation` varchar(255) NOT NULL DEFAULT '' COMMENT '操作',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作员',
  `parameter` longtext COMMENT '请求参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `member`
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本日期',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `amount` decimal(27,12) NOT NULL COMMENT '消费金额',
  `attribute_value0` varchar(255) DEFAULT NULL COMMENT '会员注册项值0',
  `attribute_value1` varchar(255) DEFAULT NULL COMMENT '会员注册项值1',
  `attribute_value2` varchar(255) DEFAULT NULL COMMENT '会员注册项值2',
  `attribute_value3` varchar(255) DEFAULT NULL COMMENT '会员注册项值3',
  `attribute_value4` varchar(255) DEFAULT NULL COMMENT '会员注册项值4',
  `attribute_value5` varchar(255) DEFAULT NULL COMMENT '会员注册项值5',
  `attribute_value6` varchar(255) DEFAULT NULL COMMENT '会员注册项值6',
  `attribute_value7` varchar(255) DEFAULT NULL COMMENT '会员注册项值7',
  `attribute_value8` varchar(255) DEFAULT NULL COMMENT '会员注册项值8',
  `attribute_value9` varchar(255) DEFAULT NULL COMMENT '会员注册项值9',
  `balance` decimal(27,12) NOT NULL COMMENT '余额',
  `birth` datetime DEFAULT NULL COMMENT '出生日期',
  `email` varchar(255) DEFAULT NULL COMMENT 'Email',
  `gender` int(11) DEFAULT NULL COMMENT '性别',
  `is_enabled` bit(1) NOT NULL COMMENT '是否启用',
  `is_locked` bit(1) NOT NULL COMMENT '是否锁定',
  `lock_key` varchar(255) NOT NULL DEFAULT '' COMMENT '锁定KEY',
  `locked_date` datetime DEFAULT NULL COMMENT '锁定日期',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录日期',
  `login_failure_count` int(11) NOT NULL COMMENT '连续登录失败次数',
  `login_ip` varchar(255) DEFAULT NULL COMMENT '最后登录IP',
  `login_plugin_id` varchar(255) DEFAULT NULL COMMENT '登录插件ID',
  `mobile` varchar(255) DEFAULT NULL COMMENT '手机',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `open_id` varchar(255) DEFAULT NULL COMMENT 'openID',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `point` bigint(20) NOT NULL COMMENT '积分',
  `register_ip` varchar(255) NOT NULL DEFAULT '' COMMENT '注册IP',
  `safe_key_expire` datetime DEFAULT NULL COMMENT '安全密钥失效日期',
  `safe_key_value` varchar(255) DEFAULT NULL COMMENT '安全密钥',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `zip_code` varchar(255) DEFAULT NULL COMMENT '邮编',
  `area_id` bigint(20) DEFAULT NULL COMMENT '地区',
  `member_rank_id` bigint(20) NOT NULL COMMENT '会员等级',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_member_username` (`username`),
  KEY `ind_member_email` (`email`),
  KEY `ind_member_login_plugin_id_o_i` (`login_plugin_id`,`open_id`),
  KEY `ind_member_area` (`area_id`),
  KEY `ind_member_member_rank` (`member_rank_id`),
  CONSTRAINT `fk_member_area` FOREIGN KEY (`area_id`) REFERENCES `area` (`id`),
  CONSTRAINT `fk_member_member_rank` FOREIGN KEY (`member_rank_id`) REFERENCES `member_rank` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `member_attribute`
-- ----------------------------
DROP TABLE IF EXISTS `member_attribute`;
CREATE TABLE `member_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `is_enabled` bit(1) NOT NULL COMMENT '是否启用',
  `is_required` bit(1) NOT NULL COMMENT '是否必填',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `options` longtext COMMENT '可选项',
  `pattern` varchar(255) DEFAULT NULL COMMENT '配比',
  `property_index` int(11) DEFAULT NULL COMMENT '属性序号',
  `type` int(11) NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `member_favorite_goods`
-- ----------------------------
DROP TABLE IF EXISTS `member_favorite_goods`;
CREATE TABLE `member_favorite_goods` (
  `favorite_members` bigint(20) NOT NULL,
  `favorite_goods` bigint(20) NOT NULL,
  PRIMARY KEY (`favorite_members`,`favorite_goods`),
  KEY `ind_member_favorite_goods_f_g` (`favorite_goods`),
  CONSTRAINT `fk_member_favorite_goods_f_g` FOREIGN KEY (`favorite_goods`) REFERENCES `goods` (`id`),
  CONSTRAINT `fk_member_favorite_goods_f_m` FOREIGN KEY (`favorite_members`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `member_rank`
-- ----------------------------
DROP TABLE IF EXISTS `member_rank`;
CREATE TABLE `member_rank` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `amount` decimal(21,6) DEFAULT NULL COMMENT '消费金额',
  `is_default` bit(1) NOT NULL COMMENT '是否默认',
  `is_special` bit(1) NOT NULL COMMENT '是否特殊',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `scale` double NOT NULL COMMENT '优惠比例',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_member_rank_name` (`name`),
  KEY `ind_member_rank_amount` (`amount`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本',
  `content` longtext NOT NULL COMMENT '内容',
  `ip` varchar(255) NOT NULL DEFAULT '' COMMENT 'IP',
  `is_draft` bit(1) NOT NULL COMMENT '是否为草稿',
  `receiver_delete` bit(1) NOT NULL COMMENT '收件人删除',
  `receiver_read` bit(1) NOT NULL COMMENT '收件人已读',
  `sender_delete` bit(1) NOT NULL COMMENT '发件人删除',
  `sender_read` bit(1) NOT NULL COMMENT '发件人已读',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
  `for_message_id` bigint(20) DEFAULT NULL COMMENT '原消息',
  `receiver_id` bigint(20) DEFAULT NULL COMMENT '收件人',
  `sender_id` bigint(20) DEFAULT NULL COMMENT '发件人',
  PRIMARY KEY (`id`),
  KEY `ind_message_for_message` (`for_message_id`),
  KEY `ind_message_receiver` (`receiver_id`),
  KEY `ind_message_sender` (`sender_id`),
  CONSTRAINT `fk_message_for_message` FOREIGN KEY (`for_message_id`) REFERENCES `message` (`id`),
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `member` (`id`),
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `message_config`
-- ----------------------------
DROP TABLE IF EXISTS `message_config`;
CREATE TABLE `message_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `is_mail_enabled` bit(1) NOT NULL COMMENT '是否启用邮件',
  `is_sms_enabled` bit(1) NOT NULL COMMENT '是否启用短信',
  `type` int(11) NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_message_config_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `navigation`
-- ----------------------------
DROP TABLE IF EXISTS `navigation`;
CREATE TABLE `navigation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `is_blank_target` bit(1) NOT NULL COMMENT '是否新窗口打开',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `position` int(11) NOT NULL COMMENT '位置',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '链接地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `order`
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `delete_flag` bit(1) DEFAULT NULL COMMENT '删除标记',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `amount` decimal(21,6) NOT NULL COMMENT '订单金额',
  `amount_paid` decimal(21,6) NOT NULL COMMENT '已付金额',
  `area_name` varchar(255) DEFAULT NULL COMMENT '地区名称',
  `complete_date` datetime DEFAULT NULL COMMENT '完成日期',
  `consignee` varchar(255) DEFAULT NULL COMMENT '收货人',
  `coupon_discount` decimal(21,6) NOT NULL COMMENT '优惠券折扣',
  `exchange_point` bigint(20) NOT NULL COMMENT '兑换积分',
  `expire` datetime DEFAULT NULL COMMENT '过期时间',
  `fee` decimal(21,6) NOT NULL COMMENT '手续费',
  `freight` decimal(21,6) NOT NULL COMMENT '运费',
  `invoice_content` varchar(255) DEFAULT NULL COMMENT '发票内容',
  `invoice_title` varchar(255) DEFAULT NULL COMMENT '发票标题',
  `is_allocated_stock` bit(1) NOT NULL COMMENT '是否已分配库存',
  `is_exchange_point` bit(1) NOT NULL COMMENT '是否已兑换积分',
  `is_use_coupon_code` bit(1) NOT NULL COMMENT '是否已使用优惠码',
  `lock_expire` datetime DEFAULT NULL COMMENT '锁定过期日期',
  `lock_key` varchar(255) DEFAULT NULL COMMENT '锁定KEY',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `offset_amount` decimal(21,6) NOT NULL COMMENT '调整金额',
  `payment_method_name` varchar(255) DEFAULT NULL COMMENT '支付方式名称',
  `payment_method_type` int(11) DEFAULT NULL COMMENT '支付方式类型',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `price` decimal(21,6) NOT NULL COMMENT '商品价格',
  `promotion_discount` decimal(21,6) NOT NULL COMMENT '促销折扣',
  `promotion_names` longtext COMMENT '促销名称',
  `quantity` int(11) NOT NULL COMMENT '商品数量',
  `refund_amount` decimal(21,6) NOT NULL COMMENT '退款金额',
  `returned_quantity` int(11) NOT NULL COMMENT '退款商品数量',
  `reward_point` bigint(20) NOT NULL COMMENT '赠送积分',
  `shipped_quantity` int(11) NOT NULL COMMENT '已发货数量',
  `shipping_method_name` varchar(255) DEFAULT NULL COMMENT '配送方式名称',
  `shipping_date` datetime DEFAULT NULL COMMENT '配送时间',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `status` int(11) NOT NULL COMMENT '状态',
  `source` int(11) NOT NULL COMMENT '来源',
  `tax` decimal(21,6) NOT NULL COMMENT '税金',
  `type` int(11) NOT NULL COMMENT '类型',
  `weight` int(11) NOT NULL COMMENT '重量',
  `zip_code` varchar(255) DEFAULT NULL COMMENT '邮编',
  `area_id` bigint(20) DEFAULT NULL COMMENT '地区',
  `coupon_code_id` bigint(20) DEFAULT NULL COMMENT '优惠码',
  `member_id` bigint(20) NOT NULL COMMENT '会员',
  `payment_method_id` bigint(20) DEFAULT NULL COMMENT '支付方式',
  `shipping_method_id` bigint(20) DEFAULT NULL COMMENT '发货方式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_order_sn` (`sn`),
  KEY `ind_order_area` (`area_id`),
  KEY `ind_order_coupon_code` (`coupon_code_id`),
  KEY `ind_order_member` (`member_id`),
  KEY `ind_order_payment_method` (`payment_method_id`),
  KEY `ind_order_shipping_method` (`shipping_method_id`),
  CONSTRAINT `fk_order_area` FOREIGN KEY (`area_id`) REFERENCES `area` (`id`),
  CONSTRAINT `fk_order_coupon_code` FOREIGN KEY (`coupon_code_id`) REFERENCES `coupon_code` (`id`),
  CONSTRAINT `fk_order_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `fk_order_payment_method` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`),
  CONSTRAINT `fk_order_shipping_method` FOREIGN KEY (`shipping_method_id`) REFERENCES `shipping_method` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `order_coupon`
-- ----------------------------
DROP TABLE IF EXISTS `order_coupon`;
CREATE TABLE `order_coupon` (
  `orders` bigint(20) NOT NULL,
  `coupons` bigint(20) NOT NULL,
  KEY `ind_order_coupon_coupons` (`coupons`),
  KEY `ind_order_coupon_orders` (`orders`),
  CONSTRAINT `fk_order_coupon_coupons` FOREIGN KEY (`coupons`) REFERENCES `coupon` (`id`),
  CONSTRAINT `fk_order_coupon_orders` FOREIGN KEY (`orders`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `order_item`
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `is_delivery` bit(1) NOT NULL COMMENT '是否需要物流',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品名称',
  `price` decimal(21,6) NOT NULL COMMENT '商品价格',
  `quantity` int(11) NOT NULL COMMENT '商品数量',
  `returned_quantity` int(11) NOT NULL COMMENT '退货数量',
  `shipped_quantity` int(11) NOT NULL COMMENT '已发货数量',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `specifications` longtext COMMENT '规格',
  `thumbnail` varchar(255) DEFAULT NULL COMMENT '商品缩略图',
  `type` int(11) NOT NULL COMMENT '商品类型',
  `weight` int(11) DEFAULT NULL COMMENT '商品重量',
  `is_review` bit(1) DEFAULT b'0' COMMENT '是否评论',
  `order_id` bigint(20) NOT NULL COMMENT '订单',
  `product_id` bigint(20) DEFAULT NULL COMMENT '商品',
  PRIMARY KEY (`id`),
  KEY `ind_order_item_orders` (`order_id`),
  KEY `ind_order_item_product` (`product_id`),
  CONSTRAINT `fk_order_item_orders` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `order_log`
-- ----------------------------
DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作员',
  `type` int(11) NOT NULL COMMENT '类型',
  `order_id` bigint(20) NOT NULL COMMENT '订单',
  PRIMARY KEY (`id`),
  KEY `ind_order_log_orders` (`order_id`),
  CONSTRAINT `fk_order_log_orders` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=144 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `parameter`
-- ----------------------------
DROP TABLE IF EXISTS `parameter`;
CREATE TABLE `parameter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `parameter_group` varchar(255) NOT NULL DEFAULT '' COMMENT '参数组',
  `names` longtext NOT NULL COMMENT '参数名称',
  `product_category_id` bigint(20) NOT NULL COMMENT '绑定分类',
  PRIMARY KEY (`id`),
  KEY `ind_parameter_product_category` (`product_category_id`),
  CONSTRAINT `fk_parameter_product_category` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `payment`
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `account` varchar(255) DEFAULT NULL COMMENT '收款账号',
  `amount` decimal(21,6) NOT NULL COMMENT '付款金额',
  `bank` varchar(255) DEFAULT NULL COMMENT '收款银行',
  `fee` decimal(21,6) NOT NULL COMMENT '支付手续费',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `method` int(11) NOT NULL COMMENT '方式',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作员',
  `payer` varchar(255) DEFAULT NULL COMMENT '付款人',
  `payment_method` varchar(255) DEFAULT NULL COMMENT '付款方式',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_payment_sn` (`sn`),
  KEY `ind_payment_orders` (`order_id`),
  CONSTRAINT `fk_payment_orders` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `payment_log`
-- ----------------------------
DROP TABLE IF EXISTS `payment_log`;
CREATE TABLE `payment_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `amount` decimal(21,6) NOT NULL COMMENT '支付金额',
  `fee` decimal(21,6) NOT NULL COMMENT '支付手续费',
  `payment_plugin_id` varchar(255) DEFAULT NULL COMMENT '支付插件ID',
  `payment_plugin_name` varchar(255) DEFAULT NULL COMMENT '支付插件名称',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `status` int(11) NOT NULL COMMENT '状态',
  `type` int(11) NOT NULL COMMENT '类型',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_payment_log_sn` (`sn`),
  KEY `ind_payment_log_member` (`member_id`),
  KEY `ind_payment_log_orders` (`order_id`),
  CONSTRAINT `fk_payment_log_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `fk_payment_log_orders` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `payment_method`
-- ----------------------------
DROP TABLE IF EXISTS `payment_method`;
CREATE TABLE `payment_method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `content` longtext COMMENT '内容',
  `description` varchar(255) DEFAULT NULL COMMENT '介绍',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标icon',
  `method` int(11) NOT NULL COMMENT '方式',
  `is_default` bit(1) DEFAULT NULL COMMENT '是否默认',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `timeout` int(11) DEFAULT NULL COMMENT '超时时间',
  `type` int(11) NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `permission`
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `is_enabled` bit(1) NOT NULL COMMENT '是否启用',
  `module` varchar(255) DEFAULT NULL COMMENT '模块',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '链接地址',
  `value` varchar(255) NOT NULL DEFAULT '' COMMENT '权限值',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `value` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8 COMMENT='权限';

-- ----------------------------
--  Table structure for `permission_role`
-- ----------------------------
DROP TABLE IF EXISTS `permission_role`;
CREATE TABLE `permission_role` (
  `permissions` bigint(20) NOT NULL COMMENT '权限',
  `roles` bigint(20) NOT NULL COMMENT '角色',
  PRIMARY KEY (`permissions`,`roles`),
  KEY `FK_permission_role_role` (`roles`),
  KEY `permissions` (`permissions`),
  CONSTRAINT `FK_permission_role_permission` FOREIGN KEY (`permissions`) REFERENCES `permission` (`id`),
  CONSTRAINT `FK_permission_role_role` FOREIGN KEY (`roles`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `plugin_config`
-- ----------------------------
DROP TABLE IF EXISTS `plugin_config`;
CREATE TABLE `plugin_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `attributes` longtext COMMENT '属性',
  `is_enabled` bit(1) NOT NULL COMMENT '是否启用',
  `plugin_id` varchar(255) NOT NULL DEFAULT '' COMMENT '插件ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_plugin_config_plugin_id` (`plugin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `point_log`
-- ----------------------------
DROP TABLE IF EXISTS `point_log`;
CREATE TABLE `point_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `balance` bigint(20) NOT NULL COMMENT '当前积分',
  `credit` bigint(20) NOT NULL COMMENT '获取积分',
  `debit` bigint(20) NOT NULL COMMENT '扣除积分',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作员',
  `type` int(11) NOT NULL COMMENT '类型',
  `member_id` bigint(20) NOT NULL COMMENT '会员',
  PRIMARY KEY (`id`),
  KEY `ind_point_log_member` (`member_id`),
  CONSTRAINT `fk_point_log_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `allocated_stock` int(11) NOT NULL COMMENT '已分配库存',
  `cost` decimal(21,6) DEFAULT NULL COMMENT '成本价',
  `exchange_point` bigint(20) NOT NULL COMMENT '兑换积分',
  `is_default` bit(1) NOT NULL COMMENT '是否默认',
  `market_price` decimal(21,6) NOT NULL COMMENT '市场价',
  `price` decimal(21,6) NOT NULL COMMENT '销售价',
  `reward_point` bigint(20) NOT NULL COMMENT '赠送积分',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `specification_values` longtext COMMENT '规格值',
  `stock` int(11) NOT NULL COMMENT '库存',
  `goods_id` bigint(20) NOT NULL COMMENT '货品',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_product_sn` (`sn`),
  KEY `ind_product_goods` (`goods_id`),
  CONSTRAINT `fk_product_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `product_category`
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `grade` int(11) NOT NULL COMMENT '层级',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `is_marketable` bit(1) DEFAULT NULL COMMENT '是否上架',
  `is_top` bit(1) DEFAULT NULL COMMENT '是否置顶',
  `is_cash` bit(1) DEFAULT NULL COMMENT '是否货到付款',
  `image` varchar(255) DEFAULT NULL COMMENT '展示图片',
  `seo_description` varchar(255) DEFAULT NULL COMMENT '页面描述',
  `seo_keywords` varchar(255) DEFAULT NULL COMMENT '页面关键词',
  `seo_title` varchar(255) DEFAULT NULL COMMENT '页面标题',
  `tree_path` varchar(255) NOT NULL DEFAULT '' COMMENT '树路径',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级分类',
  PRIMARY KEY (`id`),
  KEY `ind_product_category_parent` (`parent_id`),
  CONSTRAINT `fk_product_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `product_category_brand`
-- ----------------------------
DROP TABLE IF EXISTS `product_category_brand`;
CREATE TABLE `product_category_brand` (
  `product_categories` bigint(20) NOT NULL,
  `brands` bigint(20) NOT NULL,
  PRIMARY KEY (`product_categories`,`brands`),
  KEY `ind_product_category_brand_b` (`brands`),
  CONSTRAINT `fk_product_category_brand_b` FOREIGN KEY (`brands`) REFERENCES `brand` (`id`),
  CONSTRAINT `fk_product_category_brand_p_c` FOREIGN KEY (`product_categories`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `product_category_promotion`
-- ----------------------------
DROP TABLE IF EXISTS `product_category_promotion`;
CREATE TABLE `product_category_promotion` (
  `product_categories` bigint(20) NOT NULL,
  `promotions` bigint(20) NOT NULL,
  PRIMARY KEY (`product_categories`,`promotions`),
  KEY `ind_product_category_p_p` (`promotions`),
  CONSTRAINT `fk_product_category_p_p` FOREIGN KEY (`promotions`) REFERENCES `promotion` (`id`),
  CONSTRAINT `fk_product_category_p_p_c` FOREIGN KEY (`product_categories`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `product_notify`
-- ----------------------------
DROP TABLE IF EXISTS `product_notify`;
CREATE TABLE `product_notify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `email` varchar(255) NOT NULL DEFAULT '' COMMENT 'Email',
  `has_sent` bit(1) NOT NULL COMMENT '是否已发送',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员',
  `product_id` bigint(20) NOT NULL COMMENT '商品',
  PRIMARY KEY (`id`),
  KEY `ind_product_notify_member` (`member_id`),
  KEY `ind_product_notify_product` (`product_id`),
  CONSTRAINT `fk_product_notify_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `fk_product_notify_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `project`
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '排序',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父id',
  PRIMARY KEY (`id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `fk_project_parent` FOREIGN KEY (`parent_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='楼盘';

-- ----------------------------
--  Table structure for `promotion`
-- ----------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `begin_date` datetime DEFAULT NULL COMMENT '起始日期',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `introduction` longtext COMMENT '介绍',
  `is_coupon_allowed` bit(1) NOT NULL COMMENT '是否允许使用优惠券',
  `is_free_shipping` bit(1) NOT NULL COMMENT '是否免运费',
  `maximum_price` decimal(21,6) DEFAULT NULL COMMENT '最大商品价格',
  `maximum_quantity` int(11) DEFAULT NULL COMMENT '最大商品数量',
  `minimum_price` decimal(21,6) DEFAULT NULL COMMENT '最小商品价格',
  `minimum_quantity` int(11) DEFAULT NULL COMMENT '最小商品数量',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `point_expression` varchar(255) DEFAULT NULL COMMENT '积分运算表达式',
  `price_expression` varchar(255) DEFAULT NULL COMMENT '价格运算表达式',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `promotion_coupon`
-- ----------------------------
DROP TABLE IF EXISTS `promotion_coupon`;
CREATE TABLE `promotion_coupon` (
  `promotions` bigint(20) NOT NULL,
  `coupons` bigint(20) NOT NULL,
  PRIMARY KEY (`promotions`,`coupons`),
  KEY `ind_promotion_coupon_coupons` (`coupons`),
  CONSTRAINT `fk_promotion_coupon_coupons` FOREIGN KEY (`coupons`) REFERENCES `coupon` (`id`),
  CONSTRAINT `fk_promotion_coupon_promotions` FOREIGN KEY (`promotions`) REFERENCES `promotion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `promotion_gift`
-- ----------------------------
DROP TABLE IF EXISTS `promotion_gift`;
CREATE TABLE `promotion_gift` (
  `gift_promotions` bigint(20) NOT NULL,
  `gifts` bigint(20) NOT NULL,
  PRIMARY KEY (`gift_promotions`,`gifts`),
  KEY `ind_promotion_gift_gifts` (`gifts`),
  CONSTRAINT `fk_promotion_gift_gift_p` FOREIGN KEY (`gift_promotions`) REFERENCES `promotion` (`id`),
  CONSTRAINT `fk_promotion_gift_gifts` FOREIGN KEY (`gifts`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `promotion_member_rank`
-- ----------------------------
DROP TABLE IF EXISTS `promotion_member_rank`;
CREATE TABLE `promotion_member_rank` (
  `promotions` bigint(20) NOT NULL,
  `member_ranks` bigint(20) NOT NULL,
  PRIMARY KEY (`promotions`,`member_ranks`),
  KEY `ind_promotion_member_rank_m_r` (`member_ranks`),
  CONSTRAINT `fk_promotion_member_rank_m_r` FOREIGN KEY (`member_ranks`) REFERENCES `member_rank` (`id`),
  CONSTRAINT `fk_promotion_member_rank_p` FOREIGN KEY (`promotions`) REFERENCES `promotion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `receiver`
-- ----------------------------
DROP TABLE IF EXISTS `receiver`;
CREATE TABLE `receiver` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '地址',
  `area_name` varchar(255) NOT NULL DEFAULT '' COMMENT '地区名称',
  `consignee` varchar(255) NOT NULL DEFAULT '' COMMENT '收货人',
  `is_default` bit(1) NOT NULL COMMENT '是否默认',
  `phone` varchar(255) NOT NULL DEFAULT '' COMMENT '电话',
  `zip_code` varchar(255) NOT NULL DEFAULT '' COMMENT '邮编',
  `area_id` bigint(20) NOT NULL COMMENT '地区',
  `member_id` bigint(20) NOT NULL COMMENT '会员',
  PRIMARY KEY (`id`),
  KEY `ind_receiver_area` (`area_id`),
  KEY `ind_receiver_member` (`member_id`),
  CONSTRAINT `fk_receiver_area` FOREIGN KEY (`area_id`) REFERENCES `area` (`id`),
  CONSTRAINT `fk_receiver_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `refunds`
-- ----------------------------
DROP TABLE IF EXISTS `refunds`;
CREATE TABLE `refunds` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `account` varchar(255) DEFAULT NULL COMMENT '退款账号',
  `amount` decimal(21,6) NOT NULL COMMENT '退款金额',
  `bank` varchar(255) DEFAULT NULL COMMENT '退款银行',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `method` int(11) NOT NULL COMMENT '退款方式',
  `operator` varchar(255) NOT NULL DEFAULT '' COMMENT '操作员',
  `payee` varchar(255) DEFAULT NULL COMMENT '收款人',
  `payment_method` varchar(255) DEFAULT NULL COMMENT '支付方式',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单',
  `member_id` bigint(11) DEFAULT NULL COMMENT '会员id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_refunds_sn` (`sn`),
  KEY `ind_refunds_orders` (`order_id`),
  CONSTRAINT `fk_refunds_orders` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `returns`
-- ----------------------------
DROP TABLE IF EXISTS `returns`;
CREATE TABLE `returns` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `area` varchar(255) DEFAULT NULL COMMENT '地区',
  `delivery_corp` varchar(255) DEFAULT NULL COMMENT '物流公司',
  `freight` decimal(21,6) DEFAULT NULL COMMENT '物流费用',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) NOT NULL DEFAULT '' COMMENT '操作员',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `shipper` varchar(255) DEFAULT NULL COMMENT '发货人',
  `shipping_method` varchar(255) DEFAULT NULL COMMENT '配送方式',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `tracking_no` varchar(255) DEFAULT NULL COMMENT '运单号',
  `zip_code` varchar(255) DEFAULT NULL COMMENT '邮编',
  `order_id` bigint(20) NOT NULL COMMENT '订单',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_returns_sn` (`sn`),
  KEY `ind_returns_orders` (`order_id`),
  CONSTRAINT `fk_returns_orders` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `returns_item`
-- ----------------------------
DROP TABLE IF EXISTS `returns_item`;
CREATE TABLE `returns_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `images` text COMMENT '售后传图',
  `desc` longtext COMMENT '会员退货描述',
  `cause` varchar(200) DEFAULT '' COMMENT '原因',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `type` int(10) NOT NULL DEFAULT '1' COMMENT '退款类型(1：退货并退款，2：仅退款)',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `specifications` longtext COMMENT '规格',
  `member_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '会员ID',
  `product_id` bigint(20) DEFAULT NULL COMMENT '商品',
  `return_id` bigint(20) NOT NULL COMMENT '退货单',
  PRIMARY KEY (`id`),
  KEY `ind_returns_item_returns` (`return_id`),
  CONSTRAINT `fk_returns_item_returns` FOREIGN KEY (`return_id`) REFERENCES `returns` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `review`
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '内容',
  `ip` varchar(255) NOT NULL DEFAULT '' COMMENT 'IP',
  `is_show` bit(1) NOT NULL COMMENT '是否显示',
  `score` int(11) DEFAULT NULL COMMENT '评分',
  `images` longtext COMMENT '评价图片',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '货品',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单行id',
  `for_review_id` bigint(20) DEFAULT NULL COMMENT '评价id',
  `product_id` bigint(20) NOT NULL COMMENT '产品',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员',
  PRIMARY KEY (`id`),
  KEY `ind_review_goods` (`product_id`),
  KEY `ind_review_member` (`member_id`),
  KEY `fk_review_r` (`for_review_id`),
  KEY `order_item_id` (`order_item_id`),
  KEY `goods` (`goods_id`),
  CONSTRAINT `fk_review_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`),
  CONSTRAINT `fk_review_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `fk_review_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`),
  CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_review_r` FOREIGN KEY (`for_review_id`) REFERENCES `review` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `is_enabled` bit(1) NOT NULL COMMENT '是否启用',
  `is_system` bit(1) NOT NULL COMMENT '是否内置',
  `value` varchar(255) NOT NULL DEFAULT '' COMMENT '角色值',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `value` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `seo`
-- ----------------------------
DROP TABLE IF EXISTS `seo`;
CREATE TABLE `seo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `type` int(11) NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_seo_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shipping`
-- ----------------------------
DROP TABLE IF EXISTS `shipping`;
CREATE TABLE `shipping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `area` varchar(255) DEFAULT NULL COMMENT '地区',
  `consignee` varchar(255) DEFAULT NULL COMMENT '收货人',
  `delivery_corp` varchar(255) DEFAULT NULL COMMENT '物流公司',
  `delivery_corp_code` varchar(255) DEFAULT NULL COMMENT '物流公司代码',
  `delivery_corp_url` varchar(255) DEFAULT NULL COMMENT '物流公司网址',
  `freight` decimal(21,6) DEFAULT NULL COMMENT '物流费用',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) NOT NULL DEFAULT '' COMMENT '操作员',
  `phone` varchar(255) DEFAULT NULL COMMENT '电话',
  `shipping_method` varchar(255) DEFAULT NULL COMMENT '配送方式',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `tracking_no` varchar(255) DEFAULT NULL COMMENT '运单号',
  `zip_code` varchar(255) DEFAULT NULL COMMENT '邮编',
  `order_id` bigint(20) NOT NULL COMMENT '订单',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_shipping_sn` (`sn`),
  KEY `ind_shipping_orders` (`order_id`),
  CONSTRAINT `fk_shipping_orders` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shipping_item`
-- ----------------------------
DROP TABLE IF EXISTS `shipping_item`;
CREATE TABLE `shipping_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `is_delivery` bit(1) NOT NULL COMMENT '是否需要物流',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `sn` varchar(255) NOT NULL DEFAULT '' COMMENT '编号',
  `specifications` longtext COMMENT '规格',
  `product_id` bigint(20) DEFAULT NULL COMMENT '商品',
  `shipping_id` bigint(20) NOT NULL COMMENT '发货单',
  PRIMARY KEY (`id`),
  KEY `ind_shipping_item_product` (`product_id`),
  KEY `ind_shipping_item_shipping` (`shipping_id`),
  CONSTRAINT `fk_shipping_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_shipping_item_shipping` FOREIGN KEY (`shipping_id`) REFERENCES `shipping` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shipping_method`
-- ----------------------------
DROP TABLE IF EXISTS `shipping_method`;
CREATE TABLE `shipping_method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `continue_weight` int(11) NOT NULL COMMENT '续重量',
  `default_continue_price` decimal(21,6) NOT NULL COMMENT '默认续重价格',
  `default_first_price` decimal(21,6) NOT NULL COMMENT '默认首重价格',
  `description` varchar(255) DEFAULT NULL COMMENT '介绍',
  `first_weight` int(11) NOT NULL COMMENT '首重量',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标icon',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `default_delivery_corp_id` bigint(20) DEFAULT NULL COMMENT '默认物流公司',
  PRIMARY KEY (`id`),
  KEY `ind_shipping_method_d_d_c` (`default_delivery_corp_id`),
  CONSTRAINT `fk_shipping_method_default_d_c` FOREIGN KEY (`default_delivery_corp_id`) REFERENCES `delivery_corp` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shipping_payment_method`
-- ----------------------------
DROP TABLE IF EXISTS `shipping_payment_method`;
CREATE TABLE `shipping_payment_method` (
  `shipping_methods` bigint(20) NOT NULL,
  `payment_methods` bigint(20) NOT NULL,
  PRIMARY KEY (`shipping_methods`,`payment_methods`),
  KEY `ind_shipping_payment_m_p_m` (`payment_methods`),
  CONSTRAINT `fk_shipping_payment_method_p_m` FOREIGN KEY (`payment_methods`) REFERENCES `payment_method` (`id`),
  CONSTRAINT `fk_shipping_payment_method_s_m` FOREIGN KEY (`shipping_methods`) REFERENCES `shipping_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `sms`
-- ----------------------------
DROP TABLE IF EXISTS `sms`;
CREATE TABLE `sms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `mobile` varchar(50) NOT NULL DEFAULT '' COMMENT '手机',
  `sms_code` varchar(50) NOT NULL DEFAULT '' COMMENT '短信值',
  `sms_type` int(11) NOT NULL COMMENT '短信类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `sn`
-- ----------------------------
DROP TABLE IF EXISTS `sn`;
CREATE TABLE `sn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `last_value` bigint(20) NOT NULL COMMENT '末值',
  `type` int(11) NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_sn_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `specification`
-- ----------------------------
DROP TABLE IF EXISTS `specification`;
CREATE TABLE `specification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `options` longtext NOT NULL COMMENT '可选项',
  `product_category_id` bigint(20) NOT NULL COMMENT '绑定分类',
  PRIMARY KEY (`id`),
  KEY `ind_specification_product_c` (`product_category_id`),
  CONSTRAINT `fk_specification_product_c` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `statistic`
-- ----------------------------
DROP TABLE IF EXISTS `statistic`;
CREATE TABLE `statistic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `complete_order_amount` decimal(21,6) NOT NULL COMMENT '订单完成金额',
  `complete_order_count` bigint(20) NOT NULL COMMENT '订单完成数',
  `create_order_amount` decimal(21,6) NOT NULL COMMENT '订单创建金额',
  `create_order_count` bigint(20) NOT NULL COMMENT '订单创建数',
  `day` int(11) NOT NULL COMMENT '日',
  `month` int(11) NOT NULL COMMENT '月',
  `register_member_count` bigint(20) NOT NULL COMMENT '会员注册数',
  `year` int(11) NOT NULL COMMENT '年',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `stock_log`
-- ----------------------------
DROP TABLE IF EXISTS `stock_log`;
CREATE TABLE `stock_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `in_quantity` int(11) NOT NULL COMMENT '入库数量',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator` varchar(255) NOT NULL DEFAULT '' COMMENT '操作员',
  `out_quantity` int(11) NOT NULL COMMENT '出库数量',
  `stock` int(11) NOT NULL COMMENT '当前库存',
  `type` int(11) NOT NULL COMMENT '类型',
  `product_id` bigint(20) NOT NULL COMMENT '商品',
  PRIMARY KEY (`id`),
  KEY `ind_stock_log_product` (`product_id`),
  CONSTRAINT `fk_stock_log_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tag`
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `modify_date` datetime NOT NULL COMMENT '修改日期',
  `version` bigint(20) NOT NULL COMMENT '版本号',
  `orders` int(11) DEFAULT NULL COMMENT '订单排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标icon',
  `memo` varchar(255) DEFAULT NULL COMMENT '备注',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `type` int(11) NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
