<template>
  <SwitchTabs ref="switchTabs" :tabList="tabList" @pressFinal="init">
    <template v-slot:content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1" class="q-pt-md">
        <div class="q-px-lg q-pb-md">
          <q-table
            :columns="batchColumns"
            :rows="batchValues"
            :loading="isLoading"
            :bordered="false"
            selection="single"
            flat
            row-key="id"
            hide-pagination
            :pagination="pagination"
            v-model:selected="batchSelected"
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
                  @update:model-value="queryBatchAll"
                  size="sm"
                />
              </div>
            </template>
          </q-table>
        </div>
        <q-separator />
        <div class="flex justify-end q-px-lg q-py-md">
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="下一步"
            class="q-mb-sm"
            type="button"
            :disable="!batchItem"
            @click="generatePackageList"
          />
        </div>
      </div>
      <div v-show="slotProps.currentTabNumber === 2" class="q-pa-lg">
        <div class="row q-col-gutter-lg ">
          <div class="col-8">
            <VGetPackagesByCode @result="getPackageInfo" />
            <q-table
              :style="batchPackageValues.length > 5 ? 'height: 62vh' : 'height: auto'"
              :columns="batchPackageColumns"
              :rows="batchPackageValues"
              :loading="isLoading"
              :bordered="false"
              selection="multiple"
              v-model:selected="batchPackageSelected"
              flat
              row-key="id"
              hide-pagination
              :pagination="{ rowsPerPage: 0 }"
              virtual-scroll
            >
              <template v-slot:body="props">
                <q-tr :props="props" @click="props.selected = true">
                  <q-td>
                    <q-checkbox v-model="props.selected" color="secondary" />
                  </q-td>
                  <q-td v-for="col in props.cols" :key="col.name" :props="props">{{
                    col.value
                  }}</q-td>
                </q-tr>
              </template>
            </q-table>
          </div>
          <div class="col-4">
            <VList :content="listContent" :dense="true" class="q-mb-md" />
            <q-btn
              color="blue"
              label="完成領取"
              class="q-mb-sm full-width"
              type="button"
              :disable="batchPackageSelected.length === 0"
              @click="createReceiveBatch"
            />
          </div>
        </div>
      </div>
    </template>
  </SwitchTabs>
</template>

<script>
import { apiQueryBatch, apiQueryPackageBatch, apiReceiveBatch } from '@/api';
import VGetPackagesByCode from '@/components/VGetPackagesByCode.vue';
import VList from '@/components/VList.vue';
import SwitchTabs from '@/components/SwitchTabs.vue';

export default {
  components: {
    VGetPackagesByCode,
    VList,
    SwitchTabs,
  },
  props: {
    currentStationId: {
      type: Number,
    },
    processActionName: {
      type: String,
    },
  },
  data() {
    return {
      tabList: [
        {
          name: 'deliverInfo',
          title: '交付資訊',
        },
        {
          name: 'choosePackage',
          title: '選擇包盤',
        },
      ],
      qrcode: '',
      isLoading: false,
      packagesProcess: [
        {
          actionName: 'sterilizedReceive', // 配盤區 => 滅菌區
          label: '滅菌區領取',
          packgesStatus: 1,
        },
        {
          actionName: 'inventoryReceive', // 滅菌區 => 庫存區
          label: '庫存區領取',
          packgesStatus: 5,
        },
        {
          actionName: 'reSterilizedReceive', //  重新滅菌 => 配盤區
          label: '配盤區領取重新滅菌',
          packgesStatus: 13,
        },
      ],
      batchColumns: [
        {
          headerStyle: 'width: 25%;',
          align: 'left',
          name: 'title',
          label: '批次名稱',
          field: 'title',
        },
        { align: 'right', name: 'totalQty', label: '包盤總數量', field: 'totalQty' },
        {
          align: 'right',
          name: 'waitForReceiveQty',
          label: '未領取數量',
          field: 'waitForReceiveQty',
        },
        { align: 'center', name: 'action', label: '', field: 'action', headerStyle: 'width: 15%;' },
      ],
      batchValues: [],
      pagination: {
        sortBy: 'desc',
        descending: false,
        page: 0,
        rowsPerPage: 10,
        totalPages: 1,
      },
      batchSelected: [],
      batchPackageColumns: [
        {
          headerStyle: 'width: 30%;',
          align: 'left',
          name: 'qrcode',
          label: 'QRcode',
          field: 'qrcode',
        },
        { align: 'left', name: 'configName', label: '包盤名稱', field: 'configName' },
        {
          align: 'left',
          name: 'divisionId',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
      ],
      batchPackageValues: [],
      batchPackageSelected: [],
    };
  },
  computed: {
    listContent() {
      return {
        title: '交付批次資訊',
        items: [
          {
            label: '領取批號名稱',
            value: this.batchItem?.title,
          },
          {
            label: '包盤數量',
            value: this.batchPackageValues.length,
          },
          {
            label: '已選取數量',
            value: `+ ${this.batchPackageSelected.length}`,
            labelCss: 'text-weight-bold',
            valueCss: 'text-weight-bold text-secondary',
          },
        ],
      };
    },
    targetProcess() {
      return this.packagesProcess.find((item) => item.actionName === this.processActionName);
    },
    batchItem() {
      return this.batchSelected[0];
    },
  },
  methods: {
    async createReceiveBatch() {
      this.$gLoading(true);
      try {
        const data = {
          deliverBatchId: this.batchItem.id,
          stationId: this.currentStationId,
          packages: this.batchPackageSelected.map((item) => item.id),
        };
        await apiReceiveBatch(data);
        this.$gNotifySuccess('領取成功');
        this.$refs.switchTabs.handleForwardTab();
        this.batchSelected = [];
        this.batchPackageValues = [];
        this.init();
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async queryBatchAll(page = 1) {
      try {
        this.isLoading = true;
        const res = await apiQueryBatch({ stationId: this.currentStationId, page: page - 1 });
        const { content, totalPages } = res.data;
        this.batchValues = content;
        this.pagination.page = page;
        this.pagination.totalPages = totalPages;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.isLoading = false;
      }
    },
    async queryBatchPackageList() {
      try {
        const res = await apiQueryPackageBatch({ deliverBatchId: this.batchItem.id });
        this.batchPackageValues = res.data;
      } catch (error) {
        this.$gNotifyError('發生異常');
      }
    },
    getPackageInfo(result) {
      const { id } = result;
      const batchIdx = this.batchPackageValues.findIndex((item) => item.id === id);
      const batchSelectedIdx = this.batchPackageSelected.findIndex((item) => item.id === id);
      if (batchIdx !== -1 && batchSelectedIdx === -1) {
        this.batchPackageSelected.push(result);
      }
    },
    async init() {
      await this.queryBatchAll();
      if (this.batchValues.length > 0) {
        this.batchSelected.push(this.batchValues[0]);
      } else {
        this.configSelected = [];
      }
    },
    async generatePackageList() {
      await this.queryBatchPackageList();
      this.$refs.switchTabs.handleForwardTab();
    },
  },
  watch: {
    batchItem() {
      this.batchPackageSelected = [];
    },
    currentStationId() {
      this.init();
    },
  },
  created() {
    if (!Number.isNaN(this.currentStationId)) {
      this.init();
    }
  },
};
</script>
