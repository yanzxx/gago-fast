<template>
	<div>
		<!-- 轮播图 -->
		<div v-if="['swiper',].includes(props.code) && props.isShow">
			<home-swiper></home-swiper>
		</div>
		<!-- 图表 -->
		<div class="item" v-if="['chart',].includes(code) && isShow">
			<home-chart></home-chart>
		</div>
		<!-- 日程 -->
		<div class="item" v-if="['schedule',].includes(code) && isShow">
			<home-schedule></home-schedule>
		</div>
	</div>
</template>

<script setup>
	import HomeSwiper from './home-swiper.vue';
  import { defineProps } from 'vue';
	import HomeChart from './home-chart.vue'
	import HomeSchedule from './home-schedule/index.vue'

	const props = defineProps({
		code: {
			type: String,
			default: '',
			required: true
		},
		isShow: {
			type: Boolean,
			default: true,
			required: true
		},
	})
</script>

<style lang="scss">
	.item {
		background-color: #ffffff;
		margin: 6px 0 0 0;
    width: 100%;
    min-height: 300px;
	}
</style>