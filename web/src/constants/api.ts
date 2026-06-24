export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''
export const DEFAULT_DEVICE_ID = import.meta.env.VITE_DEVICE_ID || '220aa852-ee70-4105-8cf5-98c23cb5e631'

export const API_ENDPOINTS = {
  SENSOR_LATEST: (deviceId: string) => `/api/sensor/${deviceId}/latest`,
  DEVICE_STATUS: (deviceId: string) => `/api/device/${deviceId}/status`,
} as const
