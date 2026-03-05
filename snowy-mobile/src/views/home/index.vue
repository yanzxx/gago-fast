<template>
	<div class="home-container">
		<div class="home-title-bar">
			<div class="system-name">{{ systemName }}</div>
		</div>
		<item v-for="(it, i) in homeConfigs" :index="i" :key="i" :code="it.code" :isShow="it.isShow"></item>
	</div>
</template>
<script setup>
	import Item from "./item.vue"
	import store from '@/store'
	import {
		computed
	} from "vue";
	
	// 快捷用户菜单
	const homeConfigs = computed(() => {
		const rawConfigs = store.getters.homeConfigs || []
		// 仅保留当前首页允许的模块，防止历史缓存把已下线模块渲染出来
		return rawConfigs.filter(item => item && item.code === 'chart')
	})

	const systemName = computed(() => {
		return store.getters.sysBaseConfig?.SNOWY_SYS_NAME || '系统首页'
	})
</script>

<style lang="scss">
	.home-container {
		border-radius: 5px;
		margin: 6px 6px 60px 6px;
	}

	.home-title-bar {
		background: linear-gradient(135deg, #1f8a70 0%, #16745f 100%);
		border-radius: 10px;
		padding: 12px 14px;
		margin-bottom: 8px;
		box-shadow: 0 6px 14px rgba(31, 138, 112, 0.22);
	}

	.system-name {
		font-size: 17px;
		font-weight: 700;
		color: #ffffff;
		letter-spacing: 0.5px;
	}
</style>
