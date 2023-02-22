const esModules = ['quasar/lang', 'quasar/dist/quasar.esm.prod.js'].join('|');
module.exports = {
  preset: '@vue/cli-plugin-unit-jest',
  transform: {
    '^.+\\.vue$': 'vue-jest',
    'vee-validate/dist/rules': 'babel-jest',
  },
  transformIgnorePatterns: [`node_modules/(?!(${esModules}))`],
};

// transformIgnorePatterns: ['/node_modules/'],
