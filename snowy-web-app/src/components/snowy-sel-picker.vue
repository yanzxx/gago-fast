<template>
	<div class="snowy-sel-picker">

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

      <div class="choiced" v-show="!!curSelDataKey && (!isMultiple? true : curSelDataKey.length > 0)"
           :style="{maxHeight:!isMultiple?'5vh':'20vh', overflowY: 'scroll'}">
        <!-- 单选已选择 -->
        <div class="single" v-if="!isMultiple">
          <div class="name" @click="delData(curSelData)">
            {{ curSelData[map.label] }}
          </div>
          <van-icon name="delete" @click.stop="delData(curSelData)" color="#e43d33" size="20" />
        </div>
        <!-- 多选已选择 -->
        <template v-if="!!isMultiple" >
          <div class="multiple" v-for="(item, index) in curSelData" :key="index">
            <div class="name" @click="delData(item)">
              {{ item[map.label] }}
            </div>
            <van-icon name="delete" @click.stop="delData(item)" color="#e43d33" size="20" />
          </div>
        </template>
      </div>

      <div class="org">
        <template v-for="(item, index) in props.rangeData" :key="index">
          <van-cell>
            <template #title>
              <van-icon name="circle" :size="25"
                        v-show="!isMultiple ? item[map.key] != curSelDataKey: curSelDataKey.indexOf(item[map.key]) == -1"
                        @click="selData(item, index)" />
              <van-icon name="checked" :size="25" color="#2979ff"
                        v-show="!isMultiple ? item[map.key] == curSelDataKey: curSelDataKey.indexOf(item[map.key]) != -1"
                        @click="delData(item, index)" />
              <span class="custom-title" @click="selOrDelData(item, index)">{{item[map.label]}}</span>
            </template>
          </van-cell>
        </template>
      </div>
    </van-popup>
	</div>
</template>

<script setup>
	import { ref, watch } from "vue";
	import XEUtils from 'xe-utils'

	const emits = defineEmits(['cancel', 'confirm'])

	const props = defineProps({
		// value: [String, Array],
		modelValue: [String, Array],
		border: {
			type: Boolean,
			default: true,
			required: false
		},
		isMultiple: {
			type: Boolean,
			default: false,
			required: false
		},
		placeholder: {
			type: String,
			default: "请选择",
			required: false
		},
		map: {
			type: Object,
			default: function () {
        return {
          key: "key",
          label: "label"
        }
      },
			required: false
		},
		rangeData: {
			type: Array,
			default: function () {
        return []
      },
			required: false
		},
		disabled: {
			type: Boolean,
			default: false,
			required: false
		},
		// 是否开启大数据模式，如果开启大数据模式，
		// 那么就要实现queryCurSelData方法，并提供相应的回调（通过服务端获取已选择的数据）
		// 与此同时要实现scrollToLower方法（用于分页加载）
		isBigData: {
			type: Boolean,
			default: false,
			required: false
		},
	})
	// 数据类型校验
	// if (props.value) {
	// 	if (!props.isMultiple && typeof props.value !== "string") {
	// 		console.error("单选所传value值应为字符串")
	// 	}
	// 	if (!!props.isMultiple && !Array.isArray(props.value)) {
	// 		console.error("多选所传value值应为数组")
	// 	}
	// }
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
  const showBottom = ref(false)

	// 当前选中的数据key及数据
	const curSelDataKey = !props.isMultiple ? ref("") : ref([])
	const curSelData = !props.isMultiple ? ref({}) : ref([])

	watch(() => props.modelValue, () => {
		initData()
	}, {
		deep: false,
		immediate: false
	})

	watch(() => props.rangeData, () => {
		if(!props.isBigData){
			initData()
		}
	}, {
		deep: false,
		immediate: false
	})

  const open = () => {
    showBottom.value = true
  }
	
	const initData = () => {
		if (!props.isMultiple) {
			curSelDataKey.value = props.modelValue ? XEUtils.clone(props.modelValue, true) : ""
			if(props.isBigData){
				emits('queryCurSelData', curSelDataKey.value, (val) => {
					curSelData.value = val
				})
			}else{
				if (!XEUtils.isEmpty(curSelDataKey.value)) {
					const curSelDataArr = XEUtils.filterTree(props.rangeData, item => {
						return curSelDataKey.value === item[props.map.key]
					})
					if (curSelDataArr && curSelDataArr.length === 1) {
						curSelData.value = curSelDataArr[0]
					}
				} else {
					curSelData.value = {}
				}
			}
			
		}
		// 多选curSelData初始化值赋值
		// if (!!props.isMultiple) {
		// 	curSelDataKey.value = props.modelValue ? XEUtils.clone(props.modelValue, true) : []
		//
		// 	if(props.isBigData){
		// 		emits('queryCurSelData', curSelDataKey.value, (val) => {
		// 			curSelData.value = val
		// 		})
		// 	}else{
		// 		if (!XEUtils.isEmpty(curSelDataKey.value)) {
		// 			curSelData.value = XEUtils.filterTree(props.rangeData, item => {
		// 				return curSelDataKey.value.includes(item[props.map.key])
		// 			})
		// 		} else {
		// 			curSelData.value = []
		// 		}
		// 	}
		// }
	}

	// 选择或删除数据
	const selOrDelData = (item, index) => {
		if (!props.isMultiple) {
			if (item[props.map.key] != curSelDataKey.value) {
				selData(item, index)
			} else {
				delData(item, index)
			}
		} else {
			if (!curSelDataKey.value.includes(item[props.map.key])) {
				selData(item, index)
			} else {
				delData(item, index)
			}
		}
	}

	// 选择数据
	const selData = (item) => {
		if (!props.isMultiple) {
			curSelDataKey.value = item[props.map.key]
			curSelData.value = item
		} else {
			curSelDataKey.value.push(item[props.map.key])
			curSelData.value.push(item)
		}
	}
	// // 移除数据
	const delData = (item) => {
		if (!props.isMultiple) {
			curSelDataKey.value = ""
			curSelData.value = {}
		} else {
			curSelDataKey.value.splice(curSelDataKey.value.findIndex(curSelDataKeyItem => curSelDataKeyItem === item[
				props.map.key]), 1);
			curSelData.value.splice(curSelData.value.findIndex(curSelDataItem => curSelDataItem[props.map.key] ===
				item[props.map.key]), 1);
		}
	}

	// 取消
	const cancel = () => {
		// 重置数据
		initData()
    showBottom.value = false
	}

	const confirm = () => {
		// 更新数据
		// emits('update:modelValue', curSelDataKey.value)
		// 调用父组件方法
		emits('confirm', {
			curSelDataKey: curSelDataKey.value,
			curSelData: curSelData.value
		})
    showBottom.value = false
	}

	defineExpose({
		initData,
    open
	})
</script>

<style lang="scss">
	.snowy-sel-picker {

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

      .cal {
        color: #909399;
      }

      .conf {
        color: #2979ff;
      }
    }

    .choiced {
      text-align: left;
      margin-top: 10px;

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
      margin-top: 10px;
      height: 22vh;
    }
	}
</style>
