<template>
	<div class="adminui-topbar">
		<div class="left-panel">
			<environment-outlined />
			<a-breadcrumb style="margin-left: 10px" separator=">">
				<template v-for="(item, index) in breadList" :key="item.title">
					<a-breadcrumb-item v-if="item.path != '/' && !item.meta.hiddenBreadcrumb" :key="item.meta.title">
						<span :class="index === breadList.length - 1 ? 'active' : ''">
							{{ item.meta.title }}
						</span>
					</a-breadcrumb-item>
				</template>
			</a-breadcrumb>
		</div>
		<div class="center-panel"></div>
		<div class="right-panel">
			<slot></slot>
		</div>
	</div>
</template>

<script>
	export default {
		data() {
			return {
				breadList: []
			}
		},
		watch: {
			$route() {
				this.getBreadcrumb()
			}
		},
		created() {
			this.getBreadcrumb()
		},
		methods: {
			getBreadcrumb() {
				const matched = this.$route.meta.breadcrumb
				this.breadList = matched
			}
		}
	}
</script>

<style scoped>
	.active {
		color: #3278ff;
	}
</style>
