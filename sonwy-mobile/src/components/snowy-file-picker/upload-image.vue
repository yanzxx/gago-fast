<template>
	<div class="uni-file-picker__container">
		<div class="file-picker__box" v-for="(item,index) in filesList" :key="index" :style="boxStyle">
			<div class="file-picker__box-content" :style="borderStyle">
				<!-- |||||||||||||||||||||||此处有调整-开始||||||||||||||||||||||||||||||||| -->
				<img v-if="
						item.extname === 'png' ||
						item.extname === 'jpg' ||
						item.extname === 'jpng' ||
						item.extname === 'ico' ||
						item.extname === 'gif'" class="file-img" :src="item.url" mode="aspectFill" @click.stop="prdivimg(item,index)" />
				<!-- ${$store.getters.allEnv[$store.getters.envKey].baseUrl} -->
				<img v-else-if="item.extname === 'doc'" class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/docx.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<img v-else-if="item.extname === 'xls'" class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/xlsx.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<img v-else-if="item.extname === 'zip'" class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/zip.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<img v-else-if="item.extname === 'rar'" class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/rar.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<img v-else-if="item.extname === 'ppt'" class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/ppt.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<img v-else-if="item.extname === 'txt'" class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/txt.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<img v-else-if="item.extname === 'html'" class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/html.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<img v-else class="file-img" :src="$store.getters.allEnv[$store.getters.envKey].baseUrl + '/imgs/fileImg/file.png'" mode="aspectFill" @click.stop="openFile(item,index)" />
				<!-- |||||||||||||||||||||||此处有调整-结束||||||||||||||||||||||||||||||||| -->
				<div v-if="delIcon && !readonly" class="icon-del-box" @click.stop="delFile(index)">
					<div class="icon-del"></div>
					<div class="icon-del rotate"></div>
				</div>
				<div v-if="(item.progress && item.progress !== 100) ||item.progress===0 " class="file-picker__progress">
					<progress class="file-picker__progress-item" :percent="item.progress === -1?0:item.progress" stroke-width="4"
					 :backgroundColor="item.errMsg?'#ff5a5f':'#EBEBEB'" />
				</div>
				<div v-if="item.errMsg" class="file-picker__mask" @click.stop="uploadFiles(item,index)">
					点击重试
				</div>
			</div>
		</div>
		<div v-if="filesList.length < limit && !readonly" class="file-picker__box" :style="boxStyle">
			<div class="file-picker__box-content is-add" :style="borderStyle" @click="choose">
				<slot>
					<div class="icon-add"></div>
					<div class="icon-add rotate"></div>
				</slot>
			</div>
		</div>
	</div>
</template>

<script>
	// |||||||||||||||||||||||此处有调整-开始|||||||||||||||||||||||||||||||||
	import {
		fileDownload
	} from '@/api/dev/fileApi'
	// |||||||||||||||||||||||此处有调整-结束|||||||||||||||||||||||||||||||||
	export default {
		name: "uploadimg",
		emits:['uploadFiles','choose','delFile'],
		props: {
			filesList: {
				type: Array,
				default () {
					return []
				}
			},
			disabled:{
				type: Boolean,
				default: false
			},
			disablePrediv: {
				type: Boolean,
				default: false
			},
			limit: {
				type: [Number, String],
				default: 9
			},
			imgStyles: {
				type: Object,
				default () {
					return {
						width: 'auto',
						height: 'auto',
						border: {}
					}
				}
			},
			delIcon: {
				type: Boolean,
				default: true
			},
			readonly:{
				type:Boolean,
				default:false
			}
		},
		computed: {
			styles() {
				let styles = {
					width: 'auto',
					height: 'auto',
					border: {}
				}
				return Object.assign(styles, this.imgStyles)
			},
			boxStyle() {
				const {
					width = 'auto',
						height = 'auto'
				} = this.styles
				let obj = {}
				if (height === 'auto') {
					if (width !== 'auto') {
						obj.height = this.value2px(width)
						obj['padding-top'] = 0
					} else {
						obj.height = 0
					}
				} else {
					obj.height = this.value2px(height)
					obj['padding-top'] = 0
				}

				if (width === 'auto') {
					if (height !== 'auto') {
						obj.width = this.value2px(height)
					} else {
						obj.width = '33.3%'
					}
				} else {
					obj.width = this.value2px(width)
				}

				let classles = ''
				for(let i in obj){
					classles+= `${i}:${obj[i]};`
				}
				return classles
			},
			borderStyle() {
				let {
					border
				} = this.styles
				let obj = {}
				const widthDefaultValue = 1
				const radiusDefaultValue = 3
				if (typeof border === 'boolean') {
					obj.border = border ? '1px #eee solid' : 'none'
				} else {
					let width = (border && border.width) || widthDefaultValue
					width = this.value2px(width)
					let radius = (border && border.radius) || radiusDefaultValue
					radius = this.value2px(radius)
					obj = {
						'border-width': width,
						'border-style': (border && border.style) || 'solid',
						'border-color': (border && border.color) || '#eee',
						'border-radius': radius
					}
				}
				let classles = ''
				for(let i in obj){
					classles+= `${i}:${obj[i]};`
				}
				return classles
			}
		},
		methods: {
			uploadFiles(item, index) {
				this.$emit("uploadFiles", item)
			},
			choose() {
				this.$emit("choose")
			},
			delFile(index) {
				this.$emit('delFile', index)
			},
			prdivimg(img, index) {
				let urls = []
				if(Number(this.limit) === 1&&this.disablePrediv&&!this.disabled){
					this.$emit("choose")
				}
				if(this.disablePrediv) return
				this.filesList.forEach(i => {
					urls.push(i.url)
				})

				uni.predivimg({
					urls: urls,
					current: index
				});
			},
			// |||||||||||||||||||||||此处有调整-开始|||||||||||||||||||||||||||||||||
			// 打开文件
			openFile(item, index){
				// #ifdef H5
				self.location.href = item.url
				// #endif
				// #ifndef H5
				fileDownload(item.uuid).then(response => {
					uni.openDocument({
						filePath: response.tempFilePath,
						success: () => {
							
						}
					});
				})
				// #endif
			},
			// |||||||||||||||||||||||此处有调整-结束|||||||||||||||||||||||||||||||||
			value2px(value) {
				if (typeof value === 'number') {
					value += 'px'
				} else {
					if (value.indexOf('%') === -1) {
						value = value.indexOf('px') !== -1 ? value : value + 'px'
					}
				}
				return value
			}
		}
	}
</script>

<style lang="scss">
	.uni-file-picker__container {
		/* #ifndef APP-NVUE */
		display: flex;
		box-sizing: border-box;
		/* #endif */
		flex-wrap: wrap;
		margin: -5px;
	}

	.file-picker__box {
		position: relative;
		// flex: 0 0 33.3%;
		width: 33.3%;
		height: 0;
		padding-top: 33.33%;
		/* #ifndef APP-NVUE */
		box-sizing: border-box;
		/* #endif */
	}

	.file-picker__box-content {
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		margin: 5px;
		border: 1px #eee solid;
		border-radius: 5px;
		overflow: hidden;
	}

	.file-picker__progress {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		/* border: 1px red solid; */
		z-index: 2;
	}

	.file-picker__progress-item {
		width: 100%;
	}

	.file-picker__mask {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		justify-content: center;
		align-items: center;
		position: absolute;
		right: 0;
		top: 0;
		bottom: 0;
		left: 0;
		color: #fff;
		font-size: 12px;
		background-color: rgba(0, 0, 0, 0.4);
	}

	.file-img {
		width: 100%;
		height: 100%;
	}

	.is-add {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		align-items: center;
		justify-content: center;
	}

	.icon-add {
		width: 50px;
		height: 5px;
		background-color: #f1f1f1;
		border-radius: 2px;
	}

	.rotate {
		position: absolute;
		transform: rotate(90deg);
	}

	.icon-del-box {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		align-items: center;
		justify-content: center;
		position: absolute;
		top: 3px;
		right: 3px;
		height: 26px;
		width: 26px;
		border-radius: 50%;
		background-color: rgba(0, 0, 0, 0.5);
		z-index: 2;
		transform: rotate(-45deg);
	}

	.icon-del {
		width: 15px;
		height: 2px;
		background-color: #fff;
		border-radius: 2px;
	}
</style>
