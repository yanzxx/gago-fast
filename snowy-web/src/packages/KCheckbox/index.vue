<template>
	<a-checkbox @change="handleChange" :checked="value">
		{{ label }}
	</a-checkbox>
</template>
<script>
	/*
	 * author kcz
	 * date 2019-11-20
	 * description 多选框组件,改成v-model Boolean值
	 */
	export default {
		name: 'KCheckbox',
		model: {},
		props: {
			value: {
				type: Boolean,
				default: false
			},
			label: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				chackboxVal: false
			}
		},
		computed: {
			_val() {
				this.handleSetChackboxVal(this.value)
				return this.value
			}
		},
		methods: {
			handleChange(e) {
				this.$emit('update:value', e.target.checked)
			},
			handleSetChackboxVal(val) {
				this.chackboxVal = val
			}
		}
	}
</script>
