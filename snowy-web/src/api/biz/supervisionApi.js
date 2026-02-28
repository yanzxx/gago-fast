import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/supervision/${url}`, ...arg)

export default {
	home(data) {
		return request('home', data, 'get')
	},
	farmStats(data) {
		return request('farmStats', data, 'get')
	},
	riskThresholdDetail(data) {
		return request('riskThreshold/detail', data, 'get')
	},
	saveRiskThreshold(data) {
		return request('riskThreshold/save', data)
	}
}
