
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/ten/storage/${url}`, ...arg)

export default {
	// 获取租户分页
	tenPage(data) {
		return request('page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'add' : 'edit', data)
	},
	// 删除租户
	tenDelete(data) {
		return request('delete', data)
	},
	// 获取租户详情
	tenDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取数据源列表
	tenDbsList(data) {
		return request('dbsList', data, 'get')
	}
}
