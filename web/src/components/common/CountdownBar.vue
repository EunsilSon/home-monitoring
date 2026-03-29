<script setup lang="ts">
interface Props {
  /** 1(막 갱신됨) → 0(다음 갱신 직전) */
  ratio: number
}

const props = defineProps<Props>()
</script>

<template>
  <div class="countdown-track" role="progressbar" :aria-valuenow="Math.round(props.ratio * 100)" aria-valuemin="0" aria-valuemax="100">
    <div
      class="countdown-bar"
      :style="{ width: `${props.ratio * 100}%` }"
    />
  </div>
</template>

<style scoped>
.countdown-track {
  width: 100%;
  height: 3px;
  background: rgba(0, 0, 0, 0.06);
  border-radius: 99px;
  overflow: hidden;
}

.countdown-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--col-teal), var(--col-azure));
  border-radius: 99px;
  transition: width 0.9s linear;
}
</style>
