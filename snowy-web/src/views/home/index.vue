<template>
	<div class="regulation-board-page">
		<div class="board-header">
			<div v-if="visibleQuickModuleGroups.length" class="quick-entry" :class="{ open: quickMenuOpen }" ref="quickEntryRef">
				<div class="quick-icon" @click.stop="toggleQuickMenu">☰</div>
				<div class="quick-menu">
					<div class="quick-menu-title">模块导航</div>
					<div class="quick-group" v-for="group in visibleQuickModuleGroups" :key="group.title">
						<div class="quick-group-title">{{ group.title }}</div>
						<div class="quick-menu-item" v-for="item in group.items" :key="item.path" @click="navigateTo(item.path)">
							{{ item.title }}
						</div>
					</div>
				</div>
			</div>
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
					<a-button block type="primary" @click="openRiskThresholdModal">设置风险阈值</a-button>
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

		<a-modal
			v-model:visible="farmStatsModalOpen"
			title="农场经营健康与贷款情况"
			:footer="null"
			width="980px"
			@cancel="onFarmStatsModalClose"
		>
			<a-spin :spinning="farmStatsLoading">
				<div class="farm-stats-header">
					<div class="farm-stats-name">{{ farmStatsData.farmName || '-' }}</div>
					<div class="farm-stats-meta">农场ID：{{ farmStatsData.farmId || '-' }}</div>
				</div>
				<div class="farm-stats-charts">
					<div class="farm-chart-card">
						<div class="farm-chart-title">经营健康数据</div>
						<div ref="healthChartRef" class="farm-chart"></div>
					</div>
					<div class="farm-chart-card">
						<div class="farm-chart-title">贷款情况</div>
						<div ref="loanChartRef" class="farm-chart"></div>
					</div>
				</div>
			</a-spin>
		</a-modal>

		<a-modal
			v-model:visible="riskThresholdModalOpen"
			title="设置风险阈值"
			ok-text="保存"
			cancel-text="取消"
			:confirm-loading="riskThresholdSubmitting"
			@ok="submitRiskThreshold"
		>
			<a-spin :spinning="riskThresholdLoading">
				<a-form ref="riskThresholdFormRef" :model="riskThresholdForm" :rules="riskThresholdRules" layout="vertical">
					<a-form-item label="养殖场" name="farmId">
						<a-select
							v-model:value="riskThresholdForm.farmId"
							:options="farmSelectOptions"
							placeholder="请选择养殖场"
							show-search
							:filter-option="(input, option) => (option?.label || '').includes(input)"
							@change="loadRiskThresholdDetail"
						/>
					</a-form-item>
					<a-form-item label="即将过期预警天数" name="expiringDays">
						<a-input-number v-model:value="riskThresholdForm.expiringDays" :min="1" :max="60" style="width: 100%" />
					</a-form-item>
					<a-form-item label="7天死亡预警阈值（头）" name="deathCountThreshold">
						<a-input-number
							v-model:value="riskThresholdForm.deathCountThreshold"
							:min="1"
							:max="9999"
							style="width: 100%"
						/>
					</a-form-item>
					<a-form-item label="中风险阈值（分）" name="mediumRiskScore">
						<a-input-number v-model:value="riskThresholdForm.mediumRiskScore" :min="1" :max="100" style="width: 100%" />
					</a-form-item>
					<a-form-item label="高风险阈值（分）" name="highRiskScore">
						<a-input-number v-model:value="riskThresholdForm.highRiskScore" :min="1" :max="100" style="width: 100%" />
					</a-form-item>
				</a-form>
			</a-spin>
		</a-modal>
	</div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import supervisionApi from '@/api/biz/supervisionApi'
import sysOrgApi from '@/api/sys/orgApi'
import { toFarmTreeSelectData, findFarmTreeNode } from '@/utils/farmTree'
import tool from '@/utils/tool'

const router = useRouter()
const loading = ref(false)
const mapChartRef = ref()
let mapChart = null
let clockTimer = null
const quickMenuOpen = ref(false)
const quickEntryRef = ref(null)
const farmStatsModalOpen = ref(false)
const farmStatsLoading = ref(false)
const healthChartRef = ref()
const loanChartRef = ref()
let healthChart = null
let loanChart = null
const riskThresholdModalOpen = ref(false)
const riskThresholdLoading = ref(false)
const riskThresholdSubmitting = ref(false)
const riskThresholdFormRef = ref()

const quickModuleGroups = [
	{
		title: '监管总览',
		items: [{ title: '综合监管视图', path: '/comprehensiveSupervision' }]
	},
	{
		title: '畜牧管理',
		items: [
			{ title: '畜牧登记列表', path: '/livestockList' },
			{ title: '畜牧统计看板', path: '/livestockBoard' }
		]
	},
	{
		title: '银行金融管理',
		items: [
			{ title: '金融产品管理', path: '/productManage' },
			{ title: '贷款管理', path: '/loanAdministration' },
			{ title: '贷后管理', path: '/afterLoanAdministration' }
		]
	},
	{
		title: '保险管理',
		items: [
			{ title: '保险产品管理', path: '/bxProductManage' },
			{ title: '投保管理', path: '/tbManage' },
			{ title: '保后管理', path: '/bhManage' },
			{ title: '理赔管理', path: '/lpManage' }
		]
	}
]

const normalizePath = (path) => {
	if (!path) return ''
	return path.startsWith('/') ? path : `/${path}`
}

const collectMenuPaths = (menus = [], collector = new Set()) => {
	;(menus || []).forEach((item) => {
		const path = normalizePath(item?.path || item?.url || item?.route)
		if (path) {
			collector.add(path)
		}
		if (item?.children?.length) {
			collectMenuPaths(item.children, collector)
		}
	})
	return collector
}

const collectMenuTitles = (menus = [], collector = new Set()) => {
	;(menus || []).forEach((item) => {
		const rawTitle = item?.meta?.title || item?.title || item?.name
		const title = typeof rawTitle === 'string' ? rawTitle.trim() : ''
		if (title) {
			collector.add(title)
		}
		if (item?.children?.length) {
			collectMenuTitles(item.children, collector)
		}
	})
	return collector
}

const menuSnapshot = computed(() => tool.data.get('MENU') || [])

const allowedMenuPaths = computed(() => {
	return collectMenuPaths(menuSnapshot.value)
})

const allowedMenuTitles = computed(() => {
	const menus = tool.data.get('MENU') || []
	return collectMenuTitles(menus)
})

const visibleQuickModuleGroups = computed(() => {
	const allowed = allowedMenuPaths.value
	const allowedTitles = allowedMenuTitles.value
	return quickModuleGroups
		.map((group) => ({
			...group,
			items: group.items.filter((item) => {
				return allowed.has(normalizePath(item.path)) || allowedTitles.has(item.title)
			})
		}))
		.filter((group) => group.items.length > 0)
})

const navigateTo = (path) => {
	if (!path) return
	quickMenuOpen.value = false
	router.push(path)
}

const toggleQuickMenu = () => {
	quickMenuOpen.value = !quickMenuOpen.value
}

const handleClickOutside = (event) => {
	const root = quickEntryRef.value
	if (!root) return
	if (!root.contains(event.target)) {
		quickMenuOpen.value = false
	}
}

const selectedFarmId = ref(undefined)
const farmTree = ref([])
const homeData = reactive({
	refreshTime: '',
	overview: {},
	mapPoints: [],
	alerts: [],
	anomalies: []
})

const farmStatsData = reactive({
	farmId: '',
	farmName: '',
	inStockCount: 0,
	outStockCount: 0,
	deadCount: 0,
	immunizedCount: 0,
	notImmunizedCount: 0,
	expiredCount: 0,
	loanApplyCount: 0,
	loanApprovedCount: 0,
	loanProcessingCount: 0,
	loanPendingCount: 0,
	loanAmountTotal: 0,
	loanAmountApproved: 0
})

const localNow = ref('')
const riskThresholdForm = reactive({
	farmId: undefined,
	expiringDays: 7,
	deathCountThreshold: 10,
	mediumRiskScore: 60,
	highRiskScore: 80
})

const riskThresholdRules = {
	farmId: [{ required: true, message: '请选择养殖场', trigger: 'change' }],
	expiringDays: [{ required: true, type: 'number', message: '请输入即将过期预警天数', trigger: 'change' }],
	deathCountThreshold: [{ required: true, type: 'number', message: '请输入7天死亡预警阈值', trigger: 'change' }],
	mediumRiskScore: [{ required: true, type: 'number', message: '请输入中风险阈值', trigger: 'change' }],
	highRiskScore: [{ required: true, type: 'number', message: '请输入高风险阈值', trigger: 'change' }]
}

const formatNow = () => {
	const d = new Date()
	const pad = (n) => String(n).padStart(2, '0')
	return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(
		d.getSeconds()
	)}`
}

const boardTimeText = computed(() => `${homeData.refreshTime || localNow.value || '-'}`)

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

const flattenFarmOptions = (tree = [], collector = []) => {
	;(tree || []).forEach((node) => {
		if (node?.value) {
			collector.push({ label: node.title, value: node.value })
		}
		if (node?.children?.length) {
			flattenFarmOptions(node.children, collector)
		}
	})
	return collector
}

const farmSelectOptions = computed(() => flattenFarmOptions(farmTree.value, []))

const collectLeafFarmIds = (nodes = [], collector = new Set()) => {
	nodes.forEach((node) => {
		if (!node) return
		if (node?.children?.length) {
			collectLeafFarmIds(node.children, collector)
			return
		}
		const id = node?.value || node?.key
		if (id !== undefined && id !== null && id !== '') {
			collector.add(String(id))
		}
	})
	return collector
}

const leafFarmIdSet = computed(() => collectLeafFarmIds(farmTree.value, new Set()))

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

	const currentLeafFarmIds = leafFarmIdSet.value
	const scatterData = (homeData.mapPoints || [])
		.filter((item) => {
			const farmId = item?.farmId
			if (!farmId) return false
			if (!currentLeafFarmIds.size) return true
			return currentLeafFarmIds.has(String(farmId))
		})
		.map((item) => ({
			farmId: item.farmId,
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
				silent: true,
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

	mapChart.off('click')
	mapChart.on('click', (params) => {
		if (params?.seriesName !== '养殖场分布') return
		const farmId = params?.data?.farmId
		if (!farmId) return
		openFarmStatsModal(farmId)
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
		if (!selectedFarmId.value && farmSelectOptions.value.length) {
			selectedFarmId.value = farmSelectOptions.value[0].value
		}
	} catch (e) {
		farmTree.value = []
	}
}

const loadRiskThresholdDetail = async () => {
	if (!riskThresholdForm.farmId) return
	riskThresholdLoading.value = true
	try {
		const data = await supervisionApi.riskThresholdDetail({ farmId: riskThresholdForm.farmId })
		riskThresholdForm.expiringDays = Number(data?.expiringDays || 7)
		riskThresholdForm.deathCountThreshold = Number(data?.deathCountThreshold || 10)
		riskThresholdForm.mediumRiskScore = Number(data?.mediumRiskScore || 60)
		riskThresholdForm.highRiskScore = Number(data?.highRiskScore || 80)
	} finally {
		riskThresholdLoading.value = false
	}
}

const openRiskThresholdModal = async () => {
	riskThresholdModalOpen.value = true
	riskThresholdForm.farmId = selectedFarmId.value || farmSelectOptions.value?.[0]?.value
	await loadRiskThresholdDetail()
}

const submitRiskThreshold = async () => {
	try {
		await riskThresholdFormRef.value?.validate()
		if (riskThresholdForm.highRiskScore <= riskThresholdForm.mediumRiskScore) {
			message.warning('高风险阈值必须大于中风险阈值')
			return
		}
		riskThresholdSubmitting.value = true
		await supervisionApi.saveRiskThreshold({
			farmId: riskThresholdForm.farmId,
			expiringDays: riskThresholdForm.expiringDays,
			deathCountThreshold: riskThresholdForm.deathCountThreshold,
			mediumRiskScore: riskThresholdForm.mediumRiskScore,
			highRiskScore: riskThresholdForm.highRiskScore
		})
		message.success('风险阈值保存成功')
		riskThresholdModalOpen.value = false
	} finally {
		riskThresholdSubmitting.value = false
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

const renderFarmStatsCharts = async () => {
	await nextTick()
	if (healthChartRef.value) {
		if (!healthChart) healthChart = echarts.init(healthChartRef.value)
		healthChart.setOption({
			tooltip: { trigger: 'item' },
			legend: { bottom: 0, textStyle: { color: '#dffcf3' } },
			series: [
				{
					name: '经营健康',
					type: 'pie',
					radius: ['35%', '62%'],
					center: ['50%', '44%'],
					label: { color: '#dffcf3' },
					data: [
						{ name: '存栏', value: Number(farmStatsData.inStockCount || 0) },
						{ name: '出栏', value: Number(farmStatsData.outStockCount || 0) },
						{ name: '死亡', value: Number(farmStatsData.deadCount || 0) },
						{ name: '已接种', value: Number(farmStatsData.immunizedCount || 0) },
						{ name: '未接种', value: Number(farmStatsData.notImmunizedCount || 0) },
						{ name: '已过期', value: Number(farmStatsData.expiredCount || 0) }
					]
				}
			]
		})
	}
	if (loanChartRef.value) {
		if (!loanChart) loanChart = echarts.init(loanChartRef.value)
		loanChart.setOption({
			tooltip: { trigger: 'axis' },
			xAxis: {
				type: 'category',
				data: ['申请总数', '审批通过', '处理中', '待提交'],
				axisLabel: { color: '#dffcf3' }
			},
			yAxis: {
				type: 'value',
				axisLabel: { color: '#dffcf3' },
				splitLine: { lineStyle: { color: 'rgba(200,255,236,0.15)' } }
			},
			series: [
				{
					type: 'bar',
					data: [
						Number(farmStatsData.loanApplyCount || 0),
						Number(farmStatsData.loanApprovedCount || 0),
						Number(farmStatsData.loanProcessingCount || 0),
						Number(farmStatsData.loanPendingCount || 0)
					],
					itemStyle: {
						color: '#4fd2a8'
					},
					label: { show: true, position: 'top', color: '#dffcf3' }
				}
			],
			grid: { left: 40, right: 18, top: 18, bottom: 40 }
		})
	}
}

const openFarmStatsModal = async (farmId) => {
	farmStatsModalOpen.value = true
	farmStatsLoading.value = true
	try {
		const data = await supervisionApi.farmStats({ farmId })
		Object.assign(farmStatsData, {
			farmId: data?.farmId || farmId,
			farmName: data?.farmName || farmLabel(farmId),
			inStockCount: Number(data?.inStockCount || 0),
			outStockCount: Number(data?.outStockCount || 0),
			deadCount: Number(data?.deadCount || 0),
			immunizedCount: Number(data?.immunizedCount || 0),
			notImmunizedCount: Number(data?.notImmunizedCount || 0),
			expiredCount: Number(data?.expiredCount || 0),
			loanApplyCount: Number(data?.loanApplyCount || 0),
			loanApprovedCount: Number(data?.loanApprovedCount || 0),
			loanProcessingCount: Number(data?.loanProcessingCount || 0),
			loanPendingCount: Number(data?.loanPendingCount || 0),
			loanAmountTotal: Number(data?.loanAmountTotal || 0),
			loanAmountApproved: Number(data?.loanAmountApproved || 0)
		})
		await renderFarmStatsCharts()
	} catch (e) {
		message.error('加载农场经营健康与贷款数据失败')
	} finally {
		farmStatsLoading.value = false
	}
}

const onFarmStatsModalClose = () => {
	healthChart?.resize()
	loanChart?.resize()
}

const onResize = () => mapChart?.resize()
const onAllChartResize = () => {
	onResize()
	healthChart?.resize()
	loanChart?.resize()
}

watch(
	() => homeData.mapPoints,
	() => {
		renderChinaMap()
	},
	{ deep: true }
)

onMounted(async () => {
	localNow.value = formatNow()
	clockTimer = window.setInterval(() => {
		localNow.value = formatNow()
	}, 1000)
	document.addEventListener('mousedown', handleClickOutside)
	await Promise.all([loadFarmTree(), loadHomeData()])
	await initChinaMap()
	window.addEventListener('resize', onAllChartResize)
})

onUnmounted(() => {
	if (clockTimer) {
		window.clearInterval(clockTimer)
		clockTimer = null
	}
	document.removeEventListener('mousedown', handleClickOutside)
	window.removeEventListener('resize', onAllChartResize)
	if (mapChart) {
		mapChart.dispose()
		mapChart = null
	}
	if (healthChart) {
		healthChart.dispose()
		healthChart = null
	}
	if (loanChart) {
		loanChart.dispose()
		loanChart = null
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

.quick-entry {
	position: absolute;
	left: 0;
	top: 4px;
	z-index: 30;
	padding-bottom: 6px;
}

.quick-icon {
	width: 36px;
	height: 36px;
	border-radius: 8px;
	border: 1px solid rgba(126, 212, 186, 0.45);
	background: rgba(12, 45, 37, 0.88);
	color: #d9fff4;
	font-size: 20px;
	line-height: 34px;
	text-align: center;
	cursor: pointer;
	user-select: none;
}

.quick-menu {
	position: absolute;
	left: 0;
	top: 36px;
	width: 230px;
	padding: 8px;
	border-radius: 10px;
	border: 1px solid rgba(126, 212, 186, 0.32);
	background: rgba(8, 30, 25, 0.96);
	box-shadow: 0 10px 24px rgba(0, 0, 0, 0.28);
	opacity: 0;
	transform: translateY(8px);
	pointer-events: none;
	transition: all 0.2s ease;
}

.quick-entry.open .quick-menu {
	opacity: 1;
	transform: translateY(0);
	pointer-events: auto;
}

.quick-menu-title {
	font-size: 12px;
	color: #9fd9c7;
	padding: 4px 8px 8px;
}

.quick-group + .quick-group {
	margin-top: 6px;
	padding-top: 6px;
	border-top: 1px dashed rgba(126, 212, 186, 0.2);
}

.quick-group-title {
	font-size: 12px;
	color: #8ecbb8;
	padding: 2px 8px 4px;
}

.quick-menu-item {
	padding: 8px 10px;
	color: #dcfff3;
	border-radius: 6px;
	cursor: pointer;
	font-size: 13px;
}

.quick-menu-item:hover {
	background: rgba(62, 161, 131, 0.24);
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

.farm-stats-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12px;
}

.farm-stats-name {
	font-size: 16px;
	font-weight: 700;
	color: #dffcf3;
}

.farm-stats-meta {
	font-size: 12px;
	color: #a7d9c9;
}

.farm-stats-charts {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 12px;
}

.farm-chart-card {
	background: rgba(8, 28, 24, 0.8);
	border: 1px solid rgba(124, 201, 176, 0.2);
	border-radius: 10px;
	padding: 10px;
}

.farm-chart-title {
	font-size: 14px;
	font-weight: 600;
	color: #dffcf3;
	margin-bottom: 6px;
}

.farm-chart {
	height: 300px;
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
