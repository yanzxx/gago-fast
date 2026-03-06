-- 2026-03-06 消息管理/消息通知演示数据
-- 目标库: PostgreSQL
-- 目标schema: yzx

BEGIN;

WITH demo_message(id, category, subject, content, create_time) AS (
  VALUES
    ('26030611350000000001', 'AUDIT',   '养殖补贴申报待审核',       '您提交的2026年第一季度养殖补贴申请已进入审核，请在2日内补充附件材料。', '2026-03-06 09:20:00'::timestamp),
    ('26030611350000000002', 'AUDIT',   '检疫证明上传提醒',         '系统检测到有3头牲畜缺少最新检疫证明，请尽快补录，避免出栏受限。',         '2026-03-05 16:45:00'::timestamp),
    ('26030611350000000003', 'AUDIT',   '防疫计划已通过审批',       '本月防疫计划已审核通过，可按计划执行并按时上传执行记录。',                 '2026-03-05 11:08:00'::timestamp),
    ('26030611350000000004', 'POLICY',  '市农业局发布新补贴政策',   '《规模化养殖场绿色升级补贴实施细则》已发布，请在政策通知中查看详情。',       '2026-03-04 14:30:00'::timestamp),
    ('26030611350000000005', 'POLICY',  '环保巡检制度更新通知',     '环保巡检台账提交频率由每月1次调整为每月2次，请及时调整工作安排。',           '2026-03-03 10:12:00'::timestamp),
    ('26030611350000000006', 'POLICY',  '出栏备案流程调整公告',     '出栏备案新增运输车辆信息字段，2026-04-01起生效，请提前准备。',               '2026-03-02 09:01:00'::timestamp),
    ('26030611350000000007', 'WARNING', '圈舍温度异常预警',         '3号圈舍温度连续20分钟高于阈值，请立即检查通风设备。',                       '2026-03-06 07:42:00'::timestamp),
    ('26030611350000000008', 'WARNING', '饮水系统流量告警',         '东区饮水系统流量较昨日下降35%，建议排查管路堵塞或泵站故障。',                 '2026-03-05 22:16:00'::timestamp),
    ('26030611350000000009', 'WARNING', '死亡率波动风险提醒',       '近7日死亡率较上周上升0.9%，请关注饲料质量与环境应激因素。',                 '2026-03-04 19:55:00'::timestamp),
    ('26030611350000000010', 'AUDIT',   '养殖保险续保待确认',       '您有1份养殖保险将在7天后到期，请前往保险管理完成续保确认。',                 '2026-03-03 08:36:00'::timestamp),
    ('26030611350000000011', 'POLICY',  '培训通知：疫病防控专场',   '2026-03-10 15:00开展疫病防控线上培训，建议场长与防疫员准时参加。',           '2026-03-02 17:40:00'::timestamp),
    ('26030611350000000012', 'WARNING', '饲料库存低位预警',         '当前主饲料库存预计仅可维持2.3天，请及时补货避免断供风险。',                 '2026-03-01 13:25:00'::timestamp)
),
insert_message AS (
  INSERT INTO yzx.dev_message(id, tenant_id, category, subject, content, ext_json, delete_flag, create_time, create_user, update_time, update_user)
  SELECT dm.id, '-1', dm.category, dm.subject, dm.content, NULL, 'NOT_DELETE', dm.create_time, '1543837863788879871', dm.create_time, '1543837863788879871'
  FROM demo_message dm
  WHERE NOT EXISTS (SELECT 1 FROM yzx.dev_message m WHERE m.id = dm.id)
  RETURNING id
),
enabled_user AS (
  SELECT id AS user_id
  FROM yzx.sys_user
  WHERE user_status = 'ENABLE'
),
demo_message_all AS (
  SELECT id, category FROM demo_message
),
demo_relation AS (
  SELECT
    ('36' || substr(md5(dm.id || eu.user_id), 1, 18))::varchar(20) AS relation_id,
    dm.id AS object_id,
    eu.user_id AS target_id,
    CASE
      WHEN dm.category = 'POLICY' THEN '{"read":true}'
      ELSE '{"read":false}'
    END AS ext_json
  FROM demo_message_all dm
  CROSS JOIN enabled_user eu
)
INSERT INTO yzx.dev_relation(id, tenant_id, object_id, target_id, category, ext_json)
SELECT dr.relation_id, '-1', dr.object_id, dr.target_id, 'MSG_TO_USER', dr.ext_json
FROM demo_relation dr
WHERE NOT EXISTS (
  SELECT 1
  FROM yzx.dev_relation r
  WHERE r.object_id = dr.object_id
    AND r.target_id = dr.target_id
    AND r.category = 'MSG_TO_USER'
);

COMMIT;
