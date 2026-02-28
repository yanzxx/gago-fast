<template>
	<div class="regulation-board-page">
		<div class="board-header">
			<div class="board-title-wrap">
				<div class="board-title">综合监管视图</div>
				<div class="board-subtitle">养殖主体全景、风险预警、金融动态一屏联动</div>
			</div>
			<div class="board-time">2026-02-28 09:30 | 实时刷新</div>
		</div>
		<div class="board-main">
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
						<div class="map-dot normal">正常 27</div>
						<div class="map-dot warning">预警 6</div>
						<div class="map-dot danger">高风险 2</div>
						<div class="map-hint">点击农场点位可钻取到存栏、贷款、投保、理赔详情</div>
						<div class="map-legend">
							<div class="legend-title">图例</div>
							<div class="legend-group" v-if="selectedLayers.includes('FARM_DISTRIBUTION')">
								<div class="legend-sub-title">养殖场分布</div>
								<div class="legend-item"><span class="legend-dot normal"></span><span>正常农场</span></div>
								<div class="legend-item"><span class="legend-dot warning"></span><span>预警农场</span></div>
								<div class="legend-item"><span class="legend-dot danger"></span><span>高风险农场</span></div>
							</div>
							<div class="legend-group" v-if="selectedLayers.includes('HEALTH_HEAT')">
								<div class="legend-sub-title">经营健康热力</div>
								<div class="legend-item"><span class="legend-bar health-low"></span><span>低</span></div>
								<div class="legend-item"><span class="legend-bar health-mid"></span><span>中</span></div>
								<div class="legend-item"><span class="legend-bar health-high"></span><span>高</span></div>
							</div>
							<div class="legend-group" v-if="selectedLayers.includes('LOAN_HEAT')">
								<div class="legend-sub-title">贷款金额热力</div>
								<div class="legend-item"><span class="legend-bar loan-low"></span><span>低额度</span></div>
								<div class="legend-item"><span class="legend-bar loan-mid"></span><span>中额度</span></div>
								<div class="legend-item"><span class="legend-bar loan-high"></span><span>高额度</span></div>
							</div>
						</div>
						<div class="map-layer-switch">
							<div class="legend-title">图层选择</div>
							<a-checkbox-group v-model:value="selectedLayers" :options="layerOptions" class="layer-checkbox-group" />
						</div>
					</div>
				</a-card>
			</div>

			<div class="board-side side-right">
				<a-card :bordered="false" class="panel-card side-block side-top">
					<div class="panel-title">风险预警管理</div>
					<div class="alert-list">
						<div class="alert-item" v-for="alert in alerts" :key="alert.id">
							<div class="alert-top">
								<a-tag :color="alert.color">{{ alert.level }}</a-tag>
								<span>{{ alert.time }}</span>
							</div>
							<div class="alert-content">{{ alert.content }}</div>
						</div>
					</div>
					<a-button block type="primary">批量发送整改通知</a-button>
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
						:data-source="anomalyData"
						:pagination="false"
						row-key="id"
						size="small"
					/>
				</a-card>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const farmTree = [
	{
		title: '张北一号养殖场',
		value: 'farm-1',
		key: 'farm-1'
	},
	{
		title: '张北二号养殖场',
		value: 'farm-2',
		key: 'farm-2'
	}
]

const selectedFarmId = ref()
const selectedLayers = ref(['FARM_DISTRIBUTION', 'HEALTH_HEAT', 'LOAN_HEAT'])
const layerOptions = [
	{ label: '养殖场分布', value: 'FARM_DISTRIBUTION' },
	{ label: '经营健康热力图', value: 'HEALTH_HEAT' },
	{ label: '贷款金额热力图', value: 'LOAN_HEAT' }
]

const panoramaMetrics = [
	{ label: '存栏健康率', value: '92%', percent: 92, color: '#34d399' },
	{ label: '防疫完成率', value: '87%', percent: 87, color: '#60a5fa' },
	{ label: '贷款履约率', value: '94%', percent: 94, color: '#fbbf24' },
	{ label: '在保覆盖率', value: '81%', percent: 81, color: '#a78bfa' }
]

const financeItems = [
	{ title: '贷款管理', value: '在贷余额 1,280万', desc: '逾期率 2.3%' },
	{ title: '投保管理', value: '在保金额 960万', desc: '失效率 1.1%' },
	{ title: '理赔管理', value: '结案率 88.4%', desc: '平均结案 3.2天' }
]

const immunityItems = [
	{ title: '已接种', value: '14,230', desc: '覆盖率 87.1%' },
	{ title: '未接种', value: '1,982', desc: '需补种 3日内完成' },
	{ title: '即将过期', value: '436', desc: '7天内到期' }
]

const centerStats = [
	{ label: '当前筛选农场', value: '2' },
	{ label: '存栏总量', value: '18,420' },
	{ label: '在贷余额', value: '1,280万' },
	{ label: '在保金额', value: '960万' }
]

const mapChartRef = ref()
let mapChart
const chinaMapLoaded = ref(false)
const chinaMapSources = ['/maps/china.json', 'https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json']

const farmScatterData = [
	{ name: '张北一号养殖场', value: [114.72, 41.15, 92], level: 'normal' },
	{ name: '张北二号养殖场', value: [114.80, 41.20, 63], level: 'warning' },
	{ name: '康保一号养殖场', value: [114.60, 41.85, 37], level: 'danger' },
	{ name: '沽源三号养殖场', value: [115.68, 41.67, 88], level: 'normal' }
]

const healthHeatData = [
	{ name: '河北', value: 82 },
	{ name: '北京', value: 76 },
	{ name: '天津', value: 73 },
	{ name: '内蒙古', value: 68 },
	{ name: '山西', value: 71 },
	{ name: '山东', value: 79 },
	{ name: '河南', value: 74 }
]

const loanHeatData = [
	{ name: '河北', value: 1280 },
	{ name: '北京', value: 620 },
	{ name: '天津', value: 540 },
	{ name: '内蒙古', value: 860 },
	{ name: '山西', value: 720 },
	{ name: '山东', value: 980 },
	{ name: '河南', value: 910 }
]

const alerts = [
	{ id: 1, level: '高风险', color: 'red', time: '09:12', content: '张北二号养殖场 7天内防疫即将过期 23 头' },
	{ id: 2, level: '中风险', color: 'orange', time: '08:46', content: '北片区羊类死亡数量超过阈值（12头）' },
	{ id: 3, level: '高风险', color: 'red', time: '08:20', content: '理赔单 LP20260228003 处理超时' }
]

const rectifyItems = [
	{ label: '整改通知发送率', value: '92%', percent: 92, color: '#60a5fa' },
	{ label: '3日内处置完成率', value: '76%', percent: 76, color: '#34d399' },
	{ label: '超期未处理占比', value: '8%', percent: 8, color: '#f87171' }
]

const anomalyColumns = [
	{ title: '养殖场', dataIndex: 'farmName' },
	{ title: '异常类型', dataIndex: 'type' },
	{ title: '风险等级', dataIndex: 'level' },
	{ title: '触发时间', dataIndex: 'time' },
	{ title: '处置状态', dataIndex: 'status' },
	{ title: '整改通知', dataIndex: 'notice' }
]

const anomalyData = [
	{ id: 1, farmName: '张北二号养殖场', type: '防疫过期预警', level: '高', time: '2026-02-28 09:12', status: '待处理', notice: '未发送' },
	{ id: 2, farmName: '张北一号养殖场', type: '死亡异常预警', level: '中', time: '2026-02-28 08:46', status: '处理中', notice: '已发送' }
]

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

	const series = [
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
		}
	]

	const visualMap = []

	if (selectedLayers.value.includes('HEALTH_HEAT')) {
		const idx = series.length
		series.push({
			name: '经营健康热力',
			type: 'map',
			map: 'china',
			roam: true,
			silent: true,
			itemStyle: { borderColor: '#6ec8ab', borderWidth: 0.5, opacity: 0.7 },
			data: healthHeatData
		})
		visualMap.push({
			type: 'continuous',
			seriesIndex: idx,
			min: 50,
			max: 100,
			show: false,
			inRange: { color: ['#f59e0b', '#22c55e', '#10b981'] }
		})
	}

	if (selectedLayers.value.includes('LOAN_HEAT')) {
		const idx = series.length
		series.push({
			name: '贷款金额热力',
			type: 'map',
			map: 'china',
			roam: true,
			silent: true,
			itemStyle: { borderColor: '#6ec8ab', borderWidth: 0.5, opacity: 0.42 },
			data: loanHeatData
		})
		visualMap.push({
			type: 'continuous',
			seriesIndex: idx,
			min: 300,
			max: 1500,
			show: false,
			inRange: { color: ['#60a5fa', '#2563eb', '#1e3a8a'] }
		})
	}

	if (selectedLayers.value.includes('FARM_DISTRIBUTION')) {
		series.push({
			name: '养殖场分布',
			type: 'effectScatter',
			coordinateSystem: 'geo',
			data: farmScatterData,
			symbolSize: (val) => Math.max(8, Math.min(16, val[2] / 6)),
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
		})
		series.push({
			name: '养殖场标签',
			type: 'scatter',
			coordinateSystem: 'geo',
			data: farmScatterData,
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
		})
	}

	mapChart.setOption({
		tooltip: {
			trigger: 'item',
			backgroundColor: 'rgba(9, 32, 27, 0.95)',
			borderColor: 'rgba(108, 189, 164, 0.42)',
			textStyle: { color: '#e7fff6' },
			formatter: (params) => {
				if (params.seriesName === '养殖场分布') {
					return `${params.name}<br/>健康指数：${params.value?.[2] ?? '-'}`
				}
				if (params.seriesName === '经营健康热力') {
					return `${params.name}<br/>健康评分：${params.value ?? '-'}`
				}
				if (params.seriesName === '贷款金额热力') {
					return `${params.name}<br/>贷款金额：${params.value ?? '-'} 万`
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
		visualMap,
		series
	})
}

const initChinaMap = async () => {
	await nextTick()
	if (!mapChartRef.value) return
	mapChart = echarts.init(mapChartRef.value)
	chinaMapLoaded.value = await ensureChinaMap()
	renderChinaMap()
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
			// try next source
		}
	}
	return false
}

const onResize = () => mapChart?.resize()

onMounted(() => {
	initChinaMap()
	window.addEventListener('resize', onResize)
})

onUnmounted(() => {
	window.removeEventListener('resize', onResize)
	if (mapChart) {
		mapChart.dispose()
		mapChart = null
	}
})

watch(selectedLayers, () => {
	renderChinaMap()
})
</script>

<style scoped lang="less">
.regulation-board-page {
	padding: 12px;
	min-height: 100%;
	background: radial-gradient(circle at 20% 10%, #143e33 0%, #102c26 45%, #0c1f1b 100%);
}

.board-header {
	position: relative;
	display: flex;
	justify-content: center;
	margin-bottom: 10px;
	padding-top: 2px;
	overflow: hidden;
}

.board-title-wrap {
	position: relative;
	text-align: center;
	display: inline-block;
	width: fit-content;
	max-width: calc(100% - 220px);
	padding: 4px 12px 8px;
	border-radius: 12px;
}

.board-title-wrap::before,
.board-title-wrap::after {
	content: '';
	position: absolute;
	top: 50%;
	width: 50vw;
	height: 3px;
	transform: translateY(-50%);
	filter: drop-shadow(0 0 8px rgba(87, 216, 177, 0.9));
	background: rgba(112, 238, 200, 0.28);
	overflow: hidden;
}

.board-title-wrap::before {
	right: calc(100% + 6px);
	-webkit-mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0), #000 16%, #000 100%);
	mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0), #000 16%, #000 100%);
}

.board-title-wrap::before {
	box-shadow: inset -40px 0 26px rgba(255, 255, 255, 0.65);
	animation: neon-flow-left 1.8s linear infinite;
}

.board-title-wrap::after {
	left: calc(100% + 6px);
	-webkit-mask-image: linear-gradient(90deg, #000 0%, #000 84%, rgba(0, 0, 0, 0));
	mask-image: linear-gradient(90deg, #000 0%, #000 84%, rgba(0, 0, 0, 0));
	box-shadow: inset 40px 0 26px rgba(255, 255, 255, 0.65);
	animation: neon-flow-right 1.8s linear infinite;
}

@media (max-width: 1400px) {
	.board-title-wrap::before,
	.board-title-wrap::after {
		width: 35vw;
	}
}

.board-title {
	font-size: 36px;
	font-weight: 700;
	letter-spacing: 2px;
	color: #e8fff7;
	text-shadow: 0 0 10px rgba(84, 222, 186, 0.42);
	line-height: 1.1;
}

.board-subtitle {
	margin-top: 2px;
	color: rgba(173, 218, 204, 0.86);
	font-size: 12px;
	letter-spacing: 1px;
}

.board-time {
	position: absolute;
	right: 0;
	top: 2px;
	color: #8cb9ad;
	font-size: 13px;
	padding: 4px 10px;
	border-radius: 999px;
	background: rgba(16, 42, 36, 0.72);
	border: 1px solid rgba(101, 176, 154, 0.3);
}

@keyframes neon-flow-left {
	0% {
		box-shadow: inset -10px 0 20px rgba(255, 255, 255, 0.18);
	}
	100% {
		box-shadow: inset -220px 0 28px rgba(255, 255, 255, 0.9);
	}
}

@keyframes neon-flow-right {
	0% {
		box-shadow: inset 10px 0 20px rgba(255, 255, 255, 0.18);
	}
	100% {
		box-shadow: inset 220px 0 28px rgba(255, 255, 255, 0.9);
	}
}


.panel-card,
.map-panel {
	background: linear-gradient(145deg, rgba(26, 52, 45, 0.92), rgba(16, 36, 31, 0.92));
	border: 1px solid rgba(95, 157, 136, 0.22);
	border-radius: 12px;
}

:deep(.ant-card-body) {
	padding: 12px;
}

.board-main {
	display: flex;
	gap: 12px;
	height: calc(100vh - 140px);
}

.board-side {
	width: 400px;
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.side-block {
	overflow: hidden;
}

.side-top {
	flex: 1;
}

.side-middle {
	flex: 1;
}

.side-bottom {
	flex: 1;
}

.board-center {
	flex: 1;
	min-width: 0;
	display: flex;
}

.panel-title {
	color: #d9fff3;
	font-weight: 600;
	margin-bottom: 12px;
}

.metric-item {
	margin-bottom: 12px;
}

.map-panel {
	width: 100%;
	height: 100%;
	position: relative;
}

.map-panel :deep(.ant-card-body) {
	height: 100%;
	display: flex;
	flex-direction: column;
}

.map-head {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12px;
}

.map-head-tools {
	display: flex;
	gap: 8px;
	align-items: center;
}

.layer-checkbox-group {
	display: flex;
	flex-direction: column;
	gap: 6px;
}

.layer-checkbox-group :deep(.ant-checkbox-wrapper) {
	color: #caece1;
	margin-right: 0;
	padding: 2px 5px;
	border: 1px solid rgba(103, 183, 159, 0.22);
	border-radius: 6px;
	background: rgba(17, 47, 40, 0.72);
	font-size: 12px;
}

.metric-text {
	display: flex;
	justify-content: space-between;
	color: #b9e0d4;
	margin-bottom: 6px;
}

.map-placeholder {
	flex: 1;
	min-height: 360px;
	border-radius: 10px;
	border: 1px dashed rgba(118, 194, 170, 0.35);
	background:
		radial-gradient(circle at 22% 28%, rgba(52, 211, 153, 0.09), transparent 30%),
		radial-gradient(circle at 72% 48%, rgba(248, 113, 113, 0.14), transparent 34%),
		radial-gradient(circle at 50% 70%, rgba(251, 191, 36, 0.1), transparent 26%),
		linear-gradient(180deg, rgba(17, 66, 58, 0.65), rgba(8, 30, 25, 0.9));
	padding: 12px;
	position: relative;
	overflow: hidden;
}

.china-map-chart {
	position: absolute;
	inset: 0;
	width: 100%;
	height: 100%;
	z-index: 1;
}

.center-stats {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 10px;
	margin-bottom: 12px;
}

.stat-chip {
	padding: 8px 10px;
	border-radius: 10px;
	background: rgba(20, 50, 43, 0.9);
	border: 1px solid rgba(120, 194, 171, 0.24);
}

.chip-label {
	color: #8ebeb0;
	font-size: 12px;
}

.chip-value {
	margin-top: 4px;
	color: #f0fff9;
	font-size: 18px;
	font-weight: 700;
}

.map-dot {
	display: inline-block;
	padding: 4px 10px;
	border-radius: 999px;
	font-size: 12px;
	position: absolute;
	top: 10px;
	z-index: 4;
}

.map-dot.normal {
	left: 10px;
}

.map-dot.warning {
	left: 88px;
}

.map-dot.danger {
	left: 160px;
}

.map-dot.normal {
	background: rgba(52, 211, 153, 0.2);
	color: #34d399;
}

.map-dot.warning {
	background: rgba(251, 191, 36, 0.2);
	color: #fbbf24;
}

.map-dot.danger {
	background: rgba(248, 113, 113, 0.2);
	color: #f87171;
}

.map-hint {
	position: absolute;
	left: 12px;
	bottom: 130px;
	color: #9ec9bb;
	font-size: 12px;
	z-index: 2;
}

.map-legend {
	position: absolute;
	left: 12px;
	bottom: 12px;
	width: 190px;
	padding: 8px;
	background: rgba(9, 32, 27, 0.84);
	border: 1px solid rgba(103, 183, 159, 0.32);
	border-radius: 8px;
	color: #caece1;
	font-size: 12px;
	z-index: 3;
}

.map-layer-switch {
	position: absolute;
	right: 12px;
	bottom: 12px;
	width: 176px;
	padding: 6px;
	background: rgba(9, 32, 27, 0.84);
	border: 1px solid rgba(103, 183, 159, 0.32);
	border-radius: 8px;
	color: #caece1;
	font-size: 12px;
	z-index: 3;
}

.legend-title {
	font-weight: 700;
	color: #e7fff6;
	margin-bottom: 6px;
}

.legend-group + .legend-group {
	margin-top: 6px;
	padding-top: 6px;
	border-top: 1px solid rgba(103, 183, 159, 0.24);
}

.legend-sub-title {
	color: #a2d2c3;
	margin-bottom: 4px;
}

.legend-item {
	display: flex;
	align-items: center;
	gap: 6px;
	margin-bottom: 3px;
}

.legend-dot {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	display: inline-block;
}

.legend-dot.normal {
	background: #34d399;
}

.legend-dot.warning {
	background: #fbbf24;
}

.legend-dot.danger {
	background: #f87171;
}

.legend-bar {
	width: 18px;
	height: 8px;
	border-radius: 4px;
	display: inline-block;
}

.legend-bar.health-low {
	background: #f59e0b;
}

.legend-bar.health-mid {
	background: #22c55e;
}

.legend-bar.health-high {
	background: #10b981;
}

.legend-bar.loan-low {
	background: #60a5fa;
}

.legend-bar.loan-mid {
	background: #2563eb;
}

.legend-bar.loan-high {
	background: #1e3a8a;
}

.alert-list {
	height: 165px;
	overflow: auto;
	margin-bottom: 10px;
}

.alert-item {
	background: rgba(25, 52, 45, 0.7);
	border: 1px solid rgba(104, 163, 145, 0.22);
	border-radius: 10px;
	padding: 8px;
	margin-bottom: 8px;
}

.alert-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	color: #9ec9bb;
	font-size: 12px;
	margin-bottom: 4px;
}

.alert-content {
	color: #e8fff7;
	font-size: 13px;
}

.mini-chart {
	border: 1px solid rgba(97, 158, 140, 0.24);
	border-radius: 10px;
	padding: 8px;
	background: rgba(18, 43, 37, 0.82);
	margin-bottom: 8px;
}

.mini-title {
	color: #b3ddd0;
	font-size: 13px;
}

.mini-value {
	color: #f0fff9;
	font-size: 18px;
	font-weight: 700;
	margin-top: 4px;
}

.mini-desc {
	color: #90bfaf;
	font-size: 12px;
	margin-top: 4px;
}

.anomaly-table {
	:deep(.ant-table) {
		background: transparent;
	}
	:deep(.ant-table-thead > tr > th) {
		background: rgba(32, 73, 63, 0.8);
		color: #ddfff4;
		border-bottom-color: rgba(95, 157, 136, 0.35);
	}
	:deep(.ant-table-tbody > tr > td) {
		background: rgba(14, 34, 29, 0.72);
		color: #d3f3e8;
		border-bottom-color: rgba(95, 157, 136, 0.2);
	}
}

:deep(.ant-form-item-label > label) {
	color: #c5ebdf;
}

:deep(.ant-input),
:deep(.ant-select-selector),
:deep(.ant-picker),
:deep(.ant-input-affix-wrapper) {
	background: rgba(12, 34, 29, 0.86) !important;
	border-color: rgba(96, 155, 138, 0.45) !important;
	color: #d9fff4 !important;
}

@media (max-width: 1400px) {
	.board-main {
		flex-direction: column;
		height: auto;
	}

	.board-side {
		width: 100%;
	}

	.board-center {
		width: 100%;
		height: 520px;
	}
}
</style>
