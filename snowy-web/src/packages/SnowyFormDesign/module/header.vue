<!--
 * @Description: 头部
 * @Author: kcz
 * @Date: 2019-12-30 00:37:05
 * @LastEditors: xiaonuobase
 * @LastEditTime: 2022-03-20 23:29:02
 -->
<template>
	<header class="header" v-text="title"></header>
</template>
<script>
	export default {
		props: {
			title: {
				type: String,
				default: '表单设计器'
			}
		}
	}
</script>
