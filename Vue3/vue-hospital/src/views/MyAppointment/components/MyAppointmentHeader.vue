<script setup>

</script>

<template>
  <div class="page-header">
    <h1>我的预约</h1>
    <p>查看和管理您的预约记录</p>
  </div>
</template>

<style scoped lang="scss">
@use '@/styles/var.scss' as hospital-vars;

.page-header {
  background: linear-gradient(135deg, hospital-vars.$primary-color, hospital-vars.$primary-light);
  color: white;
  padding: 40px;
  border-radius: 16px;
  margin-bottom: 30px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(hospital-vars.$primary-color, 0.3);

  h1 {
    font-size: 32px;
    margin-bottom: 12px;
    font-weight: 600;
  }

  p {
    font-size: 16px;
    opacity: 0.9;
    margin: 0;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 20px;

    h1 {
      font-size: 24px;
    }
  }
}
</style>