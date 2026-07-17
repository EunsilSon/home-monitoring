import { ref, onMounted, onUnmounted, computed } from 'vue'
import type { DeviceStatus, SensorData } from '@/types/sensor'
import { sensorService } from '@/services/sensorService'
import { POLLING_INTERVAL_MS, SENSOR_STALE_THRESHOLD_MS } from '@/constants/polling'

export type PollingStatus = 'idle' | 'loading' | 'success' | 'error'
export type ConnectionStatus = PollingStatus | 'offline' | 'unknown'

function isSensorData(value: SensorData | null): value is SensorData {
  return value !== null && typeof value === 'object' && 'updatedAt' in value
}

export function useSensorPolling() {
  const data        = ref<SensorData | null>(null)
  const device      = ref<DeviceStatus | null>(null)
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
      const [latestResult, deviceResult] = await Promise.allSettled([
        sensorService.fetchLatest(),
        sensorService.fetchDeviceStatus(),
      ])

      let hasResponse = false

      if (latestResult.status === 'fulfilled' && isSensorData(latestResult.value)) {
        data.value        = latestResult.value
        lastFetched.value = new Date(latestResult.value.updatedAt) // 마지막 수집 데이터
        hasResponse       = true
      }

      if (deviceResult.status === 'fulfilled') {
        device.value = deviceResult.value
        hasResponse  = true
      }

      if (!hasResponse) {
        const reason = latestResult.status === 'rejected'
          ? latestResult.reason
          : deviceResult.status === 'rejected'
            ? deviceResult.reason
            : new Error('데이터를 불러오지 못했습니다.')
        throw reason
      }

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
  const hasDevice = computed(() => device.value !== null)
  const isSensorDataStale = computed(() => {
    if (!data.value) {
      return false
    }
    return Date.now() - Date.parse(data.value.updatedAt) > SENSOR_STALE_THRESHOLD_MS
  })
  const connectionStatus = computed<ConnectionStatus>(() => {
    if (status.value === 'error') {
      return 'error'
    }
    if (!data.value) {
      return device.value?.status === 'UNKNOWN' ? 'unknown' : status.value
    }
    if (!isSensorDataStale.value) {
      return 'success'
    }
    if (device.value?.status === 'OFF' || isSensorDataStale.value) {
      return 'offline'
    }
    if (device.value?.status === 'UNKNOWN') {
      return 'unknown'
    }
    return status.value
  })

  return {
    data,
    device,
    status,
    connectionStatus,
    errorMsg,
    lastFetched,
    countdownRatio,
    isLoading,
    isError,
    hasData,
    hasDevice,
    isSensorDataStale,
    fetchData,
  }
}
