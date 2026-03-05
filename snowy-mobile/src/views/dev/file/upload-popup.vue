<template>
	<uni-popup ref="popupRef" type="bottom" safeArea>
		<view class="container">
			<uni-segmented-control style="margin-bottom: 20px;" :current="curView" :values="['动态', '本地', '阿里云', '腾讯']"
				styleType="button" activeColor="#007aff" @clickItem="(e) => {
					if (curView != e.currentIndex) {
						curView = e.currentIndex
					}
				}">
			</uni-segmented-control>
			<view v-show="curView === 0">
				<!-- fileMediatype="image" -->
				<snowy-file-picker mode="grid" uploadType="Dynamic"></snowy-file-picker>
			</view>
			<view v-show="curView === 1">
				<snowy-file-picker mode="grid" uploadType="Local"></snowy-file-picker>
			</view>
			<view v-show="curView === 2">
				<snowy-file-picker mode="grid" uploadType="Aliyun"></snowy-file-picker>
			</view>
			<view v-show="curView === 3">
				<snowy-file-picker mode="grid" uploadType="Tencent"></snowy-file-picker>
			</view>
			<!-- 表单中使用文件上传示例 -->
			<!-- <uni-forms ref="formRef" :model="formData" label-position="top" labelWidth="75px">
				<uni-forms-item label="文件" name="file" required :rules="[{ required: true, errorMessage: '请输入账号' }]">
					<snowy-file-picker v-model="formData.file" fileMediatype="image" mode="list" file-mediatype="all"></snowy-file-picker>
				</uni-forms-item>
			</uni-forms>
			<button class="btn-sub" type="primary" @click="submit">提交</button> -->
		</view>
	</uni-popup>
</template>
<script setup>
	import {
		ref
	} from "vue"
	import SnowyFilePicker from '@/components/snowy-file-picker/index.vue'
	const emits = defineEmits(['handleOk'])
	const curView = ref(0)
	const popupRef = ref()
	const open = () => {
		popupRef.value.open("bottom")
		emits('handleOk')
	}

	// 表单中使用文件上传示例
	// const formRef = ref()
	// const formData = ref({
	// 	file: ['1664989145386385410','1665706408393490433', '1665710300330115073']
	// })
	// const submit = () => {
	// 	console.log(formData.value)
	// 	formRef.value.validate().then(res => {})
	// 	uni.$emit('formBack', {})
	// 	uni.navigateBack({
	// 		delta: 1
	// 	})
	// }
	defineExpose({
		open
	})
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