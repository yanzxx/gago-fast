import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/tabularDisplay/${url}`, ...arg)

export default {
	// 保存表单数据的表头可展示项
	saveTabularDisplay(data) {
		return request('add', data, 'post', {
			messageTipFlag: 'false'
		})
	}
}
