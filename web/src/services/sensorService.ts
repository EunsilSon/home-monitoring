import axios from 'axios'
import type { SensorData } from '@/types/sensor'
import { API_BASE_URL, API_ENDPOINTS } from '@/constants/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10_000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const sensorService = {
  async fetchLatest(): Promise<SensorData> {
    const { data } = await apiClient.get<SensorData>(API_ENDPOINTS.SENSOR_LATEST)
    return data
  },
}