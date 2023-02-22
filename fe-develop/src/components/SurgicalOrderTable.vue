<template>
  <div>
    <SearchBar
      v-if="hasSearchBar"
      ref="searchBar"
      :searchConfig="['qrcode']"
      @queryContentArray="queryContentArray"
    />
    <q-table
      :columns="tableColumns"
      :rows="values"
      :loading="isLoading"
      :bordered="false"
      selection="single"
      v-model:selected="dataSelected"
      :pagination="pagination"
      flat
      :row-key="dataCategory === 'order' ? 'orderId' : 'applyId'"
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
            @update:model-value="(val) => dispatchQueryData(dataCategory, val)"
            size="sm"
          />
        </div>
      </template>
    </q-table>
  </div>
</template>

<script>
import { apiApplyOrders, apiSurgicalApply } from '@/api';
import SearchBar from '@/components/SearchBar.vue';

export default {
  name: 'SurgicalOrderTable',
  components: {
    SearchBar,
  },
  props: {
    // order | surgical
    dataCategory: {
      type: String,
      required: true,
    },
    // 0: 未領用; 1:已領用; 2:使用中;3:使用完成;4:回收清點完成;
    orderStatus: {
      type: Number,
    },
    // 非必填，不選則全撈 0:申請中; 1:已審核; 2:已發放; 3:使用中; 4:使用完成(全部package_order.status都已使用完成才會壓); 5:回收完成
    surgicalStatus: {
      type: [Number, String],
    },
    hasSearchBar: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['dataSelected'],
  data() {
    return {
      columns: [
        {
          align: 'left',
          name: 'operatingNumber',
          label: '手術編號',
          field: 'operatingNumber',
        },
        { align: 'left', name: 'orderId', label: '訂單號碼', field: 'orderId' },
        {
          align: 'left',
          name: 'division',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        {
          align: 'left',
          name: 'medicalRecordNumber',
          label: '病歷號',
          field: 'medicalRecordNumber',
        },
        { align: 'left', name: 'surgeryName', label: '手術名稱', field: 'surgeryName' },
        { align: 'left', name: 'doctor', label: '醫生', field: 'doctor' },
        { align: 'left', name: 'operatingRoom', label: '手術室', field: 'operatingRoom' },
        { align: 'left', name: 'operatingDate', label: '手術日期', field: 'operatingDate' },
        { align: 'left', name: 'operatingOrder', label: '排刀順序', field: 'operatingOrder' },
        { align: 'left', name: 'action', label: '', field: 'action', headerStyle: 'width: 5%;' },
      ],
      values: [],
      dataSelected: [],
      pagination: {
        sortBy: 'desc',
        descending: false,
        page: 0,
        rowsPerPage: 10,
        totalPages: 1,
      },
      isLoading: false,
      queryString: {},
    };
  },
  computed: {
    tableColumns() {
      return this.dataCategory === 'surgical'
        ? this.columns.filter((item) => item.name !== 'orderId')
        : this.columns;
    },
  },
  methods: {
    async queryApplyOrders(page = 1) {
      this.isLoading = true;
      try {
        const data = {
          status: this.orderStatus,
          sort: 'desc',
        };
        const res = await apiApplyOrders({ page: page - 1, ...data });
        const { content, totalPages } = res.data;
        this.pagination.page = page;
        this.pagination.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      } finally {
        this.isLoading = false;
      }
    },
    async querySurgicalApply(page = 1) {
      this.isLoading = true;
      try {
        const params = {
          ...this.queryString,
          status: this.surgicalStatus,
        };
        const res = await apiSurgicalApply({ page: page - 1, ...params });
        const { content, totalPages } = res.data;
        this.pagination.page = page;
        this.pagination.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      } finally {
        this.isLoading = false;
      }
    },
    async dispatchQueryData(category, page = 1) {
      if (category === 'order') {
        await this.queryApplyOrders(page);
      } else if (category === 'surgical') {
        await this.querySurgicalApply(page);
      }
    },
    async init() {
      const category = ['order', 'surgical'];
      if (category.includes(this.dataCategory)) {
        await this.dispatchQueryData(this.dataCategory);
        if (this.values.length > 0) {
          const [first] = this.values;
          this.dataSelected = [first];
        } else {
          this.dataSelected = [];
        }
      }
    },
    reset() {
      if (this.hasSearchBar) {
        this.$refs.searchBar.searchDefault();
      }
      this.init();
    },
    queryContentArray(content) {
      this.queryString = content.reduce(
        (acc, current) => ({
          ...acc,
          [current.key]: current.value,
        }),
        {},
      );
      this.dispatchQueryData(this.dataCategory);
    },
  },
  watch: {
    dataSelected: {
      immediate: true,
      handler(val) {
        this.$emit('dataSelected', val);
      },
    },
    surgicalStatus() {
      this.init();
    },
  },
  mounted() {
    this.init();
  },
};
</script>
