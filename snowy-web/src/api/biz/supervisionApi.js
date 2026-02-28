import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/supervision/${url}`, ...arg)

export default {
	home(data) {
		return request('home', data, 'get')
	}
}
