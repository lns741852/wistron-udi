<template>
  <div>
    <div class="row q-mb-md">
      <div class="col-12 col-sm-3 q-py-md bg-white text-center border-right">
        <p class="text-subtitle2 text-grey">處理包盤數</p>
        <h5 class="text-weight-bold q-mb-xs">{{ analysisInfo.totalWashedPackages }}</h5>
        <p class="text-subtitle2 text-grey">數</p>
      </div>
      <div class="col-12 col-sm-3 q-py-md bg-white text-center  border-right">
        <p class="text-subtitle2 text-grey">清洗次數</p>
        <h5 class="text-weight-bold q-mb-xs">{{ analysisInfo.totalWashedCount }}</h5>
        <p class="text-subtitle2 text-grey">數</p>
      </div>
      <div class="col-12 col-sm-3 q-py-md bg-white text-center  border-right">
        <p class="text-subtitle2 text-grey">失敗</p>
        <h5 class="text-weight-bold q-mb-xs">{{ analysisInfo.failedWashed }}</h5>
        <p class="text-subtitle2 text-grey">數</p>
      </div>
      <div class="col-12 col-sm-3 q-py-md bg-white text-center">
        <p class="text-subtitle2 text-grey">清洗機使用率</p>
        <h5 class="text-weight-bold q-mb-xs">{{ analysisInfo.totalUsageRate }}</h5>
        <p class="text-subtitle2 text-grey">％</p>
      </div>
    </div>
    <div class="bg-white q-pa-md">
      <p class="text-weight-bold q-ml-sm q-mb-sm">清洗機資訊</p>
      <VTable
        rowKey="washingMachine"
        :columns="columns"
        :rows="values"
        :loading="isLoading"
        :bodySlots="['action']"
        :hasPage="false"
        :virtualScroll="true"
      >
        <template #action="props">
          <VLinearProgress
            :value="props.row.usageRate / 100"
            :content="`${props.row.usageRate ?? 0} %`"
          />
        </template>
      </VTable>
    </div>
  </div>
</template>

<script>
import { apiQueryWashAnalysis } from '@/api';
import VLinearProgress from '@/components/VLinearProgress.vue';

export default {
  inject: ['reportDate'],
  components: {
    VLinearProgress,
  },
  data() {
    return {
      columns: [
        { align: 'left', name: 'washingMachine', label: '清洗機', field: 'washingMachine' },
        {
          align: 'right',
          name: 'washedPackages',
          label: '清洗包盤數',
          field: 'washedPackages',
        },
        { align: 'right', name: 'washedCount', label: '清洗次數', field: 'washedCount' },
        {
          align: 'left',
          name: 'action',
          label: '使用率',
          field: 'action',
          headerStyle: 'width: 25%;',
        },
      ],
      values: [],
      isLoading: false,
      date: {
        start: '',
        end: '',
      },
      isQuery: false,
      analysisInfo: {},
    };
  },
  methods: {
    async queryReport() {
      try {
        this.isQuery = true;
        const { start, end } = this.date;
        const params = { start, end };
        const res = await apiQueryWashAnalysis(params);
        const { washingMachines, ...rest } = res.data;
        this.analysisInfo = rest;
        this.values = washingMachines;
      } catch (error) {
        const msg = error.data?.msg || '發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.isQuery = false;
      }
    },
    presetQueryReport(val) {
      if (val && !this.isQuery) {
        const { to, from } = val;
        this.date.start = from;
        this.date.end = to;
        this.queryReport();
      }
    },
  },
  watch: {
    'reportDate.value'(val) {
      const date = typeof val === 'string' ? { to: val, from: val } : val;
      this.presetQueryReport(date);
    },
  },
  created() {
    this.presetQueryReport(this.reportDate.value);
  },
};
</script>
