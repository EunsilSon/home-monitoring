<script setup lang="ts">
import { computed } from 'vue'
import type { MetricConfig } from '@/types/sensor'
import { DECIMAL_PLACES } from '@/constants/format'

interface Props {
  config:     MetricConfig
  value:      number | null
  isLoading:  boolean
}

const props = defineProps<Props>()

const displayValue = computed(() => {
  if (props.value === null) return '—'
  return props.value.toFixed(DECIMAL_PLACES)
})
</script>

<template>
  <div
      class="metric-card"
      :class="props.config.cardClass"
  >
    <!-- decorative blob (CSS ::before 보조) -->
    <div class="card-blob" />

    <!-- icon -->
    <div class="card-icon">
      <v-icon :icon="props.config.icon" />
    </div>

    <!-- label -->
    <p class="card-label">{{ props.config.label }}</p>

    <!-- value -->
    <div class="card-value-row">
      <span
          class="card-value"
          :class="{ refreshing: props.isLoading }"
      >
        <template v-if="props.isLoading && props.value === null">
          <span class="skeleton skeleton-value" />
        </template>
        <template v-else>
          {{ displayValue }}
        </template>
      </span>
      <span v-if="props.value !== null" class="card-unit">{{ props.config.unit }}</span>
    </div>
  </div>
</template>

<style scoped>
.metric-card {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: clamp(1.2rem, 3vw, 1.8rem) clamp(1rem, 2.5vw, 1.5rem);
  border: 1.5px solid rgba(255, 255, 255, 0.85);
  position: relative;
  overflow: hidden;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.metric-card:hover {
  transform: translateY(-3px);
}

/* ── card variants ── */
.card-temp {
  box-shadow: 0 8px 28px rgba(54, 141, 210, 0.14);
}
.card-temp .card-icon  { color: var(--col-azure); }
.card-temp .card-value { color: var(--col-azure); }

.card-humi {
  box-shadow: 0 8px 28px rgba(0, 167, 194, 0.12);
}
.card-humi .card-icon  { color: var(--col-teal); }
.card-humi .card-value { color: var(--col-teal); }

.card-heat {
  background: rgba(211, 225, 238, 0.55);
  box-shadow: 0 8px 28px rgba(192, 208, 230, 0.35);
}
.card-heat .card-icon  { color: #4a7ea8; }
.card-heat .card-value { color: #2d6090; }

/* ── decorative blob ── */
.card-blob {
  position: absolute;
  top: -30px;
  right: -30px;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  opacity: 0.13;
  pointer-events: none;
}
.card-temp .card-blob { background: var(--col-azure); }
.card-humi .card-blob { background: var(--col-teal); }
.card-heat .card-blob { background: #7fa8c8; }

/* ── inner elements ── */
.card-icon {
  font-size: clamp(1.4rem, 3vw, 1.8rem);
  margin-bottom: 0.5rem;
}

.card-label {
  font-family: var(--font-body);
  font-size: clamp(0.62rem, 1.2vw, 0.72rem);
  font-weight: 500;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #999;
  margin-bottom: 0.3rem;
}

.card-value-row {
  display: flex;
  align-items: baseline;
  gap: 0.15em;
}

.card-value {
  font-family: var(--font-mono);
  font-size: clamp(2rem, 5.5vw, 3.2rem);
  font-weight: 500;
  line-height: 1;
  transition: opacity 0.4s ease;
}

.card-value.refreshing {
  opacity: 0.4;
}

.card-unit {
  font-family: var(--font-mono);
  font-size: clamp(0.75rem, 1.6vw, 0.9rem);
  color: #aaa;
}

/* ── skeleton ── */
.skeleton-value {
  display: inline-block;
  width: 90px;
  height: 2.8rem;
  border-radius: 8px;
  background: linear-gradient(90deg, #e8e8e8 25%, #f4f4f4 50%, #e8e8e8 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}

@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>