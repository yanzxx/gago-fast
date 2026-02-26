import { moduleRequest } from '@/utils/request'

const request = moduleRequest(`/auth/sso/cas/`)

/**
 * 单点登录
 */
export default {
	// 获取SSO登录重定向URL
	getSSOLoginRedirectUrl(data) {
		return request('loginRedirectUrl', data, 'get')
	},
	// 验证SSO ticket并获取登录信息
	loginTicket2_0(ticket) {
		return request('loginTicket2_0', { ticket }, 'post')
	}
}