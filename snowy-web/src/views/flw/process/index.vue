<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="searchKey" label="发起人关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入发起人姓名关键词" />
					</a-form-item>
				</a-col>
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
				<a-space>
					<a-button type="primary" @click="revokeBatchProcess()" v-if="hasPerm('flwProcessBatchRevoke')">撤回</a-button>
					<a-button @click="endBatchProcess()" v-if="hasPerm('flwProcessBatchEnd')">终止</a-button>
					<a-button @click="activeBatchProcess()" v-if="hasPerm('flwProcessBatchActive')">激活</a-button>
					<a-button @click="suspendBatchProcess()" v-if="hasPerm('flwProcessBatchSuspend')">挂起</a-button>
					<xn-batch-delete
						:selectedRowKeys="selectedRowKeys"
						@batchDelete="deleteBatchProcess"
						v-if="hasPerm('flwProcessBatchDelete')"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'title'">
					<ellipsis :length="10" tooltip>
						{{ record.title }}
					</ellipsis>
				</template>
				<template v-if="column.dataIndex === 'abstractTitle'">
					<ellipsis :length="10" tooltip>
						{{ record.abstractTitle }}
					</ellipsis>
				</template>

				<template v-if="column.dataIndex === 'state'">
					<a-badge status="success" v-if="record.state === '运行中'" />
					<a-badge status="error" v-else-if="record.state === '已终止'" />
					<a-badge status="warning" v-else-if="record.state === '已挂起'" />
					<a-badge status="default" v-else />
					<span>{{ record.state }}</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="detaileProcessRef.onOpen(record)" v-if="hasPerm('flwProcessDetail')">详情</a>
					<a-divider type="vertical" v-if="hasPerm(['flwProcessDetail', 'flwProcessDelete'], 'and')" />
					<a-popconfirm title="确认删除此流程吗？" @confirm="processRecordDelete(record)">
						<a-button type="link" danger size="small" v-if="hasPerm('flwProcessDelete')">删除</a-button>
					</a-popconfirm>
					<a-divider
						type="vertical"
						v-if="hasPerm(['flwProcessDetail', 'flwProcessDelete'], 'and') && record.state !== '已结束'"
					/>

					<a-dropdown>
						<a class="ant-dropdown-link" v-if="record.state === '运行中' || record.state === '已挂起'">
							更多
							<DownOutlined />
						</a>
						<template #overlay>
							<a-menu>
								<a-menu-item
									v-if="hasPerm('flwProcessActiveSuspend') && (record.state === '运行中' || record.state === '已挂起')"
								>
									<a v-if="record.state === '运行中'" @click="editActiveSuspendStatus(record)">挂起</a>
									<a v-else-if="record.state === '已挂起'" @click="editActiveSuspendStatus(record)">激活</a>
								</a-menu-item>
								<a-menu-item v-if="hasPerm('flwProcessRevoke') && record.state === '运行中'">
									<a @click="processRecordRevoke(record)">撤回</a>
								</a-menu-item>
								<a-menu-item v-if="hasPerm('flwProcessEnd') && record.state === '运行中'">
									<a-popconfirm title="确定终止此流程？" @confirm="processRecordEnd(record)">
										<a>终止</a>
									</a-popconfirm>
								</a-menu-item>

								<a-menu-item v-if="hasPerm('flwProcessTurn') && record.state === '运行中'">
									<a @click="processTurnFormRef.onOpen(record)">转办</a>
								</a-menu-item>
							</a-menu>
						</template>
					</a-dropdown>
				</template>
			</template>
		</s-table>
		<detaileProcess ref="detaileProcessRef" />
		<process-turn-form ref="processTurnFormRef" @successful="table.refresh()" />
	</a-card>
</template>

<script setup name="flwProcess">
	import processApi from '@/api/flw/processApi'
	import { message } from 'ant-design-vue'
	import detaileProcess from './detaileProcess.vue'
	import processTurnForm from './processTurnForm.vue'

	let searchFormState = reactive({})
	const searchFormRef = ref()
	const table = ref()
	const detaileProcessRef = ref()
	const stateLoading = ref(false)
	let selectedRowKeys = ref([])
	const processTurnFormRef = ref()

	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '标题',
			dataIndex: 'title'
		},
		{
			title: '摘要',
			dataIndex: 'abstractTitle'
		},
		{
			title: '定义名称',
			dataIndex: 'processDefinitionName',
			ellipsis: true
		},
		{
			title: '定义版本',
			dataIndex: 'processDefinitionVersion',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '发起人',
			dataIndex: 'initiatorName'
		},
		{
			title: '发起人组织',
			dataIndex: 'initiatorOrgName',
			ellipsis: true
		},
		{
			title: '发起人职位',
			dataIndex: 'initiatorPositionName',
			ellipsis: true
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
			title: '当前节点',
			dataIndex: 'currentActivityNames',
			ellipsis: true
		},
		{
			title: '当前办理人',
			dataIndex: 'assignees',
			ellipsis: true
		},
		{
			title: '状态',
			dataIndex: 'state',
			width: '100px'
		}
	]
	if (
		hasPerm([
			'flwProcessDetail',
			'flwProcessEnd',
			'flwProcessDelete',
			'flwProcessActiveSuspend',
			'flwProcessRevoke',
			'flwProcessTurn'
		])
	) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			width: '180px'
		})
	}
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
		return processApi.processMonitorPage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		table.value.refresh(true)
	}
	// 撤回
	const processRecordRevoke = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		processApi.processRevoke(params).then(() => {
			table.value.refresh()
		})
	}
	// 终止
	const processRecordEnd = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		processApi.processEnd(params).then(() => {
			table.value.refresh()
		})
	}
	// 删除
	const processRecordDelete = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		processApi.processDelete(params).then(() => {
			table.value.refresh()
		})
	}
	// 激活挂起
	const editActiveSuspendStatus = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		if (record.state === '运行中') {
			stateLoading.value = true
			processApi
				.processSuspend(params)
				.then(() => {
					table.value.refresh()
				})
				.finally(() => {
					stateLoading.value = false
				})
		}
		if (record.state === '已挂起') {
			stateLoading.value = true
			processApi
				.processActive(params)
				.then(() => {
					table.value.refresh()
				})
				.finally(() => {
					stateLoading.value = false
				})
		}
	}
	// 批量操作的验证
	const batchValidate = () => {
		if (selectedRowKeys.value.length < 1) {
			message.warning('请选择一条或多条数据')
			return false
		}
		const params = selectedRowKeys.value.map((m) => {
			return {
				id: m
			}
		})
		return params
	}
	// 批量撤回
	const revokeBatchProcess = () => {
		const params = batchValidate()
		if (params) {
			processApi.processRevoke(params).then(() => {
				table.value.clearRefreshSelected()
			})
		}
	}
	// 批量终止
	const endBatchProcess = () => {
		const params = batchValidate()
		if (params) {
			processApi.processEnd(params).then(() => {
				table.value.clearRefreshSelected()
			})
		}
	}
	// 批量激活
	const activeBatchProcess = () => {
		const params = batchValidate()
		if (params) {
			processApi.processActive(params).then(() => {
				table.value.clearRefreshSelected()
			})
		}
	}
	// 批量挂起
	const suspendBatchProcess = () => {
		const params = batchValidate()
		if (params) {
			processApi.processSuspend(params).then(() => {
				table.value.clearRefreshSelected()
			})
		}
	}
	// 批量删除
	const deleteBatchProcess = (params) => {
		if (params) {
			processApi.processDelete(params).then(() => {
				table.value.clearRefreshSelected()
			})
		}
	}
</script>
