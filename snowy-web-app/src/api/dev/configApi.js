import request from '@/utils/request'

export function configSysBaseList() {
	return request({
		url: '/dev/config/sysBaseList',
		extConf: {
			isToken: false
		},
		method: 'get',
		timeout: 20000
	})
}
