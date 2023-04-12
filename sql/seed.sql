/*
 Navicat Premium Data Transfer

 Source Server         : 191.168.10.91
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : 191.168.10.91:3306
 Source Schema         : seed

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 12/04/2023 16:33:20
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT,
    `type`        varchar(64) NOT NULL COMMENT '字典类型',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `is_system`   tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否系统内置，1：是；0：否，默认',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    `update_time` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP (3) COMMENT '修改时间',
    `is_deleted`  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，1：已删除、0：未删除，默认',
    PRIMARY KEY (`id`),
    UNIQUE KEY `sys_dict_type_idx` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT,
    `dict_id`     bigint unsigned NOT NULL COMMENT '所属字典id，关联sys_dict.id',
    `label`       varchar(32)  DEFAULT NULL COMMENT '名称',
    `value`       varchar(255) NOT NULL COMMENT '值',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `sorts`       int          DEFAULT NULL COMMENT '排序，升序',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    `update_time` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP (3) COMMENT '修改时间',
    `is_deleted`  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，1：已删除、0：未删除，默认',
    PRIMARY KEY (`id`),
    KEY           `sys_dict_item_dict_id_idx` (`dict_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT,
    `code`        varchar(32)                                                  NOT NULL COMMENT '角色标识',
    `name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `is_deleted`  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，0：未删除，默认；1：已删除',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    `update_time` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP (3) COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `sys_role_code_idex` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `code`, `name`, `description`, `is_deleted`, `create_time`, `update_time`)
VALUES (1, 'ROLE_ADMIN', '管理员', '', 0, '2023-04-07 15:12:32.000', NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT,
    `username`    varchar(20) NOT NULL COMMENT '账号',
    `password`    varchar(32)  DEFAULT NULL COMMENT '密码',
    `phone`       varchar(16)  DEFAULT NULL COMMENT '手机号',
    `sex`         char(1)      DEFAULT NULL COMMENT '性别，F：女，M：男，null：未知',
    `birth_date`  date         DEFAULT NULL COMMENT '生日',
    `email`       varchar(64)  DEFAULT NULL COMMENT '邮箱',
    `avatar_url`  varchar(255) DEFAULT NULL COMMENT '头像url地址',
    `full_name`   varchar(100) DEFAULT NULL COMMENT '姓名',
    `id_card_no`  varchar(20)  DEFAULT NULL COMMENT '身份证号',
    `dept_id`     bigint unsigned DEFAULT NULL COMMENT '部门id',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    `update_time` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP (3) COMMENT '修改时间',
    `is_deleted`  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，0：否，默认；1：已删除',
    `status`      tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态，0：正常；其它异常',
    PRIMARY KEY (`id`),
    UNIQUE KEY `sys_user_username_idx` (`username`),
    KEY           `sys_user_phone_idx` (`phone`) USING BTREE,
    KEY           `sys_user_email_idx` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `password`, `phone`, `sex`, `birth_date`, `email`, `avatar_url`, `full_name`,
                        `id_card_no`, `dept_id`, `create_time`, `update_time`, `is_deleted`, `status`)
VALUES (1, 'admin', '123456789', '18900000000', 'F', '2000-01-01', 'admin@seed.com', NULL, '管理员',
        '123456789012345678', NULL, '2023-01-01 00:00:00.000', '2023-04-04 14:41:21.123', 0, 0);
INSERT INTO `sys_user` (`id`, `username`, `password`, `phone`, `sex`, `birth_date`, `email`, `avatar_url`, `full_name`,
                        `id_card_no`, `dept_id`, `create_time`, `update_time`, `is_deleted`, `status`)
VALUES (2, 'test', '123456', '13900000000', 'M', '2000-01-01', 'test@seed.com', NULL, '测试员', NULL, NULL,
        '2023-04-12 10:17:18.000', '2023-04-12 16:24:35.831', 1, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT,
    `user_id`     bigint unsigned NOT NULL COMMENT '用户id',
    `role_id`     bigint unsigned NOT NULL COMMENT '角色id',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '常见时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `sys_user_role_idx` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_time`)
VALUES (1, 1, 1, '2023-04-12 16:31:55.492');
COMMIT;

SET
FOREIGN_KEY_CHECKS = 1;
