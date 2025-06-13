<script setup>
import MyAppointmentHeader from './components/MyAppointmentHeader.vue'
import MyAppointmentContainer from './components/MyAppointmentContainer.vue';

</script>

<template>
  <div class="my-appointment-container">
    <MyAppointmentHeader />
    <MyAppointmentContainer />
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/var.scss' as hospital-vars;

.my-appointment-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: hospital-vars.$bg-color; // 使用别名引用
}
@media (max-width: 768px) {
  .my-appointment-container {
    padding: 16px;
  }
}

</style>