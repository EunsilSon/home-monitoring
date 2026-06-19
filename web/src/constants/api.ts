export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ||'';

export const API_ENDPOINTS = {
  SENSOR_LATEST: '/api/sensor/latest',
  DEVICE_STATUS: '/api/device/status',
} as const
