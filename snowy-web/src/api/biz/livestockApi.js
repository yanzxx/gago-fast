import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/livestock/${url}`, ...arg)

export default {
	// 新增登记
	add(data) {
		return request('add', data)
	},
	// 编辑登记
	edit(data) {
		return request('edit', data)
	},
	// 登记详情
	detail(data) {
		return request('detail', data, 'get')
	},
	// 登记列表分页
	page(data) {
		return request('page', data, 'get')
	},
	// 农场维度畜种选项（后端可按 farmId 过滤）
	speciesOptions(data) {
		return request('species/options', data, 'get')
	}
}
