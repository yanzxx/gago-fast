import loginApi from '@/api/auth/loginApi'
import userCenterApi from '@/api/sys/userCenterApi'
import dictApi from '@/api/dev/dictApi'
import router from '@/router'
import tool from '@/utils/tool'
import { message } from 'ant-design-vue'
import { useGlobalStore } from '@/store'
import config from '@/config/index'
import { inject } from 'vue'
import checkPasswordExpire from '@/passwordExpire'
const reload = inject('reload')

export const afterLogin = async (loginToken) => {
	tool.data.set('TOKEN', loginToken)
	// 获取登录的用户信息
	const loginUser = await loginApi.getLoginUser()
	const globalStore = useGlobalStore()
	globalStore.setUserInfo(loginUser)
	tool.data.set('USER_INFO', loginUser)
	let baseConfig = null
	if (config.storageType === 'localStorage') {
		baseConfig = localStorage.getItem(config.STORAGE_PREFIX + 'SNOWY_SYS_BASE_CONFIG')
	} else {
		baseConfig = sessionStorage.getItem(config.STORAGE_PREFIX + 'SNOWY_SYS_BASE_CONFIG')
	}
	const FAST_DEFAULT_SHOW_ROUTE = JSON.parse(baseConfig).FAST_DEFAULT_SHOW_ROUTE

	// 获取用户绑定的角色信息
	const roles = loginUser.roles
	let roleIdList = []
	if (roles) {
		roleIdList = roles.map((item) => {
			return item.id
		})
	}
	const spaMenu = await userCenterApi.roleOwnSpaTreeSelector({ id: roleIdList.join(',') })
	tool.data.set('SPA', spaMenu)
	// 获取用户的菜单
	const menu = await userCenterApi.userLoginMenu()
	let indexMenu = menu[0].children[0].path
	tool.data.set('MENU', menu)
	// 重置系统默认应用
	tool.data.set('SNOWY_MENU_MODULE_ID', menu[0].id)
	if (!!tool.data.get('LAST_VIEWS_PATH')) {
		// 如果有缓存，将其登录跳转到最后访问的路由
		indexMenu = tool.data.get('LAST_VIEWS_PATH')
	}
	// 如果存在退出后换新账号登录，进行重新匹配，匹配无果则默认首页
	if (menu) {
		let routerTag = 0
		menu.forEach((item) => {
			if (item.children) {
				if (JSON.stringify(item.children).indexOf(indexMenu) > -1) {
					routerTag++
				}
			}
		})
		if (routerTag === 0) {
			indexMenu = menu[0].children[0].path
		}
	}
	if (FAST_DEFAULT_SHOW_ROUTE !== '' && FAST_DEFAULT_SHOW_ROUTE != null) {
		await router.replace({
			path: '/' + FAST_DEFAULT_SHOW_ROUTE
		})
	} else {
		await router.replace({
			path: indexMenu
		})
	}
	dictApi.dictTree().then((data) => {
		// 设置字典到store中
		tool.data.set('DICT_TYPE_TREE_DATA', data)
	})

	// 检查密码是否过期
	checkPasswordExpire()
}

export const afterChangeSystemImportantData = async (loginToken) => {
	// 获取登录的用户信息
	const loginUser = await loginApi.getLoginUser()
	const globalStore = useGlobalStore()
	globalStore.setUserInfo(loginUser)
	tool.data.set('USER_INFO', loginUser)

	// 获取用户绑定的角色信息
	const roles = loginUser.roles
	let roleIdList = []
	if (roles) {
		roleIdList = roles.map((item) => {
			return item.id
		})
	}
	const spaMenu = await userCenterApi.roleOwnSpaTreeSelector({ id: roleIdList.join(',') })
	tool.data.set('SPA', spaMenu)
	// 获取用户的菜单
	const menu = await userCenterApi.userLoginMenu()
	tool.data.set('MENU', menu)
	setTimeout(() => {
		window.location.reload()
	}, 500)
}
