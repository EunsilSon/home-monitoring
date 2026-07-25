<script setup lang="ts">
import { ref, watch } from 'vue'
import type { ThresholdData, ThresholdRequest } from '@/types/sensor'
import { sensorService } from '@/services/sensorService'

interface Props {
  deviceId: string
  visible: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'close'): void
  (e: 'saved'): void
}>()

// Form state
const temperatureMin = ref<string>('')
const temperatureMax = ref<string>('')
const humidityMin    = ref<string>('')
const humidityMax    = ref<string>('')
const heatIndexMin   = ref<string>('')
const heatIndexMax   = ref<string>('')
const slackEnabled   = ref<boolean>(false)

const isLoading = ref(false)
const isSaving  = ref(false)
const errorMsg  = ref('')
const successMsg = ref('')
const validationErrors = ref<string[]>([])

// Fetch threshold when modal opens
watch(() => props.visible, async (newVal) => {
  if (newVal) {
    await loadThreshold()
  } else {
    resetForm()
  }
})

async function loadThreshold() {
  isLoading.value = true
  errorMsg.value = ''
  validationErrors.value = []

  try {
    const data: ThresholdData | null = await sensorService.fetchThreshold(props.deviceId)
    if (data) {
      temperatureMin.value = data.temperatureMin?.toString() ?? ''
      temperatureMax.value = data.temperatureMax?.toString() ?? ''
      humidityMin.value    = data.humidityMin?.toString() ?? ''
      humidityMax.value    = data.humidityMax?.toString() ?? ''
      heatIndexMin.value   = data.heatIndexMin?.toString() ?? ''
      heatIndexMax.value   = data.heatIndexMax?.toString() ?? ''
      slackEnabled.value   = data.slackEnabled ?? false
    }
  } catch (err: unknown) {
    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as { response?: { status?: number } }
      if (axiosErr.response?.status === 404) {
        return
      }
    }
    errorMsg.value = '알림 맞춤 설정을 불러오지 못했습니다.'
  } finally {
    isLoading.value = false
  }
}

function validate(): boolean {
  const errors: string[] = []

  const fields = [
    { label: '온도 최소', value: temperatureMin.value, max: temperatureMax.value, maxLabel: '온도 최대' },
    { label: '온도 최대', value: temperatureMax.value, min: temperatureMin.value, minLabel: '온도 최소' },
    { label: '습도 최소', value: humidityMin.value, max: humidityMax.value, maxLabel: '습도 최대' },
    { label: '습도 최대', value: humidityMax.value, min: humidityMin.value, minLabel: '습도 최소' },
    { label: '체감 온도 최소', value: heatIndexMin.value, max: heatIndexMax.value, maxLabel: '체감 온도 최대' },
    { label: '체감 온도 최대', value: heatIndexMax.value, min: heatIndexMin.value, minLabel: '체감 온도 최소' },
  ]

  for (const field of fields) {
    if (field.value.trim() === '') {
      errors.push(`${field.label} 값을 입력해주세요.`)
      continue
    }
    if (isNaN(Number(field.value))) {
      errors.push(`${field.label}은(는) 숫자여야 합니다.`)
    }
  }

  // Cross-field validation: min < max
  if (temperatureMin.value && temperatureMax.value &&
      !isNaN(Number(temperatureMin.value)) && !isNaN(Number(temperatureMax.value))) {
    if (Number(temperatureMin.value) >= Number(temperatureMax.value)) {
      errors.push('온도 최소 값은 최대 값보다 작아야 합니다.')
    }
  }
  if (humidityMin.value && humidityMax.value &&
      !isNaN(Number(humidityMin.value)) && !isNaN(Number(humidityMax.value))) {
    if (Number(humidityMin.value) >= Number(humidityMax.value)) {
      errors.push('습도 최소 값은 최대 값보다 작아야 합니다.')
    }
  }
  if (heatIndexMin.value && heatIndexMax.value &&
      !isNaN(Number(heatIndexMin.value)) && !isNaN(Number(heatIndexMax.value))) {
    if (Number(heatIndexMin.value) >= Number(heatIndexMax.value)) {
      errors.push('체감 온도 최소 값은 최대 값보다 작아야 합니다.')
    }
  }

  validationErrors.value = errors
  return errors.length === 0
}

async function handleSave() {
  successMsg.value = ''
  errorMsg.value = ''

  if (!validate()) return

  isSaving.value = true

  const request: ThresholdRequest = {
    temperatureMin: temperatureMin.value ? Number(temperatureMin.value) : null,
    temperatureMax: temperatureMax.value ? Number(temperatureMax.value) : null,
    humidityMin:    humidityMin.value    ? Number(humidityMin.value)    : null,
    humidityMax:    humidityMax.value    ? Number(humidityMax.value)    : null,
    heatIndexMin:   heatIndexMin.value   ? Number(heatIndexMin.value)   : null,
    heatIndexMax:   heatIndexMax.value   ? Number(heatIndexMax.value)   : null,
    slackEnabled:   slackEnabled.value,
  }

  try {
    await sensorService.saveThreshold(props.deviceId, request)
    successMsg.value = '알림 맞춤 설정이 저장되었습니다.'
    emit('saved')
  } catch (err: unknown) {
    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as { response?: { status?: number } }
      if (axiosErr.response?.status === 403) {
        alert('권한이 없어 알림 맞춤 설정을 저장할 수 없습니다.')
        return
      }
    }
    errorMsg.value = '저장에 실패했습니다. 다시 시도해주세요.'
  } finally {
    isSaving.value = false
  }
}

function resetForm() {
  temperatureMin.value = ''
  temperatureMax.value = ''
  humidityMin.value    = ''
  humidityMax.value    = ''
  heatIndexMin.value   = ''
  heatIndexMax.value   = ''
  slackEnabled.value   = false
  errorMsg.value       = ''
  successMsg.value     = ''
  validationErrors.value = []
}

function handleOverlayClick(e: MouseEvent) {
  if ((e.target as HTMLElement).classList.contains('modal-overlay')) {
    emit('close')
  }
}
</script>

<template>
  <Transition name="modal">
    <div v-if="visible" class="modal-overlay" @click="handleOverlayClick">
      <div class="modal-card">
        <!-- Header -->
        <div class="modal-header">
          <h2 class="modal-title">알림 맞춤 설정</h2>
          <button class="close-btn" @click="emit('close')" aria-label="닫기">&times;</button>
        </div>

        <!-- Body -->
        <div class="modal-body">
          <!-- Loading -->
          <div v-if="isLoading" class="loading-text">불러오는 중...</div>

          <!-- Form -->
          <div v-else class="form-content">
            <!-- 온도 -->
            <div class="field-group">
              <p class="field-group-label">온도</p>
              <div class="field-row">
                <div class="field">
                  <label class="field-label">최소</label>
                  <input
                      v-model="temperatureMin"
                      type="number"
                      step="0.1"
                      class="field-input"
                      placeholder="예: 15"
                  />
                </div>
                <div class="field">
                  <label class="field-label">최대</label>
                  <input
                      v-model="temperatureMax"
                      type="number"
                      step="0.1"
                      class="field-input"
                      placeholder="예: 30"
                  />
                </div>
              </div>
            </div>

            <!-- 습도 -->
            <div class="field-group">
              <p class="field-group-label">습도</p>
              <div class="field-row">
                <div class="field">
                  <label class="field-label">최소</label>
                  <input
                      v-model="humidityMin"
                      type="number"
                      step="0.1"
                      class="field-input"
                      placeholder="예: 30"
                  />
                </div>
                <div class="field">
                  <label class="field-label">최대</label>
                  <input
                      v-model="humidityMax"
                      type="number"
                      step="0.1"
                      class="field-input"
                      placeholder="예: 80"
                  />
                </div>
              </div>
            </div>

            <!-- 체감 온도 -->
            <div class="field-group">
              <p class="field-group-label">체감 온도</p>
              <div class="field-row">
                <div class="field">
                  <label class="field-label">최소</label>
                  <input
                      v-model="heatIndexMin"
                      type="number"
                      step="0.1"
                      class="field-input"
                      placeholder="예: 18"
                  />
                </div>
                <div class="field">
                  <label class="field-label">최대</label>
                  <input
                      v-model="heatIndexMax"
                      type="number"
                      step="0.1"
                      class="field-input"
                      placeholder="예: 32"
                  />
                </div>
              </div>
            </div>

            <!-- 슬랙 알림 토글 -->
            <div class="field-group">
              <p class="field-group-label">슬랙 알림</p>
              <div class="toggle-row">
                <span class="toggle-label">{{ slackEnabled ? 'ON' : 'OFF' }}</span>
                <button
                    class="toggle-btn"
                    :class="{ 'toggle-btn--on': slackEnabled }"
                    @click="slackEnabled = !slackEnabled"
                    type="button"
                    role="switch"
                    :aria-checked="slackEnabled"
                >
                  <span class="toggle-thumb" />
                </button>
              </div>
            </div>

            <!-- Validation Errors -->
            <div v-if="validationErrors.length > 0" class="validation-errors">
              <p v-for="(err, idx) in validationErrors" :key="idx" class="validation-error-item">
                {{ err }}
              </p>
            </div>

            <!-- Error -->
            <div v-if="errorMsg" class="status-msg status-msg--error">{{ errorMsg }}</div>

            <!-- Success -->
            <div v-if="successMsg" class="status-msg status-msg--success">{{ successMsg }}</div>
          </div>
        </div>

        <!-- Footer -->
        <div class="modal-footer">
          <button class="btn btn--cancel" @click="emit('close')">닫기</button>
          <button
              class="btn btn--save"
              :disabled="isSaving || isLoading"
              @click="handleSave"
          >
            {{ isSaving ? '저장 중...' : '저장' }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
/* ── Overlay ── */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  backdrop-filter: blur(2px);
}

/* ── Card ── */
.modal-card {
  background: #ffffff;
  border-radius: 20px;
  width: 100%;
  max-width: 420px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
}

/* ── Header ── */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 22px 12px;
  flex-shrink: 0;
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #1c1c1e;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.06);
  font-size: 18px;
  color: #636366;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.12);
}

/* ── Body ── */
.modal-body {
  padding: 8px 22px 20px;
  flex: 1;
  overflow-y: auto;
}

.loading-text {
  text-align: center;
  color: #8e8e93;
  font-size: 14px;
  padding: 24px 0;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* ── Field Group ── */
.field-group {
  padding: 10px 0;
}

.field-group + .field-group {
  border-top: 0.5px solid rgba(0, 0, 0, 0.06);
}

.field-group-label {
  font-size: 13px;
  font-weight: 600;
  color: #8e8e93;
  text-transform: uppercase;
  letter-spacing: 0;
  margin-bottom: 8px;
}

.field-row {
  display: flex;
  gap: 12px;
}

.field {
  flex: 1;
  min-width: 0;
}

.field-label {
  display: block;
  font-size: 12px;
  color: #8e8e93;
  margin-bottom: 4px;
}

.field-input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  border: 0.5px solid rgba(0, 0, 0, 0.12);
  background: #f9f9fb;
  font-size: 15px;
  color: #1c1c1e;
  outline: none;
  transition: border-color 0.2s, background 0.2s;
  -webkit-appearance: none;
}

.field-input:focus {
  border-color: #007aff;
  background: #ffffff;
}

.field-input::placeholder {
  color: #c7c7cc;
}

/* ── Toggle ── */
.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toggle-label {
  font-size: 15px;
  font-weight: 500;
  color: #1c1c1e;
}

.toggle-btn {
  width: 51px;
  height: 31px;
  border-radius: 999px;
  border: none;
  background: #e5e5ea;
  cursor: pointer;
  position: relative;
  transition: background 0.25s ease;
  flex-shrink: 0;
}

.toggle-btn--on {
  background: #34c759;
}

.toggle-thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 27px;
  height: 27px;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
  transition: transform 0.25s ease;
}

.toggle-btn--on .toggle-thumb {
  transform: translateX(20px);
}

/* ── Validation Errors ── */
.validation-errors {
  margin-top: 4px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 59, 48, 0.08);
}

.validation-error-item {
  font-size: 13px;
  color: #c0392b;
  line-height: 1.5;
}

.validation-error-item + .validation-error-item {
  margin-top: 2px;
}

/* ── Status Messages ── */
.status-msg {
  margin-top: 4px;
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  text-align: center;
}

.status-msg--error {
  background: rgba(255, 59, 48, 0.08);
  color: #c0392b;
}

.status-msg--success {
  background: rgba(52, 199, 89, 0.1);
  color: #1e7a34;
}

/* ── Footer ── */
.modal-footer {
  display: flex;
  gap: 10px;
  padding: 16px 22px 22px;
  flex-shrink: 0;
  border-top: 0.5px solid rgba(0, 0, 0, 0.06);
}

.btn {
  flex: 1;
  padding: 12px 0;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: background 0.2s, opacity 0.2s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn--cancel {
  background: rgba(0, 0, 0, 0.06);
  color: #636366;
}

.btn--cancel:hover:not(:disabled) {
  background: rgba(0, 0, 0, 0.1);
}

.btn--save {
  background: #007aff;
  color: #ffffff;
}

.btn--save:hover:not(:disabled) {
  background: #0066d6;
}

/* ── Modal Transition ── */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.25s ease;
}

.modal-enter-active .modal-card,
.modal-leave-active .modal-card {
  transition: transform 0.25s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-card {
  transform: scale(0.92) translateY(16px);
}

.modal-leave-to .modal-card {
  transform: scale(0.92) translateY(16px);
}

/* ── Remove number input spinners ── */
.field-input::-webkit-outer-spin-button,
.field-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.field-input[type='number'] {
  -moz-appearance: textfield;
}
</style>
