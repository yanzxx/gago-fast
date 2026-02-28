<template>
	<div class="product-manage-page">
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
				<a-form-item label="产品名称">
					<a-input v-model:value="filters.productName" allow-clear placeholder="请输入产品名称" />
				</a-form-item>
				<a-form-item label="状态">
					<a-select
						v-model:value="filters.status"
						allow-clear
						placeholder="请选择状态"
						style="width: 180px"
						:options="statusOptions"
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
			<div class="section-title">产品列表</div>
			<div class="toolbar">
				<a-space>
					<a-button type="primary" @click="openAddModal">新增产品</a-button>
					<!-- 暂时隐藏批量删除（仅前端隐藏） -->
					<!-- <a-button danger :disabled="!selectedRows.length" @click="doBatchDelete">批量删除</a-button> -->
				</a-space>
			</div>

			<a-table
				row-key="productCode"
				:data-source="tableData"
				:columns="columns"
				:pagination="false"
				:loading="tableLoading"
			>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'speciesCodes'">
						{{ getSpeciesLabel(record.speciesCodes) }}
					</template>
					<template v-else-if="column.dataIndex === 'amountRange'">
						{{ record.amountMin }} ~ {{ record.amountMax }}
					</template>
					<template v-else-if="column.dataIndex === 'annualRate'">
						{{ record.annualRate }}%
					</template>
					<template v-else-if="column.dataIndex === 'status'">
						<a-tag :color="statusColorMap[record.status] || 'default'">{{ getStatusLabel(record.status) }}</a-tag>
					</template>
					<template v-else-if="column.dataIndex === 'repayType'">
						{{ getRepayTypeLabel(record.repayType) }}
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
			:title="formMode === 'add' ? '新增金融产品' : '编辑金融产品'"
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
						<a-form-item label="适用畜种" name="speciesCodes">
							<a-select
								v-model:value="formData.speciesCodes"
								mode="multiple"
								:options="speciesOptions"
								placeholder="请选择适用畜种"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="最低额度" name="amountMin">
							<a-input-number v-model:value="formData.amountMin" :min="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="最高额度" name="amountMax">
							<a-input-number v-model:value="formData.amountMax" :min="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="年化利率(%)" name="annualRate">
							<a-input-number v-model:value="formData.annualRate" :min="0" :step="0.01" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="期限(月)" name="termMonths">
							<a-input-number v-model:value="formData.termMonths" :min="1" :step="1" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="还款方式" name="repayType">
							<a-select v-model:value="formData.repayType" :options="repayTypeOptions" placeholder="请选择还款方式" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="产品说明" name="remark">
							<a-textarea v-model:value="formData.remark" :rows="3" maxlength="1000" placeholder="请输入产品说明" />
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
		</a-modal>

		<a-modal v-model:visible="detailModalOpen" title="金融产品详情" :footer="null" width="760px">
			<a-descriptions :column="2" bordered size="small">
				<a-descriptions-item label="产品名称">{{ detailData.productName || '-' }}</a-descriptions-item>
				<a-descriptions-item label="产品编码">{{ detailData.productCode || '-' }}</a-descriptions-item>
				<a-descriptions-item label="养殖场">{{ getFarmLabel(detailData.farmId) }}</a-descriptions-item>
				<a-descriptions-item label="状态">{{ getStatusLabel(detailData.status) }}</a-descriptions-item>
				<a-descriptions-item label="适用畜种" :span="2">{{ getSpeciesLabel(detailData.speciesCodes) }}</a-descriptions-item>
				<a-descriptions-item label="额度范围">{{ detailData.amountMin }} ~ {{ detailData.amountMax }}</a-descriptions-item>
				<a-descriptions-item label="年化利率">{{ detailData.annualRate }}%</a-descriptions-item>
				<a-descriptions-item label="期限(月)">{{ detailData.termMonths || '-' }}</a-descriptions-item>
				<a-descriptions-item label="还款方式">{{ getRepayTypeLabel(detailData.repayType) }}</a-descriptions-item>
				<a-descriptions-item label="产品说明" :span="2">{{ detailData.remark || '-' }}</a-descriptions-item>
			</a-descriptions>
		</a-modal>
	</div>
</template>

<script setup>
import { message, Modal } from 'ant-design-vue'
import tool from '@/utils/tool'
import sysOrgApi from '@/api/sys/orgApi'
import { toFarmTreeSelectData, flattenFarmTreeOptions, findFarmTreeNode } from '@/utils/farmTree'
import livestockApi from '@/api/biz/livestockApi'
import finProductApi from '@/api/biz/finProductApi'
import dictApi from '@/api/dev/dictApi'

const userInfo = tool.data.get('USER_INFO') || {}

const filters = reactive({
	farmId: undefined,
	productName: '',
	status: undefined
})

const tableData = ref([])
const tableLoading = ref(false)
const farmOptions = ref([])
const farmTreeData = ref([])
const speciesOptions = ref([])
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
	speciesCodes: [],
	amountMin: null,
	amountMax: null,
	annualRate: null,
	termMonths: null,
	repayType: undefined,
	remark: ''
})

const detailModalOpen = ref(false)
const detailData = ref({})

const fallbackStatusOptions = [
	{ label: '草稿', value: 'DRAFT' },
	{ label: '已上架', value: 'ON_SHELF' },
	{ label: '已下架', value: 'OFF_SHELF' }
]
const fallbackRepayTypeOptions = [
	{ label: '先息后本', value: 'INTEREST_FIRST' },
	{ label: '等额本息', value: 'EQUAL_INSTALLMENT' },
	{ label: '到期还本付息', value: 'LUMP_SUM' }
]
const statusOptions = ref([...fallbackStatusOptions])
const repayTypeOptions = ref([...fallbackRepayTypeOptions])
const finProductStatusDictCode = 'FIN_PRODUCT_STATUS'
const finRepayTypeDictCode = 'FIN_REPAY_TYPE'
const dictCategory = 'BIZ'

const statusColorMap = {
	DRAFT: 'default',
	ON_SHELF: 'green',
	OFF_SHELF: 'orange'
}

const columns = [
	{ title: '产品名称', dataIndex: 'productName', width: 160 },
	{ title: '产品编码', dataIndex: 'productCode', width: 150 },
	{ title: '适用畜种', dataIndex: 'speciesCodes', width: 180 },
	{ title: '额度范围', dataIndex: 'amountRange', width: 160 },
	{ title: '年化利率', dataIndex: 'annualRate', width: 120 },
	{ title: '期限(月)', dataIndex: 'termMonths', width: 100 },
	{ title: '还款方式', dataIndex: 'repayType', width: 120 },
	{ title: '状态', dataIndex: 'status', width: 100 },
	{ title: '操作', key: 'action', fixed: 'right', width: 260 }
]

const formRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
	productCode: [{ required: true, message: '请输入产品编码', trigger: 'blur' }],
	speciesCodes: [{ required: true, type: 'array', min: 1, message: '请选择适用畜种', trigger: 'change' }],
	amountMin: [{ required: true, message: '请输入最低额度', trigger: 'change' }],
	amountMax: [{ required: true, message: '请输入最高额度', trigger: 'change' }],
	annualRate: [{ required: true, message: '请输入年化利率', trigger: 'change' }],
	termMonths: [{ required: true, message: '请输入期限', trigger: 'change' }],
	repayType: [{ required: true, message: '请选择还款方式', trigger: 'change' }]
}

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

const mapDictChildrenToCodeOptions = (children = []) => {
	return children
		.map((item) => ({
			label: item.name || item.dictLabel,
			value: item.dictValue
		}))
		.filter((item) => !!item.label && !!item.value)
}

const findDictNodeByCode = (tree = [], code) => {
	for (const node of tree) {
		if (node?.dictValue === code) return node
		const hit = findDictNodeByCode(node?.children || [], code)
		if (hit) return hit
	}
	return null
}

const getOptionLabel = (options = [], value) => {
	const hit = (options || []).find((item) => item?.value === value)
	return hit?.label
}

const getDictLabel = (dictCode, value, fallbackOptions = []) => {
	if (!value) return '-'
	const label = tool.dictTypeData(dictCode, value)
	if (label && label !== '无此字典项' && label !== '无此字典' && label !== '需重新登录') {
		return label
	}
	const optionLabel = getOptionLabel(fallbackOptions, value)
	return optionLabel || value
}

const getStatusLabel = (status) => getDictLabel(finProductStatusDictCode, status, statusOptions.value)
const getRepayTypeLabel = (repayType) => getDictLabel(finRepayTypeDictCode, repayType, repayTypeOptions.value)

const getSpeciesLabel = (speciesCodes) => {
	if (!speciesCodes) return '-'
	const codeList = String(speciesCodes).split(',').map((item) => item.trim()).filter(Boolean)
	const map = new Map((speciesOptions.value || []).map((item) => [item.value, item.label]))
	return codeList.map((code) => map.get(code) || code).join('、')
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

const loadSpeciesOptions = async () => {
	try {
		const data = await livestockApi.speciesOptions({ farmId: filters.farmId })
		speciesOptions.value = (data || []).map((item) => ({ label: item.label, value: item.value }))
	} catch (e) {
		speciesOptions.value = []
	}
}

const loadFinProductDictOptions = async () => {
	const cachedStatus = mapDictChildrenToCodeOptions(tool.dictTypeList(finProductStatusDictCode))
	const cachedRepayType = mapDictChildrenToCodeOptions(tool.dictTypeList(finRepayTypeDictCode))
	if (cachedStatus.length) {
		statusOptions.value = cachedStatus
	}
	if (cachedRepayType.length) {
		repayTypeOptions.value = cachedRepayType
	}
	if (cachedStatus.length && cachedRepayType.length) {
		return
	}
	try {
		const treeData = await dictApi.dictTree({ category: dictCategory })
		const statusRoot = findDictNodeByCode(treeData || [], finProductStatusDictCode)
		const repayTypeRoot = findDictNodeByCode(treeData || [], finRepayTypeDictCode)
		const remoteStatus = mapDictChildrenToCodeOptions(statusRoot?.children || [])
		const remoteRepayType = mapDictChildrenToCodeOptions(repayTypeRoot?.children || [])
		statusOptions.value = remoteStatus.length ? remoteStatus : fallbackStatusOptions
		repayTypeOptions.value = remoteRepayType.length ? remoteRepayType : fallbackRepayTypeOptions
	} catch (e) {
		statusOptions.value = fallbackStatusOptions
		repayTypeOptions.value = fallbackRepayTypeOptions
	}
}

const buildQueryParams = () => ({
	current: pagination.current,
	size: pagination.pageSize,
	farmId: filters.farmId,
	productName: filters.productName?.trim(),
	status: filters.status
})

const fetchTableData = async () => {
	tableLoading.value = true
	try {
		const res = await finProductApi.page(buildQueryParams())
		tableData.value = (res?.records || []).map((item) => ({
			...item,
			id: item?.id || item?.productId || '',
			productCode: item?.productCode
		}))
		pagination.total = res?.total || 0
		pagination.current = res?.current || pagination.current
		pagination.pageSize = res?.size || pagination.pageSize
	} catch (e) {
		tableData.value = []
		pagination.total = 0
	} finally {
		tableLoading.value = false
	}
}

const onSearch = () => {
	pagination.current = 1
	fetchTableData()
}

const onReset = () => {
	filters.farmId = undefined
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
		speciesCodes: [],
		amountMin: null,
		amountMax: null,
		annualRate: null,
		termMonths: null,
		repayType: undefined,
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
	const payload = { id: record.id, productCode: record.productCode }
	const detail = await finProductApi.detail(payload)
	formMode.value = 'edit'
	Object.assign(formData, {
		id: detail?.id || record.id,
		farmId: detail?.farmId,
		productName: detail?.productName,
		productCode: detail?.productCode || record.productCode,
		speciesCodes: detail?.speciesCodes ? String(detail.speciesCodes).split(',').filter(Boolean) : [],
		amountMin: detail?.amountMin,
		amountMax: detail?.amountMax,
		annualRate: detail?.annualRate,
		termMonths: detail?.termMonths,
		repayType: detail?.repayType,
		remark: detail?.remark
	})
	formModalOpen.value = true
	await nextTick()
	formRef.value?.clearValidate()
}

const openDetailModal = async (record) => {
	detailData.value = await finProductApi.detail({ id: record.id, productCode: record.productCode })
	detailModalOpen.value = true
}

const submitForm = async () => {
	await formRef.value?.validate()
	if (Number(formData.amountMin) > Number(formData.amountMax)) {
		message.error('最低额度不能大于最高额度')
		return
	}
	formSubmitting.value = true
	try {
		const payload = {
			id: formData.id,
			productCode: formData.productCode,
			farmId: isAdminUser() ? formData.farmId : resolveFarmId(),
			productName: formData.productName?.trim(),
			speciesCodes: (formData.speciesCodes || []).join(','),
			amountMin: formData.amountMin,
			amountMax: formData.amountMax,
			annualRate: formData.annualRate,
			termMonths: formData.termMonths,
			repayType: formData.repayType,
			remark: formData.remark?.trim()
		}
		if (formMode.value === 'add') {
			await finProductApi.add(payload)
		} else {
			await finProductApi.edit(payload)
		}
		message.success('保存成功')
		formModalOpen.value = false
		fetchTableData()
	} finally {
		formSubmitting.value = false
	}
}

const doOnShelf = (record) => {
	Modal.confirm({
		title: '确认上架该产品？',
		onOk: async () => {
			await finProductApi.onShelf({ id: record.id, productCode: record.productCode })
			message.success('上架成功')
			fetchTableData()
		}
	})
}

const doOffShelf = (record) => {
	Modal.confirm({
		title: '确认下架该产品？',
		onOk: async () => {
			await finProductApi.offShelf({ id: record.id, productCode: record.productCode })
			message.success('下架成功')
			fetchTableData()
		}
	})
}

const doDelete = (record) => {
	Modal.confirm({
		title: '确认删除该产品？',
		onOk: async () => {
			await finProductApi.delete({ id: record.id, productCode: record.productCode })
			message.success('删除成功')
			fetchTableData()
		}
	})
}

const doBatchDelete = () => {
	const rows = []
	if (!rows.length) return
	const blocked = rows.filter((item) => item.status === 'ON_SHELF')
	if (blocked.length) {
		message.warning('已上架产品不允许删除，请先下架后再批量删除')
		return
	}
	Modal.confirm({
		title: `确认批量删除 ${rows.length} 条产品？`,
		onOk: async () => {
			await finProductApi.batchDelete(
				rows.map((item) => ({
					id: item.id,
					productCode: item.productCode
				}))
			)
			message.success('批量删除成功')
			fetchTableData()
		}
	})
}

onMounted(() => {
	loadFarmOptions()
	loadSpeciesOptions()
	loadFinProductDictOptions()
	fetchTableData()
})
</script>

<style scoped>
.product-manage-page {
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
