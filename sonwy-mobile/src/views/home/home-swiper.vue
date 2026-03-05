<template>
  <div class="home-swiper" id="home-swiper">
    <van-swipe class="my-swipe" :autoplay="3000">
      <van-swipe-item v-for="(item, index) in data" :key="index">
        <img :src="item" class="swiper-img" />
      </van-swipe-item>
    </van-swipe>
  </div>
</template>

<script setup>
	import { reactive } from "vue";
	const data = reactive(['/static/images/image1.jpeg', '/static/images/image2.jpeg'])
</script>

<style lang="scss">

.home-swiper {
  .my-swipe .van-swipe-item {
    img.swiper-img {
      height: 200px;
      width: 100%;
    }
  }
}

</style>