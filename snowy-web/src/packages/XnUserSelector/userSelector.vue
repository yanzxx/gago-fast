<template>
	<div>
		<div
			class="selector-input-div"
			:style="{ width: record.options.width }"
			@blur="onEditorBlur($event)"
			@focus="onEditorFocus($event)"
			ref="userSelectorRef"
		>
			<div
				:class="!record.options.disabled ? 'selector-input-div-external' : 'selector-input-div-external-disable'"
				@click="!record.options.disabled ? showUserPlusModal() : ''"
			>
				<span v-if="resultData.length === 0" class="selector-input-div-span">{{ record.options.placeholder }}</span>
				<a-tag
					v-for="user in resultData"
					:key="user.id"
					:closable="!record.options.disabled"
					@close="closeTag(user.id)"
					class="selector-input-div-tag"
					>{{ user.name }}</a-tag
				>
			</div>
			<a-button
				v-if="resultData.length > 0 & !record.options.disabled"
				type="link"
				class="selector-input-div-btn"
				size="small"
				@click="delAllResultData"
				>
        <template #icon>
          <CloseCircleFilled style="color: #d9d9d9" />
        </template>
      </a-button>
		</div>

		<a-modal
			v-model:visible="visible"
			title="用户选择"
			:width="1000"
			:destroy-on-close="true"
			:mask="false"
			@ok="handleOk"
			@cancel="handleClose"
		>
			<a-row :gutter="[10, 10]">
				<a-col :xs="24" :sm="24" :md="7" :lg="7" :xl="7">
					<a-card size="small" :loading="cardLoading" class="selectorTreeDiv">
						<a-tree
							v-if="!isEmpty(treeData)"
							v-model:expandedKeys="defaultExpandedKeys"
							:tree-data="treeData"
							:field-names="treeFieldNames"
							@select="treeSelect"
						>
						</a-tree>
						<a-empty v-else :image="Empty.PRESENTED_IMAGE_SIMPLE" />
					</a-card>
				</a-col>
				<a-col :xs="24" :sm="18" :md="11" :lg="11" :xl="11">
					<div class="table-operator" style="margin-bottom: 10px">
						<a-form
							ref="searchFormRef"
							name="advanced_search"
							class="ant-advanced-search-form"
							:model="searchFormState"
						>
							<a-row :gutter="24">
								<a-col :span="12">
									<a-form-item name="searchKey">
										<a-input v-model:value="searchFormState.searchKey" placeholder="请输入用户名" />
									</a-form-item>
								</a-col>
								<a-col :span="12">
									<a-button type="primary" class="primarySele" @click="loadData()"> 查询 </a-button>
									<a-button class="snowy-buttom-left" @click="reset()"> 重置 </a-button>
								</a-col>
							</a-row>
						</a-form>
					</div>
					<div class="user-table">
						<a-table
							ref="table"
							size="small"
							:columns="commons"
							:data-source="tableData"
							:expand-row-by-click="true"
							bordered
							:pagination="false"
						>
							<template #title>
								<span>待选择列表 {{ tableRecordNum }} 条</span>
								<div v-if="!radioModel" style="float: right">
									<a-button type="dashed" size="small" @click="addAllPageRecord" :disabled="tableRecordNum === 0">
										添加当前数据
									</a-button>
								</div>
							</template>
							<template #bodyCell="{ column, record }">
								<template v-if="column.dataIndex === 'action'">
									<a-button type="dashed" size="small" @click="addRecord(record)">添加</a-button>
								</template>
							</template>
						</a-table>
						<div class="mt-2">
							<a-pagination
								v-if="!isEmpty(tableData)"
								v-model:current="current"
								v-model:page-size="pageSize"
								:total="total"
								size="small"
								showSizeChanger
								@change="paginationChange"
							/>
						</div>
					</div>
				</a-col>
				<a-col :xs="24" :sm="6" :md="6" :lg="6" :xl="6">
					<div class="user-table">
						<a-table
							ref="selectedTable"
							size="small"
							:columns="selectedCommons"
							:data-source="selectedData"
							:expand-row-by-click="true"
							bordered
						>
							<template #title>
								<span>已选择: {{ selectedData.length }}</span>
								<div v-if="!radioModel" style="float: right">
									<a-button
										type="dashed"
										danger
										size="small"
										@click="delAllRecord"
										:disabled="selectedData.length === 0"
										>全部移除</a-button
									>
								</div>
							</template>
							<template #bodyCell="{ column, record }">
								<template v-if="column.dataIndex === 'action'">
									<a-button type="dashed" danger size="small" @click="delRecord(record)">移除</a-button>
								</template>
							</template>
						</a-table>
					</div>
				</a-col>
			</a-row>
		</a-modal>
	</div>
</template>

<script setup name="XnUserSelector">
	import { ref, watch } from 'vue'
	import { Empty, message } from 'ant-design-vue'
  import { CloseCircleFilled } from '@ant-design/icons-vue';
	import { remove, cloneDeep, isEmpty } from 'lodash-es'

	// 弹窗是否打开
	const visible = ref(false)
	const userSelectorRef = ref()
	// 主表格common
	const commons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 80
		},
		{
			title: '用户名',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '账号',
			dataIndex: 'account'
		}
	]
	// 选中表格的表格common
	const selectedCommons = [
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 80
		},
		{
			title: '用户名',
			dataIndex: 'name',
			ellipsis: true
		}
	]
	// 主表格的ref 名称
	const table = ref()
	// 选中表格的ref 名称
	const selectedTable = ref()
	const tableRecordNum = ref(0)
	const searchFormState = ref({})
	const searchFormRef = ref()
	const cardLoading = ref(false)
	// 替换treeNode 中 title,key,children
	const treeFieldNames = { children: 'children', title: 'name', key: 'id' }
	// 获取机构树数据
	const treeData = ref([])
	//  默认展开二级树的节点id
	const defaultExpandedKeys = ref([])
	const emit = defineEmits({ change: null, input: null })
	const tableData = ref([])
	const selectedData = ref([])
	// eslint-disable-next-line vue/require-prop-types
	const props = defineProps(['record', 'config', 'value'])
	// 是否是单选
	const radioModel = ref(props.record.options.radioModel)
	// 数据是否转换成工作流格式
	const dataIsConverterFlw = props.config.dataIsConverterFlw || true
	const resultData = ref([])

	// 分页相关
	const current = ref(0) // 当前页数
	const pageSize = ref(0) // 每页条数
	const total = ref(0) // 数据总数

	// 监听数据
	watch(
		() => props.value,
		(newVal, oldVal) => {
			if (newVal) {
				resultData.value = cloneDeep(newVal)
				selectedData.value = cloneDeep(newVal)
			}
		},
		{ deep: true, immediate: true }
	)
	// 打开弹框，使用同步接口获取数据完成后再往下走
	const showUserPlusModal = () => {
		visible.value = true
		// 获取机构树
		const getOrgTree = props.record.options.getOrgTree || props.config.getOrgTree
		if (getOrgTree) {
			getOrgTree()
				.then((tree) => {
					if (!isEmpty(tree)) {
						treeData.value = tree
						tree.forEach((item) => {
							// 因为0的顶级
							if (item.parentId === '0') {
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
				.finally(() => {
					cardLoading.value = false
				})
		}
		loadData()
	}
	// 查询主表格数据
	const loadData = async (param) => {
		// 总共多少条
		let userPage = {}
		const getUserPage = props.record.options.getUserPage || props.config.getOrgPage
		if (getUserPage) {
			let params = {}
			if (isEmpty(param)) {
				params = searchFormState.value
			} else {
				params = Object.assign(param, searchFormState.value)
			}
			userPage = await getUserPage(params)
		}
		if (!isEmpty(userPage)) {
			current.value = userPage.current
			pageSize.value = userPage.size
			total.value = userPage.total
			// 重置、赋值
			tableData.value = []
			tableRecordNum.value = 0
			tableData.value = userPage.records
			if (userPage.records) {
				tableRecordNum.value = userPage.records.length
			} else {
				tableRecordNum.value = 0
			}
		}
	}
	const judge = () => {
		if (radioModel.value && selectedData.value.length > 0) {
			return false
		}
		return true
	}
	// 添加记录
	const addRecord = (record) => {
		if (!judge()) {
			message.warning('只可选择一条')
			return
		}
		const selectedRecord = selectedData.value.filter((item) => item.id === record.id)
		if (selectedRecord.length === 0) {
			selectedData.value.push(record)
		} else {
			message.warning('该记录已存在')
		}
	}
	// 添加全部
	const addAllPageRecord = () => {
		let newArray = selectedData.value.concat(tableData.value)
		let list = []
		for (let item1 of newArray) {
			let flag = true
			for (let item2 of list) {
				if (item1.id === item2.id) {
					flag = false
				}
			}
			if (flag) {
				list.push(item1)
			}
		}
		selectedData.value = list
	}
	// 删减记录
	const delRecord = (record) => {
		remove(selectedData.value, (item) => item.id === record.id)
	}
	// 删减全部记录
	const delAllRecord = () => {
		remove(selectedData.value)
	}
	// 点击树查询
	const treeSelect = (selectedKeys) => {
		if (selectedKeys.length > 0) {
			searchFormState.value.orgId = selectedKeys.toString()
		} else {
			delete searchFormState.value.orgId
		}
		loadData()
	}
	// 确定
	const handleOk = () => {
		const value = []
		selectedData.value.forEach((item) => {
			const obj = {
				id: item.id,
				name: item.name,
				account: item.account
			}
			value.push(obj)
		})
		resultData.value = value
		emit('change', value)
		emit('input', value)
		handleClose()
	}
	// 重置
	const reset = () => {
		delete searchFormState.value.searchKey
		loadData()
	}
	// 关闭
	const handleClose = () => {
		searchFormState.value = {}
		tableRecordNum.value = 0
		tableData.value = []
		visible.value = false
	}
	// pageSize改变回调分页事件
	const paginationChange = (page, pageSize) => {
		const param = {
			current: page,
			size: pageSize
		}
		loadData(param)
	}
	// 数据进入后转换
	const goDataConverter = (data) => {
		const resultData = []
		if (data.length > 0) {
			const values = data[0].value.split(',')
			if (JSON.stringify(values) !== '[""]') {
				for (let i = 0; i < values.length; i++) {
					resultData.push(values[i])
				}
			}
		}
		return resultData
	}
	// 数据出口转换器
	const outDataConverter = (data) => {
		const obj = {}
		let label = ''
		let value = ''
		for (let i = 0; i < data.length; i++) {
			if (data.length === i + 1) {
				label = label + data[i].name
				value = value + data[i].id
			} else {
				label = label + data[i].name + ','
				value = value + data[i].id + ','
			}
		}
		obj.key = 'USER'
		obj.label = label
		obj.value = value
		obj.extJson = ''
		return obj
	}
	// 点击标签删除
	const closeTag = (value) => {
		remove(resultData.value, (item) => item.id === value)
		// 顺手吧里面的也给搞掉
		remove(selectedData.value, (item) => item.id === value)
	}
	// 清空
	const delAllResultData = () => {
		remove(resultData.value)
		// 顺手吧里面的也给搞掉
		remove(selectedData.value)
	}
	const onEditorBlur = () => {
		emit('change', [])
		emit('input', [])
	}
	const onEditorFocus = () => {
		emit('change', [])
		emit('input', [])
	}
</script>

<style lang="less" scoped>
	.selector-input-div {
		display: flex;
		justify-content: flex-end;
	}
	.selector-input-div-external {
		width: 100%;
		min-height: 32px;
		border: 1px solid #d9d9d9;
		border-radius: 2px;
		cursor: pointer;
		z-index: 0;
		padding: 2px;
		background-color: white;
	}
	.selector-input-div-external-disable {
		width: 100%;
		min-height: 32px;
		border: 1px solid #d9d9d9;
		border-radius: 2px;
		cursor: pointer;
		z-index: 0;
		padding: 2px;
		background-color: #d9d9d940;
	}
	.selector-input-div-span {
		width: auto;
		color: rgb(200 199 199);
		position: absolute;
		padding-left: 10px;
		padding-top: 2px;
		display: block;
	}
	.selector-input-div-tag {
		margin-top: 2px;
		margin-bottom: 2px;
	}
	.selector-input-div-btn {
		float: right;
		position: absolute;
		z-index: 999;
		margin-top: 3px;
	}
	.selectorTreeDiv {
		max-height: 500px;
		overflow: auto;
	}
	.cardTag {
		margin-left: 10px;
	}
	.primarySele {
		margin-right: 10px;
	}
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.user-table {
		overflow: auto;
		max-height: 450px;
	}
</style>
