<template>
  <div class="page">
    <div class="card">
      <van-cell v-for="item in tasks" :key="item.id" :title="item.name" :label="`创建时间：${item.createTime}，记录数：${item.total}`">
        <template #value>
          <span class="ok">{{ item.status === 'DONE' ? '已完成' : '处理中' }}</span>
        </template>
      </van-cell>
      <snowy-empty v-if="tasks.length === 0" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import SnowyEmpty from '@/components/snowy-empty.vue'
import { listExportTasks } from './storage'

const tasks = ref([])

onMounted(() => {
  tasks.value = listExportTasks()
})
</script>

<style scoped>
.page { margin: 6px; }
.card { background: #fff; border-radius: 10px; overflow: hidden; }
.ok { color: #1f8a70; font-weight: 600; }
</style>
