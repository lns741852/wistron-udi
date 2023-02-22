<template>
  <div>
    <div class="row bg-white q-pa-lg shadow-1 q-mb-md">
      <div class="col-9">
        <div class="row items-center">
          <q-img
            spinner-color="white"
            class="q-pr-lg"
            :src="boxImg"
            alt="deviceBox"
            width="360px"
            fit="contain"
          />
          <div class="q-pl-lg">
            <div class="row items-center">
              <h3 class="text-h5 text-weight-bold">器械盒</h3>
            </div>
            <p class="text-grey q-mb-md">{{ deviceBoxType.name }}</p>
            <ul>
              <li class="list">
                <p class="list__title">廠牌</p>
                <p class="list__text">{{ deviceBoxType.brand }}</p>
              </li>
              <li class="list">
                <p class="list__title">型號</p>
                <p class="list__text">{{ deviceBoxType.manufactureId }}</p>
              </li>
              <li class="list">
                <p class="list__title">總數</p>
                <p class="list__text">{{ queryQty.totalQty }}</p>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="col-3 text-right">
        <q-btn
          color="info"
          icon="add_circle_outline"
          label="新增器械盒"
          class="q-mb-sm"
          @click="$refs.deviceBoxDialog.$refInit()"
        />
      </div>
    </div>
    <div class="shadow-1 q-pa-md bg-white">
      <!-- 條件篩選 -->
      <div class="row items-center justify-between">
        <q-select
          outlined
          bg-color="grey-8"
          color="white"
          dark
          v-model="queryParams.status"
          :options="deviceStatus"
          option-label="name"
          option-value="status"
          dense
          options-dense
          emit-value
          map-options
          style="width: 160px"
        >
          <template v-slot:prepend>
            <q-icon name="img:/icons/division.png" />
          </template>
        </q-select>
        <ul class="row q-mb-md col-8 col-lg-5">
          <li
            class="col q-mr-xs border-dotted q-px-xs"
            v-for="(device, idx) in devicesStatusQty"
            :key="`${idx}${device.name}`"
          >
            <p class="text-grey q-mb-xs">{{ device.name }}</p>
            <p class="text-weight-bold text-right">{{ device.qty }}</p>
          </li>
        </ul>
      </div>
      <!-- 結果顯示 -->
      <VTable
        rowKey="id"
        :columns="columns"
        :rows="itemList"
        :bodySlots="['status', 'action']"
        :loading="isLoading"
        :currentPage="currentPage"
        :totalPages="totalPages"
        @changePage="changePage"
      >
        <template #status="props">
          <q-badge
            transparent
            align="middle"
            class="q-pa-xs"
            :color="`${$statusFilters.surgeryDevice(props.row.status)?.color}`"
          >
            {{ $statusFilters.surgeryDevice(props.row.status)?.name }}
          </q-badge>
        </template>

        <template #action="props">
          <q-btn
            dense
            round
            flat
            color="grey"
            icon="visibility"
            @click="viewDetail(props.row)"
          ></q-btn>
        </template>
      </VTable>
    </div>
    <DeviceBoxDialog ref="deviceBoxDialog" @updateDeiveBoxItemQty="queryDeviceBox(queryObj)" />
    <DeviceBoxDetailDialog ref="deviceBoxDetailDialog" title="器械盒" :detail="targetBoxDetail" />
  </div>
</template>
<script>
import { apiQueryDeviceBox, apiGetDeviceBox } from '@/api';
import DeviceBoxDialog from './components/DeviceBoxTransaction.vue';
import DeviceBoxDetailDialog from './components/DeviceAndBoxDetail.vue';

export default {
  name: 'DeviceBoxes',
  components: {
    DeviceBoxDialog,
    DeviceBoxDetailDialog,
  },
  inject: ['boxImg'],
  data() {
    return {
      columns: [
        { align: 'center', name: 'status', label: '狀態', field: 'status' },
        { align: 'left', name: 'udi', label: 'UDI', field: 'udi' },
        { name: 'usedCount', label: '使用次數', field: 'usedCount' },
        { name: 'cost', label: '成本', field: (val) => this.$utilsFilters.currency(val.cost) },
        { align: 'left', name: 'createTime', label: '建檔日期', field: 'createTime' },
        { align: 'left', name: 'action', label: '功能', field: 'action' },
      ],
      itemList: [],
      isLoading: false,
      currentPage: 1,
      totalPages: 1,
      queryQty: {},
      queryParams: {
        status: 'all',
        page: 0,
      },
      deviceBoxType: {},
      targetBoxDetail: {},
    };
  },
  computed: {
    devicesStatusQty() {
      const { totalQty, repairQty, unusedQty, usedQty, scrappedQty } = this.queryQty;
      return [
        { name: '總支數', qty: totalQty },
        { name: '維修數', qty: repairQty },
        { name: '全新', qty: unusedQty },
        { name: '已使用數', qty: usedQty },
        { name: '報廢數', qty: scrappedQty },
      ];
    },
    deviceStatus() {
      const status = this.$statusFilters.surgeryDevice('all');
      const ignoreStatus = ['UNSCANABLE', 'MISSING', 'REPAIR_DONE', 'RECEIVE_SCAN_DONE'];
      const filterStatus = status.filter((item) => ignoreStatus.indexOf(item.value) === -1);
      return [{ name: '全部', status: 'all' }, ...filterStatus];
    },
  },
  watch: {
    'queryParams.status'() {
      this.queryParams.page = 0;
      this.queryDeviceBox(this.queryParams);
    },
  },
  methods: {
    async queryDeviceBox(queryObj = []) {
      const data = Object.keys(queryObj).reduce(
        (acc, key) => (queryObj[key] === 'all' ? acc : { ...acc, [key]: queryObj[key] }),
        {},
      );
      try {
        const res = await apiQueryDeviceBox(data);
        const {
          totalQty = 0,
          usedQty = 0,
          unusedQty = 0,
          scrappedQty = 0,
          repairQty = 0,
          boxes,
          brand,
          manufacture_id: manufactureId,
        } = res.data;
        // boxDetail
        this.queryQty = { totalQty, usedQty, unusedQty, scrappedQty, repairQty };
        this.itemList = boxes.content;
        // pagination
        this.currentPage = this.queryParams.page + 1;
        this.totalPages = boxes.totalPages;
        this.deviceBoxType.brand = brand;
        this.deviceBoxType.manufactureId = manufactureId;
      } catch (error) {
        this.$q.notify({
          type: 'negative',
          message: '系統發生異常',
        });
      }
    },
    async viewDetail(item) {
      try {
        const res = await apiGetDeviceBox(item.qrcode);
        this.targetBoxDetail = {
          ...res.data,
          images: [{ id: 999, path: this.boxImg, isMain: true }],
          ...this.deviceBoxType,
        };
        this.$refs.deviceBoxDetailDialog.showDialog = true;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      }
    },
    changePage(page) {
      this.queryParams.page = page - 1;
      this.queryDeviceBox(this.queryParams);
    },
  },
  created() {
    this.queryDeviceBox(this.queryParams);
  },
};
</script>
