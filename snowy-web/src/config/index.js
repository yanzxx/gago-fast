const DEFAULT_CONFIG = {
	// 首页地址
	DASHBOARD_URL: '/index',

	// 系统本地缓存字段前缀（localStorage 、sessionStorage、cookie等），注意在单ip网络环境中不同项目之前的缓存数据污染问题
	STORAGE_PREFIX: 'GAGO_',

	// 配置选择要缓存数据的方式： localStorage、sessionStorage
	storageType: 'localStorage',

	// 接口地址
	API_URL: import.meta.env.VITE_API_BASEURL || `${window.location.protocol}//${window.location.host}/api`,

	// 请求超时
	TIMEOUT: 60000,

	// TokenName // Authorization
	TOKEN_NAME: 'token',

	// Token前缀，注意最后有个空格，如不需要需设置空字符串 // Bearer
	TOKEN_PREFIX: '',

	// 追加其他头
	HEADERS: {},

	// 请求是否开启缓存
	REQUEST_CACHE: false,

	// 布局 经典：classical，双排菜单：doublerow
	SNOWY_LAYOUT: 'doublerow',

	// 菜单是否折叠
	SNOWY_MENU_COLLAPSE: false,

	// 模块坞
	SNOWY_MODULE_UNFOLD_OPEN: true,

	// 是否展示首页大屏
	SNOWY_HOME_PAGE_OPEN: true,

	// 是否默认跳转到首页大屏
	SNOWY_GO_TO_HOME_PAGE: false,

	// 是否开启多标签
	SNOWY_LAYOUT_TAGS_OPEN: true,

	// 是否开启展示面包屑
	SNOWY_BREADCRUMD_OPEN: false,

	// 顶栏是否应用主题色
	SNOWY_TOP_HANDER_THEME_COLOR_OPEN: false,

	// 顶栏主题色通栏
	SNOWY_TOP_HANDER_THEME_COLOR_SPREAD: false,

	// 侧边菜单是否排他展开
	SNOWY_SIDE_UNIQUE_OPEN: true,

	// 语言
	LANG: 'zh-cn',

	// 主题颜色
	COLOR: '#1F8A70',

	// 默认整体主题
	SNOWY_THEME: 'light',

	// 整体表单风格
	SNOWY_FORM_STYLE: 'drawer',

	// 成功色
	success: '#52c41a',

	// 警告色
	warning: '#faad14',

	// 错误色
	error: '#f5222f',

	// 系统基础配置，这些是数据库中保存起来的
	SYS_BASE_CONFIG: {
		// 默认logo
		SNOWY_SYS_LOGO: '/img/jiage.png',
		// 后端接口地址
		SNOWY_SYS_API_URL: import.meta.env.VITE_API_BASEURL,
		// 默认跳转路由
		FAST_DEFAULT_SHOW_ROUTE: 'index',
		// 系统加载中LOGO
		FAST_LOADING_IMAGE: '/img/logo.png',
		// 系统ICO
		FAST_TBA_ICON: '/favicon.ico',
		// 系统名称
		SNOWY_SYS_NAME: 'Jiage',
		// 版本
		SNOWY_SYS_VERSION: '2.0',
		// 版权
		SNOWY_SYS_COPYRIGHT: 'fast ©2022 Created by 佳格',
		// 版权跳转URL
		SNOWY_SYS_COPYRIGHT_URL: '',
		// 默认文件存储
		SNOWY_SYS_DEFAULT_FILE_ENGINE: 'LOCAL',
		// 是否开启验证码
		SNOWY_SYS_DEFAULT_CAPTCHA_OPEN: 'false',
		// 默认重置密码
		SNOWY_SYS_DEFAULT_PASSWORD: '123456'
	},
}
window.DEFAULT_CONFIG_LIST = DEFAULT_CONFIG

export default DEFAULT_CONFIG
