
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/flw/process/monitor/${url}`, ...arg)
/**
 * 流程
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取所有流程分页
	processMonitorPage(data) {
		return request('monitorPage', data, 'get')
	},
	// 删除流程
	processDelete(data) {
		return request('delete', data)
	},
	// 终止流程
	processEnd(data) {
		return request('end', data)
	},
	// 撤回流程
	processRevoke(data) {
		return request('revoke', data)
	},
	// 挂起流程
	processSuspend(data) {
		return request('suspend', data)
	},
	// 激活流程
	processActive(data) {
		return request('active', data)
	},
	// 转办流程
	processTurn(data) {
		return request('turn', data)
	},
	// 获取流程变量分页
	processVariablePage(data) {
		return request('variablePage', data, 'get')
	},
	// 获取流程详情
	processDetail(data) {
		return request('detail', data, 'get')
	}
}
