<template>
	<div class="node-wrap">
		<div class="node-wrap-box">
			<div class="title" style="background: #ff943e">
				<user-outlined class="icon" />
				<span>{{ childNode.title }}</span>
			</div>
			<div class="content">
				<span v-if="toText(childNode)">{{ toText(childNode) }}</span>
				<span v-else class="placeholder">未选择审批人</span>
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
					const strArray = this.toTag(childNode.properties.participateInfo[0])
					if (strArray.length > 0) {
						let value = ''
						// eslint-disable-next-line no-plusplus
						for (let i = 0; i < strArray.length; i++) {
							if (strArray.length === i + 1) {
								value = value + strArray[i]
							} else {
								value = value + strArray[i] + '，'
							}
						}
						return value
					} else {
						return false
					}
				} else {
					return false
				}
			},
			toTag(participateInfo) {
				// eslint-disable-next-line no-undefined
				if (participateInfo === undefined) {
					return []
				}
				if (participateInfo.label === '') {
					return []
				} else {
					let resultArray = []
					if (participateInfo.label.indexOf(',') !== -1) {
						resultArray = participateInfo.label.split(',')
					} else {
						resultArray.push(participateInfo.label)
					}
					return resultArray
				}
			}
		}
	}
</script>
