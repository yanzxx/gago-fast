<template>
	<div class="work-container">
    <!-- 轮播图 -->
    <home-swiper></home-swiper>

    <!-- 宫格组件 -->
    <div v-for="(userMenu, i) in userMobileMenus" :index="i" :key="userMenu.id" style="background-color: #ffffff; margin: 6px 0;">

      <van-cell>
        <template #title>
          <i v-if="userMenu.icon && userMenu.icon.indexOf('icon') > -1" class="jiage" :class="userMenu.icon" style="font-size: 16px;" :style="{color: userMenu.color ? userMenu.color : '#1890FF'}"></i>
          <i v-else class="snowy" :class="userMenu.icon" style="font-size: 16px;color: #1890FF;" :style="{color: userMenu.color ? userMenu.color : '#1890FF'}"></i>
          <span class="custom-title" style="margin-left: 5px;">{{userMenu.name}}</span>
        </template>
        <template #value>
          <label v-for="(item,index) in allSelData[userMenu.id]" :key="index"
                 :style=" { marginLeft: '5px', color: index === (allSelData[userMenu.id].length-1) ?'#8799a3':'#1890FF'}"
                 @click="clickText(item,index, userMenu.id)">
            {{item.name + (index === (allSelData[userMenu.id].length-1) ? '' : ' | ') }}
          </label>
        </template>
      </van-cell>


      <div class="grid-body">
        <van-row>
          <van-col span="6" v-for="(item, j) in handleData(userMenu.id, userMenu.children)" :index="j"
                   :key="handleKey(item,j)" @click="gridItemClick(userMenu.id, item, j)">
            <div class="grid-item-box">
              <snowy-icon :backgroundColor="item.color" custom-prefix="snowy" :type="item.icon" size="20" color="#FFFFFF"></snowy-icon>
              <label class="text">{{item.title}}</label>
            </div>
          </van-col>
        </van-row>
      </div>
    </div>
  </div>

</template>

<script setup>
	import store from '@/store'
	import { reactive } from "vue";
  import SnowyIcon from '@/components/snowy-icon.vue';
  import HomeSwiper from '../home/home-swiper.vue';
  import router from '@/router/index.js';

	const userMobileMenus = store.getters.userMobileMenus
	// 当前选中的数据
	let selData = reactive({})
	// 所有选中的数据
	let allSelData = reactive({})
	if (userMobileMenus && userMobileMenus.length > 0) {
		userMobileMenus.forEach(item => {
			allSelData[item.id] = []
			allSelData[item.id].push(item)
			selData[item.id] = []
		})
	}
	const handleData = (userMenuId, userMenuChildren) => {
		return selData[userMenuId] && selData[userMenuId].length > 0 ? selData[userMenuId] : userMenuChildren
	}
	const handleKey = (item, j) => {
		item.key = j
		return item.key
	}
	const gridItemClick = (userMenuId, item) => {
		if (item.children && item.children.length > 0) {
			// 菜单进行更新
			item.key = item.key + 1
			selData[userMenuId] = item.children
			// 向已选中的数据中添加新的数据
			allSelData[userMenuId].push(item)
		} else if (item.menuType == 'MENU') {
      if (item.path.indexOf('org') > -1) {
        router.push({
          path: '/org'
        })
      } else if (item.path.indexOf('position') > -1) {
        router.push({
          path: '/position'
        })
      } else if (item.path.indexOf('user') > -1) {
        router.push({
          path: '/user'
        })
      } else if (item.path.indexOf('myFarm') > -1) {
        router.push({
          path: '/myFarm'
        })
      }
		}
	}
</script>
<style lang="scss">

.work-container {
  margin-left: 6px;
  margin-right: 6px;
  margin-top: 6px;

  .text {
    text-align: center;
    font-size: 13px;
    margin-top: 10px;
    color: #000;
  }

  .grid-item-box {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 15px 0;
  }

  .van-cell__title {
    text-align: left;
    display: flex;
    align-items: center;
  }

}
</style>
