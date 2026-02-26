<template>
	<xn-form-container
		title="授权终端登录"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:show-pagination="false"
		@close="onClose"
	>
		<div v-if="isLoginTermainal != 'true'" style="margin-bottom: 10px">
			<a-alert message="当前 【限制登录终端开关】 为关闭状态" type="warning" />
		</div>
		<a-spin :spinning="spinningLoading">
			<a-table size="middle" :columns="overallColumns" :data-source="loadDatasGlobal" :pagination="false" bordered>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'desc'">
						<a-checkbox :checked="record.nameCheck" @update:checked="(val) => changeSub(record, val)">
							{{ record.desc }}
						</a-checkbox>
					</template>
				</template>
			</a-table>
		</a-spin>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="grantLoginTerminalForm">
	import roleApi from '@/api/sys/roleApi'
	import { message } from 'ant-design-vue'
	import tool from '@/utils/tool'

	const toolDataGet = (key) => {
		return tool.data.get(key)
	}

	const spinningLoading = ref(false)
	let firstShowMap = $ref({})
	const emit = defineEmits({ successful: null })
	const submitLoading = ref(false)
	// 抽屉的宽度
	const drawerWidth = 1000

	const overallColumns = [
		{
			key: 'title',
			title: '登录终端名称',
			dataIndex: 'desc',
			width: 150
		},
		{
			key: 'code',
			title: '登录终端编码',
			dataIndex: 'value',
			width: 200
		}
	]
	const loadDatasGlobal = ref([])
	const isLoginTermainal = toolDataGet('SNOWY_SYS_BASE_CONFIG').FAST_TERMINAL_LIMIT_OPEN

	// 获取数据
	const loadData = async () => {
		spinningLoading.value = true
		const res = await roleApi.roleGrantTerminalSelector()
		if (res && res.length > 0) {
			const param = {
				id: resultDataModel.id
			}
			// 获取回显数据
			const resEcho = await roleApi.ownGrantTerminalResource(param)
			spinningLoading.value = false
			if (resEcho && resEcho.grantInfoList) {
				res.forEach((item) => {
					if (resEcho.grantInfoList.includes(item.value)) {
						item.nameCheck = true
					} else {
						item.nameCheck = false
					}
					// if (item.desc.indexOf('PC') > -1) {
					// 	item.nameCheck = true
					// }
				})
				loadDatasGlobal.value = [...res]
			}
		}
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

	// 关闭抽屉
	const onClose = () => {
		// 将这些缓存的给清空
		loadDatasGlobal.value = []
		visible = false
	}

	// 验证并提交数据
	const onSubmit = () => {
		const grantGlobalList = []
		loadDatasGlobal.value.forEach((item) => {
			if (item.nameCheck) {
				grantGlobalList.push(item.value)
			}
		})
		if (grantGlobalList.length == 0) {
			message.warning('终端登录权限至少勾选一个')
			return
		}
		submitLoading.value = true
		roleApi
			.grantTerminalUser({
				id: resultDataModel.id,
				grantTerminalList: grantGlobalList
			})
			.then(() => {
				onClose()
				emit('successful')
			})
			.finally(() => {
				submitLoading.value = false
			})
	}

	// 勾选
	const changeSub = (record, val) => {
		record.nameCheck = val
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
