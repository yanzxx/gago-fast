<template>
	<xn-form-container
		v-model:visible="drawer"
		:destroy-on-close="true"
		:title="modelTitle"
		:width="700"
		:bodyStyle="{ 'padding-top': '0px' }"
	>
		<a-form ref="noticeFormRef" :model="formData" layout="vertical">
			<a-tabs v-model:activeKey="activeKey">
				<a-tab-pane key="1" tab="人员配置" force-render>
					<div v-show="formVerify" style="margin-bottom: 10px">
						<a-alert message="请切换标签查看，填写完必填项！" type="error" />
					</div>
					<div v-show="alertShow" style="margin-bottom: 10px">
						<a-alert message="未选择任何类型的人员配置，默认所有人均可参与此流程。" type="warning" />
					</div>
					<a-button type="primary" round @click="selectionParticipants('ORG')">
						<plus-outlined />
						选择机构
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('ORG')" />
					<a-divider />
					<a-button type="primary" round @click="selectionParticipants('ROLE')">
						<plus-outlined />
						选择角色
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('ROLE')" />
					<a-divider />
					<a-button type="primary" round @click="selectionParticipants('POSITION')">
						<plus-outlined />
						选择职位
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('POSITION')" />
					<a-divider />
					<a-button type="primary" round @click="selectionParticipants('USER')">
						<plus-outlined />
						选择用户
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('USER')" />
					<a-divider />
				</a-tab-pane>
				<a-tab-pane key="2" tab="基础配置" force-render>
					<a-row :gutter="[10, 0]">
						<a-col :span="12">
							<a-form-item
								label="流水号"
								name="processSnTemplateId"
								:rules="[{ required: true, message: '请选择流水号' }]"
							>
								<a-select
									v-model:value="form.properties.configInfo.processSnTemplateId"
									placeholder="请选择流水号"
									:options="snTemplateOptions"
								>
								</a-select>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item
								label="打印模板"
								name="processPrintTemplateId"
								:rules="[{ required: true, message: '请选择打印模板' }]"
							>
								<a-select
									v-model:value="form.properties.configInfo.processPrintTemplateId"
									placeholder="请选择打印模板"
									:options="printTemplateOptions"
								>
								</a-select>
							</a-form-item>
						</a-col>
					</a-row>
					<a-form-item
						label="标题模板"
						name="processTitleTemplate"
						:rules="[{ required: true, message: '请创造标题模板' }]"
					>
						<template-generator
							ref="processTitleGenerator"
							v-model:inputValue="form.properties.configInfo.processTitleTemplate"
							:fieldInfoLis="fieldInfoLis"
						/>
					</a-form-item>
					<a-form-item
						label="摘要模板"
						name="processAbstractTemplate"
						:rules="[{ required: true, message: '请创造摘要模板' }]"
					>
						<template-generator
							ref="processAbstractGenerator"
							v-model:inputValue="form.properties.configInfo.processAbstractTemplate"
							:fieldInfoLis="fieldInfoLis"
						/>
					</a-form-item>

					<a-form-item label="开启自动去重" name="processEnableAutoDistinct">
						<a-switch v-model:checked="form.properties.configInfo.processEnableAutoDistinct" />
					</a-form-item>
					<a-form-item v-show="form.properties.configInfo.processEnableAutoDistinct" name="processAutoDistinctType">
						<a-radio-group v-model:value="form.properties.configInfo.processAutoDistinctType">
							<a-radio
								v-for="autoDistinctType in processAutoDistinctTypeList"
								:key="autoDistinctType.value"
								:value="autoDistinctType.value"
								>{{ autoDistinctType.label }}</a-radio
							>
						</a-radio-group>
					</a-form-item>
					<a-row :gutter="[10, 0]">
						<a-col :span="12">
							<a-form-item label="开启审批撤销" name="processEnableRevoke">
								<a-switch v-model:checked="form.properties.configInfo.processEnableRevoke" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="开启意见必填" name="processEnableCommentRequired">
								<a-switch v-model:checked="form.properties.configInfo.processEnableCommentRequired" />
							</a-form-item>
						</a-col>
					</a-row>
				</a-tab-pane>
				<a-tab-pane key="3" tab="通知配置" force-render>
					<a-form-item label="开启退回通知" name="processEnableBackNotice">
						<a-switch v-model:checked="formData.processEnableBackNotice" />
					</a-form-item>
					<a-form-item
						label="退回通知方式"
						v-show="formData.processEnableBackNotice"
						name="processBackNoticeChannel"
						:rules="[{ required: formData.processEnableBackNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group v-model:value="formData.processBackNoticeChannel" :options="noticeInfoList" />
					</a-form-item>
					<a-form-item
						label="退回通知模板"
						v-show="formData.processEnableBackNotice"
						name="processBackNoticeTemplate"
						:rules="[{ required: formData.processEnableBackNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="enableBackNoticeRef"
							v-model:inputValue="formData.processBackNoticeTemplate"
							:fieldInfoLis="fieldInfoLis"
						/>
					</a-form-item>

					<a-form-item label="开启待办通知" name="processEnableTodoNotice">
						<a-switch v-model:checked="form.properties.configInfo.processEnableTodoNotice" />
					</a-form-item>
					<a-form-item
						label="待办通知方式"
						v-show="form.properties.configInfo.processEnableTodoNotice"
						name="processTodoNoticeChannel"
						:rules="[{ required: formData.processEnableTodoNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group
							v-model:value="form.properties.configInfo.processTodoNoticeChannel"
							:options="noticeInfoList"
						/>
					</a-form-item>
					<a-form-item
						label="待办通知模板"
						v-show="form.properties.configInfo.processEnableTodoNotice"
						name="processTodoNoticeTemplate"
						:rules="[{ required: formData.processEnableTodoNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="todoNoticeChannelRef"
							:fieldInfoLis="fieldInfoLis"
							v-model:inputValue="form.properties.configInfo.processTodoNoticeTemplate"
						/>
					</a-form-item>

					<a-form-item label="开启抄送通知" name="processEnableCopyNotice">
						<a-switch v-model:checked="form.properties.configInfo.processEnableCopyNotice" />
					</a-form-item>
					<a-form-item
						label="抄送通知方式"
						v-show="form.properties.configInfo.processEnableCopyNotice"
						name="processCopyNoticeChannel"
						:rules="[{ required: formData.processEnableCopyNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group
							v-model:value="form.properties.configInfo.processCopyNoticeChannel"
							:options="noticeInfoList"
						/>
					</a-form-item>
					<a-form-item
						label="抄送通知模板"
						v-show="form.properties.configInfo.processEnableCopyNotice"
						name="processCopyNoticeTemplate"
						:rules="[{ required: formData.processEnableCopyNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="enableCopyNoticeRef"
							:fieldInfoLis="fieldInfoLis"
							v-model:inputValue="form.properties.configInfo.processCopyNoticeTemplate"
						/>
					</a-form-item>

					<a-form-item label="开启完成通知" name="processEnableCompleteNotice">
						<a-switch v-model:checked="form.properties.configInfo.processEnableCompleteNotice" />
					</a-form-item>
					<a-form-item
						label="完成通知方式"
						v-show="form.properties.configInfo.processEnableCompleteNotice"
						name="processCompleteNoticeChannel"
						:rules="[{ required: formData.processEnableCompleteNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group
							v-model:value="form.properties.configInfo.processCompleteNoticeChannel"
							:options="noticeInfoList"
						/>
					</a-form-item>
					<a-form-item
						label="完成通知模板"
						v-show="form.properties.configInfo.processEnableCompleteNotice"
						name="processCompleteNoticeTemplate"
						:rules="[{ required: formData.processEnableCompleteNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="enableCompleteNoticeRef"
							:fieldInfoLis="fieldInfoLis"
							v-model:inputValue="form.properties.configInfo.processCompleteNoticeTemplate"
						/>
					</a-form-item>
				</a-tab-pane>
			</a-tabs>
		</a-form>

		<template #footer>
			<a-button type="primary" style="margin-right: 8px" @click="onFinish">保存</a-button>
			<a-button @click="drawer = false">取消</a-button>
		</template>

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
	</xn-form-container>
</template>

<script>
	import templateGenerator from './nodes/prop/templateGenerator.vue'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import roleSelectorPlus from '@/components/Selector/roleSelectorPlus.vue'
	import userSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import posSelectorPlus from '@/components/Selector/posSelectorPlus.vue'
	import orgSelectorPlus from '@/components/Selector/orgSelectorPlus.vue'
	import propTag from './nodes/prop/propTag.vue'
	import templatePrintApi from '@/api/flw/templatePrintApi'
	import templateSnApi from '@/api/flw/templateSnApi'

	export default {
		components: {
			templateGenerator,
			roleSelectorPlus,
			userSelectorPlus,
			posSelectorPlus,
			orgSelectorPlus,
			propTag
		},
		props: {
			modelValue: { type: Object, default: () => {} },
			formFieldListValue: { type: Array, default: () => [] },
			recordData: { type: Object, default: () => {} }
		},

		data() {
			return {
				noticeInfoList: JSON.parse(JSON.stringify(config.noticeInfoList)),
				// 摘要模板，因为要从传来的字段中取
				abstractStr: '',
				snTemplateOptions: [],
				printTemplateOptions: [],
				// 自动去重类型
				processAutoDistinctTypeList: [
					{
						label: '当审批人和发起人是同一个人，审批自动通过',
						value: 'SAMPLE'
					},
					{
						label: '当同一审批人在流程中连续多次出现时，自动去重',
						value: 'MULTIPLE'
					}
				],
				drawer: false,
				modelTitle: '全局属性',
				activeKey: '1',
				childNode: this.modelValue,
				form: {},
				formData: {},
				fieldInfoLis: [],
				formVerify: false
			}
		},
		computed: {
			// 监听内部数组，选了人员相关，我们就不提示
			alertShow() {
				if (this.form.properties.participateInfo.length > 0) {
					return false
				} else {
					return true
				}
			}
		},
		watch: {
			modelValue(val) {
				this.childNode = val
			},
			childNode(val) {
				this.$emit('update:modelValue', val)
			},
			formFieldListValue(val) {
				// 获取主表名称
				const parentTableName = JSON.parse(this.recordData.tableJson).filter((item) => item.tableType === 'parent')[0]
					.tableName
				// 监听到字段列表后，将其转至定义的变量中
				this.fieldInfoLis = []
				// 不仅表单中有字段，而且还要必须选择了主表
				if (val.length > 0) {
					const fildLists = this.getListField(val)
					fildLists.forEach((item) => {
						const obj = {}
						// 判断是否是选择了表，并且选择了字段
						if (item.selectTable && item.selectColumn) {
							// 判断是否是主表的字段，并且是必填项
							const requireds = item.rules[0].required
							if ((item.selectTable === parentTableName) & requireds) {
								obj.label = item.label
								obj.value = item.model
								this.fieldInfoLis.push(obj)
							}
						}
					})
				}
			}
		},
		methods: {
			showDrawer() {
				this.form = {}
				this.form = JSON.parse(JSON.stringify(this.childNode))
				this.formData = this.form.properties.configInfo
				this.drawer = true
				// 制作默认的摘要字段
				this.makeAbstractTemp()
				// 获取序列号列表
				templateSnApi.templateFlwTemplateSnSelector().then((data) => {
					this.snTemplateOptions = data.map((item) => {
						return {
							value: item['id'],
							label: item['name']
						}
					})
				})
				// 获取打印模板列表
				templatePrintApi.templateFlwTemplatePrintSelector().then((data) => {
					this.printTemplateOptions = data.map((item) => {
						return {
							value: item['id'],
							label: item['name']
						}
					})
				})
			},
			// 制作默认显示的摘要信息
			makeAbstractTemp() {
				let temp = this.form.properties.configInfo.processAbstractTemplate
				if (temp === '') {
					if (this.fieldInfoLis.length > 0) {
						let fieldInfoTemp = ''
						for (let a = 0; a < this.fieldInfoLis.length; a++) {
							// 最多3个摘要，按顺序而来
							if (a < 3) {
								const str = this.fieldInfoLis[a].label + ':' + this.fieldInfoLis[a].value
								if (a === 3 - 1 || a === this.fieldInfoLis.length - 1) {
									fieldInfoTemp = fieldInfoTemp + str
								} else {
									fieldInfoTemp = fieldInfoTemp + str + ','
								}
							}
						}
						temp = fieldInfoTemp
					}
				}
			},
			onFinish() {
				// 校验表单的正确性
				this.$refs.noticeFormRef
					.validate()
					.then((values) => {
						if (this.form.id === '') {
							this.form.id = this.$TOOL.snowyUuid()
						}
						this.form.dataLegal = true
						this.form.properties.configInfo = values
						this.$emit('update:modelValue', this.form)
						this.drawer = false
					})
					.catch((info) => {
						this.formVerify = true
						setTimeout((e) => {
							this.formVerify = false
						}, 2000)
					})
			},
			// 选择参与人
			selectionParticipants(type) {
				let participateInfo = this.form.properties.participateInfo
				let data = []
				if (participateInfo.length > 0) {
					participateInfo.forEach((item) => {
						if (item.key === type) {
							data.push(item)
						}
					})
				}
				if (type === 'ORG') {
					this.$refs.orgselectorPlus.showOrgPlusModal(data)
				}
				if (type === 'ROLE') {
					this.$refs.roleselectorPlus.showRolePlusModal(data)
				}
				if (type === 'POSITION') {
					this.$refs.posselectorPlus.showPosPlusModal(data)
				}
				if (type === 'USER') {
					this.$refs.userselectorPlus.showUserPlusModal(data)
				}
			},
			// 回调函数，这几个选择人员相关的设计器，都是的
			callBack(value) {
				let participateInfo = this.form.properties.participateInfo
				if (participateInfo.length > 0) {
					let mark = 0
					for (let a = 0; a < participateInfo.length; a++) {
						if (value.key === participateInfo[a].key) {
							if (value.label === '') {
								participateInfo.splice(a, 1)
							} else {
								participateInfo[a] = value
							}
							mark = 1
						}
					}
					if (mark === 0) {
						participateInfo.push(value)
					}
				} else {
					this.form.properties.participateInfo.push(value)
				}
			},
			// 获取tag标签的内容
			getTagList(type) {
				const participateInfo = this.form.properties.participateInfo
				if (participateInfo.length < 0) {
					return undefined
				} else {
					const obj = participateInfo.find((item) => item.key === type)
					return obj
				}
			},
			getListField(data) {
				let result = []
				// 递归遍历控件树
				const traverse = (array) => {
					array.forEach((element) => {
						if (element.type === 'grid' || element.type === 'tabs') {
							// 栅格布局 and 标签页
							element.columns.forEach((item) => {
								traverse(item.list)
							})
						} else if (element.type === 'card') {
							// 卡片布局 and  动态表格
							traverse(element.list)
						} else if (element.type === 'table') {
							// 表格布局
							element.trs.forEach((item) => {
								item.tds.forEach((val) => {
									traverse(val.list)
								})
							})
						} else {
							const type = element.type
							// 排除一些
							if (
								(type !== 'alert') &
								(type !== 'text') &
								(type !== 'divider') &
								(type !== 'batch') &
								(type !== 'html')
							) {
								result.push(element)
							}
						}
					})
				}
				traverse(data)
				return result
			}
		}
	}
</script>
