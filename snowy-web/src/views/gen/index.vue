<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2" v-if="indexShow">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="业务名" name="busName">
						<a-input v-model:value="searchFormState.busName" placeholder="请输入业务名关键词" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="功能名" name="functionName">
						<a-input v-model:value="searchFormState.functionName" placeholder="请输入功能名关键词" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="类名" name="className">
						<a-input v-model:value="searchFormState.className" placeholder="请输入类名关键词" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="table.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false" v-if="indexShow">
		<s-table
			ref="table"
			:columns="columns"
			:data="loadDate"
			:expand-row-by-click="true"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:row-selection="options.rowSelection"
			:toolConfig="{ refresh: true, height: true, columnSetting: true, striped: false }"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="openConfig()"> 新建 </a-button>
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchCodeGen" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'tablePrefix'">
					{{ tablePrefixFilter(record.tablePrefix) }}
				</template>
				<template v-if="column.dataIndex === 'generateType'">
					{{ generateTypeFilter(record.generateType) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="genPreviewRef.onOpen(record)">预览</a>
					<a-divider type="vertical" />
					<a-button type="link" size="small" @click="execGenFormRef.onOpen(record)">生成</a-button>
					<a-divider type="vertical" />
					<a @click="openConfig(record)">配置</a>
					<a-divider type="vertical" />
					<a-popconfirm title="删除此信息？" @confirm="deleteCodeGen(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<steps v-else ref="stepsRef" @successful="table.refresh(true)" @closed="closeConfig()" />
	<genPreview ref="genPreviewRef" />
	<execGenForm ref="execGenFormRef" @saveCodeEmit="execGen" @nullCodeEmit="nullCodeHandler"></execGenForm>
</template>

<script setup name="genIndex">
	import { message } from 'ant-design-vue'
	import downloadUtil from '@/utils/downloadUtil'
	import steps from './steps.vue'
	import genPreview from './preview.vue'
	import execGenForm from './execGenForm.vue'
	import genBasicApi from '@/api/gen/genBasicApi'
	import {afterChangeSystemImportantData} from "@/views/auth/login/util";

	const table = ref()
	const indexShow = ref(true)
	const stepsRef = ref()
	const genPreviewRef = ref()
	const execGenFormRef = ref()
	const searchFormRef = ref()
	let searchFormState = reactive({})

	const columns = [
		{
			title: '业务名',
			dataIndex: 'busName',
			ellipsis: true
		},
		{
			title: '功能名',
			dataIndex: 'functionName',
			ellipsis: true
		},
		{
			title: '类名',
			dataIndex: 'className',
			ellipsis: true
		},
		{
			title: '包名',
			dataIndex: 'packageName',
			ellipsis: true
		},
		{
			title: '作者',
			dataIndex: 'authorName',
			ellipsis: true
		},
		{
			title: '移除表前缀',
			dataIndex: 'tablePrefix',
			ellipsis: true
		},
		{
			title: '生成方式',
			dataIndex: 'generateType',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '220px'
		}
	]

	// 表格查询 返回 Promise 对象
	const loadDate = (parameter) => {
		return genBasicApi.basicPage(Object.assign(parameter, searchFormState)).then((data) => {
			return data
		})
	}
	// 列表选择配置
	let selectedRowKeys = ref([])
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	const generateTypeFilter = (text) => {
		const array = [
			{
				label: '压缩包',
				value: 'ZIP'
			},
			{
				label: '项目内',
				value: 'PRO'
			}
		]
		return array.find((f) => f.value === text).label
	}
	const tablePrefixFilter = (text) => {
		const array = [
			{
				label: '移除',
				value: 'Y'
			},
			{
				label: '不移除',
				value: 'N'
			}
		]
		return array.find((f) => f.value === text).label
	}

	// 生成代码
	const execGen = (record) => {
		const param = {
			id: record.id,
			genBackendFileNames:
				record.genBackendFileNames && record.genBackendFileNames.length > 0 ? record.genBackendFileNames : null,
			genFrontFileNames:
				record.genFrontFileNames && record.genFrontFileNames.length > 0 ? record.genFrontFileNames : null,
			genSqlFileNames: record.genSqlFileNames && record.genSqlFileNames.length > 0 ? record.genSqlFileNames : null
		}
		if (record.generateType === 'PRO') {
			genBasicApi.basicExecGenPro(param).then(() => {
				message.success('操作成功')
				table.value.refresh()
				execGenFormRef.value.onClose()
				afterChangeSystemImportantData()
			})
		} else {
			// 下载压缩包
			genBasicApi.basicExecGenBiz(param).then((res) => {
				message.success('操作成功，下载中...')
				downloadUtil.resultDownload(res)
				execGenFormRef.value.onClose()
			})
		}
	}
	// 为空提示
	const nullCodeHandler = () => {
		message.warning('至少勾选一个生成的代码')
	}
	// 重置查询条件
	const reset = () => {
		searchFormRef.value.resetFields()
		table.value.refresh(true)
	}
	// 删除
	const deleteCodeGen = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		genBasicApi.basicDelete(params).then(() => {
			table.value.refresh()
		})
	}
	// 批量删除
	const deleteBatchCodeGen = (params) => {
		genBasicApi.basicDelete(params).then(() => {
			table.value.refresh()
		})
	}
	// 打开配置界面
	const openConfig = (record) => {
		indexShow.value = false
		nextTick(() => {
			stepsRef.value.configSteps(record)
		})
	}
	// 关闭配置界面
	const closeConfig = () => {
		indexShow.value = true
	}
</script>

<style scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
</style>
