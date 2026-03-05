import request from '@/utils/request'

// B端账号密码登录
export function login(data) {
	return request({
		url: '/auth/b/doLogin',
		extConf: {
			isToken: false
		},
		method: 'post',
		data: data
	})
}

// 获取用户信息
export function getLoginUser() {
	return request({
		url: '/auth/b/getLoginUser',
		method: 'get'
	})
}

// 退出方法
export function logout() {
	return request({
		url: '/auth/b/doLogout',
		method: 'get'
	})
}

// 获取验证码
export function getPicCaptcha() {
	return request({
		url: '/auth/b/getPicCaptcha',
		extConf: {
			isToken: false
		},
		method: 'get',
		timeout: 20000
	})
}
