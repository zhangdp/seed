package com.zhangdp.seed.common.constant;

/**
 * 2023/5/17 表名常量
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface TableNameConst {

    /**
     * 分隔符
     */
    String SPLIT = "_";

    /**
     * 前缀-sys系统相关表
     */
    String PREFIX_SYS = "sys";
    /**
     * 前缀-log日志表
     */
    String PREFIX_LOG = "log";

    /**
     * 用户表
     */
    String SYS_USER = PREFIX_SYS + SPLIT + "user";
    /**
     * 部门表
     */
    String SYS_DEPT = PREFIX_SYS + SPLIT + "dept";
    /**
     * 资源表
     */
    String SYS_RESOURCE = PREFIX_SYS + SPLIT + "resource";
    /**
     * 参数表
     */
    String SYS_CONFIG = PREFIX_SYS + SPLIT + "config";
    /**
     * 字典表
     */
    String SYS_DICT_DATA = PREFIX_SYS + SPLIT + "dict_data";
    /**
     * 角色表
     */
    String SYS_ROLE = PREFIX_SYS + SPLIT + "sys_role";

}
