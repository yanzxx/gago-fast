<template>
	<div class="homePage" id="homePage">
		<div @click="jumpToPageUrl('/sys/user')">跳转到个人信息页面</div>
		<div @click="jumpToPageUrl('/index')">跳转到目录页</div>
	</div>
</template>

<script setup>
	import { onMounted } from 'vue'
	import router from '@/router'
	onMounted(() => {
		//配置
	})

	const jumpToPageUrl = (urlPath) => {
		router.replace({
			path: urlPath
		})
	}
</script>

<style lang="less">
	.homePage {
		width: 100%;
		height: 100%;
		color: #fff;
		display: flex;
		justify-content: center;
		align-items: center;

		div {
			margin-left: 30px;
			cursor: pointer;
		}
	}
</style>
