import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/mobile/global-resource/${url}`, ...arg)
export default {
	// 获取全局按钮权限分页
	mobileGlobalButtonPage(data) {
		return request('page', data, 'get')
	},

	// 新增/编辑全局按钮权限
	addGlobalButtonData(data, edit) {
		return request(edit ? 'add' : 'edit', data, 'post')
	},

	// 删除全局按钮权限
	deleteGlobalButtonData(data) {
		return request('delete', data, 'post')
	}
}
