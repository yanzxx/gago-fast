<template>
	<cus-file-picker return-type="returnType" v-model="imgVal" @select="select" @delete="del" :auto-upload="true"
		v-bind="$attrs">
	</cus-file-picker>
</template>

<script setup>
	// uploadImage、uploadFile有调整
	import cusFilePicker from "./cus-file-picker.vue";
	import {
		ref,
		reactive,
		watch,
	} from "vue";
	import {
		fileUploadDynamicReturnId,
		fileUploadReturnId,
		fileUploadAliyunReturnId,
		fileUploadTencentReturnId,
		fileDelete,
		fileDetail,
		fileDownload
	} from '@/api/dev/fileApi.js'
	import {
		getToken
	} from '@/utils/auth'
	import config from "@/config.js"
	import XEUtils from 'xe-utils'
	import store from '@/store'
	const emits = defineEmits(['update:modelValue'])

	const props = defineProps({
		modelValue: [String, Array],
		returnType: {
			type: String,
			default: 'array' // array || object
		},
		uploadType: {
			type: String,
			default: 'Dynamic' // Dynamic || Local || Aliyun || Tencent
		},
	})
	const imgVal = ref()
	const imgId = ref()

	if (props.returnType == 'array') {
		imgVal.value = []
		imgId.value = []
		if (props.modelValue) {
			const promiseList = []
			const tempImgVal = []
			props.modelValue.forEach(item => {
				promiseList.push(new Promise((resolve, reject) => {
					fileDetail({
						id: item
					}).then(res => {
						// #ifdef APP-PLUS
						fileDownload(res.data.id).then(response => {
							tempImgVal.push({
								"uuid": res.data.id,
								"name": res.data.name,
								"extname": res.data.suffix,
								"url": response.tempFilePath,
							})
							imgId.value.push(res.data.id)
							resolve()
						})
						// #endif
						// #ifndef APP-PLUS
						tempImgVal.push({
							"uuid": res.data.id,
							"name": res.data.name,
							"extname": res.data.suffix,
							"url": res.data.downloadPath,
						})
						imgId.value.push(res.data.id)
						resolve()
						// #endif
					}).catch((e) => {
						reject(e)
					})
				}))
			})
			Promise.all(promiseList).then(() => {
				imgVal.value = tempImgVal
			}).catch(err => {
				console.log(err)
			})
		}
	}
	if (props.returnType == 'object') {
		imgVal.value = {}
		imgId.value = ''
		if (props.modelValue) {
			fileDetail({
				id: props.modelValue
			}).then(res => {
				imgVal.value = {
					"uuid": res.data.id,
					"name": res.data.name,
					"extname": res.data.suffix,
					"url": res.data.downloadPath,
				}
				imgId.value = res.data.id
			})
		}
	}

	// 处理数据
	const handleImgData = (tempImgVal, imgId, res, tempFile) => {
		if (props.returnType == 'array') {
			tempImgVal.push({
				"uuid": res.data,
				"name": tempFile.name,
				"extname": tempFile.extname,
				"url": `${store.getters.allEnv[store.getters.envKey].baseUrl}/dev/file/download?id=${res.data}`,
			})
			imgId.value.push(res.data)
		}
		if (props.returnType == 'object') {
			tempImgVal = {
				"uuid": res.data,
				"name": tempFile.name,
				"extname": tempFile.extname,
				"url": `${store.getters.allEnv[store.getters.envKey].baseUrl}/dev/file/download?id=${res.data}`,
			}
			imgId.value = res.data
		}
	}
	// 获取上传状态
	const select = (e) => {
		const tempFiles = e.tempFiles;
		const promiseList = []
		const tempImgVal = XEUtils.clone(imgVal.value, true)
		tempFiles.forEach(tempFile => {
			promiseList.push(new Promise((resolve, reject) => {
				if('Local' == props.uploadType){
					fileUploadReturnId({
						filePath: tempFile.path
					}).then(res => {
						handleImgData(tempImgVal, imgId, res, tempFile)
						resolve()
					})
				}else if('Aliyun' == props.uploadType){
					fileUploadAliyunReturnId({
						filePath: tempFile.path
					}).then(res => {
						handleImgData(tempImgVal, imgId, res, tempFile)
						resolve()
					})
				}else if('Tencent' == props.uploadType){
					fileUploadTencentReturnId({
						filePath: tempFile.path
					}).then(res => {
						handleImgData(tempImgVal, imgId, res, tempFile)
						resolve()
					})
				}else{
					fileUploadDynamicReturnId({
						filePath: tempFile.path
					}).then(res => {
						handleImgData(tempImgVal, imgId, res, tempFile)
						resolve()
					})
				}
			}))
		})
		Promise.all(promiseList).then(() => {
			imgVal.value = tempImgVal
			// 更新数据
			emits('update:modelValue', imgId.value)
		}).catch(err => {
			console.log(err)
		})
	}
	// 删除文件
	const del = (e) => {
		if (props.returnType == 'array') {
			fileDelete([{
				id: e.tempFile.uuid
			}]).then(res => {
				imgId.value.splice(XEUtils.indexOf(imgId.value, e.tempFile.uuid), 1)
				imgVal.value.splice(XEUtils.findIndexOf(imgVal.value, item => item.uuid == e.tempFile.uuid), 1)
				// 更新数据
				emits('update:modelValue', imgId.value)
			})
		}
		if (props.returnType == 'object') {
			fileDelete([{
				id: e.tempFile.uuid
			}]).then(res => {
				imgVal.value = {}
				imgId.value = ''
				// 更新数据
				emits('update:modelValue', imgId.value)
			})
		}

	}
</script>

<style lang="scss">

</style>