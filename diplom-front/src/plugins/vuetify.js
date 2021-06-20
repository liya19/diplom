import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader

Vue.use(Vuetify);

const opts = {
  icons: {
    iconfont: 'mdi'
  },
  theme: {
    dark: localStorage.getItem('darkTheme') === 'true',
    options: {
      // Для использования scss-переменных типа var(--v-...)
      customProperties: true
    },
    themes: {
      light: {
        // Набор цветов взят с фигмы
        primary: '#005FCF',
        secondary: '#999999',
        defaultPrimary: '#005FCF',
        defaultSecondary: '#999999',
        accent: '#CE3F37',

        info: '#00c7da',
        success: '#00bf23',
        warning: '#f1af03',
        error: '#CE3F37',

        menu1: '#F5F5F5',
        menu2: '#EBEDF1',
        menu3: '#D4D1D1',
        menu4: '#CCCCCC',

        text1: '#999999',
        text2: '#636467',
        text3: '#202123', // Добавил сам, в фигме нет

        background1: '#FFFFFF',
        background2: '#F7F8FB',
        background3: '#F8F9FC',
        background4: '#E5E5E5'
      },
      dark: {
        primary: '#3b95ff',
        secondary: '#636363'
      }
    }
  }
};

export default new Vuetify(opts)
