<template>
  <div class="page">
    <div class="card">
      <van-field v-model="filter.collarCode" label="项圈编号" placeholder="支持模糊" />
      <van-field v-model="filter.orgName" label="组织" placeholder="请输入组织" />
      <van-field v-model="filter.startDate" label="开始日期" type="date" />
      <van-field v-model="filter.endDate" label="结束日期" type="date" />
      <div class="btn-row">
        <van-button size="small" plain type="primary" @click="reset">清空</van-button>
        <van-button size="small" type="primary" @click="load">查询</van-button>
        <van-button size="small" type="primary" @click="exportNow">导出</van-button>
      </div>
    </div>

    <div class="card">
      <van-cell v-for="item in list" :key="item.id" :title="item.collarCode" :label="`${item.camelTag} | ${item.orgName} | ${item.registerDate}`" is-link @click="goDetail(item.id)">
        <template #value>
          <span class="ok">{{ item.status || '已登记' }}</span>
        </template>
      </van-cell>
      <snowy-empty v-if="list.length === 0" />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import router from '@/router'
import { showSuccessToast } from 'vant'
import SnowyEmpty from '@/components/snowy-empty.vue'
import { createExportTask, listRecords } from './storage'

const filter = reactive({
  collarCode: '',
  orgName: '',
  startDate: '',
  endDate: ''
})
const list = ref([])

const inDate = (d, start, end) => {
  if (!d) return false
  if (start && d < start) return false
  if (end && d > end) return false
  return true
}

const load = () => {
  const all = listRecords()
  list.value = all.filter(item => {
    if (filter.collarCode && !String(item.collarCode || '').includes(filter.collarCode)) return false
    if (filter.orgName && !String(item.orgName || '').includes(filter.orgName)) return false
    if ((filter.startDate || filter.endDate) && !inDate(item.registerDate, filter.startDate, filter.endDate)) return false
    return true
  })
}

const reset = () => {
  filter.collarCode = ''
  filter.orgName = ''
  filter.startDate = ''
  filter.endDate = ''
  load()
}

const exportNow = () => {
  createExportTask({
    name: `项圈登记导出_${Date.now()}`,
    filter: { ...filter },
    total: list.value.length
  })
  showSuccessToast('已创建导出任务')
}

const goDetail = (id) => router.push({ path: '/production/detail', query: { id } })

load()
</script>

<style scoped lang="scss">
.page {
  margin: 6px;
  --van-primary-color: #1f8a70;
  --van-button-primary-background: #1f8a70;
  --van-button-primary-border-color: #1f8a70;
}
.card { background: #fff; border-radius: 10px; padding: 8px; margin-bottom: 8px; }
.btn-row { display: flex; gap: 8px; justify-content: flex-end; margin: 6px 0; }
.ok { color: #1f8a70; font-weight: 600; }
</style>
