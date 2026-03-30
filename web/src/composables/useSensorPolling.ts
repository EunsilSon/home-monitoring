import { ref, onMounted, onUnmounted, computed } from 'vue'
import type { SensorData } from '@/types/sensor'
import { sensorService } from '@/services/sensorService'
import { POLLING_INTERVAL_MS } from '@/constants/polling'

export type PollingStatus = 'idle' | 'loading' | 'success' | 'error'

export function useSensorPolling() {
  const data        = ref<SensorData | null>(null)
  const status      = ref<PollingStatus>('idle')
  const errorMsg    = ref<string>('')
  const lastFetched = ref<Date | null>(null)

  /** 다음 업데이트까지 남은 비율 (1 → 0) */
  const countdownRatio = ref<number>(1)

  let pollingTimer:   ReturnType<typeof setTimeout>   | null = null
  let countdownTimer: ReturnType<typeof setInterval>  | null = null
  let fetchStartedAt  = 0

  // ── countdown ticker ──────────────────────────────────────────
  function startCountdown() {
    stopCountdown()
    fetchStartedAt = Date.now()
    countdownRatio.value = 1

    countdownTimer = setInterval(() => {
      const elapsed = Date.now() - fetchStartedAt
      countdownRatio.value = Math.max(0, 1 - elapsed / POLLING_INTERVAL_MS)
    }, 500)
  }

  function stopCountdown() {
    if (countdownTimer !== null) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }

  // ── fetch ─────────────────────────────────────────────────────
  async function fetchData() {
    status.value  = 'loading'
    errorMsg.value = ''

    try {
      data.value        = await sensorService.fetchLatest()
      lastFetched.value = new Date()
      status.value      = 'success'
    } catch (err) {
      status.value  = 'error'
      errorMsg.value = err instanceof Error ? err.message : '데이터를 불러오지 못했습니다.'
    } finally {
      startCountdown()
      scheduleNext()
    }
  }

  function scheduleNext() {
    if (pollingTimer !== null) clearTimeout(pollingTimer)
    pollingTimer = setTimeout(fetchData, POLLING_INTERVAL_MS)
  }

  function stopPolling() {
    if (pollingTimer !== null) { clearTimeout(pollingTimer); pollingTimer = null }
    stopCountdown()
  }

  // ── lifecycle ─────────────────────────────────────────────────
  onMounted(() => fetchData())
  onUnmounted(() => stopPolling())

  // ── computed ──────────────────────────────────────────────────
  const isLoading = computed(() => status.value === 'loading')
  const isError   = computed(() => status.value === 'error')
  const hasData   = computed(() => data.value !== null)

  return {
    data,
    status,
    errorMsg,
    lastFetched,
    countdownRatio,
    isLoading,
    isError,
    hasData,
    fetchData,
  }
}