import { Configuration } from '@nuxt/types'

const configuration: Configuration = {
  mode: 'spa',
  srcDir: 'app',
  head: {
    titleTemplate: '%s - ' + process.env.npm_package_name,
    title: process.env.npm_package_name || '',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      {
        hid: 'description',
        name: 'description',
        content: process.env.npm_package_description || ''
      }
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }]
  },
  loading: { color: '#fff' },
  css: ['firebaseui/dist/firebaseui.css'],
  plugins: ['~/plugins/firebase', '~/plugins/vuetify'],
  modules: ['nuxt-clipboard2'],
  buildModules: ['@nuxt/typescript-build', '@nuxtjs/vuetify'],
  typescript: {
    typeCheck: {
      eslint: true
    }
  },
  env: {
    firebaseApiKey: process.env.FIREBASE_API_KEY!,
    firebaseProjectId: process.env.FIREBASE_PROJECT_ID!,
    firebaseMessagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID!
  }
}

export default configuration
