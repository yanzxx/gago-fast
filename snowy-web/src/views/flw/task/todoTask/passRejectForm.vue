<template>
	<a-modal
		:title="buttonType === 'COMPLETE' ? '填写意见' : '驳回意见'"
		:visible="visible"
		:destroy-on-close="true"
		:width="600"
		@ok="onOk"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="意见：" name="comment">
				<a-textarea v-model:value="formData.comment" placeholder="请填写意见" :auto-size="{ minRows: 3, maxRows: 6 }" />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="passRejectForm">
	import { required } from '@/utils/formRules'
	const emit = defineEmits({ ok: null })
	// 填写意见
	const visible = ref(false)
	const formRef = ref({})
	const formData = ref({})
	const buttonType = ref()
	const formRules = {}
	const onOpen = (type, processEnableCommentRequired) => {
		buttonType.value = type
		visible.value = true
		if (processEnableCommentRequired) {
			formRules.comment = [required('请输入意见')]
		}
	}
	const onOk = () => {
		formRef.value.validate().then(() => {
			emit('ok', formData.value)
			onClose()
		})
	}
	// 关闭小抽屉
	const onClose = () => {
		formData.value.comment = ''
		visible.value = false
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
