<template>
	<div class="home-container">
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
</script>

<style lang="scss">
	.home-container {
		border-radius: 5px;
    margin: 6px 6px 60px 6px;
	}
</style>
