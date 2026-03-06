<template>
  <div class="page">
    <div class="card">
      <div class="tips">批量模板：每行一条，格式：项圈编号,骆驼标识,组织名称,登记日期</div>
      <van-field v-model="batchText" rows="10" autosize type="textarea" placeholder="例如：XC202603060001,CM001,一牧场,2026-03-06" />
      <div class="btns">
        <van-button block plain type="primary" @click="checkBatch">先校验</van-button>
        <van-button block type="primary" @click="submitBatch" style="margin-top: 8px;">批量提交</van-button>
      </div>
    </div>

    <div class="card" v-if="checkResult.length">
      <div class="title">校验结果</div>
      <van-cell v-for="row in checkResult" :key="row.rowNo" :title="`第${row.rowNo}行 - ${row.collarCode || '-'}`" :label="row.error || '通过'">
        <template #value>
          <span :style="{color: row.error ? '#ee0a24' : '#1f8a70'}">{{ row.error ? '失败' : '通过' }}</span>
        </template>
      </van-cell>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showFailToast, showSuccessToast } from 'vant'
import { existsCode, parseBatchText, saveBatch } from './storage'
import { orgTree } from '@/api/biz/bizOrgApi'
import { livestockAdd } from '@/api/biz/livestockApi'

const batchText = ref('')
const checkResult = ref([])
const farmIdByName = ref({})
const submitting = ref(false)

const flattenFarmMap = (nodes = [], collector = {}) => {
  ;(nodes || []).forEach((node) => {
    const id = node?.id || node?.value
    const name = node?.name || node?.title
    if (id && name) {
      collector[String(name)] = String(id)
    }
    if (node?.children?.length) {
      flattenFarmMap(node.children, collector)
    }
  })
  return collector
}

const loadFarmMap = async () => {
  try {
    const res = await orgTree()
    farmIdByName.value = flattenFarmMap(res?.data || [])
  } catch (e) {
    farmIdByName.value = {}
  }
}

const checkBatch = () => {
  const rows = parseBatchText(batchText.value)
  if (!rows.length) {
    checkResult.value = []
    showFailToast('请先输入批量数据')
    return false
  }
  const seen = new Set()
  checkResult.value = rows.map((r) => {
    let error = ''
    if (!r.collarCode || !r.camelTag || !r.orgName || !r.registerDate) error = '必填字段缺失'
    else if (seen.has(r.collarCode)) error = '批次内编号重复'
    else if (existsCode(r.collarCode)) error = '系统内编号重复'
    seen.add(r.collarCode)
    return { ...r, error }
  })
  showSuccessToast('校验完成')
  return checkResult.value.some((v) => !v.error)
}

const submitBatch = async () => {
  if (submitting.value) return
  if (!checkResult.value.length) {
    const hasPass = checkBatch()
    if (!hasPass) return
  }
  const passRows = checkResult.value.filter(v => !v.error)
  if (!passRows.length) {
    showFailToast('无可提交数据')
    return
  }
  submitting.value = true
  const failedRows = []
  let successCount = 0
  for (const row of passRows) {
    try {
      await livestockAdd({
        farmId: farmIdByName.value[row.orgName] || undefined,
        speciesName: row.camelTag,
        birthDate: row.registerDate,
        immunityStatus: 'NORMAL',
        lastImmunityDate: row.registerDate,
        collarNo: row.collarCode,
        remark: '批量登记',
        imageUrls: '[]',
        status: 'IN_STOCK'
      })
      successCount += 1
    } catch (e) {
      failedRows.push(row)
    }
  }
  if (failedRows.length) {
    saveBatch(failedRows.map(v => ({
      codeMode: 'MANUAL',
      collarCode: v.collarCode,
      camelTag: v.camelTag,
      orgName: v.orgName,
      registerDate: v.registerDate,
      camelPhoto: '批量补传',
      collarPhoto: '批量补传',
      remark: '批量登记(离线)',
      status: '已登记(离线)',
      syncStatus: 'LOCAL_ONLY'
    })))
  }
  submitting.value = false
  if (!successCount) {
    showFailToast(`后端提交失败，已离线保存 ${failedRows.length} 条`)
    return
  }
  if (failedRows.length) {
    showSuccessToast(`提交成功 ${successCount} 条，离线保存 ${failedRows.length} 条`)
    return
  }
  showSuccessToast(`提交成功 ${successCount} 条`)
}

onMounted(() => {
  loadFarmMap()
})
</script>

<style scoped lang="scss">
.page {
  margin: 6px;
  --van-primary-color: #1f8a70;
  --van-button-primary-background: #1f8a70;
  --van-button-primary-border-color: #1f8a70;
}
.card { background: #fff; border-radius: 10px; padding: 10px; margin-bottom: 8px; }
.tips { font-size: 12px; color: #6b8f84; margin-bottom: 8px; }
.title { font-size: 14px; font-weight: 700; margin-bottom: 8px; }
</style>
