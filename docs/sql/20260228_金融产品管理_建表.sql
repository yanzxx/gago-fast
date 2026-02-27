-- 金融产品管理（最小版）
-- schema: yzx

CREATE TABLE IF NOT EXISTS yzx.biz_fin_product (
    id varchar(64) PRIMARY KEY,
    tenant_id varchar(64),
    farm_id varchar(64) NOT NULL,
    product_name varchar(100) NOT NULL,
    product_code varchar(64) NOT NULL,
    species_codes varchar(500) NOT NULL,
    amount_min numeric(18,2) NOT NULL,
    amount_max numeric(18,2) NOT NULL,
    annual_rate numeric(8,4) NOT NULL,
    term_months int NOT NULL,
    repay_type varchar(32) NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'DRAFT',
    remark varchar(1000),
    ext_json varchar(1000),
    delete_flag varchar(32) NOT NULL DEFAULT 'NOT_DELETE',
    create_time timestamp,
    create_user varchar(64),
    update_time timestamp,
    update_user varchar(64)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_biz_fin_product_code ON yzx.biz_fin_product(product_code);
CREATE INDEX IF NOT EXISTS idx_biz_fin_product_farm_status ON yzx.biz_fin_product(farm_id, status);
