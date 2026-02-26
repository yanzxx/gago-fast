import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`${url}`, ...arg)
/**
 * 机构选择器
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取机构分页
	orgSelector(url, data = {}) {
		return request(url, data, 'get')
	},
	// 获取机构树
	treeSelector(url, data = {}) {
		return request(url, data, 'get')
	}
}
