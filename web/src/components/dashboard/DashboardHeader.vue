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
  padding: 8px 0 18px;
}

.page-title {
  font-size: 30px;
  font-weight: 700;
  color: #1c1c1e;
  letter-spacing: 0;
  line-height: 1.1;
  overflow-wrap: anywhere;
}

.page-subtitle {
  font-size: 15px;
  color: #8e8e93;
  margin-top: 4px;
  font-weight: 400;
  line-height: 1.4;
}

@media (min-width: 768px) {
  .page-header {
    padding: 0 0 22px;
  }

  .page-title {
    font-size: 34px;
  }
}
</style>
