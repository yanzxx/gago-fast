<template>
	<div class="loan-administration-page">
		<a-card :bordered="false" class="filter-card">
			<div class="section-title">筛选条件</div>
			<a-form layout="inline" :model="filters" class="filter-form">
				<a-form-item label="养殖场">
					<a-tree-select
						v-model:value="filters.farmId"
						allow-clear
						placeholder="请选择养殖场"
						style="width: 220px"
						:tree-data="farmTreeData"
						:disabled="!isAdminUser()"
						tree-default-expand-all
						show-search
						tree-node-filter-prop="title"
					/>
				</a-form-item>
				<a-form-item label="申请单号">
					<a-input v-model:value="filters.applyNo" allow-clear placeholder="请输入申请单号" />
				</a-form-item>
				<a-form-item label="状态">
					<a-select
						v-model:value="filters.loanStatus"
						allow-clear
						placeholder="请选择状态"
						style="width: 180px"
						:options="loanStatusOptions"
					/>
				</a-form-item>
				<a-form-item>
					<a-space>
						<a-button type="primary" @click="onSearch">查询</a-button>
						<a-button @click="onReset">重置</a-button>
					</a-space>
				</a-form-item>
			</a-form>
		</a-card>

		<a-card :bordered="false" class="list-card">
			<div class="section-title">贷款申请列表</div>
			<div class="toolbar">
				<a-space>
					<a-button type="primary" @click="openAddModal">新增申请</a-button>
				</a-space>
			</div>

			<a-table row-key="id" :data-source="tableData" :columns="columns" :pagination="false" :loading="tableLoading">
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'farmId'">
						{{ getFarmLabel(record.farmId) }}
					</template>
					<template v-else-if="column.dataIndex === 'loanStatus'">
						<a-tag :color="statusColorMap[record.loanStatus] || 'default'">{{ getStatusLabel(record.loanStatus) }}</a-tag>
					</template>
					<template v-else-if="column.dataIndex === 'applyAmount'">{{ formatAmount(record.applyAmount) }}</template>
					<template v-else-if="column.key === 'action'">
						<a-space size="small">
							<a-button type="link" size="small" @click="openDetailModal(record)">详情</a-button>
							<a-button type="link" size="small" @click="openFlowModal(record)">流程</a-button>
							<a-button type="link" size="small" @click="openEditModal(record)">编辑</a-button>
							<a-button type="link" size="small" danger @click="doDelete(record)">删除</a-button>
						</a-space>
					</template>
				</template>
			</a-table>

			<div class="pagination-wrap">
				<a-pagination
					v-model:current="pagination.current"
					v-model:pageSize="pagination.pageSize"
					:total="pagination.total"
					:show-size-changer="true"
					:show-total="(total) => `共 ${total} 条`"
					@change="onPageChange"
					@showSizeChange="onPageSizeChange"
				/>
			</div>
		</a-card>

		<a-modal
			v-model:visible="formModalOpen"
			:title="formMode === 'add' ? '新增贷款申请' : '编辑贷款申请'"
			:confirm-loading="formSubmitting"
			width="760px"
			ok-text="保存"
			cancel-text="取消"
			@ok="submitForm"
		>
			<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
				<a-row :gutter="16">
					<a-col :span="12" v-if="isAdminUser()">
						<a-form-item label="养殖场" name="farmId">
							<a-select v-model:value="formData.farmId" :options="farmOptions" placeholder="请选择养殖场" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="申请单号" name="applyNo">
							<a-input v-model:value="formData.applyNo" :disabled="formMode === 'edit'" maxlength="64" placeholder="请输入申请单号" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="金融产品" name="productCode">
							<a-select
								v-model:value="formData.productCode"
								placeholder="请选择金融产品"
								:options="productOptions"
								show-search
								:filter-option="filterOption"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="申请金额(元)" name="applyAmount">
							<a-input-number v-model:value="formData.applyAmount" :min="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="申请人" name="applicantName">
							<a-input v-model:value="formData.applicantName" maxlength="50" placeholder="请输入申请人" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="备注" name="remark">
							<a-textarea v-model:value="formData.remark" :rows="3" maxlength="500" placeholder="请输入备注" />
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
		</a-modal>

		<a-modal v-model:visible="detailModalOpen" title="贷款申请详情" :footer="null" width="760px">
			<a-descriptions :column="2" bordered size="small">
				<a-descriptions-item label="申请单号">{{ detailData.applyNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="状态">{{ getStatusLabel(detailData.loanStatus) }}</a-descriptions-item>
				<a-descriptions-item label="养殖场">{{ getFarmLabel(detailData.farmId) }}</a-descriptions-item>
				<a-descriptions-item label="金融产品">{{ detailData.productName || detailData.productCode || '-' }}</a-descriptions-item>
				<a-descriptions-item label="申请金额">{{ formatAmount(detailData.applyAmount) }}</a-descriptions-item>
				<a-descriptions-item label="申请人">{{ detailData.applicantName || '-' }}</a-descriptions-item>
				<a-descriptions-item label="提交时间">{{ detailData.applyTime || '-' }}</a-descriptions-item>
				<a-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</a-descriptions-item>
			</a-descriptions>
		</a-modal>

		<a-modal v-model:visible="flowModalOpen" title="审核流程" :footer="null" width="760px">
			<a-steps :current="flowCurrent" direction="vertical">
				<a-step v-for="item in flowSteps" :key="item.key" :title="item.title" :description="item.description" :status="item.status" />
			</a-steps>
		</a-modal>
	</div>
</template>

<script setup>
import { message, Modal } from 'ant-design-vue'
import tool from '@/utils/tool'
import sysOrgApi from '@/api/sys/orgApi'
import { toFarmTreeSelectData, flattenFarmTreeOptions, findFarmTreeNode } from '@/utils/farmTree'
import finProductApi from '@/api/biz/finProductApi'
import loanAdministrationApi from '@/api/biz/loanAdministrationApi'

const userInfo = tool.data.get('USER_INFO') || {}

const filters = reactive({
	farmId: undefined,
	applyNo: '',
	loanStatus: undefined
})

const fallbackLoanStatusOptions = [
	{ label: '待提交', value: 'PENDING_SUBMIT' },
	{ label: '审批中', value: 'PROCESSING' },
	{ label: '已通过', value: 'APPROVED' },
	{ label: '已拒绝', value: 'REJECTED' }
]

const loanStatusOptions = ref([...fallbackLoanStatusOptions])
const farmOptions = ref([])
const farmTreeData = ref([])
const productOptions = ref([])
const tableData = ref([])
const tableLoading = ref(false)
const detailModalOpen = ref(false)
const detailData = ref({})
const flowModalOpen = ref(false)
const flowRecord = ref({})

const formRef = ref()
const formModalOpen = ref(false)
const formSubmitting = ref(false)
const formMode = ref('add')
const formData = reactive({
	id: '',
	farmId: undefined,
	applyNo: '',
	productCode: undefined,
	applyAmount: null,
	applicantName: '',
	loanStatus: 'PENDING_SUBMIT',
	remark: ''
})

const formRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	applyNo: [{ required: true, message: '请输入申请单号', trigger: 'blur' }],
	productCode: [{ required: true, message: '请选择金融产品', trigger: 'change' }],
	applyAmount: [{ required: true, message: '请输入申请金额', trigger: 'change' }],
	applicantName: [{ required: true, message: '请输入申请人', trigger: 'blur' }]
}

const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const statusColorMap = {
	PENDING_SUBMIT: 'default',
	PROCESSING: 'blue',
	APPROVED: 'green',
	REJECTED: 'red'
}

const columns = [
	{ title: '申请单号', dataIndex: 'applyNo', width: 180 },
	{ title: '养殖场', dataIndex: 'farmId', width: 180 },
	{ title: '金融产品', dataIndex: 'productName', width: 180 },
	{ title: '申请金额(元)', dataIndex: 'applyAmount', width: 140 },
	{ title: '申请人', dataIndex: 'applicantName', width: 140 },
	{ title: '状态', dataIndex: 'loanStatus', width: 120 },
	{ title: '提交时间', dataIndex: 'applyTime', width: 180 },
	{ title: '操作', key: 'action', fixed: 'right', width: 240 }
]

const flowCurrent = computed(() => {
	const status = flowRecord.value?.loanStatus
	if (status === 'PENDING_SUBMIT') return 0
	if (status === 'PROCESSING') return 1
	if (status === 'APPROVED' || status === 'REJECTED') return 2
	return 0
})

const flowSteps = computed(() => {
	const status = flowRecord.value?.loanStatus
	const resultDesc = status === 'APPROVED' ? '审批通过' : status === 'REJECTED' ? '审批拒绝' : '待审批'
	return [
		{
			key: 'submit',
			title: '提交申请',
			description: flowRecord.value?.applyTime || '申请资料已提交',
			status: 'finish'
		},
		{
			key: 'accept',
			title: '受理审核',
			description: status === 'PENDING_SUBMIT' ? '待受理' : '已进入审核流程',
			status: status === 'PENDING_SUBMIT' ? 'process' : 'finish'
		},
		{
			key: 'result',
			title: '审批结果',
			description: resultDesc,
			status: status === 'REJECTED' ? 'error' : status === 'APPROVED' ? 'finish' : 'wait'
		}
	]
})

const localRecords = ref([
	{
		id: 'L001',
		applyNo: 'LOAN-202602-001',
		farmId: '1543842934270394368',
		productCode: 'FP-2026001',
		productName: '活体抵押经营贷',
		applyAmount: 300000,
		applicantName: '张三',
		loanStatus: 'PROCESSING',
		applyTime: '2026-02-27 14:10:00',
		remark: '首笔申请'
	}
])

const isAdminUser = () => {
	const roleCodeList = userInfo.roleCodeList || []
	return Array.isArray(roleCodeList) && roleCodeList.includes('superAdmin')
}

const resolveFarmId = () => userInfo.farmId || userInfo.farm_id || userInfo.orgId || userInfo.org_id

const filterOption = (input, option) => String(option?.label || '').toLowerCase().includes(input.toLowerCase())

const getFarmLabel = (farmId) => {
	if (!farmId) return '-'
	const hit = farmOptions.value.find((item) => item.value === farmId)
	return hit?.label || farmId
}

const getStatusLabel = (value) => {
	if (!value) return '-'
	const hit = loanStatusOptions.value.find((item) => item.value === value)
	return hit?.label || value
}

const getProductNameByCode = (productCode) => {
	if (!productCode) return ''
	const hit = productOptions.value.find((item) => item.value === productCode)
	return hit?.name || hit?.label || productCode
}

const formatAmount = (value) => {
	if (value === null || value === undefined || value === '') return '-'
	const amount = Number(value)
	if (Number.isNaN(amount)) return value
	return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const loadFarmOptions = async () => {
	try {
		const tree = toFarmTreeSelectData(await sysOrgApi.orgTree())
		let scopedTree = tree
		if (!isAdminUser()) {
			const currentFarmId = resolveFarmId()
			const currentNode = findFarmTreeNode(tree, currentFarmId)
			if (currentNode) {
				scopedTree = [currentNode]
			}
		}
		farmTreeData.value = scopedTree
		farmOptions.value = flattenFarmTreeOptions(scopedTree)
	} catch (e) {
		farmTreeData.value = []
		farmOptions.value = []
	}
}

const loadProductOptions = async () => {
	try {
		const res = await finProductApi.page({ current: 1, size: 200, status: 'ON_SHELF', farmId: filters.farmId })
		const records = res?.records || []
		productOptions.value = records.map((item) => ({
			label: `${item.productName} (${item.productCode})`,
			name: item.productName,
			value: item.productCode
		}))
	} catch (e) {
		productOptions.value = [
			{ label: '活体抵押经营贷 (FP-2026001)', name: '活体抵押经营贷', value: 'FP-2026001' },
			{ label: '饲料采购周转贷 (FP-2026002)', name: '饲料采购周转贷', value: 'FP-2026002' }
		]
	}
}

const buildQueryParams = () => ({
	current: pagination.current,
	size: pagination.pageSize,
	farmId: filters.farmId,
	applyNo: filters.applyNo?.trim(),
	loanStatus: filters.loanStatus
})

const applyLocalFilter = () => {
	let list = [...localRecords.value]
	if (filters.farmId) {
		list = list.filter((item) => item.farmId === filters.farmId)
	}
	if (filters.applyNo) {
		const keyword = filters.applyNo.trim()
		list = list.filter((item) => String(item.applyNo || '').includes(keyword))
	}
	if (filters.loanStatus) {
		list = list.filter((item) => item.loanStatus === filters.loanStatus)
	}
	pagination.total = list.length
	const start = (pagination.current - 1) * pagination.pageSize
	tableData.value = list.slice(start, start + pagination.pageSize)
}

const fetchTableData = async () => {
	tableLoading.value = true
	try {
		const res = await loanAdministrationApi.page(buildQueryParams())
		tableData.value = (res?.records || []).map((item) => ({
			...item,
			id: item?.id || item?.loanId || item?.applyNo,
			productName: item?.productName || getProductNameByCode(item?.productCode)
		}))
		pagination.total = res?.total || 0
		pagination.current = res?.current || pagination.current
		pagination.pageSize = res?.size || pagination.pageSize
	} catch (e) {
		applyLocalFilter()
	} finally {
		tableLoading.value = false
	}
}

const onSearch = () => {
	pagination.current = 1
	fetchTableData()
}

const onReset = () => {
	filters.farmId = isAdminUser() ? undefined : resolveFarmId()
	filters.applyNo = ''
	filters.loanStatus = undefined
	pagination.current = 1
	fetchTableData()
}

const onPageChange = (page, pageSize) => {
	pagination.current = page
	pagination.pageSize = pageSize
	fetchTableData()
}

const onPageSizeChange = (current, size) => {
	pagination.current = current
	pagination.pageSize = size
	fetchTableData()
}

const resetFormData = () => {
	Object.assign(formData, {
		id: '',
		farmId: filters.farmId || resolveFarmId(),
		applyNo: '',
		productCode: undefined,
		applyAmount: null,
		applicantName: '',
		loanStatus: 'PENDING_SUBMIT',
		remark: ''
	})
}

const openAddModal = async () => {
	formMode.value = 'add'
	resetFormData()
	formModalOpen.value = true
	await nextTick()
	formRef.value?.clearValidate()
}

const openEditModal = async (record) => {
	try {
		const detail = await loanAdministrationApi.detail({ id: record.id, applyNo: record.applyNo })
		Object.assign(formData, {
			id: detail?.id || record.id,
			farmId: detail?.farmId || record.farmId,
			applyNo: detail?.applyNo || record.applyNo,
			productCode: detail?.productCode || record.productCode,
			applyAmount: detail?.applyAmount || record.applyAmount,
			applicantName: detail?.applicantName || record.applicantName,
			loanStatus: detail?.loanStatus || record.loanStatus,
			remark: detail?.remark || record.remark
		})
	} catch (e) {
		Object.assign(formData, {
			id: record.id,
			farmId: record.farmId,
			applyNo: record.applyNo,
			productCode: record.productCode,
			applyAmount: record.applyAmount,
			applicantName: record.applicantName,
			loanStatus: record.loanStatus,
			remark: record.remark
		})
	}
	formMode.value = 'edit'
	formModalOpen.value = true
	await nextTick()
	formRef.value?.clearValidate()
}

const openDetailModal = async (record) => {
	try {
		detailData.value = await loanAdministrationApi.detail({ id: record.id, applyNo: record.applyNo })
	} catch (e) {
		detailData.value = record
	}
	detailData.value.productName = detailData.value.productName || getProductNameByCode(detailData.value.productCode)
	detailModalOpen.value = true
}

const openFlowModal = async (record) => {
	try {
		flowRecord.value = await loanAdministrationApi.detail({ id: record.id, applyNo: record.applyNo })
	} catch (e) {
		flowRecord.value = record
	}
	flowModalOpen.value = true
}

const submitForm = async () => {
	await formRef.value?.validate()
	formSubmitting.value = true
	const payload = {
		id: formData.id,
		farmId: isAdminUser() ? formData.farmId : resolveFarmId(),
		applyNo: formData.applyNo?.trim(),
		productCode: formData.productCode,
		applyAmount: formData.applyAmount,
		applicantName: formData.applicantName?.trim(),
		loanStatus: formData.loanStatus,
		remark: formData.remark?.trim()
	}
	try {
		if (formMode.value === 'add') {
			await loanAdministrationApi.add(payload)
		} else {
			await loanAdministrationApi.edit(payload)
		}
		message.success('保存成功')
		formModalOpen.value = false
		fetchTableData()
	} catch (e) {
		if (formMode.value === 'add') {
			localRecords.value.unshift({
				...payload,
				id: `${Date.now()}`,
				productName: getProductNameByCode(payload.productCode),
				applyTime: new Date().toLocaleString('zh-CN', { hour12: false })
			})
		} else {
			localRecords.value = localRecords.value.map((item) =>
				item.id === payload.id
					? {
						...item,
						...payload,
						productName: getProductNameByCode(payload.productCode)
					}
					: item
			)
		}
		message.success('本地模拟保存成功')
		formModalOpen.value = false
		fetchTableData()
	} finally {
		formSubmitting.value = false
	}
}

const doDelete = (record) => {
	Modal.confirm({
		title: '确认删除该贷款申请？',
		onOk: async () => {
			try {
				await loanAdministrationApi.delete({ id: record.id, applyNo: record.applyNo })
				message.success('删除成功')
			} catch (e) {
				localRecords.value = localRecords.value.filter((item) => item.id !== record.id)
				message.success('本地模拟删除成功')
			}
			fetchTableData()
		}
	})
}

watch(
	() => filters.farmId,
	() => {
		loadProductOptions()
	}
)

onMounted(() => {
	loadFarmOptions()
	if (!isAdminUser()) {
		filters.farmId = resolveFarmId()
	}
	loadProductOptions()
	fetchTableData()
})
</script>

<style scoped>
.loan-administration-page {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.filter-card,
.list-card {
	border-radius: 12px;
}

.section-title {
	font-size: 15px;
	font-weight: 600;
	color: #1f2a22;
	margin-bottom: 12px;
}

.toolbar {
	display: flex;
	justify-content: flex-start;
	align-items: center;
	margin-bottom: 12px;
}

.pagination-wrap {
	margin-top: 12px;
	display: flex;
	justify-content: flex-end;
}

:deep(.ant-table-thead > tr > th) {
	white-space: nowrap;
	text-align: center;
}

:deep(.ant-table-thead > tr > th .ant-table-column-title) {
	white-space: nowrap;
}

:deep(.ant-table-tbody > tr > td) {
	text-align: center;
}
</style>
