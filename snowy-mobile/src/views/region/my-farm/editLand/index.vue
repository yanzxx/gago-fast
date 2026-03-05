<template>
  <div class="editLand">
    <van-nav-bar
      title="编辑地块信息"
      className="head-bar"
      left-arrow
      @click-left="onClickLeft"
    />
    <div class="editLandBox">
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
                  <div><span>{{data.editLand.realArea ? data.editLand.realArea : '--'}}</span> 亩</div>
                  <div>面积</div>
                </div>
              </div>
              <div class="landBasicInfoItem">
                <div class="landBasicInfoItemLeft">
                  <img src="./img/length.png" />
                </div>
                <div class="landBasicInfoItemRight">
                  <div><span>{{data.editLand.length ? data.editLand.length : '--'}}</span> 米</div>
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
              <van-field v-model="data.editLand.shortName" label="地块名称" placeholder="请输入" input-align="right" class="contentField" />
              <van-field v-model="data.editLand.area" label="填写面积（亩）" placeholder="请输入" input-align="right" label-width="100px" class="contentField" @blur="areaChangeHandle" />
              <van-field v-model="data.editLand.variety" label="作物类型" placeholder="请选择" input-align="right" is-link readonly @click="data.showVariety = true" class="contentField" />
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="footBox">
      <div class="saveBtn" @click="saveLand">保存</div>
    </div>
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
import { onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showLoadingToast, showSuccessToast, showFailToast, closeToast, showToast } from 'vant'
import myFarm from "@/api/region/myFarm/index.js"
import { boundingExtent, getCenter } from 'ol/extent'

const route = useRoute()
const router = useRouter()

const columns = [
  { text: '水稻', value: '水稻' },
  { text: '小麦', value: '小麦' },
  { text: '其他', value: '其他' },
];

const data = reactive({
  editLand: JSON.parse(localStorage.getItem('editLand')),
  showVariety: false,
  address: '',
})

onMounted(() => {
  drawCanvasHandle(data.editLand.border.coordinates[0], 'landImg')
  let extent = boundingExtent(data.editLand.border.coordinates[0])
  let center = getCenter(extent)
  map_click(center)
})

/** 返回 */
const onClickLeft = () => {
  if(route.query.path && route.query.path === 'search') {
    router.go(-2)
  }else {
    router.go(-1)
  }
  localStorage.removeItem('editLand')
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
/** 确定作物类型 */
const onConfirm = ({ selectedValues }) => {
  data.editLand.variety = selectedValues[0]
  data.showVariety = false
}
/** 保存 */
const saveLand = () => {
  if(!data.editLand.shortName) {
    showToast('请输入地块名称')
    return
  }
  if(!data.editLand.area) {
    showToast('请输入地块面积')
    return
  }
  if(!data.editLand.variety) {
    showToast('请选择作物类型')
    return
  }
  showLoadingToast({
    message: '保存中...',
    forbidClick: true,
    duration: 30000,
  })
  myFarm.editLandInfo({
    id: data.editLand.id,
    shortName: data.editLand.shortName,
    area: data.editLand.area,
    variety: data.editLand.variety,
    address: data.editLand.address ? data.editLand.address : data.address,
    length: data.editLand.length,
  }).then(res => {
    if(res.code === 200) {
      closeToast()
      showSuccessToast('编辑成功')
      if(route.query.path && route.query.path === 'search') {
        router.go(-2)
      }else {
        router.go(-1)
      }
      localStorage.removeItem('editLand')
    }else {
      closeToast()
      showFailToast('编辑失败')
    }
  }).catch(() => {
    closeToast()
    showFailToast('编辑失败')
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
  if(isNaN(Number(data.editLand.area)) || Number(data.editLand.area) < 0) {
    data.editLand.area = ''
  }else if(Number(data.editLand.area) == 0) {
    data.editLand.area = '0'
  }else {
    data.editLand.area = JSON.stringify(Number(Number(data.editLand.area).toFixed(2)))
  }
}
</script>

<style lang="less" scoped>
.editLand {
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
  .editLandBox {
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
</style>
<style lang="less">
.van-cell__right-icon {
  font-size: 14px;
}
.contentField {
  .van-field__label {
    --van-field-label-color: #58756f !important;
  }
}
</style>
