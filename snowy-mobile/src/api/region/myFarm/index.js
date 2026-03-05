import request from '@/utils/request'

export default {
	// 获取业务中台token
	getCenterToken(data){
		return request({
			url: '/growth/getToken',
			method: 'post',
			data
		})
	},
	// 获取地块json
	getLandList(data){
		return request({
			url: '/parcel/queryList',
			method: 'post',
			data
		})
	},
	// 获取农事记录列表
	getFarmRecordPage(data) {
		return request({
			url: '/farmRecord/page',
			method: 'post',
			data
		})
	},
	// 删除文件
	deleteFileList(data) {
		return request({
			url: '/dev/file/delete',
			method: 'post',
			data
		})
	},
	// 新增农事记录
	addFarmRecord(data) {
		return request({
			url: '/farmRecord/add',
			method: 'post',
			data
		})
	},
	// 新增地块
	addLandInfo(data) {
		return request({
			url: '/parcel/add',
			method: 'post',
			data
		})
	},
	// 编辑地块
	editLandInfo(data) {
		return request({
			url: '/parcel/update',
			method: 'post',
			data
		})
	},
	// 获取设备详情
	getFacilityDetails(data) {
		return request({
			url: '/device/queryCollectData',
			method: 'post',
			data
		})
	},
	// 获取虫情设备详情
	getDetectorDetails(data) {
		return request({
			url: '/device/queryCqDetail',
			method: 'post',
			data
		})
	},
	// 获取摄像头设备详情
	getCameraDetails(data) {
		return request({
			url: '/device/queryCameraData',
			method: 'post',
			data
		})
	},
	// 获取本年长势趋势
	getGrowthTrend(data) {
		return request({
			url: '/growth/growthTrend',
			method: 'get',
			data
		})
	},
	// 获取长势面积
	getGrowthArea(data) {
		return request({
			url: '/growth/growthArea',
			method: 'get',
			data
		})
	},
	// 作业总量统计
	getJobStatistics(params) {
		console.log('params ==>', params)
		return request({
			url: '/jobAnalysis/dateArea?start=' + params.start + '&end=' + params.end,
			method: 'get',
		})
	},
	// 获取作业轨迹数据
	urpPage(data) {
		return request({
			url: '/job/page',
			method: 'post',
			data
		})
	},
	// 获取轨迹详情
	urpDetail(data) {
		return request({
			url: '/job/queryDetail',
			method: 'post',
			data
		})
	},
	// 获取地块列表
	getLandListPage(data) {
		return request({
			url: '/parcel/page',
			method: 'post',
			data
		})
	},
	// 获取作业数据
	getWorkEveryData(data) {
		return request({
			url: '/jobLand/banners',
			method: 'post',
			data
		})
	},
	// 获取物联设备采集数据
	getAnalysis(data) {
		return request({
			url: '/deviceData/getAnalysis',
			method: 'post',
			data
		})
	},
	getWxToken(data) {
		return request({
			url: '/wx/mp/getSignature',
			method: 'get',
			data
		})
	},
}
