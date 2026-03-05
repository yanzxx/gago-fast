<template>
  <div id="landGrowth">
    <div id="mapGrowth" class="mapGrowth" ref="mapGrowthContainer" @click="closeAll">
      <!-- 右侧工具栏 -->
      <div class="mapTypeBox" @click.stop>
        <img src="./img/map.png" @click="showMapType" v-if="!data.mapHigh" />
        <img src="./img/mapHigh.png" @click="closeMapType" v-else />
      </div>
      <!-- 地图类型 -->
      <div class="mapTypeListBox" id="mapTypeListBox" @click.stop>
        <div class="typeListTitle">数据源</div>
        <div class="typeContent">
          <van-radio-group v-model="data.formlayers.resource" shape="dot" @change="resourceHandle">
            <van-radio name="1" checked-color="#298256">空-无人航拍</van-radio>
            <van-radio name="2" checked-color="#298256">天-卫星遥感</van-radio>
          </van-radio-group>
          <div class="line"></div>
          <van-radio-group v-model="data.formlayers.layerType" shape="dot" @change="layerTypeHandle">
            <van-radio name="1" checked-color="#298256">长势图层</van-radio>
            <van-radio name="2" checked-color="#298256">影像图层</van-radio>
          </van-radio-group>
        </div>
      </div>
      <!-- 底部内容 -->
      <div class="bottomContentBox" @click.stop>
        <div class="tabBox">
          <div :class="data.activeTab === 0 ? 'selectedTabItem' : 'tabItem'" @click="data.activeTab = 0">长势监测<img src="./img/lefttab.png" class="lefttab" v-if="data.activeTab === 0" /></div>
          <div :class="data.activeTab === 1 ? 'selectedTabItem' : 'tabItem'" @click="data.activeTab = 1; growthCurrentHandle();">长势趋势<img src="./img/centertab.png" class="centertab" v-if="data.activeTab === 1" /></div>
          <div :class="data.activeTab === 2 ? 'selectedTabItem' : 'tabItem'" @click="data.activeTab = 2; growthAreaHandle();">长势分级<img src="./img/righttab.png" class="righttab" v-if="data.activeTab === 2" /></div>
        </div>
        <div class="contentBox">
          <div class="timeListBox" id="timeListBox" ref="timeListBox" v-show="data.activeTab === 0">
            <div :class="data.activeTime === index ? 'selectedTimeItem' : 'timeItem'" v-for="(item, index) in data.timeList" :key="index" @click="timechange(item, index)">
              <div class="yearBox">{{item.date.slice(0, 4)}}</div>
              <div class="dayBox">{{item.date.slice(4, 6)}}-{{item.date.slice(6, 8)}}</div>
              <img :src="item.icon" class="iconBox" />
              <div class="cloudBox">云量{{Number((item.cloudCover * 100).toFixed(2))}}%</div>
            </div>
          </div>
          <!-- 本年度长势趋势 -->
					<div class="growthLine" id="growthLine" ref="growthLine" v-show="data.activeTab === 1" />
          <!-- 本年度长势分级 -->
          <div class="eachGrowthArea" id="eachGrowthArea" ref="eachGrowthArea" v-show="data.activeTab === 2" />
        </div>
      </div>
      <!-- 左侧图例内容 -->
      <div class="leftLegendBox" @click.stop>
        <div class="showLegendList">
					<div
						class="showLegendItem"
						v-for="(item, index) of data.legendList"
						:key="index"
						@click="legendHandle(item)"
					>
						<i
							:style="{
								backgroundImage: item.state
									? 'linear-gradient(to bottom right,' + item.c1 + ',' + item.c2
									: 'linear-gradient(to bottom right,#ccc,#ccc'
							}"
						/>
						<span>{{ item.name }}</span>
					</div>
				</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import GeoJSON from 'ol/format/GeoJSON'
import Map from 'ol/Map'
import VectorLayer from 'ol/layer/Vector'
import VectorSource from 'ol/source/Vector'
import View from 'ol/View'
import { getVectorContext } from 'ol/render'
import "ol/ol.css"
import { Fill, Stroke, Style, Text } from 'ol/style'
import { getTopLeft, getWidth } from 'ol/extent'
import { WMTS as WMTSource, XYZ as tileXyz } from 'ol/source'
import proj4 from 'proj4'
import { register } from 'ol/proj/proj4'
import * as olProj from 'ol/proj'
import { Projection, addProjection } from 'ol/proj'
import TileLayer from 'ol/layer/Tile'
import WMTSTileGrid from 'ol/tilegrid/WMTS'
import Feature from 'ol/Feature'
import MultiPolygon from 'ol/geom/MultiPolygon'
import { colorLercHandle, editLercHandle } from '@/utils/layer'
import * as echarts from 'echarts'
import axios from 'axios'
import myFarm from "@/api/region/myFarm/index.js"
import icon1 from './img/icon1.png'
import icon2 from './img/icon2.png'
import icon3 from './img/icon3.png'

const route = useRoute()
const router = useRouter()

const map = ref(null)
const mapGrowthContainer = ref(null)
const timeListBox = ref(null)
const growthLine = ref(null)
const eachGrowthArea = ref(null)
const data = reactive({
  featureObj: null,
  actual: '20230725',
  timeList: [],
	cloudList: [],
  accessToken: '69c5397506b57238fc82ce3132fad95d',
  legendList: [
		{
			name: '很旺',
			c1: '#00802b',
			c2: '#00802b',
			state: true,
			colorArr: [
				[223, '#00802b'],
				[256, '#00802b']
			],
			val: 5
		},
		{
			name: '较旺',
			c1: '#8ebf6a',
			c2: '#00802b',
			state: true,
			colorArr: [
				[191, '#8ebf6a'],
				[223, '#00802b']
			],
			val: 4
		},
		{
			name: '中等',
			c1: '#faffad',
			c2: '#8ebf6a',
			state: true,
			colorArr: [
				[160, '#faffad'],
				[191, '#8ebf6a']
			],
			val: 3
		},
		{
			name: '较弱',
			c1: '#edad57',
			c2: '#faffad',
			state: true,
			colorArr: [
				[128, '#edad57'],
				[160, '#faffad']
			],
			val: 2
		},
		{
			name: '极差',
			c1: '#e05a00',
			c2: '#edad57',
			state: true,
			val: 1,
			colorArr: [
				[1, '#e05a00'],
				[128, '#edad57']
			]
		}
	],
  growthTrendObj: {
		date: '2023-07-25',
		landId: null,
		type: '1',
		year: new Date().getFullYear() + ''
	},
	lercType: 'ndvi_drone',
	formlayers: {
		resource: '1',
    layerType: '1',
	},
  projection: null,
  mapHigh: false,
  activeTab: 0,
  activeTime: null,
})

onMounted(() => {
  initMap()
  getTimeLineHandle('ndvi_drone')//时间轴
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
			url: `https://t${Math.round(
				Math.random() * 7
			)}.tianditu.gov.cn/${type}_${projectionType}/wmts?tk=94a9ef43494c03ab32a38cba10671c05`,
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
		}),
		visible: false
	})
	BaseLayer.set('baseMap', name)
	return BaseLayer
}
/** 天地图 */
const baseTianDiTuLayerLayer2 = (type, projectionType, opacity, proj, name) => {
	const projection = olProj.get(proj)
	const projectionExtent = projection.getExtent()
	const size = getWidth(projectionExtent) / 256
	const resolutions = new Array(19)
	const matrixIds = new Array(19)
	for (let z = 1; z < 19; ++z) {
		resolutions[z] = size / Math.pow(2, z)
		matrixIds[z] = z
	}
	const baseLayer = new TileLayer({
		opacity,
		zIndex: 0,
		minZoom: 0,
		maxZoom: 19,
		className: 'base',
		preload: Infinity,
		source: new WMTSource({
			url: `https://t{0-7}.tianditu.gov.cn/${type}_${projectionType}/wmts?tk=94a9ef43494c03ab32a38cba10671c05`,
			layer: type,
			matrixSet: projectionType,
			format: 'tiles',
			projection,
			tileGrid: new WMTSTileGrid({
				origin: getTopLeft(projectionExtent),
				resolutions,
				matrixIds
			}),
			style: 'default',
			wrapX: true
		}),
		visible: true
	})
	baseLayer.set('baseMap', name)
	return baseLayer
}
/** 地图初始化 */
const initMap = () => {
  const stop = [
		[1, '#e05a00'],
		[128, '#edad57'],
		[160, '#faffad'],
		[191, '#8ebf6a'],
		[223, '#00802b'],
		[256, '#00802b']
	]
	const url = `https://opapi.gagogroup.cn/api/v2/publisher/data/ndvi_drone/20230725/{z}/{x}/{y}/lerc?token=${data.accessToken}`
	proj4.defs(
		'EPSG:4490',
		'GEOGCS["China Geodetic Coordinate System 2000",DATUM["China_2000",SPHEROID["CGCS2000",6378137,298.257222101,AUTHORITY["EPSG","1024"]],AUTHORITY["EPSG","1043"]],PRIMEM["Greenwich",0,AUTHORITY["EPSG","8901"]],UNIT["degree",0.0174532925199433,AUTHORITY["EPSG","9122"]],AUTHORITY["EPSG","4490"]]'
	)
	register(proj4)
	data.projection = new Projection({
		code: 'EPSG:4490',
		units: 'degrees',
		axisOrientation: 'neu'
	})
	data.projection.setExtent([-180, -90, 180, 90])
	data.projection.setWorldExtent([-180, -90, 180, 90])
	addProjection(data.projection)
  // 卫星影像图层
	const bing = baseTianDiTuLayerLayer2('img', 'c', 1, 'EPSG:4490', 'satellite')
	// 电子地图图层
	const bingVec = baseTianDiTuLayerLayer('vec', 'c', 1, 'EPSG:4490', 'satelite')
  // 地块图层
  const basePbfLayer = new VectorLayer({
		features: [],
		style: function (feature) {
			const style = new Style({
				fill: new Fill({
					color: 'rgba(10,28,69,0.15)'
				}),
				stroke: new Stroke({
					color: `#47ffff`,
					width: 1
				}),
				text: new Text({
					fill: new Fill({
						color: '#ffffff'
					}),
					text: `${feature.get('short_name') === undefined ? '-' : feature.get('short_name')}`,
					font: 'bold 58 微软雅黑'
				})
			})
			return style
		},
		zIndex: 1001
	})
  // 卫星遥感图层
	const bing2 = new TileLayer({
		source: new tileXyz({
			//https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
			url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
			projection: 'EPSG:4490'
		})
	})
	// 长势地图图层
	const bing3 = new TileLayer({
		source: new tileXyz({
			url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=${data.actual.slice(0,4)}-${data.actual.slice(4,6)}-${data.actual.slice(6,8)}&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
			// url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=2023-08-11&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
			projection: 'EPSG:3857'
		}),
		zIndex:5
	})
  // 无人航拍切换
	const lercLayer = colorLercHandle(stop, url, 14, 0, 'classification', 280, -1, data.projection, 'EPSG:4490')
  map.value = new Map({
    view: new View({
			center: [120.5753880739212, 32.499661445617676],
			zoom: 7,
			minZoom: 2,
			maxZoom: 18,
			projection: 'EPSG:4490',
		}),
		layers: [bingVec, bing, bing3, bing2, basePbfLayer, lercLayer],
		target: mapGrowthContainer.value,
	})
  map.value.getView().fit([120.55738250199978, 32.49469701308624, 120.58182389258319, 32.50264718815811], {
		padding: [0, 0, 0, 0] // 可选：指定视图边缘的填充，以腾出空间显示要素
	})
  map.value.on('click', (event) => {
    let featureObj = map.value.forEachFeatureAtPixel(event.pixel, function (feature, layer) {
      if(feature) {
        return feature
      }
      return null
    })
    data.featureObj = featureObj
    const layers = map.value.getLayers()
    const type = featureObj && featureObj.getGeometry().getType()
    if(featureObj && featureObj.values_) {
      const polygonVector = map.value.getLayers().array_[4]
      polygonVector.setStyle(function (featureVal) {
				return new Style({
					fill: new Fill({
						color: `${featureVal.get('id') === featureObj.values_.id ? 'rgba(77,81,72,0.25)' : 'rgba(10,28,69,0.25)'}`
					}),
					stroke: new Stroke({
						color: `${featureVal.get('id') === featureObj.values_.id ? '#f3f28b' : '#47ffff'}`,
						width: 1
					}),
					text: new Text({
						fill: new Fill({
							color: '#ffffff'
						}), //
						text: `${featureVal.get('short_name') === undefined ? '-' : featureVal.get('short_name')}`,
						font: 'bold 58 微软雅黑'
					})
				})
			})
			data.growthTrendObj.landId = featureObj.values_.id
      growthCurrentHandle()
			growthAreaHandle()
    }else {
      const polygonVector = map.value.getLayers().array_[4]
      polygonVector.setStyle(function (featureVal) {
				return new Style({
					fill: new Fill({
						color: `rgba(10,28,69,0.25)`
					}),
					stroke: new Stroke({
						color: `#47ffff`,
						width: 1
					}),
					text: new Text({
						fill: new Fill({
							color: '#ffffff'
						}), //
						text: `${featureVal.get('short_name') === undefined ? '-' : featureVal.get('short_name')}`,
						font: 'bold 58 微软雅黑'
					})
				})
			})
			data.growthTrendObj.landId = null
      growthCurrentHandle()
			growthAreaHandle()
    }
  })
  getCenterTokenHandle()
  getGeojsonListHandle()
}
/** 获取业务中台token */
const getCenterTokenHandle = () => {
	myFarm.getCenterToken().then((res) => {
		if(res.code === 200) {
      // const TileLayer = map.value.getLayers().array_[1]
      // TileLayer.setSource(
      //   new tileXyz({
      //     url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230725/{z}/{x}/{y}/png?token=${res.data.accessToken}`,
      //     projection: 'EPSG:4326'
      //   })
      // )
      data.accessToken = res.data.accessToken
      getTimeLineHandle('ndvi_drone')
			getColorMatching('ndvi_drone')
    }
	})
}
/** 获取geojson地块 */
const getGeojsonListHandle = () => {
	myFarm.getLandList({flag: 1}).then((res) => {
		if(res.code === 200) {
      pointAndPloygonHandle(res.data, 'port')
    }
	})
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
		const polygonVector = map.value.getLayers().array_[4]
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
		polygonVector.setSource(
			new VectorSource({
				features: new GeoJSON().readFeatures(polygonArr, {
					dataProjection: 'EPSG:4326',
					featureProjection: 'EPSG:4490'
				})
			})
		)
    if (type === 'port') {
      const layersArray = map.value.getLayers()
			const vectorlayers = layersArray.array_[4]
			const showlayers = layersArray.array_[5]
			cropping(showlayers,vectorlayers)
		}
	}
}
/** 获取配色 */
const getColorMatching = (type) => {
	axios
		.get(`https://opapi.gagogroup.cn/api/v1/publisher/types`, { headers: { token: data.accessToken } })
		.then((res) => {
			if (res.data) {
				console.log(res, 'resndvi_field')
			}
		})
}
/** 获取时间轴数据 */
const getTimeLineHandle = (type) => {
	data.cloudList = []
	axios
		.get(`https://opapi.gagogroup.cn/api/v2/publisher/times/${type}`, { headers: { token: data.accessToken } })
		.then((res) => {
			if (res.data && res.data.code === 200) {
				if (type === 'ndvi_field') {
					data.timeList = [...res.data.data].slice(15)
				} else {
					data.timeList = [...res.data.data]
          for(let i = 0; i < data.timeList.length; i++) {
            data.timeList[i] = {
              date: data.timeList[i],
              cloudCover: 0,
              icon: icon3,
            } 
          }
          data.activeTime = data.timeList.length - 1
          timeChangeHandle(data.timeList[data.timeList.length - 1])
				}
			}
		})
}
/** 获取时间轴数据 */
const getTimeLineHandle2 = (type) => {
	let token = localStorage.getItem('token')
	axios
		.get(`${import.meta.env.VITE_APP_BASE_API}/growth/growthNrtDateLine`, { headers: { token } })
		.then((res) => {
			if (res.data) {
				if (type === 'ndvi_field') {
					let timeListNew=[]
					let cloudListNew=[]
					for(let item of res.data.data){
						timeListNew.push(item.date.substring(0,10).replaceAll("-", ""))
						cloudListNew.push({
							date:item.date.substring(0,10).replaceAll("-", ""),
							cloudCover:item.cloudCover
						})
					}
					data.timeList = [...timeListNew].slice(15)
					data.cloudList = [...cloudListNew].slice(15)
				} else {
					let timeListNew=[]
					let cloudListNew=[]
					for(let item of res.data.data){
						timeListNew.push(item.date.substring(0,10).replaceAll("-", ""))
						cloudListNew.push({
							date:item.date.substring(0,10).replaceAll("-", ""),
							cloudCover:item.cloudCover
						})
					}
					data.timeList = [...timeListNew]
					data.cloudList = [...cloudListNew]
          for(let i = 0; i < data.timeList.length; i++) {
            let nowDate = data.cloudList.filter(item => {
              return item.date === data.timeList[i]
            })
            if(nowDate.length > 0) {
              if(nowDate[0].cloudCover === 0) {
                data.timeList[i] = {
                  date: data.timeList[i],
                  cloudCover: nowDate[0].cloudCover,
                  icon: icon3,
                }
              }else if(nowDate[0].cloudCover === 100) {
                data.timeList[i] = {
                  date: data.timeList[i],
                  cloudCover: nowDate[0].cloudCover,
                  icon: icon1,
                }
              }else {
                data.timeList[i] = {
                  date: data.timeList[i],
                  cloudCover: nowDate[0].cloudCover,
                  icon: icon2,
                }
              }
            }else {
              data.timeList[i] = {
                date: data.timeList[i],
                cloudCover: 0,
                icon: icon3,
              }
            }
          }
          data.activeTime = data.timeList.length - 1
          timeChangeHandle(data.timeList[data.timeList.length - 1])
				}
			}
		})
}
/** 时间轴change */
const timeChangeHandle = (val, isRun) => {
	data.actual = val.date
	const showlayers = map.value.getLayers().array_[5]
	const TileLayer = map.value.getLayers().array_[3]
	const growthLayer = map.value.getLayers().array_[2]
	const stop = [
		[1, '#e05a00'],
		[128, '#edad57'],
		[160, '#faffad'],
		[191, '#8ebf6a'],
		[223, '#00802b'],
		[256, '#00802b']
	]
	const url = `https://opapi.gagogroup.cn/api/v2/publisher/data/${data.lercType}/${data.actual}/{z}/{x}/{y}/lerc?token=${data.accessToken}`
	const lercSource = editLercHandle(
		stop,
		url,
		14,
		0,
		'classification',
		data.lercType === 'ndvi_field' ? 256 : 280,
		1,
		data.projection,
		'EPSG:4490',
		map.value.getView().getZoom()
	)
	if (data.formlayers.resource === '1') {
		growthLayer.setVisible(false)
		showlayers.setSource(lercSource)
		TileLayer.setSource(
			new tileXyz({
				//https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
				url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
				projection: 'EPSG:4490'
			})
		)
	}
	if (data.formlayers.resource === '2') {
		showlayers.setVisible(false)
		growthLayer.setSource(new tileXyz({
      url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=${data.actual.slice(0, 4)}-${data.actual.slice(4, 6)}-${data.actual.slice(6, 8)}&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
      projection: 'EPSG:3857'
    }))
		// TileLayer.setSource(
		// 	new tileXyz({
		// 		//https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
		// 		url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
		// 		projection: 'EPSG:4490'
		// 	})
		// )
	}
  if(isRun) {
    layerTypeHandle()
  }
	data.growthTrendObj.date = `${val.date.slice(0, 4)}-${val.date.slice(4, 6)}-${val.date.slice(6, 8)}`
	growthCurrentHandle()
	growthAreaHandle()
}
/** 裁剪 */
const cropping = (tileLayer,vectorLayer) => {
	const layersArray = map.value.getLayers()
	tileLayer.on('prerender', function (e) {
		const polygons = []
		const vectorContext = getVectorContext(e)
		const style = new Style({
			fill: new Fill({
				color: 'rgba(0,0,0,0)'
			})
		})
		if (e && e.context) {
			vectorLayer.getSource().forEachFeature(function (feature) {
				const geometry = feature.getGeometry()
				const type = geometry.getType()
				if (type === 'Polygon') {
					polygons.push(geometry.getCoordinates())
				} else if (type === 'MultiPolygon') {
					Array.prototype.push.apply(polygons, geometry.getCoordinates())
				}
			})
			e.context.globalCompositeOperation = 'source-over'
			let ctx = e.context
			ctx.save()
			vectorContext.setStyle(style)
			vectorContext.drawGeometry(new MultiPolygon(polygons)) // 可以对边界设置一个样式
			ctx.clip()
			tileLayer.setExtent(new MultiPolygon(polygons).getExtent())
		}
	})
	tileLayer.on('postrender', (event) => {
		let ctx = event.context
		ctx.restore()
	})
}
/** 展示地图类型 */
const showMapType = () => {
  data.mapHigh = true
  let mapTypeListBox = document.getElementById('mapTypeListBox')
  mapTypeListBox.style.right = '64px'
  mapTypeListBox.style.transition = 'all 0.5s'
}
/** 关闭地图类型 */
const closeMapType = () => {
  data.mapHigh = false
  let mapTypeListBox = document.getElementById('mapTypeListBox')
  mapTypeListBox.style.right = '-150px'
  mapTypeListBox.style.transition = 'all 0.5s'
}
/** 关闭所有 */
const closeAll = () => {
  data.mapHigh = false
  let mapTypeListBox = document.getElementById('mapTypeListBox')
  mapTypeListBox.style.right = '-150px'
  mapTypeListBox.style.transition = 'all 0.5s'
}
/** 数据源修改触发 */
const resourceHandle = (value) => {
  data.formlayers.layerType = '1'
  const growthLayer = map.value.getLayers().array_[2]
	const showlayers = map.value.getLayers().array_[5]
	const TileLayer = map.value.getLayers().array_[3]
	const vectorlayers = map.value.getLayers().array_[4]
	if (data.formlayers.resource === '1') {
		getTimeLineHandle('ndvi_drone')
		data.growthTrendObj.type = 1
		const stop = [
			[1, '#e05a00'],
			[128, '#edad57'],
			[160, '#faffad'],
			[191, '#8ebf6a'],
			[223, '#00802b'],
			[256, '#00802b']
		]
		const url = `https://opapi.gagogroup.cn/api/v2/publisher/data/${data.lercType}/${data.actual}/{z}/{x}/{y}/lerc?token=${data.accessToken}`
		// const url = `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=2023-08-06&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`
		const lercSource = editLercHandle(
			stop,
			url,
			14,
			0,
			'classification',
			data.lercType === 'ndvi_field' ? 256 : 280,
			1,
			data.projection,
			'EPSG:4490',
			map.value.getView().getZoom()
		)
		growthLayer.setVisible(false)
		showlayers.setVisible(true)
		showlayers.setSource(lercSource)
		TileLayer.setSource(
			new tileXyz({
				//https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
				url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
				projection: 'EPSG:4490'
			})
		)
		cropping(showlayers,vectorlayers)
	}else if (data.formlayers.resource === '2') {
		getTimeLineHandle2('ndvi_drone') 
		data.growthTrendObj.type = 2
		growthLayer.setVisible(true)
		showlayers.setVisible(false)
		growthLayer.setSource(new tileXyz({
      url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=${data.actual.slice(0, 4)}-${data.actual.slice(4, 6)}-${data.actual.slice(6, 8)}&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
      // url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=2023-08-01&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
      projection: 'EPSG:3857'
    }))
		TileLayer.setSource(
			new tileXyz({
				//https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
				url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
				projection: 'EPSG:4490'
			})
		)
		cropping(growthLayer,vectorlayers)
	}
}
/** 点击时间 */
const timechange = (value, index) => {
  data.activeTime = index
  timeChangeHandle(value, true)
}
/** 本年度长势趋势 */
const growthCurrentHandle = () => {
	let Echarts = echarts.init(growthLine.value)
	myFarm.getGrowthTrend(data.growthTrendObj).then((res) => {
		if(res.code === 200) {
      const dataX = res.data.map((item) => item.date.slice(5, 10))
      const dataY = res.data.map((item) => {
        if(item.meanValue >= 0) {
          return item.meanValue
        }else{
          return 0
        }
      })
      const option = {
        color: ['#4bad71'],
        grid: {
          top: 20,
          bottom: 10,
          left: 5,
          right: 10,
          containLabel: true
        },
        itemStyle: {
          normal: {
            barBorderRadius: 3
          }
        },
        tooltip: {
          formatter: function (params) {
            let res = params[0].name + '<br/>长势等级为：'
            let texts = ''
            if (params[0].value >= 0 && params[0].value <= 0.2) {
              texts = '极差'
            } else if (params[0].value > 0.2 && params[0].value <= 0.4) {
              texts = '较弱'
            } else if (params[0].value > 0.4 && params[0].value <= 0.6) {
              texts = '中等'
            } else if (params[0].value > 0.6 && params[0].value <= 0.8) {
              texts = '较旺'
            } else if (params[0].value > 0.8 && params[0].value <= 1.0) {
              texts = '很旺'
            }
            res = res + texts
            return res
          },
          trigger: 'axis',
          axisPointer: {
            type: 'line',
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.37)'
            }
          }
        },
        xAxis: {
          type: 'category',
          offset: 2,
          nameGap: 50,
          splitLine: { show: false },
          axisTick: { show: false },
          axisLabel: {
            interval: 0, // 强制所有标签都显示
            rotate: 45, // 可以结合倾斜角度进行调整
            color: '#5E817A'
          },
          data: dataX
        },
        yAxis: {
          type: 'value',
          show: true,
          axisLine: { show: false },
          interval: 0.2, // 设置刻度间隔为10
          axisLabel: {
            textStyle: {
              fontSize: 12,
              color: 'grey'
            },
            // 这里重新定义y轴
            formatter: function (value) {
              var texts = []
              if (value === 0) {
                texts.push('')
              } else if (value >= 0 && value <= 0.2) {
                texts.push('极差')
              } else if (value >= 0.2 && value <= 0.4) {
                texts.push('较弱')
              } else if (value >= 0.4 && value <= 0.6) {
                texts.push('中等')
              } else if (value >= 0.6 && value <= 0.8) {
                texts.push('较旺')
              } else if (value >= 0.8 && value <= 1.0) {
                texts.push('很旺')
              } else {
                texts.push('')
              }
              return texts
            }
          },
          axisTick: { show: false },
          splitLine: {
            show: false,
            lineStyle: {
              type: 'dotted',
              color: '#1cb4eb'
            }
          }
        },
        series: [
          {
            type: 'line',
            showSymbol: false,
            smooth: true,
            name: '值',
            encode: {
              x: 0,
              y: 1
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(
                0,
                0,
                0,
                1,
                [
                  {
                    offset: 0,
                    color: 'rgba(4,183,111,0.8)' // 渐变起点颜色和透明度
                  },
                  {
                    offset: 1,
                    color: 'rgba(4,183,111,0)' // 渐变终点颜色和透明度
                  }
                ],
                false
              )
            },
            data: dataY
          }
        ]
      }
      document.getElementById('growthLine').removeAttribute('_echarts_instance_')  // 保证echarts每次重新加载
      // 绘制图表
      Echarts.setOption(option)
    }
	})
}
/** 当前面积趋势 */
const growthAreaHandle = () => {
	let Echarts = echarts.init(eachGrowthArea.value)
	const nParams = { ...data.growthTrendObj }
  console.log('nParams', nParams)
	myFarm.getGrowthArea(nParams).then((res) => {
		if(res.code === 200) {
      let levelObj = res.data && res.data.length > 0 ? res.data[0] : {}
      const option = {
        title: {
          subtext: '面积（亩）'
        },
        grid: {
          right: 10,
          bottom: 30
        },
        color: ['#1E90FF'],
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['极差', '较弱', '中等', '较旺', '很旺'],
          axisLabel: {
            color: '#00895b'
          },
          axisLine: {
            lineStyle: {
              color: '#00895b'
            }
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [levelObj.level1, levelObj.level2, levelObj.level3, levelObj.level4, levelObj.level5],
            type: 'bar',
            itemStyle: {
              //	const stop = [[1, '#e05a00'], [128, '#edad57'], [160, '#faffad'], [191, '#8ebf6a'],[223, '#00802b'],[256, '#00802b']]
              normal: {
                color: function (params) {
                  const colorList = ['#e05a00', '#edad57', '#faffad', '#8ebf6a', '#00802b', '#00802b']
                  // return colorList[params.dataIndex]
                  return new echarts.graphic.LinearGradient(0, 1, 0, 0, [
                    {
                      offset: 0,
                      color: colorList[params.dataIndex]
                    },
                    {
                      offset: 1,
                      color: colorList[params.dataIndex + 1]
                    }
                  ])
                }
              }
            },
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.2)'
            },
            barWidth: '20%'
          }
        ]
      }
      document.getElementById('eachGrowthArea').removeAttribute('_echarts_instance_')  // 保证echarts每次重新加载
      // 绘制图表
      Echarts.setOption(option)
    }
	})
}
/** 图例点击 */
const legendHandle = (node) => {
	node.state = !node.state
	let arr = []
	data.legendList.forEach((its) => {
		if (its.state) {
			arr = [...arr, ...its.colorArr]
		}
	})
	const nArr = arrRepeatHandle(arr).sort((a, b) => a[0] - b[0])
	const showlayers = map.value.getLayers().array_[5]
	const stop = nArr
	const url = `https://opapi.gagogroup.cn/api/v2/publisher/data/${data.lercType}/${data.actual}/{z}/{x}/{y}/lerc?token=${data.accessToken}`
	const lercSource = editLercHandle(
		stop,
		url,
		14,
		0,
		'classification',
		data.lercType === 'ndvi_field' ? 256 : 280,
		1,
		data.projection,
		'EPSG:4490',
		map.value.getView().getZoom()
	)
	showlayers.setSource(lercSource)
}
/** 数组去重 */
const arrRepeatHandle = (arr) => {
	const obj = {}
	const nArr = []
	arr.forEach((its) => {
		if (!obj[its[0]]) {
			obj[its[0]] = 1
			nArr.push(its)
		}
	})
	return nArr
}
/** 图层切换 */
const layerTypeHandle = (value) => {
  const growthLayer = map.value.getLayers().array_[2]
	const showlayers = map.value.getLayers().array_[5]
	const TileLayer = map.value.getLayers().array_[3]
	const vectorlayers = map.value.getLayers().array_[4]
  if(data.formlayers.layerType === '2') {
    if(data.formlayers.resource === '1') {
      showlayers.setVisible(false)
      TileLayer.setSource(
        new tileXyz({
          //https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
          url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
          projection: 'EPSG:4490'
        })
      )
      cropping(showlayers,vectorlayers)
    }else if(data.formlayers.resource === '2') {
      growthLayer.setVisible(true)
      showlayers.setVisible(false)
      growthLayer.setSource(new tileXyz({
        url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=${data.actual.slice(0, 4)}-${data.actual.slice(4, 6)}-${data.actual.slice(6, 8)}&zoom={z}&col={x}&row={y}&type=tci&tms=mercator&token=${data.accessToken}`,
        // url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=2023-08-01&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
        projection: 'EPSG:3857'
      }))
      TileLayer.setSource(
        new tileXyz({
          //https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
          url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
          projection: 'EPSG:4490'
        })
      )
      cropping(growthLayer,vectorlayers)
    }
  }else if(data.formlayers.layerType === '1') {
    if(data.formlayers.resource === '1') {
      const stop = [
        [1, '#e05a00'],
        [128, '#edad57'],
        [160, '#faffad'],
        [191, '#8ebf6a'],
        [223, '#00802b'],
        [256, '#00802b']
      ]
      const url = `https://opapi.gagogroup.cn/api/v2/publisher/data/${data.lercType}/${data.actual}/{z}/{x}/{y}/lerc?token=${data.accessToken}`
      // const url = `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=2023-08-06&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`
      const lercSource = editLercHandle(
        stop,
        url,
        14,
        0,
        'classification',
        data.lercType === 'ndvi_field' ? 256 : 280,
        1,
        data.projection,
        'EPSG:4490',
        map.value.getView().getZoom()
      )
      growthLayer.setVisible(false)
      showlayers.setVisible(true)
      showlayers.setSource(lercSource)
      TileLayer.setSource(
        new tileXyz({
          //https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
          url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
          projection: 'EPSG:4490'
        })
      )
      cropping(showlayers,vectorlayers)
    }else if(data.formlayers.resource === '2') {
      growthLayer.setVisible(true)
      showlayers.setVisible(false)
      growthLayer.setSource(new tileXyz({
        url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=${data.actual.slice(0, 4)}-${data.actual.slice(4, 6)}-${data.actual.slice(6, 8)}&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
        // url: `https://opapi.gagogroup.cn/api/v1/real/land/tile?landId=nrt&date=2023-08-01&zoom={z}&col={x}&row={y}&type=ndvi&tms=mercator&token=${data.accessToken}`,
        projection: 'EPSG:3857'
      }))
      TileLayer.setSource(
        new tileXyz({
          //https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/20230711/{z}/{x}/{y}/png?token=69c5397506b57238fc82ce3132fad95d
          url: `https://opapi.gagogroup.cn/api/v2/publisher/data/image_drone/${data.actual}/{z}/{x}/{y}/png?token=${data.accessToken}`,
          projection: 'EPSG:4490'
        })
      )
      cropping(growthLayer,vectorlayers)
    }
  }
}

/** 监听时间轴变化 */
watch(
  () => data.timeList,
  () => {
    setTimeout(() => {
      const timeListBox = document.getElementById('timeListBox')
      timeListBox.style['scroll-behavior'] = 'auto'
      timeListBox.scrollLeft = 0
      setTimeout(() => {
        timeListBox.style['scroll-behavior'] = 'smooth'
        timeListBox.scrollLeft = timeListBox.scrollWidth - timeListBox.clientWidth
      }, 1000)
    }, 0)
  },
  {
    deep: true,
    immediate: true,
  }
)
</script>

<style lang="less" scoped>
#landGrowth {
  width: 100vw;
  height: 100vh;
  height: calc(100vh - 50px);
  position: relative;
  .mapGrowth {
    width: 100vw;
    height: 100%;
    position: relative;
    .mapTypeBox {
      width: 44px;
      height: 44px;
      border-radius: 8px;
      background-color: #fff;
      position: absolute;
      top: 80px;
      right: 10px;
      z-index: 100;
      padding: 10px;
      img {
        width: 24px;
        height: 24px;
      }
    }
    .mapTypeListBox {
      position: absolute;
      top: 80px;
      right: -150px;
      width: 150px;
      height: 160px;
      background-color: #fff;
      border-radius: 8px;
      z-index: 99;
      .typeListTitle {
        width: 100%;
        height: 30px;
        background-color: #f5f6f7;
        border-radius: 8px 8px 0 0;
        font-size: 14px;
        line-height: 30px;
        padding-left: 10px;
      }
      .typeContent {
        width: 100%;
        height: 130px;
        padding: 10px;
        .van-radio {
          margin-bottom: 5px;
          font-size: 14px;
          --van-radio-size: 18px;
        }
        .van-radio:last-child {
          margin-bottom: 0;
        }
        .line {
          width: 100%;
          height: 1px;
          border-top: 1px dashed #ddd;
          margin: 10px 0;
        }
      }
    }
    .bottomContentBox {
      width: 100%;
      height: 240px;
      background-color: #f5f6f7;
      position: absolute;
      bottom: 0;
      left: 0;
      z-index: 100;
      border-radius: 15px 15px 0 0;
      .tabBox {
        width: 100%;
        height: 40px;
        color: #2d4f47;
        font-weight: bold;
        display: flex;
        align-items: center;
        justify-content: center;
        .tabItem, .selectedTabItem {
          width: calc(100% / 3);
          height: 40px;
          line-height: 40px;
          text-align: center;
          position: relative;
          .lefttab {
            position: absolute;
            top: 0;
            left: 0;
            height: 50px;
            z-index: -1;
          }
          .centertab {
            position: absolute;
            top: 0;
            left: 50%;
            transform: translate(-50%, 0);
            height: 50px;
            z-index: -1;
          }
          .righttab {
            position: absolute;
            top: 0;
            right: 0;
            height: 50px;
            z-index: -1;
          }
        }
        .selectedTabItem {
          color: #298256;
        }
      }
      .contentBox {
        width: 100%;
        height: calc(100% - 40px);
        background-color: #fff;
        border-radius: 15px 15px 0 0;
        padding: 10px;
        .timeListBox {
          width: 100%;
          height: 100%;
          padding: 15px 10px;
          overflow-x: auto;
          overflow-y: hidden;
          white-space: nowrap;
          .timeItem, .selectedTimeItem {
            width: 120px;
            height: 100%;
            border-radius: 10px;
            background-color: #f5f6f7;
            display: inline-block;
            margin-right: 20px;
            border: 1px solid #fff;
            .yearBox {
              width: 100%;
              height: 30px;
              line-height: 40px;
              text-align: center;
              font-size: 15px;
            }
            .dayBox {
              width: 100%;
              height: 30px;
              line-height: 30px;
              text-align: center;
              font-weight: bold;
              font-size: 18px;
            }
            .iconBox {
              width: 50px;
              height: 50px;
              margin: 0 35px;
            }
            .cloudBox {
              width: 100%;
              height: 30px;
              text-align: center;
              font-size: 15px;
            }
          }
          .timeItem:last-child, .selectedTimeItem:last-child {
            margin-right: 0;
          }
          .selectedTimeItem {
            border: 1px solid #298256;
            background-color: #eff8f5;
            .yearBox, .dayBox {
              color: #298256;
            }
          }
        }
        .timeListBox::-webkit-scrollbar {
          display: none;
        }
        .growthLine {
          width: calc(100vw - 20px);
          height: 180px;
        }
        .eachGrowthArea {
          width: calc(100vw - 20px);
          height: 180px;
        }
      }
    }
    .leftLegendBox {
			width: 60px;
			padding: 4px;
			background-color:  rgba(255, 255, 255, 0.7);;
			border-radius: 8px;
      position: absolute;
      left: 10px;
      bottom: 250px;
      z-index: 100;
			.showLegendList {
				width: 100%;
				margin-top: 5px;
				.showLegendItem {
					width: 100%;
					display: flex;
					align-items: center;
					justify-content: center;
					cursor: pointer;
					margin-bottom: 5px;
					i {
						display: inline-block;
						width: 4px;
						height: 20px;
					}
					span {
						font-size: 12px;
						font-family: PingFangSC-Regular, PingFang SC;
						font-weight: 400;
						color: #475669;
						margin-left: 10px;
					}
				}
			}
		}
  }
}
</style>
<style lang="less">
.ol-zoom {
  display: none;
}
.van-radio__label {
  padding-top: 2px !important;
}
</style>
