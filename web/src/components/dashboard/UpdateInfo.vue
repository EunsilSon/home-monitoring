<script setup lang="ts">
import CountdownBar from '@/components/common/CountdownBar.vue'

interface Props {
  lastFetchedLabel: string
  countdownRatio:   number
}

defineProps<Props>()
</script>

<template>
  <div class="update-section">
    <!-- 장치 정보 -->
    <p class="section-label">장치 정보</p>
    <div class="info-card">
      <div class="info-row">
        <div class="info-row-left">
          <div class="info-icon">📡</div>
          <span class="info-key">마지막 업데이트 시각</span>
        </div>
        <span class="info-val">{{ lastFetchedLabel }}</span>
      </div>
    </div>

    <!-- 다음 업데이트 -->
    <CountdownBar :ratio="countdownRatio" />
  </div>
</template>

<style scoped>
.update-section {
  width: 100%;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: #8e8e93;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  margin-bottom: 8px;
}

/* ── Info card ── */
.info-card {
  background: #ffffff;
  border-radius: 18px;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
}

.info-row-left {
  display: flex;
  align-items: center;
  gap: 10px;
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
}

.info-val {
  font-size: 13px;
  color: #8e8e93;
  font-weight: 400;
}
</style>