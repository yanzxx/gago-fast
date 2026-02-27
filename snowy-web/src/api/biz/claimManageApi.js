import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/claimManage/${url}`, ...arg)

export default {
	page(data) {
		return request('page', data, 'get')
	},
	detail(data) {
		return request('detail', data, 'get')
	},
	add(data) {
		return request('add', data)
	},
	supplement(data) {
		return request('supplement', data)
	},
	handle(data) {
		return request('handle', data)
	},
	exportReport(data) {
		return request('exportReport', data, 'get')
	}
}
