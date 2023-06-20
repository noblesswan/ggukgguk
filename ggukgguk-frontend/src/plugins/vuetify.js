// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Vuetify
import { createVuetify } from 'vuetify'

const ggukggukTheme = {
  dark: false,
  colors: {
    background: '#FFFFFF',
    surface: '#FFFFFF',
    primary: '#3AA73A',
    'primary-darken-1': '#0f2a0f',
    secondary: '#d6f2c2',
    'secondary-darken-1': '#1f591f',
    error: '#B00020',
    info: '#2196F3',
    success: '#4CAF50',
    warning: '#FB8C00',
  }
}

export default createVuetify({
  theme: {
    defaultTheme: 'ggukggukTheme',
    themes: {
      ggukggukTheme
    }
  }
})
