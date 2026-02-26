
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/flw/model/${url}`, ...arg)
/**
 * 模型
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取模型分页
	modelPage(data) {
		return request('page', data, 'get')
	},
	// 获取所有模型列表
	modelAllList(data) {
		return request('allList', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'add' : 'edit', data)
	},
	// 删除模型
	modelDelete(data) {
		return request('delete', data)
	},
	// 部署模型
	modelDeploy(data) {
		return request('deploy', data)
	},
	// 获取模型详情
	modelDetail(data) {
		return request('detail', data, 'get')
	},
	// 停用模型
	modelDisable(data) {
		return request('disableModel', data)
	},
	// 启用模型
	modelEnable(data) {
		return request('enableModel', data)
	}
}
