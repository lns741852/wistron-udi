import '../styles/quasar.scss';
import lang from 'quasar/lang/zh-TW';
import '@quasar/extras/material-icons/material-icons.css';
import { Notify, Dialog } from 'quasar';

// To be used on app.use(Quasar, { ... })
export default {
  plugins: { Notify, Dialog },
  config: {
    notify: {
      position: 'top-right',
      progress: true,
    },
  },
  lang: lang,
};
