<template>
  <VBreadCrumb backTitle="配盤區" />
  <SwitchTabs
    ref="switchTabs"
    :tabList="tabs.items"
    finishWord="打包完成"
    @pressFinal="$refs.choosePackageType.init()"
  >
    <template v-slot:content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <ChoosePackageType
          ref="choosePackageType"
          @configSelected="(val) => (configSelected = val)"
        />
        <q-separator />
        <div class="flex justify-end q-px-lg q-py-md">
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="下一步"
            class="q-mb-sm"
            :disabled="configSelected.length === 0"
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
                    <p class="text-weight-bold text-body2">包盤資訊</p>
                    <q-img src="img/wrap-info.png" width="60px" />
                  </div>
                  <div class="row justify-between items-end">
                    <div class="text-grey text-subtitle2">
                      <p>包盤序號/{{ packageMain?.serialNo }}</p>
                      <p>位置/{{ packageMain?.position || '-' }}</p>
                    </div>
                    <div class="text-subtitle2">
                      <p>{{ $store.getters.divisionById(packagedItem.divisionId)?.name }}</p>
                      <p>{{ packagedItem.configCode }}</p>
                      <p>{{ packagedItem.configName }}</p>
                    </div>
                  </div>
                </q-card>
              </div>
              <div class="col-6 col-md-3">
                <ScanCard
                  ref="deviceCard"
                  title="加入器械"
                  imgIcon="img/wrap-device.png"
                  :disable="focusBtn !== 'device'"
                  :filled="focusBtn !== 'device'"
                  @clickSelf="changeFocus('device')"
                  @fireEnter="getDeviceInfo"
                />
              </div>
              <div class="col-6 col-md-3">
                <ScanCard
                  title="器械盒"
                  imgIcon="img/wrap-deviceBox.png"
                  :disable="focusBtn !== 'deviceBox'"
                  :filled="focusBtn !== 'deviceBox'"
                  @clickSelf="changeFocus('deviceBox')"
                  @fireEnter="getDeviceBoxInfo"
                >
                  <template #caption>
                    <span class="text-secondary q-pl-sm word-break-all">{{
                      wrapDeviceBoxInfo.qrcode
                    }}</span>
                  </template>
                </ScanCard>
              </div>
              <div class="col-6 col-md-3">
                <ScanCard
                  title="移除器械"
                  imgIcon="img/wrap-del.png"
                  :disable="focusBtn !== 'deleteDevice'"
                  :filled="focusBtn !== 'deleteDevice'"
                  @clickSelf="changeFocus('deleteDevice')"
                  @fireEnter="deleteWrapDevice"
                />
              </div>
            </div>
          </div>
          <div class="col-12">
            <div class="row q-col-gutter-lg">
              <div class="col-12 col-md-9">
                <WrapPackagesTable :values="wrappedDevices" />
              </div>
              <div class="col-12 col-md-3">
                <VCircularProgress title="器械" :progress="packingProgress" />
                <q-btn
                  color="blue"
                  @click="handleSubmit"
                  :disable="!packagedItem"
                  class="full-width"
                  >完成打包</q-btn
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
import { apiGetPkConfig, apiGetBoxByCode, apiGetDeviceByCode, apiWrapPackage } from '@/api';
import SwitchTabs from '@/components/SwitchTabs.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import VCircularProgress from '@/components/VCircularProgress.vue';
import ChoosePackageType from './components/WrapPackagesChooseType.vue';
import WrapPackagesTable from './components/WrapPackagesTable.vue';
import ScanCard from '@/views/wrapPackage/components/ScanCard.vue';

export default {
  components: {
    ChoosePackageType,
    SwitchTabs,
    VBreadCrumb,
    VCircularProgress,
    WrapPackagesTable,
    ScanCard,
  },
  mixins: [ErorrMusic],
  data() {
    return {
      tabs: {
        items: [
          {
            name: 'choosePackageType',
            title: '選擇包盤類型',
          },
          {
            name: 'chooseDevice',
            title: '選擇器械',
          },
        ],
      },
      focusBtn: 'device',
      configSelected: [],
      wrappedDevices: [],
      wrapDeviceBoxInfo: {},
      deviceTotalQty: 0,
      tempDevicePackList: [],
    };
  },
  computed: {
    applyDeviceList() {
      return this.wrappedDevices.map(({ deviceTypeId, deviceIds }) => ({
        deviceTypeId,
        deviceIds,
      }));
    },
    packagedItem() {
      return this.configSelected[0] || {};
    },
    packingProgress() {
      return {
        total: this.deviceTotalQty,
        current:
          this.wrappedDevices.reduce((acc, current) => acc + current.deviceIds?.length, 0) || 0,
      };
    },
    packageMain() {
      return this.packagedItem?.packages?.filter((pk) => pk.status === -1)[0];
    },
  },
  methods: {
    async wrapPackage({ deviceBoxQrcode, applyDeviceList }) {
      this.$gLoading(true);
      const data = {
        packageId: this.packageMain?.id,
        deviceBoxQrcode,
        applyDeviceList,
      };
      try {
        await apiWrapPackage(data);
        this.$gNotifySuccess('打包成功');
        this.wrappedDevices = [];
        this.wrapDeviceBoxInfo = {};
        this.tempDevicePackList = [];
        this.configSelected = [];
        this.$refs.switchTabs.handleForwardTab();
      } catch (error) {
        const msg = error.data.msg || '打包送出發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async getConfigDevicesDetail(configId) {
      try {
        const res = await apiGetPkConfig({ configId });
        const { deviceTypes, deviceQty } = res.data;
        this.deviceTotalQty = deviceQty;
        this.wrappedDevices = deviceTypes.map((device) => ({
          spec: device.spec,
          image: this.$utilsFilters.imagePath(device.images[0]?.path),
          nameScientific: device.nameScientific,
          name: device.name,
          deviceTypeId: device.typeId,
          qty: device.qty,
          deviceIds: [],
          styleSelected: false,
        }));
      } catch (error) {
        const { msg = '取得盤包資訊異常' } = error.data;
        this.$gNotifyError(msg);
      }
    },
    async getDeviceInfo({ text: qrcode }) {
      try {
        const res = await apiGetDeviceByCode({ qrcode });
        const {
          id: dId,
          typeId: dTypeId,
          status: dStatus,
          divisionId: dDivisionId,
          nameScientific: dName,
        } = res.data;
        const requiredDevice = this.wrappedDevices.find((item) => item.deviceTypeId === dTypeId);
        if (requiredDevice) {
          const newDevice = dStatus === 0;
          const idleDevice = dStatus === 2;
          const isInValidStatus = !newDevice && !idleDevice;
          const isExist = requiredDevice.deviceIds.includes(dId);
          if (isInValidStatus) {
            this.$gNotifyError('狀態不符合');
            this.fireErrorMusic();
          } else if (this.packagedItem.divisionId !== dDivisionId) {
            this.$gNotifyError('器械的科別非包盤申請所屬科別');
            this.fireErrorMusic();
          } else if (requiredDevice.deviceIds.length >= requiredDevice.qty) {
            this.$gNotifyError(`${dName}器械數量已足夠`);
          } else if (isExist) {
            this.$gNotifyError('這隻器械已經掃描過');
          } else {
            requiredDevice.deviceIds.push(dId);
            requiredDevice.styleSelected = true;
            this.tempDevicePackList.push({ id: dId, typeId: dTypeId, qrcode });
            this.$gNotifySuccess(`${dName}-掃描成功`);
            setTimeout(() => {
              requiredDevice.styleSelected = false;
            }, 2000);
          }
        } else {
          this.fireErrorMusic();
          this.$gNotifyError('非所需要的器械');
        }
      } catch (error) {
        const msg = error.data.msg || '掃描器械發生異常!';
        this.$gNotifyError(msg);
      }
    },
    async getDeviceBoxInfo({ text: qrcode }) {
      try {
        const res = await apiGetBoxByCode({ qrcode });
        const avaliableBoxStatus = [0, 2];
        const { status } = res.data;
        if (avaliableBoxStatus.includes(status)) {
          this.wrapDeviceBoxInfo = res.data;
        } else {
          this.$gNotifyError(`該器械盒狀態為${status}，不符合打包用狀態`);
        }
      } catch (error) {
        const msg = error.data.msg || '掃描包盤發生異常!';
        this.$gNotifyError(msg);
      }
    },
    handleSubmit() {
      const { id: addId } = this.packagedItem;
      const { qrcode } = this.wrapDeviceBoxInfo;
      const lackRequireDevices = this.wrappedDevices.reduce((acc, device) => {
        if (device.deviceIds.length !== device.qty) {
          const missingNum = device.qty - device.deviceIds.length;
          return [...acc, `${device.nameScientific} 少${missingNum}隻`];
        }
        return acc;
      }, []);
      if (!qrcode) {
        this.$gNotifyError('請掃描器械盒');
      } else if (!addId) {
        this.$gNotifyError('請選擇盤包類型');
      } else if (lackRequireDevices.length > 0) {
        this.$gNotifyError(lackRequireDevices.join(','));
      } else {
        this.wrapPackage({
          deviceBoxQrcode: qrcode,
          applyDeviceList: this.applyDeviceList,
        });
      }
    },
    deleteWrapDevice({ text: qrcode }) {
      const scanDeviceIdx = this.tempDevicePackList.findIndex((device) => device.qrcode === qrcode);
      const scanDeviceInfo = this.tempDevicePackList[scanDeviceIdx];
      if (scanDeviceIdx !== -1) {
        const targetType = this.wrappedDevices.find(
          (item) => item.deviceTypeId === scanDeviceInfo.typeId,
        );
        targetType.deviceIds = targetType.deviceIds.filter((id) => id !== scanDeviceInfo.id);
        this.tempDevicePackList.splice(scanDeviceIdx, 1);
      }
    },
    directWrapPackage() {
      this.$refs.switchTabs.handleForwardTab();
      this.$refs.deviceCard.focusInput();
    },
    changeFocus(item) {
      if (this.focusBtn !== item) {
        this.focusBtn = item;
      }
    },
  },
  watch: {
    packagedItem(val) {
      this.wrappedDevices = [];
      if (val.configId) {
        this.getConfigDevicesDetail(val.configId);
      }
    },
  },
};
</script>
