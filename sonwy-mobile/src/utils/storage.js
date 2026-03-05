import constant from './constant'

// 存储变量名
let storageKey = 'storage_data'

// 存储节点变量名
let storageNodeKeys = [
	constant.envKey,
	constant.allEnv,
	constant.sysBaseConfig,
	constant.homeConfigs,
	constant.userInfo, 
	constant.userMobileMenus, 
	constant.dictTypeTreeData
]

// 存储的数据
let storageData = JSON.parse(localStorage.getItem(storageKey)) || {}

const storage = {
	set: function(key, value) {
		if (storageNodeKeys.indexOf(key) != -1) {
			let tmp = localStorage.getItem(storageKey)
			tmp = tmp ? JSON.parse(tmp) : {}
			tmp[key] = value
			if (typeof tmp === 'object') {
				localStorage.setItem(storageKey, JSON.stringify(tmp))
			} else {
				localStorage.setItem(storageKey, tmp)
			}
		}
	},
	get: function(key) {
		return storageData[key] ? storageData[key] : null
	},
	remove: function(key) {
		delete storageData[key]
		localStorage.setItem(storageKey, JSON.stringify(storageData))
	},
	clean: function() {
		localStorage.removeItem(storageKey)
	}
}

export default storage
