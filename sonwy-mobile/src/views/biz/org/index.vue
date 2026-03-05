<template>
	<div class="org-container" id="org-container">
    <div class="crumb">
      <label class="crumb-text" v-for="(item, index) in allSelOrg" :key="index" @click="clickOrgCru(item, index)"
             :class="index === (allSelOrg.length-1) ? 'uni-secondary-color' : 'uni-primary'">
        {{ item.name + (index === (allSelOrg.length-1) ? '' : ' | ') }}
      </label>
    </div>
    <div class="org-list">
      <template v-for="(item, index) in curSelOrg" :key="index">
        <van-cell :title="item.name" @click="morePopupRef.open(item)">
          <template #right-icon>
            <van-icon v-if="item.children? true : false" name="arrow" @click.stop="clickOrg(item, index)" />
          </template>
        </van-cell>
      </template>
      <snowy-empty v-if="curSelOrg && curSelOrg.length === 0" />
    </div>
    <van-floating-bubble v-if="hasPerm('mobileBizOrgAdd')"  v-model:offset="offset" axis="xy" icon="plus" @click="add" />
    <!-- 更多操作 -->
    <morePopup ref="morePopupRef" @handleOk="loadData()"></morePopup>
  </div>
</template>

<script setup>
	import { orgTree } from '@/api/biz/bizOrgApi';
	import SnowyEmpty from "@/components/snowy-empty.vue";
	import { ref, onMounted} from "vue";
	import morePopup from './more-popup.vue';
  import router from "@/router/index";
  import { hasPerm } from '@/plugins/permission';

	const morePopupRef = ref()
  const offset = ref({ x: document.body.clientWidth - 72,y: document.body.clientHeight - 150 })

	// 所有选择的机构
	const allSelOrg = ref([])
	// 当前选择的机构
	const curSelOrg = ref([])

	// // 展示
	// onShow(() => {
	// 	uni.$once('formBack', (data) => {
	// 		loadData()
	// 	})
	// })

  onMounted(() => {
    loadData()
  })

	const loadData = () => {
		orgTree().then(res => {
			curSelOrg.value = res?.data || []
			allSelOrg.value = [{
				id: '0',
				name: '全部',
				children: res?.data || []
			}]
		})
	}

	const clickOrgCru = (item, index) => {
		curSelOrg.value = item.children
		allSelOrg.value.splice(index + 1, allSelOrg.value.length - (index + 1))
	}

	const clickOrg = (item) => {
		if (item.children) {
			curSelOrg.value = item.children
			allSelOrg.value.push(item)
		}
	}

	// 新增悬浮按钮
	const add = () => {
    router.push({
      path: '/orgForm'
    })
	}
</script>
<style lang="scss">

.org-container {
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

    .org-list-name {
      flex: 1;
    }

    .van-cell {
      border-radius: 5px;

      .van-badge__wrapper {
        top: 5px;
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
