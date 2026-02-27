-- 贷款管理（最小版）
-- schema: yzx

CREATE TABLE IF NOT EXISTS yzx.biz_loan_apply (
    id varchar(64) PRIMARY KEY,
    tenant_id varchar(64),
    farm_id varchar(64) NOT NULL,
    apply_no varchar(64) NOT NULL,
    product_code varchar(64) NOT NULL,
    apply_amount numeric(18,2) NOT NULL,
    applicant_name varchar(64) NOT NULL,
    loan_status varchar(32) NOT NULL DEFAULT 'PENDING_SUBMIT',
    remark varchar(1000),
    apply_time timestamp,
    ext_json varchar(1000),
    delete_flag varchar(32) NOT NULL DEFAULT 'NOT_DELETE',
    create_time timestamp,
    create_user varchar(64),
    update_time timestamp,
    update_user varchar(64)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_biz_loan_apply_no ON yzx.biz_loan_apply(apply_no);
CREATE INDEX IF NOT EXISTS idx_biz_loan_apply_farm_status ON yzx.biz_loan_apply(farm_id, loan_status);
CREATE INDEX IF NOT EXISTS idx_biz_loan_apply_time ON yzx.biz_loan_apply(apply_time);

INSERT INTO yzx.biz_loan_apply
(id, tenant_id, farm_id, apply_no, product_code, apply_amount, applicant_name, loan_status, remark, apply_time, delete_flag, create_time, create_user)
SELECT *
FROM (
         VALUES
             ('LA-2026001', '-1', '1543842934270394368', 'LOAN-202602-001', 'FP-2026001', 300000.00, '张三', 'PROCESSING',
              '首笔申请', now() - interval '1 day', 'NOT_DELETE', now(), 'superAdmin'),
             ('LA-2026002', '-1', '1543842934270394368', 'LOAN-202602-002', 'FP-2026002', 120000.00, '李四', 'PENDING_SUBMIT',
              null, now() - interval '8 hour', 'NOT_DELETE', now(), 'superAdmin')
     ) t(id, tenant_id, farm_id, apply_no, product_code, apply_amount, applicant_name, loan_status, remark, apply_time, delete_flag, create_time, create_user)
WHERE NOT EXISTS(SELECT 1 FROM yzx.biz_loan_apply);
