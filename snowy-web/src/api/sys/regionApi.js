import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/sys/region/${url}`, ...arg)
/**
 * 区划
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	regionPage(data) {
		return request('page', data, 'get')
	},
	// 获取区划树
	regionTree(data) {
		return request('tree', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'add' : 'edit', data)
	},
	// 删除区划
	regionDelete(data) {
		return request('delete', data)
	},
	// 获取区划详情
	regionDetail(data) {
		return request('detail', data, 'get')
	}
}
