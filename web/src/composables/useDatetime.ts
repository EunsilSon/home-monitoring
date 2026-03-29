import { computed } from 'vue'
import { DATE_FORMAT, DATETIME_FORMAT } from '@/constants/format'

/**
 * Date 객체를 지정된 포맷 문자열로 변환합니다.
 * 외부 라이브러리 없이 경량 구현 (dayjs 설치 시 대체 가능).
 */
function formatDate(date: Date, fmt: string): string {
  const pad = (n: number, len = 2) => String(n).padStart(len, '0')

  return fmt
      .replace('YYYY', String(date.getFullYear()))
      .replace('MM',   pad(date.getMonth() + 1))
      .replace('DD',   pad(date.getDate()))
      .replace('HH',   pad(date.getHours()))
      .replace('mm',   pad(date.getMinutes()))
      .replace('ss',   pad(date.getSeconds()))
}

export function useDatetime() {
  const today = computed(() => formatDate(new Date(), DATE_FORMAT))

  function formatDatetime(date: Date | null): string {
    if (!date) return '—'
    return formatDate(date, DATETIME_FORMAT)
  }

  function formatIso(iso: string | null | undefined): string {
    if (!iso) return '—'
    return formatDate(new Date(iso), DATETIME_FORMAT)
  }

  return { today, formatDatetime, formatIso }
}