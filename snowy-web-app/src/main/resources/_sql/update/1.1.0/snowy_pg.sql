SET NAMES utf8mb4;

CREATE TABLE "public"."mobile_global_resource" (
                                                   "id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
                                                   "tenant_id" varchar(20) COLLATE "pg_catalog"."default",
                                                   "parent_id" varchar(255) COLLATE "pg_catalog"."default",
                                                   "title" varchar(255) COLLATE "pg_catalog"."default",
                                                   "code" varchar(255) COLLATE "pg_catalog"."default",
                                                   "category" varchar(255) COLLATE "pg_catalog"."default",
                                                   "module" varchar(255) COLLATE "pg_catalog"."default",
                                                   "menu_type" varchar(255) COLLATE "pg_catalog"."default",
                                                   "path" varchar(255) COLLATE "pg_catalog"."default",
                                                   "component" varchar(255) COLLATE "pg_catalog"."default",
                                                   "icon" varchar(255) COLLATE "pg_catalog"."default",
                                                   "color" varchar(255) COLLATE "pg_catalog"."default",
                                                   "sort_code" int4,
                                                   "ext_json" text COLLATE "pg_catalog"."default",
                                                   "delete_flag" varchar(255) COLLATE "pg_catalog"."default",
                                                   "create_time" timestamp(6),
                                                   "create_user" varchar(20) COLLATE "pg_catalog"."default",
                                                   "update_time" timestamp(6),
                                                   "update_user" varchar(20) COLLATE "pg_catalog"."default",
                                                   CONSTRAINT "mobile_global_resource_pkey" PRIMARY KEY ("id")
)
;


COMMENT ON COLUMN "public"."mobile_global_resource"."id" IS '主键';

COMMENT ON COLUMN "public"."mobile_global_resource"."tenant_id" IS '租户id';

COMMENT ON COLUMN "public"."mobile_global_resource"."parent_id" IS '父ID';

COMMENT ON COLUMN "public"."mobile_global_resource"."title" IS '名称';

COMMENT ON COLUMN "public"."mobile_global_resource"."code" IS '编码';

COMMENT ON COLUMN "public"."mobile_global_resource"."category" IS '分类';

COMMENT ON COLUMN "public"."mobile_global_resource"."module" IS '模块';

COMMENT ON COLUMN "public"."mobile_global_resource"."menu_type" IS '菜单类型';

COMMENT ON COLUMN "public"."mobile_global_resource"."path" IS '路径';

COMMENT ON COLUMN "public"."mobile_global_resource"."component" IS '组件';

COMMENT ON COLUMN "public"."mobile_global_resource"."icon" IS '图标';

COMMENT ON COLUMN "public"."mobile_global_resource"."color" IS '颜色';

COMMENT ON COLUMN "public"."mobile_global_resource"."sort_code" IS '排序码';

COMMENT ON COLUMN "public"."mobile_global_resource"."ext_json" IS '扩展信息';

COMMENT ON COLUMN "public"."mobile_global_resource"."delete_flag" IS '删除标志';

COMMENT ON COLUMN "public"."mobile_global_resource"."create_time" IS '创建时间';

COMMENT ON COLUMN "public"."mobile_global_resource"."create_user" IS '创建用户';

COMMENT ON COLUMN "public"."mobile_global_resource"."update_time" IS '修改时间';

COMMENT ON COLUMN "public"."mobile_global_resource"."update_user" IS '修改用户';

COMMENT ON TABLE "public"."mobile_global_resource" IS '全局资源权限';

-- 插入菜单
INSERT INTO "public"."sys_resource" ("id", "tenant_id", "parent_id", "title", "name", "code", "category", "module", "menu_type", "path", "component", "icon", "color", "sort_code", "ext_json", "delete_flag", "create_time", "create_user", "update_time", "update_user") VALUES ('1717432105870336002', '-1', '1623378345382506498', '全局按钮权限', 'overallButton', NULL, 'MENU', '1548901111999770525', 'MENU', '/mobile/overallButton', 'mobile/resource/overallButton/index', 'icon-universal-light', NULL, 3, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
-- 给超级管理员分配上面创建的页面权限
INSERT INTO "public"."sys_relation" ("id", "tenant_id", "object_id", "target_id", "category", "ext_json") VALUES ('1717786158982369286', NULL, '1570687866138206208', '1717432105870336002', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1717432105870336002","buttonInfo":[]}');