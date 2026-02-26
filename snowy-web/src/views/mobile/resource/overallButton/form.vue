<template>
	<a-modal
		v-model:visible="visible"
		:title="formData.id ? '编辑全局按钮' : '增加全局按钮'"
		:width="550"
		:mask-closable="false"
		:destroy-on-close="true"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="显示名称：" name="title">
				<a-input v-model:value="formData.title" placeholder="请输入显示名称" allow-clear />
			</a-form-item>
			<a-form-item label="编码：" name="code">
				<a-input v-model:value="formData.code" placeholder="请输入按钮编码" allow-clear />
			</a-form-item>
			<a-form-item label="排序:" name="sortCode">
				<a-input-number style="width: 100%" v-model:value="formData.sortCode" :max="100" :min="0" />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup>
	import { required } from '@/utils/formRules'
	import overallButtonApi from '@/api/mobile/resource/overallButtonApi'
	import { ref } from 'vue'
	// 默认是关闭状态
	let visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	// 打开弹框
	const onOpen = (record, buttonData) => {
		visible.value = true
		formData.value = record ? record : {}
		if (buttonData) {
			formData.value = Object.assign({}, buttonData)
		}
	}
	// 关闭弹框
	const onClose = () => {
		formRef.value.resetFields()
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		title: [required('请输入全局按钮名称')],
		code: [required('请输入全局按钮编码')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			const defParam = {
				category: 'BUTTON',
			}
			const param = Object.assign(defParam, formData.value)
			overallButtonApi.addGlobalButtonData(param, !formData.value.id).then(() => {
				onClose()
				emit('successful')
			})
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
