<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  ratio: number
}

const props = defineProps<Props>()

const secondsLeft = computed(() => {
  const INTERVAL_S = 30
  return Math.ceil(props.ratio * INTERVAL_S)
})
</script>

<template>
  <div class="countdown-wrap">
    <div class="countdown-label">
      <span>약 {{ secondsLeft }}초 후 업데이트</span>
      <span>30초 주기</span>
    </div>
    <div
        class="countdown-track"
        role="progressbar"
        :aria-valuenow="Math.round(props.ratio * 100)"
        aria-valuemin="0"
        aria-valuemax="100"
    >
      <div
          class="countdown-fill"
          :style="{ width: `${props.ratio * 100}%` }"
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
  font-size: 13px;
  color: #8e8e93;
  margin-top: 30px;
  margin-bottom: 6px;
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
</style>