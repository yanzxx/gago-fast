import request from '@/utils/request'

// 添加当前用户日程
export function indexScheduleAdd(data) {
	return request({
		url: '/sys/index/schedule/add',
		method: 'post',
		data: data
	})
}

// 删除日程
export function indexScheduleDeleteSchedule(data) {
	return request({
		url: '/sys/index/schedule/deleteSchedule',
		method: 'post',
		data: data
	})
}

// 获取当前用户日程列表
export function indexScheduleList(data) {
	return request({
		url: '/sys/index/schedule/list',
		method: 'get',
		data: data
	})
}

// 获取当前用户站内信列表
export function indexMessageList(data) {
	return request({
		url: '/sys/index/message/list',
		method: 'get',
		data: data
	})
}

// 获取站内信详情
export function indexMessageDetail(data) {
	return request({
		url: '/sys/index/message/detail',
		method: 'get',
		data: data
	})
}

// 获取当前用户访问日志列表
export function indexVisLogList(data) {
	return request({
		url: '/sys/index/visLog/list',
		method: 'get',
		data: data
	})
}

// 获取当前用户操作日志列表
export function indexOpLogList(data) {
	return request({
		url: '/sys/index/opLog/list',
		method: 'get',
		data: data
	})
}