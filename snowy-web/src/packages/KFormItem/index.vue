<!--
 * @Description: 传入record数据，通过判断record.type，来渲染对应的组件
 * @Author: kcz
 * @Date: 2020-01-02 22:41:48
 * @LastEditors: yubaoshan
 * @LastEditTime: 2023年6月18日23:29:47
 -->
<template>
	<div class="">
		<a-form-item
			v-if="
				[
					'input',
					'textarea',
					'date',
					'time',
					'number',
					'radio',
					'checkbox',
					'select',
					'rate',
					'switch',
					'slider',
					'uploadImg',
					'uploadFile',
					'cascader',
					'treeSelect',
					'xnUserSelector',
					'xnInput'
				].includes(record.type)
			"
			:label-col="
				formConfig.layout === 'horizontal'
					? formConfig.labelLayout === 'flex'
						? { style: `width:${formConfig.labelWidth}px` }
						: formConfig.labelCol
					: {}
			"
			:wrapper-col="
				formConfig.layout === 'horizontal'
					? formConfig.labelLayout === 'flex'
						? { style: 'width:auto;flex:1' }
						: formConfig.wrapperCol
					: {}
			"
			:style="formConfig.layout === 'horizontal' && formConfig.labelLayout === 'flex' ? { display: 'flex' } : {}"
			:rules="getRules(record.type)"
			:name="record.key"
		>
			<template #label>
				<template v-if="record.help">
					<a-tooltip :title="record.help">
						<span v-text="record.label"></span>
						<question-circle-outlined class="question-circle" />
					</a-tooltip>
				</template>
				<template v-else>
					<span v-text="record.label"></span>
				</template>
			</template>
			<!-- 多行文本 -->
			<a-textarea
				:style="`width:${record.options.width}`"
				v-if="record.type === 'textarea'"
				:autoSize="{
					minRows: record.options.minRows,
					maxRows: record.options.maxRows
				}"
				:disabled="disabled || record.options.disabled"
				:placeholder="record.options.placeholder"
				:allowClear="record.options.clearable"
				:maxLength="record.options.maxLength"
				:rows="4"
				@change="handleChange($event.target.value, record.model)"
				v-model:value="record.options.defaultValue"
			/>
			<!-- 单选框 -->
			<a-radio-group
				v-else-if="record.type === 'radio'"
				:options="record.options.options"
				:disabled="disabled || record.options.disabled"
				:placeholder="record.options.placeholder"
				v-model:value="record.options.defaultValue"
				@change="handleChange($event.target.value, record.model)"
				@blur="onblur"
				@focus="onFocus"
			/>
			<!-- 多选框 -->
			<a-checkbox-group
				v-else-if="record.type === 'checkbox'"
				:options="record.options.options"
				:disabled="disabled || record.options.disabled"
				:placeholder="record.options.placeholder"
				v-model:value="record.options.defaultValue"
				@change="handleChange($event, record.model)"
				@blur="onblur"
				@focus="onFocus"
			/>
			<!-- 开关 -->
			<a-switch
				v-else-if="record.type === 'switch'"
				:disabled="disabled || record.options.disabled"
				v-model:checked="record.options.defaultValue"
				@change="handleChange($event, record.model)"
				@blur="onblur"
				@focus="onFocus"
			/>
			<!-- 滑块 -->
			<div v-else-if="record.type === 'slider'" :style="`width:${record.options.width}`" class="slider-box">
				<div class="slider">
					<!--        record.options.defaultValue-->
					<a-slider
						:disabled="disabled || record.options.disabled"
						:min="record.options.min"
						:max="record.options.max"
						:step="record.options.step"
						@change="handleChange($event, record.model)"
						v-model:value="record.options.defaultValue"
						@afterChange="onAfterChange"
						@blur="onblur"
						@focus="onFocus"
					/>
				</div>
				<div class="number" v-if="record.options.showInput">
					<a-input-number
						style="width: 100%"
						:disabled="disabled || record.options.disabled"
						:min="record.options.min"
						:max="record.options.max"
						:step="record.options.step"
						@change="handleChange($event, record.model)"
						v-model:value="record.options.defaultValue"
						@blur="onblur"
						@focus="onFocus"
					/>
				</div>
			</div>
			<component
				v-else
				:style="`width:${record.options.width}`"
				v-bind="componentOption"
				:min="record.options.min || record.options.min === 0 ? record.options.min : -Infinity"
				:max="record.options.max || record.options.max === 0 ? record.options.max : Infinity"
				:precision="
					record.options.precision > 50 || (!record.options.precision && record.options.precision !== 0)
						? null
						: record.options.precision
				"
				:parentDisabled="disabled || record.options.disabled"
				:disabled="disabled || record.options.disabled"
				:record="record"
				:config="config"
				:filterOption="
					record.options.showSearch
						? (inputValue, option) => {
								return option.label.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
								// return option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
						  }
						: false
				"
				:allowClear="record.options.clearable"
				:dynamicData="dynamicData"
				:treeData="
					!record.options.dynamic
						? record.options.options
						: dynamicData[record.options.dynamicKey]
						? dynamicData[record.options.dynamicKey]
						: []
				"
				:options="record.options.options"
				:mode="record.options.multiple ? 'multiple' : ''"
				v-model:value="record.options.defaultValue"
				:is="componentItem"
				@change="handleChange($event, record.model)"
				@blur="onblur"
				@focus="onFocus"
				@pressEnter="onPressEnter"
				@step="onStep"
				@deselect="onDeselect"
				@dropdownVisibleChange="onDropdownVisibleChange"
				@inputKeyDown="onInputKeyDown"
				@mouseenter="onMouseenter"
				@mouseleave="onMouseleave"
				@popupScroll="onPopupScroll"
				@search="onSearch"
				@select="onSelect"
				@ok="onOk"
				@openChange="onOpenChange"
				@hoverChange="onHoverChange"
				@keydown="onKeydown"
			></component>
		</a-form-item>
		<!-- 可隐藏label -->
		<a-form-item
			v-else-if="['batch', 'editor', 'selectInputList'].includes(record.type)"
			:label="!record.options.showLabel ? '' : record.label"
			:name="record.model"
			:rules="getRules(record.type)"
			:label-col="
				formConfig.layout === 'horizontal' && record.options.showLabel
					? formConfig.labelLayout === 'flex'
						? { style: `width:${formConfig.labelWidth}px` }
						: formConfig.labelCol
					: {}
			"
			:wrapper-col="
				formConfig.layout === 'horizontal' && record.options.showLabel
					? formConfig.labelLayout === 'flex'
						? { style: 'width:auto;flex:1' }
						: formConfig.wrapperCol
					: {}
			"
			:style="
				formConfig.layout === 'horizontal' && formConfig.labelLayout === 'flex' && record.options.showLabel
					? { display: 'flex' }
					: {}
			"
		>
			<component
				:ref="['batch', 'selectInputList'].includes(record.type) && 'KBatch'"
				:style="`width:${record.options.width}`"
				v-bind="componentOption"
				:record="record"
				:config="config"
				:parentDisabled="disabled || record.options.disabled"
				:disabled="disabled || record.options.disabled"
				:dynamicData="dynamicData"
				@change="handleChange($event, record.model)"
				v-model:value="record"
				@batchChange="batchChange"
				:is="componentItem"
			></component>
			<!--@change="handleChange($event, record.model, record)"-->
			<!--v-model:value="record.model"-->
		</a-form-item>
		<!-- button按钮 -->
		<a-form-item v-else-if="record.type === 'button'">
			<a-button
				:disabled="disabled || record.options.disabled"
				@click="
					record.options.handle === 'submit'
						? false
						: record.options.handle === 'reset'
						? $emit('handleReset')
						: dynamicData[record.options.dynamicFun]
						? dynamicData[record.options.dynamicFun]()
						: false
				"
				:type="record.options.type"
				:html-type="record.options.handle === 'submit' ? 'submit' : undefined"
				v-text="record.label"
			></a-button>
		</a-form-item>
		<!-- alert提示 -->
		<a-form-item v-else-if="record.type === 'alert'">
			<a-alert
				:message="record.label"
				:description="record.options.description"
				:type="record.options.type"
				:showIcon="record.options.showIcon"
				:closable="record.options.closable"
				:banner="record.options.banner"
			/>
		</a-form-item>
		<!-- 文本 -->
		<a-form-item v-else-if="record.type === 'text'">
			<div :style="{ textAlign: record.options.textAlign }">
				<label
					:class="{ 'ant-form-item-required': record.options.showRequiredMark }"
					:style="{
						fontFamily: record.options.fontFamily,
						fontSize: record.options.fontSize,
						color: record.options.color
					}"
					v-text="record.label"
				></label>
			</div>
		</a-form-item>
		<!-- html -->
		<div v-else-if="record.type === 'html'" v-html="record.options.defaultValue"></div>
		<!-- 自定义组件 -->
		<customComponent
			v-else-if="customList.includes(record.type)"
			:record="record"
			:disabled="disabled"
			:dynamicData="dynamicData"
			@change="handleChange($event, record.model, record)"
			:formConfig="formConfig"
		/>
		<!-- 用户选择器 -->
		<a-form-item
			v-else-if="record.type === 'xnUserSelector' || record.type === 'xnInput'"
			:label-col="
				formConfig.layout === 'horizontal'
					? formConfig.labelLayout === 'flex'
						? { style: `width:${formConfig.labelWidth}px` }
						: formConfig.labelCol
					: {}
			"
			:wrapper-col="
				formConfig.layout === 'horizontal'
					? formConfig.labelLayout === 'flex'
						? { style: 'width:auto;flex:1' }
						: formConfig.wrapperCol
					: {}
			"
			:style="formConfig.layout === 'horizontal' && formConfig.labelLayout === 'flex' ? { display: 'flex' } : {}"
			:rules="getRules(record.type)"
			:name="record.key"
		/>
		<div v-else>
			<!-- 分割线 -->
			<a-divider
				v-if="record.type === 'divider' && record.label !== '' && record.options.orientation"
				:orientation="record.options.orientation"
				>{{ record.label }}</a-divider
			>
			<a-divider v-else-if="record.type === 'divider' && record.label !== ''">{{ record.label }}</a-divider>
			<a-divider v-else-if="record.type === 'divider' && record.label === ''" />
		</div>
	</div>
</template>
<script>
	/*
	 * author kcz
	 * date 2019-11-20
	 */
	// import moment from "moment";
	import customComponent from './customComponent.vue'
	import ComponentArray from '../core/components_use'
	import { omit } from 'lodash-es'
	import { QuestionCircleOutlined } from '@ant-design/icons-vue'

	export default {
		name: 'KFormItem',
		components: {
			customComponent,
			QuestionCircleOutlined
		},
		props: {
			// 表单数组
			record: {
				type: Object,
				required: true
			},
			// form-item 宽度配置
			formConfig: {
				type: Object,
				required: true
			},
			config: {
				type: Object,
				default: () => ({})
			},
			dynamicData: {
				type: Object,
				default: () => ({})
			},
			disabled: {
				type: Boolean,
				default: false
			}
		},
		computed: {
			customList() {
				if (window.$customComponentList) {
					return window.$customComponentList.map((item) => item.type)
				} else {
					return []
				}
			},
			/**
			 * @description: 输出对应组件
			 * @param {*}
			 * @return {*} component
			 */
			componentItem() {
				return ComponentArray[this.record.type]
			},
			componentOption() {
				return omit(this.record.options, ['defaultValue', 'disabled'])
			}
		},
		mounted() {
			// 我们给每个组件判断是否有默认值，有的话更新它
			if (this.record.options && this.record.options.defaultValue) {
				this.$emit('change', this.record.options.defaultValue, this.record.key)
			}
		},
		methods: {
			validationSubform() {
				// 验证动态表格
				if (!this.$refs.KBatch) return true
				return this.$refs.KBatch.validationSubform()
			},
			batchChange(val, key) {
				if (val instanceof InputEvent) {
					return
				}
				this.$emit('change', val, key)
			},
			getRules(type) {
				switch (type) {
					case 'slider':
						return [
							{
								validator: (rule, value) => {
									if (this.record.options.step && value % this.record.options.step !== 0) {
										return Promise.reject('输入值必须是步长的倍数')
									}
									return Promise.resolve()
								}
							}
						]
					default:
						return this.record.rules
				}
			},
			handleChange(e, key) {
				let value = e
				if (e && e.target) {
					value = e.target.value
				}
				// 传递change事件
				this.$emit('change', value, key)
				// 判断是否手动写了代码，如果写了，就执行
				if (this.record.events && this.record.events.onChange) {
					const code = JSON.parse(this.record.events.onChange)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			// 以下是扩展的事件执行
			onblur() {
				// 判断是否手动写了代码，如果写了，就执行
				if (this.record.events && this.record.events.onBlur) {
					const code = JSON.parse(this.record.events.onBlur)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onPressEnter() {
				if (this.record.events && this.record.events.onPressEnter) {
					const code = JSON.parse(this.record.events.onPressEnter)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onFocus() {
				if (this.record.events && this.record.events.onFocus) {
					const code = JSON.parse(this.record.events.onFocus)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onStep() {
				if (this.record.events && this.record.events.onStep) {
					const code = JSON.parse(this.record.events.onStep)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onDeselect() {
				if (this.record.events && this.record.events.onDeselect) {
					const code = JSON.parse(this.record.events.onDeselect)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onDropdownVisibleChange() {
				if (this.record.events && this.record.events.onDropdownVisibleChange) {
					const code = JSON.parse(this.record.events.onDropdownVisibleChange)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onInputKeyDown() {
				if (this.record.events && this.record.events.onInputKeyDown) {
					const code = JSON.parse(this.record.events.onInputKeyDown)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onMouseenter() {
				if (this.record.events && this.record.events.onMouseenter) {
					const code = JSON.parse(this.record.events.onMouseenter)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onMouseleave() {
				if (this.record.events && this.record.events.onMouseleave) {
					const code = JSON.parse(this.record.events.onMouseleave)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onPopupScroll() {
				if (this.record.events && this.record.events.onPopupScroll) {
					const code = JSON.parse(this.record.events.onPopupScroll)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onSearch() {
				if (this.record.events && this.record.events.onSearch) {
					const code = JSON.parse(this.record.events.onSearch)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onSelect() {
				if (this.record.events && this.record.events.onSelect) {
					const code = JSON.parse(this.record.events.onSelect)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onOk() {
				if (this.record.events && this.record.events.onOk) {
					const code = JSON.parse(this.record.events.onOk)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onOpenChange() {
				if (this.record.events && this.record.events.onOpenChange) {
					const code = JSON.parse(this.record.events.onOpenChange)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onHoverChange() {
				if (this.record.events && this.record.events.onHoverChange) {
					const code = JSON.parse(this.record.events.onHoverChange)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onKeydown() {
				if (this.record.events && this.record.events.onKeydown) {
					const code = JSON.parse(this.record.events.onKeydown)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			},
			onAfterChange() {
				if (this.record.events && this.record.events.onAfterChange) {
					const code = JSON.parse(this.record.events.onAfterChange)
					try {
						eval(code)
					} catch (e) {
						console.error(e)
					}
				}
			}
		}
	}
</script>
<style lang="less" scoped>
	.slider-box {
		display: flex;
		> .slider {
			flex: 1;
			margin-right: 16px;
		}
		> .number {
			width: 70px;
		}
	}
	.anticon.anticon-question-circle-o {
		margin-left: 5px;
	}
</style>
