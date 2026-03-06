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
      <van-loading v-if="loading" size="24px" vertical>加载中</van-loading>
      <van-cell v-for="item in list" :key="item.id" :title="item.collarCode" :label="`${item.camelTag} | ${item.orgName} | ${item.registerDate}`" is-link @click="goDetail(item.id)">
        <template #value>
          <span class="ok">{{ item.status || 'IN_STOCK' }}</span>
        </template>
      </van-cell>
      <snowy-empty v-if="!loading && list.length === 0" />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import router from '@/router'
import { showFailToast, showSuccessToast } from 'vant'
import SnowyEmpty from '@/components/snowy-empty.vue'
import { buildRecordCsv, createExportTask, listRecords } from './storage'
import { livestockPage } from '@/api/biz/livestockApi'
import { orgTree } from '@/api/biz/bizOrgApi'

const filter = reactive({
  collarCode: '',
  orgName: '',
  startDate: '',
  endDate: ''
})
const list = ref([])
const loading = ref(false)
const farmNameMap = ref({})

const inDate = (d, start, end) => {
  if (!d) return false
  if (start && d < start) return false
  if (end && d > end) return false
  return true
}

const flattenFarmMap = (nodes = [], collector = {}) => {
  ;(nodes || []).forEach((node) => {
    const id = node?.id || node?.value
    const name = node?.name || node?.title
    if (id && name) {
      collector[String(id)] = String(name)
    }
    if (node?.children?.length) {
      flattenFarmMap(node.children, collector)
    }
  })
  return collector
}

const mapRemoteItem = (item = {}) => {
  const farmId = item.farmId || ''
  return {
    id: item.id || item.livestockId || item.livestockID || item.livestock_id || item.livestockNo,
    livestockNo: item.livestockNo || '',
    collarCode: item.collarNo || '',
    camelTag: item.speciesName || '-',
    orgName: farmNameMap.value[String(farmId)] || farmId || '-',
    registerDate: item.registerDate || '',
    status: item.status || 'IN_STOCK',
    source: 'REMOTE'
  }
}

const loadLocal = () => {
  const all = listRecords()
  list.value = all.filter((item) => {
    if (filter.collarCode && !String(item.collarCode || '').includes(filter.collarCode)) return false
    if (filter.orgName && !String(item.orgName || '').includes(filter.orgName)) return false
    if ((filter.startDate || filter.endDate) && !inDate(item.registerDate, filter.startDate, filter.endDate)) return false
    return true
  })
}

const load = async () => {
  loading.value = true
  try {
    if (!Object.keys(farmNameMap.value).length) {
      const orgRes = await orgTree()
      farmNameMap.value = flattenFarmMap(orgRes?.data || [])
    }
    const res = await livestockPage({
      current: 1,
      size: 50,
      collarNo: filter.collarCode || undefined,
      registerStartDate: filter.startDate || undefined,
      registerEndDate: filter.endDate || undefined
    })
    const records = res?.data?.records || []
    let mapped = records.map(mapRemoteItem)
    if (filter.orgName) {
      mapped = mapped.filter((item) => String(item.orgName || '').includes(filter.orgName))
    }
    list.value = mapped
  } catch (e) {
    loadLocal()
    showFailToast('已切换离线记录')
  } finally {
    loading.value = false
  }
}

const reset = () => {
  filter.collarCode = ''
  filter.orgName = ''
  filter.startDate = ''
  filter.endDate = ''
  load()
}

const exportNow = () => {
  if (!list.value.length) {
    showFailToast('暂无可导出数据')
    return
  }
  const content = buildRecordCsv(list.value)
  createExportTask({
    name: `项圈登记导出_${Date.now()}`,
    filter: { ...filter },
    fileName: `项圈登记导出_${Date.now()}.csv`,
    content,
    total: list.value.length
  })
  showSuccessToast('已创建导出任务')
}

const goDetail = (id) => {
  const row = list.value.find((item) => String(item.id) === String(id)) || {}
  router.push({ path: '/production/detail', query: { id, livestockNo: row.livestockNo || '' } })
}

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
