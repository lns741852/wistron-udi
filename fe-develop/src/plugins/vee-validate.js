import { Form, Field, ErrorMessage, defineRule, configure } from 'vee-validate';
import allRules from '@vee-validate/rules';
import { localize, setLocale } from '@vee-validate/i18n';
import zhTW from '@vee-validate/i18n/dist/locale/zh_TW.json';

configure({
  generateMessage: localize({ zh_TW: zhTW }),
  validateOnInput: true,
});

// 設定所有驗證規則
Object.keys(allRules).forEach((rule) => {
  defineRule(rule, allRules[rule]);
});

// 設定語系
setLocale('zh_TW');

export default function validate(app) {
  app
    .component('v-form', Form)
    .component('v-field', Field)
    .component('error-message', ErrorMessage);
}
