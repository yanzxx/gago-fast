<template>
  <div id="landManagement">
    <!-- 地图容器 -->
    <div id="map" class="map" ref="mapContainer" @click="closeAll">
      <!-- 右侧工具栏 -->
      <div class="rightToolBox" @click.stop>
        <div class="searchBox">
          <img src="./img/search.png" @click="gotoSearchPage" />
        </div>
        <div class="mapTypeBox">
          <img src="./img/map.png" @click="showMapType" v-if="!data.mapHigh" />
          <img src="./img/mapHigh.png" @click="closeMapType" v-else />
        </div>
        <div class="paintedLandBox">
          <img src="./img/paintedLand.png" @click="data.drawLandTypePicker = true" />
        </div>
      </div>
      <!-- 地图类型 -->
      <div class="mapTypeListBox" id="mapTypeListBox" @click.stop>
        <div class="typeListTitle">地图类型</div>
        <div class="typeContent">
          <div class="typeItem">
            <div class="itemImg">
              <img src="./img/tiandiImg.png" />
              <img src="./img/selectedMap.png" class="selectedImg" />
            </div>
            <div class="itemName">天地图</div>
          </div>
        </div>
      </div>
      <!-- 物联设备是否展示控制按钮 -->
<!--      <div class="facilityShowBox" @click.stop>-->
<!--        <img src="./img/facilityShow.png" />-->
<!--        <van-switch v-model="data.facilityShow" active-color="#008F5F" inactive-color="#DAE2E0" size="12px" @change="facilityShowChangeHandle" />-->
<!--      </div>-->
      <!-- 底部定位按钮 -->
      <div class="bottomLocationBox" @click.stop>
        <img src="./img/location.png" />
      </div>
      <!-- 地块列表是否展示控制按钮 -->
      <div class="landListShowBox" @click.stop="data.showLandList = true">
        <img src="./img/listMenu.png" />
      </div>
    </div>
    <!-- 地块列表 -->
    <van-popup
      v-model:show="data.showLandList"
      round
      :overlay="false"
      position="bottom"
      class="landListPopup"
      :style="{ height: '45%', marginBottom: '50px', backgroundColor: '#F5F6F7', zIndex: 110 }"
    >
      <div class="popupContentBox">
        <div class="popupHead" @touchstart="popTouchStart($event)" @touchmove="popTouchMove($event)" @touchend="popTouchEnd($event)">
          <img src="./img/popupHead.png" />
        </div>
        <div class="popupBody">
          <van-pull-refresh v-model="data.refreshLoading" @refresh="onRefresh">
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
        </div>
      </div>
    </van-popup>
    <!-- 土壤墒情液位监测仪 -->
    <van-popup
      v-model:show="data.sensorShow"
      round
      :overlay="false"
      position="bottom"
      class="landListPopup"
      id="facilityPopup"
      :style="{ height: data.isAllShow ? 'calc(100% - 126px)' : '45%', marginBottom: '50px', backgroundColor: '#F5F6F7', zIndex: 110 }"
    >
      <div class="popupContentBox">
        <div class="popupHead" @touchstart="popTouchStart2($event)" @touchmove="popTouchMove2($event)" @touchend="popTouchEnd2($event)">
          <img src="./img/popupHead.png" />
        </div>
        <div class="sensorBody">
          <div class="facilityNameBox">土壤墒情液位监测仪</div>
          <div class="facilityInfoBox">
            <van-collapse v-model="data.activeNames" @change="collapseChangeHandle">
              <van-collapse-item :name="index" v-for="(item, index) in data.sensorInfo" :key="index">
                <template #title>
                  <div class="contentItemTitle">
                    <img :src="item.icon" />
                    <div>{{item.collectName}}</div>
                  </div>
                </template>
                <template #value>
                  <div style="color: #000;">{{item.collectData}} {{item.collectUnit}}</div>
                </template>
                <div class="contentItem">
                  <div class="echartsBox" :id='"echartsBox" + index' />
                </div>
              </van-collapse-item>
            </van-collapse>
          </div>
        </div>
      </div>
    </van-popup>
    <!-- 虫情监测仪 -->
    <van-popup
      v-model:show="data.detectorShow"
      round
      :overlay="false"
      position="bottom"
      class="landListPopup"
      :style="{ height: '45%', marginBottom: '50px', backgroundColor: '#F5F6F7', zIndex: 110 }"
    >
      <div class="popupContentBox">
        <div class="popupHead" @touchstart="popTouchStart($event)" @touchmove="popTouchMove($event)" @touchend="popTouchEnd($event)">
          <img src="./img/popupHead.png" />
        </div>
        <div class="detectorBody">
          <div class="facilityNameBox">虫情监测仪</div>
          <div class="facilityInfoBox">
            <div class="contentBox">
              <div class="contentTopBox">
                <div class="contentLeftBox">
                  <img :src="'http://data.pengbo-tech.com' + data.detectorInfo.storePath" v-if="data.detectorInfo.storePath" />
                </div>
                <div class="contentRightBox">
                  <div class="numItem">
                    <div class="itemTitle">识别数量</div>
                    <div class="itemContent">
                      <img src="./img/numBg.png" />
                      <span>{{data.detectorInfo.totalNum ? data.detectorInfo.totalNum : 0}}</span>
                    </div>
                  </div>
                  <div class="typeItem">
                    <div class="itemTitle">识别种类</div>
                    <div class="itemContent">
                      <img src="./img/typeBg.png" />
                      <span>{{data.detectorInfo.totalType ? data.detectorInfo.totalType : 0}}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="contentBottomBox">
                <div class="wormList" v-if="data.detectorInfo.wormList && data.detectorInfo.wormList.length > 0" v-for="(item, index) in data.detectorInfo.newWormList" :key="index">
                  <div class="wormItem" v-for="(its, indx) in item" :key="indx">
                    <div class="wormItemLeft">
                      <div class="wormIcon">
                        <img src="./img/wormicon.png" />
                      </div>
                      <div class="wormName">{{its.name}}：</div>
                    </div>
                    <div class="wormItemRight"><span>{{its.qty ? its.qty : 0}}</span>只</div>
                  </div>
                </div>
                <div v-else style="font-size: 14px;">暂无数据</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </van-popup>
    <!-- 视频监控 -->
    <van-popup
      v-model:show="data.cameraShow"
      round
      :overlay="false"
      position="bottom"
      class="landListPopup"
      :style="{ height: '35%', marginBottom: '50px', backgroundColor: '#F5F6F7', zIndex: 110 }"
       @close="cameraClose"
    >
      <div class="popupContentBox">
        <div class="popupHead" @touchstart="popTouchStart($event)" @touchmove="popTouchMove($event)" @touchend="popTouchEnd($event)">
          <img src="./img/popupHead.png" />
        </div>
        <div class="cameraBody">
          <div class="facilityNameBox">视频监控</div>
          <div class="facilityInfoBox">
            <video ref="videoPlayer" controls class="video"></video>
          </div>
        </div>
      </div>
    </van-popup>
    <!-- 气象站 -->
    <van-popup
      v-model:show="data.stationShow"
      round
      :overlay="false"
      position="bottom"
      class="landListPopup"
      id="facilityPopup2"
      :style="{ height: data.isAllShow ? 'calc(100% - 126px)' : '45%', marginBottom: '50px', backgroundColor: '#F5F6F7', zIndex: 110 }"
    >
      <div class="popupContentBox">
        <div class="popupHead" @touchstart="popTouchStart2($event)" @touchmove="popTouchMove2($event)" @touchend="popTouchEnd2($event)">
          <img src="./img/popupHead.png" />
        </div>
        <div class="stationBody">
          <div class="facilityNameBox">气象站</div>
          <div class="facilityInfoBox">
            <van-collapse v-model="data.activeNames2" @change="collapseChangeHandle2">
              <van-collapse-item :name="index" v-for="(item, index) in data.stationInfo" :key="index">
                <template #title>
                  <div class="contentItemTitle">
                    <img :src="item.icon" />
                    <div>{{item.collectName}}</div>
                  </div>
                </template>
                <template #value>
                  <div style="color: #000;">{{item.collectData}} {{item.collectUnit}}</div>
                </template>
                <div class="contentItem">
                  <div class="echartsBox" :id='"stationEchartsBox" + index' />
                </div>
              </van-collapse-item>
            </van-collapse>
          </div>
        </div>
      </div>
    </van-popup>
    <!-- 展示画地块类型 -->
    <van-action-sheet
      v-model:show="data.drawLandTypePicker"
      :actions="data.actions"
      cancel-text="取消"
      close-on-click-action
      @cancel="onCancelDrawLand"
      @select="drawLandChangeHandle"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import GeoJSON from 'ol/format/GeoJSON'
import Map from 'ol/Map'
import VectorLayer from 'ol/layer/Vector'
import VectorSource from 'ol/source/Vector'
import View from 'ol/View'
import "ol/ol.css"
import { Fill, Stroke, Style, Icon, Text } from 'ol/style'
import { getTopLeft, getWidth } from 'ol/extent'
import { WMTS as WMTSource, XYZ as tileXyz } from 'ol/source'
import * as olProj from 'ol/proj'
import TileLayer from 'ol/layer/Tile'
import WMTSTileGrid from 'ol/tilegrid/WMTS'
import { getLength } from 'ol/sphere'
import VectorTileSource from 'ol/source/VectorTile'
import MVT from 'ol/format/MVT'
import VectorTileLayer from 'ol/layer/VectorTile'
import Feature from 'ol/Feature'
import Point from 'ol/geom/Point'
import * as echarts from 'echarts'
import myFarm from "@/api/region/myFarm/index.js"
import Hls from 'hls.js'
import mapIcon1 from './img/mapIcon/mapIcon1.png'
import mapIcon2 from './img/mapIcon/mapIcon2.png'
import mapIcon3 from './img/mapIcon/mapIcon3.png'
import mapIcon4 from './img/mapIcon/mapIcon4.png'
import mapIcon5 from './img/mapIcon/mapIcon5.png'
import mapIcon6 from './img/mapIcon/mapIcon6.png'
import mapIcon7 from './img/mapIcon/mapIcon7.png'
import tw from './img/tw.png'
import ts from './img/ts.png'
import kw from './img/kw.png'
import ks from './img/ks.png'
import fx from './img/fx.png'
import fs from './img/fs.png'
import gz from './img/gz.png'
import yl from './img/yl.png'
import qy from './img/qy.png'
import ec from './img/ec.png'
import yw from './img/yw.png'

const router = useRouter()

const map = ref(null)
const mapContainer = ref(null)
const videoPlayer = ref(null)
const data = reactive({
  token: localStorage.getItem('App-Token'),
  activeTab: 0,
  topTab: [
    {
      label: '地块',
      value: 0,
    },
    {
      label: '长势',
      value: 1,
    },
    {
      label: '作业数据',
      value: 2,
    },
  ],
  facilityShow: true,
  landList: [],
  showLandList: false,
  refreshLoading: false,
  loading: true,
  finished: false,
  featureObj: null,
  mapHigh: false,
  sensorShow: false,
  detectorShow: false,
  cameraShow: false,
  stationShow: false,
  sensorInfo: [],
  detectorInfo: {},
  cameraInfo: [],
  stationInfo: [],
  activeNames: [],
  activeNames2: [],
  BaseUrl:`${import.meta.env.VITE_APP_BASE_API}`,
  touch: {
    startX: null,
    startY: null,
  },
  drawLandTypePicker: false,
  actions: [
    { name: '画地块' },
    { name: '跑马圈地' },
  ],
  touch2: {
    startX: null,
    startY: null,
  },
  isAllShow: false,
  ms: 1000,
  lastDate: new Date() - 1000,
})

onMounted(() => {
  initMap()
  onLoad()
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
/** 物联设备点位图层 */
const pbfConsultPointLayer=(proj, name, opacity, zIndexVal, deviceType, farmCode)=> {
  const basePbSource = new VectorTileSource({
    url: `${import.meta.env.VITE_APP_BASE_API}/device/map/{z}/{x}/{y}/pbf?token=${data.token}&deviceType=${deviceType}&farmCode=${farmCode}`,
    format: new MVT(),
    projection: olProj.get(proj)
  })
  const pbfBaseLayer = new VectorTileLayer({
    declutter: true,
    wrapX: false,
    zIndex: zIndexVal,
    opacity: opacity,
    preload: Infinity,
    source: basePbSource,
    minZoom: 0,
    maxZoom: 19,
    style: function (feature) {
      let mapIcon = null
      if (feature.properties_.device_type === 1 || feature.properties_.device_type === '1') {
        mapIcon = mapIcon1
      } else if (feature.properties_.device_type === 2 || feature.properties_.device_type === '2') {
        mapIcon = mapIcon2
      } else if (feature.properties_.device_type === 3 || feature.properties_.device_type === '3') {
        mapIcon = mapIcon3
      } else if (feature.properties_.device_type === 4 || feature.properties_.device_type === '4') {
        mapIcon = mapIcon4
      } else if (feature.properties_.device_type === 5 || feature.properties_.device_type === '5') {
        mapIcon = mapIcon5
      } else if (feature.properties_.device_type === 6 || feature.properties_.device_type === '6') {
        mapIcon = mapIcon6
      } else if (feature.properties_.device_type === 7 || feature.properties_.device_type === '7') {
        mapIcon = mapIcon7
      }
      return new Style({
        fill: new Fill({
          color: 'rgba(71, 255, 255, 0.15)'
        }),
        stroke: new Stroke({
          color: `#47ffff`,
          width: 1
        }),
        image: new Icon({
          anchor: [0.5, 46],
          anchorXUnits: 'fraction',
          anchorYUnits: 'pixels',
          src: mapIcon,
          scale: 0.3
        })
      })
    },
    visible: true
  })
  pbfBaseLayer.set('basepbfMap', name)
  return pbfBaseLayer
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
  // 土壤墒情液位监测仪图层
  const sensorPbfLayer = pbfConsultPointLayer('EPSG:4326', 'sensorPbfLayer', 1, 1005, '1', '1')
  // 虫情监测仪
  const detectorPbfLayer = pbfConsultPointLayer('EPSG:4326', 'detectorPbfLayer', 1, 1005, '4', '1')
  // 视频监控
  const cameraPbfLayer = pbfConsultPointLayer('EPSG:4326', 'cameraPbfLayer', 1, 1005, '5', '1')
  // 气象站
  const meteorologicalPbfLayer = pbfConsultPointLayer('EPSG:4326', 'meteorologicalPbfLayer', 1, 1005, '7', '1')
  map.value = new Map({
    view: new View({
			center: [120.5753880739212, 32.499661445617676],
			zoom: 7,
			minZoom: 2,
			maxZoom: 18,
			projection: 'EPSG:4326',
		}),
		layers: [bing, bing2, basePbfLayer, sensorPbfLayer, detectorPbfLayer, cameraPbfLayer, meteorologicalPbfLayer],
		target: mapContainer.value,
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
    if (featureObj && type === 'Polygon') {
      let parcelCode = featureObj.values_.parcelCode
      let nowLand = data.landList.filter(item => {
        return item.parcelCode === parcelCode
      })[0]
      gotoLandDetails(nowLand)
    }else if(featureObj && featureObj.properties_ && type === 'Point') {
      if (featureObj.properties_.device_type === 1 || featureObj.properties_.device_type === '1') {
        getFacilityInfo(featureObj.properties_.device_code, featureObj.properties_.device_type)
        data.sensorShow = true
        data.detectorShow = false
        data.cameraShow = false
        data.stationShow = false
        mapIconSelect(3, true)
        mapIconSelect(4, false)
        mapIconSelect(5, false)
        mapIconSelect(6, false)
        data.activeNames = []
        data.activeNames2 = []
      } else if (featureObj.properties_.device_type === 4 || featureObj.properties_.device_type === '4') {
        getDetectorInfo(featureObj.properties_.device_code)
        data.sensorShow = false
        data.detectorShow = true
        data.cameraShow = false
        data.stationShow = false
        mapIconSelect(3, false)
        mapIconSelect(4, true)
        mapIconSelect(5, false)
        mapIconSelect(6, false)
        data.activeNames = []
        data.activeNames2 = []
      } else if (featureObj.properties_.device_type === 5 || featureObj.properties_.device_type === '5') {
        getCameraInfo(featureObj.properties_.device_code)
        data.sensorShow = false
        data.detectorShow = false
        data.cameraShow = true
        data.stationShow = false
        mapIconSelect(3, false)
        mapIconSelect(4, false)
        mapIconSelect(5, true)
        mapIconSelect(6, false)
        data.activeNames = []
        data.activeNames2 = []
      } else if (featureObj.properties_.device_type === 7 || featureObj.properties_.device_type === '7') {
        getFacilityInfo(featureObj.properties_.device_code, featureObj.properties_.device_type)
        data.sensorShow = false
        data.detectorShow = false
        data.cameraShow = false
        data.stationShow = true
        mapIconSelect(3, false)
        mapIconSelect(4, false)
        mapIconSelect(5, false)
        mapIconSelect(6, true)
        data.activeNames = []
        data.activeNames2 = []
      }
    }else {
      data.sensorShow = false
      data.detectorShow = false
      data.cameraShow = false
      data.stationShow = false
      mapIconSelect(3, false)
      mapIconSelect(4, false)
      mapIconSelect(5, false)
      mapIconSelect(6, false)
    }
  })
  getCenterTokenHandle()
  getGeojsonListHandle()
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
/** 获取geojson地块 */
const getGeojsonListHandle = () => {
	myFarm.getLandList({}).then((res) => {
		if(res.code === 200) {
      data.landList = res.data
      let map = document.getElementById('map')
      for(let i = 0; i < data.landList.length; i++) {
        data.landList[i] = {
          ...data.landList[i],
          landImg: '',
          length: data.landList[i].length ? data.landList[i].length : '',
        }
        if(!data.landList[i].img) {
          let canvas = document.createElement('canvas')
          canvas.id = 'landImg' + i
          canvas.style.position = 'absolute'
          canvas.style.top = '-100px'
          canvas.style.left = '-100px'
          map.appendChild(canvas)
          drawCanvasHandle(data.landList[i].border.coordinates[0], 'landImg', i)
        }
      }
      pointAndPloygonHandle(res.data, 'all')
      data.refreshLoading = false
    }else {
      data.refreshLoading = false
    }
	}).catch(() => {
    data.refreshLoading = false
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
		const polygonVector = map.value.getLayers().array_[2]
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
    let features = polygonVector.getSource().getFeatures()
    for(let i = 0; i < features.length; i++) {
      let length = formatLength(features[i].getGeometry())
      if(!data.landList[i].length) {
        data.landList[i].length = length
      }
    }
	}
}
/** 物联网设备是否展示改变触发 */
const facilityShowChangeHandle = (value) => {
  const layers = map.value.getLayers().array_
  layers[3].setVisible(value)
  layers[4].setVisible(value)
  layers[5].setVisible(value)
  layers[6].setVisible(value)
  data.sensorShow = false
  data.detectorShow = false
  data.cameraShow = false
  data.stationShow = false
  mapIconSelect(3, false)
  mapIconSelect(4, false)
  mapIconSelect(5, false)
  mapIconSelect(6, false)
}
/** 地块列表刷新 */
const onRefresh = () => {
  data.refreshLoading = true
  getGeojsonListHandle()
}
/** 加载列表 */
const onLoad = () => {
  data.loading = false
  data.finished = true
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
    data.landList[index].landImg = dataUrl
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
    path: '/landDetails'
  })
}
/** 编辑 */
const gotoEditLand = (node) => {
  localStorage.setItem('editLand', JSON.stringify(node))
  router.push({
    path: '/editLand'
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
  mapTypeListBox.style.right = '-120px'
  mapTypeListBox.style.transition = 'all 0.5s'
}
/** 关闭所有 */
const closeAll = () => {
  data.showLandList = false
  data.mapHigh = false
  let mapTypeListBox = document.getElementById('mapTypeListBox')
  mapTypeListBox.style.right = '-120px'
  mapTypeListBox.style.transition = 'all 0.5s'
  data.activeNames = []
  data.activeNames2 = []
}
/** 地图图标高亮 */
const mapIconSelect = (index, type) => {
  const layers = map.value.getLayers()
  layers.array_[index].setStyle(function(feature) {
    let mapIcon = null
    if (feature.properties_.device_type === 1 || feature.properties_.device_type === '1') {
      mapIcon = mapIcon1
    } else if (feature.properties_.device_type === 4 || feature.properties_.device_type === '4') {
      mapIcon = mapIcon4
    } else if (feature.properties_.device_type === 5 || feature.properties_.device_type === '5') {
      mapIcon = mapIcon5
    } else if (feature.properties_.device_type === 7 || feature.properties_.device_type === '7') {
      mapIcon = mapIcon7
    }
    return new Style({
      fill: new Fill({
        color: 'rgba(71, 255, 255, 0.15)'
      }),
      stroke: new Stroke({
        color: `#47ffff`,
        width: 1
      }),
      image: new Icon({
        anchor: type && data.featureObj && data.featureObj.properties_ && feature.get('device_code') === data.featureObj.properties_.device_code ? [0.5, 60] : [0.5, 46],
        anchorXUnits: 'fraction',
        anchorYUnits: 'pixels',
        src: mapIcon,
        scale: type && data.featureObj && data.featureObj.properties_ && feature.get('device_code') === data.featureObj.properties_.device_code ? 0.45 : 0.3,
      })
    })
  })
}
/** 获取详情 */
const getFacilityInfo = (value, type) => {
  myFarm.getFacilityDetails({
    deviceCode: value
  }).then(res => {
    if(res.code === 200) {
      if(type === 1 || type === '1') {
        data.sensorInfo = res.data.filter(item => {
          return item.collectSymbol !== 'dy'
        })
        for(let i = 0; i < data.sensorInfo.length; i++) {
          if(data.sensorInfo[i].collectSymbol === 'ec') {
            data.sensorInfo[i] = {
              ...data.sensorInfo[i],
              icon: ec,
            }
          }else if(data.sensorInfo[i].collectSymbol === 'tw') {
            data.sensorInfo[i] = {
              ...data.sensorInfo[i],
              icon: tw,
            }
          }else if(data.sensorInfo[i].collectSymbol === 'ts') {
            data.sensorInfo[i] = {
              ...data.sensorInfo[i],
              icon: ts,
            }
          }else if(data.sensorInfo[i].collectSymbol === 'jl') {
            data.sensorInfo[i] = {
              ...data.sensorInfo[i],
              icon: yw,
            }
          }
        }
      }else if(type === 7 || type === '7') {
        data.stationInfo = res.data.filter(item => {
          return item.collectSymbol !== 'dy' && item.collectSymbol !== 'ec' && item.collectSymbol !== 'tw' && item.collectSymbol !== 'ts' && item.collectSymbol !== 'jl'
        })
        for(let i = 0; i < data.stationInfo.length; i++) {
          if(data.stationInfo[i].collectSymbol === 'gz') {
            data.stationInfo[i] = {
              ...data.stationInfo[i],
              icon: gz,
            }
          }else if(data.stationInfo[i].collectSymbol === 'ylh') {
            data.stationInfo[i] = {
              ...data.stationInfo[i],
              icon: yl,
            }
          }else if(data.stationInfo[i].collectSymbol === 'qy') {
            data.stationInfo[i] = {
              ...data.stationInfo[i],
              icon: qy,
            }
          }else if(data.stationInfo[i].collectSymbol === 'wd') {
            data.stationInfo[i] = {
              ...data.stationInfo[i],
              icon: kw,
            }
          }else if(data.stationInfo[i].collectSymbol === 'sd') {
            data.stationInfo[i] = {
              ...data.stationInfo[i],
              icon: ks,
            }
          }else if(data.stationInfo[i].collectSymbol === 'fx') {
            data.stationInfo[i] = {
              ...data.stationInfo[i],
              icon: fx,
            }
          }else if(data.stationInfo[i].collectSymbol === 'fs') {
            data.stationInfo[i] = {
              ...data.stationInfo[i],
              icon: fs,
            }
          }
        }
      }
    }
  })
}
/** 获取虫情仪详情 */
const getDetectorInfo = (value) => {
  myFarm.getDetectorDetails({
    deviceCode: value
  }).then(res => {
    if(res.code === 200) {
      data.detectorInfo = {
        ...res.data,
        newWormList: chunkArray(res.data.wormList, 2),
      }
    }
  })
}
/** 获取摄像头详情 */
const getCameraInfo = (value) => {
  myFarm.getCameraDetails({
    deviceCode: value,
    protocol: "hls",
  }).then(res => {
    if(res.code === 200) {
      data.cameraInfo = res.data
      initializePlayer(data.cameraInfo[0].cameraUrl)
    }
  })
}
/** 传感器图表 */
const sensorCurrentHandle = (index) => {
  let nowSensor = data.sensorInfo[index]
  myFarm.getAnalysis({
    deviceCode: data.featureObj.properties_.device_code,
    collectSymbol: nowSensor.collectSymbol,
  }).then(res => {
    let nowData = res[nowSensor.collectSymbol]
    let echartsBox = document.getElementById('echartsBox' + index)
    let Echarts = echarts.init(echartsBox)
    const option = {
      color: ["#f28b46"],
      grid: {
        top: 30,
        bottom: 10,
        left: 10,
        right: 10,
        containLabel: true,
      },
      itemStyle: {
        normal: {
          barBorderRadius: 3,
        },
      },
      tooltip: {
        formatter: function(params) {
          let res = params[0].name + `<br/>${params[0].marker} ${params[0].seriesName}：${params[0].data} ${nowSensor.collectUnit}`
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
        type: "category",
        offset: 2,
        nameGap: 50,
        splitLine: { show: false },
        axisTick: { show: false },
        axisLabel: {
          color: "#000",
        },
        data: nowData.map(item => item.collectTime.slice(5, 10)),
      },
      yAxis: {
        type: "value",
        name: nowSensor.collectUnit,
        show: true,
        axisLine: { show: false },
        // interval: 100,  // 设置刻度间隔为10
        axisLabel: {
          textStyle: {
            fontSize: 12,
            color: "#000",
          }
        },
        axisTick: { show: false },
        splitLine: {
          show: true,
          lineStyle: {
            type: "dashed",
            color: "#eee",
          },
        },
      },
      series: [
        {
          type: "line",
          name: nowSensor.collectName,
          symbol: "none", //去掉圆点
          smooth:true,  //让曲线变平滑的
          encode: {
            x: 0,
            y: 1,
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0, color: "rgba(253, 237, 226, 1)",   // 渐变起点颜色和透明度
            },                                                     {
              offset: 1, color: "rgba(253, 237, 226, 0)",     // 渐变终点颜色和透明度
            }], false),
          },
          data: nowData.map(item => item.collectData),
        },
      ],
    }
    echartsBox.removeAttribute('_echarts_instance_')  // 保证echarts每次重新加载
    // 绘制图表
    Echarts.setOption(option)
  })
}
/**面板改变触发 */
const collapseChangeHandle = (value) => {
  // if(value.length > 0) {
  //   for(let its of value) {
  //     setTimeout(() => {
  //       sensorCurrentHandle(its)
  //     }, 0)
  //   }
  // }
}
/** 面板改变触发 */
const collapseChangeHandle2 = (value) => {
  // if(value.length > 0) {
  //   for(let its of value) {
  //     setTimeout(() => {
  //       stationCurrentHandle(its)
  //     }, 0)
  //   }
  // }
}
/** 气象站图表 */
const stationCurrentHandle = (index) => {
  let nowStation = data.stationInfo[index]
  myFarm.getAnalysis({
    deviceCode: data.featureObj.properties_.device_code,
    collectSymbol: nowStation.collectSymbol,
  }).then(res => {
    let nowData = res[nowStation.collectSymbol]
    let echartsBox = document.getElementById('stationEchartsBox' + index)
    let Echarts = echarts.init(echartsBox)
    const option = {
      color: ["#f28b46"],
      grid: {
        top: 30,
        bottom: 10,
        left: 10,
        right: 10,
        containLabel: true,
      },
      itemStyle: {
        normal: {
          barBorderRadius: 3,
        },
      },
      tooltip: {
        formatter: function(params) {
          let res = params[0].name + `<br/>${params[0].marker} ${params[0].seriesName}：${params[0].data} ${nowStation.collectUnit}`
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
        type: "category",
        offset: 2,
        nameGap: 50,
        splitLine: { show: false },
        axisTick: { show: false },
        axisLabel: {
          color: "#000",
        },
        data: nowData.map(item => item.collectTime.slice(5, 10)),
      },
      yAxis: {
        type: "value",
        name: nowStation.collectUnit,
        show: true,
        axisLine: { show: false },
        // interval: 100,  // 设置刻度间隔为10
        axisLabel: {
          textStyle: {
            fontSize: 12,
            color: "#000",
          }
        },
        axisTick: { show: false },
        splitLine: {
          show: true,
          lineStyle: {
            type: "dashed",
            color: "#eee",
          },
        },
      },
      series: [
        {
          type: "line",
          name: nowStation.collectName,
          symbol: "none", //去掉圆点
          smooth:true,  //让曲线变平滑的
          encode: {
            x: 0,
            y: 1,
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0, color: "rgba(253, 237, 226, 1)",   // 渐变起点颜色和透明度
            },                                                     {
              offset: 1, color: "rgba(253, 237, 226, 0)",     // 渐变终点颜色和透明度
            }], false),
          },
          data: nowData.map(item => item.collectData),
        },
      ],
    }
    echartsBox.removeAttribute('_echarts_instance_')  // 保证echarts每次重新加载
    // 绘制图表
    Echarts.setOption(option)
  })
}
/** 摄像头关闭 */
const cameraClose = () => {
  const videoElement = videoPlayer.value
  videoElement.src = ''
  videoElement.play()
}
/** m3u8视频格式播放 */
const initializePlayer = (url) => {
  const videoElement = videoPlayer.value
  const videoSrc = url
  if (Hls.isSupported()) {
    // 如果浏览器支持 HLS，创建 Hls 实例并将其附加到 video 元素上
    const hls = new Hls()
    hls.loadSource(videoSrc)
    hls.attachMedia(videoElement)
    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      videoElement.play()
    })
  } else if (videoElement.canPlayType('application/vnd.apple.mpegurl')) {
    // 如果浏览器支持原生 HLS 播放
    videoElement.src = videoSrc
    videoElement.addEventListener('canplay', () => {
      videoElement.play()
    })
  } else {
    console.error('HLS is not supported in this browser.')
  }
}
/** 划分多组 */
const chunkArray = (array, size) => {
  const chunks = []
  for (let i = 0; i < array.length; i += size) {
    chunks.push(array.slice(i, i + size))
  }
  return chunks
}
/** 画地块 */
const gotoAddPointLand = () => {
  router.push({
    path: '/addPointLand'
  })
}
/** 搜索页面 */
const gotoSearchPage = () => {
  router.push({
    path: '/searchPage'
  })
}
/** 查询重复元素 */
const subSet = (arr1, arr2) => {
  let arr = []
  for(let i = 0; i < arr1.length; i++) {
    if(arr2.indexOf(arr1[i]) != '-1') {
      arr.push(arr1[i])
    }
  }
  return arr
}
/** 开始 */
const popTouchStart = (e) => {
  const touch = e.touches[0]
  data.touch.startX = touch.pageX
  data.touch.startY = touch.pageY
}
/** 移动 */
const popTouchMove = (e) => {
  const touch = e.touches[0]
  //滑动位置和初始位置差
  const deltaY = touch.pageY - data.touch.startY
  if(deltaY > 0) {
    data.showLandList = false
    data.sensorShow = false
    data.detectorShow = false
    data.cameraShow = false
    data.stationShow = false
    mapIconSelect(3, false)
    mapIconSelect(4, false)
    mapIconSelect(5, false)
    mapIconSelect(6, false)
    data.activeNames = []
    data.activeNames2 = []
  }
}
/** 结束 */
const popTouchEnd = (e) => {
  data.touch.startX = null
  data.touch.startY = null
}
/** 关闭画地块选项弹框 */
const onCancelDrawLand = () => {
  data.drawLandTypePicker = false
}
/** 走一圈 */
const gotoWalkLand = () => {
  router.push({
    path: '/walkLand'
  })
}
/** 画地块选项改变触发 */
const drawLandChangeHandle = (value) => {
  if(value.name === '画地块') {
    gotoAddPointLand()
  }else if(value.name === '跑马圈地') {
    gotoWalkLand()
  }
}
/** 开始 */
const popTouchStart2 = (e) => {
  const touch = e.touches[0]
  data.touch2.startX = touch.pageX
  data.touch2.startY = touch.pageY
}
/** 移动 */
const popTouchMove2 = (e) => {
  if(new Date() - data.lastDate >= data.ms) {
    const touch = e.touches[0]
    //滑动位置和初始位置差
    const deltaY = touch.pageY - data.touch2.startY
    if(deltaY > 0) {
      if(data.isAllShow) {
        data.isAllShow = false
        let facilityPopup = document.getElementById('facilityPopup')
        if(facilityPopup) {
          facilityPopup.style.transition = 'all 0.5s'
        }
        let facilityPopup2 = document.getElementById('facilityPopup2')
        if(facilityPopup2) {
          facilityPopup2.style.transition = 'all 0.5s'
        }
      }else {
        data.showLandList = false
        data.sensorShow = false
        data.detectorShow = false
        data.cameraShow = false
        data.stationShow = false
        mapIconSelect(3, false)
        mapIconSelect(4, false)
        mapIconSelect(5, false)
        mapIconSelect(6, false)
        data.activeNames = []
        data.activeNames2 = []
      }
    }else {
      if(!data.isAllShow) {
        data.isAllShow = true
        let facilityPopup = document.getElementById('facilityPopup')
        if(facilityPopup) {
          facilityPopup.style.transition = 'all 0.5s'
        }
        let facilityPopup2 = document.getElementById('facilityPopup2')
        if(facilityPopup2) {
          facilityPopup2.style.transition = 'all 0.5s'
        }
      }
    }
    data.lastDate = new Date()
  }
}
/** 结束 */
const popTouchEnd2 = (e) => {
  data.touch2.startX = null
  data.touch2.startY = null
}

/** 监听三合一传感器所选数据变化 */
watch(
  () => data.activeNames,
  (newVal, oldVal) => {
    if(newVal && oldVal && newVal.length > oldVal.length) {
      let newArr = subSet(newVal, oldVal)
      let nowArr = JSON.parse(JSON.stringify(newVal))
      for(let its of newArr) {
        nowArr = nowArr.filter(item => {
          return item !== its
        })
      }
      for(let ele of nowArr) {
        setTimeout(() => {
          sensorCurrentHandle(ele)
        }, 0)
      }
    }
  },
  {
    deep: true,
    immediate: true,
  }
)
/** 监听气象站所选数据变化 */
watch(
  () => data.activeNames2,
  (newVal, oldVal) => {
    if(newVal && oldVal && newVal.length > oldVal.length) {
      let newArr = subSet(newVal, oldVal)
      let nowArr = JSON.parse(JSON.stringify(newVal))
      for(let its of newArr) {
        nowArr = nowArr.filter(item => {
          return item !== its
        })
      }
      for(let ele of nowArr) {
        setTimeout(() => {
          stationCurrentHandle(ele)
        }, 0)
      }
    }
  },
  {
    deep: true,
    immediate: true,
  }
)
</script>

<style lang="less" scoped>
#landManagement {
  width: 100vw;
  height: calc(100vh - 50px);
  display: flex;
  flex-direction: row;
  position: relative;
  .map {
    width: 100vw;
    height: 100%;
    position: relative;
    .topTabBox {
      height: 36px;
      background-color: #fff;
      position: absolute;
      top: 20px;
      left: 10px;
      z-index: 100;
      border-radius: 18px;
      display: flex;
      align-items: center;
      padding: 3px;
      .tabItem, .selectedTabItem {
        padding: 5px 10px;
        border-radius: 15px;
        color: #475669;
        font-size: 14px;
        font-weight: bold;
      }
      .selectedTabItem {
        color: #fff;
        background: linear-gradient(90deg, #31AD62 0%, #008F5F 100%);
      }
    }
    .rightToolBox {
      width: 44px;
      height: 134px;
      border-radius: 8px;
      position: absolute;
      top: 80px;
      right: 10px;
      background-color: #fff;
      z-index: 100;
      padding: 10px;
      .searchBox, .mapTypeBox, .paintedLandBox {
        width: 24px;
        img {
          width: 100%;
          height: 100%;
        }
      }
      .searchBox {
        height: 34px;
        padding-bottom: 10px;
      }
      .mapTypeBox {
        height: 46px;
        padding: 10px 0;
        border-top: 1px solid rgba(0, 0, 0, 0.09);
        border-bottom: 1px solid rgba(0, 0, 0, 0.09);
      }
      .paintedLandBox {
        height: 34px;
        padding-top: 10px;
      }
    }
    .mapTypeListBox {
      position: absolute;
      top: 124px;
      right: -120px;
      width: 120px;
      height: 120px;
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
        height: 90px;
        padding: 10px;
        .typeItem {
          width: 50px;
          height: 75px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          .itemImg {
            width: 50px;
            height: 50px;
            border: 1px solid #008F5F;
            border-radius: 6px;
            position: relative;
            img {
              width: 100%;
              height: 100%;
            }
            .selectedImg {
              position: absolute;
              bottom: 0;
              right: 0;
              width: 16px;
              height: 16px;
            }
          }
          .itemName {
            height: 20px;
            font-size: 12px;
            color: #008F5F;
            margin-top: 5px;
          }
        }
      }
    }
    .facilityShowBox {
      width: 44px;
      height: 60px;
      border-radius: 8px;
      background-color: #fff;
      position: absolute;
      top: 224px;
      right: 10px;
      z-index: 100;
      padding: 6px 10px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      img {
        width: 100%;
        height: 24px;
        margin-bottom: 5px;
      }
    }
    .bottomLocationBox {
      width: 44px;
      height: 44px;
      border-radius: 22px;
      background-color: #fff;
      position: absolute;
      bottom: 20px;
      left: 10px;
      z-index: 100;
      img {
        width: 100%;
        height: 100%;
      }
    }
    .landListShowBox {
      width: 44px;
      height: 44px;
      border-radius: 8px;
      background: linear-gradient(90deg, #31AD62 0%, #008F5F 100%);
      position: absolute;
      bottom: 20px;
      right: 10px;
      z-index: 100;
      padding: 8px 10px;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
  .landListPopup {
    .popupContentBox {
      width: 100%;
      height: 100%;
      .popupHead {
        width: 100%;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        img {
          width: 32px;
        }
      }
      .popupBody {
        width: 100%;
        height: calc(100% - 24px);
        overflow-x: hidden;
        overflow-y: auto;
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
      }
      .popupBody::-webkit-scrollbar {
        display: none;
      }
      .sensorBody, .stationBody {
        width: 100%;
        height: calc(100% - 24px);
        overflow: hidden;
        .facilityNameBox {
          width: 100%;
          height: 30px;
          padding: 0 16px;
          line-height: 30px;
          font-size: 17px;
          font-weight: bold;
        }
        .facilityInfoBox {
          width: 100%;
          height: calc(100% - 30px);
          padding: 10px 16px;
          overflow-x: hidden;
          overflow-y: auto;
          .contentItemTitle {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            img {
              width: 40px;
              height: 40px;
              margin-right: 5px;
            }
          }
          .contentItem {
            width: 100%;
            height: 150px;
            .echartsBox {
              width: 100%;
              height: 150px;
            }
          }
        }
        .facilityInfoBox::-webkit-scrollbar {
          display: none;
        }
      }
      .cameraBody {
        width: 100%;
        height: calc(100% - 24px);
        overflow: hidden;
        .facilityNameBox {
          width: 100%;
          height: 30px;
          padding: 0 16px;
          line-height: 30px;
          font-size: 17px;
          font-weight: bold;
        }
        .facilityInfoBox {
          width: 100%;
          height: calc(100% - 30px);
          padding: 10px 16px 15px 16px;
          overflow: hidden;
          .video {
            width: 100%;
            height: 100%;
            object-fit: cover; // 让video铺满整个div
            border-radius: 10px;
          }
        }
      }
      .detectorBody {
        width: 100%;
        height: calc(100% - 24px);
        overflow: hidden;
        .facilityNameBox {
          width: 100%;
          height: 30px;
          padding: 0 16px;
          line-height: 30px;
          font-size: 17px;
          font-weight: bold;
        }
        .facilityInfoBox {
          width: 100%;
          height: calc(100% - 30px);
          padding: 10px 16px 15px 16px;
          overflow: hidden;
          .contentBox {
            width: 100%;
            height: 100%;
            background-color: #fff;
            border-radius: 10px;
            padding: 10px;
            .contentTopBox {
              width: 100%;
              height: 100px;
              margin-bottom: 15px;
              display: flex;
              align-items: center;
              justify-content: space-between;
              .contentLeftBox {
                width: 100px;
                height: 100px;
                img {
                  width: 100%;
                  height: 100%;
                  border-radius: 10px;
                }
              }
              .contentRightBox {
                width: calc(100% - 110px);
                height: 100px;
                margin-left: 10px;
                background-color: #f5f6f7;
                border-radius: 10px;
                padding: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                .numItem, .typeItem {
                  width: 50%;
                  height: 100%;
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  justify-content: center;
                  .itemTitle {
                    font-size: 14px;
                    color: #58756f;
                    font-weight: bold;
                    margin-bottom: 2px;
                  }
                  .itemContent {
                    width: 60px;
                    height: 60px;
                    position: relative;
                    line-height: 60px;
                    text-align: center;
                    font-size: 30px;
                    font-weight: bold;
                    color: #067050;
                    img {
                      width: 100%;
                      height: 100%;
                      position: absolute;
                      top: 0;
                      left: 0;
                    }
                  }
                }
              }
            }
            .contentBottomBox {
              width: 100%;
              height: calc(100% - 115px);
              overflow-x: hidden;
              overflow-y: auto;
              .wormList {
                width: 100%;
                height: 44px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 10px;
                .wormItem {
                  width: calc(50% - 5px);
                  height: 100%;
                  background-color: #F5F7F7;
                  border-radius: 10px;
                  padding: 10px 8px;
                  display: flex;
                  align-items: center;
                  justify-content: space-between;
                  .wormItemLeft {
                    display: flex;
                    align-items: center;
                    justify-content: flex-start;
                    .wormIcon {
                      width: 24px;
                      height: 24px;
                      border-radius: 9px;
                      background-color: #e4ebe9;
                      margin-right: 2px;
                      img {
                        width: 14px;
                        height: 14px;
                        margin: 5px;
                      }
                    }
                    .wormName {
                      font-size: 13px;
                      color: #58756f;
                    }
                  }
                  .wormItemRight {
                    font-size: 13px;
                    span {
                      font-size: 18px;
                      font-weight: bold;
                    }
                  }
                }
              }
              .wormList:last-child {
                margin-bottom: 0;
              }
            }
            .contentBottomBox::-webkit-scrollbar {
              display: none;
            }
          }
        }
      }
    }
  }
}
</style>
<style lang="less">
.ol-zoom {
  display: none !important;
}
.van-collapse-item {
  margin-bottom: 10px;
}
.van-collapse-item:last-child {
  margin-bottom: 0;
}
.van-collapse-item__title {
  height: 60px;
  display: flex;
  align-items: center;
  border-radius: 10px;
  .van-cell__right-icon {
    font-size: 14px;
  }
}
.van-collapse-item__title--expanded {
  border-radius: 10px 10px 0 0;
}
.van-collapse-item__content {
  border-radius: 0 0 10px 10px;
}
</style>
