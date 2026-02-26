<template>
	<xn-form-container
		title="生成代码"
		:width="500"
		:visible="visible"
		:destroy-on-close="true"
		:body-style="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-checkbox @change="handleChange">全选</a-checkbox>
		<a-spin :spinning="loadingSpinning">
			<div v-for="item of data.genFileNames" :key="item.title">
				<a-alert :message="item.title" type="info" style="margin-top: 10px" />
				<div v-for="it of item.value" :key="it.title" style="margin-top: 10px">
					<a-checkbox v-model:checked="it.checked">{{ it.title }}</a-checkbox>
				</div>
			</div>
		</a-spin>
		<div style="position: fixed; bottom: 10px; right: 10px">
			<a-button @click="onClose">取消</a-button>
			<a-button type="primary" @click="saveCodeOption" style="margin-left: 20px">确定</a-button>
		</div>
	</xn-form-container>
</template>

<script setup name="genPreview">
	import genBasicApi from '@/api/gen/genBasicApi'
	import { reactive, defineEmits } from 'vue'

	const emit = defineEmits(['saveCodeEmit', 'nullCodeEmit'])
	// 默认是关闭状态
	const visible = ref(false)
	const loadingSpinning = ref(true)

	const data = reactive({
		genFileNames: [],
		recordObj: {}
	})
	// 全选
	const handleChange = (event) => {
		data.genFileNames.forEach((item) => {
			if (item.es === 'genFrontFileNames' || item.es === 'genBackendFileNames') {
				item.value.forEach((item_value) => {
					item_value.checked = event.target.checked
				})
			}
		})
	}
	// 打开抽屉
	const onOpen = (record) => {
		data.recordObj = record
		visible.value = true
		loadingSpinning.value = true
		genBasicApi.getGenFileNames().then((res) => {
			const genFileNames = []
			Object.keys(res).forEach((item) => {
				if (item === 'genFrontFileNames') {
					const obj = {}
					obj.title = '前端代码'
					obj.es = item
					obj.value = []
					const value = res[item]
					value.forEach((item) => {
						obj.value.push({
							title: item,
							checked: false
						})
					})
					genFileNames.push(obj)
				} else if (item === 'genBackendFileNames') {
					const obj = {}
					obj.title = '后端代码'
					obj.es = item
					obj.value = []
					const value = res[item]
					value.forEach((item) => {
						obj.value.push({
							title: item,
							checked: false
						})
					})
					genFileNames.push(obj)
				} else {
					const obj = {}
					obj.title = 'SQL文件'
					obj.es = item
					obj.value = []
					const value = res[item]
					value.forEach((item) => {
						obj.value.push({
							title: item,
							checked: false
						})
					})
					genFileNames.push(obj)
				}
			})
			data.genFileNames = [...genFileNames]
			loadingSpinning.value = false
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}

	const saveCodeOption = () => {
		let codeOption = {}
		data.genFileNames.forEach((item) => {
			const value = item.value
			value.forEach((it) => {
				if (it.checked) {
					if (codeOption[item.es]) {
						codeOption[item.es].push(it.title)
					} else {
						codeOption[item.es] = []
						codeOption[item.es].push(it.title)
					}
				}
			})
		})

		const emitObj = Object.assign({}, data.recordObj, codeOption)

		if (
			(!codeOption.genFrontFileNames && !codeOption.genBackendFileNames && !codeOption.genSqlFileNames) ||
			((codeOption.genFrontFileNames ? codeOption.genFrontFileNames.length == 0 : true) &&
				(codeOption.genBackendFileNames ? codeOption.genBackendFileNames.length == 0 : true) &&
				(codeOption.genSqlFileNames ? codeOption.genSqlFileNames.length == 0 : true))
		) {
			emit('nullCodeEmit')
			return
		}
		emit('saveCodeEmit', emitObj)
	}

	defineExpose({
		onOpen,
		onClose
	})
</script>
<style type="less" scoped>
	.gen-preview-content {
		height: calc(100vh - 160px);
		overflow: auto;
	}
</style>
