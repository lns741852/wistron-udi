<template>
  <div class="flex justify-between q-mb-md">
    <h4 class="text-weight-bold">{{ $route.meta?.title }}</h4>
  </div>

  <div class="row q-col-gutter-lg">
    <div class="col-4 col-md-3">
      <div class="q-pa-md bg-white">
        <p class="text-weight-bold q-mb-md">包盤資訊</p>
        <p class="bg-custom  border-radius-sm q-pa-sm q-mb-md">
          {{ packageInfo.serialNo }}
        </p>
        <ul class="row q-mb-md">
          <li class="list col-12">
            <p class="list__title">包盤編號</p>
            <p class="list__text">{{ packageInfo.configCode }}</p>
          </li>
          <li class="list col-12">
            <p class="list__title">包盤名稱</p>
            <p class="list__text">{{ packageInfo.configName }}</p>
          </li>
          <li class="list col-12" v-if="packageInfo.divisionId">
            <p class="list__title">科別</p>
            <p class="list__text">
              {{ $store.getters.divisionById(packageInfo.divisionId)?.name }}
            </p>
          </li>
        </ul>
      </div>
      <q-separator />
      <div class="q-px-sm q-py-lg  bg-white">
        <q-list>
          <q-item
            v-for="item in values"
            :key="item.key"
            clickable
            :active="item.key === activeItem"
            active-class="bg-custom-cyan-99 border-radius-sm"
            @click="activeItem = item.key"
          >
            <q-item-section>
              <q-item-label class="text-weight-bold text-body2">{{ item.value }}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </div>
    <div class="col-8 col-md-9">
      <transition name="fade">
        <VTimeLine :values="trackingRecords" v-show="activeItem === 'history'">
          <template #title>歷程紀錄 </template>
        </VTimeLine>
      </transition>
      <transition name="fade">
        <DeviceTrackingInfo :contents="trackingDevices" v-show="activeItem === 'deviceInfo'" />
      </transition>
      <transition name="fade">
        <SterilizedTracking v-show="activeItem === 'sterilized'" :result="sterilizedResult" />
      </transition>
      <transition name="fade">
        <SurgicalTracking v-show="activeItem === 'surigcal'" :result="sugicalResult" />
      </transition>
    </div>
  </div>
</template>

<script>
import { apiGetTracking, apiGetTrackingDevice, apiGetSterilizedDetail, apiGetOrder } from '@/api';
import VTimeLine from '@/components/VTimeLine.vue';
import DeviceTrackingInfo from './components/DeviceTrackingInfo.vue';
import SterilizedTracking from './components/SterilizationTrackingResult.vue';
import SurgicalTracking from './components/SurgicalTracking.vue';

export default {
  name: 'packageTrackingDetail',
  components: {
    VTimeLine,
    DeviceTrackingInfo,
    SterilizedTracking,
    SurgicalTracking,
  },
  props: {
    trackingId: {
      type: [Number, String],
      required: true,
    },
  },
  data() {
    return {
      activeItem: 'history',
      values: [
        { key: 'history', value: '歷程' },
        { key: 'deviceInfo', value: '器械資訊' },
        { key: 'sterilized', value: '滅菌資訊' },
        { key: 'surigcal', value: '手術資訊' },
      ],
      trackingRecords: [],
      trackingDevices: [],
      sterilizedResult: {},
      sugicalResult: {},
      packageInfo: {},
    };
  },
  computed: {},
  methods: {
    async getTrackingInfo() {
      this.$gLoading(true);
      try {
        const res = await apiGetTracking(this.trackingId);
        const { trackingRecords, ...rest } = res.data;
        this.packageInfo = rest;
        this.trackingRecords = trackingRecords.map((t) => ({
          title: this.$statusFilters.inventoryPackage(t.status)?.name,
          recordAt: t.createTime,
          text: t.createName,
        }));
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
        setTimeout(() => {
          this.backToRecordList();
        }, 500);
      } finally {
        this.$gLoading(false);
      }
    },
    async getTrackingDevice() {
      this.$gLoading(true);
      try {
        const res = await apiGetTrackingDevice(this.trackingId);
        this.trackingDevices = res.data;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
        setTimeout(() => {
          this.backToRecordList();
        }, 500);
      } finally {
        this.$gLoading(false);
      }
    },
    async getTrackingSterilized() {
      this.$gLoading(true);
      try {
        const res = await apiGetSterilizedDetail({ trackingId: this.trackingId });
        this.sterilizedResult = res.data;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
        setTimeout(() => {
          this.backToRecordList();
        }, 500);
      } finally {
        this.$gLoading(false);
      }
    },
    async getTrackingSurgical() {
      this.$gLoading(true);
      try {
        const res = await apiGetOrder({ trackingId: this.trackingId });
        this.sugicalResult = res.data;
      } catch (error) {
        // regarless error
      } finally {
        this.$gLoading(false);
      }
    },
    backToRecordList() {
      this.$router.push({ name: 'packageUsageRecord' });
    },
  },
  created() {
    if (this.trackingId) {
      this.getTrackingDevice();
      this.getTrackingInfo();
      this.getTrackingSterilized();
      this.getTrackingSurgical();
    } else {
      this.backToRecordList();
    }
  },
};
</script>

<style lang="scss" scoped>
.fade-enter-active {
  transition: opacity 1s;
}

.fade-leave-active {
  transition: opacity 0s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.fade-enter-to,
.fade-leave-from {
  opacity: 1;
}
</style>
