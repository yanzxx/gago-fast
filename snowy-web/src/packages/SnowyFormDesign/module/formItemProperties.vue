<template>
	<div class="properties-content kk-checkbox">
		<div class="properties-body">
			<a-empty v-show="selectItem.key === ''" class="hint-box" description="未选择控件" />

			<a-form v-show="selectItem.key !== ''" layout="vertical" :model="selectItem">
				<div
					v-if="
						(selectItem.type !== 'alert') &
						(selectItem.type !== 'text') &
						(selectItem.type !== 'html') &
						(selectItem.type !== 'button') &
						(selectItem.type !== 'divider') &
						(selectItem.type !== 'card') &
						(selectItem.type !== 'tabs') &
						(selectItem.type !== 'grid') &
						(selectItem.type !== 'table')
					"
				>
					<a-form-item v-if="canSelectPrimaryTableZhuBiao" label="主表">
						<a-select
							v-model:value="selectItem.selectTable"
							:options="databaseTables"
							placeholder="请选择主表"
							:disabled="canSelectPrimaryTableZhuBiao"
						/>
					</a-form-item>
					<a-form-item v-if="canSelectChildTable" label="选择子表">
						<a-select
							v-model:value="selectItem.selectTable"
							:options="childTables"
							@change="changeChildTable"
							placeholder="请选择子表"
						/>
					</a-form-item>
					<a-form-item v-if="hasDatabase && !canSelectChildTable" label="选择字段">
						<a-select
							v-model:value="selectItem.selectColumn"
							:options="tableColumns"
							@change="changeColumn"
							placeholder="请选择字段"
						/>
					</a-form-item>
					<a-form-item v-show="false">
						<a-input v-model:value="selectItem.selectTableType" />
					</a-form-item>
				</div>
				<a-form-item v-if="typeof selectItem.label !== 'undefined'" label="标签">
					<a-input v-model:value="selectItem.label" placeholder="请输入" />
				</a-form-item>

				<a-form-item v-if="!hideModel && typeof selectItem.model !== 'undefined'" label="数据字段">
					<a-input v-model:value="selectItem.model" placeholder="请输入" :disabled="canSelectChildDisabled" />
				</a-form-item>
				<!-- input type start -->
				<a-form-item v-if="selectItem.type === 'input'" label="输入框类型">
					<a-input v-model:value="options.type" placeholder="请输入" />
				</a-form-item>
				<!-- input type end -->
				<a-form-item v-if="typeof options.rangePlaceholder !== 'undefined' && options.range" label="占位内容">
					<a-input v-model:value="options.rangePlaceholder[0]" placeholder="请输入" />
					<a-input v-model:value="options.rangePlaceholder[1]" placeholder="请输入" />
				</a-form-item>

				<a-form-item v-else-if="typeof options.placeholder !== 'undefined'" label="占位内容">
					<a-input v-model:value="options.placeholder" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'textarea'" label="自适应内容高度">
					<a-input-number v-model:value="options.minRows" style="width: 100%" placeholder="最小高度" />
					<a-input-number v-model:value="options.maxRows" style="width: 100%" placeholder="最大高度" />
				</a-form-item>
				<a-form-item v-if="typeof options.width !== 'undefined'" label="宽度">
					<a-input v-model:value="options.width" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="typeof options.height !== 'undefined'" label="高度">
					<a-input-number v-model:value="options.height" />
				</a-form-item>
				<a-form-item v-if="typeof options.step !== 'undefined'" label="步长">
					<a-input-number v-model:value="options.step" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="typeof options.min !== 'undefined'" label="最小值">
					<a-input-number v-model:value="options.min" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="typeof options.max !== 'undefined'" label="最大值">
					<a-input-number v-model:value="options.max" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="typeof options.maxLength !== 'undefined'" label="最大长度">
					<a-input-number v-model:value="options.maxLength" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="typeof options.tabBarGutter !== 'undefined'" label="标签间距">
					<a-input-number v-model:value="options.tabBarGutter" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="typeof options.precision !== 'undefined'" label="数值精度">
					<a-input-number v-model:value="options.precision" :min="0" :max="50" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="typeof options.dictCode !== 'undefined'" label="dictCode">
					<a-input v-model:value="options.dictCode"></a-input>
				</a-form-item>
				<!-- 选项配置及动态数据配置 start -->
				<a-form-item v-if="typeof options.options !== 'undefined'" label="选项配置">
					<a-radio-group v-model:value="options.dynamic" button-style="solid">
						<a-radio-button :value="false">静态数据</a-radio-button>
						<a-radio-button :value="true">动态数据</a-radio-button>
					</a-radio-group>

					<a-select
						v-if="options.dynamic"
						class="mt-2"
						:options="dictDataOptions"
						v-model:value="options.dynamicKey"
						@change="changeDictData"
						placeholder="请选择动态数据"
					></a-select>

					<KChangeOption v-else v-model:value="options.options" class="fd-mt-2" />
				</a-form-item>
				<!-- 选项配置及动态数据配置 end -->
				<!-- tabs配置 start -->
				<a-form-item
					v-if="['tabs', 'selectInputList'].includes(selectItem.type)"
					:label="selectItem.type === 'tabs' ? '页签配置' : '列选项配置'"
				>
					<KChangeOption v-model:value="selectItem.columns" type="tab" />
				</a-form-item>
				<!-- tabs配置 end -->
				<a-form-item v-if="selectItem.type === 'grid'" label="栅格间距">
					<a-input-number v-model:value="selectItem.options.gutter" placeholder="请输入" />
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'grid'" label="列配置项">
					<KChangeOption v-model:value="selectItem.columns" type="colspan" />
				</a-form-item>

				<a-form-item v-if="selectItem.type === 'switch'" label="默认值">
					<a-switch v-model:checked="options.defaultValue" />
				</a-form-item>
				<a-form-item v-if="['number', 'slider'].indexOf(selectItem.type) >= 0" label="默认值">
					<a-input-number
						v-model:value="options.defaultValue"
						:step="options.step"
						:min="options.min || -Infinity"
						:max="options.max || Infinity"
					/>
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'rate'" label="默认值">
					<a-rate v-model:value="options.defaultValue" :allow-half="options.allowHalf" :count="options.max" />
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'select'" label="默认值">
					<a-select v-model:value="options.defaultValue" :options="options.options" />
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'radio'" label="默认值">
					<a-radio-group v-model:value="options.defaultValue" :options="options.options" />
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'checkbox'" label="默认值">
					<a-checkbox-group v-model:value="options.defaultValue" :options="options.options" />
				</a-form-item>
				<!-- 日期选择器默认值 start -->
				<a-form-item v-if="selectItem.type === 'date' && !options.range" label="默认值">
					<a-input
						v-if="!options.range"
						v-model:value="options.defaultValue"
						:placeholder="typeof options.format === 'undefined' ? '' : options.format"
					/>
					<a-input
						v-if="options.range"
						v-model:value="options.rangeDefaultValue[0]"
						:placeholder="typeof options.format === 'undefined' ? '' : options.format"
					/>
					<a-input
						v-if="options.range"
						v-model:value="options.rangeDefaultValue[1]"
						:placeholder="typeof options.format === 'undefined' ? '' : options.format"
					/>
				</a-form-item>
				<!-- 日期选择器默认值 start -->
				<a-form-item
					v-if="
						!['number', 'radio', 'checkbox', 'date', 'rate', 'select', 'switch', 'slider', 'html'].includes(
							selectItem.type
						) && typeof options.defaultValue !== 'undefined'
					"
					label="默认值"
				>
					<a-input
						v-model:value="options.defaultValue"
						:placeholder="typeof options.format === 'undefined' ? '请输入' : options.format"
					/>
				</a-form-item>
				<!-- 修改html -->
				<a-form-item v-if="selectItem.type === 'html'" label="默认值">
					<a-textarea v-model:value="options.defaultValue" :auto-size="{ minRows: 4, maxRows: 8 }" />
				</a-form-item>
				<a-form-item v-if="typeof options.format !== 'undefined'" label="时间格式">
					<a-input
						:disabled="options.showTime"
						v-model:value="getOptionFormat"
						placeholder="时间格式如：YYYY-MM-DD HH:mm:ss"
					/>
				</a-form-item>

				<a-form-item v-if="typeof options.orientation !== 'undefined'" label="标签位置">
					<a-radio-group v-model:value="options.orientation" button-style="solid">
						<a-radio-button value="left">左</a-radio-button>
						<a-radio-button value="">居中</a-radio-button>
						<a-radio-button value="right">右</a-radio-button>
					</a-radio-group>
				</a-form-item>
				<!-- 页签位置 start -->
				<a-form-item v-if="selectItem.type === 'tabs'" label="页签位置">
					<a-radio-group v-model:value="options.tabPosition" button-style="solid">
						<a-radio value="top">top</a-radio>
						<a-radio value="right">right</a-radio>
						<a-radio value="bottom">bottom</a-radio>
						<a-radio value="left">left</a-radio>
					</a-radio-group>
				</a-form-item>
				<!-- 页签位置 end -->
				<!-- 页签类型 start -->
				<a-form-item v-if="selectItem.type === 'tabs'" label="页签类型">
					<a-radio-group v-model:value="options.type" button-style="solid">
						<a-radio-button value="line">line</a-radio-button>
						<a-radio-button value="card">card</a-radio-button>
					</a-radio-group>
				</a-form-item>
				<!-- 页签类型 end -->
				<!-- 页签大小 start -->
				<a-form-item v-if="typeof options.size !== 'undefined'" label="大小">
					<a-radio-group v-model:value="options.size" button-style="solid">
						<a-radio-button value="large">large</a-radio-button>
						<a-radio-button value="default">default</a-radio-button>
						<a-radio-button value="small">small</a-radio-button>
					</a-radio-group>
				</a-form-item>
				<!-- 页签大小 end -->
				<a-form-item v-if="selectItem.type === 'button'" label="类型">
					<a-radio-group v-model:value="options.type" button-style="solid">
						<a-radio value="primary">Primary</a-radio>
						<a-radio value="default">Default</a-radio>
						<a-radio value="dashed">Dashed</a-radio>
						<a-radio value="danger">Danger</a-radio>
					</a-radio-group>
				</a-form-item>
				<!-- 下载方式 start -->
				<a-form-item v-if="typeof options.downloadWay !== 'undefined'" label="下载方式">
					<a-radio-group v-model:value="options.downloadWay" button-style="solid">
						<a-radio-button value="a">a标签</a-radio-button>
						<a-radio-button value="ajax">ajax</a-radio-button>
						<a-radio-button value="dynamic">动态函数</a-radio-button>
					</a-radio-group>
					<a-input
						v-show="options.downloadWay === 'dynamic'"
						v-model:value="options.dynamicFun"
						placeholder="动态函数名"
					></a-input>
				</a-form-item>
				<!-- 下载方式 end -->
				<a-form-item v-if="selectItem.type === 'button'" label="按钮操作">
					<a-radio-group v-model:value="options.handle" button-style="solid">
						<a-radio-button value="submit">提交</a-radio-button>
						<a-radio-button value="reset">重置</a-radio-button>
						<a-radio-button value="dynamic">动态函数</a-radio-button>
					</a-radio-group>
					<a-input
						v-show="options.handle === 'dynamic'"
						v-model:value="options.dynamicFun"
						placeholder="动态函数名"
					></a-input>
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'alert'" label="辅助描述">
					<a-input v-model:value="options.description" placeholder="请输入"></a-input>
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'alert'" label="类型">
					<a-radio-group v-model:value="options.type" button-style="solid">
						<a-radio value="success">success</a-radio>
						<a-radio value="info">info</a-radio>
						<a-radio value="warning">warning</a-radio>
						<a-radio value="error">error</a-radio>
					</a-radio-group>
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'alert'" label="操作属性">
					<kCheckbox v-model:value="options.showIcon" label="显示图标" />
					<kCheckbox v-model:value="options.banner" label="无边框" />
					<kCheckbox v-model:value="options.closable" label="可关闭" />
				</a-form-item>
				<!-- 上传图片 -->
				<a-form-item v-if="selectItem.type === 'uploadImg'" label="样式">
					<a-radio-group v-model:value="options.listType" button-style="solid">
						<a-radio-button value="text">text</a-radio-button>
						<a-radio-button value="picture">picture</a-radio-button>
						<a-radio-button value="picture-card">card</a-radio-button>
					</a-radio-group>
				</a-form-item>
				<!-- 上传数量 -->
				<a-form-item v-if="typeof options.limit !== 'undefined'" label="最大上传数量">
					<a-input-number v-model:value="options.limit" :min="1" />
				</a-form-item>

				<!-- scrollY -->
				<a-form-item v-if="typeof options.scrollY !== 'undefined'" label="scrollY">
					<a-input-number v-model:value="options.scrollY" :min="0" />
				</a-form-item>

				<!-- 上传地址 -->
				<a-form-item v-if="typeof options.action !== 'undefined'" label="上传地址">
					<a-input v-model:value="options.action" placeholder="请输入"></a-input>
				</a-form-item>

				<!-- 文件name -->
				<a-form-item v-if="typeof options.fileName !== 'undefined'" label="文件name">
					<a-input v-model:value="options.fileName" placeholder="请输入"></a-input>
				</a-form-item>
				<!-- 上传额外参数 -->
				<a-form-item v-if="typeof options.data !== 'undefined'" label="额外参数（JSON格式）">
					<a-textarea v-model:value="options.data" placeholder="严格JSON格式"></a-textarea>
				</a-form-item>
				<!-- 文字对齐方式 -->
				<a-form-item v-if="selectItem.type === 'text'" label="文字对齐方式">
					<a-radio-group v-model:value="options.textAlign" button-style="solid">
						<a-radio-button value="left">左</a-radio-button>
						<a-radio-button value="center">居中</a-radio-button>
						<a-radio-button value="right">右</a-radio-button>
					</a-radio-group>
				</a-form-item>
				<!-- 文字字体 -->
				<a-form-item v-if="selectItem.type === 'text'" label="字体属性设置">
					<div class="layout-items-center">
						<color-picker v-model:pureColor="options.color" />
						<a-select
							v-model:value="options.fontFamily"
							:options="familyOptions"
							style="width: 36%; margin-left: 2%; vertical-align: bottom"
						/>
						<a-select
							v-model:value="options.fontSize"
							:options="sizeOptions"
							style="width: 35%; margin-left: 2%; vertical-align: bottom"
						/>
					</div>
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'text'" label="操作属性">
					<kCheckbox v-model:value="options.showRequiredMark" label="显示必选标记" />
				</a-form-item>

				<a-form-item
					v-if="
						typeof options.hidden !== 'undefined' ||
						typeof options.disabled !== 'undefined' ||
						typeof options.readonly !== 'undefined' ||
						typeof options.clearable !== 'undefined' ||
						typeof options.multiple !== 'undefined' ||
						typeof options.range !== 'undefined' ||
						typeof options.showTime !== 'undefined' ||
						typeof options.allowHalf !== 'undefined' ||
						typeof options.showInput !== 'undefined' ||
						typeof options.animated !== 'undefined'
					"
					label="操作属性"
				>
					<kCheckbox v-if="typeof options.hidden !== 'undefined'" v-model:value="options.hidden" label="隐藏" />
					<kCheckbox v-if="typeof options.disabled !== 'undefined'" v-model:value="options.disabled" label="禁用" />
					<kCheckbox v-if="typeof options.readonly !== 'undefined'" v-model:value="options.readonly" label="只读" />
					<kCheckbox v-if="typeof options.clearable !== 'undefined'" v-model:value="options.clearable" label="可清除" />
					<kCheckbox v-if="typeof options.multiple !== 'undefined'" v-model:value="options.multiple" label="多选" />
					<kCheckbox v-if="typeof options.range !== 'undefined'" v-model:value="options.range" label="范围选择" />
					<kCheckbox
						v-if="typeof options.showTime !== 'undefined'"
						v-model:value="options.showTime"
						label="时间选择器"
					/>
					<kCheckbox
						v-if="typeof options.allowHalf !== 'undefined'"
						v-model:value="options.allowHalf"
						label="允许半选"
					/>
					<kCheckbox
						v-if="typeof options.showInput !== 'undefined'"
						v-model:value="options.showInput"
						label="显示输入框"
					/>
					<kCheckbox
						v-if="typeof options.showLabel !== 'undefined'"
						v-model:value="options.showLabel"
						label="显示Label"
					/>
					<kCheckbox
						v-if="typeof options.chinesization !== 'undefined'"
						v-model:value="options.chinesization"
						label="汉化"
					/>
					<kCheckbox
						v-if="typeof options.hideSequence !== 'undefined'"
						v-model:value="options.hideSequence"
						label="隐藏序号"
					/>
					<kCheckbox v-if="typeof options.drag !== 'undefined'" v-model:value="options.drag" label="允许拖拽" />
					<kCheckbox
						v-if="typeof options.showSearch !== 'undefined'"
						v-model:value="options.showSearch"
						label="可搜索"
					/>
					<kCheckbox
						v-if="typeof options.treeCheckable !== 'undefined'"
						v-model:value="options.treeCheckable"
						label="可勾选"
					/>
					<kCheckbox v-if="typeof options.animated !== 'undefined'" v-model:value="options.animated" label="动画切换" />
				</a-form-item>

				<a-form-item v-if="typeof selectItem.rules !== 'undefined' && selectItem.rules.length > 0" label="校验">
					<kCheckbox v-model:value="selectItem.rules[0].required" label="必填" />
					<a-input v-model:value="selectItem.rules[0].message" placeholder="必填校验提示信息" class="fd-mt-2" />
					<KChangeOption v-model:value="selectItem.rules" class="rule-options" type="rules" />
				</a-form-item>

				<!-- 表格选项 -->
				<a-form-item v-if="selectItem.type === 'table'" label="表格样式CSS">
					<a-input v-model:value="selectItem.options.customStyle" />
				</a-form-item>
				<a-form-item v-if="selectItem.type === 'table'" label="属性">
					<kCheckbox v-model:value="selectItem.options.bordered" label="显示边框" />
					<kCheckbox v-model:value="selectItem.options.bright" label="鼠标经过点亮" />
					<kCheckbox v-model:value="selectItem.options.small" label="紧凑型" />
				</a-form-item>

				<a-form-item v-if="selectItem.type === 'table'" label="提示">
					<p style="line-height: 26px">请点击右键增加行列，或者合并单元格</p>
				</a-form-item>

				<a-form-item v-if="typeof selectItem.help !== 'undefined'" label="帮助信息">
					<a-input v-model:value="selectItem.help" placeholder="请输入" />
				</a-form-item>

				<!-- 前缀 -->
				<a-form-item v-if="typeof options.addonBefore !== 'undefined'" label="前缀">
					<a-input v-model:value="options.addonBefore" placeholder="请输入" />
				</a-form-item>

				<!-- 后缀 -->
				<a-form-item v-if="typeof options.addonAfter !== 'undefined'" label="后缀">
					<a-input v-model:value="options.addonAfter" placeholder="请输入" />
				</a-form-item>
				<!-- 用户选择器类型 start -->
				<a-form-item v-if="selectItem.type === 'xnUserSelector'" label="是否单选">
					<a-radio-group v-model:value="options.radioModel" button-style="solid">
						<a-radio :value="true">是</a-radio>
						<a-radio :value="false">否</a-radio>
					</a-radio-group>
				</a-form-item>
				<!-- 用户选择器类型 end -->

				<!-- 事件配置 start -->
				<a-form-item v-if="selectItem.events">
					<template #label>
						<a-tooltip>
							<template #title>在此处编写相关js代码，当改组件触发该方法会执行已经编写的js代码！</template>
							事件配置（测试功能）
						</a-tooltip>
					</template>
					<div v-for="(data, event) in selectItem.events" :key="event">
						<p>
							<a-badge :status="data || data !== '' ? 'success' : 'default'" />
							<span style="width: 100px; display: inline-block; text-overflow: ellipsis">
								{{ event }}
							</span>
							<a-button
								size="small"
								type="primary"
								style="float: right"
								@click="openEventCodeForm(selectItem.key, event, data)"
							>
								<code-outlined />
								新增代码
							</a-button>
						</p>
					</div>
				</a-form-item>
				<!-- 事件配置 end -->
			</a-form>
		</div>
		<event-code-form-modal ref="eventCodeFormModal" @eventCodeFormBack="eventCodeFormBack" />
	</div>
</template>
<script>
	/*
	 * author kcz
	 * date 2019-11-20
	 * description 表单控件属性设置组件,因为配置数据是引用关系，所以可以直接修改
	 */
	import KChangeOption from '../../KChangeOption/index.vue'
	import kCheckbox from '../../KCheckbox/index.vue'
	import EventCodeFormModal from './eventCodeFormModal.vue'

	import { ColorPicker } from 'vue3-colorpicker'
	import 'vue3-colorpicker/style.css'
	import { CodeOutlined } from '@ant-design/icons-vue'

	export default {
		name: 'FormItemProperties',
		components: {
			KChangeOption,
			kCheckbox,
			ColorPicker,
			CodeOutlined,
			EventCodeFormModal
		},
		props: {
			selectItem: {
				type: Object,
				required: true
			},
			hideModel: {
				type: Boolean,
				default: false
			},
			databaseConfig: {
				type: Array,
				default: () => []
			},
			dictData: {
				type: Array,
				default: () => []
			},
			parentRecord: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				familyOptions: [
					// 字体选择设置
					{
						value: 'SimSun',

						label: '宋体'
					},
					{
						value: 'FangSong',
						label: '仿宋'
					},
					{
						value: 'SimHei',
						label: '黑体'
					},
					{
						value: 'PingFangSC-Regular',
						label: '苹方'
					},
					{
						value: 'KaiTi',
						label: '楷体'
					},
					{
						value: 'LiSu',
						label: '隶书'
					}
				],
				sizeOptions: [
					//字号选择设置
					{
						value: '26pt',
						label: '一号'
					},
					{
						value: '24pt',
						label: '小一'
					},
					{
						value: '22pt',
						label: '二号'
					},
					{
						value: '18pt',
						label: '小二'
					},
					{
						value: '16pt',
						label: '三号'
					},
					{
						value: '15pt',
						label: '小三'
					},
					{
						value: '14pt',
						label: '四号'
					},
					{
						value: '12pt',
						label: '小四'
					},
					{
						value: '10.5pt',
						label: '五号'
					},
					{
						value: '9pt',
						label: '小五'
					}
				],
				selectTable: '',
				selectColumn: '',
				selectTableType: '',
				mapping: {}
			}
		},
		computed: {
			options() {
				return this.selectItem.options || {}
			},
			hasDatabase() {
				return this.databaseConfig.length > 0
			},

			// 是否显示选择主表字段，这个地方情况错综复杂，搞的老俞我难受
			canSelectPrimaryTableZhuBiao() {
				return (
					this.hasDatabase &&
					!(this.selectItem.type === 'batch' || (this.parentRecord && this.parentRecord.type === 'batch'))
				)
			},
			canSelectPrimaryTable() {
				return (
					this.hasDatabase &&
					this.selectItem.type !== 'batch' &&
					(!this.parentRecord || (this.parentRecord && this.parentRecord.key === this.selectItem.key))
				)
			},
			canSelectChildTable() {
				return this.hasDatabase && this.selectItem.type === 'batch'
			},
			// 数据字段的输入禁用
			canSelectChildDisabled() {
				return this.hasDatabase
			},
			// 非动态表格的组件
			databaseTables() {
				// eslint-disable-next-line vue/no-side-effects-in-computed-properties
				this.mapping = this.databaseConfig.reduce((acc, cur) => {
					acc[cur.tableName] = cur.tableColumn
					return acc
				}, {})
				const tableComloms = this.databaseConfig
					.filter((v) => {
						if (v.tableType === 'parent') {
							return v
						}
					})
					.map((v) => ({
						label: v.tableRemark,
						value: v.tableName,
						tableType: v.tableType
					}))
				// 初始化这个组件的时候，我们给加上默认的数据，并锁定减少用户点击的麻烦次数
				this.selectItem.selectTable = tableComloms ? tableComloms[0].value : ''
				// 同样也加上表的类型跟名字
				this.selectItem.selectTableType = tableComloms ? tableComloms[0].tableType : ''
				return tableComloms
			},
			// 动态表格只能是子表
			childTables() {
				// eslint-disable-next-line vue/no-side-effects-in-computed-properties
				this.mapping = this.databaseConfig.reduce((acc, cur) => {
					acc[cur.tableName] = cur.tableColumn
					return acc
				}, {})
				return this.databaseConfig
					.filter((v) => {
						if (v.tableType === 'child') {
							return v
						}
					})
					.map((v) => ({
						label: v.tableRemark,
						value: v.tableName,
						tableType: v.tableType
					}))
			},
			tableColumns() {
				const selectTable = this.selectItem.selectTable || this.parentRecord?.selectTable
				return (this.mapping[selectTable] || []).map((v) => ({
					label: v.columnRemark,
					value: v.columnName
				}))
			},
			dictDataOptions() {
				return this.dictData.map((v) => ({
					label: v.name,
					value: v.dictValue
				}))
			},
			getOptionFormat: {
				get() {
					if (this.options.showTime) {
						this.options.format = 'YYYY-MM-DD HH:mm:ss'
						return this.options.format
					} else {
						if (this.options.format === 'YYYY-MM-DD HH:mm:ss') {
							this.options.format = 'YYYY-MM-DD'
						}
						return this.options.format
					}
				},
				set(value) {
					this.options.format = value
				}
			}
		},
		methods: {
			changeColumn(val, option) {
				this.selectItem.label = option.label
				this.selectItem.model = option.value
				this.selectItem.key = option.value
			},
			changeChildTable(val, option) {
				this.selectItem.label = option.label
				this.selectItem.model = option.value
				this.selectItem.key = option.value
			},
			changeDictData(val) {
				const dictItem = this.dictData.find((v) => v.dictValue === val)
				if (dictItem) {
					this.options.options = dictItem.children.map((v) => ({
						label: v.name,
						value: v.dictValue
					}))
				}
			},
			// 打开事件编辑器
			openEventCodeForm(key, name, value) {
				if (value) {
					this.$refs.eventCodeFormModal.editorJson = JSON.parse(value)
				}
				this.$refs.eventCodeFormModal.componentsKey = key
				this.$refs.eventCodeFormModal.functionName = name
				this.$refs.eventCodeFormModal.visible = true
			},
			// 事件编辑器回调
			eventCodeFormBack(key, name, value) {
				// 该表这个对象的参数值
				// eslint-disable-next-line vue/no-mutating-props
				this.selectItem.events[name] = value
			}
		}
	}
</script>

<style>
	.options-toggle {
		margin-bottom: 10px;
	}
	.mt-2 {
		margin-top: 10px !important;
	}
</style>
