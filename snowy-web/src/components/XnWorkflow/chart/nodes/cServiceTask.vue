<template>
	<div class="node-wrap">
		<div class="node-wrap-box">
			<div class="title" style="background: #3296fa">
				<send-outlined class="icon" />
				<span>{{ childNode.title }}</span>
			</div>
			<div class="content">
				<span v-if="toText(childNode)">{{ toText(childNode) }}</span>
				<span v-else class="placeholder">未选择人员</span>
			</div>
		</div>
		<add-nodes v-model="childNode.childNode"></add-nodes>
	</div>
</template>

<script>
	import addNodes from './cAddNode.vue'

	export default {
		components: {
			addNodes
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
			modelValue() {
				this.childNode = this.modelValue
			}
		},
		mounted() {
			this.childNode = this.modelValue
		},
		methods: {
			toText(childNode) {
				if (JSON.stringify(childNode) !== '{}') {
					const participateInfo = childNode.properties.participateInfo
					if (participateInfo.length > 0) {
						let resultArray = []
						if (participateInfo[0].label.indexOf(',') !== -1) {
							resultArray = participateInfo[0].label.split(',')
						} else {
							resultArray.push(participateInfo[0].label)
						}
						return resultArray.toString()
					} else {
						return false
					}
				} else {
					return false
				}
			}
		}
	}
</script>
