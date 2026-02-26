<template>
	<xn-form-container
		title="授权资源"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:show-pagination="false"
		@close="onClose"
	>
		<a-spin :spinning="spinningLoading">
			<a-table size="middle" :columns="columns" :data-source="loadDatas" :pagination="false" bordered>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'title'">
						<a-checkbox :checked="record.nameCheck" @update:checked="(val) => changeSub(record, val)">{{
							record.title
						}}</a-checkbox>
					</template>

					<!--					<template v-if="column.dataIndex === 'button'">-->
					<!--						<template v-if="record.button.length > 0">-->
					<!--							<template v-for="(item, index) in record.button" :key="item.id">-->
					<!--								<a-checkbox v-model:checked="item.check" @change="(evt) => changeChildCheckBox(record, evt)">{{-->
					<!--										item.title-->
					<!--									}}</a-checkbox>-->
					<!--								<br v-if="(index + 1) % 5 === 0" />-->
					<!--							</template>-->
					<!--						</template>-->
					<!--					</template>-->
				</template>
			</a-table>
		</a-spin>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="GrantSpaResourceForm">
	import { nextTick } from 'vue'
	import tool from '@/utils/tool'
	import roleApi from '@/api/sys/roleApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	const spinningLoading = ref(false)
	let firstShowMap = $ref({})
	const emit = defineEmits({ successful: null })
	const submitLoading = ref(false)
	// 抽屉的宽度
	const drawerWidth = 1000
	// 自动获取宽度，默认获取浏览器的宽度的90%
	//(window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) * 0.9

	const columns = [
		{
			key: 'title',
			title: '单页',
			dataIndex: 'title',
			width: 200
		}
		// {
		// 	key: 'button',
		// 	title: '按钮授权',
		// 	dataIndex: 'button'
		// }
	]
	const echoDatalist = ref([])
	const loadDatas = ref([])

	// 获取数据
	const loadData = async () => {
		// 获取表格数据
		spinningLoading.value = true
		const res = await roleApi.roleSpaTreeSelector()
		const param = {
			id: resultDataModel.id
		}
		// 获取回显数据
		const resEcho = await userCenterApi.roleOwnSpaTreeSelector(param)
		spinningLoading.value = false
		echoDatalist.value = echoModuleData(res, resEcho)
		loadDatas.value = echoDatalist.value
	}
	let visible = $ref(false)
	// 返回的数据模型，最终需要转换成这样
	let resultDataModel = {
		id: '',
		grantInfoList: []
	}
	// 打开抽屉
	const onOpen = (record) => {
		resultDataModel.id = record.id
		visible = true
		firstShowMap = {}
		loadData()
	}
	// 数据转换
	const echoModuleData = (data, resEcho) => {
		data.forEach((spa) => {
			resEcho.forEach((grant) => {
				if (spa.id === grant.id) {
					spa.nameCheck = true
				}
			})
		})
		return data
	}

	// 二级菜单的勾选
	const changeSub = (record, val) => {
		// 选中二级菜单
		record.nameCheck = val
	}
	// 关闭抽屉
	const onClose = () => {
		// 将这些缓存的给清空
		echoDatalist.value = []
		loadDatas.value = []
		firstShowMap = {}
		visible = false
	}
	// 提交之前转换数据
	const convertData = () => {
		resultDataModel.grantInfoList = []
		echoDatalist.value.forEach((table) => {
			if (table.nameCheck) {
				resultDataModel.grantInfoList.push({
					spaId: table.id,
					buttonInfo: []
				})
			}
		})
		return resultDataModel
	}
	// 验证并提交数据
	const onSubmit = () => {
		const param = convertData()
		submitLoading.value = true
		roleApi
			.roleSpaResource(param)
			.then(() => {
				onClose()
				emit('successful')
			})
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>

<style scoped>
	/* 重写复选框的样式 */
	.ant-checkbox-wrapper {
		margin-left: 0px !important;
		padding-top: 2px !important;
		padding-bottom: 2px !important;
	}
</style>
