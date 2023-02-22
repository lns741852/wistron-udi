<template>
  <div class="bg-white q-pa-lg">
    <div class="row justify-end">
      <q-btn
        color="info"
        icon="add_circle_outline"
        label="新增科別"
        class="q-mb-sm"
        @click="addDivision"
      />
    </div>
    <VTable
      rowKey="id"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :hasPage="false"
      :bodySlots="['status', 'action']"
    >
      <template #status="props">
        <q-toggle
          v-model="props.row.status"
          :true-value="0"
          :false-value="1"
          :color="props.row.status ? 'grey-5' : 'secondary'"
          @update:model-value="(val) => toggleStatus({ status: val, id: props.row.id })"
        />
      </template>
      <template #action="props">
        <q-btn
          icon="delete"
          flat
          size="12px"
          class="q-px-md"
          @click="deleteDivision({ status: delDivisionStatus, id: props.row.id })"
        />
      </template>
    </VTable>
    <DivisionCreate ref="divisionCreate" @addFinish="fetchDivisionData" />
  </div>
</template>

<script>
import { apiDivisionModify } from '@/api';
import { divisionStatus } from '@/common/statusFilters';
import DivisionCreate from '@/views/division/components/DivisionCreate.vue';

export default {
  name: 'divisionList',
  components: {
    DivisionCreate,
  },
  data() {
    return {
      columns: [
        {
          align: 'left',
          name: 'name',
          label: '科別名稱',
          field: 'name',
        },
        { align: 'left', name: 'code', label: '科別代號', field: 'code' },
        { align: 'left', name: 'createTime', label: '建立時間', field: 'createTime' },
        { align: 'left', name: 'updateTime', label: '更新時間', field: 'updateTime' },
        {
          align: 'center',
          name: 'status',
          label: '開啟',
          field: 'status',
        },
        { align: 'center', name: 'action', label: '功能', field: 'action' },
      ],
      values: [],
      isLoading: false,
    };
  },
  computed: {
    delDivisionStatus() {
      return divisionStatus({ value: 'delete' }).status;
    },
  },
  methods: {
    async toggleStatus({ status, id }) {
      try {
        await this.modifyStatus({ status, id });
      } catch (error) {
        const divisionEnable = divisionStatus({ value: 'enable' }).status;
        const divisionDisable = divisionStatus({ value: 'disabled' }).status;
        const idx = this.values.findIndex((v) => v.id === id);
        const dStatus = divisionEnable === status ? divisionDisable : divisionEnable;
        this.values[idx].status = dStatus;
      }
    },
    async deleteDivision({ status, id }) {
      this.$gLoading(true);
      try {
        await this.modifyStatus({ status, id });
        await this.fetchDivisionData();
      } catch (error) {
        this.$gNotifyError(error.message);
        // regarless of error
      } finally {
        this.$gLoading(false);
      }
    },
    async modifyStatus({ status, id }) {
      try {
        await apiDivisionModify({ status, id });
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        throw new Error(msg);
      }
    },
    async fetchDivisionData() {
      await this.$store.dispatch('sysSetting/queryDivisions');
      this.queryDivisionData();
    },
    addDivision() {
      this.$refs.divisionCreate.showDialog = true;
    },
    queryDivisionData() {
      this.values = [...this.$store.getters.divisionViewAll];
    },
  },
  created() {
    this.queryDivisionData();
  },
};
</script>
