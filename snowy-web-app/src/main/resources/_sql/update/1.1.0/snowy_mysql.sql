SET NAMES utf8mb4;

CREATE TABLE `mobile_global_resource` (
                                          `ID` varchar(20) NOT NULL COMMENT '主键',
                                          `TENANT_ID` varchar(20) DEFAULT NULL COMMENT '租户id',
                                          `PARENT_ID` varchar(255) DEFAULT NULL COMMENT '父ID',
                                          `TITLE` varchar(255) DEFAULT NULL COMMENT '名称',
                                          `CODE` varchar(255) DEFAULT NULL COMMENT '编码',
                                          `CATEGORY` varchar(255) DEFAULT NULL COMMENT '分类',
                                          `MODULE` varchar(255) DEFAULT NULL COMMENT '模块',
                                          `MENU_TYPE` varchar(255) DEFAULT NULL COMMENT '菜单类型',
                                          `PATH` varchar(255) DEFAULT NULL COMMENT '路径',
                                          `COMPONENT` varchar(255) DEFAULT NULL COMMENT '组件',
                                          `ICON` varchar(255) DEFAULT NULL COMMENT '图标',
                                          `COLOR` varchar(255) DEFAULT NULL COMMENT '颜色',
                                          `SORT_CODE` int(11) DEFAULT NULL COMMENT '排序码',
                                          `EXT_JSON` longtext COMMENT '扩展信息',
                                          `DELETE_FLAG` varchar(255) DEFAULT NULL COMMENT '删除标志',
                                          `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
                                          `CREATE_USER` varchar(20) DEFAULT NULL COMMENT '创建用户',
                                          `UPDATE_TIME` datetime DEFAULT NULL COMMENT '修改时间',
                                          `UPDATE_USER` varchar(20) DEFAULT NULL COMMENT '修改用户',
                                          PRIMARY KEY (`ID`) USING BTREE
) DEFAULT CHARSET=utf8mb4 COMMENT='全局资源权限';



-- 插入菜单
INSERT INTO `SYS_RESOURCE` (`ID`, `TENANT_ID`, `PARENT_ID`, `TITLE`, `NAME`, `CODE`, `CATEGORY`, `MODULE`, `MENU_TYPE`, `PATH`, `COMPONENT`, `ICON`, `COLOR`, `SORT_CODE`, `EXT_JSON`, `DELETE_FLAG`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`) VALUES ('1717432105870336002', NULL, '1623378345382506498', '全局按钮权限', 'overallButton', NULL, 'MENU', '1548901111999770525', 'MENU', '/mobile/overallButton', 'mobile/resource/overallButton/index', 'icon-universal-light', NULL, 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
-- 给超级管理员分配上面创建的页面权限
INSERT INTO `SYS_RELATION` (`ID`, `TENANT_ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES ('1717716253121998850', NULL, '1570687866138206208', '1717432105870336002', 'SYS_ROLE_HAS_RESOURCE', '{\"menuId\":\"1717432105870336002\",\"buttonInfo\":[]}');