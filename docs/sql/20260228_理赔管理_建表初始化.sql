-- 理赔管理（最小版）
-- schema: yzx

CREATE TABLE IF NOT EXISTS yzx.biz_claim_manage (
    id varchar(64) PRIMARY KEY,
    tenant_id varchar(64),
    farm_id varchar(64) NOT NULL,
    claim_no varchar(64) NOT NULL,
    policy_no varchar(64) NOT NULL,
    apply_no varchar(64),
    insured_name varchar(100) NOT NULL,
    claim_type varchar(32) NOT NULL,
    loss_count integer NOT NULL,
    claim_amount numeric(18,2) NOT NULL,
    occur_time varchar(20) NOT NULL,
    status varchar(32) NOT NULL DEFAULT 'PENDING',
    evidence_files text,
    result_remark varchar(1000),
    remark varchar(1000),
    ext_json varchar(1000),
    delete_flag varchar(32) NOT NULL DEFAULT 'NOT_DELETE',
    create_time timestamp,
    create_user varchar(64),
    update_time timestamp,
    update_user varchar(64)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_claim_manage_claim_no ON yzx.biz_claim_manage(claim_no);
CREATE INDEX IF NOT EXISTS idx_claim_manage_farm ON yzx.biz_claim_manage(farm_id);
CREATE INDEX IF NOT EXISTS idx_claim_manage_policy_no ON yzx.biz_claim_manage(policy_no);
CREATE INDEX IF NOT EXISTS idx_claim_manage_status ON yzx.biz_claim_manage(status);

-- 初始化示例数据（仅当表为空）
INSERT INTO yzx.biz_claim_manage
(id, tenant_id, farm_id, claim_no, policy_no, apply_no, insured_name, claim_type, loss_count, claim_amount, occur_time, status, evidence_files, result_remark, remark, delete_flag, create_time, create_user)
SELECT *
FROM (
         VALUES
             ('CLM-2026001', '-1', '1543842934270394368', 'LP20260228001', 'PL20260217001', 'TB-2026001', '张三养殖场',
              'DEATH', 3, 12000.00, '2026-02-25', 'PENDING',
              '[{\"name\":\"现场照片1.jpg\",\"url\":\"https://example.com/claim/1.jpg\"}]', null, '首笔理赔', 'NOT_DELETE', now(), 'superAdmin'),
             ('CLM-2026002', '-1', '1543842934270394368', 'LP20260228002', 'PL20260217002', 'TB-2026002', '李四养殖场',
              'DISEASE', 5, 18600.00, '2026-02-21', 'PROCESSING',
              '[{\"name\":\"检测报告.pdf\",\"url\":\"https://example.com/claim/2.pdf\"}]', null, '资料补充中', 'NOT_DELETE', now(), 'superAdmin'),
             ('CLM-2026003', '-1', '1543842934270394368', 'LP20260228003', 'PL20260217003', 'TB-2026003', '王五养殖场',
              'ACCIDENT', 2, 9800.00, '2026-02-20', 'CLOSED',
              '[{\"name\":\"现场视频.mp4\",\"url\":\"https://example.com/claim/3.mp4\"}]', '核损通过，已结案', '已归档', 'NOT_DELETE', now(), 'superAdmin')
     ) t(id, tenant_id, farm_id, claim_no, policy_no, apply_no, insured_name, claim_type, loss_count, claim_amount, occur_time, status, evidence_files, result_remark, remark, delete_flag, create_time, create_user)
WHERE NOT EXISTS(SELECT 1 FROM yzx.biz_claim_manage);
