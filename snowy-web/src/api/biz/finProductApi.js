import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/finProduct/${url}`, ...arg)

export default {
	add(data) {
		return request('add', data)
	},
	edit(data) {
		return request('edit', data)
	},
	detail(data) {
		return request('detail', data, 'get')
	},
	page(data) {
		return request('page', data, 'get')
	},
	onShelf(data) {
		return request('onShelf', data)
	},
	offShelf(data) {
		return request('offShelf', data)
	},
	delete(data) {
		return request('delete', data)
	},
	batchDelete(data) {
		return request('batchDelete', data)
	}
}
