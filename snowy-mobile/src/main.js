import { createApp } from 'vue';
import './style.css';
import './styles/index.scss';
import App from './App.vue';

import store from './store';
import router from './router';

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import Vant from 'vant';
import 'vant/lib/index.css';

import './assets/icon/jiage/iconfont.css'
import './assets/icon/snowy/line/iconfont.css'
import './assets/icon/snowy/filled/iconfont.css'

import jweixin from 'weixin-js-sdk'

// import './utils/rem.js';

const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component)
}
app.use(store);
app.use(router);
app.use(ElementPlus);
app.use(Vant);
app.config.globalProperties.$wx = jweixin
app.mount('#app');
