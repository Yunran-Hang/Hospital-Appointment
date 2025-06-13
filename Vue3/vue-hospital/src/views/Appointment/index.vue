<script setup>
import Banner from './components/Banner.vue';
import Serach from './components/Serach.vue';
</script>

<template>
  <div class="home-container">
    <Banner/>
    <Serach/>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>