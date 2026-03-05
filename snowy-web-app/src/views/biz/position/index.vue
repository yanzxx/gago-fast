<template>
	<div class="position-container">
    <div class="crumb">
      <label class="crumb-text" v-for="(item, index) in allSelOrg" :key="index"
             :class="index === (allSelOrg.length-1) ? 'uni-secondary-color' : 'uni-primary'"
             @click="clickOrgCru(item, index)">
        {{ item.name + (index === (allSelOrg.length-1) ? '' : ' | ') }}
      </label>
    </div>
    <div class="org-list">
      <template v-for="(item, index) in curSelOrg" :key="index">
        <van-cell :title="item.name">
          <template #right-icon>
            <van-icon v-if="item.children? true : false" name="arrow" @click.stop="clickOrg(item, index)" />
          </template>
        </van-cell>
      </template>
      <template v-for="(item, index) in positionData" :key="index">
        <van-cell :title="item.name" @click="morePopupRef.open(item)">
          <template #title>
            <div>
              <div v-if="item.category == 'HIGH'" style="width: 42px; height: 42px;">
                <snowy-icon backgroundColor="#f3a73f" type="icon-universal-user-c" size="20" color="#FFFFFF"></snowy-icon>
              </div>
              <div v-else-if="item.category == 'MIDDLE'" style="width: 42px; height: 42px;">
                <snowy-icon backgroundColor="#2979ff" type="icon-universal-client" size="20" color="#FFFFFF"></snowy-icon>
              </div>
              <div v-else style="width: 42px; height: 42px;">
                <snowy-icon backgroundColor="#18bc37" type="icon-user" size="20" color="#FFFFFF"></snowy-icon>
              </div>
            </div>
            <div class="biz-list-body">
              <label class="biz-list-body-name">{{item.name}}</label>
              <label class="biz-list-body-category">{{ tool.dictTypeData('POSITION_CATEGORY', item.category)}}</label>
            </div>
          </template>
        </van-cell>
      </template>
    </div>
    <snowy-empty v-if="positionData && positionData.length === 0" />
    <!-- 新增悬浮按钮 -->
    <van-floating-bubble v-if="hasPerm('mobileBizPositionAdd')"  v-model:offset="offset" axis="xy" icon="plus" @click="add" />
    <!-- 更多操作 -->
    <morePopup ref="morePopupRef" @handleOk="loadData(true)"></morePopup>
  </div>
</template>

<script setup>
	import {
		orgTree
	} from '@/api/biz/bizOrgApi';
	import {
		positionPage
	} from '@/api/biz/bizPositionApi';
	import {
		reactive,
		ref,
	} from "vue";
	import morePopup from './more-popup.vue';
	import SnowyIcon from '@/components/snowy-icon.vue';
	import XEUtils from 'xe-utils';
	import SnowyEmpty from "@/components/snowy-empty.vue";
  import { hasPerm } from '@/plugins/permission';
  import router from '@/router';
  import tool from '@/plugins/tool'
	
	const morePopupRef = ref()
	// 所有选择的机构
	const allSelOrg = ref([])
	// 当前选择的机构
	const curSelOrg = ref([])
  const offset = ref({ x: document.body.clientWidth - 72,y: document.body.clientHeight - 150 })
	
	orgTree().then(res => {
		curSelOrg.value = res?.data || []
		allSelOrg.value.push({
			id: '0',
			name: '全部',
			children: res?.data || []
		})
	})
	
	const searchFormState = reactive({})
	const parameter = reactive({
		current: 1,
		size: 10
	})
	const positionData = ref([])
	// 加载数据
	const loadData = (isReset) => {
		if (isReset) {
			parameter.current = 1
			positionData.value = []
		}
		Object.assign(parameter, searchFormState)
		positionPage(parameter).then(res => {
			if (XEUtils.isEmpty(res?.data?.records)){
				return
			}
			positionData.value = positionData.value.concat(res.data.records)
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

	// 新增悬浮按钮
	const add = () => {
    router.push({
      path: '/positionForm'
    })
	}
</script>

<style lang="scss">
	.position-container {
    margin: 6px;

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
          display: flex;

          .biz-list-body-category {
            text-align: left;
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
    }
  }

  .uni-secondary-color {
    color: #909399 !important;
  }

  .uni-primary {
    color: #2979ff !important;
  }
</style>
