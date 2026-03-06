<template>
  <div class="page">
    <div class="card" v-if="record">
      <van-cell title="项圈编号" :value="record.collarCode" />
      <van-cell title="牲畜编号" :value="record.livestockNo || '-'" />
      <van-cell title="编号模式" :value="record.codeMode || '-'" />
      <van-cell title="骆驼标识/畜种" :value="record.camelTag" />
      <van-cell title="组织/养殖场" :value="record.orgName" />
      <van-cell title="登记日期" :value="record.registerDate" />
      <van-cell title="状态" :value="record.status || '-'" />
      <van-cell title="骆驼照片" :value="record.camelPhoto || '-'" />
      <van-cell title="项圈照片" :value="record.collarPhoto || '-'" />
      <van-cell title="备注" :value="record.remark || '-'" />
      <van-cell title="创建时间" :value="record.createTime" />
    </div>
    <snowy-empty v-else />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import router from '@/router'
import SnowyEmpty from '@/components/snowy-empty.vue'
import { getRecord } from './storage'
import { livestockDetail } from '@/api/biz/livestockApi'
import { orgTree } from '@/api/biz/bizOrgApi'

const record = ref(null)
const farmNameMap = ref({})

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

const loadDetail = async () => {
  const routeQuery = router.currentRoute.value.query || {}
  const id = routeQuery.id
  const livestockNo = routeQuery.livestockNo
  try {
    if (!Object.keys(farmNameMap.value).length) {
      const orgRes = await orgTree()
      farmNameMap.value = flattenFarmMap(orgRes?.data || [])
    }
    const res = await livestockDetail({
      id: id || undefined,
      livestockNo: livestockNo || undefined
    })
    const data = res?.data || {}
    if (data?.id || data?.livestockNo) {
      const farmId = data.farmId || ''
      record.value = {
        id: data.id || '',
        livestockNo: data.livestockNo || '',
        collarCode: data.collarNo || '',
        camelTag: data.speciesName || '-',
        orgName: farmNameMap.value[String(farmId)] || farmId || '-',
        registerDate: data.registerDate || '',
        status: data.status || '',
        camelPhoto: '',
        collarPhoto: '',
        remark: data.remark || '',
        createTime: data.createTime || data.registerDate || ''
      }
      return
    }
  } catch (e) {
    // fallback local
  }
  record.value = getRecord(id)
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.page { margin: 6px; }
.card { background: #fff; border-radius: 10px; overflow: hidden; }
</style>
