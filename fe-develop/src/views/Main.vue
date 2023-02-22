<template>
  <q-layout view="lHh Lpr lFf">
    <TheHeader @toggle="toggleDrawer" @headerInputQuery="headerScan" />
    <NavBar @changeDrawer="changeDrawer" ref="nav" />
    <q-page-container class="bg-grey-2" style="min-height: 100vh">
      <div class="q-pa-lg" v-if="isInit">
        <router-view></router-view>
      </div>
    </q-page-container>
  </q-layout>
  <Loading v-model:active="isLoading" :is-full-page="true" loader="bars" color="#293042" />
  <DeviceItemDetailDialog ref="deviceDetailDialog" :title="title" :detail="deviceNBoxDetail">
    <template #tableTR="item" v-if="$route.meta?.scan === 'device'">
      <div v-show="item.tabMode === 'surgical'">
        <VDateRange @updateDate="updateDate" />
      </div>
    </template>
  </DeviceItemDetailDialog>
  <PackageDetailDialog ref="packageDetailDialog" :detail="packageDetail" />
</template>

<script>
import Loading from 'vue-loading-overlay';
import emitter from '@/common/emitter';
import Header from '@/layout/Header.vue';
import NavBar from '@/layout/NavBar.vue';
import VDateRange from '@/components/VDateRange.vue';
import JWT from '@/api/cookies';
import { apiGetDeviceItem, apiGetDeviceBox, apiGetPackageByCode } from '@/api';
import DeviceItemDetailDialog from '@/views/device/components/DeviceAndBoxDetail.vue';
import PackageDetailDialog from '@/components/PackageInnerDetail.vue';

export default {
  name: 'LayoutDefault',
  components: {
    TheHeader: Header,
    NavBar,
    Loading,
    DeviceItemDetailDialog,
    PackageDetailDialog,
    VDateRange,
  },
  provide() {
    return {
      getPackageStation: 0,
      getSterilizedStation: 1,
      getInventoryStation: 2,
      getCirculationStation: 3,
      boxImg: this.boxImg,
    };
  },
  data() {
    return {
      title: '器械',
      leftDrawerOpen: true,
      isLoading: false,
      isInit: false,
      deviceNBoxDetail: {},
      packageDetail: {},
      boxImg: 'https://sims.stage.hpicorp.tk/images/c457e648-7520-4748-a6d6-f15c7cbf507a.jpeg',
      date: '',
      deviceQrcode: '',
    };
  },
  methods: {
    toggleDrawer() {
      this.$refs.nav.toggle();
    },
    changeDrawer(status) {
      this.leftDrawerOpen = status;
    },
    handleLoading(status) {
      this.isLoading = status;
    },
    vNotifyMessage({ type, content }) {
      this.$q.notify({
        type,
        message: content,
      });
    },
    headerScan(val) {
      const queryType = ['device', 'box', 'package'];
      const type = this.$route.meta?.scan;
      if (queryType.includes(type)) {
        if (type === 'device') {
          this.title = '器械';
          this.viewDeviceDetail(val);
        } else if (type === 'box') {
          this.title = '器械盒';
          this.viewBoxDetail(val);
        } else {
          this.title = '';
          this.viewPackageDetail({ qrcode: val, devices: true });
        }
      }
    },
    async viewDeviceDetail(qrcode) {
      try {
        if (!this.date) {
          const from = this.$utilsFilters.dateStr().slice(0, 10);
          const to = this.$utilsFilters.dateStr().slice(0, 10);
          this.date = { from, to };
        }
        this.deviceQrcode = qrcode;
        const { from: start, to: end } = this.date;
        const res = await apiGetDeviceItem({ qrcode, history: true, start, end });
        this.deviceNBoxDetail = {
          ...res.data,
          images: res.data.images.map((img) => ({
            id: img.id,
            isMain: img.isMain,
            path: this.$utilsFilters.imagePath(img.path),
          })),
        };
        this.$refs.deviceDetailDialog.showDialog = true;
      } catch (error) {
        this.$gNotifyError('查無器械');
      }
    },
    async viewBoxDetail(qrcode) {
      try {
        const res = await apiGetDeviceBox(qrcode);
        this.deviceNBoxDetail = {
          ...res.data,
          images: [{ id: 999, path: this.boxImg, isMain: true }],
          ...this.deviceBoxType,
        };
        this.$refs.deviceDetailDialog.showDialog = true;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      }
    },
    async viewPackageDetail(qrcode) {
      try {
        const res = await apiGetPackageByCode(qrcode);
        this.packageDetail = res.data;
        this.$refs.packageDetailDialog.showDialog = true;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      }
    },
    async init() {
      this.$gLoading(true);
      if (JWT.getToken()) {
        await this.$store.dispatch('sysSetting/queryDivisions');
        this.isInit = true;
        this.$gLoading(false);
      } else {
        this.$router.push('/login');
        this.$gLoading(false);
      }
    },
    updateDate(date) {
      this.date = { ...date };
      this.viewDeviceDetail(this.deviceQrcode);
    },
  },
  mounted() {
    emitter.on('v-loading', ({ isLoading }) => this.handleLoading(isLoading));
    emitter.on('notice-message', (message) => {
      const { type = 'secondary', content } = message;
      this.vNotifyMessage({ type, content });
    });
  },
  unmounted() {
    emitter.off('v-loading', ({ isLoading }) => this.handleLoading(isLoading));
    emitter.off('notice-message', (message) => {
      const { type = 'secondary', content } = message;
      this.vNotifyMessage({ type, content });
    });
  },
  created() {
    this.init();
  },
};
</script>
