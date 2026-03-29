export const COLORS = {
  blush: '#e9cbd1',
  sand:  '#f1d7a5',
  azure: '#368dd2',
  mist:  '#c0d0e6',
  char:  '#373737',
  ice:   '#d3e1ee',
  teal:  '#00a7c2',
} as const

/** 지표별 accent 색상 */
export const METRIC_COLORS = {
  temperature: COLORS.azure,
  humidity:    COLORS.teal,
  heatIndex:   '#4a7ea8',  // mist 계열 진하게
} as const

export const VUETIFY_THEME = {
  defaultTheme: 'monitoring',
  themes: {
    monitoring: {
      dark: false,
      colors: {
        primary:    COLORS.azure,
        secondary:  COLORS.teal,
        accent:     COLORS.sand,
        background: '#f4f0ed',
        surface:    '#ffffff',
        error:      '#e57373',
        info:       COLORS.mist,
      },
    },
  },
} as const
