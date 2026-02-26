<template>
	<a-modal
		title="任务转办"
		:width="500"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:mask="false"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="用户：" name="userId">
				<a-input v-model:value="formData.userId" v-show="false" />
				<a-button type="link" style="padding-left: 0px" @click="openSelector(formData.userId)">选择</a-button>
				<a-tag v-if="formData.userId && formData.userName" color="orange" closable @close="closeUserTag">{{
					formData.userName
				}}</a-tag>
			</a-form-item>

			<a-form-item label="意见：" name="comment">
				<a-textarea
					v-model:value="formData.comment"
					placeholder="请输入转办意见"
					allow-clear
					:auto-size="{ minRows: 3, maxRows: 6 }"
				/>
			</a-form-item>
			<a-form-item label="附件：" name="attachment">
				<XnUpload ref="uploadRef" uploadMode="drag" @uploadDone="xnUploadDone" />
			</a-form-item>
		</a-form>
		<user-selector-plus
			ref="UserSelectorPlus"
			page-url="/flw/model/userSelector"
			org-url="/flw/model/orgTreeSelector"
			:radio-model="true"
			@onBack="userBack"
		/>
	</a-modal>
</template>

<script setup name="taskTurnForm">
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import taskApi from '@/api/flw/taskApi'
	import userSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	let visible = ref(false)
	const formRef = ref()
	const uploadRef = ref()
	const UserSelectorPlus = ref()
	// 表单数据
	const formData = ref({})

	// 打开抽屉
	const onOpen = (record) => {
		formData.value.id = record.id
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		userId: [required('请选择人员')]
	}
	// 打开人员选择器，选择人员
	const openSelector = (id) => {
		let checkedUserIds = []
		checkedUserIds.push(id)
		UserSelectorPlus.value.showUserPlusModal(checkedUserIds)
	}
	// 人员选择器回调
	const userBack = (value) => {
		if (value.length > 0) {
			formData.value.userId = value[0].id
			formData.value.userName = value[0].name
		} else {
			formData.value.userId = ''
		}
	}
	// 通过小标签删除
	const closeUserTag = () => {
		formData.value.userId = ''
		formData.value.userName = ''
	}
	// 上传组件回调传输文件
	const xnUploadDone = (data) => {
		// 因为目前只支持单个附件，所以是数组0的里面取
		formData.value.attachmentName = data[0].name
		formData.value.attachmentUrl = data[0].url
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			taskApi.taskTurn(formData.value).then(() => {
				onClose()
				emit('successful')
			})
		})
	}
	defineExpose({
		onOpen
	})
</script>
