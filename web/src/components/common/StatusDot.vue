<script setup lang="ts">
import type { PollingStatus } from '@/composables/useSensorPolling'

interface Props {
  status: PollingStatus
}

const props = defineProps<Props>()

const labelMap: Record<PollingStatus, string> = {
  idle:    '대기 중',
  loading: '업데이트 중',
  success: '실시간 연결',
  error:   '연결 오류',
}

const isLive = (s: PollingStatus) => s === 'success' || s === 'loading' || s === 'idle'
</script>

<template>
  <div class="live-badge" :class="`live-badge--${props.status}`">
    <span class="live-dot" :class="`live-dot--${props.status}`" />
    <span>LIVE · {{ labelMap[props.status] }}</span>
  </div>
</template>

<style scoped>
.live-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.04em;
  padding: 4px 10px;
  border-radius: 20px;
}

.live-badge--idle,
.live-badge--loading,
.live-badge--success {
  background: rgba(52, 199, 89, 0.12);
  color: #1e7a34;
}

.live-badge--error {
  background: rgba(255, 59, 48, 0.1);
  color: #c0392b;
}

.live-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.live-dot--idle,
.live-dot--loading,
.live-dot--success {
  background: #34c759;
  animation: livepulse 1.8s ease-in-out infinite;
}

.live-dot--error {
  background: #ff3b30;
  animation: none;
}

@keyframes livepulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50%       { opacity: 0.4; transform: scale(0.7); }
}
</style>