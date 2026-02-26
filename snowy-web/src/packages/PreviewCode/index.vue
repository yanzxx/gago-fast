<template>
	<div>
		<div class="json-box-9136076486841527">
			<codemirror style="height: 100%" :options="cmOptions" ref="myEditor" v-model:value="editorJson"></codemirror>
		</div>
		<div class="copy-btn-box-9136076486841527">
			<a-button
				@click="handleCopyJson"
				type="primary"
				class="copy-btn"
				data-clipboard-action="copy"
				:data-clipboard-text="editorJson"
			>
				复制数据
			</a-button>
			<a-button @click="handleExportJson" type="primary"> 导出代码 </a-button>
		</div>
	</div>
</template>
<script>
	// 剪切板组件
	import Clipboard from 'clipboard'
	import Codemirror from 'codemirror-editor-vue3'

	// language
	import 'codemirror/mode/javascript/javascript.js'
	import 'codemirror/mode/vue/vue.js'
	import 'codemirror/mode/htmlembedded/htmlembedded.js'
	export default {
		name: 'PreviewCode',

		components: {
			Codemirror
		},
		props: {
			fileFormat: {
				type: String,
				default: 'javascript'
			},
			editorJson: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				visible: false,
				cmOptions: {
					mode: this.getMode(), // Language mode
					// theme: "dracula", // Theme
					lineNumbers: true, // Show line number
					smartIndent: true, // Smart indent
					indentUnit: 2, // The smart indent unit is 2 spaces in length
					foldGutter: true, // Code folding
					styleActiveLine: true // Display the style of the selected row
				}
			}
		},
		methods: {
			getMode() {
				if (this.fileFormat === 'vue') {
					return this.fileFormat
				}
				return 'text/' + this.fileFormat
			},
			exportData(data, fileName = `demo.${this.fileFormat}`) {
				let content = 'data:text/csv;charset=utf-8,'
				content += data
				var encodedUri = encodeURI(content)
				var actions = document.createElement('a')
				actions.setAttribute('href', encodedUri)
				actions.setAttribute('download', fileName)
				actions.click()
			},
			handleExportJson() {
				// 导出JSON
				this.exportData(this.editorJson, 'demo.json')
			},
			handleCopyJson() {
				// 复制数据
				const clipboard = new Clipboard('.copy-btn')
				clipboard.on('success', () => {
					this.$message.success('复制成功')
				})
				clipboard.on('error', () => {
					this.$message.error('复制失败')
				})
				setTimeout(() => {
					// 销毁实例
					clipboard.destroy()
				}, 122)
			}
		}
	}
</script>
