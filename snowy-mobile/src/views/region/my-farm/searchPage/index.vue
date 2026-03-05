<template>
  <div id="searchPage">
    <van-nav-bar
      title="搜索"
      className="head-bar"
      left-arrow
      @click-left="onClickLeft"
    />
    <van-search
      v-model="data.shortName"
      show-action
      placeholder="请输入地块名称"
      @clear="clearLand"
    >
      <template #action>
        <div @click="searchLand" class="searchBtn">搜索</div>
      </template>
    </van-search>
    <div class="landListBox">
      <van-pull-refresh v-model="data.refreshLoading" @refresh="onRefresh" v-if="data.isSearch && data.landList.length > 0">
        <van-list
          v-model:loading="data.loading"
          :finished="data.finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div v-for="item in data.landList" :key="item.id" class="landItem" @click="gotoLandDetails(item)">
            <div class="landItemHead">
              <div>{{item.shortName}}</div>
              <img src="./img/landEdit.png" @click.stop="gotoEditLand(item)" />
            </div>
            <div class="landItemBody">
              <div class="landImgBox">
                <img class="landImg" :src="item.img ? item.img : item.landImg" />
                <div class="landTypeBg">画地</div>
              </div>
              <div class="landInfo">
                <div class="infoItem">
                  <div class="item-1">
                    <img src="./img/realarea.png" />
                    <div>{{(item.realArea ? item.realArea : '--') + '亩'}}</div>
                  </div>
                  <div class="item-2">
                    <img src="./img/area.png" />
                    <div>{{(item.area ? item.area : '--') + '亩'}}</div>
                  </div>
                </div>
                <div class="infoItem">
                  <div class="item">
                    <img src="./img/length.png" />
                    <div>{{(item.length ? item.length : '--') + '米'}}</div>
                  </div>
                </div>
                <div class="infoItem">
                  <div class="item">
                    <img src="./img/crop.png" />
                    <div>{{item.variety ? item.variety : '其他'}}</div>
                  </div>
                </div>
                <div class="infoItem">
                  <div class="item">
                    <img src="./img/address.png" />
                    <div>{{item.address ? item.address : '--'}}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
      <div class="noDataBox" v-if="data.isSearch && data.landList.length === 0">
        当前区域未找到相关结果
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import myFarm from "@/api/region/myFarm/index.js"
import GeoJSON from 'ol/format/GeoJSON'
import VectorSource from 'ol/source/Vector'
import { getLength } from 'ol/sphere'

const route = useRoute()
const router = useRouter()

const value = ref('')

const data = reactive({
  shortName: '',
  landList: [],
  refreshLoading: false,
  loading: false,
  finished: true,
  isSearch: false,
})

onMounted(() => {})

/** 返回 */
const onClickLeft = () => {
  router.go(-1)
}
/** 搜索 */
const searchLand = () => {
  onRefresh()
}
/** 获取作业轨迹 */
const onLoad = () => {
  myFarm.getLandList({
    shortName: data.shortName,
  }).then(res => {
    if(res.code === 200) {
      data.refreshLoading = false
      data.landList = res.data
      let searchPage = document.getElementById('searchPage')
      for(let i = 0; i < data.landList.length; i++) {
        data.landList[i] = {
          ...data.landList[i],
          landImg: data.landList[i].landImg ? data.landList[i].landImg : '',
          length: data.landList[i].length ? data.landList[i].length : '',
        }
        if(!data.landList[i].img && !data.landList[i].landImg) {
          let canvas = document.createElement('canvas')
          canvas.id = 'landImg' + i
          canvas.style.position = 'absolute'
          canvas.style.top = '-100px'
          canvas.style.left = '-100px'
          searchPage.appendChild(canvas)
          drawCanvasHandle(data.landList[i].border.coordinates[0], 'landImg', i)
        }
      }
      pointAndPloygonHandle(data.landList, 'all')
      data.isSearch = true
    }else {
      data.loading = false
      data.finished = true
      data.refreshLoading = false
      data.isSearch = true
    }
  }).catch(() => {
    data.loading = false
    data.finished = true
    data.refreshLoading = false
    data.isSearch = true
  })
}
/** 刷新 */
const onRefresh = () => {
  data.refreshLoading = true
  onLoad()
}
/** 绘制地块图像 */
const drawCanvasHandle = (positionsArr, name, index) => {
  const positions = positionsArr
  // canvas元素对应的宽高
  let canvasHeight = 100
  let canvasWidth = 100
  let amuXArr = positions.map(function (item) {
    return item[1]
  })
  let amuYArr = positions.map(function (item) {
    return item[0]
  })
  // 注意：y轴需要进行取负操作
  .map((item) => {
    return item * -1
  })
  // 拿到方向轴的最大最小值
  let xMax = Math.max(...amuXArr)
  let xMin = Math.min(...amuXArr)
  let yMax = Math.max(...amuYArr)
  let yMin = Math.min(...amuYArr)
  // 根据canvas宽高计算缩放级别
  let xScale = canvasWidth / (xMax - xMin)
  let yScale = canvasHeight / (yMax - yMin)
  let scale = xScale < yScale ? xScale : yScale
  // 计算偏移量
  let xoffset = (canvasWidth - (xMax - xMin) * scale) / 2 + 15
  let yoffset = (canvasHeight - (yMax - yMin) * scale) / 2 + 10
  let canvas = document.getElementById(name + index)
  canvas.width = 100
  canvas.height = 100
  let ctx = canvas.getContext('2d')
  ctx.clearRect(0, 0, 100, 100)
  // 由于canvas坐标轴与经纬度不相同，所以需要平移原点并旋转画布
  ctx.translate(0, canvasWidth)
  ctx.rotate(-Math.PI / 2)
  ctx.beginPath()
  // 根据偏移量移动点位并画图
  let img = new Image()
  img.src = '/img/bgm2.png'
  img.align='clClient'
  img.setAttribute("crossOrigin",'Anonymous')
  img.onload = imgfn  // 图片加载完在执行
  function imgfn() {
    ctx.drawImage(img, 0, 0, 100, 100)
    ctx.scale(0.8, 0.8)
    ctx.moveTo(
      (amuXArr[0] - xMin) * scale + xoffset,
      (yMax - amuYArr[0]) * scale + yoffset
    )
    for(let i = 1; i < amuXArr.length; i++) {
      ctx.lineTo(
        (amuXArr[i] - xMin) * scale + xoffset,
        (yMax - amuYArr[i]) * scale + yoffset
      )
    }
    ctx.fillStyle="rgba(10,28,69,0.15)"
    ctx.fill()
    ctx.closePath()
    ctx.strokeStyle = "#47ffff"
    ctx.stroke()
    ctx.scale(0.8, 0.8)
    let dataUrl = canvas.toDataURL("image/png")
    data.landList[index].landImg = dataUrl
  }
}
/** 封装展示点和面的函数 */
const pointAndPloygonHandle = (totalArr, type) => {
	if (totalArr) {
		const realPolygonArr = []
		totalArr.forEach((its) => {
			realPolygonArr.push({
				geometry: {
					coordinates: its.border.coordinates,
					type: its.border.type
				},
				type: 'Feature',
				properties: {
					type: 'landType',
					short_name: its.shortName,
					name: its.name,
					id: its.id,
					area: its.area,
					manager: its.manager,
					phone: its.phone,
					variety: its.variety,
					inputTime: its.inputTime,
					parcelCode: its.parcelCode
				}
			})
		})
		const polygonArr = {
			type: 'FeatureCollection',
			crs: {
				type: 'name',
				properties: {
					name: 'EPSG:4490'
				}
			},
			features: realPolygonArr
		}
    const source = new VectorSource({
      features: new GeoJSON().readFeatures(polygonArr, {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:4490'
      })
    })
    let features = source.getFeatures()
    for(let i = 0; i < features.length; i++) {
      let length = formatLength(features[i].getGeometry())
      data.landList[i].length = length
    }
	}
}
/** 计算周长 */
const formatLength = (line) => {
  const length = getLength(line, { projection: 'EPSG:4326' })
  let output
  output = Number(Math.round(length * 100) / 100).toFixed(2)
  if(Number(length) > 0) {
    return output
  }else {
    return "none"
  }
}
/** 查看地块详情 */
const gotoLandDetails = (node) => {
  localStorage.setItem('landDetails', JSON.stringify(node))
  router.push({
    path: '/landDetails',
    query: {
      path: 'search'
    }
  })
}
/** 编辑 */
const gotoEditLand = (node) => {
  localStorage.setItem('editLand', JSON.stringify(node))
  router.push({
    path: '/editLand',
    query: {
      path: 'search'
    }
  })
}
/** 清空地块 */
const clearLand = () => {
  data.isSearch = false
  data.shortName = ''
  data.landList = []
}
</script>

<style lang="less" scoped>
#searchPage {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
  .head-bar {
    width: 100%;
    height: 50px;
    color: #000;
    background-color: #fff;
    --van-nav-bar-height: 50px;
    --van-nav-bar-title-font-size: 18px;
    --van-nav-bar-title-text-color: #000;
    --van-nav-bar-arrow-size: 18px;
    --van-font-size-md: 16px;
    .van-icon {
      font-size: 18px;
    }
  }
  .van-search {
    z-index: 100;
    height: 50px;
    .searchBtn {
      color: #298256;
    }
  }
  .landListBox {
    width: 100%;
    height: calc(100% - 100px);
    background-color: #f5f6f7;
    overflow-x: hidden;
    overflow-y: auto;
    padding-top: 15px;
    .landItem {
      width: calc(100% - 32px);
      height: 170px;
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0px 0px 8px 0px rgba(18,45,92,0.06);
      margin-bottom: 10px;
      margin-left: 16px;
      padding: 0 10px 10px 10px;
      .landItemHead {
        width: 100%;
        height: 50px;
        border-bottom: 1px dashed #ddd;
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-size: 16px;
        font-weight: bold;
        img {
          width: 34px;
          height: 34px;
        }
      }
      .landItemBody {
        width: 100%;
        height: 110px;
        padding-top: 10px;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        .landImgBox {
          width: 100px;
          height: 100px;
          border-radius: 8px;
          position: relative;
          .landImg {
            width: 100px;
            height: 100px;
            border-radius: 8px;
          }
          .landTypeBg {
            position: absolute;
            top: 0;
            left: 0;
            height: 20px;
            line-height: 18px;
            background-image: url('./img/paintedBg.png');
            background-size: 100% 100%;
            background-repeat: no-repeat;
            padding: 1px 10px;
            color: #fff;
            font-size: 12px;
          }
        }
        .landInfo {
          width: calc(100% - 110px);
          height: 100px;
          margin-left: 10px;
          .infoItem {
            width: 100%;
            height: 25px;
            display: flex;
            align-items: center;
            justify-content: flex-start;
            .item-1, .item-2 {
              width: calc(50% - 3px);
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: flex-start;
              font-size: 16px;
              div {
                white-space: nowrap; /* 禁止文本换行 */
                overflow: hidden; /* 隐藏溢出的文本 */
                text-overflow: ellipsis; /* 显示省略号 */
              }
              img {
                width: 15px;
                height: 15px;
                margin-right: 5px;
              }
            }
            .item-1 {
              margin-right: 6px;
            }
            .item {
              width: 100%;
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: flex-start;
              font-size: 16px;
              div {
                white-space: nowrap; /* 禁止文本换行 */
                overflow: hidden; /* 隐藏溢出的文本 */
                text-overflow: ellipsis; /* 显示省略号 */
              }
              img {
                width: 15px;
                height: 15px;
                margin-right: 5px;
              }
            }
          }
        }
      }
    }
    .landItem:last-child {
      margin-bottom: 0 !important;
    }
    .noDataBox {
      width: calc(100% - 32px);
      height: 50px;
      border-radius: 8px;
      background-color: #fff;
      margin: 0 16px;
      line-height: 50px;
      text-align: center;
      color: rgba(0, 0, 0, 0.25);
      font-size: 15px;
    }
  }
  .landListBox::-webkit-scrollbar {
    display: none;
  }
}
</style>
