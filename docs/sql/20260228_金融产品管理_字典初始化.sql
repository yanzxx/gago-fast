-- 金融产品管理字典初始化
-- schema: yzx

-- 根字典：金融产品状态
INSERT INTO yzx.dev_dict (
    id, tenant_id, parent_id, dict_label, dict_value, category, sort_code, delete_flag, create_time, create_user, update_time, update_user
)
VALUES
    ('FIN_P_STATUS_ROOT', '-1', '0', '金融产品状态', 'FIN_PRODUCT_STATUS', 'BIZ', 9901, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871')
ON CONFLICT (id) DO NOTHING;

INSERT INTO yzx.dev_dict (id, tenant_id, parent_id, dict_label, dict_value, category, sort_code, delete_flag, create_time, create_user, update_time, update_user)
VALUES
    ('FIN_P_STATUS_D', '-1', 'FIN_P_STATUS_ROOT', '草稿', 'DRAFT', 'BIZ', 1, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871'),
    ('FIN_P_STATUS_ON', '-1', 'FIN_P_STATUS_ROOT', '已上架', 'ON_SHELF', 'BIZ', 2, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871'),
    ('FIN_P_STATUS_OFF', '-1', 'FIN_P_STATUS_ROOT', '已下架', 'OFF_SHELF', 'BIZ', 3, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871')
ON CONFLICT (id) DO NOTHING;

-- 根字典：还款方式
INSERT INTO yzx.dev_dict (
    id, tenant_id, parent_id, dict_label, dict_value, category, sort_code, delete_flag, create_time, create_user, update_time, update_user
)
VALUES
    ('FIN_REPAY_TYPE_ROOT', '-1', '0', '还款方式', 'FIN_REPAY_TYPE', 'BIZ', 9902, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871')
ON CONFLICT (id) DO NOTHING;

INSERT INTO yzx.dev_dict (id, tenant_id, parent_id, dict_label, dict_value, category, sort_code, delete_flag, create_time, create_user, update_time, update_user)
VALUES
    ('FIN_REPAY_TYPE_IF', '-1', 'FIN_REPAY_TYPE_ROOT', '先息后本', 'INTEREST_FIRST', 'BIZ', 1, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871'),
    ('FIN_REPAY_TYPE_EI', '-1', 'FIN_REPAY_TYPE_ROOT', '等额本息', 'EQUAL_INSTALLMENT', 'BIZ', 2, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871'),
    ('FIN_REPAY_TYPE_LS', '-1', 'FIN_REPAY_TYPE_ROOT', '到期还本付息', 'LUMP_SUM', 'BIZ', 3, 'NOT_DELETE', now(), '1543837863788879871', now(), '1543837863788879871')
ON CONFLICT (id) DO NOTHING;
