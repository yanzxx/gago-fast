import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/insApply/${url}`, ...arg)

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
	edit(data) {
		return request('edit', data)
	},
	delete(data) {
		return request('delete', data)
	}
}
