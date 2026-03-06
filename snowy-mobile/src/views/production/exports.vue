<template>
  <div class="page">
    <div class="card">
      <van-cell v-for="item in tasks" :key="item.id" :title="item.name" :label="`创建时间：${item.createTime}，记录数：${item.total}`" is-link @click="download(item.id)">
        <template #value>
          <span class="ok">{{ item.status === 'DONE' ? '下载' : '处理中' }}</span>
        </template>
      </van-cell>
      <snowy-empty v-if="tasks.length === 0" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showFailToast, showSuccessToast } from 'vant'
import SnowyEmpty from '@/components/snowy-empty.vue'
import { downloadExportTask, listExportTasks } from './storage'

const tasks = ref([])

const loadTasks = () => {
  tasks.value = listExportTasks()
}

const download = (id) => {
  const ok = downloadExportTask(id)
  if (ok) {
    showSuccessToast('下载已开始')
  } else {
    showFailToast('当前任务无可下载文件')
  }
}

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.page { margin: 6px; }
.card { background: #fff; border-radius: 10px; overflow: hidden; }
.ok { color: #1f8a70; font-weight: 600; }
</style>
