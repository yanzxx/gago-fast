<template>
  <div id="landTrack">
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
      <!-- 底部定位按钮 -->
      <div class="bottomLocationBox" @click.stop>
        <img src="./img/location.png" />
      </div>
    </div>
    <!-- 地块列表 -->
    <van-popup
      v-model:show="data.showlandTrack"
      round
      :overlay="false"
      position="bottom"
      class="landTrackPopup"
      id="landTrackPopup"
      :style="{ height: '50%', marginBottom: '50px', backgroundColor: '#F5F6F7', zIndex: 110 }"
    >
      <div class="popupContentBox">
        <div class="popupHead" @touchstart="popTouchStart($event)" @touchmove="popTouchMove($event)" @touchend="popTouchEnd($event)">
          <img src="./img/popupHead.png" />
        </div>
        <div class="popupBody">
          <div class="popupBodyTitle">
            <div class="titleBox">作业数据</div>
            <div class="detailsBtn" @click="gotoTrackListPage">
              <span>详情</span>
              <img src="./img/rightIcon.png" />
            </div>
          </div>
          <div class="timeSelectBox">
            <div class="startTimeBox" @click="data.showStartTimePicker = true">{{data.startTime}}</div>
            ~
            <div class="endTimeBox" @click="data.showEndTimePicker = true">{{data.endTime}}</div>
            <img src="./img/downIcon.png" />
          </div>
          <div class="basicInfoBox">
            <div class="infoItem">
              <div class="infoItemTop"><span>{{data.workAreaTotal ? data.workAreaTotal : '--'}}</span>亩</div>
              <div class="infoItemBottom">作业亩数</div>
            </div>
            <div class="infoItem">
              <div class="infoItemTop"><span>{{data.durationTotal ? data.durationTotal : '--'}}</span>小时</div>
              <div class="infoItemBottom">作业小时</div>
            </div>
            <div class="infoItem">
              <div class="infoItemTop"><span>{{data.workDay ? data.workDay : '--'}}</span>天</div>
              <div class="infoItemBottom">作业天数</div>
            </div>
          </div>
          <div class="echartsBox">
            <div class="workTotalBar" id="workTotalBar"></div>
          </div>
          <div class="everydayWorkBox">
            <div class="titleBox">
              <div>每日作业</div>
              <div class="titleBg"></div>
            </div>
          </div>
          <div class="trackListBox">
            <van-pull-refresh v-model="data.refreshLoading" @refresh="onRefresh">
              <van-list
                v-model:loading="data.loading"
                :finished="data.finished"
                finished-text="没有更多了"
                @load="onLoad"
              >
                <div class="trackItem" v-for="(item, index) in data.trackList" :key="index" @click="gotoTrackDetails(item)">
                  <div class="trackItemtop">
                    <div class="dateBox">
                      <img src="./img/date.png" />
                      <div>{{item.date}}</div>
                    </div>
                    <div class="line"></div>
                    <div class="typeBox">
                      <div v-if="item.machineryType === 1">拖拉机</div>
                      <div v-if="item.machineryType === 2">收获机械</div>
                      <div v-if="item.machineryType === 3">耕种机械</div>
                    </div>
                  </div>
                  <div class="trackItemBottom">
                    <div class="trackInfoItem">
                      <div class="infoItemTop"><span>{{item.workArea}}</span>亩</div>
                      <div class="infoItemBottom">作业面积</div>
                    </div>
                    <div class="trackInfoItem">
                      <div class="infoItemTop"><span>{{item.duration}}</span>小时</div>
                      <div class="infoItemBottom">作业时长</div>
                    </div>
                    <div class="trackInfoItem">
                      <div class="infoItemTop"><span style="font-size: 16px;">{{item.licensePlate}}</span></div>
                      <div class="infoItemBottom">车牌号</div>
                    </div>
                  </div>
                </div>
              </van-list>
            </van-pull-refresh>
          </div>
        </div>
      </div>
    </van-popup>
    <van-popup v-model:show="data.showStartTimePicker" position="bottom">
      <van-date-picker @confirm="dateStartConfirmHandle" @cancel="data.showStartTimePicker = false" :max-date="data.endTime ? new Date(data.endTime) : new Date(data.maxDate)" :min-date="new Date(data.minDate)" v-model="data.currentStartTime" />
    </van-popup>
    <van-popup v-model:show="data.showEndTimePicker" position="bottom">
      <van-date-picker @confirm="dateEndConfirmHandle" @cancel="data.showEndTimePicker = false" :min-date="data.startTime ? new Date(data.startTime) : new Date(data.minDate)" :max-date="new Date(data.maxDate)" v-model="data.currentEndTime" />
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
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import GeoJSON from 'ol/format/GeoJSON'
import Map from 'ol/Map'
import VectorLayer from 'ol/layer/Vector'
import VectorSource from 'ol/source/Vector'
import View from 'ol/View'
import "ol/ol.css"
import { Fill, Stroke, Style, Text } from 'ol/style'
import { getTopLeft, getWidth } from 'ol/extent'
import { WMTS as WMTSource, XYZ as tileXyz } from 'ol/source'
import * as olProj from 'ol/proj'
import TileLayer from 'ol/layer/Tile'
import WMTSTileGrid from 'ol/tilegrid/WMTS'
import Feature from 'ol/Feature'
import * as echarts from 'echarts'
import moment from 'moment'
import myFarm from "@/api/region/myFarm/index.js"

const router = useRouter()

const map = ref(null)
const mapContainer = ref(null)
const data = reactive({
  mapHigh: false,
  showlandTrack: true,
  startTime: '2023/05/16',
  endTime: '2023/05/31',
  trackList: [],
  refreshLoading: false,
  loading: true,
  finished: false,
  current: 0,
  size: 10,
  showStartTimePicker: false,
  showEndTimePicker: false,
  currentStartTime: [],
  currentEndTime: [],
  maxDate: moment(new Date()).format('YYYY/MM/DD'),
  minDate: '2023/01/01',
  durationTotal: '',
  workAreaTotal: '',
  workDay: '',
  touch: {
    startX: null,
    startY: null,
  },
  isFill: false,
  drawLandTypePicker: false,
  actions: [
    { name: '画地块' },
    { name: '跑马圈地' },
  ],
})

onMounted(() => {
  initMap()
  getWorkAllData()
  getWorkTotalData()
  onLoad()
  data.currentStartTime = [data.startTime.split('/')[0], data.startTime.split('/')[1], data.startTime.split('/')[2]]
  data.currentEndTime = [data.endTime.split('/')[0], data.endTime.split('/')[1], data.endTime.split('/')[2]]
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
  map.value = new Map({
    view: new View({
			center: [120.5753880739212, 32.499661445617676],
			zoom: 7,
			minZoom: 2,
			maxZoom: 18,
			projection: 'EPSG:4326',
		}),
		layers: [bing, bing2, basePbfLayer],
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
      pointAndPloygonHandle(res.data, 'all')
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
	}
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
/** 画地块 */
const gotoAddPointLand = () => {
  router.push({
    path: '/addPointLand'
  })
}
/** 关闭所有 */
const closeAll = () => {
  data.mapHigh = false
  let mapTypeListBox = document.getElementById('mapTypeListBox')
  mapTypeListBox.style.right = '-120px'
  mapTypeListBox.style.transition = 'all 0.5s'
}
/** 获取作业总量 */
const getWorkTotalData = () => {
  myFarm.getJobStatistics({
    start: moment(new Date(data.startTime)).format('YYYY-MM-DD'),
		end: moment(new Date(data.endTime)).format('YYYY-MM-DD'),
  }).then(res => {
    if(res.code === 200) {
      const workTotalBar = echarts.init(document.getElementById('workTotalBar'))
      const dates = res.data.map((item) => item.date.slice(5, 10))
      const counts = res.data.map((item) => item.total)
      const option = {
        title: {
          subtext: '单位：亩',
          left: '5px'
        },
        grid: {
          top: '40px',
          left: '40px',
          right: '20px',
          bottom: '40px'
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 30,
          },
        },
        yAxis: {
          type: 'value'
        },
        tooltip: {
				  trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        series: [
          {
            name: '作业总量',
            data: counts,
            type: 'bar',
            itemStyle: {
              normal: {
                color: '#33A0FF'
              }
            },
            barWidth: 6
          }
        ]
      }
      document.getElementById('workTotalBar').removeAttribute('_echarts_instance_')  // 保证echarts每次重新加载
      workTotalBar.setOption(option)
    }
  })
}
/** 获取作业数据 */
const getWorkAllData = () => {
  myFarm.getWorkEveryData({
    start: moment(new Date(data.startTime)).format('YYYY-MM-DD'),
		end: moment(new Date(data.endTime)).format('YYYY-MM-DD'),
  }).then(res => {
    if(res.code === 200) {
      data.durationTotal = res.data.durationTotal
      data.workAreaTotal = res.data.workAreaTotal
      data.workDay = res.data.workDay
    }
  })
}
/** 获取作业轨迹 */
const onLoad = () => {
  data.current++
  myFarm.urpPage({
    current: data.current,
    size: data.size,
    startTime: moment(new Date(data.startTime)).format('YYYY-MM-DD') + ' 00:00:00',
    endTime: moment(new Date(data.endTime)).format('YYYY-MM-DD') + ' 23:59:59',
  }).then(res => {
    if(res.code === 200) {
      data.loading = false
      data.refreshLoading = false
      if(res.data.records.length > 0) {
        data.trackList = data.trackList.concat(res.data.records)
        console.log(data.trackList);
        if(res.data.records.length < data.size) {
          data.finished = true
        }
      }else {
        data.finished = true
      }
    }else {
      data.loading = false
      data.finished = true
      data.refreshLoading = false
    }
  }).catch(() => {
    data.loading = false
    data.finished = true
    data.refreshLoading = false
  })
}
/** 刷新 */
const onRefresh = () => {
  data.refreshLoading = true
  data.current = 0
  data.trackList = []
  onLoad()
}
/** 确定开始时间 */
const dateStartConfirmHandle = (selectedValues) => {
  if(selectedValues) {
    data.currentStartTime = selectedValues.selectedValues
    data.startTime = selectedValues.selectedValues[0] + '/' + selectedValues.selectedValues[1] + '/' + selectedValues.selectedValues[2]
    if(!data.endTime) {
      data.currentEndTime = selectedValues.selectedValues
      data.endTime = selectedValues.selectedValues[0] + '/' + selectedValues.selectedValues[1] + '/' + selectedValues.selectedValues[2]
    }
    data.showStartTimePicker = false
    getWorkAllData()
    getWorkTotalData()
    onRefresh()
  }
}
/** 确定结束时间 */
const dateEndConfirmHandle = (selectedValues) => {
  if(selectedValues) {
    data.currentEndTime = selectedValues.selectedValues
    data.endTime = selectedValues.selectedValues[0] + '/' + selectedValues.selectedValues[1] + '/' + selectedValues.selectedValues[2]
    if(!data.startTime) {
      data.currentStartTime = selectedValues.selectedValues
      data.startTime = selectedValues.selectedValues[0] + '/' + selectedValues.selectedValues[1] + '/' + selectedValues.selectedValues[2]
    }
    data.showEndTimePicker = false
    getWorkAllData()
    getWorkTotalData()
    onRefresh()
  }
}
/** 前往列表页 */
const gotoTrackListPage = () => {
  router.push({
    path: '/trackList',
  })
}
/** 前往轨迹详情页 */
const gotoTrackDetails = (node) => {
  localStorage.setItem('trackDetails', JSON.stringify(node))
  router.push({
    path: '/trackDetails',
  })
}
/** 搜索页面 */
const gotoSearchPage = () => {
  router.push({
    path: '/searchPage'
  })
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
    if(data.isFill) {
      data.isFill = false
      let landTrackPopup = document.getElementById('landTrackPopup')
      landTrackPopup.style.height = '50%'
      landTrackPopup.style.transition = 'all 0.5s'
    }
  }else if(deltaY < 0) {
    if(!data.isFill) {
      data.isFill = true
      let landTrackPopup = document.getElementById('landTrackPopup')
      landTrackPopup.style.height = 'calc(100% - 126px)'
      landTrackPopup.style.transition = 'all 0.5s'
    }
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
</script>

<style lang="less" scoped>
#landTrack {
  width: 100vw;
  height: calc(100vh - 50px);
  overflow: hidden;
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
  }
  .landTrackPopup {
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
        .popupBodyTitle {
          width: 100%;
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 0 16px;
          .titleBox {
            font-size: 18px;
            font-weight: bold;
          }
          .detailsBtn {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            font-size: 14px;
            color: #298256;
            img {
              width: 14px;
              height: 14px;
            }
          }
        }
        .timeSelectBox {
          width: 100%;
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: flex-start;
          padding: 0 16px;
          .startTimeBox {
            margin-right: 3px;
          }
          .endTimeBox {
            margin-left: 3px;
          }
          img {
            width: 14px;
            height: 14px;
            margin-left: 3px;
          }
        }
        .basicInfoBox {
          width: calc(100% - 32px);
          height: 70px;
          margin: 0 16px;
          background-color: #fff;
          border-radius: 10px;
          padding: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          .infoItem {
            width: calc(100% / 3);
            height: 100%;
            padding-left: 5px;
            .infoItemTop {
              font-size: 14px;
              span {
                font-size: 18px;
                font-weight: bold;
              }
            }
            .infoItemBottom {
              font-size: 14px;
              color: #58756f;
            }
          }
        }
        .echartsBox {
          width: calc(100% - 32px);
          height: 180px;
          margin: 10px 16px 15px 16px;
          background-color: #fff;
          border-radius: 10px;
          #workTotalBar {
            width: calc(100vw - 32px);
            height: 180px;
          }
        }
        .everydayWorkBox {
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
        .trackListBox {
          width: calc(100% - 32px);
          // min-height: 300px;
          // height: calc(100vh - 535px);
          margin: 0 16px;
          overflow-x: hidden;
          overflow-y: hidden;
          .trackItem {
            width: 100%;
            height: 120px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0px 0px 8px 0px rgba(18,45,92,0.06);
            padding: 10px;
            margin-bottom: 10px;
            .trackItemtop {
              width: 100%;
              height: 30px;
              display: flex;
              align-items: center;
              justify-content: flex-start;
              .dateBox {
                display: flex;
                align-items: center;
                justify-content: flex-start;
                font-weight: bold;
                img {
                  width: 18px;
                  height: 18px;
                  margin-right: 5px;
                }
                div {
                  height: 30px;
                  line-height: 32px;
                }
              }
              .line {
                width: 1px;
                height: 14px;
                background-color: #ddd;
                margin: 0 15px;
              }
              .typeBox {
                font-weight: bold;
              }
            }
            .trackItemBottom {
              width: 100%;
              height: 70px;
              background-color: #f4f6f6;
              border-radius: 10px;
              display: flex;
              align-items: center;
              justify-content: center;
              padding: 10px;
              .trackInfoItem {
                width: calc(100% / 3);
                height: 100%;
                padding-left: 5px;
                .infoItemTop {
                  font-size: 14px;
                  height: 25px;
                  display: flex;
                  align-items: center;
                  span {
                    font-size: 18px;
                    font-weight: bold;
                  }
                }
                .infoItemBottom {
                  font-size: 14px;
                  color: #58756f;
                }
              }
            }
          }
          .trackItem:last-child {
            margin-bottom: 0;
          }
        }
      }
      .popupBody::-webkit-scrollbar {
        display: none;
      }
    }
  }
}
</style>
<style lang="less">
.ol-zoom {
  display: none !important;
}
</style>
