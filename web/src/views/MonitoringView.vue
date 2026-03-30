<script setup lang="ts">
import { computed } from 'vue'
import DashboardHeader from '@/components/dashboard/DashboardHeader.vue'
import DashboardFooter from '@/components/dashboard/DashboardFooter.vue'
import MetricGrid      from '@/components/dashboard/MetricGrid.vue'
import UpdateInfo      from '@/components/dashboard/UpdateInfo.vue'
import StatusDot       from '@/components/common/StatusDot.vue'
import ErrorChip       from '@/components/common/ErrorChip.vue'
import { useSensorPolling } from '@/composables/useSensorPolling'
import { useDatetime }      from '@/composables/useDatetime'

const {
  data,
  status,
  errorMsg,
  lastFetched,
  countdownRatio,
  isLoading,
  isError,
  hasData,
} = useSensorPolling()

const { today, formatDatetime } = useDatetime()

const lastFetchedLabel = computed(() =>
    lastFetched.value ? formatDatetime(lastFetched.value) : '—'
)
</script>

<template>
  <div class="monitoring-view">
    <div class="scroll-area">

      <!-- Large title header -->
      <DashboardHeader :today="today" />

      <!-- Live status badge -->
      <StatusDot :status="status" class="mb-4" />

      <!-- Error -->
      <Transition name="fade">
        <ErrorChip v-if="isError" :message="errorMsg" class="mb-4" />
      </Transition>

      <!-- 온도 hero + 습도/체감온도 small grid -->
      <MetricGrid
          :sensor-data="data"
          :is-loading="isLoading"
      />

      <!-- 장치 정보 + 다음 업데이트 -->
      <div class="spacer" />
      <UpdateInfo
          v-if="hasData || isLoading || isError"
          :last-fetched-label="lastFetchedLabel"
          :countdown-ratio="countdownRatio"
      />

      <!-- Footer -->
      <DashboardFooter />
    </div>
  </div>
</template>

<style scoped>
.monitoring-view {
  min-height: 100vh;
  background: #f2f2f7;
  display: flex;
  flex-direction: column;
}

.scroll-area {
  flex: 1;
  padding: 0 20px 40px;
  max-width: 600px;
  margin: 0 auto;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.spacer { height: 20px; }

.mb-4 { margin-bottom: 16px; }

/* Fade transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>