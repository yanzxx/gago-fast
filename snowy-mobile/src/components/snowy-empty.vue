<template>
	<div class="empty-box">
		<img :src="props.imgUrl" />
	</div>
</template>

<script setup>
	const props = defineProps({
		imgUrl: {
			type: String,
			required: false,
      default: new URL(`../assets/empty.svg`, import.meta.url).href
		}
	})
</script>

<style lang="scss" scoped>
	.empty-box {
		height: 400px;
		display: flex;
		justify-content: center;
		align-items: center;
		color: #666666;
		border: none;
	}
</style>