<template>
	<a-card>
		<Workflow
			v-model="modelDesignData.modelValue"
			:form-field-list-value="formFieldListValue"
			:record-data="recordData"
		></Workflow>
	</a-card>
</template>
<script setup name="modelDesign">
	import Workflow from '@/components/XnWorkflow/index.vue'

	// 模型设计器的值
	const modelDesignData = ref({})
	// 模型主题值
	const recordData = ref({})

	const formFieldListValue = ref([])
	// 获取值
	const getValue = () => {
		return modelDesignData.value.modelValue
	}
	// 回显值
	const setValue = (value, record) => {
		modelDesignData.value = value
		recordData.value = record
	}
	// 传字段的数据过来
	const setFormFieldListValue = (value) => {
		formFieldListValue.value = JSON.parse(JSON.stringify(value))
	}
	// 抛出钩子
	defineExpose({
		getValue,
		setValue,
		setFormFieldListValue
	})
</script>

<style></style>
