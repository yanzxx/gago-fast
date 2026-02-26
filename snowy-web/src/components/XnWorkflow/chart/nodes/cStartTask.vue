<template>
	<div class="node-wrap">
		<div class="node-wrap-box start-node">
			<div class="title" style="background: #576a95">
				<user-outlined class="icon" />
				<span>{{ childNode.title }}</span>
			</div>
			<div class="content">
				<span>{{ toText(childNode) }}</span>
			</div>
			<!--
			<a-popover>
				<template #content>
					<div v-if="childNode && childNode.properties && childNode.properties.commentList">
						<a-alert type="success" banner :show-icon="false">
							<template #description>
								<div v-for="comment in childNode.properties.commentList">
									<div>申请人：{{comment.userName}}</div>
									<div>操作时间：{{comment.approveTime}}</div>
									<a-divider style="margin: 10px 0" v-if="childNode.properties.commentList > 1"/>
								</div>
							</template>
						</a-alert>
					</div>
				</template>
			<div class="title" style="background: #576a95">
				<user-outlined class="icon" />
				<span>{{ childNode.title }}</span>
				<check-circle-outlined class="success" v-if="childNode && childNode.properties && childNode.properties.commentList && childNode.properties.commentList.length > 0"/>
			</div>
			<div class="content">
				<span>{{ toText(childNode) }}</span>
			</div>
			</a-popover>
			-->
		</div>
		<add-nodes v-model="childNode.childNode" />
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
			toText() {
				return '系统自动配置参与人'
			}
		}
	}
</script>
