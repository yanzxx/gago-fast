<template>
	<div class="pwd-container">

    <van-form label-align="top" ref="form">
      <van-field v-model="user.oldPassword" type="password" label="旧密码" placeholder="请输入旧密码" required
                 :rules="[{ required: true, message: '旧密码不能为空' }]"/>
      <van-field v-model="user.newPassword" type="password" label="新密码" placeholder="请输入新密码" required
                 :rules="[{ required: true, message: '新密码不能为空' }, { pattern, message: '密码不能少于6个字符' }]"/>
      <van-field v-model="user.confirmPassword" type="password" label="确认密码" placeholder="请确认新密码" required
                 :rules="[{ required: true, message: '确认密码不能为空' }, { validator, message: '两次输入的密码不一致' }]"/>

      <van-button type="primary" @click="submit" style="margin-top: 10px;width: 100%;">提交</van-button>
    </van-form>

	</div>
</template>

<script setup>
	import {
		userUpdatePassword
	} from "@/api/sys/userCenterApi.js"
	import {
		reactive,
		ref
	} from 'vue'
	import modal from "../../../plugins/modal";

  const pattern = /^\S{6,}$/
	const form = ref()
	const user = reactive({
		oldPassword: undefined,
		newPassword: undefined,
		confirmPassword: undefined
	})

  const validator = (val) => {
    return user.newPassword === val
  }
	const submit = () => {
		form.value.validate().then(() => {
			userUpdatePassword({
				password: user.oldPassword,
				newPassword: user.newPassword
			}).then(() => {
				modal.msgSuccess('修改成功')
			})
		})
	}
</script>

<style lang="scss">
	.pwd-container {
		margin: 6px;
		border-radius: 5px;
		padding: 3px;
		background-color: #fff;
	}
</style>
