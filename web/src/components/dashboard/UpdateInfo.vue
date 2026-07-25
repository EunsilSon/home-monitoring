<script setup lang="ts">
import type { ConnectionStatus } from '@/composables/useSensorPolling'
import CountdownBar from '@/components/common/CountdownBar.vue'

interface Props {
  sensorStatus:     ConnectionStatus
  sensorStatusLabel: string
  lastFetchedLabel: string
  lastSeenLabel:    string
  countdownRatio:   number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'settings-click'): void
}>()
</script>

<template>
  <div class="update-section">
    <!-- 장치 정보 -->
    <div class="section-header">
      <p class="section-label">장치 정보</p>
      <button class="settings-btn" @click="emit('settings-click')" aria-label="알림 맞춤 설정">
        <span class="settings-icon">⚙</span>
        설정
      </button>
    </div>
    <div class="info-card">
      <div class="info-row">
        <div class="info-row-left">
          <div class="info-icon">📶</div>
          <span class="info-key">현재 센서 상태</span>
        </div>
        <span class="status-chip" :class="`status-chip--${props.sensorStatus}`">
          {{ props.sensorStatusLabel }}
        </span>
      </div>
      <div class="info-row">
        <div class="info-row-left">
          <div class="info-icon">📡</div>
          <span class="info-key">마지막 업데이트 시각</span>
        </div>
        <span class="info-val">{{ lastFetchedLabel }}</span>
      </div>
      <div class="info-row">
        <div class="info-row-left">
          <div class="info-icon">💓</div>
          <span class="info-key">마지막 데이터 수신 시각</span>
        </div>
        <span class="info-val">{{ lastSeenLabel }}</span>
      </div>
    </div>

    <!-- 다음 업데이트 -->
    <CountdownBar :ratio="countdownRatio" :paused="props.sensorStatus === 'offline'" />
  </div>
</template>

<style scoped>
.update-section {
  width: 100%;
  min-width: 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: #8e8e93;
  text-transform: uppercase;
  letter-spacing: 0;
}

.settings-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  border-radius: 999px;
  border: none;
  background: rgba(0, 122, 255, 0.1);
  color: #007aff;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  white-space: nowrap;
}

.settings-btn:hover {
  background: rgba(0, 122, 255, 0.18);
}

.settings-icon {
  font-size: 13px;
}

/* ── Info card ── */
.info-card {
  background: #ffffff;
  border-radius: 18px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  overflow: hidden;
  width: 100%;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 18px;
  min-width: 0;
}

.info-row + .info-row {
  border-top: 0.5px solid rgba(0, 0, 0, 0.06);
}

.info-row-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 1 1 auto;
}

.info-icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: rgba(0, 122, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.info-key {
  font-size: 15px;
  color: #1c1c1e;
  font-weight: 400;
  line-height: 1.35;
}

.info-val {
  font-size: 13px;
  color: #8e8e93;
  font-weight: 400;
  text-align: right;
  line-height: 1.35;
  overflow-wrap: anywhere;
  min-width: 0;
}

.status-chip {
  flex-shrink: 0;
  min-width: 72px;
  padding: 5px 9px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  text-align: center;
  white-space: nowrap;
}

.status-chip--idle,
.status-chip--loading,
.status-chip--unknown {
  background: rgba(142, 142, 147, 0.12);
  color: #636366;
}

.status-chip--success {
  background: rgba(52, 199, 89, 0.14);
  color: #1e7a34;
}

.status-chip--offline,
.status-chip--error {
  background: rgba(255, 59, 48, 0.12);
  color: #c0392b;
}

@media (max-width: 430px) {
  .info-row {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
    padding: 14px 16px;
  }

  .info-val {
    text-align: left;
    padding-left: 40px;
  }

  .status-chip {
    margin-left: 40px;
  }
}

@media (max-width: 330px) {
  .info-val {
    padding-left: 0;
  }

  .status-chip {
    margin-left: 0;
  }
}
</style>
