<template>
  <VBreadCrumb backTitle="配盤區" />
  <SwitchTabs ref="switchTabs" :tabList="tabList">
    <template #content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <div class="q-pa-lg">
          <div class="row items-center q-mb-lg">
            <VGetPackagesByCode
              class="q-mr-sm"
              label="掃描標牌"
              scanType="packageCode"
              :showDevices="true"
              @result="getPackageInfo"
            />
            <div v-if="packageCode" class="row items-center ">
              <p class="text-green text-body2 q-mr-sm">{{ packageCode }}</p>
              <q-icon name="check_circle" color="green" size="24px" />
            </div>
          </div>
          <div class="row items-center">
            <VGetBoxByCode class="q-mr-sm" @result="getDeviceBoxInfo" />
            <div v-if="deviceBoxQrcode" class="row items-center ">
              <p class="text-green text-body2  q-mr-sm">{{ deviceBoxQrcode }}</p>
              <q-icon name="check_circle" color="green" size="24px" />
            </div>
          </div>
        </div>
        <q-separator />
        <div class="flex justify-end q-px-lg q-py-md">
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="下一步"
            :disable="!deviceBoxQrcode || !packageCode"
            @click="slotProps.handleForwardTab"
          />
        </div>
      </div>
      <div
        v-show="slotProps.currentTabNumber === 2 || slotProps.currentTabNumber === 3"
        class="q-pa-md"
      >
        <div class="row q-col-gutter-lg">
          <div class="col-12 col-md-9" v-show="slotProps.currentTabNumber === 2">
            <div class="row q-col-gutter-lg">
              <div class="col-6">
                <RewrapPackagesChannel
                  title="未掃描"
                  inputLabel="掃描器械"
                  :devices="firstChannelDevices"
                  :showDeviceBadge="false"
                  @changeStatus="changeStatus"
                  @scanText="(val) => dispatchScanDeviceStatus('receive', val)"
                >
                  <template #btnToggle>
                    <q-btn-toggle
                      v-model="deviceScanMode"
                      no-caps
                      rounded
                      unelevated
                      toggle-color="secondary"
                      color="white"
                      text-color="grey-2"
                      size="8px"
                      :options="[
                        { slot: 'one', value: status.receive },
                        { slot: 'two', value: status.replace },
                      ]"
                    >
                      <template v-slot:one>
                        <q-icon name="qr_code_scanner" />
                      </template>
                      <template v-slot:two>
                        <q-icon name="construction" />
                      </template>
                    </q-btn-toggle>
                  </template>
                </RewrapPackagesChannel>
              </div>
              <div class="col-6">
                <RewrapPackagesChannel
                  title="已掃描"
                  inputLabel="移除器械"
                  :devices="dScanned"
                  :showDeviceBadge="false"
                  @scanText="(val) => deleteDevice(val)"
                />
              </div>
            </div>
          </div>

          <div class="col-12 col-md-9" v-show="slotProps.currentTabNumber === 3">
            <div class="row q-col-gutter-lg">
              <div class="col-6">
                <RewrapPackagesChannel
                  title="待替換"
                  inputLabel="掃描器械"
                  :devices="dAbnormal"
                  @scanText="(val) => checktDeviceQrcode(val)"
                />
              </div>
              <div class="col-6">
                <RewrapPackagesChannel
                  title="已掃描"
                  inputLabel="移除器械"
                  :devices="replaceDevices"
                  :showDeviceBadge="false"
                  @scanText="(val) => deleteDevice(val)"
                />
              </div>
            </div>
          </div>

          <div class="col-12 col-md-3">
            <q-card class="q-pa-md q-mb-md">
              <p class="text-weight-bold text-body2">包盤資訊</p>
              <div class="text-subtitle2">
                <p>{{ $store.getters.divisionById(packageMain.divisionId)?.name }}</p>
                <p>{{ packageMain.configCode }}</p>
                <p>{{ packageMain.configName }}</p>
              </div>
            </q-card>
            <VCircularProgress title="器械" :progress="packingProgress" />
            <q-btn
              v-if="dAbnormalWaitChange.length > 0 && slotProps.currentTabNumber === 2"
              label="更換器械"
              color="blue"
              class="full-width"
              @click="slotProps.handleForwardTab"
              :disable="dUnscannable.length > 0"
            />
            <q-btn
              v-else
              label="重新打包"
              color="blue"
              class="full-width"
              @click="repackSubmit({ step: slotProps.currentTabNumber === 2 ? 2 : 1 })"
              :disable="!ableSubmit"
            />
          </div>
        </div>
      </div>
    </template>
  </SwitchTabs>
</template>

<script>
import { apiGetDeviceItem, apiReWrapPackage } from '@/api';
import SwitchTabs from '@/components/SwitchTabs.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import VCircularProgress from '@/components/VCircularProgress.vue';
import VGetPackagesByCode from '@/components/VGetPackagesByCode.vue';
import VGetBoxByCode from '@/components/VGetBoxByCode.vue';
import RewrapPackagesChannel from './components/RewrapPackagesChannel.vue';

export default {
  name: 'repack',
  components: {
    SwitchTabs,
    VBreadCrumb,
    VCircularProgress,
    VGetBoxByCode,
    VGetPackagesByCode,
    RewrapPackagesChannel,
  },
  data() {
    return {
      deviceScanMode: this.getDeviceStatus('RECEIVE_SCAN_DONE')?.status,
      tabList: [
        {
          name: 'scanPackage',
          title: '掃描包盤',
        },
        {
          name: 'scanDevices',
          title: '清點器械',
        },
        {
          name: 'changeDevice',
          title: '更換器械',
        },
      ],
      packageMain: {},
      devices: [],
      replaceDevices: [],
      packageCode: '',
      deviceBoxQrcode: '',
      status: {
        normal: this.getDeviceStatus('NORMAL')?.status,
        replace: this.getDeviceStatus('REPLACED')?.status,
        receive: this.getDeviceStatus('RECEIVE_SCAN_DONE')?.status,
        unscannable: this.getDeviceStatus('UNSCANABLE')?.status,
        missing: this.getDeviceStatus('MISSING')?.status,
      },
    };
  },
  computed: {
    dOriginReceiveStatus() {
      return this.devices.filter(({ status }) => {
        const { receive } = this.status;
        return status === receive;
      });
    },
    dUnscannable() {
      return this.devices.filter(({ status, updateStatus, newDeviceId }) => {
        const { receive } = this.status;
        return status === receive && updateStatus === receive && !newDeviceId;
      });
    },
    dScanned() {
      return this.devices.filter(({ status, updateStatus, newDeviceId }) => {
        const { receive } = this.status;
        return status === receive && updateStatus === receive && newDeviceId;
      });
    },
    dAbnormal() {
      return this.devices.filter(({ updateStatus }) => {
        const { unscannable, missing, replace } = this.status;
        return updateStatus === unscannable || updateStatus === missing || updateStatus === replace;
      });
    },
    dAbnormalWaitChange() {
      const { receive } = this.status;
      return this.devices.filter((item) => item.updateStatus !== receive && !item.newDeviceId);
    },
    dAlreadyPack() {
      return [...this.dScanned, ...this.replaceDevices];
    },
    ableSubmit() {
      return this.devices.every((d) => !!d.newDeviceId) && this.deviceBoxQrcode;
    },
    packingProgress() {
      return {
        total: this.devices.length,
        current: this.dAlreadyPack.length,
      };
    },
    uncheckPackLength() {
      return this.dUnscannable.length + this.dAbnormal.length;
    },
    firstChannelDevices() {
      const unscannable = this.dUnscannable.map((device) => ({
        ...device,
        hasAddOnButton: true,
        showDeviceBadge: device.updateStatus === 3 && !!device.newDeviceId,
      }));
      const abnormal = this.dAbnormal.map((device) => ({
        ...device,
        hasAddOnButton: device.status === this.status.receive,
        showDeviceBadge: true,
      }));
      return [...unscannable, ...abnormal];
    },
  },
  methods: {
    async repackSubmit({ step }) {
      this.$gLoading(true);
      try {
        const devices = this.devices
          .filter((item) => item.updateStatus === this.status.receive)
          .map((d) => d.id);
        const replacedDevices = this.devices
          .filter((item) => item.updateStatus !== this.status.receive)
          .map((d) => ({
            id: d.id,
            status: d.updateStatus,
            newDeviceId: d.newDeviceId,
          }));
        const data = {
          deviceBoxQrcode: this.deviceBoxQrcode,
          devices,
          replacedDevices,
        };
        await apiReWrapPackage(this.packageMain.id, data);
        this.$gNotifySuccess('重新打包成功');
        this.repackReset();
        this.$refs.switchTabs.handleForwardTab();
        if (step === 2) {
          this.$refs.switchTabs.handleForwardTab();
        }
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    dispatchScanDeviceStatus(mode, qrcode) {
      if (mode === 'receive') {
        const idx = this.dOriginReceiveStatus.findIndex((device) => device.qrcode === qrcode);
        if (idx === -1) return;
        this.checktDeviceQrcode(qrcode);
      }
    },
    async checktDeviceQrcode(scanCode) {
      const idx = this.devices.findIndex((device) => device.qrcode === scanCode);
      const currentDevice = this.devices[idx];
      if (idx !== -1 && currentDevice.status === this.status.receive) {
        if (this.deviceScanMode === this.status.receive) {
          currentDevice.newDeviceId = currentDevice.id;
        } else {
          currentDevice.newDeviceId = '';
        }
        currentDevice.updateStatus = this.deviceScanMode;
      } else {
        this.switchPackagesDevice(scanCode);
      }
    },
    async switchPackagesDevice(qrcode) {
      try {
        const res = await apiGetDeviceItem({ qrcode });
        const { deviceId, status, typeId, division } = res.data;
        const typeWaitChangeList = this.dAbnormalWaitChange.filter(
          (item) => item.typeId === typeId,
        );
        if (typeWaitChangeList.length === 0) {
          this.$gNotifyError('該類型器械非此器械所需或所有');
        } else if (status !== this.status.normal && status !== this.status.replace) {
          this.$gNotifyError(`系統記錄該器械為${this.getDeviceStatus(status)?.name}狀態`);
        } else if (division !== this.packageMain.divisionId) {
          this.$gNotifyError(
            `該類型器械非${this.$store.getters.divisionById(this.packageMain.divisionId)?.name}`,
          );
        } else {
          const firstDevice = typeWaitChangeList[0];
          const idx = this.devices.findIndex((d) => d.id === firstDevice.id);
          if (firstDevice.status === this.status.normal) {
            this.devices[idx].updateStatus = firstDevice.status;
          }
          this.devices[idx].newDeviceId = deviceId;
          this.replaceDevices.push({ ...res.data, replaceOriginId: this.devices[idx].id });
        }
      } catch (error) {
        const msg = error.data?.msg || '掃描器械發生異常!';
        this.$gNotifyError(msg);
      }
    },
    getPackageInfo(result) {
      const { devices, status, qrcode, ...rest } = result;
      const inventoryStatus = this.$statusFilters.inventoryPackage(status)?.value;
      if (inventoryStatus === 'WASH_FINISH' || inventoryStatus === 'RECV_FROM_EXPIRED_STER') {
        this.packageMain = rest;
        this.devices = devices.map((item) => ({
          ...item,
          updateStatus: item.status,
          newDeviceId: null,
        }));
        this.packageCode = qrcode;
      } else {
        const state = this.$statusFilters.inventoryPackage(status)?.name;
        this.$gNotifyError(`系統記錄該器械為${state}狀態`);
      }
    },
    getDeviceBoxInfo(result) {
      const avaliableBoxStatus = [0, 2];
      const { status, qrcode } = result;
      if (!avaliableBoxStatus.includes(status)) {
        this.$gNotifyError(`該器械盒狀態為${status}，不符合打包用狀態`);
      } else {
        this.deviceBoxQrcode = qrcode;
      }
    },
    deleteDevice(scanText) {
      const dAlreadyPackIdx = this.dAlreadyPack.findIndex((d) => d.qrcode === scanText);
      if (dAlreadyPackIdx === -1) return;

      const dAlreadyPackCurrent = this.dAlreadyPack[dAlreadyPackIdx];
      if (dAlreadyPackCurrent.newDeviceId) {
        const idx = this.devices.findIndex((d) => d.id === dAlreadyPackCurrent.id);
        this.devices[idx].newDeviceId = null;
      } else {
        const originIdx = this.devices.findIndex(
          (d) => d.id === dAlreadyPackCurrent.replaceOriginId,
        );
        const rpIdx = this.replaceDevices.findIndex((d) => d.id === dAlreadyPackCurrent.deviceId);
        this.replaceDevices.splice(rpIdx, 1);
        this.devices[originIdx].newDeviceId = null;
      }
    },
    changeStatus({ id, status }) {
      const idx = this.devices.findIndex((device) => device.id === id);
      this.devices[idx].updateStatus = status;
    },
    getDeviceStatus(statusName) {
      return this.$statusFilters.surgeryDevice(statusName);
    },
    repackReset() {
      this.packageMain = {};
      this.devices = [];
      this.replaceDevices = [];
      this.packageCode = '';
      this.deviceBoxQrcode = '';
    },
  },
};
</script>
