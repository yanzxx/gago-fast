<template>
	<div style="margin-top: 6px;">
    <div class="action-area">
      <div class="type-filter">
        <van-button
          v-for="item in noticeTypeOptions"
          :key="item.value"
          size="small"
          :type="noticeType === item.value ? 'success' : 'default'"
          @click="noticeType = item.value"
        >
          {{ item.label }}
        </van-button>
      </div>

      <div class="read-filter">
        <van-button
          v-for="item in readFilterOptions"
          :key="item.value"
          size="small"
          :type="readStatus === item.value ? 'success' : 'default'"
          @click="readStatus = item.value"
        >
          {{ item.label }}
        </van-button>
      </div>

      <div class="op-row">
        <van-button size="small" plain type="success" @click="handleExport">导出</van-button>
      </div>
    </div>

		<div class="msg-list">
      <template v-for="(item, index) in filteredMsgData" :key="index">
        <van-swipe-cell>
          <van-cell size="large" @click="clickMsg(item)">
            <template #title>
              <div class="cell-wrap">
                <span class="custom-title">
                  <div class="header">
                    <span>{{ item.subject }}</span>
                    <van-tag :type="getNoticeTypeMeta(item).tagType" plain>
                      {{ getNoticeTypeMeta(item).label }}
                    </van-tag>
                  </div>
                  <div class="content">{{ item.content }}</div>
                  <div class="time">{{ item.createTime }}</div>
                  <div class="noReadTip" v-show="!item.read"></div>
                </span>
              </div>
            </template>
          </van-cell>
          <template #right>
            <van-button
              square
              type="danger"
              text="删除"
              class="delete-button"
              @click.stop="handleSwipeDelete(item)"
            />
          </template>
        </van-swipe-cell>
      </template>

			<snowy-empty v-if="filteredMsgData.length === 0" />
		</div>
	</div>
</template>

<script setup>
	import { reactive, ref, onMounted, computed } from "vue"
	import XEUtils from 'xe-utils'
	import { userDeleteMessage, userLoginUnreadMessagePage } from '@/api/sys/userCenterApi.js'
	import SnowyEmpty from "@/components/snowy-empty.vue"
  import router from '@/router'
  import modal from '@/plugins/modal'
  import { showFailToast, showSuccessToast } from 'vant'
	
  const noticeType = ref('ALL')
  const noticeTypeOptions = [
    { label: '全部类型', value: 'ALL' },
    { label: '审核通知', value: 'AUDIT' },
    { label: '政策通知', value: 'POLICY' },
    { label: '预警通知', value: 'WARNING' }
  ]

  const readStatus = ref('ALL')
  const readFilterOptions = [
    { label: '全部', value: 'ALL' },
    { label: '未读', value: 'UNREAD' },
    { label: '已读', value: 'READ' }
  ]

	const searchFormState = reactive({})
	const parameter = reactive({
		current: 1,
		size: 50
	})
	const msgData = ref([])

  const filteredMsgData = computed(() => {
    let list = msgData.value
    if (noticeType.value !== 'ALL') {
      list = list.filter(item => getNoticeTypeMeta(item).value === noticeType.value)
    }
    if (readStatus.value === 'UNREAD') {
      return list.filter(item => !item.read)
    }
    if (readStatus.value === 'READ') {
      return list.filter(item => !!item.read)
    }
    return list
  })

  onMounted(() => {
    loadData()
  })

	const loadData = () => {
    Object.assign(parameter, searchFormState)
		userLoginUnreadMessagePage(parameter).then(res => {
      msgData.value = XEUtils.isEmpty(res?.data?.records) ? [] : res.data.records
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

  const handleSwipeDelete = (item) => {
    modal.confirm('确认删除该消息吗？删除后不可恢复。').then(() => {
      userDeleteMessage([{ id: item.id }]).then(() => {
        msgData.value = msgData.value.filter(v => v.id !== item.id)
        showSuccessToast('删除成功')
      }).catch(() => {
        showFailToast('删除失败，请确认当前账号是否有删除权限')
      })
    })
  }

  const handleExport = () => {
    if (filteredMsgData.value.length === 0) {
      showFailToast('当前没有可导出的消息')
      return
    }
    const csvHeader = ['主题', '内容', '消息类型', '分类', '状态', '时间']
    const csvBody = filteredMsgData.value.map(item => ([
      item.subject || '',
      item.content || '',
      getNoticeTypeMeta(item).label,
      item.category || '',
      item.read ? '已读' : '未读',
      item.createTime || ''
    ].map(escapeCsvField).join(',')))
    const csvContent = `\uFEFF${csvHeader.join(',')}\n${csvBody.join('\n')}`
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const filename = `消息管理_${formatDateForFileName(new Date())}.csv`
    if (typeof window !== 'undefined' && window.URL) {
      const link = document.createElement('a')
      link.href = window.URL.createObjectURL(blob)
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(link.href)
      showSuccessToast('导出成功')
    }
  }

  const escapeCsvField = (value) => {
    const text = String(value).replace(/"/g, '""')
    return `"${text}"`
  }

  const formatDateForFileName = (date) => {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    const h = String(date.getHours()).padStart(2, '0')
    const mm = String(date.getMinutes()).padStart(2, '0')
    const s = String(date.getSeconds()).padStart(2, '0')
    return `${y}${m}${d}_${h}${mm}${s}`
  }

  const getNoticeTypeMeta = (item) => {
    const rawCategory = String(item?.category || '').toUpperCase()
    const text = `${item?.subject || ''} ${item?.content || ''} ${item?.category || ''}`
    if (rawCategory.includes('AUDIT') || rawCategory.includes('REVIEW') || /审核|审批|待办/.test(text)) {
      return { value: 'AUDIT', label: '审核通知', tagType: 'primary' }
    }
    if (rawCategory.includes('POLICY') || /政策|制度|公告|通知/.test(text)) {
      return { value: 'POLICY', label: '政策通知', tagType: 'success' }
    }
    if (rawCategory.includes('WARNING') || rawCategory.includes('ALERT') || /预警|异常|风险|告警/.test(text)) {
      return { value: 'WARNING', label: '预警通知', tagType: 'danger' }
    }
    return { value: 'POLICY', label: '政策通知', tagType: 'success' }
  }
</script>

<style lang="scss">
  .action-area {
    margin: 6px;
    background: #fff;
    border-radius: 10px;
    padding: 10px 12px;

    .type-filter {
      display: flex;
      gap: 8px;
      margin-bottom: 10px;
      flex-wrap: wrap;
    }

    .read-filter {
      display: flex;
      gap: 8px;
      margin-bottom: 10px;
      flex-wrap: wrap;
    }

    .op-row {
      display: flex;
      gap: 8px;
      justify-content: flex-end;
    }
  }

	.msg-list {
		margin: 6px;
		border-radius: 10px;

    .delete-button {
      height: 100%;
      min-height: 86px;
    }

    .van-cell {
      .van-cell__title {
        padding-bottom: 40px;

        .cell-wrap {
          display: flex;
          align-items: flex-start;
        }

        .custom-title {
          flex: 1;

          .header {
            color: #3a3a3a;
            margin: 5px 0;
            font-size: 15px;
            font-weight: 700;
            text-align: left;
            display: flex;
            justify-content: space-between;
            align-items: center;
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
