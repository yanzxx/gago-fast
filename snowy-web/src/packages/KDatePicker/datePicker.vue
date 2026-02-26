<!--
 * @Description: 日期选择器
 * @Author: kcz
 * @Date: 2020-01-11 15:38:28
 * @LastEditors: kcz
 * @LastEditTime: 2020-03-28 17:37:49
 -->
<template>
	<div class="">
		<!-- 月份选择 -->
		<a-date-picker
			:style="`width:${record.options.width}`"
			picker="month"
			v-if="record.type === 'date' && record.options.format === 'YYYY-MM' && record.options.range === false"
			:disabled="record.options.disabled || parentDisabled"
			:allowClear="record.options.clearable"
			:placeholder="record.options.placeholder"
			:valueFormat="record.options.format"
			@change="handleSelectChange"
			v-model:value="dateValue"
		/>
		<!-- 日期选择 -->
		<a-date-picker
			:style="`width:${record.options.width}`"
			v-else-if="record.type === 'date' && record.options.range === false"
			:disabled="record.options.disabled || parentDisabled"
			:show-time="record.options.showTime"
			:allowClear="record.options.clearable"
			:placeholder="record.options.placeholder"
			:valueFormat="record.options.format"
			@change="handleSelectChange"
			v-model:value="dateValue"
		/>

		<!-- 范围日期选择 -->
		<a-range-picker
			:style="`width:${record.options.width}`"
			v-else-if="record.type === 'date' && record.options.range === true"
			:show-time="record.options.showTime"
			:disabled="record.options.disabled || parentDisabled"
			:allowClear="record.options.clearable"
			:placeholder="record.options.rangePlaceholder"
			:valueFormat="record.options.format"
			@change="handleSelectChange"
			v-model:value="dateValue"
		/>
	</div>
</template>
<script>
	import dayjs from 'dayjs'
	import weekday from 'dayjs/plugin/weekday'
	import localeData from 'dayjs/plugin/localeData'
	dayjs.extend(weekday)
	dayjs.extend(localeData)
	export default {
		// eslint-disable-next-line vue/require-prop-types
		props: ['record', 'value', 'parentDisabled'],
		data() {
			return {
				dateValue: undefined
			}
		},
		computed: {
			date: {
				get() {
					if (!this.value || (this.record.options.range && this.value.length === 0)) {
						return undefined
					} else if (this.record.options.range) {
						return this.value.map((item) => dayjs(item).format(this.record.options.format))
					} else {
						return dayjs(this.value).format(this.record.options.format)
					}
				},
				set(value) {}
			}
		},
		watch: {
			date: {
				handler(val) {
					this.dateValue = val
				},
				immediate: true,
				deep: true
			}
		},
		methods: {
			handleSelectChange(val) {
				this.dateValue = val
				let date
				if (!val || (this.record.options.range && val.length === 0)) {
					date = ''
				} else if (this.record.options.range) {
					date = val.map((item) => item)
				} else {
					date = val
				}
				this.$emit('change', date)
				this.$emit('input', date)
			}
		}
	}
</script>
