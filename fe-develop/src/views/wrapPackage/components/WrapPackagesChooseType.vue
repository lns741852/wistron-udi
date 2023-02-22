<template>
  <div class="q-pa-lg">
    <SearchBar
      :searchConfig="['configCode', 'configName', { key: 'divisionId', name: 'division' }]"
      @queryContentArray="queryContentArray"
    />
    <q-table
      :columns="configColumns"
      :rows="configValues"
      :loading="isLoading"
      :bordered="false"
      selection="single"
      v-model:selected="configSelected"
      flat
      row-key="id"
      hide-pagination
      :pagination="pagination"
    >
      <template v-slot:header="props">
        <q-tr :props="props">
          <q-th v-for="col in props.cols" :key="col.name" :props="props">
            {{ col.label }}
          </q-th>
        </q-tr>
      </template>
      <template v-slot:body="props">
        <q-tr class="cursor-pointer" :props="props" @click="props.selected = !props.selected">
          <q-td v-for="col in props.cols" :key="col.name" :props="props">
            <div v-if="col.name === 'action' && props.selected">
              <q-avatar size="sm" color="secondary" text-color="white" icon="check" />
            </div>
            <span v-else-if="col.name === 'status'">
              <q-badge
                transparent
                align="middle"
                class="q-pa-xs q-mb-sm"
                :color="`${$statusFilters.packageApply(props.row.status)?.color}`"
              >
                {{ $statusFilters.packageApply(props.row.status)?.name }}
              </q-badge>
            </span>
            <span v-else>
              {{ col.value }}
            </span>
          </q-td>
        </q-tr>
      </template>
      <template v-slot:bottom>
        <div class="row items-center justify-end full-width">
          <q-pagination
            v-model="pagination.page"
            direction-links
            unelevated
            round
            active-color="secondary"
            active-text-color="white"
            :max="pagination.totalPages"
            @update:model-value="queryPackageApplies"
            size="sm"
          />
        </div>
      </template>
    </q-table>
  </div>
</template>

<script>
import SearchBar from '@/components/SearchBar.vue';
import { apiQueryPackageApplies } from '@/api';

export default {
  name: 'choosePackageType',
  components: {
    SearchBar,
  },
  data() {
    return {
      configColumns: [
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
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name || val.divisionId,
        },
        { align: 'right', name: 'qty', label: '申請數量', field: 'qty' },
        { align: 'right', name: 'packagedQty', label: '已完成數量', field: 'packagedQty' },
        {
          align: 'center',
          name: 'status',
          label: '狀態',
          field: 'status',
        },
        { align: 'left', name: 'action', label: '', field: 'action', headerStyle: 'width: 5%;' },
      ],
      configValues: [],
      pagination: {
        sortBy: 'desc',
        descending: false,
        page: 0,
        rowsPerPage: 10,
        totalPages: 1,
      },
      configSelected: [],
      queryString: {},
      isLoading: false,
    };
  },
  methods: {
    async queryPackageApplies(page = 1) {
      this.isLoading = true;
      try {
        const data = {
          ...this.queryString,
          orderBy: 'desc',
        };
        const res = await apiQueryPackageApplies({ page: page - 1, data });
        const { content, totalPages } = res.data;
        this.pagination.page = page;
        this.pagination.totalPages = totalPages;
        this.configValues = content;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      } finally {
        this.isLoading = false;
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
      this.queryPackageApplies(1);
    },
    async init() {
      await this.queryPackageApplies();
      this.configSelected = [];
      if (this.configValues.length > 0) {
        const [x] = this.configValues;
        this.configSelected = [x];
      }
    },
  },
  watch: {
    configSelected(val) {
      this.$emit('configSelected', val);
    },
  },
  created() {
    this.init();
  },
};
</script>
