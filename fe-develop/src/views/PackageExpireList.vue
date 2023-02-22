<template>
  <!-- <VBreadCrumb backTitle="庫存區" /> -->
  <div class="flex justify-between q-mb-md">
    <h4 class="text-weight-bold">{{ $route.meta?.title }}</h4>
  </div>

  <div class="bg-white q-py-md q-mb-lg">
    <q-list>
      <q-item tag="label">
        <q-item-section avatar>
          <q-radio v-model="queryType" val="expired" color="secondary" />
        </q-item-section>
        <q-item-section>
          <q-item-label class="q-py-sm">已到期</q-item-label>
        </q-item-section>
      </q-item>

      <q-item tag="label">
        <q-item-section avatar top>
          <q-radio v-model="queryType" val="restDays" color="secondary" />
        </q-item-section>
        <q-item-section>
          <q-item-label class="q-py-sm">剩餘有效天數</q-item-label>

          <q-item-label class="row items-center"
            ><q-input
              @focus="queryType = 'restDays'"
              min="1"
              type="number"
              v-model="query.day"
              dense
              outlined
              style="width: 120px"
              class="q-mr-xs"
              @update:model-value="
                (val) => {
                  if (val < 1) query.day = 1;
                }
              "
            />
            天
          </q-item-label>
        </q-item-section>
      </q-item>

      <q-item tag="label">
        <q-item-section avatar top>
          <q-radio v-model="queryType" val="expiredRange" color="secondary" />
        </q-item-section>
        <q-item-section>
          <q-item-label class="q-py-sm">到期區間</q-item-label>

          <q-item-label @click="queryType = 'expiredRange'">
            <VDateRangePicker
              @start="(val) => (query.start = val)"
              @end="(val) => (query.end = val)"
            />
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
    <div class="flex justify-end">
      <q-btn color="blue" label="查詢" class="q-px-lg q-mr-md" type="button" @click="beforeQuery" />
    </div>
  </div>

  <div class="bg-white q-pa-lg">
    <VTable
      rowKey="id"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      @changePage="changePage"
    />
  </div>
</template>

<script>
import { apiPackageExpiry } from '@/api';
import VDateRangePicker from '@/components/VDateRangePicker.vue';

export default {
  name: 'PackageExpireList',
  components: {
    VDateRangePicker,
  },
  data() {
    return {
      queryType: 'expired',
      query: {
        day: 1,
        start: '',
        end: '',
      },
      columns: [
        {
          headerStyle: 'width: 20%;',
          align: 'left',
          name: 'configCode',
          label: '包盤編號',
          field: 'configCode',
        },
        { align: 'left', name: 'serialNo', label: '包盤序號', field: 'serialNo' },
        { align: 'left', name: 'configName', label: '包盤名稱', field: 'configName' },
        {
          align: 'left',
          name: 'divisionId',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        { align: 'left', name: 'sterilizationDate', label: '滅菌日', field: 'sterilizationDate' },
        { align: 'left', name: 'position', label: '放置位置', field: 'position' },
        {
          align: 'left',
          name: 'expireDate',
          label: '有效日期',
          field: 'expireDate',
        },
      ],
      values: [],
      currentPage: 1,
      totalPages: 0,
      isLoading: false,
    };
  },
  methods: {
    async getList({ timeParams = {}, page = 1 } = {}) {
      try {
        this.isLoading = true;
        const params = { ...timeParams, page: page - 1 };
        const res = await apiPackageExpiry(params);
        const { content, totalPages } = res.data;
        this.currentPage = page;
        this.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.isLoading = false;
      }
    },
    getDateByDays(day) {
      if (!day) {
        return false;
      }
      const date = new Date();
      const futureDate = date.getTime() + day * 24 * 60 * 60 * 1000;
      return this.$utilsFilters.dateStr(futureDate)?.slice(0, 10);
    },
    beforeQuery() {
      if (this.queryType === 'expired') {
        this.getList();
      } else if (this.queryType === 'restDays') {
        const start = this.getDateByDays(1);
        const end = this.getDateByDays(this.query.day);
        this.getList({ timeParams: { start, end } });
      } else if (this.queryType === 'expiredRange') {
        if (!this.query.start || !this.query.end) {
          this.$gNotifyError('選擇日期');
          return;
        }
        this.getList({ timeParams: { start: this.query.start, end: this.query.end } });
      }
    },
    changePage(page) {
      this.currentPage = page;
      this.getList({ page });
    },
  },
  async created() {
    try {
      this.getList();
    } catch (error) {
      // regardless error
    }
  },
};
</script>
