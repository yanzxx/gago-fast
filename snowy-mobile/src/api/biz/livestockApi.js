import request from '@/utils/request'

// 新增牲畜登记
export function livestockAdd(data) {
	return request({
		url: '/biz/livestock/add',
		method: 'post',
		data
	})
}

// 牲畜登记分页
export function livestockPage(data) {
	return request({
		url: '/biz/livestock/page',
		method: 'get',
		data
	})
}

// 牲畜登记详情
export function livestockDetail(data) {
	return request({
		url: '/biz/livestock/detail',
		method: 'get',
		data
	})
}
