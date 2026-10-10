-- =============================================================================
-- seed 脚手架测试数据
-- =============================================================================
-- 依据：src/main/java/io/github/seed/entity 下的实体类
-- 前置：必须先执行 seed-schema.sql
--
-- 【账号】所有测试用户的密码都是 123456
--         admin  / 密码 123456  → 角色 ROLE_ADMIN
--         test   / 密码 123456  → 角色 ROLE_USER
--         locked / 密码 123456  → 状态为锁定（status=1），用于验证登录被拒的分支
-- 【手机号】admin: 18900000000   test: 13900000000   locked: 13700000000
--
-- 说明：本脚本按固定 id 插入，末尾会统一把identity序列重置到 MAX(id)+1，
--       避免后续应用自行插入时主键冲突
-- =============================================================================

SET client_encoding = 'UTF8';

-- -----------------------------------------------------------------------------
-- 部门
-- -----------------------------------------------------------------------------
INSERT INTO sys_dept (id, name, parent_id, sorts, deleted, created_at, updated_at) VALUES
    (1, '总部',   0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, '技术部', 1, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (3, '市场部', 1, 2, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 角色
-- -----------------------------------------------------------------------------
INSERT INTO sys_role (id, code, name, description, deleted, created_at, updated_at) VALUES
    (1, 'ROLE_ADMIN', '管理员',   '拥有全部权限',   0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, 'ROLE_USER',  '普通用户', '仅拥有日志查看', 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 用户（密码均为 123456 的 BCrypt 哈希）
-- -----------------------------------------------------------------------------
INSERT INTO sys_user (id, username, password, mobile, gender, birth_date, email, avatar, name, citizen_id, dept_id, status, deleted, created_at, updated_at) VALUES
    (1, 'admin',  '$2a$10$wDjUgfO37shayK2QZoodL.EKj7mSyB/Mzn3G1tDif3C0kfr5bIQcC', '18900000000', 'F', '2000-01-01', 'admin@seed.com',  NULL, '管理员',   '123456789012345678', 1, 0, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, 'test',   '$2a$10$wDjUgfO37shayK2QZoodL.EKj7mSyB/Mzn3G1tDif3C0kfr5bIQcC', '13900000000', 'M', '2000-01-01', 'test@seed.com',   NULL, '测试员',   NULL,                 2, 0, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (3, 'locked', '$2a$10$wDjUgfO37shayK2QZoodL.EKj7mSyB/Mzn3G1tDif3C0kfr5bIQcC', '13700000000', 'M', '1999-05-20', 'locked@seed.com', NULL, '已锁定账号', NULL,                 3, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 用户-角色
-- -----------------------------------------------------------------------------
INSERT INTO sys_user_role (id, user_id, role_id, created_at, updated_at) VALUES
    (1, 1, 1, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, 2, 2, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (3, 3, 2, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 权限（菜单树 + 按钮）
-- parent_id=0 为根节点；type：menu 菜单 / button 按钮
-- -----------------------------------------------------------------------------
INSERT INTO sys_permission (id, type, name, code, parent_id, icon, path, component, description, sorts, keep_alive, visible, deleted, created_at, updated_at) VALUES
    -- 一级菜单
    (1,  'menu', '系统管理', 'sys:system', 0, 'Setting',  '/system', NULL, '系统管理', 1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (30, 'menu', '日志管理', 'sys:log',    0, 'Document', '/log',    NULL, '日志管理', 2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 系统管理下的二级菜单
    (2,  'menu', '用户管理', 'sys:user',       1, 'User',           '/system/user',       'system/user/index',       '用户管理', 1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (3,  'menu', '角色管理', 'sys:role',       1, 'UserFilled',     '/system/role',       'system/role/index',       '角色管理', 2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (4,  'menu', '权限管理', 'sys:permission', 1, 'Menu',           '/system/permission', 'system/permission/index', '权限管理', 3, 1, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (5,  'menu', '部门管理', 'sys:dept',       1, 'OfficeBuilding', '/system/dept',       'system/dept/index',       '部门管理', 4, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (6,  'menu', '字典管理', 'sys:dict',       1, 'Collection',     '/system/dict',       'system/dict/index',       '字典管理', 5, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (7,  'menu', '参数管理', 'sys:config',     1, 'Tools',          '/system/config',     'system/config/index',     '参数管理', 6, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (8,  'menu', '文件管理', 'sys:file',       1, 'Folder',         '/system/file',       'system/file/index',       '文件管理', 7, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 日志管理下的二级菜单
    (31, 'menu', '登录日志', 'sys:log:login',     30, 'Key',     '/log/login',     'log/login/index',     '登录日志', 1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (32, 'menu', '操作日志', 'sys:log:operation', 30, 'Tickets', '/log/operation', 'log/operation/index', '操作日志', 2, 1, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 用户管理下的按钮（code 需与后端 @PreAuthorize 的标识一致）
    (21, 'button', '用户新增', 'sys:user:add',           2, NULL, NULL, NULL, '用户新增',     1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (22, 'button', '用户修改', 'sys:user:update',        2, NULL, NULL, NULL, '用户修改',     2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (23, 'button', '用户删除', 'sys:user:delete',        2, NULL, NULL, NULL, '用户删除',     3, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (24, 'button', '重置密码', 'sys:user:resetPassword', 2, NULL, NULL, NULL, '重置用户密码', 4, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (40, 'button', '用户查询', 'sys:user:read',          2, NULL, NULL, NULL, '用户查询',     5, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 角色管理下的按钮
    (25, 'button', '角色新增', 'sys:role:add',        3, NULL, NULL, NULL, '角色新增',   1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (26, 'button', '角色修改', 'sys:role:update',     3, NULL, NULL, NULL, '角色修改',   2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (27, 'button', '角色删除', 'sys:role:delete',     3, NULL, NULL, NULL, '角色删除',   3, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (53, 'button', '角色查询', 'sys:role:read',       3, NULL, NULL, NULL, '角色查询',   4, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (54, 'button', '分配权限', 'sys:role:permission', 3, NULL, NULL, NULL, '分配角色权限', 5, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 字典管理下的按钮
    (55, 'button', '字典查询', 'sys:dict:read',   6, NULL, NULL, NULL, '字典查询', 1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (56, 'button', '字典新增', 'sys:dict:add',    6, NULL, NULL, NULL, '字典新增', 2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (57, 'button', '字典修改', 'sys:dict:update', 6, NULL, NULL, NULL, '字典修改', 3, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (58, 'button', '字典删除', 'sys:dict:delete', 6, NULL, NULL, NULL, '字典删除', 4, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 部门管理下的按钮
    (41, 'button', '部门查询', 'sys:dept:read',   5, NULL, NULL, NULL, '部门查询', 1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (42, 'button', '部门新增', 'sys:dept:create', 5, NULL, NULL, NULL, '部门新增', 2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (43, 'button', '部门修改', 'sys:dept:update', 5, NULL, NULL, NULL, '部门修改', 3, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (44, 'button', '部门删除', 'sys:dept:delete', 5, NULL, NULL, NULL, '部门删除', 4, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 权限管理下的按钮
    (45, 'button', '权限查询', 'sys:permission:read',   4, NULL, NULL, NULL, '权限查询', 1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (46, 'button', '权限新增', 'sys:permission:create', 4, NULL, NULL, NULL, '权限新增', 2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (47, 'button', '权限修改', 'sys:permission:update', 4, NULL, NULL, NULL, '权限修改', 3, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (48, 'button', '权限删除', 'sys:permission:delete', 4, NULL, NULL, NULL, '权限删除', 4, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    -- 参数管理下的按钮
    (49, 'button', '参数查询', 'sys:config:read',   7, NULL, NULL, NULL, '参数查询', 1, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (50, 'button', '参数新增', 'sys:config:create', 7, NULL, NULL, NULL, '参数新增', 2, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (51, 'button', '参数修改', 'sys:config:update', 7, NULL, NULL, NULL, '参数修改', 3, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (52, 'button', '参数删除', 'sys:config:delete', 7, NULL, NULL, NULL, '参数删除', 4, 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 角色-权限：ROLE_ADMIN 拥有全部，ROLE_USER 只拥有日志管理
-- -----------------------------------------------------------------------------
INSERT INTO sys_role_permission (id, role_id, permission_id, created_at, updated_at)
SELECT row_number() OVER (ORDER BY p.id), 1, p.id, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'
FROM sys_permission p
WHERE p.deleted = 0;

INSERT INTO sys_role_permission (id, role_id, permission_id, created_at, updated_at) VALUES
    (100, 2, 30, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (101, 2, 31, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (102, 2, 32, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 字典
-- -----------------------------------------------------------------------------
INSERT INTO sys_dict (id, type, name, description, is_system, deleted, created_at, updated_at) VALUES
    (1, 'user_gender',   '用户性别', '用户性别',       1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, 'common_status', '通用状态', '启用/停用状态',  1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (3, 'yes_no',        '是否',     '是/否',          1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

INSERT INTO sys_dict_data (id, dict_id, value, label, description, meta_data, sorts, deleted, created_at, updated_at) VALUES
    (1, 1, 'F', '女', '性别-女', '{"color":"#f56c6c"}', 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, 1, 'M', '男', '性别-男', '{"color":"#409eff"}', 2, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (3, 2, '0', '正常', '状态正常', NULL, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (4, 2, '1', '停用', '状态停用', NULL, 2, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (5, 3, '1', '是', '是', NULL, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (6, 3, '0', '否', '否', NULL, 2, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 系统配置（示例项，脚手架代码未强制依赖这些key，可按需增删）
-- 注意：登录token有效期由 application.yml 的 app.security 配置，不在此处
-- -----------------------------------------------------------------------------
INSERT INTO sys_config (id, config_key, description, config_value, is_encrypted, is_system, deleted, created_at, updated_at) VALUES
    (1, 'SYS_NAME',      '系统名称',   'seed 脚手架', 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, 'SYS_COPYRIGHT', '版权信息',   'Copyright © seed', 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (3, 'SYS_ICP',       '备案号',     '', 0, 1, 0, '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 定时任务：短信派发任务迁入任务表，由调度器按cron驱动
-- invoke_target 指向 SmsSendTask 这个bean的dispatchPending方法，改动后无需改代码发版
-- -----------------------------------------------------------------------------
INSERT INTO sys_job (id, job_name, job_group, invoke_target, cron_expression, params, status, misfire_policy, remark, created_at, updated_at) VALUES
    (1, 'smsDispatchJob', 'DEFAULT', 'smsSendTask.dispatchPending', '0/5 * * * * *', NULL, 'WAITING', 'SKIP',
     '短信派发，轮询待发送短信并调用发送器', '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000'),
    (2, 'heartbeatDemoJob', 'DEFAULT', 'demoJob.tick', '0 0/1 * * * *', NULL, 'STOPPED', 'SKIP',
     '示例任务，默认停用，用于验证手动执行与启停', '2026-01-01 00:00:00.000', '2026-01-01 00:00:00.000');

-- -----------------------------------------------------------------------------
-- 重置identity序列：显式指定id插入不会推进序列，不重置的话应用后续insert会主键冲突
-- is_called=false 表示下一次 nextval 就返回给定的值
-- -----------------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('sys_dept',            'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_dept),            false);
SELECT setval(pg_get_serial_sequence('sys_role',            'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_role),            false);
SELECT setval(pg_get_serial_sequence('sys_user',            'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_user),            false);
SELECT setval(pg_get_serial_sequence('sys_user_role',       'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_user_role),       false);
SELECT setval(pg_get_serial_sequence('sys_permission',      'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_permission),      false);
SELECT setval(pg_get_serial_sequence('sys_role_permission', 'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_role_permission), false);
SELECT setval(pg_get_serial_sequence('sys_dict',            'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_dict),            false);
SELECT setval(pg_get_serial_sequence('sys_dict_data',       'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_dict_data),       false);
SELECT setval(pg_get_serial_sequence('sys_config',          'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_config),          false);
SELECT setval(pg_get_serial_sequence('sys_job',             'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_job),             false);
SELECT setval(pg_get_serial_sequence('sys_job_log',         'id'), (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_job_log),         false);
