<template>
  <div class="user-container">
    <!-- 搜索 -->
    <van-search
        v-model="searchFormState.searchKey"
        show-action
        placeholder="请输入搜索关键词"
        @search="loadData(true)"
        @cancel="onCancel"
    />
    <!-- 自定义面包屑 -->
    <div class="crumb">
      <label class="crumb-text" v-for="(item, index) in allSelOrg" :key="index"
             :class="index === (allSelOrg.length-1) ? 'uni-secondary-color' : 'uni-primary'"
             @click="clickOrgCru(item, index)">
        {{ item.name + (index === (allSelOrg.length-1) ? '' : ' | ') }}
      </label>
    </div>
    <div class="org-list">
      <template v-for="(item, index) in curSelOrg" :key="index">
        <van-cell :title="item.name" @click="clickOrg(item)">
          <template #right-icon>
            <van-icon v-if="item.children? true : false" name="arrow" @click.stop="clickOrg(item, index)" />
          </template>
        </van-cell>
      </template>
      <template v-for="(item, index) in userData" :key="index">
        <van-cell :title="item.name" @click="moreClick(item)">
          <template #title>
            <div style="width: 45px; height: 45px;">
              <img :src="item.avatar" />
            </div>
            <div class="biz-list-body">
              <label class="biz-list-body-name">{{item.name}}</label>
              <label class="biz-list-body-category">
                {{item.orgName + ' | '+ item.positionName +' | '+item.genderName}}
              </label>
            </div>
            <div class="biz-list-time">
              {{item.entryDate}}
            </div>
          </template>
        </van-cell>
      </template>
      <snowy-empty v-if="userData && userData.length === 0" />
    </div>
    <!-- 新增悬浮按钮 -->
    <van-floating-bubble v-if="hasPerm('mobileBizUserAdd')"  v-model:offset="offset" axis="xy" icon="plus" @click="add" />
    <!-- 更多操作 -->
    <morePopup ref="morePopupRef" @handleOk="loadData(true)"></morePopup>
  </div>
</template>

<script setup>
	import morePopup from './more-popup.vue'
	import { orgTree } from '@/api/biz/bizOrgApi'
	import { userPage } from '@/api/biz/bizUserApi'
	import { reactive, ref } from "vue"
	import XEUtils from 'xe-utils'
	import SnowyEmpty from "@/components/snowy-empty.vue"
  import router from '@/router'
  import { hasPerm } from '@/plugins/permission'

	// 新增悬浮按钮
	const add = () => {
    router.push({
      path: '/userForm'
    })
	}
	// 更多操作
	const morePopupRef = ref()
  const offset = ref({ x: document.body.clientWidth - 72,y: document.body.clientHeight - 150 })
	const moreClick = (record) => {
		morePopupRef.value.open(record)
	}

	// 所有选择的机构
	let allSelOrg = ref([])
	// 当前选择的机构
	let curSelOrg = ref([])
	orgTree().then(res => {
		curSelOrg.value = res.data
		allSelOrg.value.push({
			id: '0',
			name: '顶级',
			children: res.data
		})
	})

	// 用户相关逻辑
	let searchFormState = reactive({})
	let parameter = reactive({
		current: 1,
		size: 10
	})
	let userData = ref([])

	// 加载数据
	const loadData = (isReset) => {
		if (isReset) {
			parameter.current = 1
			userData.value = []
		}
		Object.assign(parameter, searchFormState)
		userPage(parameter).then(res => {
			if (XEUtils.isEmpty(res?.data?.records)){
				return
			}
			userData.value = userData.value.concat(res.data.records)
			parameter.current++
		}).finally(()=>{
			// uni.stopPullDownRefresh()
		})
	}
	loadData(true)

	// 点击机构面包屑
	const clickOrgCru = (item, index) => {
		curSelOrg.value = item.children
		allSelOrg.value.splice(index + 1, allSelOrg.value.length - (index + 1))
		searchFormState.orgId = (item.id === '0' ? '' : item.id)
		loadData(true)
	}
	// 点击机构
	const clickOrg = (item) => {
		curSelOrg.value = item.children
		allSelOrg.value.push(item)
		searchFormState.orgId = item.id
		loadData(true)
	}


  const onCancel = () => {
    searchFormState.searchKey = ''
  }
</script>
<style lang="scss">

.user-container {

  .van-search {
    background: transparent;
  }

  .crumb {
    border-radius: 5px;
    white-space: nowrap;
    overflow-x: scroll;
    background-color: white;
    padding: 6px;
    text-align: left;

    .crumb-text {
      margin-left: 5px;
      color: #909399;
    }
  }

  .org-list {
    margin-top: 6px;
    border-radius: 5px;
    margin-bottom: 55px;

    .org-list-name {
      flex: 1;
    }

    .van-cell {
      border-radius: 5px;

      .van-badge__wrapper {
        top: 5px;
      }

      .van-cell__title {
        position: relative;
        display: flex;

        .biz-list-body-name {
          text-align: left;
        }

        img {
          width: 45px;
          height: 45px;
        }
      }
    }

    .biz-list-body {
      display: flex;
      flex-direction: column;
      margin-left: 10px;

      .biz-list-body-category {
        font-size: 12px;
        margin: 2px 0;
        color: #999;
      }
    }

    .biz-list-time {
      position: absolute;
      top: 0;
      right: 0;
      color: #999;
      font-size: 12px;
      font-weight: normal;
    }
  }

  .uni-secondary-color {
    color: #909399 !important;
  }

  .uni-primary {
    color: #2979ff !important;
  }

}
</style>
