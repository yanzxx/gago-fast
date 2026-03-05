<template>
	<div>
		<!-- 首页看板 -->
		<div class="item" v-if="['chart',].includes(props.code) && props.isShow">
			<home-chart></home-chart>
		</div>
	</div>
</template>

<script setup>
  import { defineProps } from 'vue';
	import HomeChart from './home-chart.vue'

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
