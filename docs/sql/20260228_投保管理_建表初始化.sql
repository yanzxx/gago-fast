-- 投保管理（最小版）
-- schema: yzx

CREATE TABLE IF NOT EXISTS yzx.biz_ins_apply (
    id varchar(64) PRIMARY KEY,
    tenant_id varchar(64),
    farm_id varchar(64) NOT NULL,
    apply_no varchar(64) NOT NULL,
    policy_no varchar(64),
    product_code varchar(64) NOT NULL,
    insured_name varchar(64) NOT NULL,
    species_type varchar(64) NOT NULL,
    insured_count int NOT NULL,
    insured_amount numeric(18,2) NOT NULL,
    premium_amount numeric(18,2) NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'PENDING_SUBMIT',
    remark varchar(1000),
    ext_json varchar(1000),
    delete_flag varchar(32) NOT NULL DEFAULT 'NOT_DELETE',
    create_time timestamp,
    create_user varchar(64),
    update_time timestamp,
    update_user varchar(64)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_biz_ins_apply_no ON yzx.biz_ins_apply(apply_no);
CREATE INDEX IF NOT EXISTS idx_biz_ins_apply_farm_status ON yzx.biz_ins_apply(farm_id, status);

INSERT INTO yzx.biz_ins_apply
(id, tenant_id, farm_id, apply_no, policy_no, product_code, insured_name, species_type, insured_count, insured_amount, premium_amount, status, remark, delete_flag, create_time, create_user)
SELECT *
FROM (
         VALUES
             ('TBA-2026001', '-1', '1543842934270394368', 'TB-2026001', 'PL-2026001', 'INS-2026001', '张三养殖场', '牛', 120, 300000.00, 10500.00, 'SUBMITTED', null, 'NOT_DELETE', now(), 'superAdmin'),
             ('TBA-2026002', '-1', '1543842934270394368', 'TB-2026002', null, 'INS-2026002', '李四合作社', '羊', 80, 120000.00, 3600.00, 'PENDING_SUBMIT', null, 'NOT_DELETE', now(), 'superAdmin')
     ) t(id, tenant_id, farm_id, apply_no, policy_no, product_code, insured_name, species_type, insured_count, insured_amount, premium_amount, status, remark, delete_flag, create_time, create_user)
WHERE NOT EXISTS(SELECT 1 FROM yzx.biz_ins_apply);
