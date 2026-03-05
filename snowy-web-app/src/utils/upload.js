import store from '@/store'
import {
	getToken
} from '@/utils/auth'
import {
	errorCodeMap,
	reloadCodes
} from '@/utils/errorCode'
import {
	tansParams
} from '@/utils/common'
import modal from '@/plugins/modal'
import config from '@/config'
import {
	prefixUrl
} from "@/utils/apiAdaptive"

const {
	TIMEOUT,
	TOKEN_NAME,
	TOKEN_PREFIX
} = config

const upload = config => {
	// 适配URL路径
	config.url = prefixUrl(config.url)
	// 是否需要设置 token
	const isToken = (config.extConf || {}).isToken === false
	config.header = config.header || {}
	if (getToken() && !isToken) {
		config.header[TOKEN_NAME] = TOKEN_PREFIX + getToken()
	}
	config.header.Domain = store.getters.allEnv[store.getters.envKey].tenantDomain
	// get请求映射params参数
	if (config.params) {
		let url = config.url + '?' + tansParams(config.params)
		url = url.slice(0, -1)
		config.url = url
	}
	return new Promise((resolve, reject) => {
		modal.loading('努力加载中')
		uni.uploadFile({
			timeout: config.timeout || TIMEOUT,
			url: config.actionUrl || ((config.baseUrl || store.getters.allEnv[store.getters.envKey]
				.baseUrl) + config.url),
			file: config.file,
			filePath: config.filePath,
			name: config.name || 'file',
			header: config.header,
			formData: config.formData,
			success: (response) => {
				if ('string' == typeof(response.data)) {
					// JSON.stringify(str);
					response.data = JSON.parse(response.data)
				}
				const code = response.data.code || 200
				const msg = response.data.msg || errorCodeMap[code] || errorCodeMap['default']
				if (reloadCodes.includes(code)) {
					modal.confirm(msg || '登录状态已过期，您可以清除缓存，重新进行登录?').then(() => {
						store.commit('CLEAR_cache')
						uni.reLaunch({
							url: '/pages/login'
						})
					})
					reject('无效的会话，或者会话已过期，请重新登录。')
				} else if (code !== 200) {
					modal.alert(msg)
					reject(code)
				}
				resolve(response.data)
			},
			fail: (error) => {
				let {
					errMsg
				} = error
				if (errMsg === 'Network Error') {
					errMsg = '后端接口连接异常'
				} else if (errMsg.includes('timeout')) {
					errMsg = '系统接口请求超时'
				} else if (errMsg.includes('Request failed with status code')) {
					errMsg = '系统接口' + errMsg.substr(errMsg.length - 3) + '异常'
				} else if (errMsg.includes('request:fail')) {
					errMsg = '请求失败'
				}
				modal.alert(errMsg)
				reject(error)
			},
			complete: () => {
				modal.closeLoading()
			}
		})
	})
}

export default upload