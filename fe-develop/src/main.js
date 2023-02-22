import { createApp } from 'vue';
import { Quasar } from 'quasar';

import VueEasyLightbox from 'vue-easy-lightbox';
import VueLoading from 'vue-loading-overlay';

import { notifySuccess, notifyError } from '@/common/notifyState';
import { imagePath, currency, dateStr, generateParams, objectEquals } from '@/common/utilsFilters';
import {
  inventoryPackage,
  surgeryDevice,
  packageApply,
  sterilized,
  washing,
  surgical,
  surgicalApplyOrder,
} from '@/common/statusFilters';
import $loading from '@/common/loading';

import installValidate from './plugins/vee-validate';
import 'vue-loading-overlay/dist/vue-loading.css';

import baseComponents from './plugins/components';

import router from './router';
import store from './store';
import App from './App.vue';

import '@/router/permission';

import quasarUserOptions from './plugins/quasar-user-options';
import vueEcharts from './plugins/echarts';

const app = createApp(App);
installValidate(app);
vueEcharts(app);
baseComponents(app);

app.config.globalProperties.$gLoading = $loading;
app.config.globalProperties.$gNotifySuccess = notifySuccess;
app.config.globalProperties.$gNotifyError = notifyError;

app.config.globalProperties.$utilsFilters = {
  imagePath,
  currency,
  dateStr,
  generateParams,
  objectEquals,
};
app.config.globalProperties.$statusFilters = {
  inventoryPackage,
  surgeryDevice,
  packageApply,
  washing,
  sterilized,
  surgical,
  surgicalApplyOrder,
};
app
  .use(VueLoading)
  .use(VueEasyLightbox)
  .use(store)
  .use(router)
  .use(Quasar, quasarUserOptions)
  .mount('#app');
