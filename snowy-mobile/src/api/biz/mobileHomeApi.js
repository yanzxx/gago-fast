import request from '@/utils/request'

// 移动端首页头部信息
export function mobileHomeHeader(data) {
	return request({
		url: '/biz/mobile/home/header',
		method: 'get',
		data
	})
}

// 移动端首页核心指标
export function mobileHomeMetrics(data) {
	return request({
		url: '/biz/mobile/home/metrics',
		method: 'get',
		data
	})
}

// 移动端首页待办聚合
export function mobileHomeTodos(data) {
	return request({
		url: '/biz/mobile/home/todos',
		method: 'get',
		data
	})
}

// 移动端首页待办详情
export function mobileHomeTodoDetails(data) {
	return request({
		url: '/biz/mobile/home/todoDetails',
		method: 'get',
		data
	})
}
