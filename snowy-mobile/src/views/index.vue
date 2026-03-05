<template>
  <div class="layout" id="layout">
    <van-sticky>
      <van-nav-bar
          :title="currentTitle"
          left-arrow
          @click-left="onClickLeft"
          v-if="SHOWTOPBARROUTERLIST.includes(currentRoute)"
      />
    </van-sticky>
    <div class="layout-content">
      <router-view></router-view>
    </div>
    <van-tabbar
      route
      :active-color="'#1f8a70'"
      :inactive-color="'#6b8f84'"
      v-if="SHOWNAVIGATIONBARROUTERLIST.includes(currentRoute)"
    >
      <van-tabbar-item replace :to="item.path" v-for="(item, index) of BOTTOMNAVIGATIONBAR" :key="index">
        {{item.name}}
        <template #icon="props">
          <van-icon name="wap-home-o" v-if="index == 0" />
          <van-icon name="font-o" v-if="index == 1" />
          <van-icon name="chat-o" v-if="index == 2" />
          <van-icon name="manager-o" v-if="index == 3" />
        </template>
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import sysConfig from '@/config/index'
import { useRouter } from 'vue-router'
import { ref, watch } from 'vue'

const router = useRouter()
const currentRoute = ref()
const currentTitle = ref('')

watch(() => router.currentRoute.value,
    (newValue) => {
      currentRoute.value = newValue.path
      currentTitle.value = newValue.meta.title
    },
    { immediate: true }
)

const { BOTTOMNAVIGATIONBAR, SHOWNAVIGATIONBARROUTERLIST, SHOWTOPBARROUTERLIST } = sysConfig


/**
 * 返回上一级
 * */
const onClickLeft = () => {
  history.back();
}

</script>

<style lang="scss">

.layout {
  height: 100%;
  background: #f2faf8;

  .van-nav-bar__content {
    background: #1f8a70;

    .van-nav-bar__left {
      i {
        color: #fff;
      }
    }

    .van-nav-bar__title {
      color: #fff;
    }
  }

  .layout-content {
  }

  .van-tabbar {
    border-top: 1px solid rgba(31, 138, 112, 0.2);
    background: #ffffff;
  }

  .van-tabbar-item__text {
    color: #6b8f84;
  }

  .van-tabbar-item--active .van-tabbar-item__text {
    color: #1f8a70;
    font-weight: 600;
  }
}

</style>
