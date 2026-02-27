<template>
	<div class="tb-manage-page">
		<a-card :bordered="false" class="filter-card">
			<div class="section-title">筛选条件</div>
			<a-form layout="inline" :model="filters">
				<a-form-item label="养殖场">
					<a-select
						v-model:value="filters.farmId"
						allow-clear
						placeholder="请选择养殖场"
						style="width: 220px"
						:options="farmOptions"
						:disabled="!isAdminUser()"
					/>
				</a-form-item>
				<a-form-item label="投保单号">
					<a-input v-model:value="filters.applyNo" allow-clear placeholder="请输入投保单号" />
				</a-form-item>
				<a-form-item label="状态">
					<a-select v-model:value="filters.status" allow-clear style="width: 160px" placeholder="请选择状态" :options="statusOptions" />
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
			<div class="section-title">投保记录列表</div>
			<div class="toolbar">
				<a-space>
					<a-button type="primary" @click="openAddModal">新增投保</a-button>
				</a-space>
			</div>

			<a-table row-key="applyNo" :data-source="tableData" :columns="columns" :pagination="false" :loading="tableLoading">
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'farmId'">{{ getFarmLabel(record.farmId) }}</template>
					<template v-else-if="column.dataIndex === 'status'">
						<a-tag :color="statusColorMap[record.status] || 'default'">{{ getStatusLabel(record.status) }}</a-tag>
					</template>
					<template v-else-if="column.key === 'action'">
						<a-space size="small">
							<a-button type="link" size="small" @click="openDetailModal(record)">详情</a-button>
							<a-button type="link" size="small" @click="openEditModal(record)" :disabled="record.status === 'EFFECTIVE'">编辑</a-button>
							<a-button type="link" size="small" danger @click="doDelete(record)" :disabled="record.status === 'EFFECTIVE'">删除</a-button>
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

		<a-modal v-model:visible="formModalOpen" :title="formMode === 'add' ? '新增投保' : '编辑投保'" :confirm-loading="formSubmitting" width="760px" @ok="submitForm">
			<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
				<a-row :gutter="16">
					<a-col :span="12" v-if="isAdminUser()">
						<a-form-item label="养殖场" name="farmId">
							<a-select v-model:value="formData.farmId" :options="farmOptions" placeholder="请选择养殖场" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="投保单号" name="applyNo">
							<a-input v-model:value="formData.applyNo" :disabled="formMode === 'edit'" maxlength="64" placeholder="请输入投保单号" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="保险产品编码" name="productCode">
							<a-input v-model:value="formData.productCode" maxlength="64" placeholder="请输入保险产品编码" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="被保主体" name="insuredName">
							<a-input v-model:value="formData.insuredName" maxlength="64" placeholder="请输入被保主体" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="投保畜种" name="speciesType">
							<a-input v-model:value="formData.speciesType" maxlength="64" placeholder="请输入投保畜种" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="投保数量" name="insuredCount">
							<a-input-number v-model:value="formData.insuredCount" :min="1" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="投保金额" name="insuredAmount">
							<a-input-number v-model:value="formData.insuredAmount" :min="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="保费金额" name="premiumAmount">
							<a-input-number v-model:value="formData.premiumAmount" :min="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="状态" name="status">
							<a-select v-model:value="formData.status" :options="statusOptions" placeholder="请选择状态" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="备注" name="remark">
							<a-textarea v-model:value="formData.remark" :rows="2" maxlength="500" placeholder="请输入备注" />
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
		</a-modal>

		<a-modal v-model:visible="detailModalOpen" title="投保详情" :footer="null" width="760px">
			<a-descriptions :column="2" bordered size="small">
				<a-descriptions-item label="投保单号">{{ detailData.applyNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="保单号">{{ detailData.policyNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="养殖场">{{ getFarmLabel(detailData.farmId) }}</a-descriptions-item>
				<a-descriptions-item label="状态">{{ getStatusLabel(detailData.status) }}</a-descriptions-item>
				<a-descriptions-item label="保险产品编码">{{ detailData.productCode || '-' }}</a-descriptions-item>
				<a-descriptions-item label="被保主体">{{ detailData.insuredName || '-' }}</a-descriptions-item>
				<a-descriptions-item label="投保畜种">{{ detailData.speciesType || '-' }}</a-descriptions-item>
				<a-descriptions-item label="投保数量">{{ detailData.insuredCount || '-' }}</a-descriptions-item>
				<a-descriptions-item label="投保金额">{{ detailData.insuredAmount || '-' }}</a-descriptions-item>
				<a-descriptions-item label="保费金额">{{ detailData.premiumAmount || '-' }}</a-descriptions-item>
				<a-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</a-descriptions-item>
			</a-descriptions>
		</a-modal>
	</div>
</template>

<script setup>
import { message, Modal } from 'ant-design-vue'
import tool from '@/utils/tool'
import bizOrgApi from '@/api/biz/bizOrgApi'
import insApplyApi from '@/api/biz/insApplyApi'

const userInfo = tool.data.get('USER_INFO') || {}

const filters = reactive({ farmId: undefined, applyNo: '', status: undefined })
const statusOptions = [
	{ label: '待提交', value: 'PENDING_SUBMIT' },
	{ label: '已提交', value: 'SUBMITTED' },
	{ label: '已生效', value: 'EFFECTIVE' },
	{ label: '已失效', value: 'INVALID' }
]
const statusColorMap = { PENDING_SUBMIT: 'default', SUBMITTED: 'blue', EFFECTIVE: 'green', INVALID: 'orange' }

const farmOptions = ref([])
const tableData = ref([])
const tableLoading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const formRef = ref()
const formModalOpen = ref(false)
const formSubmitting = ref(false)
const formMode = ref('add')
const formData = reactive({
	id: '',
	farmId: undefined,
	applyNo: '',
	policyNo: '',
	productCode: '',
	insuredName: '',
	speciesType: '',
	insuredCount: null,
	insuredAmount: null,
	premiumAmount: null,
	status: 'PENDING_SUBMIT',
	remark: ''
})
const formRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	applyNo: [{ required: true, message: '请输入投保单号', trigger: 'blur' }],
	productCode: [{ required: true, message: '请输入保险产品编码', trigger: 'blur' }],
	insuredName: [{ required: true, message: '请输入被保主体', trigger: 'blur' }],
	speciesType: [{ required: true, message: '请输入投保畜种', trigger: 'blur' }],
	insuredCount: [{ required: true, message: '请输入投保数量', trigger: 'change' }],
	insuredAmount: [{ required: true, message: '请输入投保金额', trigger: 'change' }],
	premiumAmount: [{ required: true, message: '请输入保费金额', trigger: 'change' }]
}

const detailModalOpen = ref(false)
const detailData = ref({})

const columns = [
	{ title: '投保单号', dataIndex: 'applyNo', width: 160 },
	{ title: '保单号', dataIndex: 'policyNo', width: 160 },
	{ title: '养殖场', dataIndex: 'farmId', width: 180 },
	{ title: '保险产品编码', dataIndex: 'productCode', width: 150 },
	{ title: '被保主体', dataIndex: 'insuredName', width: 140 },
	{ title: '状态', dataIndex: 'status', width: 110 },
	{ title: '操作', key: 'action', fixed: 'right', width: 180 }
]

const localRecords = ref([
	{
		id: 'TB-001',
		farmId: '1543842934270394368',
		applyNo: 'TB-2026001',
		policyNo: 'PL-2026001',
		productCode: 'INS-2026001',
		insuredName: '张三养殖场',
		speciesType: '牛',
		insuredCount: 120,
		insuredAmount: 300000,
		premiumAmount: 10500,
		status: 'SUBMITTED',
		remark: ''
	}
])

const isAdminUser = () => {
	const roleCodeList = userInfo.roleCodeList || []
	return Array.isArray(roleCodeList) && roleCodeList.includes('superAdmin')
}
const resolveFarmId = () => userInfo.farmId || userInfo.farm_id || userInfo.orgId || userInfo.org_id

const getFarmLabel = (farmId) => {
	if (!farmId) return '-'
	const hit = farmOptions.value.find((item) => item.value === farmId)
	return hit?.label || farmId
}
const getStatusLabel = (status) => statusOptions.find((i) => i.value === status)?.label || status || '-'

const loadFarmOptions = async () => {
	try {
		const list = await bizOrgApi.orgList()
		let options = (list || []).map((item) => ({ label: item.name, value: item.id }))
		if (!isAdminUser()) {
			const currentFarmId = resolveFarmId()
			options = options.filter((item) => item.value === currentFarmId)
		}
		farmOptions.value = options
	} catch (e) {
		farmOptions.value = []
	}
}

const buildQueryParams = () => ({
	current: pagination.current,
	size: pagination.pageSize,
	farmId: filters.farmId,
	applyNo: filters.applyNo?.trim(),
	status: filters.status
})

const applyLocalFilter = () => {
	let list = [...localRecords.value]
	if (filters.farmId) list = list.filter((item) => item.farmId === filters.farmId)
	if (filters.applyNo) list = list.filter((item) => String(item.applyNo || '').includes(filters.applyNo.trim()))
	if (filters.status) list = list.filter((item) => item.status === filters.status)
	pagination.total = list.length
	const start = (pagination.current - 1) * pagination.pageSize
	tableData.value = list.slice(start, start + pagination.pageSize)
}

const fetchTableData = async () => {
	tableLoading.value = true
	try {
		const res = await insApplyApi.page(buildQueryParams())
		tableData.value = (res?.records || []).map((item) => ({ ...item, id: item?.id || item?.applyId || '' }))
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
	filters.status = undefined
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
		policyNo: '',
		productCode: '',
		insuredName: '',
		speciesType: '',
		insuredCount: null,
		insuredAmount: null,
		premiumAmount: null,
		status: 'PENDING_SUBMIT',
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
		const detail = await insApplyApi.detail({ id: record.id, applyNo: record.applyNo })
		Object.assign(formData, { ...detail, id: detail?.id || record.id })
	} catch (e) {
		Object.assign(formData, { ...record })
	}
	formMode.value = 'edit'
	formModalOpen.value = true
	await nextTick()
	formRef.value?.clearValidate()
}

const openDetailModal = async (record) => {
	try {
		detailData.value = await insApplyApi.detail({ id: record.id, applyNo: record.applyNo })
	} catch (e) {
		detailData.value = record
	}
	detailModalOpen.value = true
}

const submitForm = async () => {
	await formRef.value?.validate()
	formSubmitting.value = true
	const payload = {
		id: formData.id,
		farmId: isAdminUser() ? formData.farmId : resolveFarmId(),
		applyNo: formData.applyNo?.trim(),
		productCode: formData.productCode?.trim(),
		insuredName: formData.insuredName?.trim(),
		speciesType: formData.speciesType?.trim(),
		insuredCount: formData.insuredCount,
		insuredAmount: formData.insuredAmount,
		premiumAmount: formData.premiumAmount,
		status: formData.status,
		remark: formData.remark?.trim()
	}
	try {
		if (formMode.value === 'add') {
			await insApplyApi.add(payload)
		} else {
			await insApplyApi.edit(payload)
		}
		message.success('保存成功')
		formModalOpen.value = false
		fetchTableData()
	} catch (e) {
		if (formMode.value === 'add') {
			localRecords.value.unshift({ ...payload, id: `${Date.now()}` })
		} else {
			localRecords.value = localRecords.value.map((item) => (item.id === payload.id ? { ...item, ...payload } : item))
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
		title: '确认删除该投保记录？',
		onOk: async () => {
			try {
				await insApplyApi.delete({ id: record.id, applyNo: record.applyNo })
				message.success('删除成功')
			} catch (e) {
				localRecords.value = localRecords.value.filter((item) => item.id !== record.id)
				message.success('本地模拟删除成功')
			}
			fetchTableData()
		}
	})
}

onMounted(() => {
	loadFarmOptions()
	if (!isAdminUser()) {
		filters.farmId = resolveFarmId()
	}
	fetchTableData()
})
</script>

<style scoped>
.tb-manage-page {
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

:deep(.ant-table-tbody > tr > td) {
	text-align: center;
}
</style>
