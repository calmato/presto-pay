import { Configuration } from '@nuxt/types'

const configuration: Configuration = {
  mode: 'spa',
  srcDir: 'app',
  head: {
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
  plugins: [],
  buildModules: ['@nuxt/typescript-build'],
  typescript: {
    typeCheck: {
      eslint: true
    }
  }
}

export default configuration
