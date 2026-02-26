<template>
	<a-card :bordered="false" :loading="cardLoading">
		<a-collapse v-model:activeKey="activeKey" ghost expandIconPosition="right" :bordered="true">
			<a-collapse-panel
				v-for="item in modelCategory"
				:key="item.dictValue"
				v-show="myModelList.filter((m) => m.category === item.dictValue).length > 0"
			>
				<template #header>
					<span style="font-weight: 700">{{ item.name }}</span>
					<span style="color: #71797d; margin-left: 5px">
						（{{ myModelList.filter((m) => m.category === item.dictValue).length }}）
					</span>
				</template>
				<a-row :gutter="10">
					<a-col :span="4" v-for="model in myModelList.filter((m) => m.category === item.dictValue)">
						<process-card
							:color="model.color"
							:label="model.name"
							:icon="model.icon"
							@click="startProcessRef.onOpen(model)"
						/>
					</a-col>
				</a-row>
			</a-collapse-panel>
		</a-collapse>
		<div v-if="activeKey.length === 0">
			<a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" />
		</div>
	</a-card>
	<startProcess ref="startProcessRef" />
</template>

<script setup name="flwNewTask">
	import processMyApi from '@/api/flw/processMyApi'
	import processCard from '../../process/processCard.vue'
	import startProcess from './startProcess.vue'
	import { Empty } from 'ant-design-vue'
	import tool from '@/utils/tool'
	const cardLoading = ref(true)
	// 默认展开的
	const activeKey = ref([])
	// 我的流程列表
	const myModelList = ref([])

	const startProcessRef = ref()

	// 流程分类的字典
	const modelCategory = tool.dictTypeList('MODEL_CATEGORY')
	processMyApi.processMyModelList().then((data) => {
		cardLoading.value = false
		myModelList.value = data
		// 加入展开的
		if (data.length > 0) {
			data.forEach((item) => {
				activeKey.value.push(item.category)
			})
		}
	})
</script>
