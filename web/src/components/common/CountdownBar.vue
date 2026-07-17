<script setup lang="ts">
import { computed } from 'vue'
import { POLLING_INTERVAL_MS } from '@/constants/polling'

interface Props {
  ratio: number
  paused?: boolean
}

const props = defineProps<Props>()
const intervalSeconds = POLLING_INTERVAL_MS / 1000

const secondsLeft = computed(() => {
  return Math.ceil(props.ratio * intervalSeconds)
})

const progressRatio = computed(() => {
  return props.paused ? 0 : props.ratio
})
</script>

<template>
  <div class="countdown-wrap">
    <div class="countdown-label">
      <span>{{ props.paused ? '센서 오프라인' : `약 ${secondsLeft}초 후 업데이트` }}</span>
      <span>{{ props.paused ? '업데이트 대기' : '1분 주기' }}</span>
    </div>
    <div
        class="countdown-track"
        role="progressbar"
        :aria-valuenow="Math.round(progressRatio * 100)"
        aria-valuemin="0"
        aria-valuemax="100"
    >
      <div
          class="countdown-fill"
          :class="{ 'countdown-fill--paused': props.paused }"
          :style="{ width: `${progressRatio * 100}%` }"
      />
    </div>
  </div>
</template>

<style scoped>
.countdown-wrap {
  width: 100%;
}

.countdown-label {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 13px;
  color: #8e8e93;
  margin-top: 30px;
  margin-bottom: 6px;
  line-height: 1.35;
}

.countdown-track {
  height: 4px;
  background: rgba(0, 0, 0, 0.08);
  border-radius: 99px;
  overflow: hidden;
}

.countdown-fill {
  height: 100%;
  background: #007aff;
  border-radius: 99px;
  transition: width 0.9s linear;
}

.countdown-fill--paused {
  background: #8e8e93;
  transition: none;
}

@media (max-width: 360px) {
  .countdown-label {
    flex-direction: column;
    gap: 2px;
  }
}
</style>
