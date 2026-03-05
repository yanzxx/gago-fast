<template>
	<view>
		<view class="sticky">
			<uni-search-bar placeholder="请输入文件名称关键词" v-model="searchFormState.searchKey"
				@confirm="loadData(true)"></uni-search-bar>
		</view>
		<view class="file-list">
			<uni-list>
				<uni-list-item v-for="(item, index) in fileData" :key="index" :showArrow="false" :clickable="true"
					@tap="moreTapItem(item, index)">
					<!-- 自定义 body -->
					<template v-slot:body>
						<view class="item">
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">文件名称</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.name }}</view>
									</uni-col>
								</uni-row>
							</view>
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">缩略图</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">
											<image v-if="
												item.suffix === 'png' ||
												item.suffix === 'jpg' ||
												item.suffix === 'jpng' ||
												item.suffix === 'ico' ||
												item.suffix === 'gif'" class="item-row-content-image" :src="item.thumbnail"></image>
											<image v-else-if="item.suffix === 'doc' || item.suffix === 'docx'"
												class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/docx.png'">
											</image>
											<image v-else-if="item.suffix === 'xls' || item.suffix === 'xlsx'"
												class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/xlsx.png'">
											</image>
											<image v-else-if="item.suffix === 'zip'" class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/zip.png'">
											</image>
											<image v-else-if="item.suffix === 'rar'" class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/rar.png'">
											</image>
											<image v-else-if="item.suffix === 'ppt' || item.suffix === 'pptx'"
												class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/ppt.png'">
											</image>
											<image v-else-if="item.suffix === 'txt'" class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/txt.png'">
											</image>
											<image v-else-if="item.suffix === 'html'" class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/html.png'">
											</image>
											<image v-else class="item-row-content-image"
												:src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/images/fileImg/file.png'">
											</image>
										</view>
									</uni-col>
								</uni-row>
							</view>
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">文件大小</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.sizeInfo }}</view>
									</uni-col>
								</uni-row>
							</view>
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">文件后缀</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.suffix }}</view>
									</uni-col>
								</uni-row>
							</view>
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">储存引擎</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.engine }}</view>
									</uni-col>
								</uni-row>
							</view>


							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">储存桶</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.bucket }}</view>
									</uni-col>
								</uni-row>
							</view>
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">上传时间</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.createTime }}</view>
									</uni-col>
								</uni-row>
							</view>
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">存储路径</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.storagePath }}</view>
									</uni-col>
								</uni-row>
							</view>
							<view class="item-row">
								<uni-row>
									<uni-col :span="4">
										<view class="item-row-title">下载链接</view>
									</uni-col>
									<uni-col :span="20">
										<view class="item-row-content">{{ item.downloadPath }}</view>
									</uni-col>
								</uni-row>
							</view>
						</view>
					</template>
				</uni-list-item>
			</uni-list>
		</view>

		<!-- 新增悬浮按钮 -->
		<uni-fab :pattern="{
				color: '#7A7E83',
				backgroundColor: '#fff',
				selectedColor: '#007AFF',
				buttonColor: '#007AFF',
				iconColor: '#fff'
			}" horizontal="right" vertical="bottom" direction="horizontal" @fabClick="add"></uni-fab>
		<!-- 更多操作 -->
		<morePopup ref="morePopupRef" @handleOk="loadData(true)"></morePopup>
		<!-- 更多操作 -->
		<uploadPopup ref="uploadPopupRef" @handleOk="loadData(true)"></uploadPopup>
	</view>
</template>

<script setup>
	import uploadPopup from './upload-popup.vue'
	import morePopup from './more-popup.vue'
	import {
		reactive,
		ref,
		getCurrentInstance
	} from "vue"
	import {
		filePage
	} from '@/api/dev/fileApi'
	import {
		onLoad,
		onShow,
		onReady,
		onPullDownRefresh,
		onReachBottom
	} from "@dcloudio/uni-app"
	import XEUtils from 'xe-utils'
	
	const searchFormState = reactive({})
	const parameter = reactive({
		current: 1,
		size: 10
	})
	const fileData = ref([])
	// 加载数据
	const loadData = (isReset) => {
		if (isReset) {
			parameter.current = 1
			fileData.value = []
		}
		Object.assign(parameter, searchFormState)
		filePage(parameter).then(res => {
			if (XEUtils.isEmpty(res?.data?.records)){
				return
			}
			fileData.value = fileData.value.concat(res.data.records)
			parameter.current++
		}).finally(()=>{
			uni.stopPullDownRefresh()
		})
	}
	// 展示
	onShow(() => {
		loadData(true)
	})
	// 下拉刷新
	onPullDownRefresh(() => {
		loadData(true)
	})
	// 上拉加载
	onReachBottom(() => {
		loadData()
	})
	// 更多操作
	const morePopupRef = ref()
	const moreTapItem = (item, index) => {
		morePopupRef.value.open(item)
	}
	const uploadPopupRef = ref()
	const add = () => {
		uploadPopupRef.value.open()
	}
</script>

<style lang="scss">
	.file-list {
		margin: 15upx;
		border-radius: 5upx;

		.item {
			width: 100vw;

			.item-row {
				margin: 20upx 10upx;

				.item-row-title {
					font-size: 25upx;
					color: #999;
				}

				.item-row-content {
					font-size: 25upx;
					text-align: right;
					word-break: break-all;

					.item-row-content-image {
						width: 100upx;
						height: 100upx;
						// border-style: double;
						border-radius: 5upx;
						box-shadow: 1upx 4upx 5upx rgba(72, 22, 174, 0.3);
					}
				}
			}

		}

	}
</style>