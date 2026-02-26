<template>
	<div
		:class="{
			'layout-width': ['grid', 'table', 'card', 'divider', 'html'].includes(record.type)
		}"
	>
		<!-- 动态表格设计模块 start -->
		<template v-if="record.type === 'batch'">
			<div class="batch-box" :class="{ active: record.key === selectItem.key }" @click.stop="handleSelectItem(record)">
				<a-form-item
					:label="!record.options.showLabel ? '' : record.label"
					:label-col="
						config.layout === 'horizontal' && record.options.showLabel
							? config.labelLayout === 'flex'
								? { style: `width:${config.labelWidth}px` }
								: config.labelCol
							: {}
					"
					:wrapper-col="
						config.layout === 'horizontal' && record.options.showLabel
							? config.labelLayout === 'flex'
								? { style: 'width:auto;flex:1' }
								: config.wrapperCol
							: {}
					"
					:style="
						config.layout === 'horizontal' && config.labelLayout === 'flex' && record.options.showLabel
							? { display: 'flex' }
							: {}
					"
				>
					<div class="draggable-box">
						<draggable
							item-key="key"
							:component-data="{ class: 'list-main' }"
							v-bind="{
								group: insertAllowed ? 'form-draggable' : '',
								ghostClass: 'moving',
								handle: '.drag-move'
							}"
							v-model="record.list"
							@start="$emit('dragStart', $event, record.list)"
							@add="(evt) => handleColAdd(evt, record.list)"
						>
							<template #item="{ element: item }">
								<div class="">
									<div class="">
										<formNode
											:key="item.key"
											class="drag-move"
											v-model:selectItem="selectItem"
											:record="item"
											:parent-record="record"
											:hideModel="hideModel"
											:config="config"
											@handleSelectItem="handleSelectItem"
											@handleColAdd="handleColAdd"
											@handleCopy="$emit('handleCopy')"
											@handleShowRightMenu="handleShowRightMenu"
											@handleDelete="$emit('handleDelete')"
										/>
									</div>
								</div>
							</template>
						</draggable>
					</div>
				</a-form-item>
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
		<!-- 动态表格设计模块 end -->
		<!-- 选择输入列 start -->
		<template v-else-if="record.type === 'selectInputList'">
			<div
				class="select-input-list-box"
				:class="{ active: record.key === selectItem.key }"
				@click.stop="handleSelectItem(record)"
			>
				<a-form-item
					:label="!record.options.showLabel ? '' : record.label"
					:label-col="
						config.layout === 'horizontal' && record.options.showLabel
							? config.labelLayout === 'flex'
								? { style: `width:${config.labelWidth}px` }
								: config.labelCol
							: {}
					"
					:wrapper-col="
						config.layout === 'horizontal' && record.options.showLabel
							? config.labelLayout === 'flex'
								? { style: 'width:auto;flex:1' }
								: config.wrapperCol
							: {}
					"
					:style="
						config.layout === 'horizontal' && config.labelLayout === 'flex' && record.options.showLabel
							? { display: 'flex' }
							: {}
					"
				>
					<div class="column-box" v-for="(column, index) in record.columns" :key="index">
						<div class="check-box">
							<a-checkbox v-if="record.options.multiple" disabled>
								{{ column.label }}
							</a-checkbox>
							<a-radio-group v-else disabled name="radio">
								<a-radio :value="column.value">{{ column.label }}</a-radio>
							</a-radio-group>
						</div>
						<div class="draggable-box">
							<div class="list-main">
								<draggable
									tag="transition-group"
									:component-data="{ name: 'list' }"
									v-bind="{
										group: insertAllowed ? 'form-draggable' : '',
										ghostClass: 'moving',
										animation: 180,
										handle: '.drag-move'
									}"
									item-key="key"
									v-model="column.list"
									@start="$emit('dragStart', $event, column.list)"
									@add="$emit('handleColAdd', $event, column.list)"
								>
									<template #item="{ element: item }">
										<div class="">
											<div class="">
												<formNode
													:key="item.key"
													class="drag-move"
													v-model:selectItem="selectItem"
													:record="item"
													:hideModel="hideModel"
													:config="config"
													@handleSelectItem="handleSelectItem"
													@handleColAdd="handleColAdd"
													@handleCopy="$emit('handleCopy')"
													@handleShowRightMenu="handleShowRightMenu"
													@handleDelete="$emit('handleDelete')"
												/>
											</div>
										</div>
									</template>
								</draggable>
							</div>
						</div>
					</div>
				</a-form-item>
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
		<!-- 选择输入列 end -->
		<!-- 标签Tabs布局 start -->
		<template v-else-if="record.type === 'tabs'">
			<div class="grid-box" :class="{ active: record.key === selectItem.key }" @click.stop="handleSelectItem(record)">
				<a-tabs
					class="grid-row"
					:default-active-key="0"
					:tabBarGutter="record.options.tabBarGutter || null"
					:type="record.options.type"
					:size="record.options.size"
					:tabPosition="record.options.tabPosition"
					:animated="record.options.animated"
				>
					<a-tab-pane v-for="(tabItem, index) in record.columns" :key="index" :tab="tabItem.label">
						<div class="grid-col">
							<div class="draggable-box">
								<div class="list-main">
									<draggable
										tag="transition-group"
										:component-data="{ name: 'list' }"
										v-bind="{
											group: 'form-draggable',
											ghostClass: 'moving',
											animation: 180,
											handle: '.drag-move'
										}"
										item-key="key"
										v-model="tabItem.list"
										@start="$emit('dragStart', $event, tabItem.list)"
										@add="$emit('handleColAdd', $event, tabItem.list)"
									>
										<template #item="{ element: item }">
											<div class="">
												<div class="">
													<layoutItem
														class="drag-move"
														:key="item.key"
														v-model:selectItem="selectItem"
														:startType="startType"
														:insertAllowedType="insertAllowedType"
														:record="item"
														:hideModel="hideModel"
														:config="config"
														@handleSelectItem="handleSelectItem"
														@handleColAdd="handleColAdd"
														@handleCopy="$emit('handleCopy')"
														@handleShowRightMenu="handleShowRightMenu"
														@handleDelete="$emit('handleDelete')"
													/>
												</div>
											</div>
										</template>
									</draggable>
								</div>
							</div>
						</div>
					</a-tab-pane>
				</a-tabs>

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
		<!-- 标签Tabs布局 end -->
		<!-- 栅格布局 start -->
		<template v-else-if="record.type === 'grid'">
			<div class="grid-box" :class="{ active: record.key === selectItem.key }" @click.stop="handleSelectItem(record)">
				<a-row class="grid-row" :gutter="record.options.gutter">
					<a-col class="grid-col" v-for="(colItem, idnex) in record.columns" :key="idnex" :span="colItem.span || 0">
						<div class="draggable-box">
							<div class="list-main">
								<draggable
									tag="transition-group"
									:component-data="{ name: 'list' }"
									v-bind="{
										group: 'form-draggable',
										ghostClass: 'moving',
										animation: 180,
										handle: '.drag-move'
									}"
									item-key="key"
									v-model="colItem.list"
									@start="$emit('dragStart', $event, colItem.list)"
									@add="$emit('handleColAdd', $event, colItem.list)"
								>
									<template #item="{ element: item }">
										<div class="">
											<div class="">
												<layoutItem
													class="drag-move"
													:key="item.key"
													v-model:selectItem="selectItem"
													:startType="startType"
													:insertAllowedType="insertAllowedType"
													:record="item"
													:hideModel="hideModel"
													:config="config"
													@handleSelectItem="handleSelectItem"
													@handleColAdd="handleColAdd"
													@handleCopy="$emit('handleCopy')"
													@handleShowRightMenu="handleShowRightMenu"
													@handleDelete="$emit('handleDelete')"
												/>
											</div>
										</div>
									</template>
									<!--              </transition-group>-->
								</draggable>
							</div>
						</div>
					</a-col>
				</a-row>

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
		<!-- 栅格布局 end -->
		<!-- 卡片布局 start -->
		<template v-else-if="record.type === 'card'">
			<div class="grid-box" :class="{ active: record.key === selectItem.key }" @click.stop="handleSelectItem(record)">
				<a-card class="grid-row" :title="record.label">
					<div class="grid-col">
						<div class="draggable-box">
							<div class="list-main">
								<draggable
									tag="transition-group"
									:component-data="{ name: 'list' }"
									v-bind="{
										group: 'form-draggable',
										ghostClass: 'moving',
										animation: 180,
										handle: '.drag-move'
									}"
									item-key="key"
									v-model="record.list"
									@start="$emit('dragStart', $event, record.list)"
									@add="(evt) => handleColAdd(evt, record.list)"
								>
									<template #item="{ element: item }">
										<div class="">
											<div class="">
												<layoutItem
													class="drag-move"
													:key="item.key"
													v-model:selectItem="selectItem"
													:startType="startType"
													:insertAllowedType="insertAllowedType"
													:record="item"
													:hideModel="hideModel"
													:config="config"
													@handleSelectItem="handleSelectItem"
													@handleColAdd="handleColAdd"
													@handleCopy="$emit('handleCopy')"
													@handleShowRightMenu="handleShowRightMenu"
													@handleDelete="$emit('handleDelete')"
												/>
											</div>
										</div>
									</template>
									<!--              </transition-group>-->
								</draggable>
							</div>
						</div>
					</div>
				</a-card>

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
		<!-- 卡片布局 end -->
		<!-- 表格布局 start -->
		<template v-else-if="record.type === 'table'">
			<div class="table-box" :class="{ active: record.key === selectItem.key }" @click.stop="handleSelectItem(record)">
				<table
					class="table-layout kk-table-9136076486841527"
					:class="{
						bright: record.options.bright,
						small: record.options.small,
						bordered: record.options.bordered
					}"
					:style="record.options.customStyle"
				>
					<tr v-for="(trItem, trIndex) in record.trs" :key="trIndex">
						<td
							class="table-td"
							v-for="(tdItem, tdIndex) in trItem.tds"
							v-show="tdItem.colspan && tdItem.rowspan"
							:key="tdIndex"
							:colspan="tdItem.colspan"
							:rowspan="tdItem.rowspan"
							@contextmenu.prevent="$emit('handleShowRightMenu', $event, record, trIndex, tdIndex)"
						>
							<div class="draggable-box">
								<draggable
									:component-data="{
										class: 'list-main'
									}"
									v-bind="{
										group: 'form-draggable',
										ghostClass: 'moving',
										animation: 180,
										handle: '.drag-move',
										style: { 'min-height': tdItem.rowspan * 80 - 19 + 'px' }
									}"
									item-key="key"
									:list="tdItem.list"
									@start="$emit('dragStart', $event, tdItem.list)"
									@add="(evt) => handleColAdd(evt, tdItem.list)"
								>
									<template #item="{ element: item }">
										<div class="">
											<div class="">
												<layoutItem
													class="drag-move"
													:key="item.key"
													v-model:selectItem="selectItem"
													:startType="startType"
													:insertAllowedType="insertAllowedType"
													:record="item"
													:hideModel="hideModel"
													:config="config"
													@handleSelectItem="handleSelectItem"
													@handleColAdd="handleColAdd"
													@handleCopy="$emit('handleCopy')"
													@handleShowRightMenu="handleShowRightMenu"
													@handleDelete="$emit('handleDelete')"
												/>
											</div>
										</div>
									</template>
								</draggable>
							</div>
						</td>
					</tr>
				</table>

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
		<!-- 表格布局 end -->
		<template v-else>
			<formNode
				:key="record.key"
				v-model:selectItem="selectItem"
				:record="record"
				:config="config"
				:hideModel="hideModel"
				@handleSelectItem="handleSelectItem"
				@handleCopy="$emit('handleCopy')"
				@handleDelete="$emit('handleDelete')"
				@handleShowRightMenu="$emit('handleShowRightMenu')"
			/>
		</template>
	</div>
</template>
<script>
	/*
	 * author kcz
	 * date 2019-11-20
	 * description 使用递归组件调用自己，生成布局结构及表单
	 */
	import draggable from 'vuedraggable-es'
	import formNode from './formNode.vue'
	import { CopyOutlined, DeleteOutlined } from '@ant-design/icons-vue'
	export default {
		name: 'LayoutItem',
		components: {
			formNode,
			draggable,
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
				required: true
			},
			config: {
				type: Object,
				required: true
			},
			startType: {
				type: String,
				required: true
			},
			insertAllowedType: {
				type: Array,
				required: true
			},
			hideModel: {
				type: Boolean,
				default: false
			}
		},
		computed: {
			insertAllowed() {
				return this.insertAllowedType.includes(this.startType)
			}
		},
		methods: {
			handleShowRightMenu(e, record, trIndex, tdIndex) {
				this.$emit('handleShowRightMenu', e, record, trIndex, tdIndex)
			},
			handleSelectItem(record) {
				this.$emit('handleSelectItem', record, this.record)
			},
			handleColAdd(e, list) {
				this.$emit('handleColAdd', e, list, false, this.record)
			}
		}
	}
</script>
