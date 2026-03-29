<script setup lang="ts">
import { computed } from 'vue'
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
    label:     '온도',
    unit:      '°C',
    icon:      'mdi-thermometer',
    colorVar:  '--col-azure',
    cardClass: 'card-temp',
  },
  {
    key:       'humidity',
    label:     '습도',
    unit:      '%',
    icon:      'mdi-water-percent',
    colorVar:  '--col-teal',
    cardClass: 'card-humi',
  },
  {
    key:       'heatIndex',
    label:     '체감온도',
    unit:      '°C',
    icon:      'mdi-sun-thermometer-outline',
    colorVar:  '--col-mist',
    cardClass: 'card-heat',
  },
]

const getValue = (key: MetricConfig['key']) =>
    props.sensorData ? props.sensorData[key] : null
</script>

<template>
  <div class="metric-grid">
    <MetricCard
        v-for="metric in METRICS"
        :key="metric.key"
        :config="metric"
        :value="getValue(metric.key)"
        :is-loading="props.isLoading"
    />
  </div>
</template>

<style scoped>
.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: clamp(0.8rem, 2.5vw, 1.5rem);
  width: 100%;
  max-width: 900px;
}

@media (max-width: 600px) {
  .metric-grid {
    grid-template-columns: 1fr;
  }
}
</style>