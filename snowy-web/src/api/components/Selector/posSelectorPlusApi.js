import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`${url}`, ...arg)
/**
 * 职位选择器
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取职位分页
	posSelector(url, data = {}) {
		return request(url, data, 'get')
	},
	// 获取机构树
	treeSelector(url, data = {}) {
		return request(url, data, 'get')
	}
}
