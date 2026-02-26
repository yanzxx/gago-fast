<template>
	<a-row>
		<a-col :span="5">
			<a-card class="cardImp" :bordered="false" :loading="cardLoading">
				<a-tree
					v-if="treeData.length > 0"
					v-model:expandedKeys="defaultExpandedKeys"
					:tree-data="treeData"
					:field-names="treeFieldNames"
					@select="treeSelect"
				/>
				<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</a-card>
		</a-col>
		<a-col :span="19">
			<a-card :bordered="false" style="margin-bottom: 10px">
				<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
					<a-row :gutter="24">
						<a-col :span="8">
							<a-form-item name="searchKey" label="名称关键词">
								<a-input v-model:value="searchFormState.searchKey" placeholder="请输入区划名称关键词"></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="8">
							<a-button type="primary" @click="table.refresh(true)">
								<template #icon><SearchOutlined /></template>
								查询
							</a-button>
							<a-button class="snowy-buttom-left" @click="reset">
								<template #icon><redo-outlined /></template>
								重置
							</a-button>
						</a-col>
					</a-row>
				</a-form>
			</a-card>
			<a-card :bordered="false">
				<s-table
					ref="table"
					:columns="columns"
					:data="loadData"
					:expand-row-by-click="true"
					:alert="options.alert.show"
					bordered
					:row-key="(record) => record.id"
					:row-selection="options.rowSelection"
				>
					<template #operator class="table-operator">
						<a-space>
							<a-button type="primary" @click="form.onOpen(undefined, searchFormState.parentCode)">
								<template #icon><plus-outlined /></template>
								新增
							</a-button>
							<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchRegion" />
						</a-space>
					</template>
					<template #bodyCell="{ column, record }">
						<template v-if="column.dataIndex === 'category'">
							{{ $TOOL.dictTypeData('REGION_CATEGORY', record.category) }}
						</template>
						<template v-if="column.dataIndex === 'action'">
							<a @click="form.onOpen(record)">编辑</a>
							<a-divider type="vertical" />
							<a-popconfirm title="删除此区划与下级区划吗？" @confirm="removeRegion(record)">
								<a-button type="link" danger size="small">删除</a-button>
							</a-popconfirm>
						</template>
					</template>
				</s-table>
			</a-card>
		</a-col>
	</a-row>
	<Form ref="form" @successful="table.refresh(true)" />
</template>

<script setup name="sysRegion">
	import { message, Empty } from 'ant-design-vue'
	import regionApi from '@/api/sys/regionApi'
	import Form from './form.vue'

	const columns = [
		{
			title: '区划名称',
			dataIndex: 'name'
		},
		{
			title: '区划代码',
			dataIndex: 'code'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	let selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	// 定义tableDOM
	const table = ref(null)
	const form = ref()
	const searchFormRef = ref()
	let searchFormState = reactive({})
	// 默认展开的节点
	let defaultExpandedKeys = ref([])
	const treeData = ref([])
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	const cardLoading = ref(true)

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		loadTreeData()
		return regionApi.regionPage(Object.assign(parameter, searchFormState)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		table.value.refresh(true)
	}
	// 加载左侧的树
	const loadTreeData = () => {
		regionApi.regionTree().then((res) => {
			cardLoading.value = false
			if (res !== null) {
				treeData.value = res
				// 默认展开2级
				treeData.value.forEach((item) => {
					// 因为0的顶级
					if (item.parentCode === '0') {
						defaultExpandedKeys.value.push(item.id)
						// 取到下级ID
						if (item.children) {
							item.children.forEach((items) => {
								defaultExpandedKeys.value.push(items.id)
							})
						}
					}
				})
			}
		})
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.parentCode = selectedKeys.toString()
		} else {
			delete searchFormState.parentCode
		}
		table.value.refresh(true)
	}
	// 删除
	const removeRegion = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		regionApi.regionDelete(params).then(() => {
			table.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchRegion = (params) => {
		regionApi.regionDelete(params).then(() => {
			table.value.clearRefreshSelected()
		})
	}
</script>

<style scoped>
	.cardImp {
		margin-right: 10px;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.primaryAdd {
		margin-right: 10px;
	}
	.snowy-buttom-left {
		margin-left: 8px;
	}
</style>
