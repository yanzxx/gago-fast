<template>
  <van-popup
      v-model:show="showBottom"
      round
      ref="formDialogRef"
      position="bottom"
      :style="{ height: '250px' }"
  >
    <div class="container">
      <van-form ref="formRef" @submit="submit" label-align="top">
        <van-field
            v-model="formData.scheduleTime"
            is-link
            readonly
            name="datePicker"
            label="时间"
            placeholder="请选择时间"
            @click="showPicker = true"
            required
        />
        <van-field
            v-model="formData.scheduleContent"
            rows="2"
            autosize
            label="日程描述"
            type="textarea"
            placeholder="请输入日程描述"
            required
        />
        <van-popup v-model:show="showPicker" position="bottom">
          <van-time-picker v-model="currentTime" title="" @confirm="onConfirm" @cancel="showPicker = false" />
        </van-popup>
      </van-form>
      <van-button type="primary" @click="submit" style="width: 100%;">确定</van-button>
    </div>
  </van-popup>
</template>

<script setup>
	import {
		ref,
    defineEmits
	} from "vue";
	import {
		indexScheduleAdd
	} from '@/api/sys/indexApi'

	const formDialogRef = ref()
	const formRef = ref()
	const formData = ref({})
  const showBottom = ref(false)
  const showPicker = ref(false);
  const currentTime = ref(['12', '00']);
	const emit = defineEmits({
		ok: null
	})
	const onOpen = (data) => {
		formData.value.scheduleDate = data
    showBottom.value = true
	}
	const submit = () => {
		formRef.value.validate().then(() => {
			indexScheduleAdd(formData.value).then(res => {
				emit('ok', res)
        showBottom.value = false
				// 重置表单数据
				formData.value = {}
			})
		})
	}

  const onConfirm = ({ selectedValues }) => {
    formData.value.scheduleTime = selectedValues.join(':');
    showPicker.value = false;
  };

	defineExpose({
		onOpen
	})
</script>

<style lang="scss">
	.container {
		border-radius: 5px;
		padding: 15px 6px;
		background-color: #FFF;

	}
</style>