<template>
	<div id="codeEditBox"></div>
	<div class="bottomBtn">
		<a-button class="copybtn" @click="copySql">复制sql</a-button>
		<a-button type="primary" @click="handleOk">确认</a-button>
		<a-button class="lastbtn" @click="onCancel">取消</a-button>
	</div>
</template>
<script setup>
	import $ from 'jquery'
	import { ref, toRaw } from 'vue'
	import * as monaco from 'monaco-editor'
	import { format } from 'sql-formatter'
	import { pythonCompletion, sqlCompletion, cppCompletion, javaCompletion, csharpCompletion } from '@/utils/completion'

	const emit = defineEmits(['getcopySql', 'gethandleOk', 'getonCancel'])
	const editor = ref(null)
	const language = ref('sql')
	const editorTheme = ref('vs-dark')
	$(document).ready(function () {
		initEditor()
		// handleFormat()
	})
	const initEditor = () => {
		// 初始化编辑器，确保dom已经渲染
		editor.value = monaco.editor.create(document.getElementById('codeEditBox'), {
			value: `-- DROP TABLE IF EXISTS t_basis;
CREATE TABLE "t_basis" (
  "id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "delete_flag" varchar(10) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL DEFAULT now(),
  "create_user" varchar(32) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) NOT NULL DEFAULT now(),
  "update_user" varchar(32) COLLATE "pg_catalog"."default",
  PRIMARY KEY ("id")
);
COMMENT ON COLUMN "t_basis"."id" IS '主键';
COMMENT ON COLUMN "t_basis"."delete_flag" IS '删除状态';
COMMENT ON COLUMN "t_basis"."create_time" IS '创建时间';
COMMENT ON COLUMN "t_basis"."update_time" IS '更新时间';
COMMENT ON COLUMN "t_basis"."create_user" IS '创建人';
COMMENT ON COLUMN "t_basis"."update_user" IS '更新人';`, //编辑器初始显示文字
			language: language.value, //语言支持自行查阅demo
			theme: editorTheme.value, //官方自带三种主题vs, hc-black, or vs-dark
			selectOnLineNumbers: true, //显示行号
			roundedSelection: false, //选区是否有圆角
			readOnly: true, // 只读
			cursorStyle: 'line', //光标样式
			scrollBeyondLastLine: false, //设置编辑器是否可以滚动到最后一行之后
			automaticLayout: true, //自动布局
			glyphMargin: true, //字形边缘
			useTabStops: false,
			fontSize: 15, //字体大小
			autoIndent: true, //控制编辑器在用户键入、粘贴、移动或缩进行时是否应自动调整缩进
			quickSuggestionsDelay: 100 //代码提示延时
		})

		// 监听值的变化
		// editor.value.onDidChangeModelContent((val) => {
		//   console.log(val.changes[0].text)
		// })

		// 创建代码提醒
		pythonCompletion
		sqlCompletion
		cppCompletion
		csharpCompletion
		javaCompletion
	}
	const handleFormat = () => {
		let lan = toRaw(editor.value).getModel().getLanguageId()
		// console.log(lan)
		let content = toRaw(editor.value).getValue()
		if (lan == 'sql') {
			toRaw(editor.value).setValue(format(content))
		} else if (lan == 'json') {
			toRaw(editor.value).trigger('anyString', 'editor.action.formatDocument')
			toRaw(editor.value).setValue(content)
		}
	}
	const copySql = () => {
		let content = toRaw(editor.value).getValue()
		// toRaw(editor.value).setValue(format(content))
		emit('getcopySql', content)
	}
	const handleOk = () => {
		emit('gethandleOk')
	}
	const onCancel = () => {
		emit('getonCancel')
	}
</script>
<style scoped>
	#codeEditBox {
		width: 100%;
		height: 330px;
		/* background-color: yellow; */
	}
	.bottomBtn {
		text-align: right;
		margin-top: 15px;
	}
	.ant-btn {
		margin-right: 10px;
	}
	.lastbtn {
		margin-right: 0;
	}
	.copybtn {
		background-color: #67c23a;
		border-color: #67c23a;
		color: #fff;
	}
</style>
