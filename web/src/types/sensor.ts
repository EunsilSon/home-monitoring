export interface SensorData {
  deviceId:    string
  temperature: number
  humidity:    number
  heatIndex:   number
  recordedAt:  string
  updatedAt:   string
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