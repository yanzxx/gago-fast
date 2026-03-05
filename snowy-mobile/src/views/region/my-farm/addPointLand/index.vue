<template>
  <div id="addPointLand">
    <div id="map" class="map" ref="mapContainer" v-show="data.nowShow === 'map'">
      <van-icon name="arrow-left" class="back-btn" @click="onClickLeft" />
      <img src="./img/point.png" class="point" />
      <div class="btnBox">
        <div class="leftBtn" @click="clearPolygon">
          <img src="./img/back.png" />
        </div>
        <div class="centerBtn" @click="drawCenterHandle">
          <img src="./img/addPoint.png" />
        </div>
        <div class="rightBtn" @click="temporarySaveHandle">
          <img src="./img/finish.png" />
        </div>
      </div>
      <div class="locationBtn" @click="resetLoaction">
        <img src="./img/origin.png" />
      </div>
    </div>
    <div class="saveLandBox" v-show="data.nowShow === 'page'">
      <van-nav-bar
        title="新增地块"
        className="head-bar"
        left-arrow
        @click-left="onClickLeft2"
      />
      <div class="saveLandInfo">
        <div class="landImgBox">
          <canvas id="landImg"></canvas>
        </div>
        <div class="landInfo" id="landInfo">
          <div class="landInfoHead">
            <img src="./img/popupHead.png" />
          </div>
          <div class="landInfoBody">
            <div class="landBasicInfo">
              <div class="landBasicInfoList">
                <div class="landBasicInfoItem">
                  <div class="landBasicInfoItemLeft">
                    <img src="./img/realarea.png" />
                  </div>
                  <div class="landBasicInfoItemRight">
                    <div><span>{{data.realArea ? data.realArea : '--'}}</span> 亩</div>
                    <div>面积</div>
                  </div>
                </div>
                <div class="landBasicInfoItem">
                  <div class="landBasicInfoItemLeft">
                    <img src="./img/length.png" />
                  </div>
                  <div class="landBasicInfoItemRight">
                    <div><span>{{data.length ? data.length : '--'}}</span> 米</div>
                    <div>周长</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="landInfoTitle">
              <div class="titleBox">
                <div>地块信息</div>
                <div class="titleBg"></div>
              </div>
            </div>
            <div class="landInfoContent">
              <div class="contentBox">
                <van-field v-model="data.shortName" label="地块名称" placeholder="请输入" input-align="right" class="contentField" />
                <van-field v-model="data.area" label="填写面积（亩）" placeholder="请输入" input-align="right" label-width="100px" class="contentField" @blur="areaChangeHandle" />
                <van-field v-model="data.variety" label="作物类型" placeholder="请选择" input-align="right" is-link readonly @click="data.showVariety = true" class="contentField" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="footBox">
        <div class="saveBtn" @click="saveLand">保存</div>
      </div>
    </div>
    <canvas id="landImg2"></canvas>
    <van-popup v-model:show="data.showVariety" position="bottom">
      <van-picker
        title="作物类型"
        :columns="columns"
        @confirm="onConfirm"
        @cancel="data.showVariety = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showLoadingToast, showSuccessToast, showFailToast, closeToast, showToast } from 'vant'
import Map from 'ol/Map'
import VectorLayer from 'ol/layer/Vector'
import VectorSource from 'ol/source/Vector'
import View from 'ol/View'
import "ol/ol.css"
import { Fill, Stroke, Style, Text, Circle } from 'ol/style'
import { getTopLeft, getWidth, boundingExtent, getCenter } from 'ol/extent'
import { WMTS as WMTSource, XYZ as tileXyz } from 'ol/source'
import * as olProj from 'ol/proj'
import TileLayer from 'ol/layer/Tile'
import WMTSTileGrid from 'ol/tilegrid/WMTS'
import { getArea, getLength } from 'ol/sphere'
import Feature from 'ol/Feature'
import Point from 'ol/geom/Point'
import MultiPoint from 'ol/geom/MultiPoint'
import Polygon from 'ol/geom/Polygon'
import LineString from 'ol/geom/LineString'
import { Modify } from "ol/interaction"
import myFarm from '@/api/region/myFarm/index.js'

const route = useRoute()
const router = useRouter()

const columns = [
  { text: '水稻', value: '水稻' },
  { text: '小麦', value: '小麦' },
  { text: '其他', value: '其他' },
];

const mapContainer = ref(null)
const data = reactive({
  nowShow: 'map',
  modify: null,
  featrues: null,
  source: null,
  drawVectorLayer: null,
  centers: [],
  mianji: '',
  zhouchang: '',
  realArea: '',
  length: '',
  img: '',
  border: {
    type: 'Polygon',
    coordinates: [],
    bbox: null,
  },
  area: '',
  variety: '',
  shortName: '',
  showVariety: false,
  address: '',
})

onMounted(() => {
  initMap()
})

/** 天地图 */
const baseTianDiTuLayerLayer = (type, projectionType, opacity, proj, name) => {
	const projection = olProj.get(proj)
	const projectionExtent = projection.getExtent()
	const size = getWidth(projectionExtent) / 256
	const resolutions = new Array(19)
	const matrixIds = new Array(19)
	for (let z = 1; z < 19; ++z) {
		resolutions[z] = size / Math.pow(2, z)
		matrixIds[z] = z
	}
	const BaseLayer = new TileLayer({
		opacity: opacity,
		zIndex: 0,
		minZoom: 0,
		maxZoom: 19,
		className: 'base',
		preload: Infinity,
		source: new WMTSource({
			url: `https://t${Math.round(Math.random() * 7)}.tianditu.gov.cn/${type}_${projectionType}/wmts?tk=94a9ef43494c03ab32a38cba10671c05`,
			layer: type,
			matrixSet: projectionType,
			format: 'tiles',
			projection: projection,
			tileGrid: new WMTSTileGrid({
				origin: getTopLeft(projectionExtent),
				resolutions: resolutions,
				matrixIds: matrixIds
			}),
			style: 'default',
			wrapX: true
		})
	})
	BaseLayer.set('baseMap', name)
	return BaseLayer
}

/** 地图初始化 */
const initMap = () => {
  const bing = baseTianDiTuLayerLayer('img', 'w', 1, 'EPSG:3857', 'satellite')
  const bing2 = new TileLayer({
		source: new tileXyz({
			url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230725/{z}/{x}/{y}/png?token=`,
			projection: 'EPSG:4326'
		})
	})
  // 绘制状态
  let coordinateArr, drawState = false
  // 设置样式
  const style = new Style({
    stroke: new Stroke({
      color: '#47ffff',
      width: 2,
    }),
    fill: new Fill({
      color: 'rgba(10,28,69,0.15)',
    }),
	})
  const circleStyle = new Style({
    image: new Circle({
      radius: 5,
      fill:new Fill({
        color: '#47ffff',
      }),
    }),
    geometry: function (feature) {
      const coordinates = feature.getGeometry().getCoordinates()[0]
      return new MultiPoint(coordinates)
    },
  })
	// 面积的提示框style
	const labelStyle = new Style({
	  text: new Text({
	    font: '14px Calibri,sans-serif',
	    fill: new Fill({
	      color: 'rgba(255, 255, 255, 1)',
	    }),
	    padding: [3, 3, 3, 3],
	    textBaseline: 'bottom',
	    offsetY: -10,
	  }),
	})
	// 周长提示框框style
	const labelStyle2 = new Style({
	  text: new Text({
	    font: '14px Calibri,sans-serif',
	    fill: new Fill({
	      color: 'rgba(255, 255, 255, 1)',
	    }),
	    padding: [3, 3, 3, 3],
	    textBaseline: 'bottom',
	    offsetY: 10,
	  }),
	})
	// 多边形变成提示
	const tipStyle = new Style({
	  text: new Text({
	    font: '12px Calibri,sans-serif',
	    fill: new Fill({
	      color: 'rgba(255, 255, 255, 1)',
	    }),
	    backgroundFill: new Fill({
	      color: 'rgba(0, 0, 0, 0.4)',
	    }),
	    padding: [2, 2, 2, 2],
	    textAlign: 'left',
	    offsetX: -15,
	  }),
	})
	const modifyStyle = new Style({
	  image: new Circle({
	    radius: 5,
	    stroke: new Stroke({
	      color: 'rgba(0, 0, 0, 0.7)',
	    }),
	    fill: new Fill({
	      color: 'rgba(0, 0, 0, 0.4)',
	    }),
	  }),
	  text: new Text({
	    font: '12px Calibri,sans-serif',
	    fill: new Fill({
	      color: 'rgba(255, 255, 255, 1)',
	    }),
	    backgroundFill: new Fill({
	      color: 'rgba(0, 0, 0, 0.7)',
	    }),
	    padding: [2, 2, 2, 2],
	    textAlign: 'left',
	    offsetX: 5,
	  }),
	})
	// 边长框样式
	const segmentStyle = new Style({
	  text: new Text({
	    font: '12px Calibri,sans-serif',
	    fill: new Fill({
	      color: 'rgba(255, 255, 255, 1)',
	    }),
	    padding: [2, 2, 2, 2],
	    textBaseline: 'bottom',
	    offsetY: -5,
	  }),
	})
	const segmentStyles = [segmentStyle]
  //获取多边形面积
  const formatArea = function (polygon) {
    const area = getArea(polygon, {projection: 'EPSG:4326'})
    let output
    output = Number(Math.round(area * 100) / 100*0.0015).toFixed(2) + '亩'
    return output
  }
  //获取线段周长
  const formatLength = function (line) {
    const length =  getLength(line, {projection: 'EPSG:4326'})
    let output
    output = Number(Math.round(length * 100) / 100).toFixed(2) + '米'
    if(Number(length)>0){
      return output
    }else{
      return "none"
    }
  }
  data.featrues = new Feature({
    geometry: new Polygon([])
  })
  data.source = new VectorSource({
    features: [
      data.featrues
    ]
  })
  data.modify = new Modify({source: data.source, style: modifyStyle})
  let tipPoint
  function styleFunction(feature, segments, drawType, tip) {
		const styles = [style,circleStyle]
		const geometry = feature.getGeometry()
		const type = geometry.getType()
		let point, label, line, label2, point2, lineLength
		if (!drawType || drawType === type) {
		  if (type === 'Polygon') {
		    line = new LineString(geometry.getCoordinates()[0])
		    point = geometry.getInteriorPoint()
		    label = formatArea(geometry)
		    label2 =  formatLength(geometry)
		    point2 = geometry.getInteriorPoint()
		  }
		}
		if (segments && line) {
		  let count = 0
		  line.forEachSegment(function (a, b) {
		    const segment = new LineString([a, b])
		    const label = formatLength(segment)
		    if (segmentStyles.length - 1 < count) {
		      segmentStyles.push(segmentStyle.clone())
		    }
		    const segmentPoint = new Point(segment.getCoordinateAt(0.5))
		    segmentStyles[count].setGeometry(segmentPoint)
		    segmentStyles[count].getText().setText(label)
		    if(!label.includes('none')){
          styles.push(segmentStyles[count])
        }
		    count++
		  })
		}
		if (label2 || label) {
			data.mianji = label
			data.zhouchang = label2
		  labelStyle2.getText().setText(label2)
		  styles.push(labelStyle2)
		  labelStyle.getText().setText(label)
		  styles.push(labelStyle)
		}
		if (tip && type === 'Point' && !data.modify.getOverlay().getSource().getFeatures().length) {
		  tipPoint = geometry
		  tipStyle.getText().setText(tip)
		  styles.push(tipStyle)
		}
		return styles
	}
  data.drawVectorLayer = new VectorLayer({
	  source: data.source,
	  style: function (feature) {
	    return styleFunction(feature, true)
	  },
	})
  map.value = new Map({
    view: new View({
      center: [120.5753880739212, 32.499661445617676],
      zoom: 7,
      minZoom: 2,
      maxZoom: 18,
      projection: 'EPSG:4326',
    }),
    layers: [bing, bing2, data.drawVectorLayer],
    target: mapContainer.value,
  })
  map.value.getView().fit([120.55738250199978, 32.49469701308624, 120.58182389258319, 32.50264718815811], {
	  padding: [200, 300, 200, 200], // 可选：指定视图边缘的填充，以腾出空间显示要素
    duration: 1000,
  })
  map.value.addInteraction(data.modify)
  data.modify.on('modifyend', function (e) {
    data.centers = e.features.item(0).getGeometry().getCoordinates()[0]
  })
  getCenterTokenHandle()
}
/** 获取业务中台token */
const getCenterTokenHandle = () => {
	myFarm.getCenterToken().then((res) => {
		if(res.code === 200) {
      const TileLayer = map.value.getLayers().array_[1]
      TileLayer.setSource(
        new tileXyz({
          url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230725/{z}/{x}/{y}/png?token=${res.data.accessToken}`,
          projection: 'EPSG:4326'
        })
      )
    }
	})
}
/** 返回 */
const onClickLeft = () => {
  router.go(-1)
}
/** 返回 */
const onClickLeft2 = () => {
  data.nowShow = 'map'
}
/** 定位 */
const resetLoaction = () => {
  map.value.getView().fit([120.55738250199978, 32.49469701308624, 120.58182389258319, 32.50264718815811], {
	  padding: [200, 300, 200, 200], // 可选：指定视图边缘的填充，以腾出空间显示要素
    duration: 1000,
  })
}
/** 打点 打点的问题存在重复点击 */
const drawCenterHandle = () => {
  if(data.centers.length===0){
    data.centers.push(map.value.getView().getCenter())
  }else{
    if(data.centers.length>=2){
      if(data.centers[data.centers.length-1].toString()===data.centers[0].toString()){
        data.centers.splice(data.centers.length-1,1)
        data.centers.push(map.value.getView().getCenter())
        data.centers.push(data.centers[0])
      }else{
        data.centers.push(map.value.getView().getCenter())
        data.centers.push(data.centers[0])
      }
    }
    if(!data.centers.includes(map.value.getView().getCenter())||map.value.getView().getCenter().toString()===data.centers[0].toString()){
      data.centers.push(map.value.getView().getCenter())
    }
  }
  data.featrues.getGeometry().setCoordinates([data.centers])
}
/** 清除多边形 */
const clearPolygon = () => {
  if(data.centers.length>0){
    data.centers.splice(data.centers.length - 1, 1)
    data.featrues.getGeometry().setCoordinates([data.centers])
  }
}
/** 确定 */
const temporarySaveHandle = () => {
  if(data.centers[0].toString() === data.centers[data.centers.length - 1].toString()) {
    drawCanvasHandle(data.centers, 'landImg')
    drawCanvasHandle2(data.centers, 'landImg2')
    const mianjis = Number(data.mianji.slice(0, data.mianji.length - 1))
    const zhouchangs = Number(data.zhouchang.slice(0, data.zhouchang.length - 1))
    if((mianjis > 0 && zhouchangs > 0)) {
      data.realArea = Number(mianjis).toFixed(2)
      data.length = Number(zhouchangs).toFixed(2)
      data.nowShow = 'page'
      let extent = boundingExtent(data.centers)
      let center = getCenter(extent)
      map_click(center)
    }
  }else{
    showToast('地块未闭合不能保存')
    return
  }
}
/** 绘制地块图像 */
const drawCanvasHandle = (positionsArr, name) => {
  const positions = positionsArr
  // canvas元素对应的宽高
  let canvasHeight = window.innerWidth
  let canvasWidth = 180
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
  let xoffset = (canvasWidth - (xMax - xMin) * scale) / 2 + 120
  let yoffset = (canvasHeight - (yMax - yMin) * scale) / 2 + 180
  let canvas = document.getElementById(name)
  canvas.width = window.innerWidth
  canvas.height = 180
  let ctx = canvas.getContext('2d')
  ctx.clearRect(0, 0, window.innerWidth, 180)
  // 由于canvas坐标轴与经纬度不相同，所以需要平移原点并旋转画布
  ctx.translate(0, canvasWidth)
  ctx.rotate(-Math.PI / 2)
  ctx.beginPath()
  // 根据偏移量移动点位并画图
  let img = new Image()
  img.src = '/static/images/bgm3.png'
  img.align='clClient'
  img.setAttribute("crossOrigin",'Anonymous')
  img.onload = imgfn  // 图片加载完在执行
  function imgfn() {
    ctx.drawImage(img, 0, 0, window.innerWidth, img.height)
    ctx.scale(0.5, 0.5)
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
    ctx.scale(0.8, 1)
    let dataUrl = canvas.toDataURL("image/png")
  }
}
/** 绘制地块图像 */
const drawCanvasHandle2 = (positionsArr, name) => {
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
  let canvas = document.getElementById(name)
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
  img.src = '/static/images/bgm2.png'
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
    data.img = dataUrl
  }
}
/** 确定作物类型 */
const onConfirm = ({ selectedValues }) => {
  data.variety = selectedValues[0]
  data.showVariety = false
}
/** 保存 */
const saveLand = () => {
  if(!data.shortName) {
    showToast('请输入地块名称')
    return
  }
  if(!data.area) {
    showToast('请输入地块面积')
    return
  }
  if(!data.variety) {
    showToast('请选择作物类型')
    return
  }
  showLoadingToast({
    message: '保存中...',
    forbidClick: true,
    duration: 30000,
  })
  data.border.coordinates[0] = data.centers
  myFarm.addLandInfo({
    shortName: data.shortName,
    name: data.shortName,
    area: data.area,
    realArea: data.realArea,
    length: data.length,
    img: data.img,
    border: data.border,
    address: data.address,
    variety: data.variety,
  }).then(res => {
    if(res.code === 200) {
      closeToast()
      showSuccessToast('保存成功')
      router.go(-1)
    }else {
      closeToast()
      showFailToast('保存失败')
    }
  }).catch(() => {
    closeToast()
    showFailToast('保存失败')
  })
}
/** 获取地块位置 */
const map_click = (coord) => {
  let geocoder
  // AMap.plugin 表示高德地图插件
  AMap.plugin('AMap.Geocoder',function() {
    geocoder = new AMap.Geocoder({
      // city 指定进行编码查询的城市，支持传入城市名、adcode 和 citycode
      radius: 30 
    })
    geocoder.getAddress(coord, function(status, result) {
      if (status === 'complete' && result.info === 'OK') {
        // result为对应的地理位置详细信息
        let val  = result.regeocode.addressComponent
        data.address = val.province + val.city + val.district + val.township;
      }
    })
  })
}
/** 校验地块面积 */
const areaChangeHandle = () => {
  if(isNaN(Number(data.area)) || Number(data.area) < 0) {
    data.area = ''
  }else if(Number(data.area) == 0) {
    data.area = '0'
  }else {
    data.area = JSON.stringify(Number(Number(data.area).toFixed(2)))
  }
}
</script>

<style lang="less" scoped>
#addPointLand {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  .map {
    width: 100vw;
    height: 100vh;
	  position: relative;
    .back-btn {
      position: absolute;
      top: 20px;
      left: 15px;
      color: #fff;
      font-size: 20px;
      z-index: 100;
    }
    .point {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      z-index: 100;
    }
    .btnBox {
      position: absolute;
      bottom: 70px;
      left: 50%;
      transform: translate(-50%, 0);
      z-index: 100;
      display: flex;
      align-items: center;
      justify-content: center;
      .leftBtn, .rightBtn {
        width: 40px;
        height: 40px;
        border-radius: 20px;
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        img {
          width: 60%;
          height: 60%;
          margin: 20%;
        }
      }
      .centerBtn {
        width: 60px;
        height: 60px;
        border-radius: 30px;
        background-color: #4d81e6;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 20px;
        img {
          width: 60%;
          height: 60%;
          margin: 20%;
        }
      }
    }
    .locationBtn {
      width: 40px;
      height: 40px;
      border-radius: 20px;
      background-color: #fff;
      position: absolute;
      bottom: 80px;
      left: 15px;
      z-index: 100;
      img {
        width: 60%;
        height: 60%;
        margin: 20%;
      }
    }
  }
  .saveLandBox {
    width: 100%;
    height: 100%;
    overflow: hidden;
    .head-bar {
      width: 100%;
      height: 50px;
      color: #fff;
      background-color: #008F5F;
      --van-nav-bar-height: 50px;
      --van-nav-bar-title-font-size: 18px;
      --van-nav-bar-title-text-color: #fff;
      --van-nav-bar-arrow-size: 18px;
      --van-font-size-md: 16px;
      .van-icon {
        font-size: 18px;
      }
    }
    .saveLandInfo {
      width: 100%;
      height: calc(100% - 110px);
      overflow: hidden;
      .landImgBox {
        width: 100vw;
        height: 180px;
      }
      .landInfo {
        width: 100vw;
        height: calc(100% - 275px);
        position: absolute;
        bottom: 60px;
        left: 0;
        background-color: #f5f6f7;
        border-radius: 15px 15px 0 0;
        .landInfoHead {
          width: 100%;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          img {
            width: 32px;
          }
        }
        .landInfoBody {
          width: 100%;
          height: calc(100% - 24px);
          .landBasicInfo {
            width: calc(100% - 32px);
            height: 70px;
            background-color: #fff;
            border-radius: 8px;
            margin: 0 16px 15px 16px;
            padding: 8px 10px;
            .landBasicInfoList {
              width: 100%;
              height: 50px;
              display: flex;
              align-items: center;
              justify-content: flex-start;
              margin-bottom: 4px;
              .landBasicInfoItem {
                width: 50%;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: flex-start;
                .landBasicInfoItemLeft {
                  width: 45px;
                  height: 45px;
                  img {
                    width: 100%;
                    height: 100%;
                  }
                }
                .landBasicInfoItemRight {
                  width: calc(100% - 55px);
                  height: 100%;
                  margin-left: 10px;
                  font-size: 14px;
                  display: flex;
                  flex-direction: column;
                  align-items: flex-start;
                  justify-content: center;
                  div:first-child {
                    span {
                      font-size: 18px;
                      font-weight: bold;
                    }
                  }
                  div:last-child {
                    color: #58756f;
                  }
                }
              }
            }
            .landBasicInfoList:last-child {
              margin-bottom: 0;
            }
          }
          .landInfoTitle {
            width: calc(100% - 32px);
            height: 50px;
            margin: 0 16px;
            font-size: 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-top: 1px solid rgba(0,0,0,0.15);
            .titleBox {
              font-weight: bold;
              position: relative;
              .titleBg {
                width: 40px;
                height: 6px;
                background: linear-gradient(270deg, rgba(0,170,255,0.04) 0%, rgba(41,130,86,0.45) 100%);
                position: absolute;
                left: 0;
                bottom: 2px;
              }
            }
          }
          .landInfoContent {
            width: 100%;
            height: calc(100% - 135px);
            overflow-x: hidden;
            overflow-y: auto;
            padding: 0 10px;
            .contentBox {
              background-color: #fff;
              border-radius: 15px;
              padding: 5px;
            }
          }
          .landInfoContent::-webkit-scrollbar {
            display: none;
          }
        }
      }
    }
    .footBox {
      width: 100%;
      height: 60px;
      background-color: #fff;
      padding: 10px 16px;
      .saveBtn {
        width: 100%;
        height: 40px;
        line-height: 40px;
        text-align: center;
        background: linear-gradient(90deg, #31AD62 0%, #008F5F 100%);
        color: #fff;
        border-radius: 20px;
        font-weight: bold;
        font-size: 18px;
      }
    }
  }
  #landImg2 {
    position: absolute;
    top: -100px;
    left: -100px;
  }
}
</style>
<style lang="less">
.ol-zoom {
  display: none;
}
.contentField {
  .van-field__label {
    --van-field-label-color: #58756f !important;
  }
}
</style>
