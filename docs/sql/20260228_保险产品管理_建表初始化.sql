-- 保险产品管理（最小版）
-- schema: yzx

CREATE TABLE IF NOT EXISTS yzx.biz_ins_product (
    id varchar(64) PRIMARY KEY,
    tenant_id varchar(64),
    farm_id varchar(64) NOT NULL,
    product_name varchar(100) NOT NULL,
    product_code varchar(64) NOT NULL,
    insurance_type varchar(64) NOT NULL,
    coverage_scope varchar(1000) NOT NULL,
    premium_rate numeric(8,4) NOT NULL,
    max_compensation numeric(18,2) NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'DRAFT',
    remark varchar(1000),
    ext_json varchar(1000),
    delete_flag varchar(32) NOT NULL DEFAULT 'NOT_DELETE',
    create_time timestamp,
    create_user varchar(64),
    update_time timestamp,
    update_user varchar(64)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_biz_ins_product_code ON yzx.biz_ins_product(product_code);
CREATE INDEX IF NOT EXISTS idx_biz_ins_product_farm_status ON yzx.biz_ins_product(farm_id, status);

INSERT INTO yzx.biz_ins_product
(id, tenant_id, farm_id, product_name, product_code, insurance_type, coverage_scope, premium_rate, max_compensation, status, remark, delete_flag, create_time, create_user)
SELECT *
FROM (
         VALUES
             ('INSP-2026001', '-1', '1543842934270394368', '育肥牛综合险', 'INS-2026001', 'COMPREHENSIVE',
              '覆盖死亡、疫病和自然灾害风险', 3.5000, 300000.00, 'ON_SHELF', '首期产品', 'NOT_DELETE', now(), 'superAdmin'),
             ('INSP-2026002', '-1', '1543842934270394368', '育肥羊疫病险', 'INS-2026002', 'EPIDEMIC',
              '覆盖主要传染性疫病损失', 2.2000, 120000.00, 'DRAFT', null, 'NOT_DELETE', now(), 'superAdmin')
     ) t(id, tenant_id, farm_id, product_name, product_code, insurance_type, coverage_scope, premium_rate, max_compensation, status, remark, delete_flag, create_time, create_user)
WHERE NOT EXISTS(SELECT 1 FROM yzx.biz_ins_product);
