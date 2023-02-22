<template>
  <q-breadcrumbs class="q-mb-lg">
    <q-breadcrumbs-el class="text-info text-weight-bold" label="器械類型列表" />
  </q-breadcrumbs>
  <div class="bg-white q-pa-lg">
    <div class="row items-start justify-between">
      <SearchBar
        :searchConfig="['nameScientific', 'spec']"
        @queryContentArray="queryContentArray"
      />
      <q-btn
        color="info"
        icon="add_circle_outline"
        label="新增器械種類"
        class="q-mb-sm"
        @click="$refs.typeDialog.showDialog = true"
      />
    </div>
    <VTable
      rowKey="id"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :bodySlots="['typeInfo', 'action']"
      @changePage="queryDeviceTypes"
    >
      <template #typeInfo="props">
        <div class="row items-center no-wrap">
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
      </template>
      <template #action="props">
        <q-btn
          dense
          round
          flat
          class="q-pr-lg"
          color="grey"
          icon="visibility"
          @click="viewDetail(props.row)"
        />
      </template>
    </VTable>

    <DeviceTypeDialog ref="typeDialog" @modifySuccess="init" />
  </div>
</template>
<script>
import { apiQueryDeviceTypes } from '@/api';
import DeviceTypeDialog from './components/DeviceTypeTransaction.vue';
import SearchBar from '@/components/SearchBar.vue';

export default {
  name: 'devicesTypes',
  components: {
    DeviceTypeDialog,
    SearchBar,
  },
  data() {
    return {
      isLoading: false,
      queryString: {},
      columns: [
        {
          headerStyle: 'width: 40%;',
          align: 'left',
          name: 'typeInfo',
          label: '器械類型',
          field: 'typeInfo',
        },
        { align: 'left', name: 'spec', label: '規格', field: 'spec' },
        { name: 'totalQty', label: '總數', field: 'totalQty' },
        { align: 'left', name: 'action', label: '功能', field: 'action' },
      ],
      values: [],
      currentPage: 1,
      totalPages: 1,
    };
  },
  methods: {
    async queryDeviceTypes(page = 1) {
      this.isLoading = true;
      try {
        const params = {
          page: page - 1,
          ...this.queryString,
        };
        const res = await apiQueryDeviceTypes(params);
        const { content, totalPages } = res.data;
        this.currentPage = page;
        this.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      } finally {
        this.isLoading = false;
        this.$gLoading(false);
      }
    },
    queryContentArray(content) {
      this.queryString = content.reduce(
        (acc, current) => ({
          ...acc,
          [current.key]: current.value,
        }),
        {},
      );
      this.queryDeviceTypes();
    },
    viewDetail(data) {
      this.$router.push(`/deviceDetail/${data.id}`);
    },
    init() {
      this.$gLoading(true);
      const routerPage = this.$route.params.page;
      const page = routerPage || 1;
      this.queryDeviceTypes(page);
    },
  },
  created() {
    this.init();
  },
};
</script>
