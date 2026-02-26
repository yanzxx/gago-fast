<!--
 * @Description: 将数据通过k-form-item组件解析，生成控件
 * @Author: kcz
 * @Date: 2019-12-30 00:37:05
 * @LastEditTime: 2022年3月21日 00点25分
 * @LastEditors: yubaoshan
 * @FilePath: \snowy-form-design\packages\SnowyFormDesign\module\formNode.vue
 -->
<template>
	<div
		class="drag-move-box"
		@click.stop="$emit('handleSelectItem', record)"
		:class="{ active: record.key === selectItem.key }"
	>
		<div class="form-item-box">
			<kFormItem :formConfig="config" :record="record" />
		</div>
		<div v-if="!hideModel" class="show-key-box" v-text="record.label + (record.model ? '/' + record.model : '')" />
		<div
			class="copy"
			:class="record.key === selectItem.key ? 'active' : 'unactivated'"
			@click.stop="$emit('handleCopy')"
		>
			<copy-outlined />
		</div>
		<div
			class="delete"
			:class="record.key === selectItem.key ? 'active' : 'unactivated'"
			@click.stop="$emit('handleDelete')"
		>
			<delete-outlined />
		</div>
	</div>
</template>
<script>
	/*
	 * author kcz
	 * date 2019-11-20
	 * description 通过json生成的单个表单节点
	 */
	import kFormItem from '../../KFormItem/index'
	import { CopyOutlined, DeleteOutlined } from '@ant-design/icons-vue'
	export default {
		components: {
			kFormItem,
			CopyOutlined,
			DeleteOutlined
		},
		props: {
			record: {
				type: Object,
				required: true
			},
			selectItem: {
				type: Object,
				default: () => {}
			},
			config: {
				type: Object,
				required: true
			},
			hideModel: {
				type: Boolean,
				default: false
			}
		}
	}
</script>
