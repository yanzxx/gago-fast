
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/flw/templatePrint/${url}`, ...arg)
/**
 * 打印模板
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取打印模板分页
	templatePrintPage(data) {
		return request('page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	templatePrintSubmitForm(data, edit = false) {
		return request(edit ? 'add' : 'edit', data)
	},
	// 删除打印模板
	templatePrintDelete(data) {
		return request('delete', data)
	},
	// 获取打印模板详情
	templatePrintDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取打印模板选择列表
	templateFlwTemplatePrintSelector(data) {
		return request('flwTemplatePrintSelector', data, 'get')
	}
}
