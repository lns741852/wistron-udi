<template>
  <q-input
    ref="deviceInput"
    label="掃描器械"
    v-model="scanText"
    type="search"
    dense
    outlined
    style="width: 200px"
    @keyup.enter="checktDeviceQrcode"
  >
    <template v-slot:append>
      <q-icon name="qr_code_scanner" />
    </template>
  </q-input>
</template>

<script>
import { apiGetDeviceItem } from '@/api';

export default {
  name: 'VCheckDeviceByCode',
  props: {
    devices: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      scanText: '',
    };
  },
  methods: {
    async checktDeviceQrcode() {
      const idx = this.devices.findIndex((device) => device.qrcode === this.scanText);
      if (idx !== -1) {
        this.$emit('checkDeviceFinish', idx);
      } else {
        const info = await this.getDeviceInfo(this.scanText);
        this.$gNotifyError(info || '非該盤器械');
      }
      this.scanText = '';
    },
    async getDeviceInfo(qrcode) {
      try {
        const res = await apiGetDeviceItem({ qrcode });
        return `為${res.data.packageSerialNo}之器械`;
      } catch (error) {
        return '查無器械相關資訊';
      }
    },
  },
};
</script>
