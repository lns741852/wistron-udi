<template>
  <q-input
    ref="deviceBoxInput"
    label="掃描器械盒"
    v-model.trim="scanText"
    type="search"
    dense
    outlined
    style="width: 250px"
    @keyup.enter="getInfo"
  >
    <template v-slot:append>
      <q-icon name="qr_code_scanner" />
    </template>
  </q-input>
</template>

<script>
import { apiGetBoxByCode } from '@/api';

export default {
  name: 'vGetBoxByCode',
  data() {
    return {
      scanText: '',
    };
  },
  methods: {
    async getInfo() {
      try {
        if (!this.scanText) return;
        const res = await apiGetBoxByCode({ qrcode: this.scanText });
        this.$emit('result', { qrcode: this.scanText, ...res.data });
      } catch (error) {
        const msg = error.data?.msg || '掃描包盤發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.scanText = '';
      }
    },
  },
};
</script>
