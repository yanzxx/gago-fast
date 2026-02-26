<template>
	<xn-form-container
		title="发起流程"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="flowForm" tab="表单">
				<snowy-form-build ref="formBuild" :value="jsonData" :config="formConfig" />
			</a-tab-pane>
			<a-tab-pane key="flowChart" tab="流程图">
				<flowChart v-model="modelDesignData" />
			</a-tab-pane>
		</a-tabs>
		<template #footer v-if="activeKey === 'flowForm'">
			<a-spin :spinning="buttonSpinning">
				<span v-for="button in buttonInfo" :key="button.key">
					<a-button
						style="margin-left: 8px"
						:type="button.type"
						@click="buttonClick(button.key)"
						v-if="button.value === 'SHOW'"
						>{{ button.label }}</a-button
					>
				</span>
			</a-spin>
		</template>
		<userPosSelector ref="userPosSelectorRef" @ok="startEvent" />
	</xn-form-container>
</template>

<script setup name="newProcess">
	import { message } from 'ant-design-vue'
	import tool from '@/utils/tool'
	import processMyApi from '@/api/flw/processMyApi'
	import flowChart from '@/components/XnWorkflow/chart/index.vue'
	import userPosSelector from './userPosSelector.vue'
	import workFlowUtils from '@/components/XnWorkflow/nodes/utils/index'
	const apiUrl = tool.data.get('SNOWY_SYS_BASE_CONFIG').SNOWY_SYS_API_URL + '/dev/file/uploadDynamicReturnUrl'
	const token = tool.data.get('TOKEN')
	const formConfig = ref({
		uploadFile: apiUrl, // 上传文件地址
		uploadImage: apiUrl, // 上传图片地址
		uploadFileName: '', // 上传文件name
		uploadImageName: '', // 上传图片name
		uploadFileData: { data: 223 }, // 上传文件额外参数
		uploadImageData: { data: 223 }, // 上传图片额外参数
		uploadFileHeaders: { token: token }, // 上传文件请求头部
		uploadImageHeaders: { token: token } // 上传图片请求头部
	})
	// 选中的tab
	const activeKey = ref('flowForm')

	// 模型设计器的值
	const modelDesignData = ref({})
	const jsonData = ref([])
	const formBuild = ref()
	// 默认是关闭状态
	let visible = $ref(false)
	const emit = defineEmits({ successful: null })

	// 职位选择ref定义
	const userPosSelectorRef = ref()
	const formData = ref({})
	const buttonInfo = ref()
	const buttonSpinning = ref(false)
	// 此界面全局modelId,也就是选中的
	const modelId = ref('')

	// 自动获取宽度，默认获取浏览器的宽度的90%
	const drawerWidth = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) * 0.5

	// 打开抽屉
	const onOpen = (record) => {
		// 预先设置modelId
		modelId.value = record.id
		modelDesignData.value = JSON.parse(record.processJson)
		const thisNode = JSON.parse(record.processJson).childNode.childNode
		buttonInfo.value = thisNode.properties.buttonInfo
		if (thisNode.properties.fieldInfo.length === 0 || thisNode.properties.buttonInfo.length === 0) {
			message.warning('该节点按钮与字段配置不正确，请联系管理员')
			return
		}
		// 转换数据
		jsonData.value = workFlowUtils.convFormFilid(JSON.parse(record.formJson), thisNode.properties.fieldInfo)
		visible = true
	}
	// 关闭抽屉
	const onClose = () => {
		modelId.value = ''
		visible = false
	}
	// 点击按钮
	const buttonClick = (key) => {
		if (key === 'SUBMIT') {
			onSubmit()
		}
		if (key === 'SAVE') {
			save()
		}
	}
	// 保存草稿
	const save = () => {
		// 使用getData函数获取数据
	}

	// 验证并提交数据
	const onSubmit = () => {
		formBuild.value.getData().then((values) => {
			formData.value = values
			// 打开职位选择
			userPosSelectorRef.value.onOpen()
		})
	}
	const startEvent = (recrod) => {
		const userInfo = tool.data.get('USER_INFO')
		// 拼接JSON
		const param = {
			modelId: modelId.value,
			dataJson: JSON.stringify(formData.value),
			initiator: userInfo.id,
			initiatorName: userInfo.name,
			initiatorOrgId: recrod.orgId,
			initiatorOrgName: recrod.orgName,
			initiatorPositionId: recrod.positionId,
			initiatorPositionName: recrod.positionName
		}
		// 调用接口发起流程
		buttonSpinning.valueOf = true
		processMyApi
			.processStart(param)
			.then(() => {
				onClose()
			})
			.finally(() => {
				buttonSpinning.valueOf = false
			})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
