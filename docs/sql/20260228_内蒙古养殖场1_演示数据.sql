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
