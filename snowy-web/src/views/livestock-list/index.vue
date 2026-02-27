<template>
	<div class="livestock-list-page">
		<a-card :bordered="false" class="filter-card">
			<div class="section-title">筛选条件</div>
			<a-form layout="inline" :model="filters" class="filter-form">
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
					<a-button type="primary">新增登记</a-button>
					<a-button :disabled="!selectedRowKeys.length">批量导出</a-button>
				</a-space>
			</div>

			<a-table
				row-key="livestockNo"
				:data-source="pagedData"
				:columns="columns"
				:pagination="false"
				:row-selection="{ selectedRowKeys, onChange: onSelectChange }"
				size="middle"
			>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'status'">
						<a-tag :color="statusColorMap[record.status]">{{ record.status }}</a-tag>
					</template>
					<template v-else-if="column.dataIndex === 'immunityStatus'">
						<a-tag :color="immunityColorMap[record.immunityStatus]">{{ record.immunityStatus }}</a-tag>
					</template>
					<template v-else-if="column.key === 'action'">
						<a-space size="small">
							<a-button type="link" size="small">详情</a-button>
							<a-button type="link" size="small">编辑</a-button>
							<a-button type="link" size="small" danger>删除</a-button>
						</a-space>
					</template>
				</template>
			</a-table>

			<div class="pagination-wrap">
				<a-pagination
					v-model:current="pagination.current"
					v-model:pageSize="pagination.pageSize"
					:total="filteredData.length"
					:show-size-changer="true"
					:show-total="(total) => `共 ${total} 条`"
				/>
			</div>
		</a-card>
	</div>
</template>

<script setup>
import dayjs from 'dayjs'

const defaultFilters = () => ({
	speciesName: undefined,
	collarNo: '',
	immunityStatus: undefined,
	registerRange: []
})

const filters = reactive(defaultFilters())
const selectedRowKeys = ref([])
const pagination = reactive({
	current: 1,
	pageSize: 10
})

const immunityStatusOptions = [
	{ label: '已接种', value: '已接种' },
	{ label: '未接种', value: '未接种' },
	{ label: '已过期', value: '已过期' }
]

const speciesOptions = ref([])

const statusColorMap = {
	存栏: 'green',
	出栏: 'blue',
	死亡: 'red'
}

const immunityColorMap = {
	已接种: 'green',
	未接种: 'gold',
	已过期: 'red'
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

const rawData = ref([
	{
		livestockNo: 'LM-20260001',
		speciesName: '骆驼',
		birthDate: '2024-03-12',
		status: '存栏',
		immunityStatus: '已接种',
		lastImmunityDate: '2026-02-03',
		collarNo: 'COL-09001',
		gender: '公',
		penNo: 'A-01',
		registerDate: '2026-02-01',
		updatedAt: '2026-02-27 10:25:31'
	},
	{
		livestockNo: 'LM-20260002',
		speciesName: '牛',
		birthDate: '2023-09-01',
		status: '出栏',
		immunityStatus: '未接种',
		lastImmunityDate: '-',
		collarNo: 'COL-09002',
		gender: '母',
		penNo: 'B-02',
		registerDate: '2026-02-10',
		updatedAt: '2026-02-27 09:13:05'
	},
	{
		livestockNo: 'LM-20260003',
		speciesName: '羊',
		birthDate: '2024-01-18',
		status: '死亡',
		immunityStatus: '已过期',
		lastImmunityDate: '2025-01-02',
		collarNo: 'COL-09003',
		gender: '公',
		penNo: 'C-03',
		registerDate: '2026-02-18',
		updatedAt: '2026-02-26 14:40:17'
	}
])

const filteredData = computed(() => {
	const [startDate, endDate] = filters.registerRange || []

	return rawData.value.filter((item) => {
		const matchSpecies = !filters.speciesName || item.speciesName === filters.speciesName
		const matchCollar = !filters.collarNo || item.collarNo.includes(filters.collarNo.trim())
		const matchImmunity = !filters.immunityStatus || item.immunityStatus === filters.immunityStatus
		const matchRange =
			!startDate ||
			!endDate ||
			(dayjs(item.registerDate).isAfter(dayjs(startDate).subtract(1, 'day')) &&
				dayjs(item.registerDate).isBefore(dayjs(endDate).add(1, 'day')))
		return matchSpecies && matchCollar && matchImmunity && matchRange
	})
})

const pagedData = computed(() => {
	const start = (pagination.current - 1) * pagination.pageSize
	const end = start + pagination.pageSize
	return filteredData.value.slice(start, end)
})

const onSearch = () => {
	pagination.current = 1
}

const onReset = () => {
	Object.assign(filters, defaultFilters())
	selectedRowKeys.value = []
	pagination.current = 1
}

const onSelectChange = (keys) => {
	selectedRowKeys.value = keys
}

const loadSpeciesOptions = async () => {
	// TODO: 后续接接口，例如：
	// const res = await livestockApi.getSpeciesOptions()
	// speciesOptions.value = (res || []).map((item) => ({ label: item.name, value: item.code }))
	speciesOptions.value = [
		{ label: '骆驼', value: '骆驼' },
		{ label: '牛', value: '牛' },
		{ label: '羊', value: '羊' }
	]
}

onMounted(() => {
	loadSpeciesOptions()
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
}

:deep(.ant-table-thead > tr > th .ant-table-column-title) {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
</style>
