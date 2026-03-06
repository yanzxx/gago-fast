<template>
  <div class="todo-page">
    <div v-if="showHeader" class="page-header">
      <div class="left" @click="goBack">
        <van-icon name="arrow-left" />
        <span>返回</span>
      </div>
      <div class="title">{{ pageTitle }}</div>
      <div class="right" @click="goHome">首页</div>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="loadList">
      <div v-if="error" class="error-wrap">
        加载失败
        <span class="link" @click="loadList">重试</span>
      </div>
      <van-empty v-else-if="!list.length" description="暂无待办数据" />
      <van-list v-else finished-text="">
        <van-cell v-for="row in list" :key="row.id || row.triggerTime" is-link @click="openDetail(row)">
          <template #title>
            <span class="title">{{ row.anomalyType || '-' }}</span>
          </template>
          <template #label>
            <div class="content">{{ row.content || '-' }}</div>
            <div class="meta">{{ row.triggerTime || '-' }} · {{ row.status || '-' }}</div>
          </template>
        </van-cell>
      </van-list>
    </van-pull-refresh>

    <van-popup v-model:show="detailVisible" position="bottom" round :style="{ maxHeight: '70%', padding: '16px' }">
      <div class="detail-title">待办详情</div>
      <div class="detail-row">
        <span class="k">类型</span>
        <span class="v">{{ activeDetail.anomalyType || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="k">状态</span>
        <span class="v">{{ activeDetail.status || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="k">时间</span>
        <span class="v">{{ activeDetail.triggerTime || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="k">内容</span>
        <span class="v">{{ activeDetail.content || '-' }}</span>
      </div>
      <div class="detail-actions">
        <van-button plain type="primary" size="small" @click="detailVisible = false">关闭</van-button>
        <van-button
          type="primary"
          size="small"
          :disabled="!activeDetail.farmId"
          @click="goFarm(activeDetail)"
        >
          去养殖场
        </van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { mobileHomeTodoDetails } from '@/api/biz/mobileHomeApi'
import { showFailToast } from 'vant'

const route = useRoute()
const router = useRouter()
const refreshing = ref(false)
const error = ref(false)
const list = ref([])
const detailVisible = ref(false)
const activeDetail = ref({})
const pageTitle = computed(() => route.query.todoName || '待办详情')
const showHeader = computed(() => {
  const todoType = String(route.query.todoType || '')
  const todoName = String(route.query.todoName || '')
  const hiddenTypes = ['ESTRUS', 'BREED', 'ILLEGAL_OUT']
  const hiddenNames = ['发情提醒', '配种提醒', '非法离栏']
  return !hiddenTypes.includes(todoType) && !hiddenNames.includes(todoName)
})

const loadList = async () => {
  refreshing.value = true
  error.value = false
  try {
    const res = await mobileHomeTodoDetails({
      todoType: route.query.todoType || '',
      farmId: route.query.farmId || ''
    })
    list.value = res?.data || []
  } catch (e) {
    error.value = true
  } finally {
    refreshing.value = false
  }
}

const openDetail = (row) => {
  activeDetail.value = row || {}
  detailVisible.value = true
}

const goFarm = async (row) => {
  try {
    await router.push({
      path: '/myFarm',
      query: {
        farmId: row?.farmId || '',
        anomalyId: row?.id || '',
        todoType: route.query.todoType || ''
      }
    })
    detailVisible.value = false
  } catch (e) {
    showFailToast('跳转失败，请稍后重试')
  }
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
    return
  }
  goHome()
}

const goHome = () => {
  router.push('/home')
}

onMounted(() => {
  loadList()
})
</script>

<style scoped lang="scss">
.todo-page {
  background: #f5f7fb;
  min-height: 100vh;
}

.page-header {
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  background: #0f8f5f;
  border-bottom: 1px solid #0c774f;
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-header .left,
.page-header .right {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #ffffff;
  font-size: 13px;
}

.page-header .title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
}

.title {
  font-size: 14px;
  font-weight: 600;
}

.content {
  font-size: 12px;
  color: #5c6b7a;
  margin-top: 2px;
}

.meta {
  font-size: 12px;
  color: #9aa5b5;
  margin-top: 2px;
}

.error-wrap {
  font-size: 12px;
  color: #cf1322;
  padding: 12px;
}

.link {
  color: #0f8f5f;
  margin-left: 8px;
}

:deep(.van-button--primary) {
  background: #0f8f5f;
  border-color: #0f8f5f;
}

:deep(.van-button--primary.van-button--plain) {
  color: #0f8f5f;
  background: #ffffff;
  border-color: #0f8f5f;
}

.detail-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.detail-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  line-height: 1.5;
}

.detail-row .k {
  width: 56px;
  color: #8795a1;
}

.detail-row .v {
  flex: 1;
  color: #2f3e4d;
  word-break: break-word;
}

.detail-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
