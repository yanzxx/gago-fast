<template>
  <div class="action-popup">
    <van-action-sheet ref="popupRef" v-model:show="show">
      <van-cell v-if="hasPerm('mobileBizPositionEdit')" title="编辑" :clickable="true" @click="edit" />
      <van-cell v-if="hasPerm('mobileBizPositionDelete')" title="刪除" :clickable="true" @click="del" />
      <van-cell title="取消" :clickable="true" @click="cancel" />
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { positionDelete } from '@/api/biz/bizPositionApi.js'
import modal from '@/plugins/modal.js';
import router from '@/router/index';
import { hasPerm } from '@/plugins/permission';

const emits = defineEmits(['handleOk'])

// 弹出ref
const popupRef = ref()
const show = ref(false)
// 打开
let record = ref({})

const open = (data) => {
  record.value = data
  show.value = true
}
// 编辑
const edit = () => {
  router.push({
    path: '/positionForm',
    query: {
      id: record.value.id
    }
  })
  show.value = false
}
// 删除
const del = () => {
  modal.confirm(`是否确认删除【${ record.value.name }】职位？`).then(() => {
    positionDelete([{
      id: record.value.id
    }]).then(() => {
      emits('handleOk')
      show.value = false
    })
  })
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

.action-popup {
  .van-cell {
    text-align: center;

    .van-cell__title {
      height: 40px;
      justify-content: center;
    }
  }

}
</style>