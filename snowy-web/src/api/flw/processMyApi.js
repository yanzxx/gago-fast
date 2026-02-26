
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/flw/process/${url}`, ...arg)
/**
 * 我的流程
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取我可以发起的流程模型列表
	processMyModelList(data) {
		return request('myModelList', data, 'get')
	},
	// 发起流程
	processStart(data) {
		return request('start', data)
	},
	// 获取我发起的流程分页
	processMyPage(data) {
		return request('myPage', data, 'get')
	},
	// 撤回流程
	processRevoke(data) {
		return request('revoke', data)
	},
	// 获取流程详情
	processDetail(data) {
		return request('detail', data, 'get')
	}
}
