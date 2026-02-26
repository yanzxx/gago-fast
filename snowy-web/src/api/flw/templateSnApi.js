
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/flw/templateSn/${url}`, ...arg)
/**
 * 流水号
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取流水号模板分页
	templateSnPage(data) {
		return request('page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	templateSnSubmitForm(data, edit = false) {
		return request(edit ? 'add' : 'edit', data)
	},
	// 删除流水号模板
	templateSnDelete(data) {
		return request('delete', data)
	},
	// 获取流水号模板详情
	templateSnDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取序列号模板选择列表
	templateFlwTemplateSnSelector(data) {
		return request('flwTemplateSnSelector', data, 'get')
	}
}
