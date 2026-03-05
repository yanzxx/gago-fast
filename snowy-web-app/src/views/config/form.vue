<template>
	<view class="container">
		<uni-forms ref="formRef" :model="formData" label-position="top" labelWidth="75px">
			<uni-forms-item label="环境KEY" name="key" required :rules="[{ required: true, errorMessage: '请输入环境key' }]">
				<uni-easyinput v-model="formData.key" placeholder="请输入环境key"></uni-easyinput>
			</uni-forms-item>
			<uni-forms-item label="环境名称" name="name" required :rules="[{ required: true, errorMessage: '请输入环境名称' }]">
				<uni-easyinput v-model="formData.name" placeholder="请输入环境名称"></uni-easyinput>
			</uni-forms-item>
			<uni-forms-item label="baseUrl" name="baseUrl" required :rules="[{ required: true, errorMessage: '请输入baseUrl' }]">
				<uni-easyinput v-model="formData.baseUrl" placeholder="请输入baseUrl"></uni-easyinput>
			</uni-forms-item>
			<uni-forms-item label="tenantDomain" name="tenantDomain" required :rules="[{ required: true, errorMessage: '请输入tenantDomain' }]">
				<uni-easyinput v-model="formData.tenantDomain" placeholder="请输入tenantDomain"></uni-easyinput>
			</uni-forms-item>
		</uni-forms>
		<button class="btn-sub" type="primary" @click="submit">提交</button>
	</view>
</template>

<script setup>
	import {
		nextTick,
		reactive,
		ref
	} from "vue"
	import store from '@/store'
	import XEUtils from "xe-utils"
	import {
		onLoad,
		onShow,
		onReady,
		onPullDownRefresh,
		onReachBottom
	} from "@dcloudio/uni-app"
	const formRef = ref()
	let formData = ref({})
	
	// 加載
	onLoad((option) => {
		if(!option.record){
			return
		}
		formData.value = JSON.parse(decodeURIComponent(option.record));
	})
	
	const submit = () => {
		formRef.value.validate().then(res => {
			let obj = XEUtils.clone(store.getters.allEnv, true)
			obj[formData.value.key] = {
				name: formData.value.name,
				baseUrl: formData.value.baseUrl,
				tenantDomain: formData.value.tenantDomain,
			}
			store.commit('SET_allEnv', obj)
			uni.navigateBack({
				delta: 1
			})
			
		})
	}
</script>

<style lang="scss">
	.container {
		margin: 15upx;
		border-radius: 5upx;
		padding: 25upx;
		background-color: $uni-white;
		.btn-sub {
			background-color: $uni-primary;
		}
	}
</style>