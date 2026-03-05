<template>
  <div class="metric-page">
    <div class="page-header">
      <div class="left" @click="goBack">
        <van-icon name="arrow-left" />
        <span>返回</span>
      </div>
      <div class="title">{{ pageTitle }}</div>
      <div class="right" @click="goHome">首页</div>
    </div>

    <div class="filter-bar">
      <van-field
        v-model="startDate"
        readonly
        label="开始"
        placeholder="选择开始日期"
        @click="showStartPicker = true"
      />
      <van-field
        v-model="endDate"
        readonly
        label="结束"
        placeholder="选择结束日期"
        @click="showEndPicker = true"
      />
      <div class="actions">
        <van-button size="small" plain type="primary" @click="resetRange">清空</van-button>
        <van-button size="small" type="primary" @click="loadList">筛选</van-button>
      </div>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="loadList">
      <div v-if="error" class="error-wrap">
        加载失败
        <span class="link" @click="loadList">重试</span>
      </div>
      <van-empty v-else-if="!list.length" description="暂无详情数据" />
      <van-list v-else finished-text="">
        <van-cell v-for="row in list" :key="row.id || row.eventTime" is-link @click="openDetail(row)">
          <template #title>
            <span class="title">{{ row.title || '-' }}</span>
          </template>
          <template #label>
            <div class="content">{{ row.content || '-' }}</div>
            <div class="meta">{{ row.eventTime || '-' }} · {{ row.status || '-' }}</div>
          </template>
        </van-cell>
      </van-list>
    </van-pull-refresh>

    <van-popup v-model:show="showStartPicker" position="bottom" round>
      <van-date-picker
        :model-value="startPickerValue"
        title="选择开始日期"
        @confirm="onStartConfirm"
        @cancel="showStartPicker = false"
      />
    </van-popup>
    <van-popup v-model:show="showEndPicker" position="bottom" round>
      <van-date-picker
        :model-value="endPickerValue"
        title="选择结束日期"
        @confirm="onEndConfirm"
        @cancel="showEndPicker = false"
      />
    </van-popup>

    <van-popup v-model:show="detailVisible" position="bottom" round :style="{ maxHeight: '70%', padding: '16px' }">
      <div class="detail-title">数据详情</div>
      <div class="detail-row">
        <span class="k">标题</span>
        <span class="v">{{ activeDetail.title || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="k">内容</span>
        <span class="v">{{ activeDetail.content || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="k">状态</span>
        <span class="v">{{ activeDetail.status || '-' }}</span>
      </div>
      <div class="detail-row">
        <span class="k">时间</span>
        <span class="v">{{ activeDetail.eventTime || '-' }}</span>
      </div>
      <div class="detail-actions">
        <van-button type="primary" size="small" @click="detailVisible = false">关闭</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showFailToast } from 'vant'
import { mobileHomeMetricDetails } from '@/api/biz/mobileHomeApi'

const route = useRoute()
const router = useRouter()
const refreshing = ref(false)
const error = ref(false)
const list = ref([])
const startDate = ref('')
const endDate = ref('')
const showStartPicker = ref(false)
const showEndPicker = ref(false)
const detailVisible = ref(false)
const activeDetail = ref({})

const now = new Date()
const defaultPicker = [String(now.getFullYear()), String(now.getMonth() + 1).padStart(2, '0'), String(now.getDate()).padStart(2, '0')]
const startPickerValue = ref(defaultPicker)
const endPickerValue = ref(defaultPicker)

const startDateObj = computed(() => (startDate.value ? new Date(startDate.value) : null))
const endDateObj = computed(() => (endDate.value ? new Date(endDate.value) : null))
const pageTitle = computed(() => route.query.metricName || '指标详情')

const loadList = async () => {
  if (startDateObj.value && endDateObj.value && startDateObj.value > endDateObj.value) {
    showFailToast('开始时间不能大于结束时间')
    return
  }
  refreshing.value = true
  error.value = false
  try {
    const res = await mobileHomeMetricDetails({
      metricType: route.query.metricType || '',
      farmId: route.query.farmId || '',
      startDate: startDate.value || '',
      endDate: endDate.value || ''
    })
    list.value = res?.data || []
  } catch (e) {
    error.value = true
  } finally {
    refreshing.value = false
  }
}

const onStartConfirm = ({ selectedValues }) => {
  startPickerValue.value = selectedValues
  startDate.value = selectedValues.join('-')
  showStartPicker.value = false
}

const onEndConfirm = ({ selectedValues }) => {
  endPickerValue.value = selectedValues
  endDate.value = selectedValues.join('-')
  showEndPicker.value = false
}

const resetRange = () => {
  startDate.value = ''
  endDate.value = ''
  loadList()
}

const openDetail = (row) => {
  activeDetail.value = row || {}
  detailVisible.value = true
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
.metric-page {
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

.filter-bar {
  background: #fff;
  padding-bottom: 8px;
  margin-bottom: 8px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 0 12px;
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
}
</style>
