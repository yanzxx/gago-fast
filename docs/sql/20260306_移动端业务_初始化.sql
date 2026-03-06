-- 移动端业务初始化 SQL（PostgreSQL）
-- 生成时间：2026-03-06
-- 适用范围：snowy-mobile 当前已落地的本地后端业务接口
-- 包含模块：移动端首页（监管聚合）、牲畜登记、贷款/投保/理赔/贷后、养殖场坐标与演示数据
-- 前置条件：已先执行 docs/sql/system/merged_snowy_pg_full.sql
-- 备注：/parcel /device /growth /job 等接口在当前仓库未找到本地后端实现，通常依赖外部业务中台

BEGIN;

-- ==================================================
-- SOURCE: docs/sql/biz_livestock_init.sql
-- ==================================================

-- 畜牧登记基础表（PostgreSQL）
-- 执行库：liudayu_ks
-- 执行schema：yzx

create table if not exists yzx.biz_livestock
(
    id                 varchar(64)  not null primary key,
    tenant_id          varchar(64),
    farm_id            varchar(64)  not null,
    livestock_no       varchar(64)  not null,
    species_name       varchar(64)  not null,
    birth_date         varchar(10)  not null,
    status             varchar(32)  not null,
    immunity_status    varchar(32)  not null,
    last_immunity_date varchar(10)  not null,
    collar_no          varchar(64)  not null,
    gender             varchar(32),
    pen_no             varchar(64),
    register_date      varchar(10)  not null,
    remark             varchar(500),
    image_urls         text,
    delete_flag        varchar(16)  not null default 'NOT_DELETE',
    create_time        timestamp    not null default now(),
    create_user        varchar(64),
    update_time        timestamp,
    update_user        varchar(64)
);

create unique index if not exists ux_biz_livestock_no on yzx.biz_livestock (livestock_no);
create index if not exists idx_biz_livestock_farm on yzx.biz_livestock (farm_id);
create index if not exists idx_biz_livestock_species on yzx.biz_livestock (species_name);
create index if not exists idx_biz_livestock_status on yzx.biz_livestock (status);
create index if not exists idx_biz_livestock_immunity on yzx.biz_livestock (immunity_status);
create index if not exists idx_biz_livestock_register_date on yzx.biz_livestock (register_date);

-- 初始化测试数据（仅当表为空）
insert into yzx.biz_livestock
(id, tenant_id, farm_id, livestock_no, species_name, birth_date, status, immunity_status, last_immunity_date,
 collar_no, gender, pen_no, register_date, remark, image_urls, delete_flag, create_time, create_user)
select *
from (
         values
             ('LSID-2026001', '-1', '1543842934270394368', 'LS-2026001', '牛', '2025-02-12', 'IN_STOCK', 'IMMUNIZED',
              '2026-01-20', 'COL-1001', 'MALE', 'A-01', '2026-01-21', '首批登记', null, 'NOT_DELETE', now(), 'superAdmin'),
             ('LSID-2026002', '-1', '1543842934270394368', 'LS-2026002', '羊', '2025-03-19', 'IN_STOCK', 'NOT_IMMUNIZED',
              '2025-10-10', 'COL-1002', 'FEMALE', 'A-02', '2026-01-23', null, null, 'NOT_DELETE', now(), 'superAdmin'),
             ('LSID-2026003', '-1', '1543842934270394368', 'LS-2026003', '马', '2024-11-08', 'OUT_STOCK', 'IMMUNIZED',
              '2025-12-01', 'COL-1003', 'MALE', 'B-01', '2026-01-25', null, null, 'NOT_DELETE', now(), 'superAdmin'),
             ('LSID-2026004', '-1', '1543842934270394368', 'LS-2026004', '骆驼', '2025-04-03', 'IN_STOCK', 'EXPIRED',
              '2025-07-12', 'COL-1004', 'FEMALE', 'C-03', '2026-01-17', null, null, 'NOT_DELETE', now(), 'superAdmin'),
             ('LSID-2026005', '-1', '1543842934270394368', 'LS-2026005', '牦牛', '2024-12-29', 'DEAD', 'EXPIRED',
              '2025-06-11', 'COL-1005', 'MALE', 'C-01', '2026-01-09', null, null, 'NOT_DELETE', now(), 'superAdmin')
     ) t(id, tenant_id, farm_id, livestock_no, species_name, birth_date, status, immunity_status, last_immunity_date,
         collar_no, gender, pen_no, register_date, remark, image_urls, delete_flag, create_time, create_user)
where not exists(select 1 from yzx.biz_livestock);


-- ==================================================
-- SOURCE: docs/sql/20260228_贷款管理_建表初始化.sql
-- ==================================================

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


-- ==================================================
-- SOURCE: docs/sql/20260228_投保管理_建表初始化.sql
-- ==================================================

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


-- ==================================================
-- SOURCE: docs/sql/20260228_理赔管理_建表初始化.sql
-- ==================================================

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


-- ==================================================
-- SOURCE: docs/sql/20260228_贷后管理_建表初始化.sql
-- ==================================================

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


-- ==================================================
-- SOURCE: docs/sql/20260228_养殖场经纬度初始化.sql
-- ==================================================

-- 养殖场经纬度初始化
-- 适用库: liudayu_ks
-- 适用 schema: yzx / sys_org
-- 说明: 为当前存在牲畜数据的养殖场组织写入经纬度到 sys_org.ext_json

with farm_list as (
    select distinct l.farm_id,
           row_number() over (order by l.farm_id) as rn
    from yzx.biz_livestock l
    where l.delete_flag = 'NOT_DELETE'
),
coord as (
    select
        farm_id,
        round((114.10 + ((rn - 1) % 18) * 0.08)::numeric, 6) as longitude,
        round((40.80 + ((rn - 1) / 18) * 0.06 + ((rn - 1) % 6) * 0.01)::numeric, 6) as latitude
    from farm_list
)
update sys_org o
set ext_json = (
    jsonb_set(
        jsonb_set(
            coalesce(case when o.ext_json is not null and o.ext_json <> '' then o.ext_json::jsonb else '{}'::jsonb end, '{}'::jsonb),
            '{longitude}',
            to_jsonb(c.longitude),
            true
        ),
        '{latitude}',
        to_jsonb(c.latitude),
        true
    )::text
)
from coord c
where o.id = c.farm_id;

-- 检查结果
select
    o.id,
    o.name,
    o.ext_json
from sys_org o
join (select distinct farm_id from yzx.biz_livestock where delete_flag = 'NOT_DELETE') f
  on f.farm_id = o.id
order by o.id;


-- ==================================================
-- SOURCE: docs/sql/20260228_内蒙古养殖场1_演示数据.sql
-- ==================================================

-- 演示数据初始化：内蒙古养殖场1
-- 说明：
-- 1) 优先按组织名称“内蒙古养殖场1”定位 farm_id
-- 2) 若未找到该组织，则回退到 yzx.biz_livestock 里已有的任一 farm_id
-- 3) 脚本可重复执行：会先清理本脚本写入的 demo 记录再重建

begin;

with target_farm as (
    select id as farm_id
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
),
fallback_farm as (
    select farm_id
    from yzx.biz_livestock
    where delete_flag = 'NOT_DELETE'
    order by create_time desc
    limit 1
),
use_farm as (
    select farm_id from target_farm
    union all
    select farm_id from fallback_farm
    where not exists (select 1 from target_farm)
    limit 1
)
-- 1) 清理旧演示数据
, d1 as (
    delete from yzx.biz_after_loan_notice n
    using yzx.biz_after_loan_anomaly a, use_farm f
    where n.anomaly_id = a.id
      and a.farm_id = f.farm_id
      and a.create_user = 'demo_nmg1'
    returning n.id
), d2 as (
    delete from yzx.biz_after_loan_anomaly a
    using use_farm f
    where a.farm_id = f.farm_id
      and a.create_user = 'demo_nmg1'
    returning a.id
), d3 as (
    delete from yzx.biz_claim_manage c
    using use_farm f
    where c.farm_id = f.farm_id
      and c.create_user = 'demo_nmg1'
    returning c.id
), d4 as (
    delete from yzx.biz_ins_apply i
    using use_farm f
    where i.farm_id = f.farm_id
      and i.create_user = 'demo_nmg1'
    returning i.id
), d5 as (
    delete from yzx.biz_loan_apply l
    using use_farm f
    where l.farm_id = f.farm_id
      and l.create_user = 'demo_nmg1'
    returning l.id
), d6 as (
    delete from yzx.biz_livestock l
    using use_farm f
    where l.farm_id = f.farm_id
      and l.create_user = 'demo_nmg1'
    returning l.id
)
select 1;

-- 2) 写入牲畜演示数据（畜牧登记）
with use_farm as (
    select id as farm_id
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
    union all
    select farm_id
    from yzx.biz_livestock
    where delete_flag = 'NOT_DELETE'
    order by create_time desc
    limit 1
)
insert into yzx.biz_livestock (
    id, tenant_id, farm_id, livestock_no, species_name, birth_date, status, immunity_status,
    last_immunity_date, collar_no, gender, pen_no, register_date, remark, image_urls,
    delete_flag, create_time, create_user
)
select * from (
    values
    ('NMG1-LS-0001','-1',null,'NMG1-20260001','牛','2025-03-12','IN_STOCK','IMMUNIZED','2026-02-10','NMG1-C-0001','MALE','A-01','2026-02-11','演示数据-牛1',null,'NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LS-0002','-1',null,'NMG1-20260002','牛','2025-04-02','IN_STOCK','IMMUNIZED','2026-02-08','NMG1-C-0002','FEMALE','A-02','2026-02-11','演示数据-牛2',null,'NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LS-0003','-1',null,'NMG1-20260003','羊','2025-05-03','IN_STOCK','NOT_IMMUNIZED','2025-12-28','NMG1-C-0003','MALE','B-01','2026-02-11','演示数据-羊1',null,'NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LS-0004','-1',null,'NMG1-20260004','羊','2025-05-19','IN_STOCK','EXPIRED','2025-11-20','NMG1-C-0004','FEMALE','B-02','2026-02-11','演示数据-羊2',null,'NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LS-0005','-1',null,'NMG1-20260005','马','2024-12-14','OUT_STOCK','IMMUNIZED','2026-01-15','NMG1-C-0005','MALE','C-01','2026-02-11','演示数据-马1',null,'NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LS-0006','-1',null,'NMG1-20260006','骆驼','2025-01-27','DEAD','EXPIRED','2025-10-18','NMG1-C-0006','FEMALE','C-02','2026-02-11','演示数据-骆驼1',null,'NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LS-0007','-1',null,'NMG1-20260007','牦牛','2025-02-17','IN_STOCK','IMMUNIZED','2026-02-12','NMG1-C-0007','MALE','D-01','2026-02-11','演示数据-牦牛1',null,'NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LS-0008','-1',null,'NMG1-20260008','牦牛','2025-02-22','IN_STOCK','NOT_IMMUNIZED','2026-01-28','NMG1-C-0008','FEMALE','D-02','2026-02-11','演示数据-牦牛2',null,'NOT_DELETE',now(),'demo_nmg1')
) t(id, tenant_id, farm_id, livestock_no, species_name, birth_date, status, immunity_status,
    last_immunity_date, collar_no, gender, pen_no, register_date, remark, image_urls,
    delete_flag, create_time, create_user)
cross join (select farm_id from use_farm limit 1) f;

-- 3) 写入贷款演示数据
with use_farm as (
    select id as farm_id
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
    union all
    select farm_id
    from yzx.biz_livestock
    where delete_flag = 'NOT_DELETE'
    order by create_time desc
    limit 1
)
insert into yzx.biz_loan_apply (
    id, tenant_id, farm_id, apply_no, product_code, apply_amount, applicant_name,
    loan_status, remark, apply_time, delete_flag, create_time, create_user
)
select * from (
    values
    ('NMG1-LA-0001','-1',null,'NMG1-LOAN-001','FP-2026001',580000.00,'内蒙古养殖场1','APPROVED','演示-已通过',now()-interval '12 day','NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LA-0002','-1',null,'NMG1-LOAN-002','FP-2026002',320000.00,'内蒙古养殖场1','PROCESSING','演示-处理中',now()-interval '4 day','NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-LA-0003','-1',null,'NMG1-LOAN-003','FP-2026002',180000.00,'内蒙古养殖场1','PENDING_SUBMIT','演示-待提交',now()-interval '1 day','NOT_DELETE',now(),'demo_nmg1')
) t(id, tenant_id, farm_id, apply_no, product_code, apply_amount, applicant_name,
    loan_status, remark, apply_time, delete_flag, create_time, create_user)
cross join (select farm_id from use_farm limit 1) f;

-- 4) 写入投保演示数据
with use_farm as (
    select id as farm_id
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
    union all
    select farm_id
    from yzx.biz_livestock
    where delete_flag = 'NOT_DELETE'
    order by create_time desc
    limit 1
)
insert into yzx.biz_ins_apply (
    id, tenant_id, farm_id, apply_no, policy_no, product_code, insured_name, species_type,
    insured_count, insured_amount, premium_amount, status, remark, delete_flag, create_time, create_user
)
select * from (
    values
    ('NMG1-IA-0001','-1',null,'NMG1-TB-001','NMG1-PL-001','INS-2026001','内蒙古养殖场1','牛',150,460000.00,13200.00,'POLICY_ISSUED','演示-已出单','NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-IA-0002','-1',null,'NMG1-TB-002',null,'INS-2026002','内蒙古养殖场1','羊',120,260000.00,7400.00,'SUBMITTED','演示-已提交','NOT_DELETE',now(),'demo_nmg1')
) t(id, tenant_id, farm_id, apply_no, policy_no, product_code, insured_name, species_type,
    insured_count, insured_amount, premium_amount, status, remark, delete_flag, create_time, create_user)
cross join (select farm_id from use_farm limit 1) f;

-- 5) 写入理赔演示数据
with use_farm as (
    select id as farm_id
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
    union all
    select farm_id
    from yzx.biz_livestock
    where delete_flag = 'NOT_DELETE'
    order by create_time desc
    limit 1
)
insert into yzx.biz_claim_manage (
    id, tenant_id, farm_id, claim_no, policy_no, apply_no, insured_name, claim_type,
    loss_count, claim_amount, occur_time, status, evidence_files, result_remark,
    remark, delete_flag, create_time, create_user
)
select * from (
    values
    ('NMG1-CL-0001','-1',null,'NMG1-LP-001','NMG1-PL-001','NMG1-TB-001','内蒙古养殖场1','DEATH',2,18000.00,'2026-02-20','PENDING','[]',null,'演示-待处理','NOT_DELETE',now(),'demo_nmg1'),
    ('NMG1-CL-0002','-1',null,'NMG1-LP-002','NMG1-PL-001','NMG1-TB-001','内蒙古养殖场1','DISEASE',4,26500.00,'2026-02-18','CLOSED','[]','核损通过，已赔付','演示-已结案','NOT_DELETE',now(),'demo_nmg1')
) t(id, tenant_id, farm_id, claim_no, policy_no, apply_no, insured_name, claim_type,
    loss_count, claim_amount, occur_time, status, evidence_files, result_remark,
    remark, delete_flag, create_time, create_user)
cross join (select farm_id from use_farm limit 1) f;

-- 6) 写入预警演示数据 + 整改通知
with use_farm as (
    select id as farm_id
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
    union all
    select farm_id
    from yzx.biz_livestock
    where delete_flag = 'NOT_DELETE'
    order by create_time desc
    limit 1
),
insert_anomaly as (
    insert into yzx.biz_after_loan_anomaly (
        id, tenant_id, farm_id, anomaly_type, target_no, trigger_time, risk_level,
        status, description, ext_json, delete_flag, create_time, create_user
    )
    select * from (
        values
        ('NMG1-ALN-0001','-1',null,'健康预警','NMG1-20260004',now()-interval '2 hour','HIGH','待处置','防疫过期牲畜达到阈值','{}','NOT_DELETE',now(),'demo_nmg1'),
        ('NMG1-ALN-0002','-1',null,'死亡异常预警','NMG1-20260006',now()-interval '1 day','MEDIUM','处理中','7天死亡数量超过阈值','{}','NOT_DELETE',now(),'demo_nmg1'),
        ('NMG1-ALN-0003','-1',null,'抵押不足','NMG1-LOAN-002',now()-interval '3 day','LOW','已处置','抵押覆盖率不足，已补齐','{}','NOT_DELETE',now(),'demo_nmg1')
    ) t(id, tenant_id, farm_id, anomaly_type, target_no, trigger_time, risk_level,
        status, description, ext_json, delete_flag, create_time, create_user)
    cross join (select farm_id from use_farm limit 1) f
    returning id, farm_id
)
insert into yzx.biz_after_loan_notice (
    id, tenant_id, anomaly_id, farm_id, requirement, deadline, receiver,
    send_status, ext_json, delete_flag, create_time, create_user
)
select
    'NMG1-NTC-0001',
    '-1',
    ia.id,
    ia.farm_id,
    '请在3日内完成补种并回传影像凭证',
    to_char(now() + interval '3 day', 'YYYY-MM-DD'),
    '内蒙古养殖场1负责人',
    'SENT',
    '{}',
    'NOT_DELETE',
    now(),
    'demo_nmg1'
from insert_anomaly ia
where ia.id = 'NMG1-ALN-0001';

-- 7) 为目标养殖场补经纬度（如果没有）
with target_farm as (
    select id as farm_id
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
)
update sys_org o
set ext_json = (
    jsonb_set(
        jsonb_set(
            coalesce(case when o.ext_json is not null and o.ext_json <> '' then o.ext_json::jsonb else '{}'::jsonb end, '{}'::jsonb),
            '{longitude}', to_jsonb(111.670801::numeric), true
        ),
        '{latitude}', to_jsonb(40.818311::numeric), true
    )::text
)
from target_farm tf
where o.id = tf.farm_id;

commit;

-- 检查：目标农场聚合（可选）
with target_farm as (
    select id as farm_id, name
    from sys_org
    where delete_flag = 'NOT_DELETE'
      and name = '内蒙古养殖场1'
    order by create_time desc
    limit 1
)
select
    tf.farm_id,
    tf.name,
    (select count(1) from yzx.biz_livestock l where l.farm_id = tf.farm_id and l.delete_flag='NOT_DELETE') as livestock_cnt,
    (select count(1) from yzx.biz_loan_apply la where la.farm_id = tf.farm_id and la.delete_flag='NOT_DELETE') as loan_cnt,
    (select count(1) from yzx.biz_after_loan_anomaly a where a.farm_id = tf.farm_id and a.delete_flag='NOT_DELETE') as anomaly_cnt
from target_farm tf;


COMMIT;
