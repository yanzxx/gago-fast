<template>
	<div style="margin-top: 6px;">
		<div class="sticky">
      <van-tabs v-model:active="curView" type="card" @change="(e) => {
          curView = e
          loadData(true)
        }">
        <van-tab :title="item" v-for="(item, index) of segmentedList" :key="index"></van-tab>
      </van-tabs>
		</div>
		<div class="msg-list">

      <template v-for="(item, index) in msgData"
                :key="index">

        <van-cell size="large" @click="clickMsg(item, index)">
          <template #title>
            <span class="custom-title">
              <div class="header">{{item.subject}}</div>
              <div class="content">{{item.content}}</div>
              <div class="time">{{item.createTime}}</div>
              <div class="noReadTip" v-show="!item.read"></div>
            </span>
          </template>
        </van-cell>
      </template>

			<snowy-empty v-if="!msgData || msgData.length === 0" />
		</div>
	</div>
</template>

<script setup>
	import { reactive, ref, onMounted } from "vue"
	import tool from '@/plugins/tool.js'
	import XEUtils from 'xe-utils'
	import { userLoginUnreadMessagePage } from '@/api/sys/userCenterApi.js'
	import SnowyEmpty from "@/components/snowy-empty.vue"
  import router from '@/router'
	
	const curView = ref(0)
	const segmentedList = ref([])
	const messageCategoryList = tool.dictList('MESSAGE_CATEGORY')
	if(!XEUtils.isEmpty(messageCategoryList)){
		messageCategoryList.forEach(item => {
			segmentedList.value.push(item.text)
		})
	}
	const searchFormState = reactive({})
	let parameter = reactive({
		current: 1,
		size: 10
	})
	const msgData = ref([])

  onMounted(() => {
    loadData()
  })

	// 加载数据
	const loadData = (isReset) => {
		if (isReset) {
			parameter.current = 1
			msgData.value = []
		}
		searchFormState.category = XEUtils.isEmpty(messageCategoryList) ? '' : messageCategoryList[curView.value]?.value
    console.log('searchFormState', searchFormState)
    parameter = Object.assign(parameter, searchFormState)
		userLoginUnreadMessagePage(parameter).then(res => {
			if (XEUtils.isEmpty(res?.data?.records)) {
				return
			}
			msgData.value = msgData.value.concat(res.data.records)
			parameter.current++
		}).finally(()=>{
			// uni.stopPullDownRefresh()
		})
	}
	
	const clickMsg = (item) => {
    router.push({
      path: '/editMessage',
      query: {
        id: item.id,
        createTime: item.createTime
      }
    })
	}
</script>

<style lang="scss">
	.msg-list {
		margin: 6px;
		border-radius: 10px;

    .van-cell {
      .van-cell__title {
        padding-bottom: 40px;

        .custom-title {
          .header {
            color: #3a3a3a;
            margin: 5px 0;
            font-size: 15px;
            font-weight: 700;
            text-align: left;
          }

          .content {
            display: flex;
            position: relative;
            justify-content: space-between;
            align-items: center;
            background-color: #fff;
            flex-direction: row;
            cursor: pointer;
            color: #909399;
            font-size: 13px;
          }

          .time {
            margin-top: 5px;
            color: #909399;
            font-size: 12px;
            position: absolute;
            right: 14px;
          }

          .noReadTip {
            position: absolute;
            top: 10px;
            right: 10px;
            width: 10px;
            height: 10px;
            border-radius: 10px;
            background: #e43d33;
          }
        }
      }
    }
	}
</style>