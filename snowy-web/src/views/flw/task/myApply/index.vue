<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="name" label="流程名称">
						<a-input v-model:value="searchFormState.name" placeholder="请输入流程名称" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-button type="primary" @click="table.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="table"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-button danger @click="revokeBatchTaskMyApply()">撤回</a-button>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a @click="myApplyDetailProcessRef.onOpen(record)">详情</a>
					<a-divider type="vertical" v-if="record.state === '运行中'" />
					<a-popconfirm title="确定要撤回流程吗？" @confirm="revokeProcess(record)">
						<a-button type="link" danger size="small" v-if="record.state === '运行中'">撤回</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<myApplyDetaileProcess ref="myApplyDetailProcessRef" />
</template>

<script setup name="flwMyApply">
	import { message } from 'ant-design-vue'
	import processMyApi from '@/api/flw/processMyApi'
	import myApplyDetaileProcess from './detaileProcess.vue'

	let searchFormState = reactive({})
	const searchFormRef = ref()
	const table = ref()
	const startProcessRef = ref()
	const myApplyDetailProcessRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '标题',
			dataIndex: 'title',
			ellipsis: true
		},
		{
			title: '流水号',
			dataIndex: 'sn',
			ellipsis: true
		},
		{
			title: '定义名称',
			dataIndex: 'processDefinitionName',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '定义版本',
			dataIndex: 'processDefinitionVersion',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '发起组织',
			dataIndex: 'initiatorOrgName',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '发起职位',
			dataIndex: 'initiatorPositionName',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '发起时间',
			dataIndex: 'startTime',
			ellipsis: true
		},
		{
			title: '结束时间',
			dataIndex: 'endTime',
			ellipsis: true
		},
		{
			title: '耗时',
			dataIndex: 'durationInfo',
			ellipsis: true
		},
		{
			title: '当前节点',
			dataIndex: 'currentActivityNames',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '状态',
			dataIndex: 'state'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	let selectedRowKeys = ref([])
	// 列表选择配置
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
	const loadData = (parameter) => {
		return processMyApi.processMyPage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		table.value.refresh(true)
	}
	// 撤回
	const revokeProcess = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		processMyApi.processRevoke(params).then(() => {
			table.value.refresh(true)
		})
	}

	// 批量撤回
	const revokeBatchTaskMyApply = () => {
		if (selectedRowKeys.value.length < 1) {
			message.warning('请选择一条或多条数据')
			return false
		}
		const params = selectedRowKeys.value.map((m) => {
			return {
				id: m
			}
		})
		processMyApi.processRevoke(params).then(() => {
			table.value.clearRefreshSelected()
		})
	}
</script>
