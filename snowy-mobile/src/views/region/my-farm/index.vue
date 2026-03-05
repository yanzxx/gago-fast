<template>
  <div id="myFarmPage">
    <!-- 顶部tab栏 -->
    <div class="topTabBox" @click.stop>
      <div :class="data.activeTab === item.value ? 'selectedTabItem' : 'tabItem'" v-for="item in data.topTab" :key="item.value" @click="changeTabHandle(item.value)">
        {{item.label}}
      </div>
    </div>
    <land-management v-if="data.activeTab === 0"></land-management>
    <land-growth v-if="data.activeTab === 1"></land-growth>
    <land-track v-if="data.activeTab === 2"></land-track>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import landManagement from './landManagement/index.vue'
import landGrowth from './landGrowth/index.vue'
import landTrack from './landTrack/index.vue'

const data = reactive({
  activeTab: 0,
  topTab: [
    {
      label: '地块',
      value: 0,
    },
    {
      label: '长势',
      value: 1,
    },
    {
      label: '作业数据',
      value: 2,
    },
  ],
})

/** 顶部tab切换 */
const changeTabHandle = (value) => {
  data.activeTab = value
}

</script>

<style lang="less" scoped>
#myFarmPage {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
  .topTabBox {
    height: 36px;
    background-color: #fff;
    position: absolute;
    top: 20px;
    left: 10px;
    z-index: 100;
    border-radius: 18px;
    display: flex;
    align-items: center;
    padding: 3px;
    .tabItem, .selectedTabItem {
      padding: 5px 10px;
      border-radius: 15px;
      color: #475669;
      font-size: 14px;
      font-weight: bold;
    }
    .selectedTabItem {
      color: #fff;
      background: linear-gradient(90deg, #31AD62 0%, #008F5F 100%);
    }
  }
}
</style>
