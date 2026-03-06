<template>
  <div class="production-page">
    <div class="intro-card">
      <div class="title">生产管理</div>
      <div class="desc">项圈登记、批量录入、记录查询</div>
    </div>

    <div class="grid-wrap">
      <div class="grid-item" @click="go('/production/register')">
        <van-icon name="edit" size="22" color="#1f8a70" />
        <div>项圈登记</div>
      </div>
      <div class="grid-item" @click="go('/production/batch')">
        <van-icon name="todo-list" size="22" color="#1f8a70" />
        <div>批量登记</div>
      </div>
      <div class="grid-item" @click="go('/production/records')">
        <van-icon name="search" size="22" color="#1f8a70" />
        <div>登记记录</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import router from '@/router'

const go = (path) => router.push({ path })
</script>

<style scoped lang="scss">
.production-page {
  margin: 6px;

  .intro-card {
    background: linear-gradient(135deg, #1f8a70, #2f9b7f);
    color: #fff;
    border-radius: 10px;
    padding: 14px;

    .title {
      font-size: 18px;
      font-weight: 700;
    }

    .desc {
      margin-top: 6px;
      opacity: 0.95;
      font-size: 13px;
    }
  }

  .grid-wrap {
    margin-top: 8px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;

    .grid-item {
      background: #fff;
      border-radius: 10px;
      padding: 16px 10px;
      text-align: center;
      color: #223a35;
      font-size: 14px;
      font-weight: 600;
    }
  }
}
</style>
