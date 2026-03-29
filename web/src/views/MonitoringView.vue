<script setup lang="ts">
import { computed } from 'vue'
import DashboardHeader from '@/components/dashboard/DashboardHeader.vue'
import DashboardFooter from '@/components/dashboard/DashboardFooter.vue'
import MetricGrid      from '@/components/dashboard/MetricGrid.vue'
import UpdateInfo      from '@/components/dashboard/UpdateInfo.vue'
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

    <main class="page-wrap">

      <!-- 헤더 -->
      <DashboardHeader
          :today="today"
          :status="status"
      />

      <!-- 에러 메시지 -->
      <Transition name="fade">
        <ErrorChip v-if="isError" :message="errorMsg" />
      </Transition>

      <!-- 지표 카드 그리드 -->
      <MetricGrid
          :sensor-data="data"
          :is-loading="isLoading"
      />

      <!-- 갱신 시각 + 카운트다운 바 -->
      <UpdateInfo
          v-if="hasData || isLoading"
          :last-fetched-label="lastFetchedLabel"
          :countdown-ratio="countdownRatio"
      />

    </main>

    <!-- 푸터 -->
    <DashboardFooter />
  </div>
</template>

<style scoped>
.monitoring-view {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 배경 그라디언트 레이어 */
.bg-gradient {
  position: fixed;
  inset: 0;
  background:
      radial-gradient(ellipse 80% 60% at 15% 10%,  rgba(200, 210, 230, 0.45) 0%, transparent 60%),
      radial-gradient(ellipse 60% 50% at 85% 85%,  rgba(0, 167, 194, 0.12)   0%, transparent 55%),
      radial-gradient(ellipse 50% 40% at 70% 20%,  rgba(241, 215, 165, 0.4)  0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 콘텐츠 영역 */
.page-wrap {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: clamp(1.5rem, 5vw, 3.5rem) clamp(1rem, 4vw, 2.5rem);
  gap: clamp(1.2rem, 3vw, 2rem);
}

/* 에러칩 전환 애니메이션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>