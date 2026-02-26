-- gen_basic添加列
ALTER TABLE gen_basic ADD COLUMN extend_base VARCHAR (255);
-- gen_config添加列
ALTER TABLE gen_config ADD COLUMN whether_sort VARCHAR (255);
-- 添加注释
comment on column gen_basic.extend_base is '是否继承基础类';
-- 添加注释
comment on column gen_config.whether_sort is '排序字段(desc)';
