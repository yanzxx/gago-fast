<template>
  <div class="page">
    <div class="card" v-if="record">
      <van-cell title="项圈编号" :value="record.collarCode" />
      <van-cell title="编号模式" :value="record.codeMode" />
      <van-cell title="骆驼标识" :value="record.camelTag" />
      <van-cell title="组织/养殖场" :value="record.orgName" />
      <van-cell title="登记日期" :value="record.registerDate" />
      <van-cell title="骆驼照片" :value="record.camelPhoto || '-'" />
      <van-cell title="项圈照片" :value="record.collarPhoto || '-'" />
      <van-cell title="备注" :value="record.remark || '-'" />
      <van-cell title="创建时间" :value="record.createTime" />
    </div>
    <snowy-empty v-else />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import router from '@/router'
import SnowyEmpty from '@/components/snowy-empty.vue'
import { getRecord } from './storage'

const record = computed(() => getRecord(router.currentRoute.value.query.id))
</script>

<style scoped>
.page { margin: 6px; }
.card { background: #fff; border-radius: 10px; overflow: hidden; }
</style>
