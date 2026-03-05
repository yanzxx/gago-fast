import store from '@/store';
import config from '@/config';
import XEUtils from 'xe-utils';
import { showToast, showFailToast } from 'vant';
import router from '@/router';

const TokenKey = 'App-Token'

export function getToken() {
	if (config.storageType === 'localStorage') {
		return localStorage.getItem(config.STORAGE_PREFIX + TokenKey)
	} else {
		return sessionStorage.getItem(config.STORAGE_PREFIX + TokenKey)
	}
}

export function setToken(token) {
	if (config.storageType === 'localStorage') {
		return localStorage.setItem(config.STORAGE_PREFIX + TokenKey, token)
	} else {
		return sessionStorage.setItem(config.STORAGE_PREFIX + TokenKey, token)
	}
}

export function removeToken() {
	return localStorage.removeItem(TokenKey)
}

// 页面跳转 校验权限
export function checkPermission(path) {
	if (!getToken()) {
		// 没有token
		if (config.NO_TOKEN_WHITE_LIST.includes(path)) {
			return true
		} else {
			showFailToast("页面【" + path + "】需要进行登录，才能进行访问！");
			router.push({
				path: config.HAS_TOKEN_BACK_URL
			})
			return false
		}
	} else {
		// 有token
		if (config.NO_TOKEN_WHITE_LIST.includes(path) || config.HAS_TOKEN_WHITE_LIST.includes(path)) {
			return true
		} else {
			// 路径正则过滤：/pages/biz/user/index =》 /pages/biz/user/**			
			const isVisit = XEUtils.findTree(store.getters.userMobileMenus, item => {
				console.log('item ====>', item)
				if (item.category === 'MENU' && item.menuType === 'MENU') {
					const itemPath = item.path
					// 不使用正则表达式（只有路径相同的时候才可以进行访问）
					if (item.regType === 'NO') {
						return path === itemPath
					}
					// 使用正则表达式规则（相同的文件夹路径下页面可进行访问）
					if (item.regType === 'YES') {
						const regExp = new RegExp("^" + itemPath.substr(0, itemPath.lastIndexOf("/") + 1))
						return regExp.test(path)
					}
				}
			})
			// console.log("是否允许访问：",isVisit)
			if (isVisit) {
				return true
			} else {
				// showToast({
				// 	title: "页面【" + path + "】需要进行授权，才能进行访问！",
				// 	icon: 'none'
				// })
				showFailToast("页面【" + path + "】需要进行登录，才能进行访问！");
				// 无权访问
				router.push({
					path: config.HAS_TOKEN_BACK_URL
				})
				return false
			}
		}
	}
}
