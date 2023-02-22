<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-white q-pa-lg shadow-1 q-mb-md" style="max-width: 900px; width: 100%">
      <div class="row justify-between items-start">
        <div class="q-pt-sm">
          <h2 class="text-h6 q-mb-md">鍋次名稱: {{ title }}</h2>
        </div>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="q-mb-md">
        <div class="q-mb-sm">
          <label class="text-subtitle2">滅菌成功<span class="text-red">*</span> </label>
          <q-radio v-model="form.isSuccess" :val="true" label="成功" name="type"></q-radio>
          <q-radio v-model="form.isSuccess" :val="false" label="失敗" name="type"></q-radio>
        </div>
        <ul class="row q-mb-lg">
          <li class="col-12">
            <p class="q-mb-xs">滅菌結束時間:</p>
            <VDateTime
              :date="form.finishDate"
              :time="form.finishTime"
              @changeDate="(val) => (form.finishDate = val)"
              @changeTime="(val) => (form.finishTime = val)"
            />
          </li>
          <li class="col-12" v-if="form.isSuccess">
            <p class="q-mb-xs">有效期限:</p>
            <VDateTime
              :date="form.expiryDate"
              :time="form.expiryTime"
              @changeDate="(val) => (form.expiryDate = val)"
              @changeTime="(val) => (form.expiryTime = val)"
            />
          </li>
          <li class="col-12 row">
            <div class="q-mr-sm">
              <p>滅菌時間</p>
              <q-input
                v-model="time.indicator"
                type="Number"
                min="1"
                max="50"
                dense
                outlined
                style="width: 210px"
              />
            </div>
            <div>
              <p>滅菌溫度</p>
              <q-input
                v-model="temperature.indicator"
                type="Number"
                min="1"
                max="200"
                dense
                outlined
                style="width: 210px"
              />
            </div>
          </li>
        </ul>

        <ul>
          <li class="row q-py-sm border-bottom text-subtitle2">
            <p class="col-3 text-weight-bold">項目</p>
            <p class="col-7 text-weight-bold">指標</p>
            <p class="col-2 text-weight-bold text-center">結果</p>
          </li>
          <li
            class="row items-center q-py-sm border-bottom"
            v-for="test in tableTest"
            :key="test.type"
          >
            <p class="col-3 text-grey text-body2 text-weight-bold" data-type="0">
              {{ test.label }}
            </p>
            <div class="col-7 text-grey">
              <q-input type="text" maxlength="20" dense outlined v-model="test.indicator" />
            </div>
            <div class="col-2 text-center ">
              <q-btn
                :outline="test.result !== 1"
                round
                unelevated
                :color="test.result === 1 ? 'secondary' : ''"
                :text-color="test.result === 1 ? 'white' : 'grey-6'"
                icon="done"
                size="10px"
                class="q-mr-xs"
                @click="toggleResult(test, 1)"
              />
              <q-btn
                :outline="test.result !== 0"
                round
                unelevated
                :color="test.result === 0 ? 'secondary' : ''"
                :text-color="test.result === 0 ? 'white' : 'grey-6'"
                icon="close"
                size="10px"
                @click="toggleResult(test, 0)"
              />
            </div>
          </li>
        </ul>
      </div>
      <div class="row justify-end">
        <q-btn
          icon="check"
          dense
          size="md"
          color="blue"
          class="q-px-md"
          @click="fireSterilizedFinish"
          >完成</q-btn
        >
      </div>
    </div>
  </q-dialog>
</template>

<script>
import { apiCreateSterilizedDone } from '@/api';
import VDateTime from '@/components/VDateTime.vue';
import { sterilizedTests } from '@/common/statusFilters';

export default {
  name: 'sterilizationFinishForm',
  components: {
    VDateTime,
  },
  props: {
    id: {
      type: Number,
      required: true,
    },
    title: String,
  },
  data() {
    return {
      showDialog: false,
      form: {
        isSuccess: true,
        finishDate: '',
        finishTime: '23:59',
        expiryDate: '',
        expiryTime: '23:59',
      },
      tests: [],
    };
  },
  computed: {
    tableTest() {
      return this.tests.filter((t) => t.type !== 1 && t.type !== 2);
    },
    time() {
      return this.tests.find((t) => t.type === 1) || {};
    },
    temperature() {
      return this.tests.find((t) => t.type === 2) || {};
    },
  },
  methods: {
    async fireSterilizedFinish() {
      this.$gLoading(true);
      try {
        const { finishDate, finishTime, expiryDate, expiryTime, isSuccess } = this.form;
        const eTime = isSuccess ? { expireTime: `${expiryDate} ${expiryTime}:59` } : {};
        const fTime = { finishTime: `${finishDate} ${finishTime}:59` };

        if (new Date(eTime) < new Date()) {
          this.$gNotifyError('有效期限時間須大於現在時間');
          return;
        }
        const result = this.checkFillMonitorItemResult();
        if (result) {
          const data = {
            ...eTime,
            ...fTime,
            id: this.id,
            isSuccess,
            monitorItems: this.tests,
          };
          await apiCreateSterilizedDone(data);
          this.$gNotifySuccess('建立滅菌完成成功');
          this.initForm();
          this.showDialog = false;
          this.$emit('sterilizationFinish', true);
        } else {
          this.$gNotifyError('請將結果填寫完整');
        }
      } catch (error) {
        const msg = error.data?.msg || '發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    checkFillMonitorItemResult() {
      return this.tests.every(({ indicator }) => {
        const memo = typeof indicator === 'number' ? String(indicator) : indicator;
        return Boolean(memo);
      });
    },
    // @param testItem 檢測項目 Object
    // @param val toggle的值(0、1) Number
    toggleResult(testItem, val) {
      const result = testItem.result === val ? '' : val;
      const idx = this.tests.findIndex((t) => t.type === testItem.type);
      this.tests[idx].result = result;
    },
    initForm() {
      this.tests = sterilizedTests();
    },
    $refInit() {
      this.initForm();
      this.showDialog = true;
    },
  },
  created() {
    this.initForm();
    this.form.finishDate = this.$utilsFilters.dateStr().slice(0, 10);
    this.form.expiryDate = this.$utilsFilters.dateStr().slice(0, 10);
  },
};
</script>
