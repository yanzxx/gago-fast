<template>
	<div class="localScreen" id="localScreen">
		<a-button type="primary" @click="localScreenHandle" style="float: right; margin-top: 20px; margin-right: 22px">
			<template v-if="toggleFlag"> 退出全屏 </template>
			<template v-else> 局部全屏 </template>
		</a-button>
	</div>
</template>

<script setup>
	import { ref } from 'vue'
	import screenfull from 'screenfull'

	let toggleFlag = ref(false)
	const localScreenHandle = () => {
		const element = document.getElementById('localScreen')
		screenfull.toggle(element)
		toggleFlag.value = !toggleFlag.value
	}
</script>

<style lang="less">
	.localScreen {
		width: 100%;
		height: 100%;
		background-image: url('@/assets/images/screenfull.png');
		background-size: 100%, 100%;
		background-repeat: no-repeat;
	}
</style>
