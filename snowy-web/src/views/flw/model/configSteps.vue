<template>
	<div>
		<a-card class="steps-card" :bordered="false">
			<a-row class="xn-row">
				<a-col :span="6"></a-col>
				<a-col :span="12">
					<a-steps :current="current">
						<a-step v-for="item in steps" :key="item.title" :title="item.title" />
					</a-steps>
				</a-col>
				<a-col :span="6">
					<div style="float: right">
						<a-button :disabled="current !== 1" style="margin-left: 8px" @click="prev"> 上一步 </a-button>
						<a-button :disabled="current === 2" type="primary" style="margin-left: 8px" @click="next"> 继续 </a-button>
						<a-button type="primary" danger ghost style="margin-left: 8px" @click="emit('previousPage')">
							关闭
						</a-button>
					</div>
				</a-col>
			</a-row>
		</a-card>

		<div v-if="current === 0">
			<form-design ref="formDesign" :form-value="workFlowParame.formValue" />
		</div>
		<div v-if="current === 1">
			<model-design ref="modelDesign" />
			<!--:model-value="workFlowParame.modelValue"-->
		</div>
		<div v-if="current === 2">
			<a-card>
				<a-result status="success" title="操作成功" sub-title="此时您可以一键部署此流程啦">
					<template #extra>
						<a-space size="middle">
							<a-button v-if="current > 0" style="margin-left: 8px" @click="prev">上一步</a-button>
							<a-button
								v-if="current == steps.length - 1"
								type="primary"
								:loading="submitLoading"
								@click="seveDeployment"
								>部署运行</a-button
							>
						</a-space>
					</template>
				</a-result>
			</a-card>
		</div>
	</div>
</template>
<script setup>
	import { message } from 'ant-design-vue'
	import { defineComponent, ref, nextTick } from 'vue'
	import modelApi from '@/api/flw/modelApi'
	import ModelDesign from './modelDesign.vue'
	import FormDesign from './formDesign.vue'
	import tool from '@/utils/tool'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import { cloneDeep } from 'lodash-es'
	const emit = defineEmits({ previousPage: null })

	const current = ref(0)
	const formDesign = ref(null)
	const modelDesign = ref(null)

	const recordData = ref({})
	const tableJson = ref([])
	const submitLoading = ref(false)
	// 整个工作流的数据模型外壳
	const workFlowParame = {
		formValue: {},
		modelValue: cloneDeep(config.nodeModel.node)
	}
	// 打开这个界面
	const configSteps = (record) => {
		// 全局赋值
		recordData.value = record
		workFlowParame.formValue = record.formJson == null ? false : JSON.parse(record.formJson)
		if (record.processJson == null) {
			// 给模型设计器基础外壳给值
			workFlowParame.modelValue.id = tool.snowyUuid() // record.id
			workFlowParame.modelValue.type = 'process'
			workFlowParame.modelValue.title = record.name
			workFlowParame.modelValue.properties.configInfo = JSON.parse(
				JSON.stringify(config.nodeConfigInfo.processConfigInfo)
			)

			// 创建参与人节点
			const userTaskNode = cloneDeep(config.nodeModel.node)
			userTaskNode.id = tool.snowyUuid()
			userTaskNode.title = '发起申请'
			userTaskNode.dataLegal = false
			userTaskNode.type = 'startTask'

			// 创建发起人模型
			const startEventNode = cloneDeep(config.nodeModel.node)
			startEventNode.id = tool.snowyUuid()
			startEventNode.title = '开始'
			startEventNode.type = 'startEvent'
			startEventNode.dataLegal = true
			startEventNode.childNode = userTaskNode

			// 装回全局模型中
			workFlowParame.modelValue.childNode = startEventNode
		} else {
			workFlowParame.modelValue = JSON.parse(record.processJson)
		}
		// 给表单设计器给值，表单value，table数据结构
		formDesign.value.setValue(workFlowParame.formValue, JSON.parse(recordData.value.tableJson))
	}
	// 下一步
	const next = () => {
		current.value++
		// 判断是哪一步
		if (current.value === 1) {
			const formValue = formDesign.value.getValue()
			if (!formDataVerification(formValue)) {
				current.value--
				return
			}
			workFlowParame.formValue = formValue
			nextTick(() => {
				// modelDesign.value.setValue(workFlowParame.modelValue);
				modelDesign.value.setValue(workFlowParame, recordData.value)
				modelDesign.value.setFormFieldListValue(workFlowParame.formValue.list)
			})
		}
		if (current.value === 2) {
			const modelValue = modelDesign.value.getValue()
			workFlowParame.modelValue = modelValue
			// 这里完成之后，需要调用保存接口，传给后端
			recordData.value.formJson = JSON.stringify(workFlowParame.formValue)
			// 校验整体的model设计后的json
			if (!modelDataVerification(workFlowParame.modelValue)) {
				current.value--
				return
			}
			recordData.value.processJson = JSON.stringify(workFlowParame.modelValue)
			// 保存进去
			modelApi.submitForm(recordData.value, !recordData.value.id).then(() => {})
		}
	}
	// 上一步
	const prev = () => {
		current.value--
		if (current.value === 0) {
			nextTick(() => {
				formDesign.value.setValue(workFlowParame.formValue, JSON.parse(recordData.value.tableJson))
			})
		}
		if (current.value === 1) {
			nextTick(() => {
				modelDesign.value.setValue(workFlowParame, recordData.value)
				modelDesign.value.setFormFieldListValue(workFlowParame.formValue.list)
			})
		}
	}
	// 分布步骤数据
	const steps = [
		{
			title: '表单设计',
			content: '表单设计'
		},
		{
			title: '模型设计',
			content: '模型设计'
		},
		{
			title: '完成',
			content: '您的流程已经完成，是否立即部署运行'
		}
	]
	// 部署流程按钮
	const seveDeployment = () => {
		const param = {
			id: recordData.value.id
		}
		submitLoading.value = true
		modelApi
			.modelDeploy(param)
			.then(() => {
				message.success('部署成功')
				submitLoading.value = false
				// 恢复到第一步
				current.value = 0
				// 告诉上一个界面成功了
				emit('previousPage')
			})
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 表单设计器选择好的内容校验
	const formDataVerification = (formValue) => {
		let error = 0
		// 判断是否包含动态表格batch
		if (formValue.list.length > 0) {
			for (let i = 0; i < formValue.list.length; i++) {
				const items = formValue.list[i]
				// 处理判断动态表格中
				if (formValue.list[i].type === 'batch') {
					// 判断动态表格中是否有内容，没内容提示并不能下一步
					if (items.list.length > 0) {
						for (let j = 0; j < items.list.length; j++) {
							const itemsObj = items.list[j]
							if (!itemsObj.selectColumn) {
								error++
								message.warning('请选择动态表格中的子表字段')
								break
							}
						}
					} else {
						error++
						message.warning('包含了动态表格,但未选择表格中内容')
						break
					}
				} else {
					const type = items.type
					// alert text divider card grid table
					if (
						(type !== 'alert') &
						(type !== 'text') &
						(type !== 'divider') &
						(type !== 'card') &
						(type !== 'grid') &
						(type !== 'table')
					) {
						// 其他项判断确认选择了表，并且选择了字段，但凡有一个没选视为不过
						if (!items.selectTable || !items.selectColumn) {
							error++
							message.warning('名称为：' + items.label + ' 的组件未选择表或字段')
							break
						}
					}
				}
			}
		} else {
			error++
			message.warning('未选择任何组件并赋予字段')
		}
		return error === 0
	}
	// 模型整体校验
	const modelDataVerification = (modelValue) => {
		if (JSON.stringify(modelValue).indexOf('dataLegal":false') !== -1) {
			message.warning('流程未配置完成')
			return false
		}
		return true
	}
	// 抛出钩子
	defineExpose({
		configSteps
	})
	defineComponent({
		ModelDesign,
		FormDesign
	})
</script>
<style scoped>
	.steps-card {
		margin-top: -12px;
		margin-left: -12px;
		margin-right: -12px;
		margin-bottom: 10px;
		padding-top: -10px;
	}
	.steps-action {
		text-align: right;
	}
	.xn-row {
		margin-bottom: -10px;
		margin-top: -10px;
	}
</style>
