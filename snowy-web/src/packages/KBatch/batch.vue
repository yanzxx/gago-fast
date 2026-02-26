<!--
 * @Description: 动态表格 用于批量填入数据
 * @Author: kcz
 * @Date: 2020-03-27 18:36:56
 * @LastEditors: kcz
 * @LastEditTime: 2021-05-14 14:04:14
 -->
<template>
	<div class="table-wrapper">
		<a-table
			class="batch-table"
			:pagination="false"
			:rowKey="(record) => record.key"
			:columns="columns"
			:dataSource="dynamicValidateForm.domains"
			bordered
			:scroll="{
				x: listLength * 190 + 80 + (!record.options.hideSequence ? 60 : 0),
				y: 400
			}"
		>
			<template #bodyCell="{ column, record: item, index }">
				<template v-if="column.dataIndex === 'dynamic-opr-button'">
					<MinusCircleOutlined
						title="删除该行"
						v-if="!disabled"
						class="dynamic-opr-button"
						type="minus-circle-o"
						@click="removeDomain(item)"
					/>
					<CopyOutlined
						title="复制添加"
						v-if="!disabled"
						type="copy-o"
						class="dynamic-opr-button"
						@click="copyDomain(item)"
					/>
				</template>

				<template v-for="recordItem in record.list" :key="recordItem.key + '1'">
					<KFormModelItem
						v-if="recordItem.key === column.dataIndex"
						:record="getItemRecord(recordItem, dynamicValidateForm.domains, index, column.dataIndex)"
						:config="config"
						:parentDisabled="disabled"
						:index="index"
						:domains="dynamicValidateForm.domains"
						:dynamicData="dynamicData"
						:value="getValue(dynamicValidateForm.domains, index, column.dataIndex)"
						@input="(val) => handleInput(val, index, recordItem.model)"
						:recordKey="record.key"
					/>
				</template>
			</template>
		</a-table>
		<a-button type="dashed" :disabled="disabled" @click="addDomain" style="margin-top: 8px">
			<plus-outlined />
			增加
		</a-button>
	</div>
</template>

<script>
	import KFormModelItem from './module/KFormModelItem.vue'
	import { PlusOutlined, MinusCircleOutlined, CopyOutlined } from '@ant-design/icons-vue'
	export default {
		name: 'KBatch',

		components: {
			KFormModelItem,
			PlusOutlined,
			MinusCircleOutlined,
			CopyOutlined
		},
		props: ['record', 'value', 'dynamicData', 'config', 'parentDisabled'],
		data() {
			return {
				dynamicValidateForm: {
					domains: []
				}
			}
		},
		computed: {
			listLength() {
				return this.record.list.filter((item) => !item.options.hidden).length
			},
			columns() {
				const columns = []
				if (!this.record.options.hideSequence) {
					columns.push({
						title: '序号',
						dataIndex: 'sequence_index_number',
						width: '60px',
						align: 'center',
						customRender: ({ text, record, index }) => {
							return index + 1
						}
					})
				}

				columns.push(
					...this.record.list
						.filter((item) => !item.options.hidden)
						.map((item, index) => {
							return {
								title: item.label,
								dataIndex: item.key,
								width: index === this.record.list.length - 1 ? '' : '190px'
							}
						})
				)

				columns.push({
					title: '操作',
					dataIndex: 'dynamic-opr-button',
					fixed: 'right',
					width: '80px',
					align: 'center'
				})

				return columns
			},
			disabled() {
				return this.record.options.disabled || this.parentDisabled
			}
		},
		watch: {
			value: {
				// value 需要深度监听及默认先执行handler函数
				handler(val) {
					if (val.options.defaultValue) {
						this.dynamicValidateForm.domains = val.options.defaultValue
					}
				},
				immediate: true,
				deep: true
			}
		},
		methods: {
			getValue(obj, index, dataIndex) {
				if (dataIndex) {
					return obj[index][dataIndex]
				}
			},
			getItemRecord(record, obj, index, dataIndex) {
				let values = this.getValue(obj, index, dataIndex)
				// 设置他的验证,这里写的吧老俞害惨了
				this.handleInput(values, index, dataIndex)
				return record
			},
			validationSubform() {
				return true
			},
			resetForm() {
				this.$refs.dynamicValidateFormTest.resetFields()
			},
			removeDomain(item) {
				const index = this.dynamicValidateForm.domains.indexOf(item)
				if (index !== -1) {
					this.dynamicValidateForm.domains.splice(index, 1)
				}
			},
			copyDomain(record) {
				const data = {}
				this.record.list.forEach((item) => {
					data[item.model] = record[item.model]
				})
				this.dynamicValidateForm.domains.push({
					...data,
					key: Date.now()
				})
				this.handleInput()
			},
			addDomain() {
				const data = {}
				this.record.list.forEach((item) => {
					data[item.model] = item.options.defaultValue
				})
				this.dynamicValidateForm.domains.push({
					...data,
					key: Date.now()
				})
				this.handleInput()
			},
			handleInput(val, index, key) {
				this.$emit('change', this.dynamicValidateForm.domains)
				this.$emit('batchChange', val, `${this.record.model}.${index}.${key}`)
			}
		}
	}
</script>
<style scoped>
	.table-wrapper {
		width: 100%;
	}
	.dynamic-opr-button:last {
		margin-left: 0px;
	}
	.dynamic-opr-button {
		cursor: pointer;
		position: relative;
		color: #999;
		transition: all 0.3s;
		margin-left: 6px;
	}
	.dynamic-opr-button:hover {
		color: #e89;
	}
	.dynamic-opr-button[disabled] {
		cursor: not-allowed;
		opacity: 0.5;
	}
</style>
