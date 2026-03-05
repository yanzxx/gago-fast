import request from '@/utils/request'

// 获取机构树选择器
export function orgTreeSelector(data, url) {
	return request({
		url: url || '/biz/user/orgTreeSelector',
		method: 'get',
		data: data
	})
}
// 获取用户分页
export function userSelector(data, url) {
	return request({
		url: url || '/biz/user/userSelector',
		method: 'get',
		data: data
	})
}
// 根据id集合获取用户集合
export function getUserListByIdList(data, url) {
	return request({
		url: url || '/sys/userCenter/getUserListByIdList',
		method: 'post',
		data: data
	})
}