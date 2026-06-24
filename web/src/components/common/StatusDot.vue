<script setup lang="ts">
import type { ConnectionStatus } from '@/composables/useSensorPolling'

interface Props {
  status: ConnectionStatus
}

const props = defineProps<Props>()

const labelMap: Record<ConnectionStatus, string> = {
  idle:    '대기 중',
  loading: '업데이트 중',
  success: '실시간 연결',
  error:   '연결 오류',
  offline: '센서 오프라인',
  unknown: '상태 확인 중',
}

const prefixMap: Record<ConnectionStatus, string> = {
  idle:    'CHECK',
  loading: 'SYNC',
  success: 'ON',
  error:   'ERROR',
  offline: 'OFF',
  unknown: 'UNKNOWN',
}
</script>

<template>
  <div class="live-badge" :class="`live-badge--${props.status}`">
    <span class="live-dot" :class="`live-dot--${props.status}`" />
    <span>{{ prefixMap[props.status] }} · {{ labelMap[props.status] }}</span>
  </div>
</template>

<style scoped>
.live-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0;
  padding: 4px 10px;
  border-radius: 20px;
  line-height: 1.35;
  max-width: 100%;
  overflow-wrap: anywhere;
}

.live-badge--idle,
.live-badge--loading,
.live-badge--success {
  background: rgba(52, 199, 89, 0.12);
  color: #1e7a34;
}

.live-badge--unknown {
  background: rgba(142, 142, 147, 0.12);
  color: #636366;
}

.live-badge--error,
.live-badge--offline {
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

.live-dot--unknown {
  background: #8e8e93;
  animation: none;
}

.live-dot--error,
.live-dot--offline {
  background: #ff3b30;
  animation: none;
}

@keyframes livepulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50%       { opacity: 0.4; transform: scale(0.7); }
}
</style>
