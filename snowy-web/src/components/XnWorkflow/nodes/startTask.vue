<template>
	<div class="node-wrap">
		<div class="node-wrap-box start-node" @click="show">
			<div class="title" style="background: #576a95">
				<user-outlined class="icon" />
				<span>{{ childNode.title }}</span>
			</div>
			<div class="content">
				<span v-if="toText(childNode)">{{ toText(childNode) }}</span>
				<span v-else class="placeholder">请配置字段与按钮</span>
			</div>
		</div>
		<add-node v-model="childNode.childNode"></add-node>
		<xn-form-container v-model:visible="drawer" :destroy-on-close="true" :width="700" :body-style="{ 'padding-top': '0px' }">
			<template #title>
				<div class="node-wrap-drawer__title">
					<label v-if="!isEditTitle" @click="editTitle">
						{{ form.title }}
						<edit-outlined class="node-wrap-drawer__title-edit" />
					</label>
					<a-input
						v-if="isEditTitle"
						ref="nodeTitle"
						v-model:value="form.title"
						allow-clear
						@blur="saveTitle"
						@keyup.enter="saveTitle"
					></a-input>
				</div>
			</template>
			<a-layout-content>
				<a-tabs v-model:activeKey="activeKey">
					<a-tab-pane key="1" tab="按钮配置" force-render>
						<div class="font-span">
							<span>参与者可以看见哪些按钮</span>
						</div>
						<prop-button-info
							ref="propButtonInfo"
							:button-info="selectedButtonInfo"
							:show-button="startTaskDefaultButtonkey"
							:no-checked="startTaskNoCheckedButtonkey"
						/>
					</a-tab-pane>
					<a-tab-pane key="2" tab="字段配置" force-render>
						<div class="font-span">
							<span>参与者可以看见表单哪些字段</span>
						</div>
						<prop-field-info ref="propFieldInfo" :field-info="fieldInfo" :form-field-list-value="formFieldListValue" />
					</a-tab-pane>
				</a-tabs>
			</a-layout-content>
			<template #footer>
				<a-button type="primary" style="margin-right: 8px" @click="save">保存</a-button>
				<a-button @click="drawer = false">取消</a-button>
			</template>
		</xn-form-container>
	</div>
</template>

<script>
	import config from '@/components/XnWorkflow/nodes/config/config'
	import addNode from './addNode.vue'
	import propButtonInfo from './prop/propButtonInfo.vue'
	import propFieldInfo from './prop/propFieldInfo.vue'

	export default {
		components: {
			addNode,
			propButtonInfo,
			propFieldInfo
		},
		props: {
			modelValue: { type: Object, default: () => {} },
			formFieldListValue: { type: Array, default: () => [] }
		},
		data() {
			return {
				childNode: {},
				drawer: false,
				isEditTitle: false,
				form: {},
				activeKey: '1',
				selectedButtonInfo: [],
				startTaskDefaultButtonkey: JSON.parse(JSON.stringify(config.button.startTaskDefaultButtonkey)),
				startTaskNoCheckedButtonkey: JSON.parse(JSON.stringify(config.button.startTaskNoCheckedButtonkey)),
				fieldInfo: []
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
			show() {
				this.form = JSON.parse(JSON.stringify(this.childNode))
				this.isEditTitle = false
				this.drawer = true
				this.selectedButtonInfo = this.form.properties.buttonInfo
				this.fieldInfo = this.form.properties.fieldInfo
			},
			editTitle() {
				this.isEditTitle = true
				this.$nextTick(() => {
					this.$refs.nodeTitle.focus()
				})
			},
			saveTitle() {
				this.isEditTitle = false
			},
			save() {
				this.form.id = this.$TOOL.snowyUuid()
				this.form.properties.buttonInfo = this.$refs.propButtonInfo.selectedButtonKeyList()
				this.form.properties.fieldInfo = this.$refs.propFieldInfo.selectedFieldList()
				this.form.dataLegal = true
				// eslint-disable-next-line vue/require-explicit-emits
				this.$emit('update:modelValue', this.form)
				this.drawer = false
			},
			// eslint-disable-next-line no-unused-vars
			toText(childNode) {
				if (childNode.dataLegal) {
					return '系统自动配置参与人'
				} else {
					return false
				}
			}
		}
	}
</script>

<style scoped>
	.font-span {
		padding-bottom: 8px;
	}
</style>
