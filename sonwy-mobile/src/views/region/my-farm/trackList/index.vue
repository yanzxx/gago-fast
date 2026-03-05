<template>
  <div id="trackList">
    <van-nav-bar
      title="作业轨迹"
      className="head-bar"
      left-arrow
      @click-left="onClickLeft"
    />
    <div class="searchBox">
      <div class="timeSelectBox">
        <div class="startTimeBox" @click="data.showStartTimePicker = true">{{data.startTime}}</div>
        ~
        <div class="endTimeBox" @click="data.showEndTimePicker = true">{{data.endTime}}</div>
        <img src="./img/downIcon.png" />
      </div>
      <div class="typeSelectBox" @click="data.showTypePicker = true">
        <div v-if="data.machineryType === '全部设备'">{{data.machineryType}}</div>
        <div v-if="data.machineryType === 1">拖拉机</div>
        <div v-if="data.machineryType === 2">收获机械</div>
        <div v-if="data.machineryType === 3">耕种机械</div>
        <img src="./img/downIcon.png" />
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
    <van-popup v-model:show="data.showStartTimePicker" position="bottom">
      <van-date-picker @confirm="dateStartConfirmHandle" @cancel="data.showStartTimePicker = false" :max-date="data.endTime ? new Date(data.endTime) : new Date(data.maxDate)" :min-date="new Date(data.minDate)" v-model="data.currentStartTime" />
    </van-popup>
    <van-popup v-model:show="data.showEndTimePicker" position="bottom">
      <van-date-picker @confirm="dateEndConfirmHandle" @cancel="data.showEndTimePicker = false" :min-date="data.startTime ? new Date(data.startTime) : new Date(data.minDate)" :max-date="new Date(data.maxDate)" v-model="data.currentEndTime" />
    </van-popup>
    <van-popup v-model:show="data.showTypePicker" position="bottom">
      <van-picker
        :columns="columns"
        @confirm="onConfirm"
        @cancel="data.showTypePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import myFarm from "@/api/region/myFarm/index.js"
import moment from 'moment'

const route = useRoute()
const router = useRouter()

const data = reactive({
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
  machineryType: '全部设备',
  showTypePicker: false,
})

onMounted(() => {
  onLoad()
  data.currentStartTime = [data.startTime.split('/')[0], data.startTime.split('/')[1], data.startTime.split('/')[2]]
  data.currentEndTime = [data.endTime.split('/')[0], data.endTime.split('/')[1], data.endTime.split('/')[2]]
})

const columns = [
  { text: '全部设备', value: '全部设备' },
  { text: '拖拉机', value: 1 },
  { text: '收获机械', value: 2 },
  { text: '耕种机械', value: 3 },
];

/** 返回 */
const onClickLeft = () => {
  router.go(-1)
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
    onRefresh()
  }
}
/** 获取作业轨迹 */
const onLoad = () => {
  data.current++
  myFarm.urpPage({
    current: data.current,
    size: data.size,
    startTime: moment(new Date(data.startTime)).format('YYYY-MM-DD') + ' 00:00:00',
    endTime: moment(new Date(data.endTime)).format('YYYY-MM-DD') + ' 23:59:59',
    machineryType: data.machineryType === '全部设备' ? null : data.machineryType,
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
/** 前往轨迹详情页 */
const gotoTrackDetails = (node) => {
  localStorage.setItem('trackDetails', JSON.stringify(node))
  router.push({
    path: '/trackDetails',
  })
}
/** 确定设备类型 */
const onConfirm = ({ selectedValues }) => {
  data.machineryType = selectedValues[0]
  data.showTypePicker = false
  onRefresh()
}
</script>

<style lang="less" scoped>
#trackList {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
  background-color: #f5f6f7;
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
  .searchBox {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    .timeSelectBox {
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: flex-start;
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
    .typeSelectBox {
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      img {
        width: 14px;
        height: 14px;
        margin-left: 3px;
      }
    }
  }
  .trackListBox {
    width: calc(100% - 32px);
    height: calc(100% - 90px);
    margin: 0 16px;
    overflow-x: hidden;
    overflow-y: auto;
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
  .trackListBox::-webkit-scrollbar {
    display: none;
  }
}
</style>
