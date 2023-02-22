module.exports = {
  publicPath: '/',

  chainWebpack: (config) => {
    config.performance.set('hints', false);
  },

  pluginOptions: {
    quasar: {
      importStrategy: 'kebab',
      rtlSupport: false,
    },
  },

  transpileDependencies: ['quasar'],
};
