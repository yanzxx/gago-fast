<template>
	<div class="schedule">
    <MyCalendar @onSelectDay="onSelectDayHandle" />
		<div class="add-schedule" @click="add">
			新增
		</div>
    <van-list
        finished-text=""
    >
      <van-cell v-for="schedule in scheduleList" :key="schedule.id">
        <template #title>
          <van-icon name="bell" color="#2979ff" size="20" />
          <span class="custom-title">{{schedule.scheduleContent}}</span>
        </template>
        <div style="align-items: center;">
          {{schedule.scheduleTime}}
          <van-icon name="delete" @click="del(schedule)" color="#e43d33" size="20" />
        </div>
      </van-cell>
    </van-list>
    <add-pop ref="addPopRef" @ok="seleScheduleList()"></add-pop>
	</div>
</template>

<script setup>
	import {
		ref,
	} from "vue";
	import {
		indexScheduleList,
		indexScheduleDeleteSchedule
	} from '@/api/sys/indexApi'
	import XEUtils from 'xe-utils'
  import AddPop from './add-pop.vue'
  import MyCalendar from "./my-calendar.vue";

	const scheduleList = ref([])
	const scheduleDate = ref(XEUtils.toDateString(new Date(), 'YYYY-MM-DD'))
	const addPopRef = ref()

	const seleScheduleList = () => {
		indexScheduleList({
			scheduleDate: scheduleDate.value
		}).then((res) => {
			scheduleList.value = res.data
		})
	}

	const onSelectDayHandle = (e) => {
		scheduleDate.value = e
		seleScheduleList()
	}

	const add = () => {
		addPopRef.value.onOpen(scheduleDate)
	}

	const del = (schedule) => {
		const params = [{
			id: schedule.id
		}]
		indexScheduleDeleteSchedule(params).then(() => {
			seleScheduleList()
		})
	}

</script>

<style lang="scss">
.schedule {
  width: 100%;

  .mpvue-calendar {
    width: 100%;
  }

  .add-schedule {
    cursor: pointer;
    margin: 10px;
    text-align: right;
    font-size: 16px;
    color: #1989fa;
    padding-bottom: 10px;
  }

  .van-cell__title {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .custom-title {
    margin-left: 10px;
  }

  .van-cell__value > div {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
}

</style>