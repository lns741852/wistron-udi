<template>
  <div>
    <div class="row q-mb-md">
      <div class="col-12 col-sm-6 q-py-md bg-white text-center border-right">
        <p class="text-subtitle2 text-grey">包盤種類數</p>
        <h5 class="text-weight-bold q-mb-xs">{{ totalTypesCount }}</h5>
        <p class="text-subtitle2 text-grey">種</p>
      </div>
      <div class="col-12 col-sm-6 q-py-md bg-white text-center">
        <p class="text-subtitle2 text-grey">製作包盤量</p>
        <h5 class="text-weight-bold q-mb-xs">{{ totalPackingCount }}</h5>
        <p class="text-subtitle2 text-grey">種</p>
      </div>
    </div>
    <div class="bg-white q-pa-md">
      <p class="text-weight-bold q-ml-sm q-mb-sm">各科包盤</p>
      <VTable
        rowKey="configCode"
        :columns="columns"
        :rows="values"
        :loading="isLoading"
        :currentPage="currentPage"
        :totalPages="totalPages"
        @changePage="queryReport"
      />
    </div>
  </div>
</template>

<script>
import { apiQueryWrapPkAnalysis } from '@/api';

export default {
  inject: ['reportDate'],
  data() {
    return {
      columns: [
        { align: 'left', name: 'configCode', label: '包盤編號', field: 'configCode' },
        { align: 'left', name: 'packageName', label: '包盤名稱', field: 'packageName' },
        {
          align: 'left',
          name: 'divisionId',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        { align: 'right', name: 'packageQty', label: '包盤數量', field: 'packageQty' },
        { align: 'right', name: 'packingCount', label: '製作數量', field: 'packingCount' },
      ],
      values: [],
      isLoading: false,
      date: {
        start: '',
        end: '',
      },
      totalPackingCount: 0,
      totalTypesCount: 0,
      isQuery: false,
      currentPage: 0,
      totalPages: 0,
    };
  },

  methods: {
    async queryReport(page) {
      try {
        this.isQuery = true;
        const { start, end } = this.date;
        const params = {
          start,
          end,
          page: page - 1,
        };
        const res = await apiQueryWrapPkAnalysis(params);
        const { totalPackingCount, packageConfigs } = res.data;
        const { content, totalPages, totalElements } = packageConfigs;
        this.totalPackingCount = totalPackingCount;
        this.totalTypesCount = totalElements;
        this.currentPage = page;
        this.totalPages = totalPages;
        this.values = content;
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
        this.queryReport(1);
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
