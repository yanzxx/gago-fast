import { createRouter, createWebHistory } from 'vue-router'
import { notification } from 'ant-design-vue'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import systemRouter from './systemRouter'
import { afterEach, beforeEach } from './scrollBehavior'
import whiteListRouters from './whiteList'
import userRoutes from '@/config/route'
import tool from '@/utils/tool'
import { cloneDeep } from 'lodash-es'
const modules = import.meta.glob('/src/views/**/**.vue')
import { globalStore, searchStore } from '@/store'
import ssoCasApi from '@/api/auth/ssoCasApi'
import { afterLogin } from '@/views/auth/login/util'

// 进度条配置
NProgress.configure({ showSpinner: false, speed: 500 })

let spaList = []
if (tool.data.get('SPA')) {
	const spa = tool.data.get('SPA').filter((item) => {
		item.extJsonObj = JSON.parse(item.extJson)
		return item.name !== 'userCenter' && item.name !== 'index' && item.extJsonObj.screenFull && item.menuType === 'MENU'
	})

	spaList = spa.map((item) => {
		return {
			path: item.path,
			component: modules['/src/views/' + item.component + '.vue'],
			meta: {
				title: item.title
			}
		}
	})
}

// 系统特殊路由
const routes_404 = [
	{
		path: '/:pathMatch(.*)*',
		hidden: true,
		component: () => import('@/layout/other/404.vue')
	}
]
// 系统路由
const routes = [...systemRouter, ...whiteListRouters, ...routes_404, ...spaList]

const router = createRouter({
	history: createWebHistory(),
	routes
})

// 设置标题
// document.title = sysBaseConfig.SNOWY_SYS_NAME

// 判断是否已加载过动态/静态路由
const isGetRouter = ref(false)

// 白名单校验
const exportWhiteListFromRouter = (router) => {
	const res = []
	for (const item of router) res.push(item.path)
	return res
}
const whiteList = exportWhiteListFromRouter(whiteListRouters)

router.beforeEach(async (to, from, next) => {
	NProgress.start()
	const store = globalStore()

	const sysBaseConfig = tool.data.get('SNOWY_SYS_BASE_CONFIG') || store.sysBaseConfig
	// 动态标题
	document.title = to.meta.title
		? `${to.meta.title} - ${sysBaseConfig.SNOWY_SYS_NAME}`
		: `${sysBaseConfig.SNOWY_SYS_NAME}`

	// 检查URL中是否有ticket参数（SSO回调处理）
	const ticket = tool.getUrlParam('ticket')
	if (ticket) {
		try {
			// 验证ticket并获取登录信息
			const loginToken = await ssoCasApi.loginTicket2_0(ticket)
			console.log('SSO ticket:', ticket)
			if (loginToken) {
				// 清除URL中的ticket参数
				tool.removeUrlParam('ticket')
				// 执行登录后的操作，afterLogin会自动处理路由跳转
				await afterLogin(loginToken)
				// 取消当前导航，因为afterLogin已经处理了路由跳转
				next(false)
				return
			}
		} catch (error) {
			console.error('SSO ticket验证失败:', error)
			// 清除URL中的ticket参数
			tool.removeUrlParam('ticket')
			// 验证失败，跳转到登录页
			next({
				path: '/login'
			})
			return false
		}
	}

	// 过滤白名单
	if (whiteList.includes(to.path)) {
		next()
		// NProgress.done()
		return false
	}

	const token = tool.data.get('TOKEN')
	if (to.path === '/login') {
		// 当用户输入了login路由，将其跳转首页即可
		if (token) {
			next({
				path: '/'
			})
			return false
		}
		// 删除路由(替换当前layout路由)
		router.addRoute(routes[0])
		isGetRouter.value = false
		next()
		return false
	} else {
		// 这里需要使用 localStorage 保存登录之前要访问的页面
		tool.data.set('LAST_VIEWS_PATH', to.fullPath)
	}
	if (!token) {
		next({
			path: '/login'
		})
		return false
	}
	// 整页路由处理
	if (to.meta.fullpage) {
		to.matched = [to.matched[to.matched.length - 1]]
	}
	// 加载动态/静态路由
	if (!isGetRouter.value) {
		const apiMenu = tool.data.get('MENU') || []
		if (apiMenu.length === 0) {
			// 创建默认模块，显示默认菜单
			apiMenu[0] = cloneDeep(userRoutes.module[0])
			const userMenu = userRoutes.menu
			const childrenApiMenu = apiMenu[0].children
			apiMenu[0].children = [...userMenu, ...childrenApiMenu]
		}
		let menuRouter = filterAsyncRouter(apiMenu)
		menuRouter = flatAsyncRoutes(menuRouter)
		menuRouter.forEach((item) => {
			router.addRoute('layout', item)
		})
		const search_store = searchStore()
		search_store.init(menuRouter)
		isGetRouter.value = true
		next({ ...to, replace: true })
		return false
	} else {
		let showLeftMenuArea = true
		const extJson = to.meta.extJson
		if (extJson) {
			const extJsonObj = JSON.parse(extJson)
			showLeftMenuArea = extJsonObj.showLeftMenuArea == null ? true : extJsonObj.showLeftMenuArea
		}
		if (extJson) {
			store.setIsLeftMenu(showLeftMenuArea)
		}
		if (to.meta.breadcrumb && to.meta.breadcrumb.length > 0) {
			const moduleId = to.meta.breadcrumb[0].meta.id
			tool.data.set('SNOWY_MENU_MODULE_ID', moduleId)
			globalStore().setNormalSelectedModule(moduleId)
		} else {
			next()
		}
	}
	beforeEach(to, from)
	next()
})

router.afterEach((to, from) => {
	afterEach(to, from)
	NProgress.done()
})

router.onError((error) => {
	NProgress.done()
	notification.error({
		message: '路由错误',
		description: error.message
	})
})

// 入侵追加自定义方法、对象
router.getMenu = () => {
	let apiMenu = tool.data.get('MENU') || []
	// 增加固定路由
	if (apiMenu.length === 0) {
		// 创建默认模块，显示默认菜单
		apiMenu[0] = cloneDeep(userRoutes.module[0])
		const userMenu = userRoutes.menu
		const childrenApiMenu = apiMenu[0].children
		apiMenu[0].children = [...userMenu, ...childrenApiMenu]
	}
	return apiMenu
}

// 转换
const filterAsyncRouter = (routerMap) => {
	const accessedRouters = []
	const _routerMap = routerMap.filter(
		(item) =>
			item.category === 'MODULE' ||
			item.category === 'MENU' ||
			(item.category === 'SPA' && (!item.meta.extJson || !JSON.parse(item.meta.extJson).showModuleArea))
	)
	_routerMap.forEach((item) => {
		item.meta = item.meta ? item.meta : {}
		// 处理外部链接特殊路由
		if (item.meta.type === 'iframe' || item.menuType === 'IFRAME') {
			item.meta.url = item.path
			item.meta.type = 'iframe'
			item.path = `/i/${item.name}`
		}
		item.meta.id = item.id
		item.meta.extJson = item.extJson
		// MAP转路由对象
		const route = {
			path: item.path,
			name: item.name,
			meta: item.meta,
			redirect: item.redirect,
			children: item.children ? filterAsyncRouter(item.children) : null,
			component: loadComponent(item.component)
		}
		accessedRouters.push(route)
	})
	return accessedRouters
}
const loadComponent = (component) => {
	if (component) {
		if (component.includes('/')) {
			return modules[`/src/views/${component}.vue`]
		}
		return modules[`/src/views/${component}/index.vue`]
	} else {
		return () => import(/* @vite-ignore */ `/src/layout/other/empty.vue`)
	}
}

// 路由扁平化
const flatAsyncRoutes = (routes, breadcrumb = []) => {
	const res = []
	routes.forEach((route) => {
		const tmp = { ...route }
		if (tmp.children) {
			const childrenBreadcrumb = [...breadcrumb]
			childrenBreadcrumb.push(route)
			const tmpRoute = { ...route }
			tmpRoute.meta.breadcrumb = childrenBreadcrumb
			delete tmpRoute.children
			res.push(tmpRoute)
			const childrenRoutes = flatAsyncRoutes(tmp.children, childrenBreadcrumb)
			childrenRoutes.map((item) => {
				res.push(item)
			})
		} else {
			const tmpBreadcrumb = [...breadcrumb]
			tmpBreadcrumb.push(tmp)
			tmp.meta.breadcrumb = tmpBreadcrumb
			res.push(tmp)
		}
	})
	return res
}

export default router
