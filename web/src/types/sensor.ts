export interface SensorData {
  deviceId:    string
  temperature: number
  humidity:    number
  heatIndex:   number
  recordedAt:  string
  updatedAt:   string
}

export type DeviceStatusValue = 'ON' | 'OFF' | 'UNKNOWN'

export interface DeviceStatus {
  deviceId:                 string
  status:                   DeviceStatusValue
  lastSeenAt:               string | null
  serverTime:               string
  offlineThresholdSeconds:  number
}

export type SensorKey = 'temperature' | 'humidity' | 'heatIndex'

export interface MetricConfig {
  key:       SensorKey
  label:     string      // 영문 라벨 (카드 상단)
  sublabel:  string      // 한글 라벨 (카드 하단)
  unit:      string
  emoji:     string
  colorVar:  string
  cardClass: string
}

export interface ThresholdData {
  deviceId:        string
  temperatureMin:  number | null
  temperatureMax:  number | null
  humidityMin:     number | null
  humidityMax:     number | null
  heatIndexMin:    number | null
  heatIndexMax:    number | null
  slackEnabled:    boolean
  createdAt:       string | null
  updatedAt:       string | null
}

export interface ThresholdRequest {
  temperatureMin:  number | null
  temperatureMax:  number | null
  humidityMin:     number | null
  humidityMax:     number | null
  heatIndexMin:    number | null
  heatIndexMax:    number | null
  slackEnabled:    boolean
}
