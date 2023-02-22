<template>
  <SwitchTabs
    ref="switchTabs"
    :tabList="tabList"
    finishWord="清點完成"
    @pressFinal="$refs.surgicalOrderTable.reset()"
  >
    <template v-slot:content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <div class="q-pa-lg">
          <SurgicalOrderTable
            ref="surgicalOrderTable"
            dataCategory="surgical"
            :hasSearchBar="true"
            :surgicalStatus="surgicalStatus"
            @dataSelected="(val) => (orderSelected = val)"
          />
        </div>
        <q-separator />
        <div class="flex justify-end q-px-lg q-py-md">
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="下一步"
            class="q-mb-sm"
            :disabled="Object.keys(surgicalItem).length === 0"
            @click="directWrapPackage"
          />
        </div>
      </div>
      <div v-show="slotProps.currentTabNumber === 2">
        <div class="row q-col-gutter-lg q-pa-lg">
          <div class="col-md-9">
            <div class="row q-col-gutter-lg">
              <div class="col-4">
                <q-card class="q-pt-xs q-pb-md q-px-md full-height">
                  <div class="row justify-between items-center q-mb-md">
                    <p class="text-weight-bold text-body2">手術資訊</p>
                    <q-img src="img/wrap-info.png" width="60px" />
                  </div>
                  <div class="row justify-between">
                    <div class="text-grey text-subtitle2">
                      <p>日期/{{ surgicalItem.operatingDate }}</p>
                      <p>編號/{{ surgicalItem.operatingNumber }}</p>
                      <p>名稱/{{ surgicalItem.surgeryName }}</p>
                      <p>醫生/{{ surgicalItem.doctor }}</p>
                    </div>
                    <div class="text-grey text-subtitle2">
                      <p>手術室/{{ surgicalItem.operatingRoom }}</p>
                      <p>排刀順序/{{ surgicalItem.operatingOrder }}</p>
                      <p>
                        科別/{{ $store.getters.divisionById(surgicalItem.divisionId)?.name || '-' }}
                      </p>
                    </div>
                  </div>
                </q-card>
              </div>
              <div class="col-4">
                <q-card class="column justify-between q-pt-xs q-pb-md q-px-md full-height">
                  <div class="row justify-between items-center q-mb-md">
                    <p class="text-weight-bold text-body2">病歷資訊</p>
                    <q-img src="img/wrap-info.png" width="60px" />
                  </div>
                  <p class="text-grey text-subtitle2">
                    病歷號碼/{{ surgicalItem.medicalRecordNumber }}
                  </p>
                </q-card>
              </div>
              <div class="col-4">
                <q-card
                  flat
                  bordered
                  class="column justify-between q-pt-xs q-pb-md q-px-md full-height cursor-pointer"
                  @click="focusBtn = 'deviceBox'"
                >
                  <div class="row justify-between items-center q-mb-md">
                    <p class="text-weight-bold text-body2">
                      掃描包盤
                    </p>
                    <q-img src="img/wrap-deviceBox.png" width="50px" />
                  </div>
                  <q-input
                    ref="deviceBoxInput"
                    v-model="scanBoxText"
                    type="search"
                    dense
                    outlined
                    style="margin-bottom: 5px"
                    @keyup.enter="getPackageInfo"
                  >
                    <template v-slot:append>
                      <q-icon name="qr_code_scanner" />
                    </template>
                  </q-input>
                </q-card>
              </div>
              <div class="col-12">
                <q-card class="q-pa-md q-mb-md" v-for="order in receivePackagesOrders" :key="order">
                  <div class="row justify-between ">
                    <h3 class="text-h6 q-pl-md q-py-sm">
                      申請單 - #{{ order.id }} -
                      <span class="text-secondary">{{
                        $statusFilters.surgicalApplyOrder(order.status).name
                      }}</span>
                    </h3>
                    <q-btn
                      color="info"
                      label="取消申請"
                      v-if="order.status === 0"
                      @click="cancelOrder(order.id)"
                    />
                  </div>
                  <q-table
                    :style="order.packages?.length > 5 ? 'height: 62vh' : 'height: auto'"
                    :columns="columns"
                    :rows="order.packages"
                    :bordered="false"
                    flat
                    row-key="typeId"
                    hide-pagination
                    :pagination="{ rowsPerPage: 0 }"
                    virtual-scroll
                  />
                  <q-separator />
                </q-card>
              </div>
            </div>
          </div>
          <div class="col-12 col-md-3">
            <VCircularProgress :progress="checkingProgress" />
          </div>
        </div>
        <q-separator />
      </div>
    </template>
  </SwitchTabs>
  <ApplyDeviceCountDialog
    ref="applyDeviceCount"
    v-if="checkDeviceMode === 'surgical'"
    :trackingId="trackingId"
    @checkFinish="checkFinish"
  />

  <ApplyDeviceScanDialog
    ref="applyDeviceScan"
    v-if="checkDeviceMode === 'recycle'"
    :trackingId="trackingId"
    :qrcode="packageQrcode"
    @closeDialog="packageQrcode = ''"
    @checkFinish="checkFinish"
  />
</template>

<script>
import { apiGetOrder, apiCancelOrder } from '@/api';
import SwitchTabs from '@/components/SwitchTabs.vue';
import SurgicalOrderTable from '@/components/SurgicalOrderTable.vue';
import ApplyDeviceCountDialog from '@/components/SurgicalApplyDeviceCount.vue';
import ApplyDeviceScanDialog from '@/components/SurgicalApplyDeviceScan.vue';
import VCircularProgress from '@/components/VCircularProgress.vue';

export default {
  components: {
    SwitchTabs,
    SurgicalOrderTable,
    ApplyDeviceCountDialog,
    ApplyDeviceScanDialog,
    VCircularProgress,
  },
  props: {
    surgicalStatus: {
      type: Number,
      require: true,
    },
    checkDeviceMode: {
      type: String,
      require: true,
      validator: (value) => ['recycle', 'surgical'].indexOf(value) !== -1,
    },
  },
  data() {
    return {
      trackingId: null,
      packageQrcode: null,
      tabList: [
        {
          name: 'chooseApplyOrder',
          title: '選取手術申請單',
        },
        {
          name: 'scanPackages',
          title: '選擇包盤',
        },
      ],
      focusBtn: 'deviceBox',
      columns: [
        {
          align: 'left',
          name: 'configCode',
          label: '包盤編號',
          field: 'configCode',
          headerStyle: 'width: 25%',
        },
        {
          align: 'left',
          name: 'configName',
          label: '包盤名稱',
          field: 'configName',
          headerStyle: 'width: 25%',
        },
        {
          name: 'divisionId',
          label: '科別',
          align: 'left',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        {
          align: 'left',
          name: 'qrcode',
          label: 'QRcode',
          field: 'qrcode',
        },
        {
          align: 'left',
          name: 'status',
          label: '包盤狀態',
          field: (val) => this.$statusFilters.inventoryPackage(val.status)?.name,
        },
      ],
      orderPackages: [],
      scanBoxText: '',
      orderSelected: [],
      applyOrders: [],
      receivePackagesOrders: [],
      countingRule: [
        {
          surgicalStatus: 2,
          orderCurrentStutus: 1,
          orderNextStatus: 2,
          step: 1,
          validChangeOrder: {
            packageStatus: 8,
          },
        },
        {
          surgicalStatus: 3,
          orderCurrentStutus: 2,
          orderNextStatus: 3,
          step: 2,
          validChangeOrder: {
            packageStatus: 9,
          },
        },
        {
          surgicalStatus: 4,
          orderCurrentStutus: 3,
          orderNextStatus: 4,
          step: 3,
          validChangeOrder: {
            packageStatus: 10,
          },
        },
      ],
    };
  },
  computed: {
    currentCountingRule() {
      return this.countingRule.find((item) => item.surgicalStatus === this.surgicalStatus);
    },
    applyPackagesAll() {
      return this.receivePackagesOrders.reduce((acc, current) => {
        if (current.packages) {
          return [...acc, ...current.packages];
        }
        return acc;
      }, []);
    },
    checkingProgress() {
      return {
        total: this.applyPackagesAll.length,
        current: this.applyPackagesAll.reduce((acc, current) => {
          if (current.status === this.currentCountingRule.validChangeOrder.packageStatus) {
            return acc + 1;
          }
          return acc;
        }, 0),
      };
    },
    surgicalItem() {
      return this.orderSelected[0] || {};
    },
    allChangeNextOrderStatus() {
      const { packageStatus } = this.currentCountingRule.validChangeOrder;
      return this.receivePackagesOrders.every((order) => {
        const expectNextOrderPackageStatus = packageStatus;
        return this.checkSameStatus(order.packages, expectNextOrderPackageStatus);
      });
    },
  },
  methods: {
    // 依據手術ID取得對應Order
    async getApplyOrderDetail(applyId, count = false) {
      try {
        const res = await apiGetOrder({ applyId, count });
        const { orders } = res.data;
        this.receivePackagesOrders = orders
          .filter((order) => order.status <= this.currentCountingRule.orderCurrentStutus + 1)
          .map((order) => ({
            ...order,
            userCheckPackage: false,
          }));
        this.applyOrders = orders;
      } catch (error) {
        const msg = error.data?.msg || '取得盤包資訊異常';
        this.$gNotifyError(msg);
      }
    },
    getPackageInfo() {
      const qrcode = this.scanBoxText;
      const packageItem = this.applyPackagesAll.find((pk) => pk.qrcode === qrcode) || {};
      const { status, trackingId } = packageItem;

      if (!status || !trackingId) {
        this.$gNotifyError('掃描包盤發生異常');
        return;
      }

      if (status === this.currentCountingRule.validChangeOrder.packageStatus) {
        this.$gNotifyError('器械已清點完成');
        return;
      }

      this.trackingId = trackingId;
      if (this.checkDeviceMode === 'surgical') {
        this.$refs.applyDeviceCount.showDialog = true;
      } else if (this.checkDeviceMode === 'recycle') {
        this.packageQrcode = this.scanBoxText;
        this.$refs.applyDeviceScan.isOpen = true;
      }

      this.scanBoxText = '';
    },
    directWrapPackage() {
      this.$refs.switchTabs.handleForwardTab();
      this.$nextTick(() => {
        this.$refs.deviceBoxInput.focus();
      });
    },
    async checkFinish() {
      await this.getApplyOrderDetail(this.surgicalItem.applyId, true);
      this.trackingId = null;
      this.packageQrcode = null;
      if (this.allChangeNextOrderStatus) {
        this.$refs.switchTabs.handleForwardTab();
      }
    },
    async cancelOrder(orderId) {
      try {
        this.$gLoading(true);
        await apiCancelOrder({ orderId });
        this.getApplyOrderDetail(this.surgicalItem.applyId, true);
      } catch (error) {
        // regarless error
      } finally {
        this.$gLoading(false);
      }
    },
    checkSameStatus(arr, status) {
      return arr.every((item) => item.status === status);
    },
  },
  watch: {
    surgicalItem(val) {
      this.orderPackages = [];
      if (val.applyId) {
        this.getApplyOrderDetail(val.applyId, true);
      }
    },
    focusBtn(val) {
      this.$nextTick(() => {
        this.$refs[`${val}Input`].focus();
      });
    },
  },
};
</script>
