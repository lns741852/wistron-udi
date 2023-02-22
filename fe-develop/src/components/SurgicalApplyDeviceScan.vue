<template>
  <q-layout view="hHh Lpr lff">
    <q-drawer
      v-model="isOpen"
      side="right"
      class="bg-grey-100 q-pt-none"
      :width="650"
      overlay
      bordered
      persistent
    >
      <q-scroll-area class="fit">
        <div class="bg-dark q-pa-sm row justify-between items-center">
          <p class="text-white">包盤序號: {{ serialNo || '-' }}</p>
          <q-btn color="white" size="12" round flat icon="close" @click="closeDrawer" />
        </div>
        <div class="q-pa-md">
          <div class="row justify-between items-center q-mb-md">
            <VCheckDeviceByCode
              :devices="devices"
              @checkDeviceFinish="(idx) => (devices[idx].updateStatus = status.scanned)"
            />
            <q-btn
              color="blue"
              label="器械確認"
              type="button"
              @click="packageDeviceCheck"
              :disable="!lightCheckFinish"
            />
          </div>
          <div class="row q-col-gutter-sm">
            <div class="col-6">
              <p class="text-center q-py-sm bg-cyan-1 q-mb-sm">
                未掃描({{ unScanDevices.length }})
              </p>
              <ul>
                <q-card
                  class="q-mb-sm"
                  data-test="unScanDevices"
                  tag="li"
                  v-for="device in unScanDevices"
                  :key="device.id"
                >
                  <q-card-section>
                    <div class="row items-center">
                      <h6 class="text-body2 text-weight-bold q-mr-xs">
                        {{ device.nameScientific }}
                      </h6>
                      <q-badge
                        v-if="device.updateStatus"
                        transparent
                        align="middle"
                        class="q-pa-xs "
                        :color="`${$statusFilters.surgeryDevice(device.updateStatus)?.color}`"
                      >
                        {{ $statusFilters.surgeryDevice(device.updateStatus)?.name }}
                      </q-badge>
                    </div>

                    <p class="text-caption q-mb-sm">{{ device.name }}</p>
                    <p class="text-caption text-grey q-mb-sm">
                      {{ device.spec || '-' }} / {{ device.brand }}
                    </p>
                    <p class="text-caption text-grey">{{ device.manufactureId }}</p>

                    <div class="row items-center">
                      <p class="col text-grey text-caption">{{ device.qrcode }}</p>
                      <div class="col-auto">
                        <q-btn color="grey-7" class="text-caption" round flat icon="more_vert">
                          <q-menu cover auto-close>
                            <q-list>
                              <q-item clickable @click="changeStatus(device.id, status.unableScan)">
                                <q-item-section>無法掃描</q-item-section>
                              </q-item>
                              <q-item clickable @click="changeStatus(device.id, status.lost)">
                                <q-item-section>遺失</q-item-section>
                              </q-item>
                            </q-list>
                          </q-menu>
                        </q-btn>
                      </div>
                    </div>
                  </q-card-section>
                </q-card>
              </ul>
            </div>
            <div class="col-6">
              <p class="text-center q-py-sm bg-cyan-1 q-mb-sm">
                已掃描({{ scannedDevices.length }})
              </p>
              <ul>
                <q-card tag="li" class="q-mb-sm" v-for="device in scannedDevices" :key="device.id">
                  <q-card-section>
                    <h6 class="text-body2 text-weight-bold">{{ device.nameScientific }}</h6>
                    <p class="text-caption q-mb-sm">{{ device.name }}</p>
                    <p class="text-caption text-grey q-mb-sm">
                      {{ device.spec }} / {{ device.brand }}
                    </p>
                    <p class="text-caption text-grey">{{ device.manufactureId }}</p>
                    <div class="row items-center">
                      <p class="col text-grey text-caption">{{ device.qrcode }}</p>
                    </div>
                  </q-card-section>
                </q-card>
              </ul>
            </div>
          </div>
        </div>
      </q-scroll-area>
    </q-drawer>
  </q-layout>
</template>

<script>
import { apiGetPackageByCode, apiCreateDeviceScanCheck } from '@/api';
import VCheckDeviceByCode from '@/components/VCheckDeviceByCode.vue';

export default {
  name: 'sugicalDeviceScan',
  components: {
    VCheckDeviceByCode,
  },
  props: {
    qrcode: {
      type: String,
    },
    trackingId: {
      type: Number,
    },
  },
  data() {
    return {
      isOpen: false,
      scanDeviceText: '',
      devices: [],
      serialNo: '',
      status: {
        scanned: 3,
        unableScan: 6,
        lost: 7,
      },
    };
  },
  computed: {
    unScanDevices() {
      return this.devices.filter((device) => device.updateStatus !== this.status.scanned);
    },
    scannedDevices() {
      return this.devices.filter(
        (device) => device.updateStatus && device.updateStatus === this.status.scanned,
      );
    },
    lightCheckFinish() {
      return this.devices.every((device) => device.updateStatus);
    },
  },
  methods: {
    async packageDeviceCheck() {
      this.$gLoading(true);
      try {
        const data = this.devices.map((device) => ({
          id: device.id,
          status: device.updateStatus,
        }));
        await apiCreateDeviceScanCheck(this.trackingId, data);
        this.isOpen = false;
        this.$gNotifySuccess('清點成功');
        this.$emit('checkFinish');
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async getPackageInfo() {
      try {
        const res = await apiGetPackageByCode({ qrcode: this.qrcode, devices: true });
        const { devices, serialNo } = res.data;
        this.serialNo = serialNo;
        this.devices = devices.map((item) => ({ ...item, updateStatus: null }));
      } catch (error) {
        const msg = error.data?.msg || '取得包盤發生異常';
        this.$gNotifyError(msg);
      }
    },
    changeStatus(id, statusCode) {
      const idx = this.devices.findIndex((device) => device.id === id);
      this.devices[idx].updateStatus = statusCode;
    },
    closeDrawer() {
      this.isOpen = !this.isOpen;
      this.devices = [];
      this.$emit('closeDialog');
    },
    async generateDevices() {
      try {
        await this.getPackageInfo();
        this.isOpen = true;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      }
    },
  },
  watch: {
    qrcode(val) {
      if (val) {
        this.generateDevices();
      }
    },
  },
};
</script>
