<script setup lang="ts">
interface Props {
  message: string
}

defineProps<Props>()
</script>

<template>
  <div class="error-chip" role="alert">
    <span class="error-dot" />
    <span>{{ message }}</span>
  </div>
</template>

<style scoped>
.error-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 59, 48, 0.1);
  color: #c0392b;
  border-radius: 20px;
  padding: 5px 12px;
  font-size: 13px;
  font-weight: 500;
}

.error-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #ff3b30;
  flex-shrink: 0;
}
</style>