<template>
	<div class="after-loan-page">
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
				<a-form-item label="时间范围">
					<a-range-picker v-model:value="filters.dateRange" value-format="YYYY-MM-DD" />
				</a-form-item>
				<a-form-item label="风险等级">
					<a-select v-model:value="filters.riskLevel" allow-clear style="width: 160px" placeholder="请选择风险等级" :options="riskLevelOptions" />
				</a-form-item>
				<a-form-item>
					<a-space>
						<a-button type="primary" @click="onSearch">查询</a-button>
						<a-button @click="onReset">重置</a-button>
					</a-space>
				</a-form-item>
			</a-form>
		</a-card>

		<a-row :gutter="12" class="kpi-row">
			<a-col :span="6" v-for="card in kpiCards" :key="card.key">
				<a-card :bordered="false" class="kpi-card">
					<div class="kpi-title">{{ card.title }}</div>
					<div class="kpi-value">{{ card.value }}</div>
				</a-card>
			</a-col>
		</a-row>

		<a-row :gutter="12" class="chart-row">
			<a-col :span="8">
				<a-card :bordered="false" class="chart-card">
					<div class="section-title">存栏统计</div>
					<div ref="stockChartRef" class="chart-box"></div>
				</a-card>
			</a-col>
			<a-col :span="8">
				<a-card :bordered="false" class="chart-card">
					<div class="section-title">抵押覆盖</div>
					<div ref="mortgageChartRef" class="chart-box"></div>
				</a-card>
			</a-col>
			<a-col :span="8">
				<a-card :bordered="false" class="chart-card">
					<div class="section-title">健康预警</div>
					<div ref="healthChartRef" class="chart-box"></div>
				</a-card>
			</a-col>
		</a-row>

		<a-card :bordered="false" class="list-card">
			<div class="section-title">异常列表</div>
			<a-table :data-source="tableData" :columns="columns" row-key="id" :pagination="false">
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'farmId'">
						{{ getFarmLabel(record.farmId) }}
					</template>
					<template v-else-if="column.dataIndex === 'riskLevel'">
						<a-tag :color="riskColorMap[record.riskLevel] || 'default'">{{ getRiskLabel(record.riskLevel) }}</a-tag>
					</template>
					<template v-else-if="column.key === 'action'">
						<a-space size="small">
							<a-button type="link" size="small" @click="openDetailModal(record)">详情</a-button>
							<a-button type="link" size="small" @click="openNoticeModal(record)">发送整改通知</a-button>
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

		<a-modal v-model:visible="detailModalOpen" title="异常详情" :footer="null" width="760px">
			<a-descriptions :column="2" bordered size="small">
				<a-descriptions-item label="养殖场">{{ getFarmLabel(detailData.farmId) }}</a-descriptions-item>
				<a-descriptions-item label="异常类型">{{ detailData.anomalyType || '-' }}</a-descriptions-item>
				<a-descriptions-item label="异常对象">{{ detailData.targetNo || '-' }}</a-descriptions-item>
				<a-descriptions-item label="风险等级">{{ getRiskLabel(detailData.riskLevel) }}</a-descriptions-item>
				<a-descriptions-item label="触发时间">{{ detailData.triggerTime || '-' }}</a-descriptions-item>
				<a-descriptions-item label="当前状态">{{ detailData.status || '-' }}</a-descriptions-item>
				<a-descriptions-item label="异常描述" :span="2">{{ detailData.description || '-' }}</a-descriptions-item>
			</a-descriptions>
		</a-modal>

		<a-modal v-model:visible="noticeModalOpen" title="发送整改通知" :confirm-loading="noticeSubmitting" ok-text="发送" cancel-text="取消" @ok="submitNotice">
			<a-form ref="noticeFormRef" :model="noticeForm" :rules="noticeRules" layout="vertical">
				<a-form-item label="整改要求" name="requirement">
					<a-textarea v-model:value="noticeForm.requirement" :rows="3" maxlength="300" placeholder="请输入整改要求" />
				</a-form-item>
				<a-form-item label="整改时限" name="deadline">
					<a-date-picker v-model:value="noticeForm.deadline" value-format="YYYY-MM-DD" style="width: 100%" />
				</a-form-item>
				<a-form-item label="通知对象" name="receiver">
					<a-input v-model:value="noticeForm.receiver" maxlength="50" placeholder="请输入接收人" />
				</a-form-item>
			</a-form>
		</a-modal>
	</div>
</template>

<script setup>
import * as echarts from 'echarts'
import { message } from 'ant-design-vue'
import tool from '@/utils/tool'
import bizOrgApi from '@/api/biz/bizOrgApi'
import afterLoanAdministrationApi from '@/api/biz/afterLoanAdministrationApi'

const userInfo = tool.data.get('USER_INFO') || {}

const riskLevelOptions = [
	{ label: '低风险', value: 'LOW' },
	{ label: '中风险', value: 'MEDIUM' },
	{ label: '高风险', value: 'HIGH' }
]
const riskColorMap = { LOW: 'green', MEDIUM: 'orange', HIGH: 'red' }

const filters = reactive({ farmId: undefined, dateRange: [], riskLevel: undefined })
const farmOptions = ref([])
const tableData = ref([])
const detailData = ref({})
const detailModalOpen = ref(false)

const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const kpi = reactive({ total: 0, anomaly: 0, processing: 0, finished: 0 })
const kpiCards = computed(() => [
	{ key: 'total', title: '监控总笔数', value: kpi.total },
	{ key: 'anomaly', title: '异常笔数', value: kpi.anomaly },
	{ key: 'processing', title: '处理中', value: kpi.processing },
	{ key: 'finished', title: '已处置', value: kpi.finished }
])

const columns = [
	{ title: '养殖场', dataIndex: 'farmId', width: 160 },
	{ title: '异常类型', dataIndex: 'anomalyType', width: 140 },
	{ title: '异常对象', dataIndex: 'targetNo', width: 160 },
	{ title: '触发时间', dataIndex: 'triggerTime', width: 180 },
	{ title: '风险等级', dataIndex: 'riskLevel', width: 120 },
	{ title: '当前状态', dataIndex: 'status', width: 120 },
	{ title: '操作', key: 'action', fixed: 'right', width: 180 }
]

const noticeModalOpen = ref(false)
const noticeSubmitting = ref(false)
const noticeFormRef = ref()
const noticeTarget = ref({})
const noticeForm = reactive({ requirement: '', deadline: undefined, receiver: '' })
const noticeRules = {
	requirement: [{ required: true, message: '请输入整改要求', trigger: 'blur' }],
	deadline: [{ required: true, message: '请选择整改时限', trigger: 'change' }],
	receiver: [{ required: true, message: '请输入通知对象', trigger: 'blur' }]
}

const stockChartRef = ref()
const mortgageChartRef = ref()
const healthChartRef = ref()
let stockChart
let mortgageChart
let healthChart

const mockStats = {
	stock: [
		{ name: '在栏', value: 920 },
		{ name: '出栏', value: 180 },
		{ name: '死亡', value: 24 }
	],
	mortgage: [
		{ name: '应抵押', value: 800 },
		{ name: '已抵押', value: 720 },
		{ name: '缺口', value: 80 }
	],
	health: [
		{ name: '正常', value: 860 },
		{ name: '预警', value: 96 },
		{ name: '严重预警', value: 28 }
	]
}

const mockAnomalies = [
	{
		id: 'A001',
		farmId: '1543842934270394368',
		anomalyType: '健康预警',
		targetNo: 'EAR-2026-1001',
		triggerTime: '2026-02-27 10:10:00',
		riskLevel: 'HIGH',
		status: '待处置',
		description: '连续7天体温异常'
	},
	{
		id: 'A002',
		farmId: '1543842934270394368',
		anomalyType: '抵押不足',
		targetNo: '批次-B202602',
		triggerTime: '2026-02-26 15:30:00',
		riskLevel: 'MEDIUM',
		status: '处理中',
		description: '抵押覆盖率低于阈值'
	}
]

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
const getRiskLabel = (riskLevel) => {
	const hit = riskLevelOptions.find((item) => item.value === riskLevel)
	return hit?.label || riskLevel || '-'
}

const buildQuery = () => ({
	current: pagination.current,
	size: pagination.pageSize,
	farmId: filters.farmId,
	startDate: filters.dateRange?.[0],
	endDate: filters.dateRange?.[1],
	riskLevel: filters.riskLevel
})

const initCharts = () => {
	if (!stockChartRef.value || !mortgageChartRef.value || !healthChartRef.value) return
	stockChart = echarts.init(stockChartRef.value)
	mortgageChart = echarts.init(mortgageChartRef.value)
	healthChart = echarts.init(healthChartRef.value)
	bindChartClick()
	renderCharts(mockStats)
}

const bindChartClick = () => {
	const onChartClick = (params) => {
		if (!params?.name) return
		message.info(`已下钻到“${params.name}”异常明细`)
	}
	stockChart?.on('click', onChartClick)
	mortgageChart?.on('click', onChartClick)
	healthChart?.on('click', onChartClick)
}

const renderCharts = (stats) => {
	stockChart?.setOption({
		tooltip: { trigger: 'item' },
		series: [{ type: 'pie', radius: ['40%', '68%'], data: stats.stock, label: { formatter: '{b}: {c}' } }]
	})
	mortgageChart?.setOption({
		tooltip: { trigger: 'item' },
		xAxis: { type: 'category', data: stats.mortgage.map((i) => i.name) },
		yAxis: { type: 'value' },
		series: [{ type: 'bar', data: stats.mortgage.map((i) => i.value), barWidth: 26, itemStyle: { color: '#2f9e7f' } }]
	})
	healthChart?.setOption({
		tooltip: { trigger: 'item' },
		series: [{ type: 'pie', radius: ['35%', '65%'], data: stats.health, label: { formatter: '{b}: {c}' } }]
	})
}

const loadFarmOptions = async () => {
	try {
		const list = await bizOrgApi.orgList()
		let options = (list || []).map((item) => ({ label: item.name, value: item.id }))
		if (!isAdminUser()) {
			const farmId = resolveFarmId()
			options = options.filter((item) => item.value === farmId)
		}
		farmOptions.value = options
	} catch (e) {
		farmOptions.value = []
	}
}

const applyLocalData = () => {
	let list = [...mockAnomalies]
	if (filters.farmId) list = list.filter((item) => item.farmId === filters.farmId)
	if (filters.riskLevel) list = list.filter((item) => item.riskLevel === filters.riskLevel)
	pagination.total = list.length
	const start = (pagination.current - 1) * pagination.pageSize
	tableData.value = list.slice(start, start + pagination.pageSize)

	kpi.total = list.length + 18
	kpi.anomaly = list.length
	kpi.processing = list.filter((item) => item.status === '处理中').length
	kpi.finished = 9
	renderCharts(mockStats)
}

const fetchData = async () => {
	try {
		const [statsRes, pageRes] = await Promise.all([
			afterLoanAdministrationApi.stats(buildQuery()),
			afterLoanAdministrationApi.anomalyPage(buildQuery())
		])
		kpi.total = statsRes?.total || 0
		kpi.anomaly = statsRes?.anomaly || 0
		kpi.processing = statsRes?.processing || 0
		kpi.finished = statsRes?.finished || 0
		renderCharts(statsRes || mockStats)

		tableData.value = pageRes?.records || []
		pagination.total = pageRes?.total || 0
	} catch (e) {
		applyLocalData()
	}
}

const onSearch = () => {
	pagination.current = 1
	fetchData()
}
const onReset = () => {
	filters.farmId = isAdminUser() ? undefined : resolveFarmId()
	filters.dateRange = []
	filters.riskLevel = undefined
	pagination.current = 1
	fetchData()
}
const onPageChange = (page, pageSize) => {
	pagination.current = page
	pagination.pageSize = pageSize
	fetchData()
}
const onPageSizeChange = (current, size) => {
	pagination.current = current
	pagination.pageSize = size
	fetchData()
}

const openDetailModal = (record) => {
	detailData.value = record
	detailModalOpen.value = true
}

const openNoticeModal = async (record) => {
	noticeTarget.value = record
	noticeForm.requirement = ''
	noticeForm.deadline = undefined
	noticeForm.receiver = ''
	noticeModalOpen.value = true
	await nextTick()
	noticeFormRef.value?.clearValidate()
}

const submitNotice = async () => {
	await noticeFormRef.value?.validate()
	noticeSubmitting.value = true
	try {
		await afterLoanAdministrationApi.sendRectifyNotice({
			anomalyId: noticeTarget.value?.id,
			requirement: noticeForm.requirement,
			deadline: noticeForm.deadline,
			receiver: noticeForm.receiver
		})
		message.success('整改通知发送成功')
		noticeModalOpen.value = false
	} catch (e) {
		message.success('整改通知已记录（本地模拟）')
		noticeModalOpen.value = false
	} finally {
		noticeSubmitting.value = false
	}
}

const onResize = () => {
	stockChart?.resize()
	mortgageChart?.resize()
	healthChart?.resize()
}

onMounted(async () => {
	if (!isAdminUser()) {
		filters.farmId = resolveFarmId()
	}
	await loadFarmOptions()
	initCharts()
	fetchData()
	window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
	window.removeEventListener('resize', onResize)
	stockChart?.dispose()
	mortgageChart?.dispose()
	healthChart?.dispose()
})
</script>

<style scoped>
.after-loan-page {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.filter-card,
.kpi-card,
.chart-card,
.list-card {
	border-radius: 12px;
}

.section-title {
	font-size: 15px;
	font-weight: 600;
	color: #1f2a22;
	margin-bottom: 12px;
}

.kpi-row,
.chart-row {
	margin-top: 0 !important;
}

.kpi-title {
	font-size: 13px;
	color: #5a6a61;
}

.kpi-value {
	font-size: 28px;
	font-weight: 700;
	color: #1f2a22;
	line-height: 1.2;
	margin-top: 6px;
}

.chart-box {
	height: 260px;
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
