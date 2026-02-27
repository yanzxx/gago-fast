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
	},
	{
		path: '/comprehensiveSupervision',
		component: () => import('@/views/home/index.vue'),
		meta: {
			title: '首页'
		}
	}
]

export default routes
