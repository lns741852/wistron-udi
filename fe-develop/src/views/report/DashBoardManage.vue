<template>
  <div>
    <h4 class="q-mb-md text-weight-bold">總覽</h4>
    <ul class="row bg-white q-mb-md">
      <li class="col-3 align-self-center" v-for="(pk, idx) in areasPackagesQty" :key="pk.name">
        <div class="text-center q-py-md" :style="areasPackagesQty.length === idx + 1 ? '' : border">
          <q-img
            class="q-mb-xs"
            :src="pk.img"
            :alt="pk.name"
            width="25px"
            height="25px"
            fit="contain"
          />
          <h5 class="text-weight-bold q-mb-xs">{{ pk.value }}</h5>
          <p class="text-subtitle2 text-grey">{{ pk.name }}</p>
        </div>
      </li>
    </ul>
    <div class="row q-col-gutter-lg q-mb-md">
      <div class="col-12 col-sm-4">
        <div class="bg-white q-pa-md full-width">
          <p class="text-weight-bold ">包盤狀態</p>
          <VPieChart :content="pieChartContent" />
        </div>
      </div>
      <div class="col-12 col-sm-8">
        <div class="bg-white q-pa-md full-width">
          <VTable
            rowKey="divisionId"
            title="各科包盤"
            :columns="columns"
            :rows="dPackages"
            :loading="isLoading"
            :currentPage="currentPage"
            :totalPages="pagination.totalPages"
            @changePage="(val) => (currentPage = val)"
          />
        </div>
      </div>
    </div>
    <div class="bg-white q-pa-md">
      <p class="text-weight-bold q-ml-md q-mb-sm">包盤類型</p>
      <PackagesTypesTable />
    </div>
  </div>
</template>

<script>
import { apiQueryPackagesResult } from '@/api';
import VPieChart from '@/components/VPieChart.vue';
import PackagesTypesTable from './components/PackagesTypesTable.vue';

export default {
  name: 'manageDashBoard',
  components: {
    PackagesTypesTable,
    VPieChart,
  },
  data() {
    return {
      systemPackage: {},
      columns: [
        {
          align: 'left',
          name: 'divisionId',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        { align: 'right', name: 'configQty', label: '包盤類型數', field: 'configQty' },
        { align: 'right', name: 'packageQty', label: '包盤總數', field: 'packageQty' },
      ],
      isLoading: false,
      border: {
        borderRight: '1px solid #e5e6e6',
      },
      currentPage: 1,
    };
  },
  computed: {
    areasPackagesQty() {
      if (!this.systemPackage.stations) return [];
      const { packing = 0, sterilization, circulation, supply } = this.systemPackage.stations;
      return [
        {
          name: '配盤區',
          value: packing,
          img: '/icons/dashboard-packaging.png',
        },
        {
          name: '滅菌區',
          value: sterilization,
          img: '/icons/dashboard-disinfect.png',
        },
        {
          name: '回收清洗區',
          value: circulation,
          img: '/icons/dashboard-recycle.png',
        },
        {
          name: '庫存區',
          value: supply,
          img: '/icons/dashboard-inventory.png',
        },
      ];
    },
    pieChartContent() {
      if (!this.systemPackage.status) return [];
      const { available, process: statusProcess, used, expired } = this.systemPackage.status;
      return [
        {
          name: '可用庫存量',
          value: available,
        },
        {
          name: '已過期',
          value: expired,
        },
        {
          name: '處理中',
          value: statusProcess,
        },
        {
          name: '手術使用中',
          value: used,
        },
      ];
    },
    dPackages() {
      if (this.packageValues.length === 0) return [];
      const data = [];
      const { page: current, totalPages: total, rowsPerPage: size } = this.pagination;
      const maxNumber = total === current ? this.packageValues.length : current * size;
      const fromNumber = (current - 1) * size;
      for (let i = fromNumber; i < maxNumber; i += 1) {
        data.push(this.packageValues[i]);
      }
      return data;
    },
    packageValues() {
      return this.systemPackage.divisionPackages || [];
    },
    pagination() {
      return {
        sortBy: 'desc',
        descending: false,
        page: this.currentPage,
        rowsPerPage: 5,
        totalPages: Math.ceil(this.packageValues.length / 5),
      };
    },
  },
  methods: {
    async queryPackageDashBoard() {
      try {
        const res = await apiQueryPackagesResult();
        this.systemPackage = res.data;
      } catch (error) {
        const msg = error.data.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      }
    },
  },
  created() {
    this.queryPackageDashBoard();
  },
};
</script>
