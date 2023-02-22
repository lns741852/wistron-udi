<template>
  <q-input
    ref="scanInput"
    v-model.trim="scanText"
    type="search"
    :label="label"
    dense
    outlined
    style="width: 250px"
    @keyup.enter="getPackagesInfo"
  >
    <template v-slot:append>
      <q-icon name="qr_code_scanner" />
    </template>
  </q-input>
</template>

<script>
import { apiGetPackageByCode } from '@/api';

export default {
  name: 'queryByCode',
  props: {
    showDevices: {
      type: Boolean,
      default: false,
    },
    scanType: {
      type: String,
      default: 'qrcode',
    },
    label: {
      type: String,
      default: '掃描包盤',
    },
  },
  emits: ['result'],
  data() {
    return {
      scanText: '',
    };
  },
  methods: {
    async getPackagesInfo() {
      if (!this.scanText) return;
      try {
        const res = await apiGetPackageByCode({
          [this.scanType]: this.scanText,
          devices: this.showDevices,
        });
        this.$emit('result', {
          qrcode: this.scanText,
          ...res.data,
        });
      } catch (error) {
        const msg = error.data?.msg || '掃描發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.scanText = '';
      }
    },
  },
};
</script>
