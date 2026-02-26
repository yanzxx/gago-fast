import { createApp } from 'vue'
import Antd from 'ant-design-vue'
import { createPinia } from 'pinia'

import './style/index.less'
import snowy from './snowy'
import i18n from './locales'
import router from './router'
import App from './App.vue'
import './tailwind.css'
import './assets/icons/jiage/iconfont.css'

import SnowyFormDesign from './packages/index'
import Cmp from './components/CustomComponent/index.vue'
import './passwordExpire'

SnowyFormDesign.setFormDesignConfig({
	title: '测试自定义字段',
	list: [
		{
			type: 'demo', // 表单类型
			label: '自定义组件', // 标题文字
			icon: 'icon-gallery',
			component: Cmp,
			options: {
				defaultValue: undefined,
				multiple: false,
				disabled: false,
				width: '100%',
				clearable: true,
				placeholder: '请选择',
				showSearch: false
			},
			model: '',
			key: '',
			rules: [
				{
					required: false,
					message: '必填项'
				}
			]
		}
	],
	uploadFile: '',
	uploadImage: 'http://localhost:89/dev/file/uploadDynamicReturnUrl',
	uploadFileName: 'file',
	uploadImageName: 'file',
	uploadFileData: { data: 1545 },
	uploadImageData: { data: 1545 },
	uploadFileHeaders: { data: 1545 },
	uploadImageHeaders: { token: '123654' },
	orgTree: [],
	userList: []
})

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(Antd)
app.use(i18n)
app.use(snowy)
app.use(SnowyFormDesign)

// 挂载app
app.mount('#app')
