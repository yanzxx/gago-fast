<template>
	<xn-form-container
		title="审批流程"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
		:push="false"
	>
		<a-spin :spinning="spinning">
			<a-tabs v-model:activeKey="activeKey">
				<a-tab-pane key="flowForm" tab="表单">
					<snowy-form-build ref="formBuild" :value="initiatorFormJson" :outputString="false" />
				</a-tab-pane>
				<a-tab-pane key="flowChart" tab="流程图">
					<flowChart v-model="initiatorModelJson" />
				</a-tab-pane>
				<a-tab-pane key="flowTrunRecord" tab="流转记录">
					<timelineForm :commentList="commentList" />
				</a-tab-pane>
			</a-tabs>
		</a-spin>

		<template #footer v-if="activeKey === 'flowForm' || buttonInfo.length === 0">
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
		<passRejectForm ref="passRejectFormRef" @ok="passRejectFormOk" />
		<taskTurnForm ref="taskTurnFormRef" @successful="onClose()" />
	</xn-form-container>
</template>

<script setup name="todoProcess">
	import { message } from 'ant-design-vue'
	import { nextTick } from 'vue'
	import processApi from '@/api/flw/processApi'
	import taskApi from '@/api/flw/taskApi'
	import flowChart from '@/components/XnWorkflow/chart/index.vue'
	import workFlowUtils from '@/components/XnWorkflow/nodes/utils/index'
	import timelineForm from '../../process/timelineForm.vue'
	import passRejectForm from './passRejectForm.vue'
	import taskTurnForm from './taskTurnForm.vue'

	// 选中的tab
	const activeKey = ref('flowForm')
	// 整体界面loading
	const spinning = ref(false)
	// 这个界面三个标签界面的值
	const initiatorModelJson = ref({})
	const initiatorFormJson = ref([])
	const initiatorDataJson = ref()
	const commentList = ref([])

	const formBuild = ref()
	// 默认是关闭状态
	let visible = $ref(false)
	const emit = defineEmits({ successful: null })

	const recordData = ref({})
	const modelData = ref({})
	const formData = ref({})
	const buttonInfo = ref()
	const buttonSpinning = ref(false)
	const passRejectFormRef = ref()
	const taskTurnFormRef = ref()

	// 自动获取宽度，默认获取浏览器的宽度的90%
	const drawerWidth = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) * 0.5
	// 打开抽屉
	const onOpen = (record) => {
		// 缓存一条记录
		recordData.value = record
		// currentActivityId
		// 获取此界面的值
		const param = {
			id: record.processInstanceId
		}
		spinning.value = true
		processApi
			.processDetail(param)
			.then((data) => {
				commentList.value = data.commentList
				if (!data.initiatorDataJson) {
					message.warning('此流程表单无任何数据，无法办理，请联系管理员！')
					return
				}

				let modelJson = JSON.parse(data.initiatorModelJson)
				modelData.value = modelJson
				// 取节点（用到按钮权限跟字段），并且给节点set一个json，也就是我们的审批记录
				let childNode = getChildNode(modelJson, record, data.commentList)
				// 设置模型，等上面这个方法走完，再执行这个
				initiatorModelJson.value = modelJson
				// 给按钮赋值，这里就不管按钮了，界面上循环展示就行
				buttonInfo.value = childNode.properties.buttonInfo
				// 设置表单 并且 处理显示字段
				initiatorFormJson.value = workFlowUtils.convFormFilid(
					JSON.parse(data.initiatorFormJson),
					childNode.properties.fieldInfo
				)
				const formBuildData = Object.values(JSON.parse(data.initiatorDataJson))[0]
				nextTick(() => {
					// 设置表单参数
					formBuild.value.setData(formBuildData)
				})
			})
			.finally(() => {
				spinning.value = false
			})
		visible = true
	}

	// 取节点（用到按钮权限跟字段），并且给节点set一个json，也就是我们的审批记录
	const getChildNode = (modelJson, record, dataList) => {
		let result = {}
		let traverse = (obj) => {
			// obj.properties.commentList = []
			if (obj.type === 'exclusiveGateway' || obj.type === 'parallelGateway') {
				// 网关下分2步走
				if (obj.conditionNodeList) {
					obj.conditionNodeList.forEach((item) => {
						traverse(item)
					})
				}
				if (obj.childNode) {
					traverse(obj.childNode)
				}
			} else {
				if (obj.id === record.currentActivityId) {
					result = obj
				} else {
					// 这里追加记录
					if (dataList) {
						dataList.forEach((item) => {
							// 给对应的节点
							if (item.activityId === obj.id) {
								// 增加多个对象
								obj.properties.commentList = []
								obj.properties.commentList.push(item)
							}
						})
					}
					// 再穿下去
					if (obj.childNode) {
						traverse(obj.childNode)
					}
				}
			}
		}
		// 传入流程的这个
		traverse(modelJson)
		return result
	}

	// 关闭抽屉
	const onClose = () => {
		emit('successful')
		activeKey.value = 'flowForm'
		visible = false
	}

	const buttonType = ref()
	// 点击按钮
	const buttonClick = (key) => {
		buttonType.value = key
		// 因为转办需要选择人员，无法跟同意驳回使用一个组件
		if (key === 'TURN') {
			taskTurnFormRef.value.onOpen(recordData.value)
		} else {
			getDataValue()
		}
	}
	// 同意
	const getDataValue = () => {
		formBuild.value.getData().then((values) => {
			formData.value = values
			// 如果是重新提交，不弹框
			if (buttonType.value === 'SUBMIT') {
				const params = {
					id: recordData.value.id,
					dataJson: JSON.stringify(values)
				}
				buttonSpinning.value = true
				taskApi
					.taskAdjust(params)
					.then(() => {
						onClose()
					})
					.finally(() => {
						buttonSpinning.value = false
					})
			} else {
				// 弹出底部抽屉，进行填写同意意见
				passRejectFormRef.value.onOpen(
					buttonType.value,
					modelData.value.properties.configInfo.processEnableCommentRequired
				)
			}
		})
	}
	// 意见框返回，并调用接口，返回数据
	const passRejectFormOk = (value) => {
		value.id = recordData.value.id
		value.dataJson = JSON.stringify(formData.value)
		buttonSpinning.value = true
		// 同意
		if (buttonType.value === 'COMPLETE') {
			taskApi
				.taskPass(value)
				.then(() => {
					onClose()
				})
				.finally(() => {
					buttonSpinning.value = false
				})
		}
		// 驳回
		if (buttonType.value === 'REJECT') {
			taskApi
				.taskReject(value)
				.then(() => {
					onClose()
				})
				.finally(() => {
					buttonSpinning.value = false
				})
		}
	}

	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
