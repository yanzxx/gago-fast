<template>
	<a-config-provider :locale="locale">
		<a-form
			ref="form"
			name="kForm"
			:model="formData"
			v-if="typeof value.list !== 'undefined' && typeof value.config !== 'undefined'"
			class="snowy-form-build-9136076486841527"
			:layout="value.config.layout"
			:hideRequiredMark="value.config.hideRequiredMark"
			@submit="handleSubmit"
			:style="value.config.customStyle"
		>
			<!-- 他们的值都在defaultValue -->
			<buildBlocks
				ref="buildBlocks"
				@handleReset="reset"
				v-for="(record, index) in value.list"
				:record="record"
				:dynamicData="getDynamicData"
				:config="config"
				:disabled="disabled"
				:formConfig="value.config"
				:validatorError="validatorError"
				:key="index"
				@change="handleChange"
			/>
		</a-form>
	</a-config-provider>
</template>
<script>
	/*
	 * author kcz
	 * date 2019-11-20
	 * description 将json数据构建成表单
	 */
	import buildBlocks from './buildBlocks.vue'
	import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
	// import moment from "moment";
	export default {
		name: 'SnowyFormBuild',
		components: {
			buildBlocks
		},
		// props: ["value", "dynamicData"],
		props: {
			value: {
				type: Object,
				required: true
			},
			dynamicData: {
				type: Object,
				default: () => {
					return {}
				}
			},
			config: {
				type: Object,
				default: () => ({})
			},
			disabled: {
				type: Boolean,
				default: false
			},
			outputString: {
				type: Boolean,
				default: false
			},
			defaultValue: {
				type: Object,
				default: () => ({})
			},
			dataIsString: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				locale: zhCN,
				formData: {},
				validatorError: {},
				defaultDynamicData: {},
				selectTableType: ''
			}
		},
		computed: {
			getDynamicData() {
				return typeof this.dynamicData === 'object' && Object.keys(this.dynamicData).length
					? this.dynamicData
					: window.$kfb_dynamicData || {}
			}
		},
		mounted() {
			this.$nextTick(() => {
				this.setData(this.defaultValue)
			})
		},
		methods: {
			// moment,
			handleSubmit(e) {
				// 提交按钮触发，并触发submit函数，返回getData函数
				e.preventDefault()
				this.$emit('submit', this.getData)
			},
			reset() {
				// 重置表单
				this.$refs.form.resetFields()
			},

			// 抛出负责嵌套，获取到最真的各个组件list
			getFormList() {
				const result = []
				// 递归遍历控件树
				const traverse = (array) => {
					array.forEach((element) => {
						if (element.type === 'grid' || element.type === 'tabs') {
							// 栅格布局 and 标签页
							element.columns.forEach((item) => {
								traverse(item.list)
							})
						} else if (element.type === 'card') {
							//  || element.type === "batch"
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
							if (element) {
								const type = element.type
								if ((type !== 'alert') & (type !== 'text') & (type !== 'divider')) {
									// 缓存一个表的类型
									if (
										!this.selectTableType &&
										element.selectTable &&
										element.selectTableType &&
										element.selectTableType === 'parent'
									) {
										this.selectTableType = element.selectTable
									}
									result.push(element)
								}
							}
						}
					})
				}
				traverse(this.value.list)
				return result
			},
			getData() {
				// 提交函数，提供父级组件调用
				return new Promise((resolve, reject) => {
					try {
						this.$nextTick(() => {
							this.$refs.form
								.validateFields()
								.then((values) => {
									this.validatorError = {}
									this.$refs.buildBlocks.forEach((item) => {
										if (!item.validationSubform()) {
											reject()
										}
									})
									// 但凡是进来，都要筛选一遍
									this.getFormList()

									// let formList = this.getFormList()
									// 处理动态表格，去掉动态表格额外生成的一些
									Object.keys(values).forEach((key) => {
										if (/\W\d\W/.test(key)) {
											delete values[key]
										}
										if (values[key] === undefined) {
											const record = this.getFormList().find((item) => item.key === key)
											if (record) {
												values[key] = record.options.defaultValue
											}
										}
									})
									if (this.outputString) {
										for (const key in values) {
											const type = typeof values[key]
											if (type === 'string' || type === 'number' || type === 'undefined') {
												continue
											} else if (type === 'object') {
												values[key] = JSON.stringify(values[key])
											}
										}
										if (this.selectTableType) {
											let result = {}
											result[this.selectTableType] = values
											resolve(result)
										} else {
											resolve(values)
										}
									} else {
										if (this.selectTableType) {
											let result = {}
											result[this.selectTableType] = values
											resolve(result) // JSON.stringify(result)
										} else {
											resolve(values)
										}
									}
								})
								.catch((err) => {
									/**
									 * @author: lizhichao<meteoroc@outlook.com>
									 * @Description: 多容器校验时，提供error返回给多容器进行判断。
									 */
									this.validatorError = err
									reject(err)
									// return;
								})
						})
					} catch (err) {
						reject(err)
					}
				})
			},
			setData(json) {
				return new Promise((resolve, reject) => {
					try {
						/*if (this.outputString) {
            // 将非string数据还原
            for (const key in json) {
              if (!json[key].startsWith("snowy-form-design#")) {
                continue;
              }
              const array = json[key].split("#");
              if (array[1] === "object") {
                json[key] = JSON.parse(array[2]);
              } else if (array[1] === "number") {
                json[key] = Number(array[2]);
              } else if (array[1] === "boolean") {
                json[key] = Boolean(array[2]);
              }
            }
            // Object.assign(this.formData, json);
          }*/
						if (this.outputString) {
							for (const key in json) {
								const type = typeof json[key]
								if (type === 'object') {
									json[key] = JSON.parse(json[key])
								}
							}
						}
						// 赋值操作，非1.x的那种this.form.setFieldsValue(json);，设置表单的值也无效
						Object.assign(this.formData, json)
						Object.keys(this.formData).forEach((key) => {
							const record = this.getFormList().find((item) => item.key === key)
							if (record && record.options) {
								record.options.defaultValue = this.formData[key]
							}
						})
						resolve(true)
					} catch (err) {
						reject(err)
					}
				})
			},

			// 批量设置某个option的值
			setOptions(fields, optionName, value) {
				fields = new Set(fields)

				// 递归遍历控件树
				const traverse = (array) => {
					array.forEach((element) => {
						if (fields.has(element.model)) {
							element.options[optionName] = value
						}
						if (element.type === 'grid' || element.type === 'tabs') {
							// 栅格布局 and 标签页
							element.columns.forEach((item) => {
								traverse(item.list)
							})
						} else if (element.type === 'card' || element.type === 'batch') {
							// 卡片布局 and  动态表格
							traverse(element.list)
						} else if (element.type === 'table') {
							// 表格布局
							element.trs.forEach((item) => {
								item.tds.forEach((val) => {
									traverse(val.list)
								})
							})
						}
					})
				}
				traverse(this.value.list)
			},
			// 隐藏表单字段
			hide(fields) {
				this.setOptions(fields, 'hidden', true)
			},
			// 显示表单字段
			show(fields) {
				this.setOptions(fields, 'hidden', false)
			},
			// 禁用表单字段
			disable(fields) {
				this.setOptions(fields, 'disabled', true)
			},
			// 启用表单字段
			enable(fields) {
				this.setOptions(fields, 'disabled', false)
			},
			handleChange(value, key) {
				if (value instanceof Event) {
					return
				}
				if (Array.isArray(this.formData[key]) && !Array.isArray(value)) {
					return
				}

				this.formData[key] = value
				// 触发change事件
				this.$emit('change', value, key)
				if (Array.isArray(value) && value.length) {
					this.$nextTick(() => {
						this.$refs.form.validateFields([key])
					})
				}
			}
		}
	}
</script>
