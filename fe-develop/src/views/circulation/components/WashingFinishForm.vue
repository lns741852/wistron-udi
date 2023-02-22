<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-white q-pa-lg shadow-1 q-mb-md">
      <div class="row justify-between items-start">
        <div class="q-pt-sm">
          <h2 class="text-h6 q-mb-md">批次名稱: {{ title }}</h2>
        </div>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="q-mb-md">
        <div class="q-mb-sm">
          <label class="text-subtitle2">清洗成功<span class="text-red">*</span> </label>
          <q-radio v-model="form.isSuccess" :val="true" label="成功" name="type"></q-radio>
          <q-radio v-model="form.isSuccess" :val="false" label="失敗" name="type"></q-radio>
        </div>
      </div>
      <div class="row justify-end">
        <q-btn
          v-if="form.date && form.time"
          icon="check"
          dense
          size="md"
          color="secondary"
          class="q-px-md"
          @click="fireWashingFinish"
          >完成</q-btn
        >
      </div>
    </div>
  </q-dialog>
</template>

<script>
import { apiCreateWashingDone } from '@/api';

export default {
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
        date: '',
        time: '23:59',
      },
    };
  },
  methods: {
    async fireWashingFinish() {
      this.$gLoading(true);
      try {
        const { isSuccess } = this.form;
        const params = `id=${this.id}&isSuccess=${isSuccess}`;
        await apiCreateWashingDone(params);
        this.$gNotifySuccess('建立清洗完成成功');
        this.showDialog = false;

        this.$emit('washingFinish', true);
      } catch (error) {
        const msg = error.data?.msg || '發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
  },
  created() {
    this.form.date = this.$utilsFilters.dateStr().slice(0, 10);
  },
};
</script>
