<template>
	<div class="info-container">
    <van-cell>
      <template #title>
        <van-icon name="manager" />
        <span class="custom-title">账户</span>
      </template>
      <template #right-icon>
        <label>{{userInfo.account}}</label>
      </template>
    </van-cell>
    <van-cell>
      <template #title>
        <i class="jiage icon-user1" style="font-size: 12px;"></i>
        <span class="custom-title">姓名</span>
      </template>
      <template #right-icon>
        <label>{{userInfo.name}}</label>
      </template>
    </van-cell>
    <van-cell>
      <template #title>
        <MobileFilled />
        <span class="custom-title">手机</span>
      </template>
      <template #right-icon>
        <label>{{userInfo.phone}}</label>
      </template>
    </van-cell>
    <van-cell>
      <template #title>
        <CrownFilled />
        <span class="custom-title">昵称</span>
      </template>
      <template #right-icon>
        <label>{{userInfo.nickname}}</label>
      </template>
    </van-cell>
    <van-cell>
      <template #title>
        <van-icon name="friends" />
        <span class="custom-title">性别</span>
      </template>
      <template #right-icon>
        <label>{{userInfo.gender}}</label>
      </template>
    </van-cell>
    <van-cell>
      <template #title>
        <van-icon name="gift" />
        <span class="custom-title">生日</span>
      </template>
      <template #right-icon>
        <label>{{userInfo.birthday}}</label>
      </template>
    </van-cell>
    <van-cell>
      <template #title>
        <ScheduleFilled />
        <span class="custom-title">邮箱</span>
      </template>
      <template #right-icon>
        <label>{{userInfo.email}}</label>
      </template>
    </van-cell>
	</div>
</template>

<script setup>
	import { computed } from "vue"
	import store from '@/store/index.js'
  import { MobileFilled, CrownFilled, ScheduleFilled } from '@ant-design/icons-vue';
	
	const userInfo = computed(() => {
		return store.getters.userInfo
	})
</script>

<style lang="scss">
	.info-container {
		background-color: #ffffff;
		border-radius: 5px;
    margin: 6px;

    .van-cell {

      label {
        color: #999;
        font-size: 12px;
      }

      .van-cell__title {
        text-align: left;

        .custom-title {
          margin-left: 10px;
        }
      }
    }
	}
</style>
