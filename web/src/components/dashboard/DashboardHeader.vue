<script setup lang="ts">
import StatusDot from '@/components/common/StatusDot.vue'
import type { PollingStatus } from '@/composables/useSensorPolling'

interface Props {
  today:  string
  status: PollingStatus
}

defineProps<Props>()
</script>

<template>
  <header class="dashboard-header">
    <p class="eyebrow">Real-time Environmental Monitoring</p>
    <h1 class="title">환경 모니터링</h1>
    <p class="date">{{ today }}</p>
    <div class="status-row">
      <StatusDot :status="status" />
    </div>
  </header>
</template>

<style scoped>
.dashboard-header {
  text-align: center;
}

.eyebrow {
  font-family: var(--font-mono);
  font-size: clamp(0.62rem, 1.1vw, 0.72rem);
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--col-teal);
  margin-bottom: 0.35rem;
}

.title {
  font-family: var(--font-display);
  font-size: clamp(1.6rem, 4vw, 2.6rem);
  color: var(--col-char);
  line-height: 1.15;
}

.date {
  font-family: var(--font-mono);
  font-size: clamp(0.72rem, 1.3vw, 0.82rem);
  color: #aaa;
  margin-top: 0.4rem;
  letter-spacing: 0.06em;
}

.status-row {
  display: flex;
  justify-content: center;
  margin-top: 0.75rem;
}
</style>