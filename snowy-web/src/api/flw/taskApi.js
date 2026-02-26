
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/flw/task/${url}`, ...arg)
/**
 * 待办任务
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取待办任务分页
	taskTodoPage(data) {
		return request('todoPage', data, 'get')
	},
	// 获取已办任务分页
	taskDonePage(data) {
		return request('donePage', data, 'get')
	},
	// 调整申请
	taskAdjust(data) {
		return request('adjust', data)
	},
	// 审批同意
	taskPass(data) {
		return request('pass', data)
	},
	// 审批拒绝
	taskReject(data) {
		return request('reject', data)
	},
	// 任务转办
	taskTurn(data) {
		return request('turn', data)
	},
	// 任务详情
	taskDetail(data) {
		return request('detail', data, 'get')
	}
}
