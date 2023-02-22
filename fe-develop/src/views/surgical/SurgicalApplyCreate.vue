<template>
  <VBreadCrumb backTitle="手術管理區" />
  <SwitchTabs ref="switchTabs" :tabList="tabList" finishWord="申請完成" @pressFinal="init">
    <template v-slot:content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <SurgicalApplyForm ref="refApplyForm" @submitForm="submitForm" />
      </div>

      <div v-show="slotProps.currentTabNumber === 2">
        <div class="row q-col-gutter-lg q-pa-lg">
          <div class="col-8">
            <SearchBar
              ref="searchBar"
              :searchConfig="['configCode', 'configName']"
              @queryContentArray="queryContentArray"
            />

            <VTable
              rowKey="configId"
              :columns="columns"
              :rows="packageValues"
              :loading="isLoading"
              :currentPage="pagination.page"
              :totalPages="pagination.totalPages"
              :bodySlots="['action']"
              @changePage="queryPackageConfig"
            >
              <template #action="props">
                <q-btn
                  dense
                  round
                  flat
                  class="q-pa-lg"
                  color="grey"
                  :disable="props.row.totalQty === 0"
                  @click="fireSelectQtyAction(props.row)"
                  ><q-icon name="touch_app"
                /></q-btn>
              </template>
            </VTable>
          </div>
          <div class="col-4">
            <VList :content="listContent" :dense="true" class="q-mb-md" />

            <q-card flat bordered class="q-pa-none q-pb-sm q-mb-md pickField">
              <q-card-section class="q-pa-sm">
                <h6 class="q-pa-md">已選擇包盤</h6>
                <q-list dense class="text-body2">
                  <template v-for="(item, idx) in pickValues" :key="`${item.label}${idx}`">
                    <q-item class="q-pa-xs-md q-pa-md-lg">
                      <q-item-section>
                        <q-item-label>
                          {{ item.configName }}
                        </q-item-label>
                        <q-item-label class="text-grey text-caption">
                          {{ item.configCode }}
                        </q-item-label>
                      </q-item-section>
                      <q-item-section>
                        <q-item-label class="row">
                          <q-input
                            v-model.number="item.pickQty"
                            type="number"
                            class="q-mr-sm"
                            style="flex-grow: 1"
                            @update:model-value="
                              (val) => {
                                if (val <= 0) {
                                  item.packageQty = 1;
                                }
                              }
                            "
                            dense
                            outlined
                            min="1"
                            max="99"
                          />
                          <q-btn
                            dense
                            round
                            flat
                            class="q-pa-lg"
                            color="grey"
                            @click="deleteQty(item)"
                            ><q-icon name="delete"
                          /></q-btn>
                        </q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-separator spaced inset />
                  </template>
                </q-list>
              </q-card-section>
            </q-card>

            <q-btn
              icon="check_box"
              color="blue"
              label="申請確認"
              class="q-mb-sm full-width"
              type="button"
              :disable="pickValues.length === 0"
              @click="createSurgicalApply"
            />
          </div>
        </div>
      </div>
    </template>
  </SwitchTabs>
  <PackagePickQtyDialog ref="pickQty" :item="targetItem" @addQuantity="addQuantity" />
</template>

<script>
import { apiQueryPackageConf, apiSurgicalCreate } from '@/api';
import SurgicalApplyForm from './components/SurgicalApplyForm.vue';
import SwitchTabs from '@/components/SwitchTabs.vue';
import SearchBar from '@/components/SearchBar.vue';
import PackagePickQtyDialog from './components/PackagePickQuantity.vue';

import VBreadCrumb from '@/components/VBreadCrumb.vue';
import VList from '@/components/VList.vue';

export default {
  components: {
    SearchBar,
    SwitchTabs,
    SurgicalApplyForm,
    PackagePickQtyDialog,
    VBreadCrumb,
    VList,
  },
  data() {
    return {
      tabList: [
        {
          name: 'fillData',
          title: '填寫基本資料',
        },
        {
          name: 'choosePackage',
          title: '選擇包盤',
        },
      ],
      applyInfo: {}, // 手術資訊
      isLoading: false,
      columns: [
        {
          align: 'left',
          name: 'configCode',
          label: '包盤編號',
          field: 'configCode',
          headerStyle: 'width: 25%',
        },
        {
          align: 'left',
          name: 'configName',
          label: '包盤名稱',
          field: 'configName',
          headerStyle: 'width: 25%',
        },
        {
          align: 'left',
          name: 'packageQty',
          label: '包盤數量',
          field: 'packageQty',
        },
        {
          align: 'left',
          name: 'inStockQty',
          label: '庫存數量',
          field: 'inStockQty',
        },
        { align: 'left', name: 'action', label: '選擇', field: 'action' },
      ],
      packageValues: [],
      pickValues: [],
      queryString: {},
      pagination: {
        page: 0,
        rowsPerPage: 10,
        totalPages: 1,
      },
      targetItem: {},
    };
  },
  computed: {
    listContent() {
      const {
        divisionId,
        operatingRoom,
        operatingDate,
        operatingOrder,
        surgeryName,
        medicalRecordNumber,
        doctor,
        // operatingNumber: 'ON09220003', // 手術單號
      } = this.applyInfo;
      return {
        title: '包盤類型摘要',
        items: [
          { label: '科別', value: this.$store.getters.divisionById(divisionId)?.name },
          { label: '手術室', value: operatingRoom },
          { label: '手術日期', value: operatingDate },
          { label: '手術排序', value: operatingOrder },
          { label: '手術名稱', value: surgeryName },
          { label: '醫師', value: doctor },
          { label: '病歷號碼', value: medicalRecordNumber },
          {
            label: '包盤種類數',
            value: `+ ${this.pickValues.length}`,
            labelCss: 'text-weight-bold',
            valueCss: 'text-weight-bold text-secondary',
          },
          {
            label: '包盤數量',
            value: this.pickValues.reduce((acc, item) => acc + item.pickQty, 0),
            labelCss: 'text-weight-bold',
            valueCss: 'text-weight-bold text-secondary',
          },
        ],
      };
    },
  },
  methods: {
    // 建立手術用器械申請單
    async createSurgicalApply() {
      this.$gLoading(true);
      try {
        const orders = this.pickValues.map((item) => ({
          configId: item.configId,
          qty: item.pickQty,
        }));
        await apiSurgicalCreate({ ...this.applyInfo, orders });
        this.$gNotifySuccess('申請成功');
        this.$refs.switchTabs.handleForwardTab();
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    // 取得所有包盤種類
    async queryPackageConfig(page = 1) {
      this.isTableLoading = true;
      try {
        const res = await apiQueryPackageConf({
          page: page - 1,
          ...this.queryString,
          showCount: 1,
        });
        const { content, totalPages } = res.data;
        this.pagination.page = page;
        this.pagination.totalPages = totalPages;
        this.packageValues = content.map((item) => ({ ...item, pickQty: 1 }));
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.isTableLoading = false;
      }
    },
    fireSelectQtyAction(item) {
      const pickItem = this.pickValues.find((val) => val.configId === item.configId);
      const pickQty = pickItem ? pickItem.pickQty : 0;
      this.targetItem = { ...item, pickQty };
      this.$refs.pickQty.showDialog = true;
    },
    deleteQty(item) {
      const idx = this.pickValues.findIndex((p) => p.configId === item.configId);
      this.pickValues.splice(idx, 1);
    },
    submitForm(form) {
      this.applyInfo = { ...form };
      this.$refs.refApplyForm.init();
      this.queryPackageConfig();
      this.$refs.switchTabs.handleForwardTab();
    },
    init() {
      this.applyInfo = {};
      this.queryString = {};
      this.pickValues = [];
      this.queryPackageConfig();
    },
    addQuantity(item) {
      const idx = this.pickValues.findIndex((val) => val.configId === item.configId);
      if (idx === -1) {
        this.pickValues.push(item);
      } else {
        this.pickValues[idx].pickQty = item.pickQty;
      }
      this.targetItem = {};
    },
    queryContentArray(content) {
      this.queryString = content.reduce(
        (acc, current) => ({
          ...acc,
          [current.key]: current.value,
        }),
        {},
      );
      this.queryPackageConfig();
    },
  },
};
</script>

<style lang="scss">
.pickField {
  height: auto;
  max-height: 500px;
  overflow: scroll;
}
</style>
