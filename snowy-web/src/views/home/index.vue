<template>
	<div class="regulation-board-page">
		<div class="board-header">
			<div class="title-neon-line"></div>
			<div class="board-title-wrap">
				<div class="board-title">综合监管视图</div>
				<div class="board-subtitle">养殖主体全景、风险预警、金融动态一屏联动</div>
			</div>
			<div class="board-time">{{ boardTimeText }}</div>
		</div>
		<div class="board-main" v-if="!loading">
			<div class="board-side side-left">
				<a-card :bordered="false" class="panel-card side-block side-top">
					<div class="panel-title">养殖主体全景</div>
					<div class="metric-item" v-for="metric in panoramaMetrics" :key="metric.label">
						<div class="metric-text">
							<span>{{ metric.label }}</span>
							<span>{{ metric.value }}</span>
						</div>
						<a-progress :percent="metric.percent" :show-info="false" :stroke-color="metric.color" />
					</div>
				</a-card>
				<a-card :bordered="false" class="panel-card side-block side-middle">
					<div class="panel-title">健康防疫</div>
					<div class="mini-chart" v-for="item in immunityItems" :key="item.title">
						<div class="mini-title">{{ item.title }}</div>
						<div class="mini-value">{{ item.value }}</div>
						<div class="mini-desc">{{ item.desc }}</div>
					</div>
				</a-card>
				<a-card :bordered="false" class="panel-card side-block side-bottom">
					<div class="panel-title">金融动态监管</div>
					<div class="mini-chart" v-for="item in financeItems" :key="item.title">
						<div class="mini-title">{{ item.title }}</div>
						<div class="mini-value">{{ item.value }}</div>
						<div class="mini-desc">{{ item.desc }}</div>
					</div>
				</a-card>
			</div>

			<div class="board-center">
				<a-card :bordered="false" class="panel-card map-panel">
					<div class="map-head">
						<div class="panel-title">GIS地图主画布</div>
						<div class="map-head-tools">
							<a-tree-select
								v-model:value="selectedFarmId"
								style="width: 240px"
								:tree-data="farmTree"
								placeholder="请选择养殖场"
								tree-default-expand-all
								show-search
								tree-node-filter-prop="title"
								allow-clear
								@change="loadHomeData"
							/>
						</div>
					</div>
					<div class="center-stats">
						<div class="stat-chip" v-for="item in centerStats" :key="item.label">
							<div class="chip-label">{{ item.label }}</div>
							<div class="chip-value">{{ item.value }}</div>
						</div>
					</div>
					<div class="map-placeholder">
						<div ref="mapChartRef" class="china-map-chart"></div>
						<div class="map-hint">点击农场点位可钻取到存栏、贷款、投保、理赔详情</div>
					</div>
				</a-card>
			</div>

			<div class="board-side side-right">
				<a-card :bordered="false" class="panel-card side-block side-top">
					<div class="panel-title">风险预警管理</div>
					<div class="alert-list">
						<div class="alert-item" v-for="alert in alertList" :key="alert.id">
							<div class="alert-top">
								<a-tag :color="riskTagColor(alert.riskLevel)">{{ riskLabel(alert.riskLevel) }}</a-tag>
								<span>{{ alert.triggerTime || '-' }}</span>
							</div>
							<div class="alert-content">{{ alert.content }}</div>
						</div>
					</div>
					<a-button block type="primary">设置风险阈值</a-button>
				</a-card>
				<a-card :bordered="false" class="panel-card side-block side-middle">
					<div class="panel-title">整改处理进度</div>
					<div class="metric-item" v-for="item in rectifyItems" :key="item.label">
						<div class="metric-text">
							<span>{{ item.label }}</span>
							<span>{{ item.value }}</span>
						</div>
						<a-progress :percent="item.percent" :show-info="false" :stroke-color="item.color" />
					</div>
				</a-card>
				<a-card :bordered="false" class="panel-card side-block side-bottom">
					<div class="panel-title">异常列表</div>
					<a-table
						class="anomaly-table"
						:columns="anomalyColumns"
						:data-source="anomalyRows"
						:pagination="false"
						row-key="id"
						size="small"
					/>
				</a-card>
			</div>
		</div>
		<a-spin v-else class="board-spin" />
	</div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'
import supervisionApi from '@/api/biz/supervisionApi'
import sysOrgApi from '@/api/sys/orgApi'
import { toFarmTreeSelectData, findFarmTreeNode } from '@/utils/farmTree'

const loading = ref(false)
const mapChartRef = ref()
let mapChart = null

const selectedFarmId = ref(undefined)
const farmTree = ref([])
const homeData = reactive({
	refreshTime: '',
	overview: {},
	mapPoints: [],
	alerts: [],
	anomalies: []
})

const boardTimeText = computed(() => `${homeData.refreshTime || '-'} | 实时刷新`)

const overview = computed(() => homeData.overview || {})

const percent = (value, total) => {
	if (!total || total <= 0) return 0
	return Math.max(0, Math.min(100, Math.round((value / total) * 100)))
}

const formatWan = (num) => {
	const n = Number(num || 0)
	if (!n) return '0'
	return `${(n / 10000).toFixed(2)}万`
}

const panoramaMetrics = computed(() => {
	const all = Number(overview.value.inStockCount || 0) + Number(overview.value.outStockCount || 0) + Number(overview.value.deadCount || 0)
	const immunityAll = Number(overview.value.immunizedCount || 0) + Number(overview.value.notImmunizedCount || 0) + Number(overview.value.expiredCount || 0)
	const claimTotal = Number(overview.value.claimTotal || 0)
	const claimClosed = Number(overview.value.claimClosed || 0)
	const loanBase = Number(overview.value.loanAmount || 0) + Number(overview.value.insuredAmount || 0)
	return [
		{
			label: '存栏健康率',
			value: `${percent(overview.value.inStockCount, all)}%`,
			percent: percent(overview.value.inStockCount, all),
			color: '#34d399'
		},
		{
			label: '防疫完成率',
			value: `${percent(overview.value.immunizedCount, immunityAll)}%`,
			percent: percent(overview.value.immunizedCount, immunityAll),
			color: '#60a5fa'
		},
		{
			label: '贷款履约率',
			value: `${percent(overview.value.insuredAmount, loanBase)}%`,
			percent: percent(overview.value.insuredAmount, loanBase),
			color: '#fbbf24'
		},
		{
			label: '理赔结案率',
			value: `${percent(claimClosed, claimTotal)}%`,
			percent: percent(claimClosed, claimTotal),
			color: '#a78bfa'
		}
	]
})

const immunityItems = computed(() => {
	const all = Number(overview.value.immunizedCount || 0) + Number(overview.value.notImmunizedCount || 0) + Number(overview.value.expiredCount || 0)
	return [
		{ title: '已接种', value: overview.value.immunizedCount || 0, desc: `覆盖率 ${percent(overview.value.immunizedCount, all)}%` },
		{ title: '未接种', value: overview.value.notImmunizedCount || 0, desc: '需补种请及时处理' },
		{ title: '已过期', value: overview.value.expiredCount || 0, desc: '优先处理高风险农场' }
	]
})

const financeItems = computed(() => {
	const claimTotal = Number(overview.value.claimTotal || 0)
	const claimClosed = Number(overview.value.claimClosed || 0)
	return [
		{ title: '贷款管理', value: `在贷余额 ${formatWan(overview.value.loanAmount)}`, desc: `异常 ${overview.value.anomalyTotal || 0} 笔` },
		{ title: '投保管理', value: `在保金额 ${formatWan(overview.value.insuredAmount)}`, desc: `覆盖农场 ${overview.value.farmCount || 0} 个` },
		{ title: '理赔管理', value: `结案率 ${percent(claimClosed, claimTotal)}%`, desc: `总理赔 ${claimTotal} 笔` }
	]
})

const centerStats = computed(() => [
	{ label: '当前筛选农场', value: selectedFarmId.value ? 1 : overview.value.farmCount || 0 },
	{ label: '存栏总量', value: overview.value.inStockCount || 0 },
	{ label: '在贷余额', value: formatWan(overview.value.loanAmount) },
	{ label: '在保金额', value: formatWan(overview.value.insuredAmount) }
])

const alertList = computed(() => {
	return (homeData.alerts || []).map((item) => ({
		...item,
		content: `${farmLabel(item.farmId)} ${item.content || ''}`.trim()
	}))
})

const rectifyItems = computed(() => {
	const total = Number(overview.value.anomalyTotal || 0)
	return [
		{
			label: '整改通知发送率',
			value: `${percent(overview.value.sentNoticeCount, total)}%`,
			percent: percent(overview.value.sentNoticeCount, total),
			color: '#60a5fa'
		},
		{
			label: '3日内处置完成率',
			value: `${percent(overview.value.resolvedInThreeDaysCount, total)}%`,
			percent: percent(overview.value.resolvedInThreeDaysCount, total),
			color: '#34d399'
		},
		{
			label: '超期未处理占比',
			value: `${percent(overview.value.overdueAnomalyCount, total)}%`,
			percent: percent(overview.value.overdueAnomalyCount, total),
			color: '#f87171'
		}
	]
})

const anomalyColumns = [
	{ title: '养殖场', dataIndex: 'farmName' },
	{ title: '异常类型', dataIndex: 'anomalyType' },
	{ title: '风险等级', dataIndex: 'riskLabel' },
	{ title: '触发时间', dataIndex: 'triggerTime' },
	{ title: '处置状态', dataIndex: 'status' },
	{ title: '整改通知', dataIndex: 'noticeStatus' }
]

const anomalyRows = computed(() => {
	return (homeData.anomalies || []).map((item) => ({
		...item,
		farmName: farmLabel(item.farmId),
		riskLabel: riskLabel(item.riskLevel)
	}))
})

const riskLabel = (riskLevel) => {
	if (riskLevel === 'HIGH') return '高风险'
	if (riskLevel === 'MEDIUM') return '中风险'
	if (riskLevel === 'LOW') return '低风险'
	return '一般'
}

const riskTagColor = (riskLevel) => {
	if (riskLevel === 'HIGH') return 'red'
	if (riskLevel === 'MEDIUM') return 'orange'
	if (riskLevel === 'LOW') return 'blue'
	return 'default'
}

const farmLabel = (farmId) => {
	if (!farmId) return '-'
	const node = findFarmTreeNode(farmTree.value, farmId)
	return node?.title || farmId
}

const chinaMapLoaded = ref(false)
const chinaMapSources = ['/maps/china.json', 'https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json']

const renderChinaMap = () => {
	if (!mapChart) return
	if (!chinaMapLoaded.value) {
		mapChart.setOption({
			graphic: [
				{
					type: 'text',
					left: 'center',
					top: 'middle',
					style: {
						text: '中国地图数据加载失败',
						fill: '#d9fff4',
						fontSize: 16
					}
				}
			]
		})
		return
	}

	const scatterData = (homeData.mapPoints || []).map((item) => ({
		name: item.farmName || farmLabel(item.farmId),
		value: [item.longitude, item.latitude, item.healthScore || 0],
		level: item.riskLevel,
		stock: item.inStockCount || 0
	}))

	mapChart.setOption({
		tooltip: {
			trigger: 'item',
			backgroundColor: 'rgba(9, 32, 27, 0.95)',
			borderColor: 'rgba(108, 189, 164, 0.42)',
			textStyle: { color: '#e7fff6' },
			formatter: (params) => {
				if (params.seriesName === '养殖场分布') {
					return `${params.name}<br/>健康指数：${params.value?.[2] ?? '-'}<br/>存栏：${params.data?.stock ?? '-'}`
				}
				return params.name
			}
		},
		geo: {
			map: 'china',
			roam: true,
			zoom: 1.1,
			label: { show: false },
			itemStyle: {
				areaColor: 'transparent',
				borderColor: 'transparent'
			}
		},
		series: [
			{
				name: '中国底图',
				type: 'map',
				map: 'china',
				roam: true,
				zoom: 1.1,
				itemStyle: {
					areaColor: '#143a31',
					borderColor: '#6ec8ab',
					borderWidth: 0.8
				},
				emphasis: {
					itemStyle: {
						areaColor: '#1f5a4c'
					}
				},
				label: { show: false },
				data: []
			},
			{
				name: '养殖场分布',
				type: 'effectScatter',
				coordinateSystem: 'geo',
				data: scatterData,
				symbolSize: (val) => Math.max(8, Math.min(16, (val?.[2] || 0) / 6)),
				rippleEffect: { scale: 3, brushType: 'stroke' },
				itemStyle: {
					color: (params) => {
						const level = params.data?.level
						if (level === 'danger') return '#f87171'
						if (level === 'warning') return '#fbbf24'
						return '#34d399'
					}
				},
				zlevel: 3
			},
			{
				name: '养殖场标签',
				type: 'scatter',
				coordinateSystem: 'geo',
				data: scatterData,
				symbolSize: 1,
				label: {
					show: true,
					formatter: '{b}',
					position: 'right',
					color: '#d9fff4',
					fontSize: 12
				},
				itemStyle: { color: 'transparent' },
				zlevel: 4
			}
		]
	})
}

const ensureChinaMap = async () => {
	if (echarts.getMap('china')) return true
	for (const source of chinaMapSources) {
		try {
			const resp = await fetch(source)
			if (!resp.ok) continue
			const geoJson = await resp.json()
			echarts.registerMap('china', geoJson)
			return true
		} catch (e) {
			// ignore
		}
	}
	return false
}

const initChinaMap = async () => {
	await nextTick()
	if (!mapChartRef.value) return
	mapChart = echarts.init(mapChartRef.value)
	chinaMapLoaded.value = await ensureChinaMap()
	renderChinaMap()
}

const loadFarmTree = async () => {
	try {
		const tree = await sysOrgApi.orgTree()
		farmTree.value = toFarmTreeSelectData(tree || [])
	} catch (e) {
		farmTree.value = []
	}
}

const loadHomeData = async () => {
	loading.value = true
	try {
		const data = await supervisionApi.home({ farmId: selectedFarmId.value })
		homeData.refreshTime = data?.refreshTime || ''
		homeData.overview = data?.overview || {}
		homeData.mapPoints = data?.mapPoints || []
		homeData.alerts = data?.alerts || []
		homeData.anomalies = data?.anomalies || []
		renderChinaMap()
	} catch (e) {
		message.error('加载综合监管数据失败')
	} finally {
		loading.value = false
	}
}

const onResize = () => mapChart?.resize()

watch(
	() => homeData.mapPoints,
	() => {
		renderChinaMap()
	},
	{ deep: true }
)

onMounted(async () => {
	await Promise.all([loadFarmTree(), loadHomeData()])
	await initChinaMap()
	window.addEventListener('resize', onResize)
})

onUnmounted(() => {
	window.removeEventListener('resize', onResize)
	if (mapChart) {
		mapChart.dispose()
		mapChart = null
	}
})
</script>

<style scoped lang="less">
.regulation-board-page {
	padding: 12px;
	min-height: 100%;
	background:
		radial-gradient(circle at 20% 12%, rgba(38, 160, 125, 0.12), rgba(6, 25, 21, 0.98) 46%),
		linear-gradient(180deg, #08261f 0%, #051a15 100%);
	color: #e7fff6;
}

.board-header {
	height: 72px;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	margin-bottom: 14px;
	overflow: visible;
}

.board-title-wrap {
	text-align: center;
	position: relative;
	z-index: 3;
	padding: 0 28px;
	background: transparent;
}

.board-title {
	font-size: 46px;
	font-weight: 700;
	line-height: 1;
	letter-spacing: 5px;
	text-shadow: 0 0 8px rgba(111, 255, 203, 0.3);
}

.board-subtitle {
	margin-top: 6px;
	font-size: 14px;
	color: rgba(214, 255, 239, 0.85);
}

.board-time {
	position: absolute;
	right: 0;
	top: 2px;
	font-size: 20px;
	color: rgba(214, 255, 239, 0.88);
	z-index: 3;
}

.title-neon-line {
	position: absolute;
	left: 50%;
	width: calc(100% - 32px);
	max-width: 1500px;
	transform: translateX(-50%);
	height: 1px;
	z-index: 2;
	top: 34px;
	background: transparent;
}

.title-neon-line::before,
.title-neon-line::after {
	content: '';
	position: absolute;
	top: 0;
	height: 1px;
	width: calc(50% - 170px);
	background: linear-gradient(
		90deg,
		rgba(226, 255, 246, 0) 0%,
		rgba(226, 255, 246, 0.85) 18%,
		rgba(226, 255, 246, 0.95) 60%,
		rgba(73, 229, 177, 0) 100%
	);
	box-shadow: 0 0 8px rgba(134, 255, 212, 0.36);
}

.title-neon-line::before {
	left: 0;
}

.title-neon-line::after {
	right: 0;
	transform: scaleX(-1);
}

.board-main {
	display: grid;
	grid-template-columns: 400px 1fr 400px;
	gap: 12px;
	height: calc(100vh - 185px);
	min-height: 680px;
}

.board-side {
	display: grid;
	gap: 12px;
}

.side-left,
.side-right {
	grid-template-rows: repeat(3, minmax(0, 1fr));
}

.panel-card {
	height: 100%;
	background: linear-gradient(135deg, rgba(15, 58, 50, 0.9), rgba(12, 34, 30, 0.9));
	border: 1px solid rgba(93, 181, 155, 0.32);
	box-shadow: inset 0 0 28px rgba(62, 150, 126, 0.14);
	border-radius: 12px;
	color: #e7fff6;
}

:deep(.ant-card-body) {
	height: 100%;
	display: flex;
	flex-direction: column;
	padding: 14px;
}

.panel-title {
	font-size: 20px;
	font-weight: 700;
	color: #ddfff1;
	margin-bottom: 12px;
}

.metric-item {
	margin-bottom: 10px;
}

.metric-text {
	display: flex;
	justify-content: space-between;
	font-size: 13px;
	margin-bottom: 6px;
}

.mini-chart {
	flex: 1;
	padding: 10px;
	border: 1px solid rgba(93, 181, 155, 0.3);
	border-radius: 8px;
	margin-bottom: 8px;
	background: rgba(10, 39, 33, 0.55);
}

.mini-title {
	font-size: 13px;
	color: #bfffe7;
}

.mini-value {
	font-size: 28px;
	font-weight: 700;
	margin-top: 4px;
}

.mini-desc {
	margin-top: 2px;
	font-size: 12px;
	color: rgba(200, 255, 234, 0.82);
}

.board-center {
	height: 100%;
}

.map-panel {
	height: 100%;
}

.map-head {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 10px;
}

.center-stats {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 8px;
	margin-bottom: 10px;
}

.stat-chip {
	border: 1px solid rgba(93, 181, 155, 0.35);
	background: rgba(11, 45, 37, 0.65);
	border-radius: 10px;
	padding: 8px 10px;
}

.chip-label {
	font-size: 12px;
	color: #b9ffe5;
}

.chip-value {
	margin-top: 6px;
	font-size: 30px;
	font-weight: 700;
}

.map-placeholder {
	position: relative;
	flex: 1;
	min-height: 420px;
	border: 1px dashed rgba(93, 181, 155, 0.35);
	border-radius: 12px;
	overflow: hidden;
}

.china-map-chart {
	width: 100%;
	height: 100%;
}

.map-hint {
	position: absolute;
	left: 12px;
	bottom: 10px;
	font-size: 12px;
	color: rgba(220, 255, 244, 0.82);
	padding: 4px 10px;
	border: 1px solid rgba(93, 181, 155, 0.4);
	background: rgba(8, 33, 28, 0.66);
	border-radius: 12px;
}

.alert-list {
	flex: 1;
	overflow: auto;
	margin-bottom: 10px;
}

.alert-item {
	padding: 8px;
	margin-bottom: 8px;
	border: 1px solid rgba(93, 181, 155, 0.35);
	border-radius: 8px;
	background: rgba(9, 35, 29, 0.5);
}

.alert-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 5px;
	font-size: 12px;
	color: #cbffe9;
}

.alert-content {
	font-size: 12px;
	line-height: 1.55;
	color: #e7fff6;
}

.anomaly-table {
	flex: 1;
}

:deep(.anomaly-table .ant-table),
:deep(.anomaly-table .ant-table-thead > tr > th),
:deep(.anomaly-table .ant-table-tbody > tr > td) {
	background: transparent;
	color: #e7fff6;
	border-color: rgba(93, 181, 155, 0.2);
}

:deep(.ant-progress-bg) {
	box-shadow: 0 0 6px currentColor;
}

.board-spin {
	width: 100%;
	padding-top: 80px;
}

@media (max-width: 1600px) {
	.board-main {
		grid-template-columns: 360px 1fr 360px;
	}
	.board-title {
		font-size: 40px;
	}
	.board-time {
		font-size: 18px;
	}
}

@media (max-width: 1380px) {
	.board-main {
		grid-template-columns: 1fr;
		height: auto;
		min-height: auto;
	}
	.side-left,
	.side-right {
		grid-template-rows: repeat(3, minmax(220px, auto));
	}
	.map-placeholder {
		min-height: 520px;
	}
}
</style>
