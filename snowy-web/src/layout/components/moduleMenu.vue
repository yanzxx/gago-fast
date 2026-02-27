<template>
	<div class="layout-items-center centerBox" v-if="moduleUnfoldOpen">
		<a-menu
			v-model:selectedKeys="selectedKeys"
			mode="horizontal"
			v-if="menu && menu.length > 1"
			class="module-menu"
			id="moduleMenu"
		>
			<a-menu-item
				v-for="item in menu"
				:key="item.id"
				class="module-menu-item"
				@click.stop.prevent="moduleClick(item)"
			>
				<template #icon>
					<component v-if="item && item.icon && item.icon.indexOf('icon') == -1" :is="item.icon" />
					<i v-else class="iconfont" :class="item.icon"></i>
				</template>
				<a
					v-if="item.category && item.category === 'SPA' && item.menuType === 'LINK'"
					:href="item.path"
					target="_blank"
					@click.stop="() => {}"
					style="color: inherit"
					>{{ item.title }}</a
				>
				<span v-else :title="selectedKeys">{{ item.title }}</span>
			</a-menu-item>
		</a-menu>
	</div>
	<div v-else class="panel-item hidden-sm-and-down">
		<a-popover v-if="menu.length > 1" placement="bottomLeft">
			<template #content>
				<a-row :gutter="[0, 5]" class="module-row">
					<div v-for="item in menu" :key="item.id">
						<a-col :span="6">
							<a-tag class="module-card" :color="item.color" @click="moduleClick(item.id)">
								<component :is="item.icon" class="module-card-icon" />
								<div class="module-card-font">{{ item.title }}</div>
							</a-tag>
						</a-col>
					</div>
				</a-row>
			</template>
			<appstore-outlined />
		</a-popover>
	</div>
</template>

<script setup>
	import router from '@/router'
	import tool from '@/utils/tool'
	import { globalStore } from '@/store'
	import { watch, onMounted } from 'vue'
	import { storeToRefs } from 'pinia'
	const store = globalStore()
	const { moduleUnfoldOpen, topHanderThemeColorOpen, normalSelectedModule } = storeToRefs(store)
	const moduleBackColor = ref(topHanderThemeColorOpen)
	// 监听目录是否折叠
	watch(moduleUnfoldOpen, (newValue) => {
		nextTick(() => {
			setModuleBackColor()
		})
	})
	// 监听是否开启了顶栏颜色
	watch(topHanderThemeColorOpen, (newValue) => {
		moduleBackColor.value = newValue
		setModuleBackColor()
	})
	watch(normalSelectedModule, (newValue) => {
		emit('switchModuleWithNoChangePage', newValue)
		selectedKeys.value = [tool.data.get('SNOWY_MENU_MODULE_ID')]
	})
	const emit = defineEmits({ switchModule: null, switchModuleWithNoChangePage: null })
	const parseExtJson = (extJson) => {
		if (!extJson) return {}
		try {
			const parsed = JSON.parse(extJson)
			return parsed && typeof parsed === 'object' ? parsed : {}
		} catch (e) {
			return {}
		}
	}
	const menuList = router.getMenu()
	const spaList = tool.data.get('SPA') || []
	const spa = spaList.filter((item) => {
		item.extJsonObj = parseExtJson(item.extJson)
		if (item.menuType === 'IFRAME') {
			item.path = `/i/${item.name}`
		}
		return item.name !== 'userCenter' && item.name !== 'index' && !!item.extJsonObj.showModuleArea
	})
	const menu = spa.concat(menuList)
	menu.sort(function (a, b) {
		const aSortCode = a.sortCode
		const bSortCode = b.sortCode
		if (aSortCode < bSortCode) {
			return -1
		}
		if (aSortCode > bSortCode) {
			return 1
		}
		return 0
	})
	const selectedKeys = ref([tool.data.get('SNOWY_MENU_MODULE_ID')])
	const homePageOpen = ref(store.homePageOpen)
	const moduleClick = (item) => {
		console.log('item', item)
		if (item.category === 'SPA') {
			if (item.menuType === 'LINK') {
				window.open(item.path)
				setTimeout(() => {
					selectedKeys.value = [tool.data.get('SNOWY_MENU_MODULE_ID')]
				}, 500)
			} else {
				const id = item.id
				tool.data.set('SNOWY_MENU_MODULE_ID', id)
				router.push({ path: item.path })
			}
			const extJson = item.extJson
			let showLeftMenuArea = true
			if (extJson) {
				const extJsonObj = parseExtJson(extJson)
				showLeftMenuArea = extJsonObj.showLeftMenuArea == null ? true : extJsonObj.showLeftMenuArea
			}
			if (item.menuType === 'MENU') {
				store.setIsLeftMenu(showLeftMenuArea)
			}
		} else {
			const id = item.id
			emit('switchModule', id)
			tool.data.set('SNOWY_MENU_MODULE_ID', id)
			selectedKeys.value = [tool.data.get('SNOWY_MENU_MODULE_ID')]
		}
	}
	onMounted(() => {
		setModuleBackColor()
	})
	// 设置背景色
	const setModuleBackColor = () => {
		if (moduleUnfoldOpen.value) {
			try {
				const moduleMenu = document.getElementById('moduleMenu')
				moduleBackColor.value
					? moduleMenu.classList.add('module-menu-color')
					: moduleMenu.classList.remove('module-menu-color')
			} catch (err) {
				console.log('设置背景色： 报错', err)
			}
			setSelectedKeys()
		}
	}
	// 设置选中
	const setSelectedKeys = () => {
		// 顶部应用列表显示出来默认的，不这么实现不会显示的
		const menuData = tool.data.get('MENU')
		recursionEstimateHighLightMenu(menuData)
		// moduleBackColor.value
		// 	? (selectedKeys.value = new Array([]))
		// 	: (selectedKeys.value = [tool.data.get('SNOWY_MENU_MODULE_ID')])
	}

	// 递归根据浏览器地址设置高亮
	const recursionEstimateHighLightMenu = (menuData) => {
		for (let i = 0; i < menuData.length; i++) {
			const menuItem = menuData[i]
			if (menuData.category !== 'SPA') {
				if (menuItem.children && menuItem.children.length > 0) {
					recursionEstimateHighLightMenu(menuItem.children)
				} else {
					if (window.location.href.indexOf(menuItem.path) > -1) {
						selectedKeys.value = [menuItem.module]
						tool.data.set('SNOWY_MENU_MODULE_ID', menuItem.module)
						selectedKeys.value = [tool.data.get('SNOWY_MENU_MODULE_ID')]
						emit('switchModuleWithNoChangePage', menuItem.module)
						return
					}
				}
			}
		}
	}
	// 跳转到首页
	const jumpToHomePageHandle = () => {
		router.push({
			path: '/homePage'
		})
	}
</script>

<style lang="less">
	.homePageIcon {
		width: 132px;
		height: 68px;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding-top: 7px;
		cursor: pointer;
		color: #fff;
	}

	.homePageIcon:hover {
		color: #d1f1e7;
	}
	.module-row {
		max-width: 357px;
	}
	.module-card {
		width: 80px;
		height: 80px;
		background: linear-gradient(135deg, #1f8a70, #2ea386);
		text-align: center;
		align-items: center;
		cursor: pointer;
		border-radius: 2px;
	}
	.module-card-icon {
		color: white;
		font-size: 20px;
		margin-top: 20px;
	}
	.module-card-font {
		color: white;
		font-size: 8px;
	}
	.ant-menu-horizontal > .ant-menu-item::after,
	.ant-menu-horizontal > .ant-menu-submenu::after {
		content: none;
	}
	.module-menu {
		line-height: 1;
		border-bottom: 0px;
		overflow: hidden;
		background-color: transparent !important;
		display: flex;
		align-items: center;
		min-height: 72px;
	}
	.module-menu-item {
		height: 72px !important;
		line-height: 1 !important;
		margin: 0 4px !important;
		padding: 0 18px !important;
		border-radius: 12px 12px 0 0 !important;
		color: rgba(255, 255, 255, 0.92);
		display: flex !important;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		gap: 8px;
		transition: all 0.2s ease;

		.iconfont,
		.ant-menu-item-icon {
			font-size: 16px;
			line-height: 1;
			height: 16px;
		}

		.ant-menu-title-content {
			margin-left: 0 !important;
			line-height: 1;
			font-size: 14px;
			font-weight: 600;
		}

		&:hover {
			color: #ffffff !important;
			background: rgba(255, 255, 255, 0.16) !important;
		}

		&.ant-menu-item-selected {
			background: linear-gradient(to bottom, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7)) !important;
			color: #1f8a70 !important;
			box-shadow: 0 8px 16px rgba(10, 70, 55, 0.15);
		}
	}
	.module-menu-color {
		color: white;
	}
	.nMenuItemStyle {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}
</style>
