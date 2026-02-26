<template>
	<xn-form-container
		:title="formData.id ? '编辑区划' : '增加区划'"
		:width="550"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="上级区划：" name="parentId">
				<a-tree-select
					v-model:value="formData.parentCode"
					style="width: 100%"
					:dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
					placeholder="请选择上级区划"
					allow-clear
					tree-default-expand-all
					:tree-data="treeData"
					:field-names="{
						children: 'children',
						label: 'name',
						value: 'code'
					}"
					@change="onParentChange"
					selectable="false"
					tree-line
				/>
			</a-form-item>
			<a-form-item label="区划名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入区划名称" allow-clear />
			</a-form-item>
			<a-form-item label="区划代码：" name="sortCode">
				<a-input-number style="width: 100%" v-model:value="formData.code" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
		<user-selector-plus
			ref="UserSelectorPlus"
			page-url="/sys/org/userSelector"
			org-url="/sys/org/orgTreeSelector"
			:radio-model="true"
			@onBack="userBack"
		/>
	</xn-form-container>
</template>

<script setup name="regionForm">
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import regionApi from '@/api/sys/regionApi'
	import userSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import tool from '@/utils/tool'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	let visible = $ref(false)
	let UserSelectorPlus = ref()
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	// 定义区划元素
	const treeData = ref([])
	const extJson = ref([])
	const submitLoading = ref(false)

	// 递归根据 id 查找节点
	const findNodeById = (nodes, id) => {
		for (const node of nodes || []) {
			if (String(node.id) === String(id)) return node
			const child = findNodeById(node.children, id)
			if (child) return child
		}
		return null
	}

	// 父级选中变更时，填充父级名称
	const onParentChange = (value, label) => {
		formData.value.parentName = Array.isArray(label) ? label?.[0] : label
	}

	// 打开抽屉
	const onOpen = (record, parentId) => {
		visible = true
		extJson.value = ref([])
		formData.value = {
			sortCode: 99
		}
		if (record) {
			const param = {
				id: record.id
			}
			regionApi.regionDetail(param).then((data) => {
				formData.value = Object.assign({}, data)
				extJson.value = JSON.parse(formData.value.extJson) || []
			})
		}
		// 获取区划树并加入顶级
		regionApi.regionTree().then((res) => {
			// 包装顶级
			treeData.value = [
				{
					id: 0,
					code: '0',
					parentId: '-1',
					name: '顶级',
					children: res
				}
			]
			// 若携带上级 id，则根据 id 反查 code 与名称
			if (parentId) {
				const node = findNodeById(res, parentId)
				if (node) {
					formData.value.parentCode = node.code
					formData.value.parentName = node.name
				}
			}
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible = false
	}
	// 默认要校验的
	const formRules = {
		name: [required('请输入区划名称')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			// 若未能通过 change 事件获取到父级名称，则根据树再次回填
			if (!formData.value.parentName && formData.value.parentCode && treeData.value?.[0]?.children) {
				const stack = [...treeData.value[0].children]
				while (stack.length) {
					const node = stack.pop()
					if (!node) break
					if (String(node.code) === String(formData.value.parentCode)) {
						formData.value.parentName = node.name
						break
					}
					if (node.children) stack.push(...node.children)
				}
			}
			regionApi
				.submitForm(formData.value, !formData.value.id)
				.then(() => {
					visible = false
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
