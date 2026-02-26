<template>
	<div class="node-wrap">
		<div class="node-wrap-box" @click="show">
			<div class="title" style="background: #ff943e">
				<user-outlined class="icon" />
				<span>{{ childNode.title }}</span>
				<close-outlined class="close" @click.stop="delNode()" />
			</div>
			<div class="content">
				<span v-if="toText(childNode)">{{ toText(childNode) }}</span>
				<span v-else class="placeholder">请选择</span>
			</div>
		</div>
		<add-node v-model="childNode.childNode"></add-node>

		<!-- 抽屉 -->
		<xn-form-container v-model:visible="drawer" :destroy-on-close="true" :width="700" :body-style="{ 'padding-top': '0px' }">
			<template #title>
				<div class="node-wrap-drawer__title">
					<label v-if="!isEditTitle" @click="editTitle"
						>{{ form.title }}<edit-outlined class="node-wrap-drawer__title-edit"
					/></label>
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
					<a-tab-pane key="1" tab="人员配置" force-render>
						<div v-show="!nodeLegal" style="margin-bottom: 10px">
							<a-alert message="请配置节点相关人员！" type="error" />
						</div>
						<a-form label-position="top" layout="vertical">
							<a-form-item label="人员类型选择">
								<a-radio-group v-model:value="userSelectionType" style="width: 100%">
									<a-row style="padding-bottom: 10px">
										<a-col :span="8">
											<a-radio value="ORG" @click="selectionClick('ORG')">机构</a-radio>
										</a-col>
										<a-col :span="8">
											<a-radio value="ROLE" @click="selectionClick('ROLE')">角色</a-radio>
										</a-col>
										<a-col :span="8">
											<a-radio value="POSITION" @click="selectionClick('POSITION')">职位</a-radio>
										</a-col>
									</a-row>
									<a-row style="padding-bottom: 10px">
										<a-col :span="8">
											<a-radio value="USER" @click="selectionClick('USER')">用户</a-radio>
										</a-col>
										<a-col :span="8">
											<a-radio value="ORG_LEADER" @click="selectionClick('ORG_LEADER')">部门主管</a-radio>
										</a-col>
										<a-col :span="8">
											<a-radio value="SUPERVISOR" @click="selectionClick('SUPERVISOR')">直属主管</a-radio>
										</a-col>
									</a-row>
									<a-row style="padding-bottom: 10px">
										<!-- 先注释，因为表单还没增加人员选择器
										<a-col :span="8">
											<a-radio value="FORM_USER" @click="selectionClick('FORM_USER')">表单内的人</a-radio>
										</a-col>
										-->
										<a-col :span="8">
											<a-radio value="INITIATOR" @click="selectionClick('INITIATOR')">发起人</a-radio>
										</a-col>
										<a-col :span="8"></a-col>
									</a-row>
								</a-radio-group>

								<div v-if="seleType" style="margin-top: 20px">
									<a-button type="primary" @click="openSelector">选择</a-button>
								</div>
								<div style="margin-top: 20px">
									<prop-tag
										v-if="form.properties.participateInfo.length > 0 && form.properties.participateInfo[0].value !== ''"
										:tag-list="form.properties.participateInfo[0]"
									/>
								</div>
							</a-form-item>
						</a-form>
					</a-tab-pane>
					<a-tab-pane key="2" tab="审批配置" force-render>
						<a-form label-position="top" layout="vertical">
							<div v-show="!nodeLegal" style="margin-bottom: 10px">
								<a-alert message="请配置节点相关人员！" type="error" />
							</div>
							<a-form-item label="任务节点类型">
								<a-radio-group v-model:value="form.properties.configInfo.userTaskType">
									<a-radio
										v-for="userTaskType in userTaskTypeList"
										:key="userTaskType.value"
										:value="userTaskType.value"
										>{{ userTaskType.label }}</a-radio
									>
								</a-radio-group>
							</a-form-item>
							<a-form-item label="多人审批类型">
								<a-radio-group v-model:value="form.properties.configInfo.userTaskMulApproveType">
									<a-radio
										v-for="userTaskMulApproveType in userTaskMulApproveTypeList"
										:key="userTaskMulApproveType.value"
										:value="userTaskMulApproveType.value"
										>{{ userTaskMulApproveType.label }}
									</a-radio>
									<br />
								</a-radio-group>
							</a-form-item>
							<a-form-item label="审批人员为空时">
								<a-radio-group v-model:value="form.properties.configInfo.userTaskEmptyApproveType">
									<a-radio
										v-for="userTaskEmptyApproveType in userTaskEmptyApproveTypeList"
										:key="userTaskEmptyApproveType.value"
										:value="userTaskEmptyApproveType.value"
										@change="userTaskEmptyApproveTypeChange"
										>{{ userTaskEmptyApproveType.label }}
									</a-radio>
								</a-radio-group>
							</a-form-item>
							<a-form-item
								v-if="form.properties.configInfo.userTaskEmptyApproveType === 'AUTO_TURN'"
								label="审批人为空时转交人"
							>
								<p><a-button type="primary" @click="seleApproveUser">选择人员</a-button></p>
								<a-tag
									v-if="form.properties.configInfo.userTaskEmptyApproveUserArray.length > 0"
									color="orange"
									closable
									@close="closeApproveUserTag"
								>
									{{ form.properties.configInfo.userTaskEmptyApproveUserArray[0].name }}</a-tag
								>
							</a-form-item>
						</a-form>
					</a-tab-pane>
					<a-tab-pane key="3" tab="按钮配置" force-render>
						<div class="font-span">
							<span>参与者可以看见哪些按钮</span>
						</div>
						<prop-button-info
							ref="propButtonInfo"
							:button-info="selectedButtonInfo"
							:show-button="defaultButtonkey"
							:no-checked="userTaskNoCheckedButtonkey"
						/>
					</a-tab-pane>
					<a-tab-pane key="4" tab="字段配置" force-render>
						<div class="font-span">
							<span>参与者可以看见或操作哪些字段</span>
						</div>
						<prop-field-info
							ref="propFieldInfo"
							:field-info="fieldInfo"
							:default-field-model="defaultFieldModel"
							:form-field-list-value="formFieldListValue"
						/>
					</a-tab-pane>
				</a-tabs>
			</a-layout-content>
			<template #footer>
				<a-button type="primary" style="margin-right: 8px" @click="save">保存</a-button>
				<a-button @click="drawer = false">取消</a-button>
			</template>
		</xn-form-container>
		<role-selector-plus
			ref="roleselectorPlus"
			page-url="/flw/model/roleSelector"
			org-url="/flw/model/orgTreeSelector"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<user-selector-plus
			ref="userselectorPlus"
			page-url="/flw/model/userSelector"
			org-url="/flw/model/orgTreeSelector"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<pos-selector-plus
			ref="posselectorPlus"
			page-url="/flw/model/positionSelector"
			org-url="/flw/model/orgTreeSelector"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<org-selector-plus
			ref="orgselectorPlus"
			page-url="/flw/model/orgListSelector"
			org-url="/flw/model/orgTreeSelector"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<form-user-selector
			ref="formuserselector"
			:form-field-list="formFieldListValue"
			:show-field="''"
			@click="formuserClick"
		/>
		<user-selector-plus
			ref="userselectorApprove"
			page-url="/flw/model/userSelector"
			org-url="/flw/model/orgTreeSelector"
			:radio-model="true"
			@onBack="callBackApprove"
		/>
	</div>
</template>

<script>
	import config from '@/components/XnWorkflow/nodes/config/config'
	import addNode from './addNode.vue'
	import propButtonInfo from './prop/propButtonInfo.vue'
	import propFieldInfo from './prop/propFieldInfo.vue'
	import formUserSelector from './prop/formUserSelector.vue'
	import roleSelectorPlus from '@/components/Selector/roleSelectorPlus.vue'
	import userSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import posSelectorPlus from '@/components/Selector/posSelectorPlus.vue'
	import orgSelectorPlus from '@/components/Selector/orgSelectorPlus.vue'
	import propTag from './prop/propTag.vue'

	export default {
		components: {
			addNode,
			propButtonInfo,
			propFieldInfo,
			formUserSelector,
			roleSelectorPlus,
			userSelectorPlus,
			posSelectorPlus,
			orgSelectorPlus,
			propTag
		},
		// inject: ['select'],
		props: {
			modelValue: { type: Object, default: () => {} },
			formFieldListValue: { type: Array, default: () => [] },
			recordData: { type: Object, default: () => {} }
		},
		data() {
			return {
				nodeLegal: false,
				childNode: {},
				drawer: false,
				isEditTitle: false,
				form: {},
				selectedButtonInfo: [],
				activeKey: '1',
				defaultButtonkey: JSON.parse(JSON.stringify(config.button.userTaskDefaultButtonkey)),
				userTaskNoCheckedButtonkey: JSON.parse(JSON.stringify(config.button.userTaskNoCheckedButtonkey)),
				defaultFieldModel: JSON.parse(JSON.stringify(config.field.userTaskFieldModel)),
				fieldInfo: [],
				tagList: [],
				seleType: false,
				// 新配置
				// 用户选择类型
				userSelectionType: '',
				userSelectionTypeList: [
					{
						label: '机构',
						value: 'ORG'
					},
					{
						label: '角色',
						value: 'ROLE'
					},
					{
						label: '职位',
						value: 'POSITION'
					},
					{
						label: '用户',
						value: 'USER'
					},
					{
						label: '部门主管',
						value: 'ORG_LEADER'
					},
					{
						label: '直属主管',
						value: 'SUPERVISOR'
					},
					/*{
						label: '表单内的人',
						value: 'FORM_USER'
					},*/
					{
						label: '发起人',
						value: 'INITIATOR'
					}
				],
				// 任务节点类型
				userTaskTypeList: [
					{
						label: '人工审批',
						value: 'ARTIFICIAL'
					},
					{
						label: '自动通过',
						value: 'COMPLETE'
					},
					{
						label: '自动拒绝',
						value: 'REJECT'
					}
				],
				// 多人审批时类型
				userTaskMulApproveTypeList: [
					{
						label: '依次审批',
						value: 'SEQUENTIAL'
					},
					{
						label: '会签（须所有审批人同意）',
						value: 'COUNTERSIGN'
					},
					{
						label: '或签（一名审批人同意或拒绝即可）',
						value: 'ORSIGN'
					}
				],
				// 审批人为空时类型
				userTaskEmptyApproveTypeList: [
					{
						label: '自动通过',
						value: 'AUTO_COMPLETE'
					},
					{
						label: '自动转交给某个人',
						value: 'AUTO_TURN'
					}
				],
				// 审批人为空时转交人
				userTaskEmptyApproveUser: ''
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
				this.form = {}
				this.form = JSON.parse(JSON.stringify(this.childNode))
				this.selectedButtonInfo = this.form.properties.buttonInfo
				this.fieldInfo = this.form.properties.fieldInfo
				this.drawer = true
				// 设置默认选中的用户类型单选 userSelectionType
				if (this.form.properties.participateInfo.length > 0) {
					this.userSelectionType = this.form.properties.participateInfo[0].key
				} else {
					// 设置发起人
					this.userSelectionType = 'INITIATOR'
					this.form.properties.participateInfo = [{ key: 'INITIATOR', label: '发起人', value: '发起人:${INITIATOR}' }]
				}
				// 校验状态
				this.isNodeLegal()
				// 设置第一个选项卡打开选择器的按钮是否展示
				if (
					this.userSelectionType === 'USER' ||
					this.userSelectionType === 'ROLE' ||
					this.userSelectionType === 'ORG' ||
					this.userSelectionType === 'POSITION' ||
					this.userSelectionType === 'FORM_USER'
				) {
					this.seleType = true
				}
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
				if (this.form.id === '') {
					this.form.id = this.$TOOL.snowyUuid()
				}
				this.form.properties.buttonInfo = this.$refs.propButtonInfo.selectedButtonKeyList()
				this.form.properties.fieldInfo = this.$refs.propFieldInfo.selectedFieldList()
				if (this.isNodeLegal()) {
					this.form.dataLegal = true
					// eslint-disable-next-line vue/require-explicit-emits
					this.$emit('update:modelValue', this.form)
					this.drawer = false
				}
			},
			selectionClick(value) {
				const type = value
				const result = []
				const obj = {}
				obj.key = type
				// 部门主管 直属主管 发起人
				if (type === 'ORG_LEADER' || type === 'SUPERVISOR' || type === 'INITIATOR') {
					this.seleType = false
					obj.label = this.userSelectionTypeList.filter((item) => item.value === type)[0].label
					obj.value = this.userSelectionTypeList.filter((item) => item.value === type)[0].label + ':${' + type + '}'
				} else {
					this.seleType = true
					obj.label = ''
					obj.value = ''
				}
				result.push(obj)
				this.form.properties.participateInfo = result
				this.isNodeLegal()
			},
			// 打开各种选择器
			openSelector() {
				let type = this.userSelectionType
				let data = this.form.properties.participateInfo
				if (type === 'ROLE') {
					this.$refs.roleselectorPlus.showRolePlusModal(data)
				}
				if (type === 'USER') {
					this.$refs.userselectorPlus.showUserPlusModal(data)
				}
				if (type === 'POSITION') {
					this.$refs.posselectorPlus.showPosPlusModal(data)
				}
				if (type === 'ORG') {
					this.$refs.orgselectorPlus.showOrgPlusModal(data)
				}
				if (type === 'FORM_USER') {
					// 表单内的人
					// eslint-disable-next-line no-undefined
					if (data[0] === undefined) {
						this.$refs.formuserselector.showFormUserModal()
					} else {
						const dataValue = data[0].value.replace('表单内的人:${', '').replace('}', '')
						this.$refs.formuserselector.showFormUserModal(dataValue)
					}
				}
			},
			delNode() {
				// eslint-disable-next-line vue/require-explicit-emits
				this.$emit('update:modelValue', this.childNode.childNode)
				// eslint-disable-next-line vue/require-explicit-emits
				this.$emit('deleteParalle')
			},
			delUser(index) {
				this.form.nodeUserList.splice(index, 1)
			},
			// 选择转交人
			seleApproveUser() {
				const data = [this.form.properties.configInfo.userTaskEmptyApproveUser]
				this.$refs.userselectorApprove.showUserPlusModal(data)
			},
			// 选择转交人回调
			callBackApprove(value) {
				this.form.properties.configInfo.userTaskEmptyApproveUser = value[0].id
				this.form.properties.configInfo.userTaskEmptyApproveUserArray = value
			},
			// 清除转交人
			closeApproveUserTag() {
				this.form.properties.configInfo.userTaskEmptyApproveUser = ''
				this.form.properties.configInfo.userTaskEmptyApproveUserArray = []
			},
			// 点击自动转交给某人单选时
			userTaskEmptyApproveTypeChange(value) {
				const type = value.target.value
				if (type === 'AUTO_TURN') {
					// 赋值默认的管理员
					this.form.properties.configInfo.userTaskEmptyApproveUser = JSON.parse(this.recordData.extJson)[0].id
					this.form.properties.configInfo.userTaskEmptyApproveUserArray = JSON.parse(this.recordData.extJson)
				} else {
					// 置为空
					this.closeApproveUserTag()
				}
			},

			// 校验节点是否合法
			isNodeLegal() {
				if (this.form.properties.participateInfo.length > 0) {
					if (this.form.properties.participateInfo[0].label === '') {
						this.nodeLegal = false
						return false
					} else {
						// 再看看默认转交人是否OK
						// eslint-disable-next-line no-lonely-if
						if (this.form.properties.configInfo.userTaskEmptyApproveType === 'AUTO_TURN') {
							if (this.form.properties.configInfo.userTaskEmptyApproveUser !== '') {
								this.nodeLegal = true
								return true
							} else {
								this.nodeLegal = false
								return false
							}
						} else {
							this.nodeLegal = true
							return true
						}
					}
				} else {
					this.nodeLegal = false
					return false
				}
			},
			selectHandle(type, data) {
				const value = []
				if (data.length > 0) {
					data.forEach((item) => {
						value.push(item.id)
					})
				}
				if (type === 1) {
					this.$refs.userselector.showUserModal(value)
				}
				if (type === 2) {
					this.$refs.roleselector.showRoleModal(value)
				}
				// this.select(type, data)
			},
			// 公共回调方法，因为它们返回的数据结构一致
			callBack(value) {
				if (value.label === []) {
					this.nodeLegal = false
				} else {
					this.form.properties.participateInfo[0] = value
				}
				this.isNodeLegal()
			},
			// 表单内的人选择器回调
			formuserClick(value) {
				// eslint-disable-next-line no-undefined
				if (value) {
					const result = []
					const obj = {}
					obj.key = 'FORM_USER'
					obj.label = '表单内的人'
					obj.value = '表单内的人:${' + value.model + '}'
					result.push(obj)
					this.form.properties.participateInfo = result
				} else {
					this.nodeLegal = false
				}
				this.isNodeLegal()
			},
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
