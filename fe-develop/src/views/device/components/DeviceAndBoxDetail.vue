<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-grey-2 q-pt-md q-px-lg q-pb-xl" style="max-width: 800px; width: 100%">
      <div class="row justify-between items-center q-mb-md">
        <h1 class="text-h6">{{ title }}查看</h1>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="row items-center bg-white q-mb-md q-pa-md">
        <div class="col-5" v-if="detail?.images?.length > 0">
          <q-img
            spinner-color="white"
            class="q-pr-lg q-mb-lg rounded-borders"
            :src="mainImages?.path"
            alt=""
            width="100%"
            fit="cover"
          />
          <div class="scroll text-no-wrap" v-if="detail.images?.length > 0">
            <q-img
              v-for="img in detail.images"
              :key="img.id"
              spinner-color="white"
              class="q-mr-sm rounded-borders"
              :src="`${img.path}`"
              alt="小圖"
              style="height: 60px; max-width: 60px"
              fit="cover"
            />
          </div>
        </div>
        <div class="q-pl-lg col-7">
          <div class="row items-center">
            <h3 class="text-h5 text-weight-bold">{{ detail.nameScientific }}</h3>
            <q-btn
              v-if="detail.deviceId && detail.status === 0"
              round
              flat
              class="q-mt-sm"
              size="12px"
              color="secondary"
              icon="delete"
              @click="checkDeleteItem({ id: detail.deviceId, title: detail.nameScientific })"
            />
          </div>
          <p class="text-grey q-mb-sm">{{ detail.name }}</p>
          <q-badge
            transparent
            align="middle"
            class="q-pa-xs q-mb-sm"
            :color="`${$statusFilters.surgeryDevice(detail.status)?.color}`"
          >
            {{ $statusFilters.surgeryDevice(detail.status)?.name }}
          </q-badge>
          <ul class="row">
            <li class="list col-12">
              <p class="list__title">QRcode</p>
              <p class="list__text">{{ detail.qrcode }}</p>
            </li>
            <li class="list col-12">
              <p class="list__title">UDI</p>
              <p class="list__text">{{ detail.udi }}</p>
            </li>
            <li class="list col-12" v-if="detail.brand">
              <p class="list__title">廠牌</p>
              <p class="list__text">{{ detail.brand }}</p>
            </li>
            <li class="list col-6" v-if="detail.manufactureId">
              <p class="list__title">型號</p>
              <p class="list__text">{{ detail.manufactureId }}</p>
            </li>
            <li class="list col-6" v-if="detail.division">
              <p class="list__title">科別</p>
              <p class="list__text">
                {{ $store.getters.divisionById(detail.division)?.name || '-' }}
              </p>
            </li>
          </ul>
          <ul class="row">
            <li class="col-4 list">
              <p class="list__title">成本</p>
              <p class="list__text">{{ detail.cost }}</p>
            </li>
            <li class="col-4 list">
              <p class="list__title">使用次數</p>
              <p class="list__text">{{ detail.usedCount }}</p>
            </li>
            <li class="col-4 list">
              <p class="list__title">維修次數</p>
              <p class="list__text">{{ detail.repairCount || 0 }}</p>
            </li>
          </ul>
        </div>
      </div>
      <ul v-if="tabModes.length > 1" class="row">
        <li v-for="item in tabModes" :key="item">
          <q-btn
            class="q-px-md q-mr-sm"
            :color="tabMode === item.value ? 'grey' : 'grey-1'"
            text-color="white"
            :label="`${title}${item.label}`"
            @click="tabMode = item.value"
            dense
          />
        </li>
      </ul>
      <div class="bg-white q-px-md">
        <div class="row justify-end q-pt-md">
          <slot name="tableTR" :tabMode="tabMode" />
        </div>
        <VTable
          rowKey="id"
          :columns="columns"
          :rows="value"
          :loading="isLoading"
          :hasPage="false"
          :virtualScroll="true"
          :style="value.length > 5 ? 'height: 50vh' : ''"
        />
      </div>
    </div>
  </q-dialog>
</template>

<script>
import { apiDeleteDeviceItem } from '@/api';
import emitter from '@/common/emitter';

export default {
  name: 'DeviceAndBoxDetail',
  props: {
    title: String, // 器械、器械盒
    detail: {
      type: Object,
    },
  },

  data() {
    return {
      tabMode: 'repair', // surgical
      isLoading: false,
      showDialog: false,
    };
  },
  computed: {
    mainImages() {
      return this.detail.images?.find((item) => item.isMain);
    },
    tabModes() {
      const { deviceId, useRecords } = this.detail;
      const common = [{ value: 'repair', label: '維修' }];
      return deviceId && useRecords
        ? [...common, { value: 'surgical', label: '使用紀錄' }]
        : common;
    },
    columns() {
      if (this.tabMode === 'surgical') {
        return [
          { align: 'left', name: 'operatingNumber', label: '手術編號', field: 'operatingNumber' },
          { align: 'left', name: 'surgicalName', label: '手術名稱', field: 'surgicalName' },
          {
            align: 'left',
            name: 'medicalRecordNumber',
            label: '病歷號',
            field: 'medicalRecordNumber',
          },
          {
            align: 'left',
            name: 'divisionId',
            label: '科別',
            field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
          },
          { name: 'operatingDate', label: '手術時間', field: 'operatingDate' },
        ];
      }
      return [
        {
          align: 'left',
          name: 'createTime',
          label: '建立時間',
          field: 'createTime',
        },
        {
          align: 'left',
          name: 'usedCount',
          label: '使用次數',
          field: (val) => (this.detailType === 'device' ? val.usedCount : val.preUsedCount),
        },
        {
          align: 'left',
          name: 'status',
          label: '狀態',
          field: (val) => {
            const { status } = val;
            return this.detailType === 'device'
              ? status
              : this.$statusFilters.surgeryDevice(status)?.name;
          },
        },
        {
          name: 'desc',
          label: '原因',
          field: (val) => (this.detailType === 'device' ? val.desc : val.comments),
        },
        { name: 'finishTime', label: '完成時間', field: 'finishTime' },
      ];
    },
    detailType() {
      return this.detail.division || this.detail.divisionId ? 'device' : 'deviceBox';
    },
    value() {
      const { deviceId, repairRecords = [], useRecords = [], records = [] } = this.detail;
      if (deviceId && useRecords) {
        return this.tabMode === 'repair' ? repairRecords : useRecords;
      }
      return records;
    },
  },
  methods: {
    async deleteDeviceItem(id) {
      try {
        const data = { deviceId: id };
        await apiDeleteDeviceItem(data);
        this.showDialog = false;
        emitter.emit('deviceDetail-updateBrands');
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    checkDeleteItem({ id, title = '' }) {
      this.$q
        .dialog({
          title: '確認通知',
          message: `確認刪除<strong>${title}</strong>申請`,
          html: true,
          ok: {
            color: 'negative',
            flat: true,
          },
          cancel: {
            flat: true,
          },
          persistent: true,
        })
        .onOk(() => {
          this.deleteDeviceItem(id);
        });
    },
  },
};
</script>
