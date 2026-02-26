<!--
 * @Description: 设计器组件事件编辑或新增
 * @Author: yubaoshan
 * @Date: 2023年6月18日01:02:03
 * @LastEditors: yubaoshan
 * @LastEditTime: 2023年6月18日01:02:14
 -->
<template>
	<a-modal
		title="编写代码"
		:visible="visible"
		@cancel="handleCancel"
		@ok="handleOk"
		wrapClassName="event-code-form-modal-9136076486841527"
		style="top: 20px; height: 500px"
		width="850px"
		:destroyOnClose="true"
	>
		<div style="background-color: #f7f7f7; color: #b2b0b0; padding-left: 30px">
			{{ componentsKey }}.{{ functionName }} () {
		</div>
		<codemirror style="height: 400px" :options="cmOptions" ref="myEditor" v-model:value="editorJson" />
		<div style="background-color: #f7f7f7; color: #b2b0b0; padding-left: 30px; margin-top: -5px">}</div>
	</a-modal>
</template>
<script>
	import Codemirror from 'codemirror-editor-vue3'
	export default {
		name: 'EventCodeFormModal',
		components: {
			Codemirror
		},
		data() {
			return {
				visible: false,
				componentsKey: '',
				functionName: '',
				editorJson: '',
				cmOptions: {
					mode: 'javascript', // Language mode
					theme: 'dracula', // Theme
					lineNumbers: true, // Show line number
					smartIndent: true, // Smart indent
					indentUnit: 2, // The smart indent unit is 2 spaces in length
					foldGutter: true, // Code folding
					styleActiveLine: true // Display the style of the selected row
				}
			}
		},
		methods: {
			handleCancel() {
				this.componentsKey = ''
				this.functionName = ''
				this.editorJson = ''
				this.visible = false
			},
			handleOk() {
				let value = ''
				if (this.editorJson) {
					value = JSON.stringify(this.editorJson, null, '\t')
				}
				// const value = JSON.stringify(this.editorJson, null, '\t')
				// 使用回调事件改变值
				this.$emit('eventCodeFormBack', this.componentsKey, this.functionName, value)
				this.handleCancel()
			}
		}
	}
</script>
