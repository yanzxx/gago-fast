<template>
	<uni-popup ref="popupRef" type="bottom" safeArea>
		<view class="container">
			<uni-list :border="false">
				<uni-list-item title="编辑" class="item" :clickable="true" @click="edit">
				</uni-list-item>
				<uni-list-item v-if="store.getters.envKey != record.key" title="刪除" class="item" :clickable="true" @click="del">
				</uni-list-item>
				<uni-list-item title="取消" class="item" :clickable="true" @click="cancel">
				</uni-list-item>
			</uni-list>
		</view>
	</uni-popup>
</template>

<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance
	} from "vue";
	import XEUtils from "xe-utils"
	import store from '@/store'
	import modal from '@/plugins/modal.js'
	
	const emits = defineEmits(['handleOk'])

	// 删除弹出ref
	const delPopRef = ref()
	// 弹出ref
	const popupRef = ref()
	// 打开
	let record = ref({})
	const open = (data) => {
		record.value = data
		popupRef.value.open("bottom")
	}
	// 编辑
	const edit = () => {
		uni.navigateTo({
			url: '/pages/config/form?record='+ encodeURIComponent(JSON.stringify(record.value))
		})
		popupRef.value.close()
	}
	// 删除
	const del = () => {
		modal.confirm(`是否确认删除【${ record.value.name }】环境？`).then(() => {
			let obj = XEUtils.clone(store.getters.allEnv, true)
			delete obj[record.value.key]				
			store.commit('SET_allEnv', obj)
			popupRef.value.close()
		})
	}
	// 取消
	const cancel = () => {
		popupRef.value.close()
	}
	defineExpose({
		open
	})
</script>
<style lang="scss">
	.container {
		margin: 15upx;
		border-radius: 5upx;
		padding: 5upx;
		background-color: $uni-white;
		.item {
			text-align:center;
		}
	}
</style>