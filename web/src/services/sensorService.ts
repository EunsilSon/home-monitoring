import axios from 'axios'
import type { DeviceStatus, SensorData, ThresholdData, ThresholdRequest } from '@/types/sensor'
import { API_BASE_URL, API_ENDPOINTS, DEFAULT_DEVICE_ID } from '@/constants/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10_000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const sensorService = {
  async fetchLatest(deviceId = DEFAULT_DEVICE_ID): Promise<SensorData | null> {
    const { data } = await apiClient.get<SensorData | null>(API_ENDPOINTS.SENSOR_LATEST(deviceId))
    return data
  },

  async fetchDeviceStatus(deviceId = DEFAULT_DEVICE_ID): Promise<DeviceStatus> {
    const { data } = await apiClient.get<DeviceStatus>(API_ENDPOINTS.DEVICE_STATUS(deviceId))
    return data
  },

  async fetchThreshold(deviceId = DEFAULT_DEVICE_ID): Promise<ThresholdData | null> {
    const { data } = await apiClient.get<ThresholdData | null>(API_ENDPOINTS.THRESHOLD(deviceId))
    return data
  },

  async saveThreshold(deviceId: string, request: ThresholdRequest): Promise<ThresholdData> {
    const { data } = await apiClient.put<ThresholdData>(API_ENDPOINTS.THRESHOLD(deviceId), request)
    return data
  },
}
