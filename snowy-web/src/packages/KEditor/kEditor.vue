<!--
 * @Description: 对vue-quill-editor封装
 * @Author: kcz
 * @Date: 2020-03-30 12:44:03
 * @LastEditors: kcz
 * @LastEditTime: 2020-04-26 19:21:27
 -->
<template>
	<div class="ql-editor-class" :class="{ chinesization: record.options.chinesization }">
		<quill-editor
			:style="{ height: `${record.options.height}px` }"
			ref="vueQuillEditor"
			:options="editorOption"
			:readOnly="record.options.disabled || parentDisabled"
			@blur="onEditorBlur($event)"
			@focus="onEditorFocus($event)"
			@update:content="onEditorChange($event)"
		/>
	</div>
</template>
<script>
	import 'quill/dist/quill.core.css'
	import 'quill/dist/quill.snow.css'
	import 'quill/dist/quill.bubble.css'
	import { QuillEditor } from '@vueup/vue-quill'
	import '@vueup/vue-quill/dist/vue-quill.snow.css'
	import { isEmpty } from 'lodash-es'

	export default {
		name: 'KEditor',
		components: { QuillEditor },
		props: ['value', 'record', 'parentDisabled'],
		data() {
			return {
				editorOption: {
					placeholder: this.record.options.placeholder,
					modules: {
						toolbar: [
							['bold', 'italic', 'underline', 'strike'],
							['blockquote', 'code-block'],
							[{ header: 1 }, { header: 2 }],
							[{ list: 'ordered' }, { list: 'bullet' }],
							[{ script: 'sub' }, { script: 'super' }],
							[{ indent: '-1' }, { indent: '+1' }],
							[{ direction: 'rtl' }],
							[{ size: ['small', false, 'large', 'huge'] }],
							[{ header: [1, 2, 3, 4, 5, 6, false] }],
							[{ color: [] }, { background: [] }],
							[{ font: [] }],
							[{ align: [] }],
							['clean'],
							['link', 'image', 'video']
						]
					}
				}
			}
		},
		watch: {
			record: {
				// value 需要深度监听及默认先执行handler函数
				handler(val) {
					if (!isEmpty(this.record.options.defaultValue)) {
						this.$nextTick(() => {
							this.$refs.vueQuillEditor.setHTML(this.record.options.defaultValue)
						})
					}
				},
				immediate: true,
				deep: true
			}
		},
		methods: {
			onEditorBlur() {}, // 失去焦点事件
			onEditorFocus() {}, // 获得焦点事件
			onEditorChange() {
				this.$emit('change', this.$refs.vueQuillEditor.getHTML())
			}
		}
	}
</script>
<style lang="less" scoped>
	.ql-editor-class {
		-webkit-box-sizing: border-box;
		box-sizing: border-box;
		line-height: 1.42;
		height: 100%;
		outline: none;
		tab-size: 4;
		-moz-tab-size: 4;
		text-align: left;
		word-wrap: break-word;
	}
</style>
