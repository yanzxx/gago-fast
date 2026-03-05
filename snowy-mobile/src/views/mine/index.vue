<template>
	<div class="mine-container" :style="{height: `${windowHeight}px`}">
		<!-- 顶部个人信息 -->
		<div class="header-section">
			<div class="avatar-section">
				<div class="icon" v-if="!avatar">
          <i class="user-outlined" type="user-outlined" style="color: #808080;font-size: 30px"></i>
				</div>
				<img class="avatar" v-if="avatar" @click="handleToAvatar" :src="avatar" />
				<div v-if="!name" @click="handleToLogin" class="login-tip">
					点击登录
				</div>
				<div class="user-info" @click="handleToInfo" v-if="name">
					<div class="title">
						{{name}}
					</div>
				</div>
			</div>
			<div class="avatar-title" @click="handleToInfo">
				<label>个人信息</label>
        <van-icon name="arrow" size="20" color="#ffffff" />
			</div>
		</div>

		<div class="content-section">
      <van-row>
        <van-col span="6">
          <div class="grid-item-box" @click="handleToHomeConfig">
            <snowy-icon backgroundColor = "#2979ff" type="icon-greenhouse" size="20" color="#FFFFFF">
            </snowy-icon>
            <label class="text">首页设置</label>
          </div>
        </van-col>
        <van-col span="6">
          <div class="grid-item-box" @click="handleBuilding">
            <snowy-icon backgroundColor = "#fa3534" type="icon-agricultural-facilities" size="20" color="#FFFFFF">
            </snowy-icon>
            <label class="text">建设中</label>
          </div>
        </van-col>
        <van-col span="6">
          <div class="grid-item-box" @click="handleBuilding">
            <snowy-icon backgroundColor = "#ff9900" type="icon-agricultural-facilities" size="20" color="#FFFFFF">
            </snowy-icon>
            <label class="text">建设中</label>
          </div>
        </van-col>
        <van-col span="6">
          <div class="grid-item-box" @click="handleBuilding">
            <snowy-icon backgroundColor = "#19be6b" type="icon-agricultural-facilities" size="20" color="#FFFFFF">
            </snowy-icon>
            <label class="text">建设中</label>
          </div>
        </van-col>
      </van-row>
		</div>

		<div class="content-list">
      <van-cell value="" is-link  @click="handleToEditInfo">
        <!-- 使用 title 插槽来自定义标题 -->
        <template #title>
          <van-icon name="contact" color="rgb(41, 121, 255)" size="19" />
          <span class="custom-title">编辑资料</span>
        </template>
      </van-cell>

      <van-cell value="" is-link @click="handleToPwd">
        <!-- 使用 title 插槽来自定义标题 -->
        <template #title>
          <van-icon name="eye-o" color="rgb(41, 121, 255)" size="19" />
          <span class="custom-title">修改密码</span>
        </template>
      </van-cell>

      <van-cell value="" is-link @click="handleToMessageManage">
        <template #title>
          <van-icon name="chat-o" color="rgb(41, 121, 255)" size="19" />
          <span class="custom-title">消息通知</span>
        </template>
      </van-cell>

      <van-cell value="" is-link @click="handleToMessageCenter">
        <template #title>
          <van-icon name="envelop-o" color="rgb(41, 121, 255)" size="19" />
          <span class="custom-title">消息管理</span>
        </template>
      </van-cell>
		</div>
    <van-button type="primary" @click="handleLogout" v-if="name" style="width: 100%;margin-top: 10px;font-size: 18px;">退出登录</van-button>
	</div>
</template>

<script setup>
	import SnowyIcon from '@/components/snowy-icon.vue'
	import store from '@/store/index.js'
	import {
		computed
	} from "vue"
	import modal from '@/plugins/modal'
  import router from '@/router'
  import { showSuccessToast } from 'vant';


	const name = store.getters.userInfo.name

	const avatar = computed(() => {
		return store.getters.userInfo.avatar
	})
	const windowHeight = computed(() => {
		return document.body.clientHeight - 50
	})
	const handleToInfo = () => {
    router.push({
      path: '/mineInfo'
    })
	}
	const handleToEditInfo = () => {
    router.push({
      path: '/mineEditInfo'
    })
	}
	const handleToPwd = () => {
    router.push({
      path: '/editPwd'
    })
	}
  const handleToMessageManage = () => {
    router.push({
      path: '/message'
    })
  }
  const handleToMessageCenter = () => {
    router.push({
      path: '/messageManage'
    })
  }
	const handleToAvatar = () => {
    showSuccessToast('模块建设中');
	}
	const handleToLogin = () => {
    router.push({
      path: '/login'
    })
	}
	const handleToHomeConfig = () =>{
    router.push({
      path: '/mineHomeConfig'
    })
	}
	const handleBuilding = () => {
    showSuccessToast('模块建设中');
	}
	const handleLogout = () => {
		modal.confirm('确定注销并退出系统吗？').then(() => {
			// 退出
			store.dispatch('LogOut').then(() => {
				// 跳转登录页面
        router.push({
          path: '/login'
        })
			})
		})
	}
</script>

<style lang="scss">
	@mixin circular {
		width: 60px;
		height: 60px;
		border-radius: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		background-color: #ffffff;
	}

	.mine-container {
		height: 100%;
    margin: 0 6px;
    margin-top: 6px;
    box-sizing: border-box;

		.header-section {
			border-radius: 5px;
			padding: 30px 15px;
			background-color: #2979ff;
			color: white;
			display: flex;

			justify-content: space-between;

			.avatar-section {
				display: flex;
				align-items: center;

				.icon {
					@include circular;
				}

				.avatar {
					@include circular;
					border: 2px solid #eaeaea;
				}

				.user-info {
					font-size: 18px;
					margin-left: 10px;

					.title {
						font-size: 20px;
						line-height: 20px;
            font-weight: 500;
					}
				}
			}

			.avatar-title {
				display: flex;
				align-items: center;

        label {
          font-size: 14px;
          font-weight: 500;
        }
			}
		}

		.content-section {
			background-color: #ffffff;
			position: relative;
			margin-top: 6px;
			top: 0;

			.grid-item-box {
				flex: 1;
				/* #ifndef APP-NVUE */
				display: flex;
				/* #endif */
				flex-direction: column;
				align-items: center;
				justify-content: center;
				padding: 15px 0;

				.text {
					text-align: center;
					font-size: 13px;
					margin-top: 7px;
          color: #000;
          font-weight: 500;
				}
			}
		}
		.content-list {
			margin-top: 6px;

      .van-cell__title {
        text-align: left;

        i {
          vertical-align: text-top;
        }

        .custom-title {
          margin-left: 5px;
        }
      }
		}
	}
</style>
