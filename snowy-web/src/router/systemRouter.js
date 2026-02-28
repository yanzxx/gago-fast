import config from '@/config'
import tool from '@/utils/tool'

// 系统路由
const routes = [
	{
		name: 'layout',
		path: '/',
		component: () => import('@/layout/index.vue'),
		redirect: tool.data.get('MENU') ? tool.data.get('MENU')[0].children[0].path : config.DASHBOARD_URL,
		children: [
			{
				path: '/comprehensiveSupervision',
				name: 'comprehensiveSupervision',
				component: () => import('@/views/livestock-board/index.vue'),
				meta: {
					title: '综合监管视图'
				}
			},
			{
				path: '/livestockBoard',
				name: 'livestockBoard',
				component: () => import('@/views/livestock-board/index.vue'),
				meta: {
					title: '综合监管视图'
				}
			},
			{
				path: '/productManage',
				name: 'productManage',
				component: () => import('@/views/product-manage/index.vue'),
				meta: {
					title: '金融产品管理'
				}
			},
			{
				path: '/loanAdministration',
				name: 'loanAdministration',
				component: () => import('@/views/loan-administration/index.vue'),
				meta: {
					title: '贷款管理'
				}
			},
			{
				path: '/afterLoanAdministration',
				name: 'afterLoanAdministration',
				component: () => import('@/views/after-loan-administration/index.vue'),
				meta: {
					title: '贷后管理'
				}
			},
			{
				path: '/bxProductManage',
				name: 'bxProductManage',
				component: () => import('@/views/bx-product-manage/index.vue'),
				meta: {
					title: '保险产品管理'
				}
			},
			{
				path: '/tbManage',
				name: 'tbManage',
				component: () => import('@/views/tb-manage/index.vue'),
				meta: {
					title: '投保管理'
				}
			},
			{
				path: '/bhManage',
				name: 'bhManage',
				component: () => import('@/views/bh-manage/index.vue'),
				meta: {
					title: '保后管理'
				}
			},
			{
				path: '/lpManage',
				name: 'lpManage',
				component: () => import('@/views/lp-manage/index.vue'),
				meta: {
					title: '理赔管理'
				}
			}
		]
	},
	{
		path: '/login',
		component: () => import('@/views/auth/login/login.vue'),
		meta: {
			title: '登录'
		}
	},
	{
		path: '/findpwd',
		component: () => import('@/views/auth/findPwd/index.vue'),
		meta: {
			title: '找回密码'
		}
	},
	{
		path: '/callback',
		component: () => import('@/views/auth/login/callback.vue'),
		meta: {
			title: '三方登录'
		}
	}
]

export default routes
