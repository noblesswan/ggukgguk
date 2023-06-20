const { defineConfig } = require('@vue/cli-service')
const fs = require('fs')
module.exports = defineConfig({
  transpileDependencies: true,
  pluginOptions: {
    vuetify: {
			// https://github.com/vuetifyjs/vuetify-loader/tree/next/packages/vuetify-loader
		}
  },
  devServer: {
    https: {
      key: fs.readFileSync('C:/dev/openssl/private.key'),
      cert: fs.readFileSync('C:/dev/openssl/private.crt'),
      ca: fs.readFileSync('C:/dev/openssl/rootCA.pem'),
    },
    client: {
      overlay: true,
      webSocketURL: "wss://0.0.0.0:443/ws",
    }
  },
  pwa: {
    manifestOptions: {
        name: "꾹꾹",
        short_name: "꾹꾹",
        start_url: "./",
        display: "standalone",
        theme_color: "#E0E3DA",
        background_color: "#E0E3DA",
        icons: [
            {
                "src": "img/icons/favicon-16x16.png",
                "type": "image/png",
                "sizes": "16x16",
                "purpose": "any"
            },
            {
                "src": "img/icons/favicon-32x32.png",
                "type": "image/png",
                "sizes": "32x32",
                "purpose": "any"
            },
            {
                "src": "img/icons/msapplication-icon-144x144.png",
                "type": "image/png",
                "sizes": "144x144",
                "purpose": "any"
            },
            {
                "src": "img/icons/mstile-150x150.png",
                "type": "image/png",
                "sizes": "150x150",
                "purpose": "any"
            },
            {
                "src": "img/icons/android-chrome-192x192.png",
                "type": "image/png",
                "sizes": "192x192",
                "purpose": "any"
            },
            {
                "src": "img/icons/android-chrome-512x512.png",
                "type": "image/png",
                "sizes": "512x512",
                "purpose": "any"
            },
            {
                "src": "img/icons/android-chrome-maskable-192x192.png",
                "type": "image/png",
                "sizes": "192x192",
                "purpose": "any"
            },
            {
                "src": "img/icons/android-chrome-maskable-512x512.png",
                "type": "image/png",
                "sizes": "512x512",
                "purpose": "any"
            },
            {
              "src": "img/icons/apple-touch-icon-152x152.png",
              "type": "image/png",
              "sizes": "152x152",
              "purpose": "any"
            },
            {
              "src": "img/icons/apple-touch-icon-180x180.png",
              "type": "image/png",
              "sizes": "180x180",
              "purpose": "any"
            },
        ],
    },

    themeColor: "#E0E3DA",
    msTileColor: "#E0E3DA",
    appleMobileWebAppCapable: "yes",
    appleMobileWebAppStatusBarStyle: "black",
    iconPaths: {
        favicon32: 'img/icons/favicon-32x32.png',
        favicon16: 'img/icons/favicon-16x16.png',
        appleTouchIcon: 'img/icons/apple-touch-icon-152x152.png',
        maskIcon: 'img/icons/safari-pinned-tab.svg',
        msTileImage: 'img/icons/msapplication-icon-144x144.png'
    },
  },
})