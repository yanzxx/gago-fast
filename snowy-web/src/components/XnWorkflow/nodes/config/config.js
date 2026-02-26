export default {
	// 模型结构，就是每个节点，都有的
	nodeModel: {
		// 节点
		node: {
			id: '', // 节点id
			title: '', // 节点名称
			type: '', // 节点类型
			dataLegal: false, // 信息是否完整
			// content:'',   // 内容、备注
			properties: {
				configInfo: {}, // 除条件路由和分合流节点外均有
				conditionInfo: [], // 条件信息，  条件节点特有
				participateInfo: [], // 参与人信息，开始节点、审批节点、抄送节点才有
				buttonInfo: [], // 按钮信息，  开始节点、审批节点、抄送节点才有
				fieldInfo: [], // 字段信息，  开始节点、审批节点、抄送节点才有
				commentList: [] // 流转记录内容
				// listenerInfo: [],       // 监听器信息，开始节点、审批节点、抄送节点才有
				// noticeInfo: [],         // 通知信息，  开始节点、审批节点、抄送节点才有
			},
			childNode: {}, // 子节点
			conditionNodeList: [] // 条件子节点
		},
		// 通用按钮默认配置，不需要改动，除非自定义开发的时候，再加什么按钮
		buttonInfo: [
			{
				key: 'SAVE',
				label: '保存',
				value: 'HIDE',
				type: 'default'
			},
			{
				key: 'SUBMIT',
				label: '提交',
				value: 'HIDE',
				type: 'primary'
			},
			{
				key: 'COMPLETE',
				label: '同意',
				value: 'HIDE',
				type: 'primary'
			},
			{
				key: 'REJECT',
				label: '驳回',
				value: 'HIDE',
				type: 'default'
			},
			{
				key: 'PRINT',
				label: '打印',
				value: 'HIDE',
				type: 'default'
			},
			{
				key: 'TURN',
				label: '转办',
				value: 'HIDE',
				type: 'default'
			}
		],
		fieldInfo: [],
		listenerInfo: [],
		noticeInfo: []
	},

	// 各个节点的configInfo,他们长的都不一样
	nodeConfigInfo: {
		// 全局配置
		processConfigInfo: {
			// 基础配置
			processSnTemplateId: undefined, // 流水号模板 id
			processPrintTemplateId: undefined, // 打印模板 id
			processTitleTemplate: 'initiator 的 processName - startTime', // 标题模板
			processAbstractTemplate: '', // 摘要模板
			processEnableAutoDistinct: false, // 开启自动去重
			processAutoDistinctType: 'SAMPLE', // 自动去重类型
			processEnableRevoke: true, // 开启审批撤销
			processEnableCommentRequired: false, // 开启意见必填
			// 通知配置
			processEnableBackNotice: false, // 开启退回通知
			processEnableTodoNotice: false, // 开启待办通知
			processEnableCopyNotice: false, // 开启抄送通知
			processEnableCompleteNotice: false, // 开启完成通知
			// 通知的方式
			processBackNoticeChannel: ['MSG'], // 退回通知渠道
			processTodoNoticeChannel: ['MSG'], // 待办通知渠道
			processCopyNoticeChannel: ['MSG'], // 抄送通知渠道
			processCompleteNoticeChannel: ['MSG'], // 完成通知渠道
			// 通知配置对应的模板
			processBackNoticeTemplate: '您于 startTime 发起的 processName 被退回', // 退回通知模板
			processTodoNoticeTemplate: '由 initiator 发起的 processName 需要您审批', // 待办通知模板
			processCopyNoticeTemplate: '您收到一条由 initiator 发起的 processName 的抄送', // 抄送通知模板
			processCompleteNoticeTemplate: '您于 startTime 发起的 processName 审批通过' // 完成通知模板
			// dataLegal: false // 数据默认不规范
		},
		// 审核节点配置
		userTaskConfigInfo: {
			userTaskType: 'ARTIFICIAL', // 任务节点类型
			userTaskMulApproveType: 'SEQUENTIAL', // 多人审批时类型
			userTaskEmptyApproveType: 'AUTO_COMPLETE', //审批人为空时类型
			userTaskEmptyApproveUser: '', // 审批人为空时转交人id
			userTaskEmptyApproveUserArray: [] // 审批人为空时转交人包含name，用来前端回显，后端不解析
			// dataLegal: false // 数据默认不规范
		},
		// 条件中默认的配置
		conditionConfigInfo: {
			priorityLevel: 1 // 优先级 默认1
			// dataLegal: false // 数据默认不规范
		},
		// 抄送节点配置
		serviceTaseConfigInfo: {
			// dataLegal: false // 数据默认不规范
		}
	},

	// 按钮相关配置
	button: {
		// 发起人节点 默认选中按钮
		startTaskDefaultButtonkey: ['SAVE', 'SUBMIT'],
		// 发起人节点 默认不让选的按钮 // 这个节点屏蔽下面配置的
		startTaskNoCheckedButtonkey: ['COMPLETE', 'PRINT', 'REJECT', 'TURN'],

		// 审批人节点 默认选中按钮
		userTaskDefaultButtonkey: ['COMPLETE', 'REJECT'],
		// 审批节点 默认不让选的
		userTaskNoCheckedButtonkey: ['SUBMIT', 'SAVE', 'REVOKE'],
		// 抄送人节点 默认选中按钮
		serviceTaskDefaultButtonkey: ['SUBMIT']
	},
	// 字段相关配置
	field: {
		// 其他节点中字段对象数据模型
		fieldModel: {
			key: '',
			label: '',
			value: 'WRITE', // 默认
			required: false, // 必填
			extJson: '' // 额外扩展，暂无
		},
		// 审批节点中字段对象数据模型
		userTaskFieldModel: {
			key: '',
			label: '',
			value: 'READ', // 默认设为只读
			required: false, // 必填
			extJson: '' // 额外扩展，暂无
		},
		// 字段列表中的字典
		fieldRadioList: [
			{
				label: '可编辑',
				value: 'WRITE'
			},
			{
				label: '只读',
				value: 'READ'
			},
			{
				label: '隐藏',
				value: 'HIDE'
			}
		]
	},
	// 通知方式字典
	noticeInfoList: [
		{
			label: '短信',
			value: 'SMS'
		},
		{
			label: '邮件',
			value: 'EMAIL'
		},
		{
			label: '站内信',
			value: 'MSG'
		}
	],
	// 模板默认自带字段
	templateDefaultFields: [
		{
			label: '发起人',
			value: 'initiator'
		},
		{
			label: '流程名称',
			value: 'processName'
		},
		{
			label: '发起时间',
			value: 'startTime'
		},
		{
			label: '表单字段',
			value: 'disabled' // 这里表示在组件显示的时候就截至了，是下一梭子数组的标题哦
		}
	]
}
