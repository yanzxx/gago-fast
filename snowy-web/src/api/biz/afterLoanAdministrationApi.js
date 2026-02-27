import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/afterLoanAdministration/${url}`, ...arg)

export default {
	stats(data) {
		return request('stats', data, 'get')
	},
	anomalyPage(data) {
		return request('anomalyPage', data, 'get')
	},
	sendRectifyNotice(data) {
		return request('sendRectifyNotice', data)
	}
}
