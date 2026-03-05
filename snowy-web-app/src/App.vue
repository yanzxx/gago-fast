<template>
    <router-view></router-view>
</template>

<script setup>
import store from '@/store';
import { onMounted } from "vue";

onMounted(() => {
  store.dispatch('GetSysBaseConfig')
})

</script>