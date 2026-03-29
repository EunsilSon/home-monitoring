<script setup lang="ts">
interface Props {
  message: string
}

defineProps<Props>()
</script>

<template>
  <div class="error-chip" role="alert">
    <v-icon size="14" color="#c62828">mdi-alert-circle-outline</v-icon>
    <span>{{ message }}</span>
  </div>
</template>

<style scoped>
.error-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  background: rgba(229, 115, 115, 0.1);
  color: #c62828;
  border: 1px solid rgba(229, 115, 115, 0.28);
  border-radius: 99px;
  padding: 0.3rem 0.9rem;
  font-family: var(--font-mono);
  font-size: clamp(0.62rem, 1.1vw, 0.7rem);
}
</style>
