<template>
	<div class="msg-detail-container">
		<div class="container">
			<div class="item-row">
        <van-row>
          <van-col span="8">
            <label class="item-row-title">主题</label>
          </van-col>
          <van-col span="16">
            <label class="item-row-content">
              {{ record?.subject }}
            </label>
          </van-col>
        </van-row>
			</div>
			<div class="item-row">
        <van-row>
          <van-col span="8">
            <label class="item-row-title">发送时间</label>
          </van-col>
          <van-col span="16">
            <label class="item-row-content">
              {{ record?.createTime }}
            </label>
          </van-col>
        </van-row>
			</div>
			<div class="item-row">
        <van-row>
          <van-col span="8">
            <label class="item-row-title">内容</label>
          </van-col>
          <van-col span="16">
            <label class="item-row-content">
              {{ record?.content }}
            </label>
          </van-col>
        </van-row>
			</div>
		</div>
		
		<div class="container">

      <van-row>
        <van-col span="24">
          <label class="item-row-title">查收情况</label>
        </van-col>
      </van-row>
			<view class="table-list">
        <el-table :data="receiveInfoList" style="width: 100%">
          <el-table-column prop="receiveUserName" label="姓名" width="180" />
          <el-table-column prop="name" label="是否已读">
            <template #default="scope">
              <div style="display: flex; align-items: center">
                <van-tag v-if="!!scope.row.read" type="success">已读</van-tag>
                <van-tag v-if="!scope.row.read" type="primary">未读</van-tag>
              </div>
            </template>
          </el-table-column>
        </el-table>
			</view>
		</div>
	</div>
</template>

<script setup>
	import { ref, onMounted } from "vue"
	import { userLoginUnreadMessageDetail } from '@/api/sys/userCenterApi.js'
  import router from '@/router'

	const record = ref({})
	const receiveInfoList = ref([])

  onMounted(() => {
    const option = router.currentRoute.value.query
    if(!option.id){
      return
    }
    userLoginUnreadMessageDetail({
      id: option.id
    }).then((res) => {
      record.value = res.data
      record.value.createTime = option?.createTime
      receiveInfoList.value = res.data.receiveInfoList
    })
  })

</script>

<style lang="scss" scoped>

.msg-detail-container {
  .container {
    margin: 6px;
    border-radius: 5px;
    padding: 15px;
    background-color: #fff;

    .van-col {
      text-align: left;

      .item-row-title {
        font-size: 14px;
        color: #999;
        font-weight: 500;
      }

      .item-row-content {
        font-size: 13px;
        color: #999;
        font-weight: 500;
      }
    }
  }
}
</style>