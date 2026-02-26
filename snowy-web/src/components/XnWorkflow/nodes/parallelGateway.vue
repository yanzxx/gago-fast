<template>
	<div class="branch-wrap">
		<div class="branch-box-wrap">
			<div class="branch-box">
				<a-button class="add-branch" type="primary" shape="round" @click="addTerm">添加并行</a-button>
				<div v-for="(item, index) in childNode.conditionNodeList" :key="index" class="col-box">
					<div class="condition-node">
						<div class="condition-node-box">
							<user-task
								v-model="childNode.conditionNodeList[index]"
								:form-field-list-value="formFieldListValue"
								:recordData="recordData"
								@deleteParalle="delTerm(index)"
							></user-task>
						</div>
					</div>
					<slot v-if="item.childNode" :node="item"></slot>
					<div v-if="index === 0" class="top-left-cover-line"></div>
					<div v-if="index === 0" class="bottom-left-cover-line"></div>
					<div v-if="index === childNode.conditionNodeList.length - 1" class="top-right-cover-line"></div>
					<div v-if="index === childNode.conditionNodeList.length - 1" class="bottom-right-cover-line"></div>
				</div>
			</div>
			<add-node v-model="childNode.childNode" :parent-data="childNode"></add-node>
		</div>
	</div>
</template>

<script>
	import addNode from './addNode.vue'
	import userTask from './userTask.vue'
	import config from '@/components/XnWorkflow/nodes/config/config'

	export default {
		components: {
			addNode,
			userTask
		},
		props: {
			modelValue: { type: Object, default: () => {} },
			formFieldListValue: { type: Array, default: () => [] },
			recordData: { type: Object, default: () => {} }
		},
		data() {
			return {
				childNode: {},
				drawer: false,
				isEditTitle: false,
				index: 0,
				form: {}
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
			addTerm() {
				const len = this.childNode.conditionNodeList.length + 1
				// 创建主节点
				const nodeModel = JSON.parse(JSON.stringify(config.nodeModel.node))
				nodeModel.id = this.$TOOL.snowyUuid()
				nodeModel.type = 'userTask'
				;(nodeModel.title = `审批人${len}`),
					(nodeModel.priorityLevel = len),
					(nodeModel.conditionNodeList = []),
					(nodeModel.childNode = {})
				// 创建 configInfo
				const configInfo = JSON.parse(JSON.stringify(config.nodeConfigInfo.userTaskConfigInfo))
				nodeModel.properties.configInfo = configInfo

				this.childNode.conditionNodeList.push(nodeModel)
			},
			delTerm(index) {
				this.childNode.conditionNodeList.splice(index, 1)
				if (this.childNode.conditionNodeList.length == 1) {
					if (this.childNode.childNode) {
						// 这是{}
						if (JSON.stringify(this.childNode.conditionNodeList[0].childNode) !== '{}') {
							this.reData(this.childNode.conditionNodeList[0].childNode, this.childNode.childNode)
						} else {
							this.childNode.conditionNodeList[0].childNode = this.childNode.childNode
						}
					}
					this.$emit('update:modelValue', this.childNode.conditionNodeList[0].childNode)
				}
			},
			reData(data, addData) {
				if (JSON.stringify(data) !== '{}') {
					data.childNode = addData
				} else {
					this.reData(data.childNode, addData)
				}
			}
		}
	}
</script>

<style></style>
