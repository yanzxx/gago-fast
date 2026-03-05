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

const download = config => {
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
		uni.downloadFile({
			url: (config.baseUrl || store.getters.allEnv[store.getters.envKey].baseUrl) + config.url,
			header: config.header,
			timeout: config.timeout || TIMEOUT,
			success: (response) => {
				resolve(response)
				
			},
			fail: (error) => {
				reject(error)
			},
			complete: () => {
				modal.closeLoading()
			}
		})
	})
}

export default download