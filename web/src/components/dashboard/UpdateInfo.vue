<script setup lang="ts">
import CountdownBar from '@/components/common/CountdownBar.vue'

interface Props {
  lastFetchedLabel: string   // 포맷된 문자열 "YYYY.MM.DD HH:mm:ss"
  countdownRatio:   number   // 1 → 0
}

defineProps<Props>()
</script>

<template>
  <div class="update-info">
    <p class="update-text">
      마지막 갱신&nbsp;
      <span class="update-time">{{ lastFetchedLabel }}</span>
    </p>
    <CountdownBar :ratio="countdownRatio" class="mt-2" />
  </div>
</template>

<style scoped>
.update-info {
  width: 100%;
  max-width: 900px;
}

.update-text {
  font-family: var(--font-mono);
  font-size: clamp(0.62rem, 1.1vw, 0.7rem);
  color: #bbb;
  letter-spacing: 0.04em;
  text-align: right;
  margin-bottom: 0.4rem;
}

.update-time {
  color: var(--col-char);
  font-weight: 500;
}
</style>
