<template>
	<div class="container-icon" :style="{ backgroundColor: props.backgroundColor }">
		<i v-if="$attrs.type && $attrs.type.indexOf('icon') > -1" class="jiage" :class="$attrs.type" style="font-size: 20px; color: #FFF"></i>
    <i v-else class="snowy" :class="$attrs.type" style="font-size: 20px; color: #FFF"></i>
	</div>
</template>

<script setup>
	const props = defineProps({
		backgroundColor: {
			type: String,
			required: false
		}
	})
</script>

<style lang="scss" scoped>
	.container-icon {
		width: 40px;
		height: 40px;
		border-radius: 10px;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 1px 4px 5px rgba(72, 22, 174, 0.3);
	}
</style>
