<template>
  <div class="home-dashboard">
    <div class="section header-section">
      <div class="header-top">
        <div class="org">{{ header.orgName || '-' }}</div>
      </div>
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
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { mobileHomeHeader, mobileHomeMetrics, mobileHomeTodos } from '@/api/biz/mobileHomeApi'

const router = useRouter()
const metricsErr = ref(false)
const todosErr = ref(false)
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
  const res = await mobileHomeHeader()
  const data = res?.data || {}
  header.orgId = data.orgId || ''
  header.orgName = data.orgName || ''
  header.refreshTime = data.refreshTime || ''
}

const loadMetrics = async () => {
  metricsErr.value = false
  try {
    const res = await mobileHomeMetrics()
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
    const res = await mobileHomeTodos({ topN: 3 })
    todos.value = res?.data || []
  } catch (e) {
    todosErr.value = true
  }
}

const loadAll = async () => {
  await Promise.allSettled([loadHeader(), loadMetrics(), loadTodos()])
}

const toMyFarm = (source) => {
  router.push({
    path: '/myFarm',
    query: { source }
  })
}

const toTodoList = (todo) => {
  router.push({
    path: '/homeTodoList',
    query: {
      todoType: todo.todoType,
      todoName: todo.todoName
    }
  })
}

onMounted(() => {
  loadAll()
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
  color: #1989fa;
  margin-left: 8px;
}

.empty-text {
  color: #9aa5b5;
}
</style>
