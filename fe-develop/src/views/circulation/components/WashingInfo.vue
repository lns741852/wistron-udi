<template>
  <div class="bg-white q-pa-lg">
    <SearchBar :searchConfig="searchConfig" @queryContentArray="queryContentArray" />

    <VTable
      rowKey="id"
      :columns="sterilizationCol"
      :rows="values"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :bodySlots="['status', 'action']"
      @changePage="queryWashing"
    >
      <template #status="props">
        <q-badge transparent align="middle" class="q-pa-xs" :color="badge(props.row).color">
          {{ badge(props.row).label }}
        </q-badge>
      </template>

      <template #action="props" v-if="mode === 'action'">
        <q-btn
          v-if="props.row.startTime && !props.row.finishTime"
          icon="check"
          dense
          size="12px"
          color="secondary"
          class="q-px-md"
          @click="fillFinish(props.row)"
          >完成清洗</q-btn
        >
      </template>
    </VTable>

    <WashingFinishForm
      ref="washingForm"
      v-if="currentItem.id && mode === 'action'"
      :id="currentItem.id"
      :title="currentItem.name"
      @washingFinish="queryWashing(1)"
    />
  </div>
</template>

<script>
import { apiQueryWashing } from '@/api';
import SearchBar from '@/components/SearchBar.vue';
import WashingFinishForm from './WashingFinishForm.vue';

export default {
  components: {
    SearchBar,
    WashingFinishForm,
  },
  props: {
    mode: {
      type: String,
      required: true,
      validator: (value) => ['view', 'action'].indexOf(value) !== -1,
    },
  },
  data() {
    return {
      currentItem: {
        id: null,
        name: '',
      },
      columns: [
        {
          headerStyle: 'width: 20%;',
          align: 'left',
          name: 'name',
          label: '批次名稱',
          field: 'name',
        },
        {
          align: 'left',
          name: 'washingMachine',
          label: '清洗機',
          field: 'washingMachine',
        },
        {
          align: 'left',
          name: 'startTime',
          label: '開始時間',
          field: (val) => this.$utilsFilters.dateStr(val.startTime),
        },
        {
          align: 'left',
          name: 'finishTime',
          label: '結束時間',
          field: (val) => (val.finishTime ? this.$utilsFilters.dateStr(val.finishTime) : '-'),
        },
        { align: 'right', name: 'qty', label: '總數量', field: 'qty' },
        { align: 'center', name: 'status', label: '狀態', field: 'status' },
      ],
      values: [],
      isLoading: false,
      currentPage: 1,
      totalPages: 0,
      queryString: {},
      defaultSterType: { type: 1 },
    };
  },
  computed: {
    badge() {
      return (item) => {
        const list = [0, 1];
        const isFinished = list.includes(item.status);
        if (isFinished) {
          return item.status
            ? { label: '清洗完成', color: 'secondary' }
            : { label: '清洗失敗', color: 'accent' };
        }
        const isSterilizedBegin = Date.now() > new Date(item.startTime);
        return isSterilizedBegin
          ? { label: '清洗中', color: 'grey' }
          : { label: '未清洗', color: 'grey' };
      };
    },
    searchConfig() {
      const commonConfig = [{ key: 'name', name: 'dispatchName' }];
      return this.mode === 'view'
        ? [{ key: 'type', name: 'washingType' }, ...commonConfig]
        : [...commonConfig];
    },
    sterilizationCol() {
      return this.mode === 'view'
        ? this.columns
        : [...this.columns, { align: 'left', name: 'action', label: '功能', field: 'action' }];
    },
  },
  methods: {
    async queryWashing(page = 1) {
      this.$gLoading(true);
      try {
        const addParams = this.mode === 'view' ? {} : this.defaultSterType;
        const res = await apiQueryWashing({
          ...addParams,
          ...this.queryString,
          page: page - 1,
        });
        const { content, totalPages } = res.data;
        this.currentPage = page;
        this.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        this.$gNotifyError('發生異常');
      } finally {
        this.$gLoading(false);
      }
    },
    fillFinish({ id, name }) {
      this.currentItem.id = id;
      this.currentItem.name = name;
      this.$nextTick(() => {
        this.$refs.washingForm.showDialog = true;
      });
    },
    queryContentArray(content) {
      this.queryString = content.reduce(
        (acc, current) => ({
          ...acc,
          [current.key]: current.value,
        }),
        {},
      );
      this.queryWashing(1);
    },
  },
  created() {
    this.queryWashing(1);
  },
};
</script>
