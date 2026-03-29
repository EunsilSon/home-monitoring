import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import { createVuetify } from 'vuetify'
import { VUETIFY_THEME } from '@/constants/theme'

export const vuetify = createVuetify({
  theme: VUETIFY_THEME,
  defaults: {
    VIcon: {
      size: 'default',
    },
  },
})