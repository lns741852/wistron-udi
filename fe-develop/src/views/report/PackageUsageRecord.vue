<template>
  <VBreadCrumb backTitle="管理區" />
  <div class="bg-white q-pa-lg q-mb-lg">
    <p class="text-weight-bold q-mb-sm">各科包盤</p>
    <q-form @submit.prevent="getHistoryList" class="row q-col-gutter-lg">
      <div class="col-6">
        <label>包盤編號</label>
        <q-input outlined dense maxlength="32" v-model="query.configCode" />
      </div>
      <div class="col-6">
        <label>包盤序號</label>
        <q-input outlined dense maxlength="32" v-model="query.serialNo" />
      </div>
      <div class="col-6">
        <label>病歷號</label>
        <q-input outlined dense maxlength="32" v-model="query.medicalRecordNo" />
      </div>
      <div class="col-6">
        <label>使用日期</label>
        <VDateRange
          class="border-darkGray"
          :dense="false"
          :dateRange="date"
          @updateDate="updateUsageDate"
        />
      </div>
      <div class="col-6">
        <label>滅菌鍋</label>
        <q-select
          outlined
          v-model="query.sterilizer"
          :options="sterilizerOptions"
          dense
          options-dense
          clearable
          class="full-width"
        >
        </q-select>
      </div>
      <div class="col-6">
        <label>滅菌日期</label>
        <q-input
          mask="####-##-##"
          :rules="[(v) => !Number.isNaN(new Date(v).valueOf())]"
          dense
          outlined
          today-btn
          v-model="query.sterilizationDate"
          clearable
        >
          <template v-slot:append>
            <q-icon name="event" class="cursor-pointer">
              <q-popup-proxy transition-show="scale" transition-hide="scale">
                <q-date v-model="query.sterilizationDate">
                  <div class="row items-center justify-end">
                    <q-btn v-close-popup label="Close" color="primary" flat />
                  </div>
                </q-date>
              </q-popup-proxy>
            </q-icon>
          </template>
        </q-input>
      </div>
      <div class="col-12">
        <div class="flex justify-end">
          <q-btn
            color="blue"
            label="查詢"
            class="q-px-lg"
            type="button"
            @click="getHistoryList(1)"
          />
        </div>
      </div>
    </q-form>
  </div>
  <div class="bg-white q-pa-lg">
    <div class="bg-white">
      <VTable
        rowKey="trackingId"
        :columns="columns"
        :rows="values"
        :loading="isLoading"
        :currentPage="currentPage"
        :totalPages="totalPages"
        :bodySlots="['action']"
        @changePage="getHistoryList"
      >
        <template #action="props">
          <q-btn
            dense
            round
            flat
            color="grey"
            icon="visibility"
            @click="queryTrackingDetail(props.row.trackingId)"
          />
        </template>
      </VTable>
    </div>
  </div>
</template>

<script>
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import VDateRange from '@/components/VDateRange.vue';
import { apiQueryPackHistory, apiQueryAttr } from '@/api';

const defaultQuery = {
  configCode: '',
  serialNo: '',
  start: '',
  end: '',
  sterilizationDate: '',
  sterilizer: '',
  medicalRecordNo: '',
};

export default {
  name: 'packageUsageRecord',
  components: {
    VBreadCrumb,
    VDateRange,
  },
  data() {
    return {
      tempQuery: '',
      query: { ...defaultQuery },
      columns: [
        {
          align: 'left',
          name: 'configCode',
          label: '包盤編號',
          field: 'configCode',
        },
        {
          align: 'left',
          name: 'serialNo',
          label: '包盤序號',
          field: 'serialNo',
        },
        { align: 'left', name: 'configName', label: '包盤名稱', field: 'configName' },
        {
          align: 'left',
          name: 'divisionId',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        { align: 'left', name: 'medicalRecordNo', label: '病歷號', field: 'medicalRecordNo' },
        { align: 'left', name: 'sterilizer', label: '滅菌鍋', field: 'sterilizer' },
        { align: 'left', name: 'sterilizationDate', label: '滅菌日期', field: 'sterilizationDate' },
        { align: 'left', name: 'usedDate', label: '使用日期', field: 'usedDate' },
        { align: 'left', name: 'expireDate', label: '有效日期', field: 'expireDate' },
        { align: 'center', name: 'action', label: '', field: 'action', headerStyle: 'width: 15%;' },
      ],
      values: [],
      isLoading: false,
      sterilizerOptions: [],
      totalPages: 0,
      currentPage: 0,
      date: null,
    };
  },
  computed: {
    queryString() {
      return Object.keys(this.query).reduce(
        (acc, key) => (this.query[key] ? { ...acc, [key]: this.query[key] } : acc),
        {},
      );
    },
  },
  methods: {
    async getHistoryList(page = 1) {
      this.isLoading = true;
      try {
        const params = {
          ...this.queryString,
          page: page - 1,
        };
        const res = await apiQueryPackHistory(params);
        const { content, totalPages } = res.data;
        this.currentPage = page;
        this.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.isLoading = false;
      }
    },
    updateUsageDate(date) {
      if (typeof date === 'string' && !Number.isNaN(new Date(date).valueOf())) {
        this.query.start = date;
        this.query.end = date;
      }
      if (typeof date === 'object') {
        if ('from' in date && 'to' in date) {
          const { from, to } = date;
          this.query.start = from;
          this.query.end = to;
        }
      }
    },
    async querySterilizerAttr() {
      try {
        const res = await apiQueryAttr('sterilizer');
        this.sterilizerOptions = res.data;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    queryTrackingDetail(trackingId) {
      this.$router.push({ name: 'packageTrackingDetail', params: { trackingId } });
    },
  },
  mounted() {
    this.querySterilizerAttr();
    if (this.query.start && this.query.end) {
      this.getHistoryList();
    }
  },
  created() {
    const startDate = new Date().getTime() - 7 * 864e5;
    const from = this.$utilsFilters.dateStr(startDate).slice(0, 10);
    const to = this.$utilsFilters.dateStr().slice(0, 10);
    this.date = { from, to };
  },
};
</script>
