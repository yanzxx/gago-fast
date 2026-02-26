<!--
 * @Description: 时间选择器
 * @Author: kcz
 * @Date: 2020-01-11 17:30:48
 * @LastEditors: kcz
 * @LastEditTime: 2020-03-28 17:35:43
 -->
<template>
	<div class="">
		<a-time-picker
			:style="`width:${record.options.width}`"
			:disabled="record.options.disabled || parentDisabled"
			:allowClear="record.options.clearable"
			:placeholder="record.options.placeholder"
			:format="record.options.format"
			@change="handleSelectChange"
			v-model:value="time"
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
		computed: {
			time: {
				get() {
					if (!this.value) {
						return undefined
					} else {
						return dayjs(this.value, this.record.options.format)
					}
				},
				set(value) {
					this.$emit('update:value', value)
				}
			}
		},
		methods: {
			handleSelectChange(val) {
				let time
				if (!val) {
					time = ''
				} else {
					time = dayjs(val).format(this.record.options.format)
				}
				this.$emit('change', time)
				this.$emit('input', time)
			}
		}
	}
</script>
