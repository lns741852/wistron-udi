<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-white" style="max-width: 900px; width: 100%">
      <div class="q-px-lg q-pt-sm">
        <div class="row justify-between items-center q-mb-md">
          <h1 class="text-h6">批次列表</h1>
          <q-btn flat round icon="close" v-close-popup />
        </div>
      </div>
      <q-separator />
      <div class="dialog-content q-px-lg q-pt-md">
        <q-table
          :columns="columns"
          :rows="values"
          :loading="isLoading"
          :bordered="false"
          flat
          selection="single"
          v-model:selected="batchSelected"
          row-key="batchId"
          :pagination="pagination"
          hide-pagination
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
                @update:model-value="queryTransferPackages"
                size="sm"
              />
            </div>
          </template>
        </q-table>
      </div>
      <q-separator spaced />
      <div class="row justify-end q-px-lg q-py-md">
        <q-btn
          color="blue"
          label="匯入"
          class="q-mb-sm"
          type="button"
          @click="
            $emit('selectBatch', batchSelected);
            showDialog = false;
          "
          :disable="batchSelected.length === 0"
        />
      </div>
    </div>
  </q-dialog>
</template>

<script>
import { apiQueryTransferPackages } from '@/api';

export default {
  props: {
    batchType: {
      type: Number,
    },
  },
  data() {
    return {
      showDialog: false,
      batchSelected: [],
      columns: [
        {
          align: 'left',
          name: 'batchId',
          label: '批號編號',
          field: 'batchTitle',
        },
        {
          align: 'left',
          name: 'batchTitle',
          label: '批號名稱',
          field: 'batchTitle',
        },
        { align: 'right', name: 'qty', label: '總數量', field: (val) => val.packages.length },
        { align: 'center', name: 'action', label: '', field: 'action', headerStyle: 'width: 15%;' },
      ],
      values: [],
      isLoading: false,
      pagination: {
        sortBy: 'desc',
        descending: false,
        page: 0,
        rowsPerPage: 10,
        totalPages: 1,
      },
    };
  },
  methods: {
    async queryTransferPackages(page = 1) {
      try {
        const res = await apiQueryTransferPackages({
          page: page - 1,
          type: this.batchType,
        });
        const { content, totalPages } = res.data;
        this.pagination.page = page;
        this.pagination.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        this.$gNotifyError('發生異常');
      }
    },
  },
  created() {
    this.queryTransferPackages();
  },
};
</script>
