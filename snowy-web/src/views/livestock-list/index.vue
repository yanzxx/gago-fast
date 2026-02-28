<template>
	<div class="livestock-list-page">
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
						@change="onFarmChange"
					/>
				</a-form-item>
				<a-form-item label="畜种名称">
					<a-select
						v-model:value="filters.speciesName"
						allow-clear
						placeholder="请选择畜种名称"
						style="width: 180px"
						:options="speciesOptions"
					/>
				</a-form-item>
				<a-form-item label="项圈编号">
					<a-input v-model:value="filters.collarNo" allow-clear placeholder="请输入项圈编号" />
				</a-form-item>
				<a-form-item label="状态">
					<a-select
						v-model:value="filters.status"
						allow-clear
						placeholder="请选择状态"
						style="width: 160px"
						:options="statusOptions"
					/>
				</a-form-item>
				<a-form-item label="防疫状态">
					<a-select
						v-model:value="filters.immunityStatus"
						allow-clear
						placeholder="请选择防疫状态"
						style="width: 180px"
						:options="immunityStatusOptions"
					/>
				</a-form-item>
				<a-form-item label="登记时间">
					<a-range-picker v-model:value="filters.registerRange" value-format="YYYY-MM-DD" />
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
			<div class="section-title">登记列表</div>
			<div class="toolbar">
				<a-space>
					<a-button type="primary" @click="openAddModal">新增登记</a-button>
					<a-button :disabled="!selectedRowKeys.length">批量导出</a-button>
				</a-space>
			</div>

			<a-table
				row-key="id"
				:data-source="pagedData"
				:columns="columns"
				:pagination="false"
				:loading="tableLoading"
				:row-selection="{ selectedRowKeys, onChange: onSelectChange }"
				size="middle"
			>
					<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'status'">
						<a-tag :color="statusColorMap[record.status] || 'default'">
							{{ getLivestockStatusLabel(record.status) }}
						</a-tag>
					</template>
					<template v-else-if="column.dataIndex === 'immunityStatus'">
						<a-tag :color="immunityColorMap[record.immunityStatus] || 'default'">
							{{ getImmunityStatusLabel(record.immunityStatus) }}
						</a-tag>
					</template>
					<template v-else-if="column.dataIndex === 'gender'">
						{{ getGenderLabel(record.gender) }}
					</template>
					<template v-else-if="column.key === 'action'">
						<a-space size="small">
							<a-button type="link" size="small" @click="openDetailModal(record)">详情</a-button>
							<a-button type="link" size="small" @click="openEditModal(record)">编辑</a-button>
							<a-button type="link" size="small" danger>删除</a-button>
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
			v-model:visible="addModalOpen"
			title="新增牲畜登记"
			:confirm-loading="addSubmitting"
			width="760px"
			ok-text="提交"
			cancel-text="取消"
			@ok="submitAdd"
		>
			<a-form ref="addFormRef" :model="addForm" :rules="addRules" layout="vertical">
				<a-row :gutter="16">
					<a-col :span="12">
						<a-form-item v-if="isAdminUser()" label="养殖场" name="farmId">
							<a-select
								v-model:value="addForm.farmId"
								placeholder="请选择养殖场"
								:options="farmOptions"
								show-search
								:filter-option="(input, option) => (option?.label || '').includes(input)"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="畜种名称" name="speciesName">
							<a-select
								v-model:value="addForm.speciesName"
								placeholder="请选择畜种名称"
								:options="speciesOptions"
								show-search
								:filter-option="(input, option) => (option?.label || '').includes(input)"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="出生日期" name="birthDate">
							<a-date-picker v-model:value="addForm.birthDate" value-format="YYYY-MM-DD" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="防疫状态" name="immunityStatus">
							<a-select v-model:value="addForm.immunityStatus" placeholder="请选择防疫状态" :options="immunityStatusOptions" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="近期防疫注射时间" name="lastImmunityDate">
							<a-date-picker
								v-model:value="addForm.lastImmunityDate"
								value-format="YYYY-MM-DD"
								style="width: 100%"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="项圈编号" name="collarNo">
							<a-input v-model:value="addForm.collarNo" maxlength="64" placeholder="请输入项圈编号" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="性别" name="gender">
							<a-select v-model:value="addForm.gender" allow-clear placeholder="请选择性别" :options="genderOptions" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="圈舍" name="penNo">
							<a-input v-model:value="addForm.penNo" maxlength="64" placeholder="请输入圈舍" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="备注" name="remark">
							<a-textarea v-model:value="addForm.remark" :rows="3" maxlength="500" placeholder="请输入备注" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="附件图片">
							<a-upload
								list-type="picture-card"
								:file-list="addImageFileList"
								:custom-request="uploadLivestockImage"
								@remove="removeLivestockImage"
								accept="image/png,image/jpeg,image/jpg"
							>
								<div>上传</div>
							</a-upload>
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
		</a-modal>

		<a-modal v-model:visible="detailModalOpen" title="牲畜登记详情" :footer="null" width="760px">
			<a-descriptions :column="2" bordered size="small">
				<a-descriptions-item label="牲畜编号">{{ detailData.livestockNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="养殖场">{{ getFarmLabel(detailData.farmId) }}</a-descriptions-item>
				<a-descriptions-item label="畜种名称">{{ detailData.speciesName || '-' }}</a-descriptions-item>
				<a-descriptions-item label="出生日期">{{ detailData.birthDate || '-' }}</a-descriptions-item>
				<a-descriptions-item label="状态">{{ getLivestockStatusLabel(detailData.status) }}</a-descriptions-item>
				<a-descriptions-item label="防疫状态">{{ getImmunityStatusLabel(detailData.immunityStatus) }}</a-descriptions-item>
				<a-descriptions-item label="近期防疫注射时间">{{ detailData.lastImmunityDate || '-' }}</a-descriptions-item>
				<a-descriptions-item label="项圈编号">{{ detailData.collarNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="性别">{{ getGenderLabel(detailData.gender) }}</a-descriptions-item>
				<a-descriptions-item label="圈舍">{{ detailData.penNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="登记时间">{{ detailData.registerDate || '-' }}</a-descriptions-item>
				<a-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</a-descriptions-item>
				<a-descriptions-item label="附件图片" :span="2">
					<a-space wrap>
						<a-image
							v-for="(url, index) in detailImageUrls"
							:key="`${url}-${index}`"
							:src="url"
							:width="72"
							:height="72"
							style="object-fit: cover"
						/>
						<span v-if="!detailImageUrls.length">-</span>
					</a-space>
				</a-descriptions-item>
			</a-descriptions>
		</a-modal>

		<a-modal
			v-model:visible="editModalOpen"
			title="编辑牲畜登记"
			:confirm-loading="editSubmitting"
			width="760px"
			ok-text="保存"
			cancel-text="取消"
			@ok="submitEdit"
		>
			<a-form ref="editFormRef" :model="editForm" :rules="editRules" layout="vertical">
				<a-row :gutter="16">
					<a-col :span="12">
						<a-form-item v-if="isAdminUser()" label="养殖场" name="farmId">
							<a-select
								v-model:value="editForm.farmId"
								placeholder="请选择养殖场"
								:options="farmOptions"
								show-search
								:filter-option="(input, option) => (option?.label || '').includes(input)"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="畜种名称" name="speciesName">
							<a-select
								v-model:value="editForm.speciesName"
								placeholder="请选择畜种名称"
								:options="speciesOptions"
								show-search
								:filter-option="(input, option) => (option?.label || '').includes(input)"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="出生日期" name="birthDate">
							<a-date-picker v-model:value="editForm.birthDate" value-format="YYYY-MM-DD" style="width: 100%" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="状态" name="status">
							<a-select v-model:value="editForm.status" placeholder="请选择状态" :options="statusOptions" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="防疫状态" name="immunityStatus">
							<a-select v-model:value="editForm.immunityStatus" placeholder="请选择防疫状态" :options="immunityStatusOptions" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="近期防疫注射时间" name="lastImmunityDate">
							<a-date-picker
								v-model:value="editForm.lastImmunityDate"
								value-format="YYYY-MM-DD"
								style="width: 100%"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="项圈编号" name="collarNo">
							<a-input v-model:value="editForm.collarNo" maxlength="64" placeholder="请输入项圈编号" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="性别" name="gender">
							<a-select v-model:value="editForm.gender" allow-clear placeholder="请选择性别" :options="genderOptions" />
						</a-form-item>
					</a-col>
					<a-col :span="12">
						<a-form-item label="圈舍" name="penNo">
							<a-input v-model:value="editForm.penNo" maxlength="64" placeholder="请输入圈舍" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="备注" name="remark">
							<a-textarea v-model:value="editForm.remark" :rows="3" maxlength="500" placeholder="请输入备注" />
						</a-form-item>
					</a-col>
					<a-col :span="24">
						<a-form-item label="附件图片">
							<a-upload
								list-type="picture-card"
								:file-list="editImageFileList"
								:custom-request="uploadEditImage"
								@remove="removeEditImage"
								accept="image/png,image/jpeg,image/jpg"
							>
								<div>上传</div>
							</a-upload>
						</a-form-item>
					</a-col>
				</a-row>
			</a-form>
		</a-modal>
	</div>
</template>

<script setup>
import tool from '@/utils/tool'
import { message } from 'ant-design-vue'
import dictApi from '@/api/dev/dictApi'
import livestockApi from '@/api/biz/livestockApi'
import sysOrgApi from '@/api/sys/orgApi'
import { toFarmTreeSelectData, flattenFarmTreeOptions, findFarmTreeNode } from '@/utils/farmTree'
import fileApi from '@/api/dev/fileApi'

const defaultFilters = () => ({
	farmId: undefined,
	speciesName: undefined,
	collarNo: '',
	status: undefined,
	immunityStatus: undefined,
	registerRange: []
})

const filters = reactive(defaultFilters())
const selectedRowKeys = ref([])
const tableData = ref([])
const tableLoading = ref(false)
const userInfo = tool.data.get('USER_INFO') || {}
const farmOptions = ref([])
const farmTreeData = ref([])
const genderOptions = ref([])
const pagination = reactive({
	current: 1,
	pageSize: 10,
	total: 0
})
const addFormRef = ref()
const addModalOpen = ref(false)
const addSubmitting = ref(false)
const addImageFileList = ref([])
const detailModalOpen = ref(false)
const detailData = ref({})
const editFormRef = ref()
const editModalOpen = ref(false)
const editSubmitting = ref(false)
const editImageFileList = ref([])

const defaultAddForm = () => ({
	farmId: undefined,
	speciesName: undefined,
	birthDate: undefined,
	immunityStatus: undefined,
	lastImmunityDate: undefined,
	collarNo: '',
	gender: undefined,
	penNo: '',
	remark: ''
})
const addForm = reactive(defaultAddForm())
const defaultEditForm = () => ({
	id: '',
	livestockNo: '',
	farmId: undefined,
	speciesName: undefined,
	birthDate: undefined,
	status: undefined,
	immunityStatus: undefined,
	lastImmunityDate: undefined,
	collarNo: '',
	gender: undefined,
	penNo: '',
	remark: ''
})
const editForm = reactive(defaultEditForm())
const addRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	speciesName: [{ required: true, message: '请选择畜种名称', trigger: 'change' }],
	birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
	immunityStatus: [{ required: true, message: '请选择防疫状态', trigger: 'change' }],
	lastImmunityDate: [{ required: true, message: '请选择近期防疫注射时间', trigger: 'change' }],
	collarNo: [{ required: true, message: '请输入项圈编号', trigger: 'blur' }]
}
const editRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	speciesName: [{ required: true, message: '请选择畜种名称', trigger: 'change' }],
	birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
	status: [{ required: true, message: '请选择状态', trigger: 'change' }],
	immunityStatus: [{ required: true, message: '请选择防疫状态', trigger: 'change' }],
	lastImmunityDate: [{ required: true, message: '请选择近期防疫注射时间', trigger: 'change' }],
	collarNo: [{ required: true, message: '请输入项圈编号', trigger: 'blur' }]
}

const fallbackImmunityStatusOptions = [
	{ label: '已接种', value: 'IMMUNIZED' },
	{ label: '未接种', value: 'NOT_IMMUNIZED' },
	{ label: '已过期', value: 'EXPIRED' }
]
const fallbackStatusOptions = [
	{ label: '存栏', value: 'IN_STOCK' },
	{ label: '出栏', value: 'OUT_STOCK' },
	{ label: '死亡', value: 'DEAD' }
]
const immunityStatusOptions = ref([...fallbackImmunityStatusOptions])
const statusOptions = ref([...fallbackStatusOptions])

const fallbackSpeciesOptions = [
	{ label: '骆驼', value: '骆驼', code: 'CAMEL' },
	{ label: '牛', value: '牛', code: 'CATTLE' },
	{ label: '羊', value: '羊', code: 'SHEEP' },
	{ label: '马', value: '马', code: 'HORSE' },
	{ label: '驴', value: '驴', code: 'DONKEY' },
	{ label: '牦牛', value: '牦牛', code: 'YAK' }
]
const speciesOptions = ref([...fallbackSpeciesOptions])
const livestockStatusDictCode = 'LIVESTOCK_STATUS'
const immunityStatusDictCode = 'LIVESTOCK_IMMUNITY_STATUS'
const dictCategory = 'BIZ'

const statusColorMap = {
	IN_STOCK: 'green',
	OUT_STOCK: 'blue',
	DEAD: 'red'
}

const immunityColorMap = {
	IMMUNIZED: 'green',
	NOT_IMMUNIZED: 'gold',
	EXPIRED: 'red'
}

const columns = [
	{ title: '牲畜编号', dataIndex: 'livestockNo', width: 140 },
	{ title: '畜种名称', dataIndex: 'speciesName', width: 100 },
	{ title: '出生日期', dataIndex: 'birthDate', width: 120 },
	{ title: '状态', dataIndex: 'status', width: 90 },
	{ title: '防疫状态', dataIndex: 'immunityStatus', width: 100 },
	{ title: '近期防疫注射时间', dataIndex: 'lastImmunityDate', width: 150 },
	{ title: '项圈编号', dataIndex: 'collarNo', width: 120 },
	{ title: '性别', dataIndex: 'gender', width: 80 },
	{ title: '圈舍', dataIndex: 'penNo', width: 100 },
	{ title: '登记时间', dataIndex: 'registerDate', width: 120 },
	{ title: '操作', key: 'action', fixed: 'right', width: 170 }
]

const pagedData = computed(() => tableData.value)

const onSearch = () => {
	pagination.current = 1
	fetchTableData()
}

const onReset = () => {
	Object.assign(filters, defaultFilters())
	selectedRowKeys.value = []
	pagination.current = 1
	loadSpeciesOptions()
	fetchTableData()
}

const onSelectChange = (keys) => {
	selectedRowKeys.value = keys
}

const mapDictChildrenToCodeOptions = (children = []) => {
	return children
		.map((item) => ({
			label: item.name || item.dictLabel,
			value: item.dictValue
		}))
		.filter((item) => !!item.label && !!item.value)
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

const getLivestockStatusLabel = (value) => getDictLabel(livestockStatusDictCode, value, statusOptions.value)
const getImmunityStatusLabel = (value) =>
	getDictLabel(immunityStatusDictCode, value, immunityStatusOptions.value)

const getGenderLabel = (genderValue) => {
	if (!genderValue) return '-'
	const label = tool.dictTypeData('GENDER', genderValue)
	return label && label !== '无此字典项' && label !== '无此字典' && label !== '需重新登录' ? label : genderValue
}

const getFarmLabel = (farmId) => {
	if (!farmId) return '-'
	const hit = farmOptions.value.find((item) => item.value === farmId)
	return hit?.label || farmId
}

const findDictNodeByCode = (tree = [], code) => {
	for (const node of tree) {
		if (node?.dictValue === code) return node
		const hit = findDictNodeByCode(node?.children || [], code)
		if (hit) return hit
	}
	return null
}

const resolveFarmId = () => {
	return userInfo.farmId || userInfo.farm_id || userInfo.orgId || userInfo.org_id
}

const isAdminUser = () => {
	const roleCodeList = userInfo.roleCodeList || []
	return Array.isArray(roleCodeList) && roleCodeList.includes('superAdmin')
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
	const farmId = filters.farmId
	// 优先走农场维度接口
	try {
		const remoteOptions = await livestockApi.speciesOptions({ farmId })
		speciesOptions.value = remoteOptions?.length ? remoteOptions : fallbackSpeciesOptions
	} catch (e) {
		speciesOptions.value = fallbackSpeciesOptions
	}
}

const loadStatusOptions = async () => {
	const cachedStatus = mapDictChildrenToCodeOptions(tool.dictTypeList(livestockStatusDictCode))
	const cachedImmunity = mapDictChildrenToCodeOptions(tool.dictTypeList(immunityStatusDictCode))
	if (cachedStatus.length) {
		statusOptions.value = cachedStatus
	}
	if (cachedImmunity.length) {
		immunityStatusOptions.value = cachedImmunity
	}
	if (cachedStatus.length && cachedImmunity.length) {
		return
	}

	try {
		const treeData = await dictApi.dictTree({ category: dictCategory })
		const statusRoot = findDictNodeByCode(treeData || [], livestockStatusDictCode)
		const immunityRoot = findDictNodeByCode(treeData || [], immunityStatusDictCode)
		const remoteStatus = mapDictChildrenToCodeOptions(statusRoot?.children || [])
		const remoteImmunity = mapDictChildrenToCodeOptions(immunityRoot?.children || [])
		statusOptions.value = remoteStatus.length ? remoteStatus : fallbackStatusOptions
		immunityStatusOptions.value = remoteImmunity.length ? remoteImmunity : fallbackImmunityStatusOptions
	} catch (e) {
		statusOptions.value = fallbackStatusOptions
		immunityStatusOptions.value = fallbackImmunityStatusOptions
	}
}

const loadGenderOptions = async () => {
	const cached = mapDictChildrenToCodeOptions(tool.dictTypeList('GENDER'))
	if (cached.length) {
		genderOptions.value = cached
		return
	}
	const defaultGenderOptions = [
		{ label: '男', value: 'MALE' },
		{ label: '女', value: 'FEMALE' }
	]
	try {
		const treeData = await dictApi.dictTree({ category: 'SYS' })
		const genderRoot = findDictNodeByCode(treeData || [], 'GENDER')
		const remote = mapDictChildrenToCodeOptions(genderRoot?.children || [])
		genderOptions.value = remote.length ? remote : defaultGenderOptions
	} catch (e) {
		genderOptions.value = defaultGenderOptions
	}
}

const buildQueryParams = () => {
	const [registerStartDate, registerEndDate] = filters.registerRange || []
	return {
		current: pagination.current,
		size: pagination.pageSize,
		// 非管理员不强制传 farmId，后端会按数据权限自动限制可见农场
		farmId: filters.farmId,
		speciesName: filters.speciesName,
		collarNo: filters.collarNo?.trim(),
		status: filters.status,
		immunityStatus: filters.immunityStatus,
		registerStartDate,
		registerEndDate
	}
}

const fetchTableData = async () => {
	tableLoading.value = true
	try {
		const res = await livestockApi.page(buildQueryParams())
		const records = res?.records || []
		tableData.value = records.map((item) => ({
			...item,
			id: item?.id || item?.ID || item?.Id || item?.livestockId || item?.livestockID || item?.livestock_id || ''
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

const onFarmChange = () => {
	filters.speciesName = undefined
	loadSpeciesOptions()
	onSearch()
}

const openAddModal = async () => {
	Object.assign(addForm, defaultAddForm())
	addImageFileList.value = []
	addForm.farmId = filters.farmId || resolveFarmId()
	if (isAdminUser() && !addForm.farmId && farmOptions.value.length) {
		addForm.farmId = farmOptions.value[0].value
	}
	addModalOpen.value = true
	await nextTick()
	addFormRef.value?.clearValidate()
}

const uploadLivestockImage = async (options) => {
	try {
		const formData = new FormData()
		formData.append('file', options.file)
		const url = await fileApi.fileUploadDynamicReturnUrl(formData)
		addImageFileList.value = [
			...addImageFileList.value,
			{
				uid: options.file.uid,
				name: options.file.name,
				status: 'done',
				url
			}
		]
		options.onSuccess(url)
	} catch (e) {
		message.error('图片上传失败')
		options.onError(e)
	}
}

const removeLivestockImage = (file) => {
	addImageFileList.value = addImageFileList.value.filter((item) => item.uid !== file.uid)
	return true
}

const normalizeImageUrls = (imageUrls) => {
	if (!imageUrls) return []
	if (Array.isArray(imageUrls)) return imageUrls.filter(Boolean)
	if (typeof imageUrls === 'string') {
		try {
			const parsed = JSON.parse(imageUrls)
			if (Array.isArray(parsed)) {
				return parsed.filter(Boolean)
			}
		} catch (e) {
			return imageUrls
				.split(',')
				.map((item) => item.trim())
				.filter(Boolean)
		}
	}
	return []
}

const detailImageUrls = computed(() => normalizeImageUrls(detailData.value?.imageUrls))

const mapUrlsToUploadFileList = (urls = []) => {
	return (urls || []).map((url, index) => ({
		uid: `img-${index}-${Date.now()}`,
		name: `image-${index + 1}`,
		status: 'done',
		url
	}))
}

const resolveRecordId = (record) => {
	return (
		record?.id ||
		record?.ID ||
		record?.Id ||
		record?.livestockId ||
		record?.livestockID ||
		record?.livestock_id ||
		''
	)
}

const submitAdd = async () => {
	await addFormRef.value?.validate()
	addSubmitting.value = true
	try {
		const payload = {
			farmId: isAdminUser() ? addForm.farmId : resolveFarmId(),
			speciesName: addForm.speciesName,
			birthDate: addForm.birthDate,
			immunityStatus: addForm.immunityStatus,
			lastImmunityDate: addForm.lastImmunityDate,
			collarNo: addForm.collarNo?.trim(),
			gender: addForm.gender,
			penNo: addForm.penNo?.trim(),
			remark: addForm.remark?.trim(),
			imageUrls: JSON.stringify((addImageFileList.value || []).map((item) => item.url).filter(Boolean))
		}
		await livestockApi.add(payload)
		message.success('新增成功')
		addModalOpen.value = false
		await loadSpeciesOptions()
		onSearch()
	} finally {
		addSubmitting.value = false
	}
}

const openDetailModal = async (record) => {
	const recordId = resolveRecordId(record)
	const livestockNo = record?.livestockNo
	if (!recordId && !livestockNo) {
		message.error('当前记录缺少ID和牲畜编号，无法查看详情')
		console.warn('[livestock-list] detail missing id:', record)
		return
	}
	try {
		const detail = await livestockApi.detail({ id: recordId, livestockNo })
		detailData.value = {
			...(detail || {}),
			id: detail?.id || recordId
		}
		detailModalOpen.value = true
	} catch (e) {
		message.error('加载详情失败，请稍后重试')
	}
}

const openEditModal = async (record) => {
	const recordId = resolveRecordId(record)
	const livestockNo = record?.livestockNo
	if (!recordId && !livestockNo) {
		message.error('当前记录缺少ID和牲畜编号，无法编辑')
		console.warn('[livestock-list] edit missing id:', record)
		return
	}
	try {
		const detail = await livestockApi.detail({ id: recordId, livestockNo })
		Object.assign(editForm, defaultEditForm(), {
			id: detail?.id || recordId,
			livestockNo: detail?.livestockNo || livestockNo,
			farmId: detail?.farmId || resolveFarmId(),
			speciesName: detail?.speciesName,
			birthDate: detail?.birthDate,
			status: detail?.status,
			immunityStatus: detail?.immunityStatus,
			lastImmunityDate: detail?.lastImmunityDate,
			collarNo: detail?.collarNo,
			gender: detail?.gender,
			penNo: detail?.penNo,
			remark: detail?.remark
		})
		editImageFileList.value = mapUrlsToUploadFileList(normalizeImageUrls(detail?.imageUrls))
		editModalOpen.value = true
		await nextTick()
		editFormRef.value?.clearValidate()
	} catch (e) {
		message.error('加载编辑数据失败，请稍后重试')
	}
}

const uploadEditImage = async (options) => {
	try {
		const formData = new FormData()
		formData.append('file', options.file)
		const url = await fileApi.fileUploadDynamicReturnUrl(formData)
		editImageFileList.value = [
			...editImageFileList.value,
			{
				uid: options.file.uid,
				name: options.file.name,
				status: 'done',
				url
			}
		]
		options.onSuccess(url)
	} catch (e) {
		message.error('图片上传失败')
		options.onError(e)
	}
}

const removeEditImage = (file) => {
	editImageFileList.value = editImageFileList.value.filter((item) => item.uid !== file.uid)
	return true
}

const submitEdit = async () => {
	await editFormRef.value?.validate()
	editSubmitting.value = true
	try {
		const payload = {
			id: editForm.id,
			livestockNo: editForm.livestockNo,
			farmId: isAdminUser() ? editForm.farmId : resolveFarmId(),
			speciesName: editForm.speciesName,
			birthDate: editForm.birthDate,
			status: editForm.status,
			immunityStatus: editForm.immunityStatus,
			lastImmunityDate: editForm.lastImmunityDate,
			collarNo: editForm.collarNo?.trim(),
			gender: editForm.gender,
			penNo: editForm.penNo?.trim(),
			remark: editForm.remark?.trim(),
			imageUrls: JSON.stringify((editImageFileList.value || []).map((item) => item.url).filter(Boolean))
		}
		await livestockApi.edit(payload)
		message.success('编辑成功')
		editModalOpen.value = false
		await loadSpeciesOptions()
		fetchTableData()
	} finally {
		editSubmitting.value = false
	}
}

onMounted(() => {
	loadFarmOptions()
	loadSpeciesOptions()
	loadStatusOptions()
	loadGenderOptions()
	fetchTableData()
})
</script>

<style scoped>
.livestock-list-page {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.filter-card {
	border-radius: 12px;
}

.list-card {
	border-radius: 12px;
}

.section-title {
	font-size: 15px;
	font-weight: 600;
	color: #1f2a22;
	margin-bottom: 12px;
}

.filter-form {
	row-gap: 8px;
}

.toolbar {
	display: flex;
	justify-content: space-between;
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
	overflow: hidden;
	text-overflow: ellipsis;
}

:deep(.ant-table-tbody > tr > td) {
	text-align: center;
}
</style>
