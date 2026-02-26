<template>
	<a-spin :spinning="loadSpinning">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical" :label-col="labelCol">
			<a-row :gutter="16">
				<a-col :span="4">
					<a-form-item label="系统LOGO：" name="SNOWY_SYS_LOGO">
						<a-upload
							v-model:file-list="formData.SNOWY_SYS_LOGO"
							class="avatar-uploader"
							list-type="picture-card"
							:show-upload-list="false"
							:custom-request="customRequest"
							accept="image/png, image/jpeg, image/jpg"
						>
							<img v-if="imageUrl" :src="imageUrl" alt="avatar" style="max-height: 100px; max-width: 100px" />
							<div v-else>
								<plus-outlined />
								<div class="ant-upload-text">上传</div>
							</div>
						</a-upload>
						<label style="font-size: 12px">可上传不超过100kb大小的图片.</label>
					</a-form-item>
				</a-col>
				<a-col :span="4">
					<a-form-item name="FAST_LOADING_IMAGE">
						<template #label>
							<a-tooltip>
								<template #title> 修改系统加载中LOGO后，清除缓存重新登录后生效 </template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp 系统加载中LOGO：
						</template>
						<a-upload
							v-model:file-list="formData.FAST_LOADING_IMAGE"
							class="avatar-uploader"
							list-type="picture-card"
							:show-upload-list="false"
							:custom-request="customRequestLoadingLogo"
							accept="image/png, image/jpeg, image/jpg"
						>
							<img
								v-if="imageUrlLoadingUrl"
								:src="imageUrlLoadingUrl"
								alt="avatar"
								style="max-height: 100px; max-width: 100px"
							/>
							<div v-else>
								<plus-outlined />
								<div class="ant-upload-text">上传</div>
							</div>
						</a-upload>
						<label style="font-size: 12px">可上传不超过100kb大小的图片.</label>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="系统ICO：" name="FAST_TBA_ICON">
						<a-upload
							v-model:file-list="formData.FAST_TBA_ICON"
							class="avatar-uploader"
							list-type="picture-card"
							:show-upload-list="false"
							:custom-request="customRequestTabIcon"
							accept="image/*"
						>
							<img
								v-if="imageUrlTabIcon"
								:src="imageUrlTabIcon"
								alt="avatar"
								style="max-height: 100px; max-width: 100px"
							/>
							<div v-else>
								<plus-outlined />
								<div class="ant-upload-text">上传</div>
							</div>
						</a-upload>
						<label style="font-size: 12px; display: block">可上传不超过100kb大小的图片;</label>
						<label style="color: #d34527">推荐.ico格式，也支持.png、.jpeg、.jpg、.gif格式.</label>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="系统名称：" name="SNOWY_SYS_NAME">
						<a-input v-model:value="formData.SNOWY_SYS_NAME" placeholder="请输入系统名称" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="系统版本：" name="SNOWY_SYS_VERSION">
						<a-input v-model:value="formData.SNOWY_SYS_VERSION" placeholder="请输入系统版本" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="版权信息：" name="SNOWY_SYS_COPYRIGHT">
						<a-input v-model:value="formData.SNOWY_SYS_COPYRIGHT" placeholder="请输入版权信息" />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="版权链接URL：" name="SNOWY_SYS_COPYRIGHT_URL">
						<a-input v-model:value="formData.SNOWY_SYS_COPYRIGHT_URL" placeholder="请输入版权链接URL" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="验证码开关：" name="SNOWY_SYS_DEFAULT_CAPTCHA_OPEN">
						<a-radio-group
							v-model:value="formData.SNOWY_SYS_DEFAULT_CAPTCHA_OPEN"
							:options="commonSwitchOptions"
							placeholder="请选择验证码开关"
						></a-radio-group>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="默认文件引擎：" name="SNOWY_SYS_DEFAULT_FILE_ENGINE">
						<a-radio-group
							v-model:value="formData.SNOWY_SYS_DEFAULT_FILE_ENGINE"
							:options="fileEngineOptions"
							placeholder="请选择系统默认文件引擎"
						></a-radio-group>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="默认跳转路由：" name="FAST_DEFAULT_SHOW_ROUTE">
						<a-input
							addon-before="/"
							v-model:value="formData.FAST_DEFAULT_SHOW_ROUTE"
							placeholder="请输入默认展示页面路由地址"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="限制登录终端开关：" name="FAST_TERMINAL_LIMIT_OPEN">
						<!--						<a-switch v-model:checked="formData.FAST_TERMINAL_LIMIT_OPEN" checked-children="开" un-checked-children="关" />-->
						<a-radio-group
							v-model:value="formData.FAST_TERMINAL_LIMIT_OPEN"
							:options="commonTerminalOptions"
							placeholder="请选择限制登录终端开关"
						></a-radio-group>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="默认快捷方式：" name="SNOWY_SYS_DEFAULT_WORKBENCH_DATA">
						<menuTreeSelect ref="menuTreeSelectRef" :resultData="true" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="系统默认密码：" name="SNOWY_SYS_DEFAULT_PASSWORD">
						<template #extra>密码复杂度要求至少8位，由数字、大小写字母、特殊字符组成</template>
						<a-input
							v-model:value="formData.SNOWY_SYS_DEFAULT_PASSWORD"
							placeholder="请输入系统默认密码"
							v-if="!isDefaultPassword"
						>
						</a-input>
						<a-input v-model:value="defaultPassword" placeholder="请输入系统默认密码" v-else>
							<template #addonAfter>
								<eye-outlined style="cursor: pointer" @click="isDefaultPassword = !isDefaultPassword" />
							</template>
						</a-input>
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="系统描述：" name="SNOWY_SYS_DEFAULT_DESCRRIPTION">
						<a-textarea
							v-model:value="formData.SNOWY_SYS_DEFAULT_DESCRRIPTION"
							placeholder="请输入系统描述"
							:auto-size="{ minRows: 3, maxRows: 6 }"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item>
						<a-button type="primary" :loading="submitLoading" @click="onSubmit()">保存</a-button>
						<a-button style="margin-left: 10px" @click="resetForm">重置</a-button>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
	</a-spin>
</template>

<script setup name="sysConfig">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import configApi from '@/api/dev/configApi'
	import tool from '@/utils/tool'
	import menuTreeSelect from '@/components/TreeSelect/menuTreeSelect.vue'
	import { calSize } from '@/utils/file'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	const submitLoading = ref(false)
	const imageUrl = ref('')
	const imageUrlLoadingUrl = ref('')
	const imageUrlTabIcon = ref('')
	const menuTreeSelectRef = ref()
	const loadSpinning = ref(true)
	const isDefaultPassword = ref(false)
	const defaultPassword = ref('')
	// 查询此界面的配置项,并转为表单
	const param = {
		category: 'SYS_BASE'
	}
	configApi.configList(param).then((data) => {
		loadSpinning.value = false
		if (data) {
			data.forEach((item) => {
				// 如果是系统的各种logo，它这个组件要一个数组，单独处理
				if (item.configKey === 'SNOWY_SYS_LOGO') {
					formData.value[item.configKey] = item.configValue ? [item.configValue] : []
					// 让其回显
					imageUrl.value = item.configValue
				} else if (item.configKey === 'FAST_LOADING_IMAGE') {
					formData.value[item.configKey] = item.configValue ? [item.configValue] : []
					// 让其回显
					imageUrlLoadingUrl.value = item.configValue
				} else if (item.configKey === 'FAST_TBA_ICON') {
					formData.value[item.configKey] = item.configValue ? [item.configValue] : []
					// 让其回显
					imageUrlTabIcon.value = item.configValue
				} else if (item.configKey === 'SNOWY_SYS_DEFAULT_WORKBENCH_DATA') {
					try {
						menuTreeSelectRef.value.setSelectData(JSON.parse(item.configValue).shortcut)
						// eslint-disable-next-line no-empty
					} catch (e) {}
				} else if (item.configKey === 'FAST_TERMINAL_LIMIT_OPEN') {
					formData.value[item.configKey] = item.configValue == 'true' ? true : false
				} else {
					formData.value[item.configKey] = item.configValue
				}
			})
			if (!formData.value.FAST_TERMINAL_LIMIT_OPEN) {
				formData.value.FAST_TERMINAL_LIMIT_OPEN = false
			}
		} else {
			message.warning('表单项不存在，请初始化数据库')
		}
	})
	// 文件引擎
	const fileEngineOptions = tool.dictList('FILE_ENGINE')
	// 开关
	const commonSwitchOptions = tool.dictList('COMMON_SWITCH')
	const commonTerminalOptions = [
		{
			label: '开',
			value: true
		},
		{
			label: '关',
			value: false
		}
	]

	// 密码复杂度验证
	const validatePassword = async (_rule, value) => {
		if (value.length < 8) {
			return Promise.reject('密码长度至少8位')
		}
		if (!/[0-9]/.test(value)) {
			return Promise.reject('密码必须包含数字')
		}
		if (!/[a-z]/.test(value)) {
			return Promise.reject('密码必须包含小写字母')
		}
		if (!/[A-Z]/.test(value)) {
			return Promise.reject('密码必须包含大写字母')
		}
		if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(value)) {
			return Promise.reject('密码必须包含特殊字符')
		}
		return Promise.resolve()
	}

	const customRequest = (data) => {
		formData.value.SNOWY_SYS_LOGO = ref([])
		getBase64(data.file)
			.then((res) => {
				const number = calSize(res)
				if (number > 100) {
					message.warning('请上传不超过100KB的图片')
				} else {
					imageUrl.value = res
					formData.value.SNOWY_SYS_LOGO.push(res)
				}
			})
			.catch((err) => {})
	}

	const customRequestLoadingLogo = (data) => {
		formData.value.FAST_LOADING_IMAGE = ref([])
		getBase64(data.file)
			.then((res) => {
				const number = calSize(res)
				if (number > 100) {
					message.warning('请上传不超过100KB的图片')
				} else {
					imageUrlLoadingUrl.value = res
					formData.value.FAST_LOADING_IMAGE.push(res)
				}
			})
			.catch((err) => {})
	}

	const customRequestTabIcon = (data) => {
		// if ((data.file.name).split('.')[(data.file.name).split('.').length -1] !== 'ico') {
		// 	message.error('请上传.ico格式的图片')
		// 	return
		// }
		formData.value.FAST_TBA_ICON = ref([])
		getBase64(data.file)
			.then((res) => {
				const number = calSize(res)
				if (number > 100) {
					message.warning('请上传不超过100KB的图片')
				} else {
					imageUrlTabIcon.value = res
					formData.value.FAST_TBA_ICON.push(res)
				}
			})
			.catch(() => {})
	}

	// 文件转base64，用于显示图片
	const getBase64 = (file) => {
		return new Promise((resolve, reject) => {
			// FileReader类就是专门用来读文件的
			const reader = new FileReader()
			reader.readAsDataURL(file)
			// 成功和失败返回对应的信息，reader.result一个base64，可以直接使用
			reader.onload = () => resolve(reader.result)
			// 失败返回失败的信息
			reader.onerror = (error) => reject(error)
		})
	}

	// 默认要校验的
	const formRules = {
		SNOWY_SYS_LOGO: [required('请上传系统LOGO')],
		SNOWY_SYS_NAME: [required('请输入系统名称')],
		SNOWY_SYS_VERSION: [required('请输入系统版本')],
		SNOWY_SYS_COPYRIGHT: [required('请输入版权信息')],
		SNOWY_SYS_COPYRIGHT_URL: [required('请输入版权链接URL')],
		SNOWY_SYS_DEFAULT_FILE_ENGINE: [required('请选择系统默认文件引擎')],
		SNOWY_SYS_DEFAULT_CAPTCHA_OPEN: [required('请选择系统验证码开关')],
		SNOWY_SYS_DEFAULT_PASSWORD: [
			{ required: true, message: '密码不能为空' },
			{ validator: validatePassword, trigger: 'change' }
		]
	}
	// 表单固定label实现
	const labelCol = ref({
		style: {
			width: '150px'
		}
	})

	// 查看密码
	const selectDefault = async () => {
		const res = await configApi.showDefaultPassword()
		if (res) {
			if (isDefaultPassword) {
				defaultPassword.value = res
				isDefaultPassword.value = true
			}
		}
	}
	// 验证并提交数据
	const onSubmit = () => {
		if (formData.value.FAST_TERMINAL_LIMIT_OPEN) {
			configApi.initTerminalData()
		}
		formRef.value
			.validate()
			.then(() => {
				submitLoading.value = true
				let submitParam = cloneDeep(formData.value)
				if (submitParam.SNOWY_SYS_LOGO && submitParam.SNOWY_SYS_LOGO.length > 0) {
					submitParam.SNOWY_SYS_LOGO = submitParam.SNOWY_SYS_LOGO[0]
				} else {
					submitParam.SNOWY_SYS_LOGO = null
				}
				if (submitParam.FAST_LOADING_IMAGE && submitParam.FAST_LOADING_IMAGE.length > 0) {
					submitParam.FAST_LOADING_IMAGE = submitParam.FAST_LOADING_IMAGE[0]
				} else {
					submitParam.FAST_LOADING_IMAGE = null
				}
				if (submitParam.FAST_TBA_ICON && submitParam.FAST_TBA_ICON.length > 0) {
					submitParam.FAST_TBA_ICON = submitParam.FAST_TBA_ICON[0]
				} else {
					submitParam.FAST_TBA_ICON = null
				}
				const param = Object.entries(submitParam).map((item) => {
					return {
						configKey: item[0],
						configValue: item[1]
					}
				})
				// 创建快捷方式
				const shortcut = {
					shortcut: menuTreeSelectRef.value.getSelectData()
				}
				param.push({
					configKey: 'SNOWY_SYS_DEFAULT_WORKBENCH_DATA',
					configValue: JSON.stringify(shortcut)
				})
				configApi
					.configEditForm(param)
					.then(() => {})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 重置表单，且设置默认值
	const resetForm = () => {
		imageUrl.value = ''
		formData.value = {
			SNOWY_SYS_DEFAULT_CAPTCHA_OPEN: 'true',
			SNOWY_SYS_DEFAULT_FILE_ENGINE: 'LOCAL'
		}
	}
</script>
