<template>
	<view v-if="params.url">
		<web-view :webview-styles="webviewStyles" :src="`${params.url}`"></web-view>
	</view>
</template>

<script setup>
	import {
		nextTick,
		reactive,
		ref,
		watch,
		getCurrentInstance
	} from "vue"
	import {
		onLoad,
		onShow,
		onReady,
		onPullDownRefresh,
		onReachBottom
	} from "@dcloudio/uni-app"

	// const props = defineProps({
	// 	src: {
	// 		type: [String],
	// 		default: null
	// 	},
	// })

	const params = ref({})
	const webviewStyles = ref({
		progress: {
			color: "#FF3333"
		}
	})
	onLoad((options) => {
		params.value = options
		if (options.title) {
			uni.setNavigationBarTitle({
				title: options.title
			})
		}
	})
</script>