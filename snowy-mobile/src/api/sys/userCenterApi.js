// import upload from '@/utils/upload'
import request from '@/utils/request'

// 获取登录用户的移动端菜单
export function userLoginMobileMenu() {
	return request({
		url: '/sys/userCenter/loginMobileMenu',
		method: 'get'
	})
}

// 编辑个人信息
export function userUpdateUserInfo(data) {
	return request({
		url: '/sys/userCenter/updateUserInfo',
		method: 'post',
		data: data
	})
}

// 修改用户密码
export function userUpdatePassword(data) {
	return request({
		url: '/sys/userCenter/updatePassword',
		method: 'post',
		data: data
	})
}

// 获取登录用户的职位信息
export function userLoginPositionInfo(data) {
	return request({
		url: '/sys/userCenter/loginPositionInfo',
		method: 'get',
		data: data
	})
}

// 获取登录用户的站内信分页
export function userLoginUnreadMessagePage(data) {
	return request({
		url: '/sys/userCenter/loginUnreadMessagePage',
		method: 'get',
		data: data
	})
}

// 读取登录用户站内信详情
export function userLoginUnreadMessageDetail(data) {
	return request({
		url: '/sys/userCenter/loginUnreadMessageDetail',
		method: 'get',
		data: data
	})
}


// 根据id集合获取职位集合
export function getPositionListByIdList(data) {
	return request({
		url: '/sys/userCenter/getPositionListByIdList',
		method: 'post',
		data: data
	})
}