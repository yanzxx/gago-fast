<template>
	<a-timeline style="margin-left: 20px">
		<a-timeline-item :color="getColor(comment.operate)" :key="comment.id" v-for="comment in props.commentList">
			<p style="font-weight: 700">{{ comment.taskName }}</p>
			<p>{{ comment.userName }}{{ comment.operate }} ：{{ comment.comment }}</p>
			<p style="color: rgb(188 189 190)">{{ comment.approveTime }}</p>
		</a-timeline-item>
		<!--green red gray-->
	</a-timeline>
</template>

<script setup name="timelineForm">
	const props = defineProps({
		commentList: {
			type: Array,
			// eslint-disable-next-line vue/require-valid-default-prop
			default: [],
			required: true
		}
	})

	// 获取颜色
	const getColor = (operate) => {
		if (operate === '发起申请' || operate === '同意') {
			return 'green'
		} else if (operate === '不同意') {
			return 'red'
		} else if (operate === '撤回') {
			return 'rgb(197 197 198)'
		} else {
			return 'blue'
		}
	}
</script>

<style scoped></style>
