


-- 插入菜单【自定义表单引擎】
INSERT INTO "public"."sys_resource" ("id", "tenant_id", "parent_id", "title", "name", "code", "category", "module", "menu_type", "path", "component", "icon", "color", "sort_code", "ext_json", "delete_flag", "create_time", "create_user", "update_time", "update_user") VALUES ('1731946114896048129', NULL, '1548901111999773250', '自定义表单引擎', 'formDesign', NULL, 'MENU', '1548901111999770525', 'MENU', '/formDesign', 'form/formDesign/index', 'form-outlined', NULL, 46, NULL, 'NOT_DELETE', '2023-12-05 15:58:26.454', '1543837863788879871', '2023-12-08 13:49:02.852', '1543837863788879871');
-- 插入菜单【表单引擎构建展示页面】
INSERT INTO "public"."sys_resource" ("id", "tenant_id", "parent_id", "title", "name", "code", "category", "module", "menu_type", "path", "component", "icon", "color", "sort_code", "ext_json", "delete_flag", "create_time", "create_user", "update_time", "update_user") VALUES ('1731952236973469697', NULL, '1548901111999773250', '表单引擎构建展示页面', 'formBuild', NULL, 'MENU', '1548901111999770525', 'MENU', '/formBuild', 'form/formBuild/index', NULL, NULL, 99, NULL, 'NOT_DELETE', '2023-12-05 16:22:46.07', '1543837863788879871', '2023-12-08 13:49:43.866', '1543837863788879871');


-- 给超级管理员分配【自定义表单引擎】菜单权限
INSERT INTO "public"."sys_relation" ("id", "tenant_id", "object_id", "target_id", "category", "ext_json") VALUES ('1731952281609252868', NULL, '1570687866138206208', '1731952236973469697', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1731952236973469697","buttonInfo":[]}');
-- 给超级管理员分配【表单引擎构建展示页面】菜单权限
INSERT INTO "public"."sys_relation" ("id", "tenant_id", "object_id", "target_id", "category", "ext_json") VALUES ('1731952281609252867', NULL, '1570687866138206208', '1731946114896048129', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1731946114896048129","buttonInfo":[]}');
