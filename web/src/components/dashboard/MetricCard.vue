<script setup lang="ts">
import { computed } from 'vue'
import type { MetricConfig } from '@/types/sensor'
import { DECIMAL_PLACES } from '@/constants/format'

interface Props {
  config:    MetricConfig
  value:     number | null
  isLoading: boolean
}

const props = defineProps<Props>()

const displayValue = computed(() => {
  if (props.value === null) return '—'
  return props.value.toFixed(DECIMAL_PLACES)
})
</script>

<template>
  <div class="metric-card" :class="props.config.cardClass">
    <!-- 배경 블롭 -->
    <div class="card-blob" />

    <!-- 왼쪽: 아이콘 + 라벨 -->
    <div class="card-left">
      <div class="card-icon">{{ props.config.emoji }}</div>
      <div class="card-text">
        <p class="card-label">{{ props.config.label }}</p>
        <p class="card-sublabel">{{ props.config.sublabel }}</p>
      </div>
    </div>

    <!-- 오른쪽: 수치 -->
    <div class="card-value-wrap">
      <span class="card-value" :class="{ refreshing: props.isLoading }">
        <span v-if="props.isLoading && props.value === null" class="skeleton" />
        <template v-else>{{ displayValue }}</template>
      </span>
      <span v-if="props.value !== null" class="card-unit">{{ props.config.unit }}</span>
    </div>
  </div>
</template>

<style scoped>
.metric-card {
  border-radius: 20px;
  padding: 20px 22px;
  height: 88px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 0.5px solid rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
  transition: transform 0.18s ease;
}
.metric-card:active { transform: scale(0.985); }

/* ── variants ── */
.card-temp { background: linear-gradient(135deg, #e8f4ff 0%, #d6ecff 100%); border-color: rgba(0,122,255,0.12); }
.card-humi { background: linear-gradient(135deg, #e0f7f0 0%, #cdf3e8 100%); border-color: rgba(0,199,140,0.12); }
.card-heat { background: linear-gradient(135deg, #fff3e4 0%, #ffe8cc 100%); border-color: rgba(255,149,0,0.12); }

/* decorative blob */
.card-blob {
  position: absolute;
  top: -20px; right: -20px;
  width: 90px; height: 90px;
  border-radius: 50%;
  opacity: 0.12;
  pointer-events: none;
}
.card-temp .card-blob { background: #007aff; }
.card-humi .card-blob { background: #34c759; }
.card-heat .card-blob { background: #ff9500; }

/* ── left side ── */
.card-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.card-icon {
  width: 42px; height: 42px;
  border-radius: 13px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.card-temp .card-icon { background: rgba(0,122,255,0.12); }
.card-humi .card-icon { background: rgba(52,199,89,0.12); }
.card-heat .card-icon { background: rgba(255,149,0,0.12); }

.card-label {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-bottom: 2px;
}
.card-temp .card-label { color: #0056cc; }
.card-humi .card-label { color: #007a5a; }
.card-heat .card-label { color: #a05000; }

.card-sublabel {
  font-size: 11px;
  color: #8e8e93;
}

/* ── right side ── */
.card-value-wrap {
  display: flex;
  align-items: baseline;
  gap: 2px;
  flex-shrink: 0;
}

.card-value {
  font-size: 36px;
  font-weight: 300;
  line-height: 1;
  letter-spacing: -1px;
  transition: opacity 0.4s ease;
}
.card-value.refreshing { opacity: 0.35; }

.card-temp .card-value { color: #0a4a9c; }
.card-humi .card-value { color: #006649; }
.card-heat .card-value { color: #8a4200; }

.card-unit {
  font-size: 16px;
  font-weight: 400;
}
.card-temp .card-unit { color: #0a4a9c; }
.card-humi .card-unit { color: #006649; }
.card-heat .card-unit { color: #8a4200; }

/* skeleton */
.skeleton {
  display: inline-block;
  width: 70px; height: 32px;
  border-radius: 6px;
  background: linear-gradient(90deg, #e8e8e8 25%, #f4f4f4 50%, #e8e8e8 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}
@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>