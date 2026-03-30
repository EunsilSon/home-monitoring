<script setup lang="ts">
import MetricCard from './MetricCard.vue'
import type { SensorData, MetricConfig } from '@/types/sensor'

interface Props {
  sensorData: SensorData | null
  isLoading:  boolean
}

const props = defineProps<Props>()

const METRICS: MetricConfig[] = [
  {
    key:       'temperature',
    label:     'Temperature',
    sublabel:  '온도',
    unit:      '°C',
    emoji:     '🌡️',
    colorVar:  '--col-azure',
    cardClass: 'card-temp',
  },
  {
    key:       'humidity',
    label:     'Humidity',
    sublabel:  '습도',
    unit:      '%',
    emoji:     '💧',
    colorVar:  '--col-teal',
    cardClass: 'card-humi',
  },
  {
    key:       'heatIndex',
    label:     'Sensory temperature',
    sublabel:  '체감 온도',
    unit:      '°C',
    emoji:     '🔆',
    colorVar:  '--col-mist',
    cardClass: 'card-heat',
  },
]

const getValue = (key: MetricConfig['key']) =>
    props.sensorData ? props.sensorData[key] : null
</script>

<template>
  <div class="metric-section">
    <div class="cards-stack">
      <MetricCard
          v-for="metric in METRICS"
          :key="metric.key"
          :config="metric"
          :value="getValue(metric.key)"
          :is-loading="props.isLoading"
      />
    </div>
  </div>
</template>

<style scoped>
.metric-section {
  width: 100%;
  margin-bottom: 4px;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: #8e8e93;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  margin-bottom: 10px;
}

.cards-stack {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>