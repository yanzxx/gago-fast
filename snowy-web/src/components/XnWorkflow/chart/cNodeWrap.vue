<template>
	<start-events v-if="childNode.type === 'startEvent'" v-model="childNode" />
	<start-tasks v-if="childNode.type === 'startTask'" v-model="childNode" />
	<user-tasks v-if="childNode.type === 'userTask'" v-model="childNode" />
	<service-tasks v-if="childNode.type === 'serviceTask'" v-model="childNode"></service-tasks>
	<exclusive-gateways v-if="childNode.type === 'exclusiveGateway'" v-model="childNode">
		<template #default="slot">
			<c-node-wrap v-if="slot.node" v-model="slot.node.childNode"></c-node-wrap>
		</template>
	</exclusive-gateways>
	<parallel-gateways v-if="childNode.type === 'parallelGateway'" v-model="childNode">
		<template #default="slot">
			<c-node-wrap v-if="slot.node" v-model="slot.node.childNode"></c-node-wrap>
		</template>
	</parallel-gateways>
	<c-node-wrap v-if="childNode.childNode" v-model="childNode.childNode"></c-node-wrap>
</template>

<script>
	import startEvents from './nodes/cStartEvent.vue'
	import startTasks from './nodes/cStartTask.vue'
	import userTasks from './nodes/cUserTask.vue'
	import exclusiveGateways from './nodes/cExclusiveGateway.vue'
	import parallelGateways from './nodes/cParallelGateway.vue'
	import serviceTasks from './nodes/cServiceTask.vue'

	export default {
		components: {
			startEvents,
			startTasks,
			userTasks,
			exclusiveGateways,
			parallelGateways,
			serviceTasks
		},
		props: {
			modelValue: { type: Object, default: () => {} }
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
		}
	}
</script>
