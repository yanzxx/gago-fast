import upload from '@/utils/upload'
import download from '@/utils/download'
import request from '@/utils/request'
// 动态上传文件返回id
export function fileUploadDynamicReturnId(param) {
	return upload({
		url: '/dev/file/uploadDynamicReturnId',
		file: param.file,
		filePath: param.filePath
	})
}
// 动态上传文件返回url
export function fileUploadDynamicReturnUrl(param) {
	return upload({
		url: '/dev/file/uploadDynamicReturnUrl',
		file: param.file,
		filePath: param.filePath
	})
}
// 本地文件上传，返回文件id
export function fileUploadReturnId(param) {
	return upload({
		url: '/dev/file/uploadLocalReturnId',
		file: param.file,
		filePath: param.filePath
	})
}
// 阿里云文件上传，返回文件id
export function fileUploadAliyunReturnId(param) {
	return upload({
		url: '/dev/file/uploadAliyunReturnId',
		file: param.file,
		filePath: param.filePath
	})
}
// 腾讯云文件上传，返回文件id
export function fileUploadTencentReturnId(param) {
	return upload({
		url: '/dev/file/uploadTencentReturnId',
		file: param.file,
		filePath: param.filePath
	})
}
// MINIO文件上传，返回文件id
export function fileUploadMinioReturnId(param) {
	return upload({
		url: '/dev/file/uploadMinioReturnId',
		file: param.file,
		filePath: param.filePath
	})
}
// 本地文件上传，返回文件Url
export function fileUploadLocalReturnUrl(param) {
	return upload({
		url: '/dev/file/uploadLocalReturnUrl',
		file: param.file,
		filePath: param.filePath
	})
}
// 阿里云文件上传，返回文件Url
export function fileUploadAliyunReturnUrl(param) {
	return upload({
		url: '/dev/file/uploadAliyunReturnUrl',
		file: param.file,
		filePath: param.filePath
	})
}
// 腾讯云文件上传，返回文件Url
export function fileUploadTencentReturnUrl(param) {
	return upload({
		url: '/dev/file/uploadTencentReturnUrl',
		file: param.file,
		filePath: param.filePath
	})
}
// MINIO文件上传，返回文件Url
export function fileUploadMinioReturnUrl(param) {
	return upload({
		url: '/dev/file/uploadMinioReturnUrl',
		file: param.file,
		filePath: param.filePath
	})
}
// 获取文件分页列表
export function filePage(data) {
	return request({
		url: '/dev/file/page',
		method: 'GET',
		data: data
	})
}
// 获取文件列表
export function fileList(data) {
	return request({
		url: '/dev/file/list',
		method: 'GET',
		data: data
	})
}
// 下载文件，这里要带上blob类型
export function fileDownload(id) {
	return download({
		url: '/dev/file/download?id=' + id,
	})
}
// 获取文件详情
export function fileDetail(data) {
	return request({
		url: '/dev/file/detail',
		method: 'GET',
		data: data
	})
}
// 删除文件
export function fileDelete(data) {
	return request({
		url: '/dev/file/delete',
		method: 'post',
		data: data
	})
}