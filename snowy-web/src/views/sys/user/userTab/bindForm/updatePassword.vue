<template>
	<xn-form-container title="修改密码" :width="550" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formState" :rules="rules" layout="vertical">
			<a-form-item label="旧密码：" name="password" has-feedback>
				<a-input
					v-model:value="formState.password"
					placeholder="请输入原密码"
					type="password"
					allow-clear
					autocomplete="off"
				/>
			</a-form-item>
			<a-form-item label="新密码：" name="newPassword" has-feedback>
				<a-input
					v-model:value="formState.newPassword"
					placeholder="请输入新密码"
					type="password"
					allow-clear
					autocomplete="off"
				/>
				<template #extra>密码复杂度要求至少8位，由数字、大小写字母、特殊字符组成</template>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="updatePassword">
	import { required } from '@/utils/formRules'
	import userCenterApi from '@/api/sys/userCenterApi'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	let visible = ref(false)
	const formRef = ref()
	// 表单数据
	const formState = ref({})
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = () => {
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}

	// 密码复杂度验证
	const validatePassword = async (_rule, value) => {
		if (value.length < 8) {
			return Promise.reject('密码长度至少8位')
		}
		if (!/[0-9]/.test(value)) {
			return Promise.reject('密码必须包含数字')
		}
		if (!/[a-z]/.test(value)) {
			return Promise.reject('密码必须包含小写字母')
		}
		if (!/[A-Z]/.test(value)) {
			return Promise.reject('密码必须包含大写字母')
		}
		if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value)) {
			return Promise.reject('密码必须包含特殊字符')
		}
		return Promise.resolve()
	}

	// 默认要校验的
	const rules = {
		password: [required('请输入原密码')],
		checkPassword: [required('请再次输入原密码')],
		newPassword: [
			{ required: true, message: '请输入新密码' },
			{ validator: validatePassword, trigger: 'change' }
		]
	}

	// 提交数据
	const onSubmit = async () => {
		const values = await formRef.value.validateFields()
		submitLoading.value = true
		userCenterApi
			.userUpdatePassword(values)
			.then(() => {
				formRef.value.resetFields()
				visible.value = false
				emit('successful')
			})
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>

<style scoped></style>
