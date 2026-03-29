<script setup lang="ts">
import type { PollingStatus } from '@/composables/useSensorPolling'

interface Props {
  status: PollingStatus
}

const props = defineProps<Props>()

const labelMap: Record<PollingStatus, string> = {
  idle:    '대기 중',
  loading: '갱신 중',
  success: '실시간 연결',
  error:   '연결 오류',
}
</script>

<template>
  <span class="status-dot-wrap">
    <span
        class="status-dot"
        :class="`status-dot--${props.status}`"
        :aria-label="labelMap[props.status]"
    />
    <span class="status-label">{{ labelMap[props.status] }}</span>
  </span>
</template>

<style scoped>
.status-dot-wrap {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-family: var(--font-mono);
  font-size: clamp(0.62rem, 1.1vw, 0.72rem);
  color: #999;
  letter-spacing: 0.04em;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-dot--idle,
.status-dot--loading {
  background: var(--col-sand);
  animation: pulse-dot 1.4s ease-in-out infinite;
}

.status-dot--success {
  background: var(--col-teal);
  animation: pulse-dot 2.2s ease-in-out infinite;
}

.status-dot--error {
  background: #e57373;
  animation: none;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1;  transform: scale(1); }
  50%       { opacity: 0.45; transform: scale(0.7); }
}
</style>