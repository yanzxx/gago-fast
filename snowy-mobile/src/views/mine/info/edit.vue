<template>
	<div class="edit-container">
    <van-form label-align="top" ref="formRef">
      <van-field
          v-model="formData.name"
          name="name"
          label="姓名"
          placeholder="请输入姓名"
          required
          :rules="[{ required: true, message: '请输入姓名' }]"
      />
      <van-field
          v-model="formData.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
      />
      <van-field
          v-model="formData.nickname"
          name="nickname"
          label="昵称"
          placeholder="请输入昵称"
      />
      <van-field name="gender" label="单选框">
        <template #input>
          <van-radio-group v-model="formData.gender" direction="horizontal">
            <van-radio name="1">男</van-radio>
            <van-radio name="2">女</van-radio>
          </van-radio-group>
        </template>
      </van-field>

      <van-field
          v-model="formData.birthday"
          is-link
          readonly
          name="calendar"
          label="出生日期"
          placeholder="点击选择出生日期"
          @click="showCalendar = true"
      />

      <van-action-sheet v-model:show="showCalendar" title="出生日期">
        <Calendar
            backgroundText
            :selectMode="'select'"
            class-name="select-mode"
            :select-date="selectModeDate"
            completion="true"
            @onSelect="onConfirm1"
        />
      </van-action-sheet>
      <van-field
          v-model="formData.email"
          name="email"
          label="邮箱"
          placeholder="请输入邮箱"
      />
      <van-button type="primary" @click="submit" style="margin-top: 10px;width: 100%;">提交</van-button>
    </van-form>
	</div>
</template>

<script setup>
	import { userUpdateUserInfo } from '@/api/sys/userCenterApi'
	import { ref } from "vue";
	import store from '@/store/index.js'
	import modal from '@/plugins/modal'
	import XEUtils from 'xe-utils'
  import Calendar from "mpvue-calendar";

  const currentDate = new Date()
  const currentYear = currentDate.getFullYear()
  const currentMonth = currentDate.getMonth() + 1
  const currentDay = currentDate.getDate()

  const selectModeDate = ref(`${currentYear}-${currentMonth}-${currentDay}`)

	const formRef = ref()
	const formData = ref(XEUtils.clone(store.getters.userInfo, true))
  const showCalendar = ref(false)
	// 提交
	const submit = () => {
		formRef.value.validate().then(res => {
			userUpdateUserInfo(formData.value).then(response =>{
				// 更新缓存
				store.commit('SET_userInfo', formData.value)
				modal.msgSuccess('修改成功')
			})
		})
	}

  const onConfirm1 = (date) => {
    formData.value.birthday = date;
    showCalendar.value = false;
  };
</script>

<style lang="scss">
	.edit-container {
		margin: 6px;
		border-radius: 5px;
		padding: 5px;
		background-color: #fff;

    .mpvue-calendar {
      position: relative;
      width: 96%;
      margin-left: 2%;
    }
	}
</style>
