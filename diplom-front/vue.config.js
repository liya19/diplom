module.exports = {
    transpileDependencies: [
        'vuetify'
    ],

    chainWebpack: config => {
        config.optimization.minimize(false)
        config.optimization.splitChunks(false)
        config.optimization.concatenateModules(false)
        config.optimization.runtimeChunk('single')
        config.optimization.namedChunks(true)
        config.optimization.namedModules(true)

        // config.optimization.delete('splitChunks')
    },

    devServer: {
        port: 8000,
        https: false,
        disableHostCheck: true,
        host: 'localhost'
    }
};
