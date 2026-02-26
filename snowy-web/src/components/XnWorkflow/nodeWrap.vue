<template>
	<start-event v-if="childNode.type === 'startEvent'" v-model="childNode" />

	<start-task v-if="childNode.type === 'startTask'" v-model="childNode" :formFieldListValue="formFieldListValue" />

	<user-task
		v-if="childNode.type === 'userTask'"
		v-model="childNode"
		:formFieldListValue="formFieldListValue"
		:recordData="recordData"
	/>

	<service-task v-if="childNode.type === 'serviceTask'" v-model="childNode"></service-task>

	<exclusive-gateway
		v-if="childNode.type === 'exclusiveGateway'"
		v-model="childNode"
		:formFieldListValue="formFieldListValue"
	>
		<template #default="slot">
			<node-wrap
				v-if="slot.node"
				v-model="slot.node.childNode"
				:formFieldListValue="formFieldListValue"
				:recordData="recordData"
			></node-wrap>
		</template>
	</exclusive-gateway>

	<parallel-gateway
		v-if="childNode.type === 'parallelGateway'"
		v-model="childNode"
		:formFieldListValue="formFieldListValue"
		:recordData="recordData"
	>
		<template #default="slot">
			<node-wrap
				v-if="slot.node"
				v-model="slot.node.childNode"
				:formFieldListValue="formFieldListValue"
				:recordData="recordData"
			></node-wrap>
		</template>
	</parallel-gateway>

	<node-wrap
		v-if="childNode.childNode"
		v-model="childNode.childNode"
		:formFieldListValue="formFieldListValue"
		:recordData="recordData"
	></node-wrap>
</template>

<script>
	import startEvent from './nodes/startEvent.vue'
	import startTask from './nodes/startTask.vue'
	import userTask from './nodes/userTask.vue'
	import exclusiveGateway from './nodes/exclusiveGateway.vue'
	import parallelGateway from './nodes/parallelGateway.vue'
	import serviceTask from './nodes/serviceTask.vue'

	export default {
		components: {
			startEvent,
			startTask,
			userTask,
			exclusiveGateway,
			parallelGateway,
			serviceTask
		},
		props: {
			modelValue: { type: Object, default: () => {} },
			formFieldListValue: { type: Array, default: () => [] },
			recordData: { type: Object, default: () => {} }
		},
		data() {
			return {
				childNode: {}
			}
		},
		watch: {
			modelValue(val) {
				this.childNode = val
			},
			childNode(val) {
				this.$emit('update:modelValue', val)
			}
		},
		mounted() {
			this.childNode = this.modelValue
		},
		methods: {}
	}
</script>
