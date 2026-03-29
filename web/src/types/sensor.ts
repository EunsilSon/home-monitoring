export interface SensorData {
  deviceId:    string
  temperature: number
  humidity:    number
  heatIndex:   number
  recordedAt:  string   // ISO 8601
  updatedAt:   string   // ISO 8601
}

export type SensorKey = 'temperature' | 'humidity' | 'heatIndex'

export interface MetricConfig {
  key:       SensorKey
  label:     string
  unit:      string
  icon:      string
  colorVar:  string
  cardClass: string
}
