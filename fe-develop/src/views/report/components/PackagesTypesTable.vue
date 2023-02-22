<template>
  <VTable
    rowKey="configId"
    :columns="columns"
    :rows="values"
    :loading="isLoading"
    :currentPage="currentPage"
    :totalPages="totalPages"
    :bodySlots="['action']"
    @changePage="queryPackageConfig"
  >
    <template #action="props">
      <q-btn
        dense
        round
        flat
        color="grey"
        icon="visibility"
        @click="queryPackageConfigDetail(props.row)"
      />
    </template>
  </VTable>

  <VDialog
    v-model:show="showDialog"
    title="包盤類型明細"
    dialogWidth="max-width: 750px; width: 100%"
  >
    <template #content>
      <DeviceTypeInfo :contents="targetTypes" />
    </template>
  </VDialog>
</template>

<script>
import { apiQueryPackageConf, apiGetPkConfig } from '@/api';
import DeviceTypeInfo from '@/views/report/components/DeviceTypeInfo.vue';

export default {
  name: 'PackagesTypesTable',
  components: {
    DeviceTypeInfo,
  },
  data() {
    return {
      columns: [
        {
          headerStyle: 'width: 20%;',
          align: 'left',
          name: 'configCode',
          label: '包盤編號',
          field: 'configCode',
        },
        { align: 'left', name: 'configName', label: '包盤名稱', field: 'configName' },
        {
          align: 'left',
          name: 'division',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        { align: 'right', name: 'packageQty', label: '數量', field: 'packageQty' },
        {
          name: 'inStockQty',
          label: '庫存數量',
          field: (val) => val.inStockQty || 0,
        },
        { align: 'right', name: 'totalUsedCount', label: '包盤使用總數', field: 'totalUsedCount' },
        {
          align: 'left',
          name: 'isActive',
          label: '開啟',
          field: (val) => (val.isActive ? '上架中' : '已下架'),
        },
        {
          align: 'left',
          name: 'action',
          label: '功能',
          field: 'action',
        },
      ],
      values: [],
      isLoading: false,
      totalPages: 0,
      currentPage: 0,
      targetTypes: [],
      showDialog: false,
    };
  },
  methods: {
    async queryPackageConfig(page = 1) {
      try {
        const params = {
          page: page - 1,
          showCount: 1,
        };
        const res = await apiQueryPackageConf(params);
        const { content, totalPages } = res.data;
        this.values = content;
        this.currentPage = page;
        this.totalPages = totalPages;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    // 取得特定資訊
    async queryPackageConfigDetail({ configId }) {
      try {
        if (!configId) return;
        const res = await apiGetPkConfig({ configId });
        const { deviceTypes } = res.data;
        this.targetTypes = deviceTypes;
        this.showDialog = true;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      }
    },
  },
  created() {
    this.queryPackageConfig();
  },
};
</script>
