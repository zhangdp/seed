/*
 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 09/07/2024 15:47:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(64) NOT NULL COMMENT '字典类型',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `is_system` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否系统内置，1：是；0：否，默认',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，1：已删除、0：未删除，默认',
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
CREATE TABLE `sys_dict_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `dict_id` bigint unsigned NOT NULL COMMENT '所属字典id，关联sys_dict.id',
  `label` varchar(32) DEFAULT NULL COMMENT '名称',
  `value` varchar(255) NOT NULL COMMENT '值',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sorts` int DEFAULT NULL COMMENT '排序，升序',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，1：已删除、0：未删除，默认',
  PRIMARY KEY (`id`),
  KEY `sys_dict_item_dict_id_idx` (`dict_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_properties
-- ----------------------------
DROP TABLE IF EXISTS `sys_properties`;
CREATE TABLE `sys_properties` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL COMMENT '配置标识、key',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `text_value` varchar(255) NOT NULL COMMENT '值',
  `is_encrypted` tinyint unsigned DEFAULT '0' COMMENT '值是否加密',
  `is_system` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否系统内置',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '添加时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '上次修改时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，0：否，默认；1：是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置';

-- ----------------------------
-- Records of sys_properties
-- ----------------------------
BEGIN;
INSERT INTO `sys_properties` (`id`, `code`, `description`, `text_value`, `is_encrypted`, `is_system`, `created_date`, `last_modified_date`, `is_deleted`) VALUES (1, 'ACCESS_TOKEN_TTL', '访问令牌有效期，单位：秒', '1800', 0, 1, '2024-07-09 15:26:11.566', NULL, 0);
INSERT INTO `sys_properties` (`id`, `code`, `description`, `text_value`, `is_encrypted`, `is_system`, `created_date`, `last_modified_date`, `is_deleted`) VALUES (2, 'REFRESH_TOKEN_TTL', '刷新令牌有效期，单位：秒', '86400', 0, 1, '2024-07-09 15:26:47.675', NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_resource`;
CREATE TABLE `sys_resource` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint unsigned NOT NULL COMMENT '资源类型 （1菜单 2按钮）',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `permission` varchar(32) NOT NULL COMMENT '权限标识',
  `parent_id` bigint unsigned NOT NULL COMMENT '父节点id，根节点为0',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  `path` varchar(255) NOT NULL COMMENT '路由路径',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sorts` int unsigned NOT NULL DEFAULT '0' COMMENT '排序',
  `is_keep_alive` tinyint NOT NULL DEFAULT '0' COMMENT '前端是否开启路由缓冲，1：是；0：否',
  `is_visible` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否展示，1：是，默认；0：否',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，1：是；0：否，默认',
  PRIMARY KEY (`id`),
  KEY `sys_resource_permission_idx` (`permission`),
  KEY `sys_resource_parent_id_idx` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资源';

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL COMMENT '角色标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，0：未删除，默认；1：已删除',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_role_code_idex` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `code`, `name`, `description`, `is_deleted`, `created_date`, `last_modified_date`) VALUES (1, 'ROLE_ADMIN', '管理员', '', 0, '2023-04-07 15:12:32.000', NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_resource`;
CREATE TABLE `sys_role_resource` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `role_id` bigint unsigned NOT NULL COMMENT '角色id，关联sys_role.id',
  `resource_id` bigint unsigned NOT NULL COMMENT '资源id，关联sys_resource.id',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_role_resource_uidx` (`role_id`,`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色关联资源';

-- ----------------------------
-- Records of sys_role_resource
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_resource` (`id`, `role_id`, `resource_id`, `created_date`, `last_modified_date`) VALUES (1, 1, 1, '2023-04-12 18:14:20.054', NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL COMMENT '账号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '密码',
  `mobile` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '性别，F：女，M：男，null：未知',
  `birth_date` date DEFAULT NULL COMMENT '生日',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像url地址',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '姓名',
  `citizen_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号',
  `dept_id` bigint unsigned DEFAULT NULL COMMENT '部门id',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除，0：否，默认；1：已删除',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态，0：正常；其它异常',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_user_username_idx` (`username`),
  KEY `sys_user_mobile_idx` (`mobile`) USING BTREE,
  KEY `sys_user_email_idx` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `password`, `mobile`, `gender`, `birth_date`, `email`, `avatar_url`, `name`, `citizen_id`, `dept_id`, `created_date`, `last_modified_date`, `is_deleted`, `status`) VALUES (1, 'admin', '$2a$10$wDjUgfO37shayK2QZoodL.EKj7mSyB/Mzn3G1tDif3C0kfr5bIQcC', '18900000000', 'F', '2000-01-01', 'admin@seed.com', NULL, '管理员', '123456789012345678', NULL, '2023-01-01 00:00:00.000', '2024-06-27 10:29:27.447', 0, 0);
INSERT INTO `sys_user` (`id`, `username`, `password`, `mobile`, `gender`, `birth_date`, `email`, `avatar_url`, `name`, `citizen_id`, `dept_id`, `created_date`, `last_modified_date`, `is_deleted`, `status`) VALUES (2, 'test', '$2a$10$wDjUgfO37shayK2QZoodL.EKj7mSyB/Mzn3G1tDif3C0kfr5bIQcC', '13900000000', 'M', '2000-01-01', 'test@seed.com', NULL, '测试员', NULL, NULL, '2023-04-12 10:17:18.000', '2024-06-27 14:55:20.115', 0, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL COMMENT '用户id',
  `role_id` bigint unsigned NOT NULL COMMENT '角色id',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '常见时间',
  `last_modified_date` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_user_role_idx` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `created_date`, `last_modified_date`) VALUES (1, 1, 1, '2023-04-12 16:31:55.492', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
