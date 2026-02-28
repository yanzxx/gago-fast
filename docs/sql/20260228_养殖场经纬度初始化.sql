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
