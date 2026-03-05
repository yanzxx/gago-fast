<template>
	<!-- :mask-click="false" -->
	<uni-popup ref="popupRef" type="bottom" safeArea>
		<view class="container">
			<uni-list :border="false">
				<uni-list-item class="item" title="下载" :clickable="true" @click="download">
				</uni-list-item>
				<uni-list-item class="item" title="删除" :clickable="true" @click="del">
				</uni-list-item>
				<uni-list-item class="item" title="取消" :clickable="true" @click="cancel">
				</uni-list-item>
			</uni-list>
		</view>
	</uni-popup>
</template>

<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance
	} from "vue";
	import {
		fileDelete,
		fileDownload
	} from '@/api/dev/fileApi.js'
	import modal from '@/plugins/modal.js'
	
	const emits = defineEmits(['handleOk'])

	// 删除弹出ref
	const delPopRef = ref()
	// 弹出ref
	const popupRef = ref()
	// 打开
	const record = ref({})
	const open = (item) => {
		record.value = item
		popupRef.value.open("bottom")
	}
	// 下载
	const download = () => {
		// #ifdef H5
		self.location.href = record.value.downloadPath
		// #endif
		// #ifndef H5
		fileDownload(record.value.id).then(response => {
			uni.openDocument({
				filePath: response.tempFilePath,
				success: () => {
					
				}
			});
		})
		// #endif
	}
	// 删除
	const del = () => {
		modal.confirm(`确定要删除【${ record.value.name }】文件吗？`).then(() => {
			fileDelete([{
				id: record.value.id
			}]).then(res => {
				emits('handleOk')
				popupRef.value.close()
			})
		})
	}
	// 取消
	const cancel = () => {
		popupRef.value.close()
	}
	defineExpose({
		open
	})
</script>
<style lang="scss">
	.container {
		margin: 15upx;
		border-radius: 5upx;
		padding: 5upx;
		background-color: $uni-white;

		.item {
			text-align: center;
		}
	}
</style>