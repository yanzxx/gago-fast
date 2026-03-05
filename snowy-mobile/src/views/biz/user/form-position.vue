<template>
	<div v-for="(item, index) in dataList" :key="index" >
    <van-button plain type="danger" @click="del(index)" style="width: 100%;">删除任职</van-button>
    <van-form label-align="top" ref="formRef">

      <van-field
          v-model="item.orgName"
          is-link
          readonly
          name="orgName"
          label="机构"
          placeholder="请选择机构"
          required
          :rules="[{ required: true, message: '请选择机构' }]"
          @click="selectPreOrg(index)"
      />

      <snowy-org-picker :ref="'snowyOrgPickerRef_' + index" v-model="item.orgName" @confirm="(data) => {confirmOrg(data, item)}" placeholder="请选择机构">
      </snowy-org-picker>

      <van-field
          v-model="item.positionName"
          is-link
          readonly
          name="positionName"
          label="职位"
          placeholder="请选择职位"
          required
          :rules="[{ required: true, message: '请选择职位' }]"
          @click="selectPreSel(index)"
      />

      <snowy-sel-picker :ref="'snowySelPickerRef_' + index" :map="{key: 'id', label: 'name'}" v-model="item.positionName"
                        :rangeData="positionDataList[index]" @confirm="(data) => {confirmSel(data, item)}" placeholder="请选择职位"></snowy-sel-picker>

      <van-field
          v-model="item.directorName"
          is-link
          readonly
          name="directorName"
          label="指定主管"
          placeholder="请选择主管"
          @click="selectPreUser(index)"
      />

      <snowy-user-picker :ref="'snowyUserPickerRef_' + index" v-model="item.directorName" @confirm="(data) => {confirmUser(data, item)}" placeholder="请选择主管">
      </snowy-user-picker>
    </van-form>
	</div>
  <van-button plain type="primary" @click="add" style="width: 100%;margin-top: 15px;">增加任职</van-button>
</template>

<script setup>
	import SnowyOrgPicker from '@/components/snowy-org-picker.vue'
	import SnowyUserPicker from '@/components/snowy-user-picker.vue'
	import SnowySelPicker from '@/components/snowy-sel-picker.vue'
	import {
		userPositionSelector
	} from '@/api/biz/bizUserApi'
	import XEUtils from 'xe-utils'
	import {
		nextTick,
		ref,
		watch,
		getCurrentInstance
	} from "vue"
	const {
		proxy
	} = getCurrentInstance()
	const emits = defineEmits(['update:modelValue'])
	const props = defineProps({
		modelValue: {
			type: String,
			default: '',
			required: false
		},
	})

	// 数据列表
	const dataList = ref([])
	// 职位参数
	const positionParamList = ref([])
	// 职位下拉列表
	const positionDataList = ref([])

	watch(() => props.modelValue, () => {
		initData()
	}, {
		deep: false,
		immediate: false
	})
	const initData = ()=>{
		if(props.modelValue){
			dataList.value = XEUtils.clone(JSON.parse(props.modelValue), true)
		}else{
			dataList.value = []
		}
		dataList.value.forEach((item, index) => {
			if(positionParamList.value[index] === undefined || positionParamList.value[index] === null){
				positionParamList.value[index] = {}
			}
			positionParamList.value[index].orgId = item.orgId
			loadPositionSelector(true, index)
			
			nextTick(() => {
				proxy.$refs[`positionRef${ index }`][0].initData()
				proxy.$refs[`directorRef${ index }`][0].initData()
				proxy.$refs[`directorRef${ index }`][0].loadUserData(true, {orgId: item.orgId})
			})
		})
	}

  const selectPreOrg = (index) => {
    ['snowyOrgPickerRef_' + index].value.open()
  }

  const selectPreSel = (index) => {
    ['snowySelPickerRef_' + index].value.open()
  }

  const selectPreUser = (index) => {
    ['snowyUserPickerRef_' + index].value.open()
  }

  const confirmOrg = (data, item) => {
    item.parentId = data.curSelOrgId
    item.parentName = data.curSelOrg.name
  }

  const confirmSel = (data, item) => {
    item.category = data.curSelDataKey
    item.categoryName = data.curSelData.text
  }

  const confirmUser = (data, item) => {
    item.directorId = data.curSelUserId
    item.directorName = data.curSelUser.name
  }
	
	// 组织变换
	// const orgChange = ({
	// 	curSelOrgId,
	// 	curSelOrg
	// }, index) => {
	// 	// 重置职位数据
	// 	dataList.value[index].positionId = null
	// 	positionParamList.value[index].orgId = curSelOrgId
	// 	loadPositionSelector(true, index)
	// 	// 重置用户数据
	// 	dataList.value[index].directorId = null
	// 	proxy.$refs[`directorRef${ index }`][0].loadUserData(true, {orgId: curSelOrgId})
	// }
	// // 根据职位id进行查询
	// const positionQueryCurSelData = (curSelDataKey, callback) => {
	// 	if(!XEUtils.isEmpty(curSelDataKey)){
	// 		getPositionListByIdList({
	// 			idList: [curSelDataKey]
	// 		}).then(res => {
	// 			callback(res.data[0])
	// 		})
	// 	}
	// }
	// 职位分页加载
	const loadPositionSelector = (isReset, index) => {
		if (isReset) {
			positionParamList.value[index].current = 1
			positionParamList.value[index].size = 10
			positionDataList.value[index] = []
		}
		userPositionSelector(positionParamList.value[index]).then(res => {
			if (XEUtils.isEmpty(res?.data?.records)){
				return
			}
			positionDataList.value[index] =  positionDataList.value[index].concat(res.data.records)
			positionParamList.value[index].current++
		})
	}
	// 职位下拉触发
	// const positionScrollToLower = (index) => {
	// 	loadPositionSelector(false,index)
	// }
	// 新增
	const add = () => {
		dataList.value.push({
			orgId: "",
			positionId: "",
			directorId: ""
		})
		positionDataList.value.push([])
		positionParamList.value.push({})
	}
	// 删除
	const del = (index) => {
		dataList.value.splice(index, 1)
		positionDataList.value.splice(index, 1)
		positionParamList.value.splice(index, 1)
	}
	// 表单校验
	const formListEmitAndValidate = () => {
		// 更新数据
		if(dataList.value && dataList.value.length > 0){
			emits('update:modelValue', JSON.stringify(dataList.value))
		}
		// 校验逻辑
		const promiseList = []
		dataList.value.forEach((item, index) => {
			promiseList.push(new Promise((resolve, reject) => {
				proxy.$refs[`formRef${ index }`][0].validate().then(result => {
					resolve(result)
				}).catch(err => {
					reject(err)
				})
			}))
		})
		return new Promise((resolve, reject) => {
			Promise.all(promiseList).then((result) => {
				resolve(result)
			}).catch(err=>{
				reject(err)
			})
		})
	}
	defineExpose({
		formListEmitAndValidate
	})
</script>
