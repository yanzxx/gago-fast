<template>
	<a-modal
		title="预览"
		:visible="visible"
		@ok="handleGetData"
		@cancel="handleCancel"
		okText="获取数据"
		cancelText="关闭"
		style="top: 20px"
		:destroyOnClose="true"
		:centered="true"
		:dialogStyle="dialogStyle"
		:bodyStyle="bodyStyle"
		wrapClassName="k-form-modal"
		:width="`${previewWidth}px`"
	>
		<snowy-form-build :value="jsonData" @submit="handleSubmit" ref="SnowyFormBuild" />
		<jsonModel ref="jsonModel" />
	</a-modal>
</template>
<script>
	/*
	 * author kcz
	 * date 2019-11-20
	 * lastUpdate yubaoshan
	 * lastDate 2022年3月21日 00点24分
	 */
	import jsonModel from '../SnowyFormDesign/module/jsonModal.vue'
	import { dialogStyle, bodyStyle } from '@/config/modal.js'
	export default {
		name: 'KFormPreview',
		components: {
			jsonModel
		},
		data() {
			return {
				visible: false,
				previewWidth: 850,
				jsonData: {},
				dialogStyle,
				bodyStyle
			}
		},
		methods: {
			handleSubmit(p) {
				p()
					.then((res) => {
						this.$refs.jsonModel.jsonData = res
						this.$refs.jsonModel.visible = true
					})
					.catch((err) => {
						console.error(err, '获取数据失败')
					})
			},
			handleGetData() {
				this.$refs.SnowyFormBuild.getData()
					.then((res) => {
						this.$refs.jsonModel.jsonData = res
						this.$refs.jsonModel.visible = true
					})
					.catch((err) => {
						console.log(err, '获取数据失败')
					})
			},
			handleCancel() {
				this.visible = false
			}
		}
	}
</script>
