<template>
  <div class="todo-page">
    <van-pull-refresh v-model="refreshing" @refresh="loadList">
      <div v-if="error" class="error-wrap">
        加载失败
        <span class="link" @click="loadList">重试</span>
      </div>
      <van-empty v-else-if="!list.length" description="暂无待办数据" />
      <van-list v-else finished-text="">
        <van-cell v-for="row in list" :key="row.id || row.triggerTime" is-link @click="goHandle(row)">
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
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { mobileHomeTodoDetails } from '@/api/biz/mobileHomeApi'

const route = useRoute()
const router = useRouter()
const refreshing = ref(false)
const error = ref(false)
const list = ref([])

const loadList = async () => {
  refreshing.value = true
  error.value = false
  try {
    const res = await mobileHomeTodoDetails({
      todoType: route.query.todoType || ''
    })
    list.value = res?.data || []
  } catch (e) {
    error.value = true
  } finally {
    refreshing.value = false
  }
}

const goHandle = (row) => {
  router.push({
    path: '/myFarm',
    query: {
      anomalyId: row.id || '',
      todoType: route.query.todoType || ''
    }
  })
}

onMounted(() => {
  loadList()
})
</script>

<style scoped lang="scss">
.todo-page {
  background: #f5f7fb;
  min-height: calc(100vh - 46px);
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
  color: #1989fa;
  margin-left: 8px;
}
</style>
