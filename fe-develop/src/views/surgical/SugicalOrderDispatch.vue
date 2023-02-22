<template>
  <VBreadCrumb backTitle="庫存區" />
  <SwitchTabs
    ref="switchTabs"
    :tabList="tabs.items"
    finishWord="發放完成"
    @pressFinal="$refs.surgicalOrderTable.init()"
  >
    <template v-slot:content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <div class="q-pa-lg">
          <SurgicalOrderTable
            ref="surgicalOrderTable"
            dataCategory="order"
            :orderStatus="dispatchStatus"
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
            :disabled="orderSelected.length === 0"
            @click="directWrapPackage"
          />
        </div>
      </div>
      <div v-show="slotProps.currentTabNumber === 2">
        <div class="row q-col-gutter-lg q-pa-lg">
          <div class="col-12">
            <div class="row q-col-gutter-lg">
              <div class="col-6 col-md-3">
                <q-card class="q-pt-xs q-pb-md q-px-md full-height">
                  <div class="row justify-between items-center q-mb-md">
                    <p class="text-weight-bold text-body2">手術資訊</p>
                    <q-img src="img/wrap-info.png" width="60px" />
                  </div>
                  <div class="row justify-between">
                    <div class="text-grey text-subtitle2">
                      <p>日期/{{ packagedItem.operatingDate }}</p>
                      <p>編號/{{ packagedItem.operatingNumber }}</p>
                      <p>名稱/{{ packagedItem.surgeryName }}</p>
                      <p>醫生/{{ packagedItem.doctor }}</p>
                    </div>
                    <div class="text-grey text-subtitle2">
                      <p>手術室/{{ packagedItem.operatingRoom }}</p>
                      <p>排刀順序/{{ packagedItem.operatingOrder }}</p>
                      <p>科別/ {{ $store.getters.divisionById(packagedItem.divisionId)?.name }}</p>
                    </div>
                  </div>
                </q-card>
              </div>
              <div class="col-6 col-md-3">
                <q-card class="column justify-between q-pt-xs q-pb-md q-px-md full-height">
                  <div class="row justify-between items-center q-mb-md">
                    <p class="text-weight-bold text-body2">病歷資訊</p>
                    <q-img src="img/wrap-info.png" width="60px" />
                  </div>
                  <p class="text-grey text-subtitle2">
                    病歷號碼/{{ packagedItem.medicalRecordNumber }}
                  </p>
                </q-card>
              </div>
              <div class="col-6 col-md-3">
                <q-card
                  flat
                  bordered
                  class="column justify-between q-pt-xs q-pb-md q-px-md full-height cursor-pointer"
                  @click="focusBtn = 'deviceBox'"
                >
                  <div class="row justify-between items-center q-mb-md">
                    <p class="text-weight-bold text-body2">
                      掃描包盤<span class="text-secondary q-pl-sm">{{
                        wrapDeviceBoxInfo.qrcode
                      }}</span>
                    </p>
                    <q-img src="img/wrap-deviceBox.png" width="50px" />
                  </div>
                  <q-input
                    ref="deviceBoxInput"
                    :disable="focusBtn !== 'deviceBox'"
                    :filled="focusBtn !== 'deviceBox'"
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
              <div class="col-6 col-md-3">
                <q-card
                  flat
                  bordered
                  class="column justify-between q-pt-xs q-pb-md q-px-md full-height cursor-pointer"
                  @click="focusBtn = 'deleteDevice'"
                >
                  <div class="row justify-between items-center q-mb-md">
                    <p class="text-weight-bold text-body2">移除包盤</p>
                    <q-img src="img/wrap-del.png" width="48px" />
                  </div>
                  <q-input
                    ref="deleteDeviceInput"
                    :disable="focusBtn !== 'deleteDevice'"
                    :filled="focusBtn !== 'deleteDevice'"
                    v-model="scanDeviceDeleteText"
                    type="search"
                    dense
                    outlined
                    style="margin-bottom: 5px"
                    @keyup.enter="deletePackage"
                  >
                    <template v-slot:append>
                      <q-icon name="qr_code_scanner" />
                    </template>
                  </q-input>
                </q-card>
              </div>
            </div>
          </div>

          <div class="col-12">
            <div class="row q-col-gutter-lg">
              <div class="col-12 col-md-9">
                <q-card class="q-pa-sm">
                  <h2 class="text-h6"></h2>
                  <q-table
                    :title="`手術申請單 - #${packagedItem.orderId}`"
                    :style="dispatchPackages?.length > 5 ? 'height: 62vh' : 'height: auto'"
                    :columns="dispatchColumns"
                    :rows="dispatchPackages"
                    :bordered="false"
                    flat
                    row-key="typeId"
                    hide-pagination
                    :pagination="{ rowsPerPage: 0 }"
                    virtual-scroll
                  >
                    <template v-slot:body="props">
                      <q-tr
                        :props="props"
                        :class="
                          props.row.styleSelected ? 'bg-cyan-1 text-white' : 'bg-white text-black'
                        "
                      >
                        <q-td key="configCode" :props="props">
                          {{ props.row.configCode }}
                        </q-td>
                        <q-td key="configName" :props="props">
                          {{ props.row.configName }}
                        </q-td>
                        <q-td key="qty" :props="props">
                          {{ props.row.qty }}
                        </q-td>
                        <q-td key="action" :props="props">
                          <VLinearProgress
                            :value="props.row.packages?.length / props.row.qty"
                            :content="`+${props.row.packages?.length || 0}`"
                          />
                        </q-td>
                      </q-tr>
                    </template>
                  </q-table>
                </q-card>
              </div>
              <div class="col-12 col-md-3">
                <VCircularProgress :progress="packingProgress" />
                <q-btn
                  color="blue"
                  @click="handleSubmit"
                  :disable="!packagedItem"
                  class="full-width"
                  >發放包盤</q-btn
                >
              </div>
            </div>
          </div>
        </div>
        <q-separator />
      </div>
    </template>
  </SwitchTabs>
</template>

<script>
import ErorrMusic from '@/mixins/errorMusicMixins';
import { apiGetOrder, apiGetPackageByCode, apiDispatchOrder } from '@/api';
import SwitchTabs from '@/components/SwitchTabs.vue';
import SurgicalOrderTable from '@/components/SurgicalOrderTable.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import VCircularProgress from '@/components/VCircularProgress.vue';
import VLinearProgress from '@/components/VLinearProgress.vue';

export default {
  components: {
    SwitchTabs,
    SurgicalOrderTable,
    VCircularProgress,
    VBreadCrumb,
    VLinearProgress,
  },
  mixins: [ErorrMusic],
  data() {
    return {
      dispatchStatus: 0,
      tabs: {
        current: 'chooseApplyOrder',
        items: [
          {
            name: 'chooseApplyOrder',
            title: '選取手術申請單',
          },
          {
            name: 'scanPackages',
            title: '選擇包盤',
          },
        ],
      },
      focusBtn: 'deviceBox', // box / deleteDevice / device
      dispatchColumns: [
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
        { name: 'qty', label: '總數量', field: 'qty' },
        {
          align: 'left',
          name: 'action',
          label: '已掃描數',
          field: 'action',
          headerStyle: 'width: 35%;',
        },
      ],
      dispatchPackages: [], // 已打包盤項目
      wrapDeviceBoxInfo: {}, // 裝包盤
      scanBoxText: '',
      scanDeviceText: '',
      scanDeviceDeleteText: '',
      tempPackageDispatchList: [],
      orderSelected: [],
    };
  },
  computed: {
    applyPackagesList() {
      return this.dispatchPackages.map(({ configId, packages }) => ({
        configId,
        packages,
      }));
    },
    packingProgress() {
      return {
        total: this.dispatchPackages.reduce((acc, current) => acc + current.qty, 0),
        current:
          this.dispatchPackages.reduce((acc, current) => acc + current.packages?.length, 0) || 0,
      };
    },
    packagedItem() {
      return this.orderSelected[0] || {};
    },
  },
  methods: {
    // Main: 區分重新打包或全新製作
    async dispatchOrderPackages({ orderId, orders }) {
      this.$gLoading(true);
      const data = {
        orderId,
        orders,
      };
      try {
        await apiDispatchOrder(data);
        this.$gNotifySuccess('發放成功');
        this.dispatchPackages = [];
        this.orderSelected = [];
        this.$refs.switchTabs.handleForwardTab();
      } catch (error) {
        const msg = error.data?.msg || '發放成功發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    // 2. 取得特定包盤申請資訊
    async getApplyOrderDetail(applyId) {
      try {
        const res = await apiGetOrder({ applyId, count: false });
        const { orders } = res.data;
        const targetOrder = orders.find((o) => o.id === this.packagedItem?.orderId).details || [];
        this.dispatchPackages = targetOrder.map((item) => ({
          ...item,
          packages: [],
          styleSelected: false,
        }));
      } catch (error) {
        const { msg = '取得盤包資訊異常' } = error.data;
        this.$gNotifyError(msg);
      }
    },
    async getPackageInfo() {
      try {
        if (!this.scanBoxText) return;
        const qrcode = this.scanBoxText;
        const res = await apiGetPackageByCode({ qrcode });
        const {
          id: pId,
          configCode: pConfigCode,
          configName: pConfigName,
          configId: pConfigId,
          status: dStatus,
          expireTime: pExpireTime,
        } = res.data;
        const requiredPackage = this.dispatchPackages.find((item) => item.configId === pConfigId);
        if (requiredPackage) {
          const isInInventory = dStatus !== 6;
          const isExist = requiredPackage.packages.includes(pConfigCode);
          if (isInInventory) {
            this.$gNotifyError('包盤狀態不符合');
            this.fireErrorMusic();
          } else if (new Date(pExpireTime) < new Date()) {
            this.$gNotifyError('此包盤有效期限已過期');
            this.fireErrorMusic();
          } else if (requiredPackage.packages.length >= requiredPackage.qty) {
            this.$gNotifyError(`${pConfigName}數量已足夠`);
          } else if (isExist) {
            this.$gNotifyError('這盒包盤已經掃描過');
          } else {
            requiredPackage.packages.push(pId);
            requiredPackage.styleSelected = true;
            this.tempPackageDispatchList.push({ id: pId, configId: pConfigId, qrcode });
            this.$gNotifySuccess(`${pConfigName}-掃描成功`);
            setTimeout(() => {
              requiredPackage.styleSelected = false;
            }, 2000);
          }
        } else {
          this.fireErrorMusic();
          this.$gNotifyError('非所需要的包盤');
        }
      } catch (error) {
        const msg = error.data.msg || '掃描包盤發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.scanBoxText = '';
      }
    },
    handleSubmit() {
      // 確認有configInfo
      const { orderId } = this.packagedItem;
      const lackRequirePackages = this.dispatchPackages.reduce((acc, pk) => {
        if (pk.packages.length !== pk.qty) {
          const missingNum = pk.qty - pk.packages.length;
          return [...acc, `${pk.configName} 少${missingNum}包`];
        }
        return acc;
      }, []);
      if (!orderId) {
        this.$gNotifyError('請選擇盤包類型');
      } else if (lackRequirePackages.length > 0) {
        this.$gNotifyError(lackRequirePackages.join(','));
      } else {
        this.dispatchOrderPackages({
          orderId,
          orders: this.applyPackagesList,
        });
      }
    },
    deletePackage() {
      const scanIdx = this.tempPackageDispatchList.findIndex(
        (pk) => pk.qrcode === this.scanDeviceDeleteText,
      );
      const scanPackageInfo = this.tempPackageDispatchList[scanIdx];
      if (scanIdx !== -1) {
        const targetPackage = this.dispatchPackages.find(
          (item) => item.configId === scanPackageInfo.configId,
        );
        targetPackage.packages = targetPackage.packages.filter((id) => id !== scanPackageInfo.id);
        this.tempPackageDispatchList.splice(scanIdx, 1);
      }
      this.scanDeviceDeleteText = '';
    },
    directWrapPackage() {
      this.$refs.switchTabs.handleForwardTab();
      this.$nextTick(() => {
        this.$refs.deviceBoxInput.focus();
      });
    },
  },
  watch: {
    packagedItem(val) {
      this.dispatchPackages = [];
      if (val.applyId) {
        this.getApplyOrderDetail(val.applyId);
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
