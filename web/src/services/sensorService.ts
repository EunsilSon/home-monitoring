import axios from 'axios'
import type { DeviceStatus, SensorData } from '@/types/sensor'
import { API_BASE_URL, API_ENDPOINTS } from '@/constants/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10_000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const sensorService = {
  async fetchLatest(): Promise<SensorData | null> {
    const { data } = await apiClient.get<SensorData | null>(API_ENDPOINTS.SENSOR_LATEST)
    return data
  },

  async fetchDeviceStatus(): Promise<DeviceStatus> {
    const { data } = await apiClient.get<DeviceStatus>(API_ENDPOINTS.DEVICE_STATUS)
    return data
  },
}
