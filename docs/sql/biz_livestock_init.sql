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
