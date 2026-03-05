
-- 增加限制登录终端的开关
INSERT INTO "public"."dev_config" ("id", "tenant_id", "config_key", "config_value", "category", "remark", "sort_code", "ext_json", "delete_flag", "create_time", "create_user", "update_time", "update_user") VALUES ('1554740179362967461', '-1', 'FAST_TERMINAL_LIMIT_OPEN', 'false', 'SYS_BASE', '登录终端限制开关', 7, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);