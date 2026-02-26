<template>
	<div>
		<a-button @click="hide">隐藏</a-button>
		<a-button @click="show">显示</a-button>
		<a-button @click="disable">禁用</a-button>
		<a-button @click="enable">启用</a-button>
		<a-button @click="setData">回显</a-button>
		<a-button @click="getData">提交</a-button>
		<snowy-form-build :value="jsonData" ref="KFB" @submit="handleSubmit" />
	</div>
</template>
<script>
	export default {
		name: 'Demo',
		data() {
			return {
				jsonData: {
					list: [
						// 动态表格
						{
							type: 'batch',
							label: '报名信息表',
							list: [
								{
									type: 'input',
									label: '申请类型',
									icon: 'icon-write',
									options: {
										type: 'text',
										width: '100%',
										defaultValue: '',
										placeholder: '请输入',
										clearable: false,
										maxLength: null,
										addonBefore: '',
										addonAfter: '',
										hidden: false,
										disabled: false
									},
									model: 'apply_type',
									key: 'apply_type',
									help: '',
									rules: [
										{
											required: true,
											message: '必填项'
										}
									],
									selectColumn: 'apply_type'
								},
								{
									type: 'radio',
									label: '单选框',
									icon: 'icon-danxuan-cuxiantiao',
									options: {
										disabled: false,
										hidden: false,
										defaultValue: '',
										dynamic: false,
										options: [
											{
												value: '1',
												label: '选项1'
											},
											{
												value: '2',
												label: '选项2'
											},
											{
												value: '3',
												label: '选项3'
											}
										]
									},
									model: 'radio_1664004455284',
									key: 'radio_1664004455284',
									help: '',
									rules: [
										{
											required: false,
											message: '必填项'
										}
									]
								},
								{
									type: 'input',
									label: '输入框',
									icon: 'icon-write',
									options: {
										type: 'text',
										width: '100%',
										defaultValue: '',
										placeholder: '请输入',
										clearable: false,
										maxLength: null,
										addonBefore: '',
										addonAfter: '',
										hidden: false,
										disabled: false
									},
									model: 'input_1664004457709',
									key: 'input_1664004457709',
									help: '',
									rules: [
										{
											required: false,
											message: '必填项'
										}
									]
								}
							],
							options: {
								scrollY: 0,
								disabled: false,
								hidden: false,
								showLabel: false,
								hideSequence: false,
								width: '100%'
							},
							model: 'sys_signup_info',
							key: 'sys_signup_info',
							help: '',
							selectTable: 'sys_signup_info'
						},

						{
							type: 'button',
							label: '按钮',
							options: {
								type: 'primary',
								handle: 'submit',
								dynamicFun: '',
								hidden: true,
								disabled: false
							},
							key: 'button_1619948868332'
						},
						{
							type: 'slider',
							label: '滑动输入条',
							icon: 'icon-menu',
							options: {
								width: '100%',
								defaultValue: 0,
								disabled: false,
								hidden: false,
								min: 0,
								max: 100,
								step: 1,
								showInput: false
							},
							model: 'slider_1619948887083',
							key: 'slider_1619948887083',
							help: '',
							rules: [{ required: false, message: '必填项' }]
						},
						{
							type: 'rate',
							label: '评分',
							icon: 'icon-pingfen_moren',
							options: {
								defaultValue: 0,
								max: 5,
								disabled: false,
								hidden: false,
								allowHalf: false
							},
							model: 'rate_1619948887483',
							key: 'rate_1619948887483',
							help: '',
							rules: [{ required: false, message: '必填项' }]
						},
						{
							type: 'uploadImg',
							label: '上传图片',
							icon: 'icon-image',
							options: {
								defaultValue: '[]',
								multiple: false,
								hidden: false,
								disabled: false,
								width: '100%',
								data: '{}',
								limit: 3,
								placeholder: '上传',
								fileName: 'image',
								headers: {},
								action: 'http://localhost:8080/api/dev/file/uploadDynamicReturnUrl',
								listType: 'picture-card'
							},
							model: 'uploadImg_1619948887819',
							key: 'uploadImg_1619948887819',
							help: '',
							rules: [{ required: false, message: '必填项' }]
						},
						{
							type: 'card',
							label: '卡片布局',
							list: [
								{
									type: 'date',
									label: '日期选择框',
									icon: 'icon-calendar',
									options: {
										width: '100%',
										defaultValue: '',
										rangeDefaultValue: [],
										range: false,
										showTime: false,
										disabled: false,
										hidden: false,
										clearable: false,
										placeholder: '请选择',
										rangePlaceholder: ['开始时间', '结束时间'],
										format: 'YYYY-MM-DD'
									},
									model: 'date_1619948975172',
									key: 'date_1619948975172',
									help: '',
									rules: [{ required: false, message: '必填项' }]
								},
								{
									type: 'input',
									label: '输入框',
									icon: 'icon-write',
									options: {
										type: 'text',
										width: '100%',
										defaultValue: '',
										placeholder: '请输入',
										clearable: false,
										maxLength: null,
										hidden: false,
										disabled: false
									},
									model: 'input_1619948973892',
									key: 'input_1619948973892',
									help: '',
									prefix: '',
									suffix: '',
									rules: [{ required: false, message: '必填项' }]
								},
								{
									type: 'textarea',
									label: '文本框',
									icon: 'icon-edit',
									options: {
										width: '100%',
										minRows: 4,
										maxRows: 6,
										maxLength: null,
										defaultValue: '',
										clearable: false,
										hidden: false,
										disabled: false,
										placeholder: '请输入'
									},
									model: 'textarea_1619948974269',
									key: 'textarea_1619948974269',
									help: '',
									rules: [{ required: false, message: '必填项' }]
								}
							],
							key: 'card_1619948983395'
						},
						{
							type: 'table',
							label: '表格布局',
							trs: [
								{
									tds: [
										{
											colspan: 1,
											rowspan: 1,
											list: [
												{
													type: 'slider',
													label: '滑动输入条',
													icon: 'icon-menu',
													options: {
														width: '100%',
														defaultValue: 0,
														disabled: false,
														hidden: false,
														min: 0,
														max: 100,
														step: 1,
														showInput: false
													},
													model: 'slider_1619948976300',
													key: 'slider_1619948976300',
													help: '',
													rules: [{ required: false, message: '必填项' }]
												}
											]
										},
										{
											colspan: 1,
											rowspan: 1,
											list: [
												{
													type: 'uploadImg',
													label: '上传图片',
													icon: 'icon-image',
													options: {
														defaultValue: '[]',
														multiple: false,
														hidden: false,
														disabled: false,
														width: '100%',
														data: '{}',
														limit: 3,
														placeholder: '上传',
														fileName: 'image',
														headers: {},
														action: 'http://localhost:8080/api/dev/file/uploadDynamicReturnUrl',
														listType: 'picture-card'
													},
													model: 'uploadImg_1619948977239',
													key: 'uploadImg_1619948977239',
													help: '',
													rules: [{ required: false, message: '必填项' }]
												},
												{
													type: 'time',
													label: '时间选择框',
													icon: 'icon-time',
													options: {
														width: '100%',
														defaultValue: '',
														disabled: false,
														hidden: false,
														clearable: false,
														placeholder: '请选择',
														format: 'HH:mm:ss'
													},
													model: 'time_1619948975820',
													key: 'time_1619948975820',
													help: '',
													rules: [{ required: false, message: '必填项' }]
												}
											]
										}
									]
								},
								{
									tds: [
										{
											colspan: 1,
											rowspan: 1,
											list: [
												{
													type: 'radio',
													label: '单选框',
													icon: 'icon-danxuan-cuxiantiao',
													options: {
														disabled: false,
														hidden: false,
														defaultValue: '',
														dynamicKey: undefined,
														dynamic: false,
														options: [
															{ value: '1', label: '选项1' },
															{ value: '2', label: '选项2' },
															{ value: '3', label: '选项3' }
														]
													},
													model: 'radio_1619948974588',
													key: 'radio_1619948974588',
													help: '',
													rules: [{ required: false, message: '必填项' }]
												}
											]
										},
										{
											colspan: 1,
											rowspan: 1,
											list: [
												{
													type: 'date',
													label: '日期选择框',
													icon: 'icon-calendar',
													options: {
														width: '100%',
														defaultValue: '',
														rangeDefaultValue: [],
														range: false,
														showTime: false,
														disabled: false,
														hidden: false,
														clearable: false,
														placeholder: '请选择',
														rangePlaceholder: ['开始时间', '结束时间'],
														format: 'YYYY-MM-DD'
													},
													model: 'date_1619948975548',
													key: 'date_1619948975548',
													help: '',
													rules: [{ required: false, message: '必填项' }]
												}
											]
										}
									]
								}
							],
							options: {
								width: '100%',
								bordered: true,
								bright: false,
								small: true,
								customStyle: ''
							},
							key: 'table_1619948992003'
						},
						{
							type: 'uploadFile',
							label: '上传文件',
							icon: 'icon-upload',
							options: {
								defaultValue: '[]',
								multiple: false,
								disabled: false,
								hidden: false,
								drag: false,
								downloadWay: 'a',
								dynamicFun: '',
								width: '100%',
								limit: 3,
								data: '{}',
								fileName: 'file',
								headers: {},
								action: 'http://localhost:8080/',
								placeholder: '上传'
							},
							model: 'uploadFile_1619948976948',
							key: 'uploadFile_1619948976948',
							help: '',
							rules: [{ required: false, message: '必填项' }]
						},
						{
							type: 'cascader',
							label: '级联选择器',
							icon: 'icon-guanlian',
							options: {
								disabled: false,
								hidden: false,
								showSearch: false,
								placeholder: '请选择',
								clearable: false,
								dynamicKey: undefined,
								dynamic: true,
								options: [
									{
										value: '1',
										label: '选项1',
										children: [{ value: '11', label: '选项11' }]
									},
									{
										value: '2',
										label: '选项2',
										children: [{ value: '22', label: '选项22' }]
									}
								]
							},
							model: 'cascader_1619948977724',
							key: 'cascader_1619948977724',
							help: '',
							rules: [{ required: false, message: '必填项' }]
						},
						{
							type: 'treeSelect',
							label: '树选择器',
							icon: 'icon-tree',
							options: {
								disabled: false,
								multiple: false,
								hidden: false,
								clearable: false,
								showSearch: false,
								treeCheckable: false,
								placeholder: '请选择',
								dynamicKey: undefined,
								dynamic: true,
								options: [
									{
										value: '1',
										label: '选项1',
										children: [{ value: '11', label: '选项11' }]
									},
									{
										value: '2',
										label: '选项2',
										children: [{ value: '22', label: '选项22' }]
									}
								]
							},
							model: 'treeSelect_1619948978156',
							key: 'treeSelect_1619948978156',
							help: '',
							prefix: '',
							suffix: '',
							rules: [{ required: false, message: '必填项' }]
						}
					],
					config: {
						layout: 'horizontal',
						labelCol: { xs: 4, sm: 4, md: 4, lg: 4, xl: 4, xxl: 4 },
						wrapperCol: { xs: 18, sm: 18, md: 18, lg: 18, xl: 18, xxl: 18 },
						hideRequiredMark: false,
						customStyle: ''
					}
				}
			}
		},
		methods: {
			handleSubmit(p) {
				// 通过表单提交按钮触发，获取promise对象
				p()
					.then((res) => {
						// 获取数据成功
						alert(JSON.stringify(res))
					})
					.catch((err) => {
						console.log(err, '校验失败')
					})
			},
			show() {
				this.$refs.KFB.show(['date_1619948975172'])
			},
			hide() {
				this.$refs.KFB.hide(['date_1619948975172'])
			},
			disable() {
				this.$refs.KFB.disable(['date_1619948975172'])
			},
			enable() {
				this.$refs.KFB.enable(['date_1619948975172'])
			},
			getData() {
				// 通过函数获取数据
				this.$refs.KFB.getData()
					.then((res) => {
						// 获取数据成功
						alert('查看控制台')
						console.log(JSON.stringify(res))
					})
					.catch((err) => {
						console.log(err, '校验失败')
					})
			},
			setData() {
				const json = {
					slider_1619948887083: 5,
					rate_1619948887483: 2,
					date_1619948975172: '2022-08-24',
					input_1619948973892: '老俞来做测试',
					textarea_1619948974269: '狗屁不通文章',
					uploadImg_1619948977239: [
						{
							type: 'image/jpeg',
							name: 'touxiang.jpg',
							status: 'done',
							uid: 'vc-upload-1661683554301-4',
							url: 'https://snowylayui.xiaonuo.vip/assets/images/logo.png'
						}
					],

					sys_signup_info: [
						{
							apply_type: '12322',
							radio_1664004455284: '3',
							input_1664004457709: '我',
							key: 1664004594047
						},
						{
							apply_type: '你的',
							radio_1664004455284: '2',
							input_1664004457709: '熟羊肉',
							key: 1664004598643
						}
					]
				}
				this.$refs.KFB.setData(json)
			}
		}
	}
</script>
