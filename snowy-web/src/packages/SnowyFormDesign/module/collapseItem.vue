<!--
 * @Description: 折叠组件
 * @Author: kcz
 * @Date: 2020-01-13 00:37:54
 * @LastEditors: kcz
 * @LastEditTime: 2020-03-28 11:32:39
 -->
<template>
	<draggable
		tag="div"
		class=""
		:list="list"
		v-bind="{
			group: { name: 'form-draggable', pull: 'clone', put: false },
			sort: false,
			animation: 180,
			ghostClass: 'moving',
			class: 'left-ul'
		}"
		item-key="key"
		@start="handleStart($event, list)"
	>
		<template #item="{ element: val, index }">
			<div class="left-ul-item" @dragstart="$emit('generateKey', list, index)">
				<div @click="$emit('handleListPush', val)">
					<svg v-if="val.icon" class="icon" aria-hidden="true">
						<use :xlink:href="`#${val.icon}`"></use>
					</svg>
					{{ val.label }}
				</div>
			</div>
		</template>
	</draggable>
</template>
<script>
	import draggable from 'vuedraggable-es'

	export default {
		name: 'CollapseItem',
		components: {
			draggable
		},
		props: ['list'],
		methods: {
			handleStart(e, list) {
				this.$emit('start', list[e.oldIndex].type)
			}
		}
	}
</script>
