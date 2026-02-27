-- 贷后管理（最小版）
-- schema: yzx

CREATE TABLE IF NOT EXISTS yzx.biz_after_loan_anomaly (
    id varchar(64) PRIMARY KEY,
    tenant_id varchar(64),
    farm_id varchar(64) NOT NULL,
    anomaly_type varchar(64) NOT NULL,
    target_no varchar(64) NOT NULL,
    trigger_time timestamp NOT NULL,
    risk_level varchar(32) NOT NULL,
    status varchar(32) NOT NULL,
    description varchar(1000),
    ext_json varchar(1000),
    delete_flag varchar(32) NOT NULL DEFAULT 'NOT_DELETE',
    create_time timestamp,
    create_user varchar(64),
    update_time timestamp,
    update_user varchar(64)
);

CREATE INDEX IF NOT EXISTS idx_after_loan_anomaly_farm ON yzx.biz_after_loan_anomaly(farm_id);
CREATE INDEX IF NOT EXISTS idx_after_loan_anomaly_trigger_time ON yzx.biz_after_loan_anomaly(trigger_time);
CREATE INDEX IF NOT EXISTS idx_after_loan_anomaly_risk_status ON yzx.biz_after_loan_anomaly(risk_level, status);

CREATE TABLE IF NOT EXISTS yzx.biz_after_loan_notice (
    id varchar(64) PRIMARY KEY,
    tenant_id varchar(64),
    anomaly_id varchar(64) NOT NULL,
    farm_id varchar(64) NOT NULL,
    requirement varchar(1000) NOT NULL,
    deadline varchar(10) NOT NULL,
    receiver varchar(100) NOT NULL,
    send_status varchar(32) NOT NULL DEFAULT 'SENT',
    ext_json varchar(1000),
    delete_flag varchar(32) NOT NULL DEFAULT 'NOT_DELETE',
    create_time timestamp,
    create_user varchar(64),
    update_time timestamp,
    update_user varchar(64)
);

CREATE INDEX IF NOT EXISTS idx_after_loan_notice_anomaly ON yzx.biz_after_loan_notice(anomaly_id);
CREATE INDEX IF NOT EXISTS idx_after_loan_notice_farm ON yzx.biz_after_loan_notice(farm_id);

-- 初始化示例数据（仅当异常表为空）
INSERT INTO yzx.biz_after_loan_anomaly
(id, tenant_id, farm_id, anomaly_type, target_no, trigger_time, risk_level, status, description, delete_flag, create_time, create_user)
SELECT *
FROM (
         VALUES
             ('ALN-2026001', '-1', '1543842934270394368', '健康预警', 'EAR-2026-1001', now() - interval '1 day', 'HIGH', '待处置',
              '连续7天体温异常', 'NOT_DELETE', now(), 'superAdmin'),
             ('ALN-2026002', '-1', '1543842934270394368', '抵押不足', '批次-B202602', now() - interval '2 day', 'MEDIUM', '处理中',
              '抵押覆盖率低于阈值', 'NOT_DELETE', now(), 'superAdmin'),
             ('ALN-2026003', '-1', '1543842934270394368', '存栏异常', 'LS-2026005', now() - interval '6 hour', 'LOW', '已处置',
              '近7天死亡数量超阈值', 'NOT_DELETE', now(), 'superAdmin')
     ) t(id, tenant_id, farm_id, anomaly_type, target_no, trigger_time, risk_level, status, description, delete_flag, create_time, create_user)
WHERE NOT EXISTS(SELECT 1 FROM yzx.biz_after_loan_anomaly);
