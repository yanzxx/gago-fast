<template>
	<div class="snowy-org-picker">
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
      <div class="crumb">
        <label v-for="(item, index) in allClickSelOrg" :key="index" @click="clickOrgCru(item, index)"
              :class="index === (allClickSelOrg.length-1) ? 'uni-secondary-color' : 'uni-primary'">
          {{ item.name + (index === (allClickSelOrg.length-1) ? '' : ' | ') }}
        </label>
      </div>
      <div class="choiced" v-show="!!curSelOrgId && (!props.isMultiple ? true : curSelOrgId.length > 0)"
           :style="{maxHeight: !props.isMultiple ? '5vh' : '20vh', overflowY: 'scroll'}">
        <!-- 单选已选择 -->
        <div class="single" v-if="!props.isMultiple">
          <div class="name" @click="delOrg(curSelOrg)">
            {{ curSelOrg.name }}
          </div>
          <van-icon name="delete" @click.stop="delOrg(curSelOrg)" color="#e43d33" size="20" />
        </div>
        <!-- 多选已选择 -->
        <template v-if="!!props.isMultiple">
          <div class="multiple"  v-for="(item, index) in curSelOrg" :key="index">
            <div class="name" @click="delOrg(item)">
              {{ item.name }}
            </div>
            <van-icon name="delete" @click.stop="delOrg(item)" color="#e43d33" size="20" />
          </div>
        </template>
      </div>

      <div class="org">
        <template v-for="(item, index) in curClickSelOrg" :key="index">
          <van-cell>
            <template #title>
              <van-icon name="circle" :size="25"
                        v-show="!isMultiple ? item.id != curSelOrgId: curSelOrgId.indexOf(item.id) == -1"
                        @click="selOrg(item, index)" />
              <van-icon name="checked" :size="25" color="#2979ff"
                        v-show="!isMultiple ? item.id == curSelOrgId: curSelOrgId.indexOf(item.id) != -1"
                        @click="delOrg(item, index)" />
              <span class="custom-title" @click="clickOrg(item, index)">{{ item.name }}</span>
            </template>
            <template #right-icon>
              <van-icon name="arrow" @click="clickOrg(item, index)" v-if="item.children? true : false" />
            </template>
          </van-cell>
        </template>
      </div>
    </van-popup>
	</div>
</template>

<script setup>
	import { orgTree } from '@/api/biz/bizOrgApi'

	import { ref, watch, defineExpose, defineEmits } from "vue";
	import XEUtils from 'xe-utils'

	const emits = defineEmits(['update:modelValue', 'cancel', 'confirm'])

	const props = defineProps({
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
			default: "请选择机构",
			required: false
		},
		isTopLevel: {
			type: Boolean,
			default: false,
			required: false
		}
	})
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

	// 当前选中的机构id及机构
	const curSelOrgId = !props.isMultiple ? ref("") : ref([])
	const curSelOrg = !props.isMultiple ? ref({}) : ref([])

	// 所有点击选中机构【页面数据】
	const allClickSelOrg = ref([])
	// 当前点击选中机构【页面数据】
	const curClickSelOrg = ref([])

	watch(() => props.modelValue, () => {
		loadData()
	}, {
		deep: false,
		immediate: false
	})

  const open = () => {
    showBottom.value = true
  }

	const loadData = () => {
		orgTree().then(res => {
			if (props.isTopLevel) {
				// 含有顶级
				curClickSelOrg.value = [{
					id: '0',
					parentId: '-1',
					name: '顶级',
					children: res.data
				}]
				allClickSelOrg.value = [{
					id: '-1',
					name: '全部',
					children: [{
						id: '0',
						parentId: '-1',
						name: '顶级',
						children: res.data
					}]
				}]
			} else {
				// 不含有顶级
				curClickSelOrg.value = res.data
				allClickSelOrg.value = [{
					id: '0',
					parentId: '-1',
					name: '全部',
					children: res.data
				}]
			}

			// 单选curSelOrg初始化值赋值
			if (!props.isMultiple) {
				if (props.modelValue) {
					curSelOrgId.value = XEUtils.clone(props.modelValue, true)
				} else {
					curSelOrgId.value = ""
				}
				if (curSelOrgId.value) {
					const curSelOrgArr = XEUtils.filterTree(allClickSelOrg.value, item => {
						return curSelOrgId.value === item.name
					})
					if (curSelOrgArr && curSelOrgArr.length === 1) {
						curSelOrg.value = curSelOrgArr[0]
					}
				} else {
					curSelOrg.value = {}
				}
			}
		})
	}
	loadData()
	const clickOrgCru = (item, index) => {
		curClickSelOrg.value = item.children
		allClickSelOrg.value.splice(index + 1, allClickSelOrg.value.length - (index + 1))
	}
	// 选择部门
	const selOrg = (item) => {
		if (!props.isMultiple) {
			curSelOrgId.value = item.id
			curSelOrg.value = item
		} else {
			curSelOrgId.value.push(item.id)
			curSelOrg.value.push(item)
		}
	}
	// 移除部门
	const delOrg = (item) => {
		if (!props.isMultiple) {
			curSelOrgId.value = ""
			curSelOrg.value = {}
		} else {
			curSelOrgId.value.splice(curSelOrgId.value.findIndex(curSelOrgIdItem => curSelOrgIdItem === item.id), 1);
			curSelOrg.value.splice(curSelOrg.value.findIndex(curSelOrgItem => curSelOrgItem.id === item.id), 1);
		}
	}
	// 点击部门
	const clickOrg = (item) => {
		if (item.children) {
			curClickSelOrg.value = item.children
			allClickSelOrg.value.push(item)
		}
	}

	// 取消
	const cancel = () => {
		// 重置数据
		loadData()
    showBottom.value = false
	}

	const confirm = () => {
		// 调用父组件方法
		emits('confirm', {
			curSelOrgId: curSelOrgId.value,
			curSelOrg: curSelOrg.value
		})
    showBottom.value = false
	}

  defineExpose({
    open
  })
</script>

<style lang="scss">
	.snowy-org-picker {

    .van-popup {
      padding-top: 15px;

      .van-cell__title {
        width: fit-content;
      }
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
      height: 40vh;
      overflow-y: scroll;

      .name {
        flex: 1;
        font-size: 30px;
        margin: 2px 20px;
      }
    }

    .van-cell__title {
      width: fit-content;
    }
	}

  .uni-secondary-color {
    color: #909399 !important;
  }

  .uni-primary {
    color: #2979ff !important;
  }

</style>
