<script setup lang="ts">
interface Props {
  today: string
}

defineProps<Props>()
</script>

<template>
  <header class="page-header">
    <h1 class="page-title">홈 모니터링</h1>
    <p class="page-subtitle">{{ today }}</p>
  </header>
</template>

<style scoped>
.page-header {
  padding: 12px 0 20px;
}

.page-title {
  font-size: 34px;
  font-weight: 700;
  color: #1c1c1e;
  letter-spacing: -0.5px;
  line-height: 1.1;
}

.page-subtitle {
  font-size: 13px;
  color: #8e8e93;
  margin-top: 4px;
  font-weight: 400;
}
</style>