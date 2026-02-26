<template>
	<div>
		<a-input
			v-model:value="values"
			:style="{ width: record.options.width }"
			:placeholder="record.options.placeholder"
			:bordered="false"
			:disabled="record.options.disabled"
		/>
	</div>
</template>

<script setup name="xnInput">
	import { ref } from 'vue'
	const props = defineProps(['record', 'config', 'value'])
	const values = ref(props.value)
</script>
