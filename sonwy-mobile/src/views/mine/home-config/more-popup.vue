<template>

  <div class="popup-container">
    <van-action-sheet ref="popupRef" v-model:show="show">
      <van-cell v-if="confIndex != 0" title="上移" :clickable="true" @click="up"/>
      <van-cell v-if="confIndex != configs.length - 1" title="下移" :clickable="true" @click="down"/>
      <van-cell title="取消" :clickable="true" @click="cancel"/>
    </van-action-sheet>
  </div>

</template>

<script setup>
import store from '@/store'
import {ref} from "vue"
// 弹出ref
const popupRef = ref()
// 打开
const configs = ref([])
const confIndex = ref()
const show = ref(false)
const open = (data, index) => {
  configs.value = data
  confIndex.value = index
  show.value = true
}
// 上移
const up = () => {
  swapArray(configs.value, confIndex.value - 1, confIndex.value)
  store.commit("SET_homeConfigs", configs.value)
  show.value = false
}

// 下移
const down = () => {
  swapArray(configs.value, confIndex.value, confIndex.value + 1)
  store.commit("SET_homeConfigs", configs.value)
  show.value = false
}

//数组元素互换位置
const swapArray = (arr, index1, index2) => {
  arr[index1] = arr.splice(index2, 1, arr[index1])[0]
  return arr
}
// 取消
const cancel = () => {
  show.value = false
}
defineExpose({
  open
})
</script>
<style lang="scss">

.popup-container {
  .van-cell__title {
    text-align: center !important;
  }
}

</style>