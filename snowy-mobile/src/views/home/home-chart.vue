<template>
  <div class="home-dashboard">
    <div class="section header-section">
      <div class="header-top">
        <!-- <div class="org">{{ header.orgName || '-' }}</div> -->
      </div>
      <van-dropdown-menu>
        <van-dropdown-item v-model="selectedFarmId" :options="farmOptions" @change="onFarmChange" />
      </van-dropdown-menu>
    </div>

    <div class="section">
      <div class="section-title">核心指标</div>
      <div v-if="metricsErr" class="error-line">
        指标加载失败
        <span class="link" @click="loadMetrics">重试</span>
      </div>
      <div v-else class="metric-grid">
        <div class="metric-card" @click="toMyFarm('totalStockCount')">
          <div class="label">总存栏量</div>
          <div class="value">{{ metrics.totalStockCount || 0 }}</div>
        </div>
        <div class="metric-card" @click="toMyFarm('inStockCount')">
          <div class="label">在栏数</div>
          <div class="value">{{ metrics.inStockCount || 0 }}</div>
        </div>
        <div class="metric-card warn" @click="toMyFarm('deviceAnomalyCount')">
          <div class="label">设备异常</div>
          <div class="value">{{ metrics.deviceAnomalyCount || 0 }}</div>
        </div>
        <div class="metric-card danger" @click="toMyFarm('collarAnomalyCount')">
          <div class="label">项圈异常</div>
          <div class="value">{{ metrics.collarAnomalyCount || 0 }}</div>
        </div>
      </div>
    </div>

    <div class="section">
      <div class="section-title">近7天核心指标趋势</div>
      <div v-if="weeklyErr" class="error-line">
        趋势加载失败
        <span class="link" @click="loadWeeklyStats">重试</span>
      </div>
      <van-empty v-else-if="!weeklyStats.length" description="暂无趋势数据" />
      <div v-else ref="weeklyChartRef" class="weekly-chart"></div>
    </div>

    <div class="section">
      <div class="section-title">待办事项</div>
      <div v-if="todosErr" class="error-line">
        待办加载失败
        <span class="link" @click="loadTodos">重试</span>
      </div>
      <template v-else>
        <van-empty v-if="!todos.length" description="暂无待办事项" />
        <div v-else class="todo-group">
          <div class="todo-card" v-for="todo in todos" :key="todo.todoType" @click="toTodoList(todo)">
            <div class="todo-title-row">
              <span class="todo-name">{{ todo.todoName }}</span>
              <span class="todo-count">{{ todo.count || 0 }}</span>
            </div>
            <div class="todo-item" v-for="item in todo.items" :key="item.id || item.triggerTime">
              <span class="dot"></span>
              <span class="text">{{ item.content || item.anomalyType || '-' }}</span>
            </div>
            <div v-if="!todo.items || !todo.items.length" class="todo-item empty-text">暂无摘要</div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { mobileHomeHeader, mobileHomeMetrics, mobileHomeTodos, mobileHomeWeeklyStats } from '@/api/biz/mobileHomeApi'
import { orgTree } from '@/api/biz/bizOrgApi'
import store from '@/store'

const router = useRouter()
const metricsErr = ref(false)
const todosErr = ref(false)
const weeklyErr = ref(false)
const selectedFarmId = ref('')
const farmOptions = ref([])
const weeklyStats = ref([])
const weeklyChartRef = ref(null)
let weeklyChartIns = null
const header = reactive({
  orgId: '',
  orgName: '',
  refreshTime: ''
})
const metrics = reactive({
  totalStockCount: 0,
  inStockCount: 0,
  deviceAnomalyCount: 0,
  collarAnomalyCount: 0
})
const todos = ref([])

const loadHeader = async () => {
  const res = await mobileHomeHeader({
    farmId: selectedFarmId.value || undefined
  })
  const data = res?.data || {}
  header.orgId = data.orgId || ''
  header.orgName = data.orgName || ''
  header.refreshTime = data.refreshTime || ''
}

const loadMetrics = async () => {
  metricsErr.value = false
  try {
    const res = await mobileHomeMetrics({
      farmId: selectedFarmId.value || undefined
    })
    const data = res?.data || {}
    metrics.totalStockCount = data.totalStockCount || 0
    metrics.inStockCount = data.inStockCount || 0
    metrics.deviceAnomalyCount = data.deviceAnomalyCount || 0
    metrics.collarAnomalyCount = data.collarAnomalyCount || 0
  } catch (e) {
    metricsErr.value = true
  }
}

const loadTodos = async () => {
  todosErr.value = false
  try {
    const res = await mobileHomeTodos({
      farmId: selectedFarmId.value || undefined,
      topN: 3
    })
    todos.value = res?.data || []
  } catch (e) {
    todosErr.value = true
  }
}

const renderWeeklyChart = async () => {
  if (!weeklyStats.value.length) {
    if (weeklyChartIns) {
      weeklyChartIns.dispose()
      weeklyChartIns = null
    }
    return
  }
  await nextTick()
  if (!weeklyChartRef.value) {
    return
  }
  if (weeklyChartIns) {
    weeklyChartIns.dispose()
  }
  weeklyChartIns = echarts.init(weeklyChartRef.value)
  const xData = weeklyStats.value.map((item) => item.dayLabel || '-')
  const totalStockData = weeklyStats.value.map((item) => item.totalStockCount || 0)
  const inStockData = weeklyStats.value.map((item) => item.inStockCount || 0)
  const deviceAnomalyData = weeklyStats.value.map((item) => item.deviceAnomalyCount || 0)
  const collarAnomalyData = weeklyStats.value.map((item) => item.collarAnomalyCount || 0)
  weeklyChartIns.setOption({
    color: ['#0f8f5f', '#22c55e', '#14b8a6', '#ef4444'],
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      top: 36,
      left: 10,
      right: 10,
      bottom: 10,
      containLabel: true
    },
    legend: {
      top: 0,
      icon: 'roundRect',
      itemWidth: 10,
      itemHeight: 6,
      textStyle: {
        fontSize: 10
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData,
      axisLabel: { fontSize: 10, color: '#5c6b7a' },
      axisLine: { lineStyle: { color: '#d9e2ec' } }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontSize: 10, color: '#5c6b7a' },
      splitLine: { lineStyle: { color: '#edf2f7' } }
    },
    series: [
      { name: '总存栏量', type: 'line', smooth: true, symbolSize: 6, data: totalStockData },
      { name: '在栏数', type: 'line', smooth: true, symbolSize: 6, data: inStockData },
      { name: '设备异常', type: 'line', smooth: true, symbolSize: 6, data: deviceAnomalyData },
      { name: '项圈异常', type: 'line', smooth: true, symbolSize: 6, data: collarAnomalyData }
    ]
  })
}

const loadWeeklyStats = async () => {
  weeklyErr.value = false
  try {
    const res = await mobileHomeWeeklyStats({
      farmId: selectedFarmId.value || undefined
    })
    weeklyStats.value = res?.data || []
    renderWeeklyChart()
  } catch (e) {
    weeklyErr.value = true
    weeklyStats.value = []
    renderWeeklyChart()
  }
}

const loadAll = async () => {
  await Promise.allSettled([loadHeader(), loadMetrics(), loadWeeklyStats(), loadTodos()])
}

const flattenFarmOptions = (nodes = [], collector = []) => {
  ;(nodes || []).forEach((node) => {
    const id = node?.id || node?.value
    const name = node?.name || node?.title
    const children = node?.children || []
    if (children.length) {
      flattenFarmOptions(children, collector)
      return
    }
    if (id && name) {
      collector.push({
        text: String(name),
        value: String(id)
      })
    }
  })
  return collector
}

const resolveUserFarmId = () => {
  const userInfo = store.getters.userInfo || {}
  return String(userInfo.farmId || userInfo.farm_id || userInfo.orgId || userInfo.org_id || '')
}

const hasFarmOption = (farmId) => {
  if (!farmId) return false
  return farmOptions.value.some((item) => String(item.value) === String(farmId))
}

const loadFarmOptions = async () => {
  try {
    const res = await orgTree()
    const options = flattenFarmOptions(res?.data || [])
    farmOptions.value = options
    const preferredFarmId = selectedFarmId.value || resolveUserFarmId()
    if (hasFarmOption(preferredFarmId)) {
      selectedFarmId.value = String(preferredFarmId)
      return
    }
    if (options.length) {
      selectedFarmId.value = String(options[0].value)
      return
    }
    selectedFarmId.value = ''
  } catch (e) {
    farmOptions.value = []
    selectedFarmId.value = ''
  }
}

const onFarmChange = () => {
  loadAll()
}

const metricNameMap = {
  totalStockCount: '总存栏量',
  inStockCount: '在栏数',
  deviceAnomalyCount: '设备异常',
  collarAnomalyCount: '项圈异常'
}

const toMyFarm = (source) => {
  router.push({
    path: '/homeMetricList',
    query: {
      metricType: source,
      metricName: metricNameMap[source] || source,
      farmId: selectedFarmId.value || ''
    }
  })
}

const toTodoList = (todo) => {
  router.push({
    path: '/homeTodoList',
    query: {
      todoType: todo.todoType,
      todoName: todo.todoName,
      farmId: selectedFarmId.value || ''
    }
  })
}

onMounted(() => {
  loadFarmOptions().finally(() => {
    loadAll()
  })
  window.addEventListener('resize', handleResize)
})

const handleResize = () => {
  if (weeklyChartIns) {
    weeklyChartIns.resize()
  }
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (weeklyChartIns) {
    weeklyChartIns.dispose()
    weeklyChartIns = null
  }
})
</script>

<style lang="scss">
.home-dashboard {
  background: #f5f7fb;
  padding: 8px;
}

.section {
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 10px;
}

.header-section {
  .header-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  .org {
    font-size: 16px;
    font-weight: 600;
    color: #1f2d3d;
  }

  .time {
    font-size: 12px;
    color: #7a869a;
  }
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2d3d;
  margin-bottom: 10px;
}

.metric-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.metric-card {
  border-radius: 8px;
  padding: 10px;
  background: #f5f8ff;
  border: 1px solid #e7edff;

  .label {
    font-size: 12px;
    color: #6b778c;
  }

  .value {
    margin-top: 6px;
    font-size: 20px;
    color: #1d4ed8;
    font-weight: 600;
  }
}

.metric-card.warn {
  background: #fff8eb;
  border-color: #ffe7b8;

  .value {
    color: #d48806;
  }
}

.metric-card.danger {
  background: #fff2f0;
  border-color: #ffccc7;

  .value {
    color: #cf1322;
  }
}

.todo-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.weekly-chart {
  width: 100%;
  height: 260px;
}

.todo-card {
  border: 1px solid #eef2f7;
  border-radius: 8px;
  padding: 10px;
  background: #fcfdff;
}

.todo-title-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.todo-name {
  font-size: 14px;
  color: #1f2d3d;
  font-weight: 600;
}

.todo-count {
  font-size: 14px;
  color: #1989fa;
  font-weight: 600;
}

.todo-item {
  display: flex;
  align-items: center;
  padding: 3px 0;
  color: #5c6b7a;
  font-size: 12px;
}

.todo-item .dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #91a1b5;
  margin-right: 6px;
}

.todo-item .text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.error-line {
  font-size: 12px;
  color: #cf1322;
}

.link {
  color: #0f8f5f;
  margin-left: 8px;
}

.empty-text {
  color: #9aa5b5;
}
</style>
