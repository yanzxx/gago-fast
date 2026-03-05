<template>
	<div class="snowy-user-picker">

    <van-popup
        v-model:show="showBottom"
        position="bottom"
        ref="popupRef"
        title="''"
    >
      <div class="action">
        <div class="cal" @click="cancel">
          取消
        </div>
        <div class="conf" @click="confirm">
          确定
        </div>
      </div>

      <van-search
          v-model="searchFormState.searchKey"
          show-action
          placeholder="请输入搜索关键词"
          @search="loadUserData(true)"
          @cancel="onUserCancel"
      />

      <div class="crumb">
        <label v-for="(item, index) in allClickSelOrg" :key="index" @click="clickOrgCru(item, index)"
              :class="index === (allClickSelOrg.length-1) ? 'uni-secondary-color' : 'uni-primary'">
          {{ item.name + (index === (allClickSelOrg.length-1) ? '' : ' | ') }}
        </label>
      </div>

      <div class="choiced" v-show="!!curSelUserId && (!isMultiple? true : curSelUserId.length > 0)"
           :style="{maxHeight:!isMultiple?'5vh':'20vh', overflowY: 'scroll'}">
        <!-- 单选已选择 -->
        <div class="single" v-if="!isMultiple">
          <div class="name" @click="delUser(curSelUser)">
            {{ curSelUser.name }}
          </div>
          <van-icon name="delete" @click.stop="delUser(curSelUser)" color="#e43d33" size="20" />
        </div>
        <!-- 多选已选择 -->
        <template v-if="!!isMultiple">
          <div class="multiple" v-for="(item, index) in curSelUser" :key="index">
            <div class="name" @click="delUser(item)">
              {{ item.name }}
            </div>
            <van-icon name="delete" @click.stop="delUser(item)" color="#e43d33" size="20" />
          </div>
        </template>
      </div>

      <div class="org">
        <template v-for="(item, index) in curClickSelOrg" :key="index">
          <van-cell>
            <template #title>
              <span class="custom-title" @click="clickOrg(item, index)">{{ item.name }}</span>
            </template>
            <template #right-icon>
              <van-icon name="arrow" @click="clickOrg(item, index)" v-if="item.children? true : false" />
            </template>
          </van-cell>
        </template>
      </div>

      <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="loadUserData"
      >
        <div class="userBox" v-for="(item, index) in userData" :key="index" @click="clickUser(item, index)">
          <div class="userBoxImg">
            <img :src="item.avatar || defaultImg " />
          </div>
          <div class="userBoxText">
            <div class="userBoxPerson">
              <div class="userName">{{(item ? item.name : null) || '某某某'}}</div>
              <div class="userGender">{{item?.orgName + ' | '+ item?.positionName +' | '+item?.genderName}}</div>
            </div>
            <div class="userBoxIcon">
              <van-icon name="circle" :size="25"
                        v-show="!isMultiple ? item.id != curSelUserId: curSelUserId.indexOf(item.id) == -1" />
              <van-icon name="checked" :size="25" color="#2979ff"
                        v-show="!isMultiple ? item.id == curSelUserId: curSelUserId.indexOf(item.id) != -1" />
            </div>
          </div>
        </div>
      </van-list>
    </van-popup>
	</div>
</template>

<script setup>
	import { orgTreeSelector, userSelector } from '@/api/components/picker/userPickerApi.js'
	import { reactive, ref } from "vue";
	import XEUtils from 'xe-utils'
	const emits = defineEmits(['cancel', 'confirm'])
	const props = defineProps({
		modelValue: [String, Array],
		border: {
			type: Boolean,
			default: true,
			required: false
		},
		placeholder: {
			type: String,
			default: "请选择",
			required: false
		},
		// 是否多选
		isMultiple: {
			type: Boolean,
			default: false,
			required: false
		},
		// 部门树url
		orgTreeSelectorUrl: {
			type: String,
			default: null,
			required: false
		},
		// 通过请求服务端获取已选中用户名的url
		getUserListByIdListUrl: {
			type: String,
			default: null,
			required: false
		},
		// 用户分页地址
		userSelectorUrl: {
			type: String,
			default: null,
			required: false
		},
		autoInitData: {
			type: Boolean,
			default: true,
			required: false
		}
	})
	// 数据类型校验
	if (props.modelValue) {
		if (!props.isMultiple && typeof props.modelValue !== "string") {
			console.error("单选所传modelValue值应为字符串")
		}
		if (!!props.isMultiple && !Array.isArray(props.modelValue)) {
			console.error("多选所传modelValue值应为数组")
		}
	}
	// 弹出
	const popupRef = ref()

	const searchFormState = reactive({})
	const parameter = reactive({
		current: 1,
		size: 10
	})
	const userData = ref([])
  const showBottom = ref(false)

  const loading = ref(false);
  const finished = ref(false);

  const defaultImg = ref(new URL(`../assets/logo.png`, import.meta.url).href)

	// 当前选中的机构id及机构
	const curSelUserId = !props.isMultiple ? ref("") : ref([])
	const curSelUser = !props.isMultiple ? ref({}) : ref([])
	// 所有点击选中机构【页面数据】
	const allClickSelOrg = ref([])
	// 当前点击选中机构【页面数据】
	const curClickSelOrg = ref([])
	
	const loadOrgTree = (orgTreeParam) => {
		orgTreeSelector(orgTreeParam, props.orgTreeSelectorUrl).then(res => {
			curClickSelOrg.value = res.data
			allClickSelOrg.value = [{
				id: '0',
				name: '全部',
				children: res.data
			}]
		})
	}
	
	if(props.autoInitData){
		loadOrgTree()
	}

  const open = () => {
    showBottom.value = true
  }
	
	const loadUserData = (isReset, userSelectorParam) => {
		if (isReset) {
			parameter.current = 1
			userData.value = []
		}
		Object.assign(parameter, userSelectorParam)
		Object.assign(parameter, searchFormState)
		userSelector(parameter, props.userSelectorUrl).then(res => {
      loading.value = false;
			if (XEUtils.isEmpty(res?.data)) {
        finished.value = true;
			}
      if (res?.data.length < 10) {
        finished.value = true;
      }
			userData.value = userData.value.concat(res.data)
			parameter.current++
		})
	}

  const onUserCancel = () => {
    searchFormState.searchKey = ''
  }

	if(props.autoInitData){
		loadUserData(true)
	}
	
	// 点击输入框

	// 点击面包屑
	const clickOrgCru = (item, index) => {
		curClickSelOrg.value = item.children
		allClickSelOrg.value.splice(index + 1, allClickSelOrg.value.length - (index + 1))

		searchFormState.orgId = (item.id === '0' ? null : item.id)
		loadUserData(true)
	}

	// 点击用户
	const clickUser = (item, index) => {
		if (!props.isMultiple) {
			if (item.id != curSelUserId.value) {
				selUser(item, index)
			} else {
				delUser(item, index)
			}
		} else {
			if (!curSelUserId.value.includes(item.id)) {
				selUser(item, index)
			} else {
				delUser(item, index)
			}
		}
	}

	// 选择用户
	const selUser = (item) => {
		if (!props.isMultiple) {
			curSelUserId.value = item.id
			curSelUser.value = item
		} else {
			curSelUserId.value.push(item.id)
			curSelUser.value.push(item)
		}
	}
	// // 移除用户
	const delUser = (item) => {
		if (!props.isMultiple) {
			curSelUserId.value = ""
			curSelUser.value = {}
		} else {
			curSelUserId.value.splice(curSelUserId.value.findIndex(curSelOrgIdItem => curSelOrgIdItem === item.id), 1);
			curSelUser.value.splice(curSelUser.value.findIndex(curSelOrgItem => curSelOrgItem.id === item.id), 1);
		}
	}
	// // 点击部门
	const clickOrg = (item) => {
		curClickSelOrg.value = item.children
		allClickSelOrg.value.push(item)

		searchFormState.orgId = (item.id === '0' ? null : item.id)
		loadUserData(true)
	}

	// 取消
	const cancel = () => {
		// 重置数据
    showBottom.value = false
	}
  // 确定
	const confirm = () => {
		// 调用父组件方法
		emits('confirm', {
			curSelUserId: curSelUserId.value,
			curSelUser: curSelUser.value
		})
    showBottom.value = false
	}
	
	defineExpose({
		loadOrgTree,
		loadUserData,
    open
	})
</script>

<style lang="scss" scoped>
	.snowy-user-picker {

    .van-popup {
      padding-top: 15px;
    }

    .custom-title {
      margin-left: 5px;
    }

    .action {
      display: flex;
      justify-content: space-between;
      padding: 0 10px;
      margin-top: 12px;

      .cal {
        color: #909399;
      }

      .conf {
        color: #2979ff;
      }
    }

    .crumb {
      padding-left: 10px;
      text-align: left;
      margin-bottom: 15px;
      font-size: 14px;
      margin-top: 10px;
    }

    .choiced {
      text-align: left;

      .single {
        display: inline-block;
        vertical-align: top;
        color: #3a3a3a;
        font-size: 14px;
        margin-left: 10px;
        display: flex;
      }
    }

    .org {
      width: 100vw;
      overflow-y: scroll;

      .name {
        flex: 1;
        font-size: 30px;
        margin: 2px 20px;
      }
    }

    .van-cell__title {
      height: 100%;
      width: fit-content !important;
    }

    .userBox {
      position: relative;
      height: 67px;
      padding: 10px 15px;
      box-sizing: border-box;
      display: flex;
      flex-direction: row;

      .userBoxImg {
        width: 45px;
        height: 45px;
        img {
          -webkit-touch-callout: none;
          -webkit-user-select: none;
          user-select: none;
          display: block;
          width: 45px;
          height: 45px;
          border: 1px solid #eee;
          padding: 1px;
          box-sizing: border-box;
        }
      }

      .userBoxText {
        display: flex;
        flex: 1;

        .userBoxPerson {
          font-size: 16px;
          color: #3b4144;
          font-weight: normal;
          margin-left: 10px;
          width: 88%;

          .userName {
            text-align: left;
          }

          .userGender {
            text-align: left;
            color: #999;
            font-size: 12px;
            font-weight: normal;
          }
        }

        .userBoxIcon {
          color: #000;
        }
      }
    }
	}
</style>