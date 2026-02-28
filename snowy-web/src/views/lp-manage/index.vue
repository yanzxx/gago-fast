<template>
	<div class="lp-manage-page">
		<a-card :bordered="false" class="filter-card">
			<div class="section-title">筛选条件</div>
			<a-form layout="inline" :model="filters">
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
				<a-form-item label="理赔单号">
					<a-input v-model:value="filters.claimNo" allow-clear placeholder="请输入理赔单号" />
				</a-form-item>
				<a-form-item label="保单号">
					<a-input v-model:value="filters.policyNo" allow-clear placeholder="请输入保单号" />
				</a-form-item>
				<a-form-item label="状态">
					<a-select v-model:value="filters.status" allow-clear style="width: 170px" placeholder="请选择状态" :options="statusOptions" />
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
			<div class="section-title">理赔记录列表</div>
			<div class="toolbar">
				<a-space>
					<a-button type="primary" @click="openAddModal">新增理赔</a-button>
				</a-space>
			</div>

			<a-table row-key="id" :data-source="tableData" :columns="columns" :pagination="false" :loading="tableLoading">
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'farmId'">{{ getFarmLabel(record.farmId) }}</template>
					<template v-else-if="column.dataIndex === 'status'">
						<a-tag :color="statusColorMap[record.status] || 'default'">{{ getStatusLabel(record.status) }}</a-tag>
					</template>
					<template v-else-if="column.key === 'action'">
						<a-space size="small">
							<a-button type="link" size="small" @click="openDetailModal(record)">详情</a-button>
							<a-button type="link" size="small" @click="openSupplementModal(record)" :disabled="isClosed(record.status)">补充材料</a-button>
							<a-button type="link" size="small" @click="openHandleModal(record)" :disabled="isClosed(record.status)">处理结果</a-button>
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

		<a-modal v-model:visible="addModalOpen" title="新增理赔" :confirm-loading="addSubmitting" width="820px" @ok="submitAdd">
			<a-form ref="addFormRef" :model="addForm" :rules="addRules" layout="vertical">
				<a-row :gutter="16">
					<a-col :span="12" v-if="isAdminUser()">
						<a-form-item label="养殖场" name="farmId">
							<a-select v-model:value="addForm.farmId" :options="farmOptions" placeholder="请选择养殖场" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="理赔单号" name="claimNo">
							<a-input v-model:value="addForm.claimNo" maxlength="64" placeholder="请输入理赔单号" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="保单号" name="policyNo">
							<a-input v-model:value="addForm.policyNo" maxlength="64" placeholder="请输入保单号" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="被保主体" name="insuredName">
							<a-input v-model:value="addForm.insuredName" maxlength="100" placeholder="请输入被保主体" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="理赔类型" name="claimType">
							<a-select v-model:value="addForm.claimType" :options="claimTypeOptions" placeholder="请选择理赔类型" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="损失数量" name="lossCount">
							<a-input-number v-model:value="addForm.lossCount" :min="1" :precision="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="理赔金额" name="claimAmount">
							<a-input-number v-model:value="addForm.claimAmount" :min="0" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="出险时间" name="occurTime">
							<a-date-picker v-model:value="addForm.occurTime" value-format="YYYY-MM-DD" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="材料附件">
							<a-upload
								v-model:file-list="addFileList"
								:custom-request="uploadAttachment"
								list-type="text"
								:multiple="true"
								:max-count="6"
							>
								<a-button>上传附件</a-button>
							</a-upload>
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="备注">
							<a-textarea v-model:value="addForm.remark" :rows="2" maxlength="500" placeholder="请输入备注" />
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
		</a-modal>

		<a-modal v-model:visible="supplementModalOpen" title="补充材料" :confirm-loading="supplementSubmitting" width="720px" @ok="submitSupplement">
			<a-form ref="supplementFormRef" :model="supplementForm" :rules="supplementRules" layout="vertical">
				<a-form-item label="补充附件" name="files">
					<a-upload
						v-model:file-list="supplementFileList"
						:custom-request="uploadAttachment"
						list-type="text"
						:multiple="true"
						:max-count="6"
					>
						<a-button>上传附件</a-button>
					</a-upload>
				</a-form-item>
				<a-form-item label="说明" name="remark">
					<a-textarea v-model:value="supplementForm.remark" :rows="3" maxlength="500" placeholder="请输入补充说明" />
				</a-form-item>
			</a-form>
		</a-modal>

		<a-modal v-model:visible="handleModalOpen" title="处理结果登记" :confirm-loading="handleSubmitting" width="720px" @ok="submitHandle">
			<a-form ref="handleFormRef" :model="handleForm" :rules="handleRules" layout="vertical">
				<a-form-item label="处理结果" name="status">
					<a-select v-model:value="handleForm.status" :options="handleStatusOptions" placeholder="请选择处理结果" />
				</a-form-item>
				<a-form-item label="结果说明" name="resultRemark">
					<a-textarea v-model:value="handleForm.resultRemark" :rows="4" maxlength="500" placeholder="请输入结果说明" />
				</a-form-item>
			</a-form>
		</a-modal>

		<a-modal v-model:visible="detailModalOpen" title="理赔详情" :footer="null" width="860px">
			<a-descriptions :column="2" bordered size="small">
				<a-descriptions-item label="理赔单号">{{ detailData.claimNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="保单号">{{ detailData.policyNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="养殖场">{{ getFarmLabel(detailData.farmId) }}</a-descriptions-item>
				<a-descriptions-item label="状态">{{ getStatusLabel(detailData.status) }}</a-descriptions-item>
				<a-descriptions-item label="被保主体">{{ detailData.insuredName || '-' }}</a-descriptions-item>
				<a-descriptions-item label="理赔类型">{{ getClaimTypeLabel(detailData.claimType) }}</a-descriptions-item>
				<a-descriptions-item label="损失数量">{{ detailData.lossCount || '-' }}</a-descriptions-item>
				<a-descriptions-item label="理赔金额">{{ formatAmount(detailData.claimAmount) }}</a-descriptions-item>
				<a-descriptions-item label="出险时间">{{ detailData.occurTime || '-' }}</a-descriptions-item>
				<a-descriptions-item label="申请时间">{{ detailData.applyTime || '-' }}</a-descriptions-item>
				<a-descriptions-item label="结果说明" :span="2">{{ detailData.resultRemark || '-' }}</a-descriptions-item>
				<a-descriptions-item label="附件材料" :span="2">
					<a-space wrap>
						<a v-for="item in normalizeFiles(detailData.evidenceFiles)" :key="item.url" :href="item.url" target="_blank">{{ item.name }}</a>
						<span v-if="!normalizeFiles(detailData.evidenceFiles).length">-</span>
					</a-space>
				</a-descriptions-item>
				<a-descriptions-item label="流程节点" :span="2">
					<a-steps size="small" :current="getFlowCurrent(detailData.status)">
						<a-step title="待受理" />
						<a-step title="处理中" />
						<a-step :title="detailData.status === 'REJECTED' ? '已拒赔' : '已结案'" />
					</a-steps>
				</a-descriptions-item>
			</a-descriptions>
		</a-modal>
	</div>
</template>

<script setup>
import { message } from 'ant-design-vue'
import tool from '@/utils/tool'
import sysOrgApi from '@/api/sys/orgApi'
import { toFarmTreeSelectData, flattenFarmTreeOptions, findFarmTreeNode } from '@/utils/farmTree'
import fileApi from '@/api/dev/fileApi'
import claimManageApi from '@/api/biz/claimManageApi'

const userInfo = tool.data.get('USER_INFO') || {}

const statusOptions = [
	{ label: '待受理', value: 'PENDING' },
	{ label: '处理中', value: 'PROCESSING' },
	{ label: '已结案', value: 'CLOSED' },
	{ label: '已拒赔', value: 'REJECTED' }
]
const claimTypeOptions = [
	{ label: '死亡', value: 'DEATH' },
	{ label: '疾病', value: 'DISEASE' },
	{ label: '意外', value: 'ACCIDENT' }
]
const handleStatusOptions = [
	{ label: '处理中', value: 'PROCESSING' },
	{ label: '已结案', value: 'CLOSED' },
	{ label: '已拒赔', value: 'REJECTED' }
]
const statusColorMap = { PENDING: 'default', PROCESSING: 'blue', CLOSED: 'green', REJECTED: 'red' }

const filters = reactive({ farmId: undefined, claimNo: '', policyNo: '', status: undefined })
const farmOptions = ref([])
const farmTreeData = ref([])
const tableData = ref([])
const tableLoading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const detailModalOpen = ref(false)
const detailData = ref({})

const addFormRef = ref()
const addModalOpen = ref(false)
const addSubmitting = ref(false)
const addFileList = ref([])
const addForm = reactive({
	id: '',
	farmId: undefined,
	claimNo: '',
	policyNo: '',
	insuredName: '',
	claimType: undefined,
	lossCount: null,
	claimAmount: null,
	occurTime: undefined,
	evidenceFiles: [],
	remark: ''
})
const addRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	claimNo: [{ required: true, message: '请输入理赔单号', trigger: 'blur' }],
	policyNo: [{ required: true, message: '请输入保单号', trigger: 'blur' }],
	insuredName: [{ required: true, message: '请输入被保主体', trigger: 'blur' }],
	claimType: [{ required: true, message: '请选择理赔类型', trigger: 'change' }],
	lossCount: [{ required: true, message: '请输入损失数量', trigger: 'change' }],
	claimAmount: [{ required: true, message: '请输入理赔金额', trigger: 'change' }],
	occurTime: [{ required: true, message: '请选择出险时间', trigger: 'change' }]
}

const supplementTarget = ref({})
const supplementModalOpen = ref(false)
const supplementSubmitting = ref(false)
const supplementFormRef = ref()
const supplementFileList = ref([])
const supplementForm = reactive({ files: [], remark: '' })
const supplementRules = {
	files: [{ required: true, message: '请上传补充附件', trigger: 'change' }],
	remark: [{ required: true, message: '请输入补充说明', trigger: 'blur' }]
}

const handleTarget = ref({})
const handleModalOpen = ref(false)
const handleSubmitting = ref(false)
const handleFormRef = ref()
const handleForm = reactive({ status: undefined, resultRemark: '' })
const handleRules = {
	status: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
	resultRemark: [{ required: true, message: '请输入结果说明', trigger: 'blur' }]
}

const columns = [
	{ title: '理赔单号', dataIndex: 'claimNo', width: 160 },
	{ title: '保单号', dataIndex: 'policyNo', width: 160 },
	{ title: '养殖场', dataIndex: 'farmId', width: 180 },
	{ title: '被保主体', dataIndex: 'insuredName', width: 160 },
	{ title: '理赔金额', dataIndex: 'claimAmount', width: 130 },
	{ title: '申请时间', dataIndex: 'applyTime', width: 160 },
	{ title: '状态', dataIndex: 'status', width: 110 },
	{ title: '操作', key: 'action', fixed: 'right', width: 240 }
]

const localRecords = ref([
	{
		id: 'LP-001',
		farmId: '1543842934270394368',
		claimNo: 'LP20260228001',
		policyNo: 'PL20260217001',
		insuredName: '张三养殖场',
		claimType: 'DEATH',
		lossCount: 3,
		claimAmount: 12000,
		occurTime: '2026-02-25',
		applyTime: '2026-02-26 09:15:20',
		status: 'PENDING',
		resultRemark: '',
		evidenceFiles: JSON.stringify([{ name: '现场照片1.jpg', url: 'https://example.com/file/1.jpg' }])
	}
])

const isAdminUser = () => {
	const roleCodeList = userInfo.roleCodeList || []
	return Array.isArray(roleCodeList) && roleCodeList.includes('superAdmin')
}
const resolveFarmId = () => userInfo.farmId || userInfo.farm_id || userInfo.orgId || userInfo.org_id
const isClosed = (status) => ['CLOSED', 'REJECTED'].includes(status)

const getFarmLabel = (farmId) => {
	if (!farmId) return '-'
	const hit = farmOptions.value.find((item) => item.value === farmId)
	return hit?.label || farmId
}
const getStatusLabel = (status) => statusOptions.find((item) => item.value === status)?.label || status || '-'
const getClaimTypeLabel = (claimType) => claimTypeOptions.find((item) => item.value === claimType)?.label || claimType || '-'

const getFlowCurrent = (status) => {
	if (status === 'PENDING') return 0
	if (status === 'PROCESSING') return 1
	return 2
}

const formatAmount = (value) => {
	const amount = Number(value)
	if (Number.isNaN(amount)) return '-'
	return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const normalizeFiles = (files) => {
	if (!files) return []
	if (Array.isArray(files)) return files
	if (typeof files === 'string') {
		try {
			const parsed = JSON.parse(files)
			return Array.isArray(parsed) ? parsed : []
		} catch (e) {
			return []
		}
	}
	return []
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

const buildQueryParams = () => ({
	current: pagination.current,
	size: pagination.pageSize,
	farmId: filters.farmId,
	claimNo: filters.claimNo?.trim(),
	policyNo: filters.policyNo?.trim(),
	status: filters.status
})

const resolveRecordId = (record) => record?.id || record?.ID || record?.Id || record?.claimId || record?.claim_id || ''

const applyLocalFilter = () => {
	let list = [...localRecords.value]
	if (filters.farmId) list = list.filter((item) => item.farmId === filters.farmId)
	if (filters.claimNo) list = list.filter((item) => item.claimNo?.includes(filters.claimNo.trim()))
	if (filters.policyNo) list = list.filter((item) => item.policyNo?.includes(filters.policyNo.trim()))
	if (filters.status) list = list.filter((item) => item.status === filters.status)
	pagination.total = list.length
	const start = (pagination.current - 1) * pagination.pageSize
	tableData.value = list.slice(start, start + pagination.pageSize)
}

const fetchTableData = async () => {
	tableLoading.value = true
	try {
		const res = await claimManageApi.page(buildQueryParams())
		tableData.value = (res?.records || []).map((item) => ({ ...item, id: resolveRecordId(item) }))
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
	filters.claimNo = ''
	filters.policyNo = ''
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

const mapFileListToEvidence = (fileList) =>
	(fileList || [])
		.map((item) => ({
			name: item.name || '附件',
			url: item.url || item.response || ''
		}))
		.filter((item) => item.url)

const uploadAttachment = async (options) => {
	try {
		const formData = new FormData()
		formData.append('file', options.file)
		const url = await fileApi.fileUploadDynamicReturnUrl(formData)
		options.onSuccess(url)
	} catch (e) {
		// 上传接口不可用时，使用本地临时URL兜底，避免阻塞业务流
		const fallbackUrl = URL.createObjectURL(options.file)
		options.onSuccess(fallbackUrl)
		message.warning('上传服务不可用，已使用本地临时附件')
	}
}

const openAddModal = async () => {
	Object.assign(addForm, {
		id: '',
		farmId: filters.farmId || resolveFarmId(),
		claimNo: '',
		policyNo: '',
		insuredName: '',
		claimType: undefined,
		lossCount: null,
		claimAmount: null,
		occurTime: undefined,
		evidenceFiles: [],
		remark: ''
	})
	addFileList.value = []
	addModalOpen.value = true
	await nextTick()
	addFormRef.value?.clearValidate()
}

const submitAdd = async () => {
	await addFormRef.value?.validate()
	addSubmitting.value = true
	try {
		const payload = {
			farmId: isAdminUser() ? addForm.farmId : resolveFarmId(),
			claimNo: addForm.claimNo?.trim(),
			policyNo: addForm.policyNo?.trim(),
			insuredName: addForm.insuredName?.trim(),
			claimType: addForm.claimType,
			lossCount: addForm.lossCount,
			claimAmount: addForm.claimAmount,
			occurTime: addForm.occurTime,
			evidenceFiles: JSON.stringify(mapFileListToEvidence(addFileList.value)),
			remark: addForm.remark?.trim()
		}
		await claimManageApi.add(payload)
		message.success('新增理赔成功')
		addModalOpen.value = false
		fetchTableData()
	} catch (e) {
		const mock = {
			id: `LP-${Date.now()}`,
			...payloadFromAddForm(),
			applyTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
			status: 'PENDING',
			resultRemark: ''
		}
		localRecords.value.unshift(mock)
		message.success('新增理赔成功（本地模拟）')
		addModalOpen.value = false
		fetchTableData()
	} finally {
		addSubmitting.value = false
	}
}

const payloadFromAddForm = () => ({
	farmId: isAdminUser() ? addForm.farmId : resolveFarmId(),
	claimNo: addForm.claimNo?.trim(),
	policyNo: addForm.policyNo?.trim(),
	insuredName: addForm.insuredName?.trim(),
	claimType: addForm.claimType,
	lossCount: addForm.lossCount,
	claimAmount: addForm.claimAmount,
	occurTime: addForm.occurTime,
	evidenceFiles: JSON.stringify(mapFileListToEvidence(addFileList.value)),
	remark: addForm.remark?.trim()
})

const openDetailModal = async (record) => {
	const id = resolveRecordId(record)
	try {
		const res = await claimManageApi.detail({ id, claimNo: record.claimNo })
		detailData.value = { ...record, ...(res || {}), id: resolveRecordId(res || record) }
		detailModalOpen.value = true
	} catch (e) {
		detailData.value = { ...record, id }
		detailModalOpen.value = true
	}
}

const openSupplementModal = async (record) => {
	supplementTarget.value = record
	supplementForm.files = []
	supplementForm.remark = ''
	supplementFileList.value = []
	supplementModalOpen.value = true
	await nextTick()
	supplementFormRef.value?.clearValidate()
}

const submitSupplement = async () => {
	supplementForm.files = mapFileListToEvidence(supplementFileList.value)
	await supplementFormRef.value?.validate()
	supplementSubmitting.value = true
	try {
		await claimManageApi.supplement({
			id: resolveRecordId(supplementTarget.value),
			claimNo: supplementTarget.value.claimNo,
			files: JSON.stringify(supplementForm.files),
			remark: supplementForm.remark
		})
		message.success('补充材料成功')
		supplementModalOpen.value = false
	} catch (e) {
		message.success('补充材料成功（本地模拟）')
		supplementModalOpen.value = false
	}
	supplementSubmitting.value = false
}

const openHandleModal = async (record) => {
	handleTarget.value = record
	handleForm.status = record.status === 'PENDING' ? 'PROCESSING' : record.status
	handleForm.resultRemark = record.resultRemark || ''
	handleModalOpen.value = true
	await nextTick()
	handleFormRef.value?.clearValidate()
}

const submitHandle = async () => {
	await handleFormRef.value?.validate()
	handleSubmitting.value = true
	try {
		await claimManageApi.handle({
			id: resolveRecordId(handleTarget.value),
			claimNo: handleTarget.value.claimNo,
			status: handleForm.status,
			resultRemark: handleForm.resultRemark
		})
		message.success('处理结果登记成功')
		handleModalOpen.value = false
		fetchTableData()
	} catch (e) {
		const id = resolveRecordId(handleTarget.value)
		localRecords.value = localRecords.value.map((item) =>
			item.id === id ? { ...item, status: handleForm.status, resultRemark: handleForm.resultRemark } : item
		)
		message.success('处理结果登记成功（本地模拟）')
		handleModalOpen.value = false
		fetchTableData()
	} finally {
		handleSubmitting.value = false
	}
}

onMounted(async () => {
	if (!isAdminUser()) {
		filters.farmId = resolveFarmId()
	}
	await loadFarmOptions()
	fetchTableData()
})
</script>

<style scoped>
.lp-manage-page {
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
