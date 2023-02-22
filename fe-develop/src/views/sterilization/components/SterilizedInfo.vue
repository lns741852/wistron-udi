<template>
  <div class="bg-white q-pa-lg">
    <SearchBar :searchConfig="searchConfig" @queryContentArray="queryContentArray" />

    <VTable
      rowKey="id"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :bodySlots="['status', 'action']"
      @changePage="queryQuerySterilized"
    >
      <template #status="props">
        <q-badge transparent align="middle" class="q-pa-xs" :color="badge(props.row).color">
          {{ badge(props.row).label }}
        </q-badge>
      </template>

      <template #action="props">
        <q-btn
          v-if="mode === 'action'"
          dense
          rounded
          size="12px"
          color="info"
          class="q-px-md"
          @click="fillSterilizedFinish(props.row)"
          >完成滅菌</q-btn
        >
        <q-btn
          v-if="mode === 'view' && (props.row.status === 1 || props.row.status === 0)"
          dense
          round
          flat
          size="12px"
          color="grey"
          icon="visibility"
          @click="getSterilizedDetail(props.row.id)"
        />
      </template>
    </VTable>

    <SterilizationFinishForm
      ref="sterilizedForm"
      v-if="currentItem.id && mode === 'action'"
      :id="currentItem.id"
      :title="currentItem.name"
      @sterilizationFinish="queryQuerySterilized(1)"
    />

    <SterilizationView
      ref="sterilizedView"
      v-if="targetViewItem.id && mode === 'view'"
      :detail="targetViewItem"
    />
  </div>
</template>

<script>
import { apiQuerySterilized, apiGetSterilizedDetail } from '@/api';
import SearchBar from '@/components/SearchBar.vue';
import SterilizationFinishForm from './SterilizationFinishForm.vue';
import SterilizationView from '@/views/sterilization/components/SterilizationView.vue';

export default {
  components: {
    SearchBar,
    SterilizationFinishForm,
    SterilizationView,
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
      targetViewItem: {},
      columns: [
        {
          headerStyle: 'width: 20%;',
          align: 'left',
          name: 'name',
          label: '鍋次名稱',
          field: 'name',
        },
        {
          align: 'left',
          name: 'sterilizer',
          label: '消毒鍋',
          field: 'sterilizer',
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
        { align: 'left', name: 'action', label: '功能', field: 'action' },
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
    querySterilizeType() {
      return this.queryString.type;
    },
    badge() {
      return (item) => {
        const list = [0, 1];
        const isFinished = list.includes(item.status);
        if (isFinished) {
          return item.status
            ? { label: '滅菌完成', color: 'secondary' }
            : { label: '滅菌失敗', color: 'accent' };
        }
        const isSterilizedBegin = Date.now() > new Date(item.startTime);
        return isSterilizedBegin
          ? { label: '滅菌中', color: 'grey' }
          : { label: '未滅菌', color: 'grey' };
      };
    },
    searchConfig() {
      const commonConfig = [{ key: 'name', name: 'dispatchName' }];
      return this.mode === 'view'
        ? [{ key: 'type', name: 'sterilizedType' }, ...commonConfig]
        : [...commonConfig];
    },
    sterilizationCol() {
      return this.mode === 'view'
        ? this.columns
        : [...this.columns, { align: 'left', name: 'action', label: '功能', field: 'action' }];
    },
  },
  methods: {
    async queryQuerySterilized(page = 1) {
      this.$gLoading(true);
      try {
        const addParams = this.mode === 'view' ? {} : this.defaultSterType;
        const res = await apiQuerySterilized({
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
    async getSterilizedDetail(id) {
      this.$gLoading(true);
      try {
        const res = await apiGetSterilizedDetail({ id });
        this.targetViewItem = res.data;
        this.$nextTick(() => {
          this.$refs.sterilizedView.showDialog = true;
        });
      } catch (error) {
        // continue regardless of error
      } finally {
        this.$gLoading(false);
      }
    },
    fillSterilizedFinish({ id, name }) {
      this.currentItem.id = id;
      this.currentItem.name = name;
      this.$nextTick(() => {
        this.$refs.sterilizedForm.$refInit();
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
      this.queryQuerySterilized(1);
    },
  },
  created() {
    this.queryQuerySterilized(1);
  },
};
</script>
