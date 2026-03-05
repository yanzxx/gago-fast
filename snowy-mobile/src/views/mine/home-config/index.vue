<template>
	<div class="home-config">
    <div class="tip">
      <AlertOutlined />
      <label>首页调整完成后,请重新登录,谢谢!</label>
    </div>

    <template v-for="(item, index) in homeConfigs" :key="index">
      <van-cell @click="moreOpt(item, index)">
        <template #title>
          <span class="custom-title">{{ item.name }}</span>
        </template>
        <template #right-icon>
          <van-switch v-model="item.isShow" @change="switchChange" size="20px" @click.stop.prevent="() => {console.log('')}" />
        </template>
      </van-cell>
    </template>

		<!-- 更多操作 @handleOk=""-->
		<morePopup ref="morePopupRef"></morePopup>
	</div>
</template>

<script setup>
	import store from '@/store'
	import { ref, computed } from "vue"
  import { AlertOutlined} from '@ant-design/icons-vue';
	import morePopup from './more-popup.vue'

	// 快捷用户菜单
	const homeConfigs = computed(() => {
		const rawConfigs = store.getters.homeConfigs || []
		return rawConfigs.filter(item => item && item.code === 'chart')
	})

	const switchChange = () => {
		store.commit("SET_homeConfigs", homeConfigs.value)
	}
	const morePopupRef = ref()
	const moreOpt = (item, index) => {
		morePopupRef.value.open(homeConfigs.value, index)
	}
</script>

<style lang="scss">
	.home-config {
		margin: 6px;

    .tip {
      background: rgb(255, 249, 234);
      display: flex;
      width: 100%;
      box-sizing: border-box;
      flex-direction: row;
      align-items: center;
      padding: 10px 12px;
      margin-bottom: 10px;

      span {
        color: rgb(255, 154, 67);
      }

      label {
        color: rgb(255, 154, 67);
        font-size: 14px;
        line-height: 21px;
        font-weight: 500;
        margin-left: 3px;
      }
    }

    .van-cell {
      .van-cell__title {
        text-align: left;

        .custom-title {
          color: rgb(41, 121, 255);
        }
      }
    }
	}
</style>
