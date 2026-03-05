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
	</div>
</template>

<script setup>
	import { ref, onMounted } from "vue"
	import { userLoginUnreadMessageDetail } from '@/api/sys/userCenterApi.js'
  import router from '@/router'

	const record = ref({})

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
