-- 添加字段
ALTER TABLE gen_basic ADD COLUMN extend_base varchar(255) DEFAULT null comment '是否继承基础类'	;
ALTER TABLE gen_config ADD COLUMN whether_sort varchar(255) DEFAULT null comment '排序字段(desc)'	;
