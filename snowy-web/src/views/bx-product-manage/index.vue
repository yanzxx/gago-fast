<template>
	<div class="bx-product-manage-page">
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
				<a-form-item label="产品名称">
					<a-input v-model:value="filters.productName" allow-clear placeholder="请输入产品名称" />
				</a-form-item>
				<a-form-item label="状态">
					<a-select v-model:value="filters.status" allow-clear style="width: 180px" placeholder="请选择状态" :options="statusOptions" />
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
			<div class="section-title">保险产品列表</div>
			<div class="toolbar">
				<a-space>
					<a-button type="primary" @click="openAddModal">新增产品</a-button>
				</a-space>
			</div>

			<a-table row-key="productCode" :data-source="tableData" :columns="columns" :pagination="false" :loading="tableLoading">
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'insuranceType'">{{ getTypeLabel(record.insuranceType) }}</template>
					<template v-else-if="column.dataIndex === 'premiumRate'">{{ record.premiumRate }}%</template>
					<template v-else-if="column.dataIndex === 'maxCompensation'">{{ formatAmount(record.maxCompensation) }}</template>
					<template v-else-if="column.dataIndex === 'status'">
						<a-tag :color="statusColorMap[record.status] || 'default'">{{ getStatusLabel(record.status) }}</a-tag>
					</template>
					<template v-else-if="column.key === 'action'">
						<a-space size="small">
							<a-button type="link" size="small" @click="openDetailModal(record)">详情</a-button>
							<a-button type="link" size="small" @click="openEditModal(record)" :disabled="record.status === 'ON_SHELF'">编辑</a-button>
							<a-button v-if="record.status !== 'ON_SHELF'" type="link" size="small" @click="doOnShelf(record)">上架</a-button>
							<a-button v-else type="link" size="small" @click="doOffShelf(record)">下架</a-button>
							<a-button type="link" size="small" danger @click="doDelete(record)" :disabled="record.status === 'ON_SHELF'">删除</a-button>
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
			:title="formMode === 'add' ? '新增保险产品' : '编辑保险产品'"
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
						<a-form-item label="产品名称" name="productName">
							<a-input v-model:value="formData.productName" maxlength="100" placeholder="请输入产品名称" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="产品编码" name="productCode">
							<a-input v-model:value="formData.productCode" :disabled="formMode === 'edit'" maxlength="64" placeholder="请输入产品编码" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="险种类型" name="insuranceType">
							<a-select v-model:value="formData.insuranceType" :options="insuranceTypeOptions" placeholder="请选择险种类型" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="费率(%)" name="premiumRate">
							<a-input-number v-model:value="formData.premiumRate" :min="0" :step="0.01" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="最高赔付金额" name="maxCompensation">
							<a-input-number v-model:value="formData.maxCompensation" :min="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="保障范围" name="coverageScope">
							<a-textarea v-model:value="formData.coverageScope" :rows="3" maxlength="1000" placeholder="请输入保障范围" />
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

		<a-modal v-model:visible="detailModalOpen" title="保险产品详情" :footer="null" width="760px">
			<a-descriptions :column="2" bordered size="small">
				<a-descriptions-item label="产品名称">{{ detailData.productName || '-' }}</a-descriptions-item>
				<a-descriptions-item label="产品编码">{{ detailData.productCode || '-' }}</a-descriptions-item>
				<a-descriptions-item label="养殖场">{{ getFarmLabel(detailData.farmId) }}</a-descriptions-item>
				<a-descriptions-item label="状态">{{ getStatusLabel(detailData.status) }}</a-descriptions-item>
				<a-descriptions-item label="险种类型">{{ getTypeLabel(detailData.insuranceType) }}</a-descriptions-item>
				<a-descriptions-item label="费率">{{ detailData.premiumRate ? `${detailData.premiumRate}%` : '-' }}</a-descriptions-item>
				<a-descriptions-item label="最高赔付金额">{{ formatAmount(detailData.maxCompensation) }}</a-descriptions-item>
				<a-descriptions-item label="保障范围" :span="2">{{ detailData.coverageScope || '-' }}</a-descriptions-item>
				<a-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</a-descriptions-item>
			</a-descriptions>
		</a-modal>
	</div>
</template>

<script setup>
import { message, Modal } from 'ant-design-vue'
import tool from '@/utils/tool'
import bizOrgApi from '@/api/biz/bizOrgApi'
import insProductApi from '@/api/biz/insProductApi'

const userInfo = tool.data.get('USER_INFO') || {}

const filters = reactive({ farmId: undefined, productName: '', status: undefined })

const fallbackStatusOptions = [
	{ label: '草稿', value: 'DRAFT' },
	{ label: '已上架', value: 'ON_SHELF' },
	{ label: '已下架', value: 'OFF_SHELF' }
]
const insuranceTypeOptions = [
	{ label: '活体死亡险', value: 'LIVESTOCK_DEATH' },
	{ label: '疫病险', value: 'EPIDEMIC' },
	{ label: '综合养殖险', value: 'COMPREHENSIVE' }
]

const statusOptions = ref([...fallbackStatusOptions])
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
	productName: '',
	productCode: '',
	insuranceType: undefined,
	coverageScope: '',
	premiumRate: null,
	maxCompensation: null,
	status: 'DRAFT',
	remark: ''
})

const detailModalOpen = ref(false)
const detailData = ref({})

const statusColorMap = { DRAFT: 'default', ON_SHELF: 'green', OFF_SHELF: 'orange' }

const columns = [
	{ title: '产品名称', dataIndex: 'productName', width: 160 },
	{ title: '产品编码', dataIndex: 'productCode', width: 150 },
	{ title: '险种类型', dataIndex: 'insuranceType', width: 130 },
	{ title: '费率(%)', dataIndex: 'premiumRate', width: 120 },
	{ title: '最高赔付金额', dataIndex: 'maxCompensation', width: 140 },
	{ title: '状态', dataIndex: 'status', width: 100 },
	{ title: '操作', key: 'action', fixed: 'right', width: 260 }
]

const formRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
	productCode: [{ required: true, message: '请输入产品编码', trigger: 'blur' }],
	insuranceType: [{ required: true, message: '请选择险种类型', trigger: 'change' }],
	coverageScope: [{ required: true, message: '请输入保障范围', trigger: 'blur' }],
	premiumRate: [{ required: true, message: '请输入费率', trigger: 'change' }],
	maxCompensation: [{ required: true, message: '请输入最高赔付金额', trigger: 'change' }]
}

const localRecords = ref([
	{
		id: 'IP-001',
		farmId: '1543842934270394368',
		productName: '育肥牛综合险',
		productCode: 'INS-2026001',
		insuranceType: 'COMPREHENSIVE',
		coverageScope: '覆盖死亡、疫病和自然灾害风险',
		premiumRate: 3.5,
		maxCompensation: 300000,
		status: 'ON_SHELF',
		remark: '首期产品'
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
const getStatusLabel = (status) => statusOptions.value.find((i) => i.value === status)?.label || status || '-'
const getTypeLabel = (type) => insuranceTypeOptions.find((i) => i.value === type)?.label || type || '-'

const formatAmount = (value) => {
	if (value === null || value === undefined || value === '') return '-'
	const amount = Number(value)
	if (Number.isNaN(amount)) return value
	return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

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
	productName: filters.productName?.trim(),
	status: filters.status
})

const applyLocalFilter = () => {
	let list = [...localRecords.value]
	if (filters.farmId) list = list.filter((item) => item.farmId === filters.farmId)
	if (filters.productName) list = list.filter((item) => String(item.productName || '').includes(filters.productName.trim()))
	if (filters.status) list = list.filter((item) => item.status === filters.status)
	pagination.total = list.length
	const start = (pagination.current - 1) * pagination.pageSize
	tableData.value = list.slice(start, start + pagination.pageSize)
}

const fetchTableData = async () => {
	tableLoading.value = true
	try {
		const res = await insProductApi.page(buildQueryParams())
		tableData.value = (res?.records || []).map((item) => ({
			...item,
			id: item?.id || item?.productId || '',
			productCode: item?.productCode
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
	filters.productName = ''
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
		productName: '',
		productCode: '',
		insuranceType: undefined,
		coverageScope: '',
		premiumRate: null,
		maxCompensation: null,
		status: 'DRAFT',
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
		const detail = await insProductApi.detail({ id: record.id, productCode: record.productCode })
		Object.assign(formData, {
			id: detail?.id || record.id,
			farmId: detail?.farmId || record.farmId,
			productName: detail?.productName || record.productName,
			productCode: detail?.productCode || record.productCode,
			insuranceType: detail?.insuranceType || record.insuranceType,
			coverageScope: detail?.coverageScope || record.coverageScope,
			premiumRate: detail?.premiumRate || record.premiumRate,
			maxCompensation: detail?.maxCompensation || record.maxCompensation,
			status: detail?.status || record.status,
			remark: detail?.remark || record.remark
		})
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
		detailData.value = await insProductApi.detail({ id: record.id, productCode: record.productCode })
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
		productName: formData.productName?.trim(),
		productCode: formData.productCode?.trim(),
		insuranceType: formData.insuranceType,
		coverageScope: formData.coverageScope?.trim(),
		premiumRate: formData.premiumRate,
		maxCompensation: formData.maxCompensation,
		remark: formData.remark?.trim()
	}
	try {
		if (formMode.value === 'add') {
			await insProductApi.add(payload)
		} else {
			await insProductApi.edit(payload)
		}
		message.success('保存成功')
		formModalOpen.value = false
		fetchTableData()
	} catch (e) {
		if (formMode.value === 'add') {
			localRecords.value.unshift({ ...payload, id: `${Date.now()}`, status: 'DRAFT' })
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

const doOnShelf = (record) => {
	Modal.confirm({
		title: '确认上架该保险产品？',
		onOk: async () => {
			try {
				await insProductApi.onShelf({ id: record.id, productCode: record.productCode })
				message.success('上架成功')
			} catch (e) {
				localRecords.value = localRecords.value.map((item) => (item.id === record.id ? { ...item, status: 'ON_SHELF' } : item))
				message.success('本地模拟上架成功')
			}
			fetchTableData()
		}
	})
}

const doOffShelf = (record) => {
	Modal.confirm({
		title: '确认下架该保险产品？',
		onOk: async () => {
			try {
				await insProductApi.offShelf({ id: record.id, productCode: record.productCode })
				message.success('下架成功')
			} catch (e) {
				localRecords.value = localRecords.value.map((item) => (item.id === record.id ? { ...item, status: 'OFF_SHELF' } : item))
				message.success('本地模拟下架成功')
			}
			fetchTableData()
		}
	})
}

const doDelete = (record) => {
	Modal.confirm({
		title: '确认删除该保险产品？',
		onOk: async () => {
			try {
				await insProductApi.delete({ id: record.id, productCode: record.productCode })
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
.bx-product-manage-page {
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
