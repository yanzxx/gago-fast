<template>
  <div class="landDetails">
    <van-nav-bar
      title="地块详情"
      className="head-bar"
      left-arrow
      @click-left="onClickLeft"
    />
    <div class="detailsBox">
      <div class="landImgBox">
        <canvas id="landImg"></canvas>
      </div>
      <div class="landInfo" id="landInfo">
        <div class="landInfoHead" @touchstart="popTouchStart($event)" @touchmove="popTouchMove($event)" @touchend="popTouchEnd($event)">
          <img src="./img/popupHead.png" />
        </div>
        <div class="landInfoBody">
          <div class="landInfoName">{{data.landDetails.shortName}}</div>
          <div class="landBasicInfo">
            <div class="landBasicInfoList">
              <div class="landBasicInfoItem">
                <div class="landBasicInfoItemLeft">
                  <img src="./img/realarea.png" />
                </div>
                <div class="landBasicInfoItemRight">
                  <div><span>{{data.landDetails.realArea ? data.landDetails.realArea : '--'}}</span> 亩</div>
                  <div>面积</div>
                </div>
              </div>
              <div class="landBasicInfoItem">
                <div class="landBasicInfoItemLeft">
                  <img src="./img/length.png" />
                </div>
                <div class="landBasicInfoItemRight">
                  <div><span>{{data.landDetails.length ? data.landDetails.length : '--'}}</span> 米</div>
                  <div>周长</div>
                </div>
              </div>
            </div>
            <div class="landBasicInfoList">
              <div class="landBasicInfoItem">
                <div class="landBasicInfoItemLeft">
                  <img src="./img/area.png" />
                </div>
                <div class="landBasicInfoItemRight">
                  <div><span>{{data.landDetails.area ? data.landDetails.area : '--'}}</span> 亩</div>
                  <div>填写面积</div>
                </div>
              </div>
              <div class="landBasicInfoItem">
                <div class="landBasicInfoItemLeft">
                  <img src="./img/crop.png" />
                </div>
                <div class="landBasicInfoItemRight">
                  <div><span>{{data.landDetails.variety ? data.landDetails.variety : '其他'}}</span></div>
                  <div>作物</div>
                </div>
              </div>
            </div>
          </div>
          <div class="farmRecordsTitle">
            <div class="titleBox">
              <div>农事记录</div>
              <div class="titleBg"></div>
            </div>
            <div class="acitveBtn" @click="addFarmRecords">
              <van-icon name="plus" color="#298256" />
              <span>新增</span>
            </div>
          </div>
          <div class="farmRecordsList">
            <van-pull-refresh v-model="data.refreshLoading" @refresh="onRefresh">
              <van-list
                v-model:loading="data.loading"
                :finished="data.finished"
                finished-text="没有更多了"
                @load="onLoad"
              >
                <div v-for="item in data.farmRecordsList" :key="item.id" class="farmRecordsItem" @click="gotoFarmRecordsDetails(item)">
                  <div class="farmRecordsItemTop">
                    <div class="dateBox">
                      <img src="./img/date.png" />
                      <div>{{item.recordDate ? item.recordDate.slice(0, 10) : '--'}}</div>
                    </div>
                    <div class="line"></div>
                    <div class="titleBox">{{item.title}}</div>
                  </div>
                  <div class="farmRecordsItemBottom">
                    <div class="farmRecordsItemLeft">{{item.recordMsg}}</div>
                    <div class="farmRecordsItemRight" v-if="item.fileList && item.fileList.length > 0">
                      <div>
                        <img v-if="item.fileList[0].fileType.indexOf('image') !== -1" :src="data.BaseUrl + '/dev/file/download?id=' + item.fileList[0].fileId" alt="" />
                        <video v-if="item.fileList[0].fileType.indexOf('video') !== -1" muted="muted" class="video" :src="data.BaseUrl + '/dev/file/download?id=' + item.fileList[0].fileId" :type="item.fileList[0].fileType" controls="controls" loop="-1" x5-video-player-fullscreen="true" x5-playsinline playsinline webkit-playsinline>
                          <p>你的浏览器不支持video标签.</p>
                        </video>
                      </div>
                    </div>
                    <div v-else class="landListItemInnerRight">
                      <img style="width:136px;height:100px" src="./img/noImg.png" alt="">
                    </div>
                  </div>
                </div>
              </van-list>
            </van-pull-refresh>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import myFarm from "@/api/region/myFarm/index.js"

const route = useRoute()
const router = useRouter()

const data = reactive({
  BaseUrl:`${import.meta.env.VITE_APP_BASE_API}`,
  landDetails: JSON.parse(localStorage.getItem('landDetails')),
  landImg: '',
  refreshLoading: false,
  loading: true,
  finished: false,
  current: 0,
  size: 10,
  farmRecordsList: [],
  touch: {
    startX: null,
    startY: null,
  },
  isFill: false,
})

onMounted(() => {
  drawCanvasHandle(data.landDetails.border.coordinates[0], 'landImg')
  onLoad()
})

/** 返回 */
const onClickLeft = () => {
  if(route.query.path && route.query.path === 'search') {
    router.go(-2)
  }else {
    router.go(-1)
  }
  localStorage.removeItem('landDetails')
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
    data.landImg = dataUrl
  }
}
/** 获取农事记录 */
const onLoad = () => {
  data.current++
  myFarm.getFarmRecordPage({
    current: data.current,
    size: data.size,
    parcelCode: data.landDetails.parcelCode
  }).then(res => {
    if(res.code === 200) {
      data.loading = false
      data.refreshLoading = false
      if(res.data.records.length > 0) {
        data.farmRecordsList = data.farmRecordsList.concat(res.data.records)
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
  data.farmRecordsList = []
  onLoad()
}
/** 新增 */
const addFarmRecords = () => {
  router.push({
    path: '/addFarmRecords'
  })
}
/** 详情 */
const gotoFarmRecordsDetails = (node) => {
  localStorage.setItem('farmRecordsDetails', JSON.stringify(node))
  router.push({
    path: '/farmRecordsDetails'
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
      let landInfo = document.getElementById('landInfo')
      landInfo.style.height = 'calc(100% - 215px)'
      landInfo.style.transition = 'all 0.5s'
    }
  }else if(deltaY < 0) {
    if(!data.isFill) {
      data.isFill = true
      let landInfo = document.getElementById('landInfo')
      landInfo.style.height = 'calc(100% - 50px)'
      landInfo.style.transition = 'all 0.5s'
    }
  }
}
/** 结束 */
const popTouchEnd = (e) => {
  data.touch.startX = null
  data.touch.startY = null
}
</script>

<style lang="less" scoped>
.landDetails {
  width: 100vw;
  height: 100vh;
  position: relative;
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
  .detailsBox {
    width: 100%;
    height: calc(100vh - 50px);
    overflow: hidden;
    .landImgBox {
      width: 100vw;
      height: 180px;
    }
    .landInfo {
      width: 100vw;
      height: calc(100% - 215px);
      position: absolute;
      bottom: 0;
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
        .landInfoName {
          width: calc(100% - 32px);
          height: 30px;
          font-size: 18px;
          font-weight: bold;
          margin: 0 16px;
        }
        .landBasicInfo {
          width: calc(100% - 32px);
          height: 120px;
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
        .farmRecordsTitle {
          width: calc(100% - 32px);
          height: 50px;
          margin: 0 16px;
          font-size: 16px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          border-top: 1px solid rgba(0,0,0,0.15);
          .acitveBtn {
            color: #298256;
          }
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
        .farmRecordsList {
          width: calc(100% - 32px);
          height: calc(100% - 200px);
          margin: 0 16px;
          overflow-x: hidden;
          overflow-y: auto;
          padding-bottom: 15px;
          .farmRecordsItem {
            width: 100%;
            height: 150px;
            border-radius: 8px;
            background-color: #fff;
            margin-bottom: 10px;
            box-shadow: 0px 0px 8px 0px rgba(18,45,92,0.06);
            padding: 10px;
            .farmRecordsItemTop {
              width: 100%;
              height: 30px;
              display: flex;
              align-items: center;
              justify-content: flex-start;
              font-weight: bold;
              .dateBox {
                height: 30px;
                display: flex;
                align-items: center;
                justify-content: flex-start;
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
                height: 16px;
                background-color: #ddd;
                margin: 0 15px;
              }
            }
            .farmRecordsItemBottom {
              width: 100%;
              height: 100px;
              display: flex;
              align-items: center;
              justify-content: center;
              .farmRecordsItemLeft {
                width: calc(100% - 150px);
                height: 100px;
                margin-right: 10px;
                font-size: 15px;
                overflow: auto; /* 隐藏溢出的文本 */
              }
              .farmRecordsItemLeft::-webkit-scrollbar {
                display: none;
              }
              .farmRecordsItemRight {
                width: 140px;
                height: 100px;
                img {
                  width: 140px;
                  height: 100px;
                  border-radius: 8px;
                }
                .video {
                  width: 140px;
                  height: 100px;
                  object-fit: fill;
                  border-radius: 8px;
                }
              }
            }
          }
          .farmRecordsItem:last-child {
            margin-bottom: 0;
          }
        }
        .farmRecordsList::-webkit-scrollbar {
          display: none;
        }
      }
    }
  }
}
</style>
