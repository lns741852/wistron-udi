<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="q-pt-md shadow-1 bg-white" style="max-width: 800px; width: 100%">
      <div class="row justify-between items-center q-px-lg">
        <h1 class="text-h6">清點數量</h1>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="bg-white q-px-lg q-pb-lg">
        <div class="q-gutter-sm" v-if="isDispatchPackageStatus">
          <q-radio v-model="sterilizedSuccess" :val="true" label="滅菌成功" />
          <q-radio v-model="sterilizedSuccess" :val="false" label="滅菌失敗" />
        </div>

        <q-table
          :columns="columns"
          :rows="values"
          :loading="isLoading"
          :bordered="false"
          flat
          row-key="typeId"
          hide-pagination
          :pagination="{ rowsPerPage: 0 }"
        >
          <template v-slot:body-cell-typeInfo="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-img
                  :src="$utilsFilters.imagePath(props.row.mainImagePath)"
                  width="100px"
                  :ratio="4 / 3"
                />
                <div class="q-pl-md">
                  <h6 class="text-grey-9 q-mb-xs">{{ props.row.nameScientific }}</h6>
                  <p>{{ props.row.name }}</p>
                </div>
              </div>
            </q-td>
          </template>
          <template v-slot:body-cell-spec="props">
            <q-td :props="props" class="white-space-prewrap">
              <p>{{ props.row.spec }}</p>
            </q-td>
          </template>
          <template v-slot:body-cell-action="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-input
                  v-model.number="props.row.checkQty"
                  type="number"
                  dense
                  outlined
                  min="0"
                  input-style="width: 50px"
                />
                <p class="q-ml-md">數量差異：{{ props.row.checkQty - props.row.qty }}</p>
              </div>
            </q-td>
          </template>
        </q-table>
        <q-separator />
        <div class="flex justify-end q-pt-lg">
          <q-btn color="blue" label="器械確認" type="button" @click="packageCheck" />
        </div>
      </div>
    </div>
  </q-dialog>
</template>

<script>
import { apiGetPackageTrack, apiGetPkTrackProcess } from '@/api';

export default {
  name: 'SurgicalApplyDeviceCount',
  props: {
    trackingId: {
      type: Number,
    },
  },
  data() {
    return {
      showDialog: false,
      isLoading: false,
      columns: [
        {
          headerStyle: 'width: 40%;',
          align: 'left',
          name: 'typeInfo',
          label: '器械類型',
          field: 'typeInfo',
        },
        { align: 'left', headerStyle: 'width: 20%;', name: 'spec', label: '規格', field: 'spec' },
        { name: 'qty', label: '總數', field: 'qty' },
        { align: 'left', name: 'action', label: '清點', field: 'action' },
      ],
      values: [],
      pagination: {
        sortBy: 'desc',
        descending: false,
        page: 0,
        rowsPerPage: 10,
        totalPages: 1,
      },
      packageInfo: {},
      sterilizedSuccess: true,
    };
  },
  computed: {
    isDispatchPackageStatus() {
      return this.packageInfo.status === 7;
    },
    countingStep() {
      switch (this.packageInfo.status) {
        case 7:
          return 1;
        case 8:
          return 2;
        case 9:
          return 3;
        case 17:
          return 2;
        default:
          return null;
      }
    },
  },
  methods: {
    async getPackageInfo() {
      try {
        const res = await apiGetPackageTrack(this.trackingId);
        const { deviceTypes, ...rest } = res.data;
        this.values = deviceTypes.map((type) => ({
          ...type,
          mainImagePath: type.images[0]?.path,
          checkQty: type.qty,
        }));
        this.packageInfo = rest;
      } catch (error) {
        const msg = error.data?.msg || '取得包盤發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.scanBoxText = '';
      }
    },
    async packageCheck() {
      try {
        const sterilizedSuccess = this.isDispatchPackageStatus
          ? { sterilizedSuccess: this.sterilizedSuccess }
          : {};
        const data = {
          ...sterilizedSuccess,
          step: this.countingStep,
          deviceTypes: this.values.map((val) => ({ id: val.typeId, qty: val.checkQty })),
        };
        await apiGetPkTrackProcess(this.trackingId, data);
        this.$emit('checkFinish');
        this.$gNotifySuccess('清點成功');
        this.showDialog = false;
      } catch (error) {
        const msg = error.data;
        if (msg) {
          this.$gNotifyError(msg);
        } else {
          this.$gNotifyError('系統發生異常');
        }
      }
    },
  },
  watch: {
    trackingId(val) {
      if (val) {
        this.getPackageInfo();
      }
    },
  },
};
</script>
