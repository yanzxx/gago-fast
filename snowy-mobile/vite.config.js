import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import postcsspxtoviewport from 'postcss-px-to-viewport'

const pathResolve = dir => {
    return resolve(__dirname, '.', dir)
}

const alias = {
    '@': pathResolve('src')
}

export default defineConfig(({ command, mode }) => {
    const envConfig = loadEnv(mode, './')
    const apiTarget = envConfig.VITE_API_BASEURL || 'http://127.0.0.1:8082'
    return {
        server: {
            port: Number(envConfig.VITE_PORT || 5174),
            proxy: {
                '/api': {
                    target: apiTarget,
                    ws: false,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, '')
                }
            }
        },
        resolve: {
            alias
        },
        plugins: [
            vue()
        ],
        css: {
            postcss: {
                plugins: [
                    postcsspxtoviewport({
                        unitToConvert: 'px', // 要转化的单位
                        viewportWidth: 100, // UI设计稿的宽度
                        unitPrecision: 6, // 转换后的精度，即小数点位数
                        propList: ['*'], // 指定转换的css属性的单位，*代表全部css属性的单位都进行转换
                        viewportUnit: 'px', // 指定需要转换成的视窗单位，默认vw
                        fontViewportUnit: 'px', // 指定字体需要转换成的视窗单位，默认vw
                        selectorBlackList: ['ignore-'], // 指定不转换为视窗单位的类名，
                        minPixelValue: 1, // 默认值1，小于或等于1px则不进行转换
                        mediaQuery: true, // 是否在媒体查询的css代码中也进行转换，默认false
                        replace: true, // 是否转换后直接更换属性值
                        exclude: [/node_modules\/vant/], // 排除 vantui
                        landscape: false // 是否处理横屏情况
                    })
                ]
            }
        }
    }
})
