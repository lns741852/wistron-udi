<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-white" style="max-width: 1200px; width: 100%">
      <div class="q-px-lg q-pt-sm">
        <div class="row justify-between items-center q-mb-md">
          <h1 class="text-h6">
            <span class="text-secondary">{{ isAddStatus ? '新增' : '異動' }}</span
            >申請包盤製作
          </h1>
          <q-btn flat round icon="close" @click="closeDialog" />
        </div>
      </div>
      <q-separator />
      <div class="dialog-content q-px-lg q-pt-lg">
        <div v-show="views.current === 1">
          <div class="row items-start justify-between">
            <SearchBar
              :searchConfig="['configCode', 'configName', { key: 'divisionId', name: 'division' }]"
              @queryContentArray="queryContentArray"
            />
          </div>
          <q-table
            :columns="configColumns"
            :rows="configValues"
            :loading="isTableLoading"
            :bordered="false"
            selection="single"
            v-model:selected="tableConfigSelected"
            flat
            row-key="configId"
            :pagination="{ rowsPerPage: 10 }"
          >
            <template v-slot:header="props">
              <q-tr :props="props">
                <q-th v-for="col in props.cols" :key="col.name" :props="props">
                  {{ col.label }}
                </q-th>
              </q-tr>
            </template>
            <template v-slot:body="props">
              <!-- @click="props.row.isActive ? (props.selected = !props.selected) : false" -->
              <q-tr @click="props.selected = !props.selected" class="cursor-pointer" :props="props">
                <q-td v-for="col in props.cols" :key="col.name" :props="props">
                  <div v-if="col.name === 'action' && props.selected">
                    <q-avatar size="sm" color="secondary" text-color="white" icon="check" />
                  </div>
                  <div v-else-if="col.name === 'isActive'">
                    <q-badge
                      transparent
                      align="middle"
                      class="q-pa-xs"
                      :color="col.value ? 'secondary' : 'primary'"
                    >
                      {{ col.value ? '上架中' : '已下架' }}
                    </q-badge>
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
                  @update:model-value="queryPackageConfig"
                  size="sm"
                />
              </div>
            </template>
          </q-table>
        </div>
        <div v-show="views.current === 2" class="row q-col-gutter-lg">
          <div class="col-7" style="max-height: 80vh">
            <ul class="q-mb-sm">
              <li class="row text-subtitle2">
                <p class="col-5">包盤序號</p>
                <p class="col-4">位置</p>
                <p class="col-3 q-pl-md">功能</p>
              </li>
            </ul>
            <ul class="q-mb-md">
              <li
                v-for="(pk, idx) in aparPackages"
                :key="`packages${idx}`"
                class="row q-mb-md items-center"
              >
                <div class="col-5">
                  <q-input
                    class="q-mr-md"
                    :disable="pk.status >= 0"
                    v-model.trim="pk.serialNo"
                    maxlength="4"
                    type="text"
                    dense
                    outlined
                  >
                    <template v-slot:prepend>
                      <p class="text-subtitle2">{{ configInfo.configCode }}-</p>
                    </template>
                  </q-input>
                </div>
                <div class="col-4">
                  <VSelectInput
                    :options="positionOptions"
                    :value="pk.position"
                    :maxLength="10"
                    @updateVal="(val) => (pk.position = val)"
                  />
                </div>
                <div class="col-3">
                  <q-btn
                    color="secondary"
                    v-if="pk.status === -1 || !pk.id"
                    flat
                    icon="delete"
                    @click="deleteAparPackages({ idx, ...pk })"
                  />
                  <p v-else class="q-px-md text-subtitle2 text-primary">
                    {{ $statusFilters.inventoryPackage(pk.status)?.name }}
                  </p>
                </div>
              </li>
            </ul>
            <q-btn
              color="secondary"
              outline
              class="full-width"
              label="新增項目"
              @click="addPackages"
            />
          </div>
          <div class="col-5">
            <VList :content="pickConcept" :dense="true" />
          </div>
        </div>
      </div>
      <q-separator spaced />
      <div class="flex items-center q-px-lg q-py-md">
        <p
          class="cursor-pointer"
          v-show="actionStatus && views.current !== 1 && isAddStatus"
          @click="chagneView('last')"
        >
          ←上一步
        </p>
        <q-btn
          color="blue"
          icon="exit_to_app"
          :loading="isActionLoading"
          :label="views.current === 1 ? '下一步' : '確認送出'"
          class="q-mb-sm q-ml-auto"
          :disable="!actionStatus"
          @click="chagneView('next')"
        />
      </div>
    </div>
  </q-dialog>
</template>
<script>
import {
  apiQueryPackageConf,
  apiGetPkConfig,
  apiAddPkApplyQty,
  apiUpdatePkApplyQty,
  apiGetPkApplyInfo,
  apiQueryAttr,
} from '@/api';
import SearchBar from '@/components/SearchBar.vue';
import VList from '@/components/VList.vue';
import VSelectInput from '@/components/VSelectInput.vue';

export default {
  name: '建立包盤類型',
  components: {
    SearchBar,
    VList,
    VSelectInput,
  },
  data() {
    return {
      aparId: null,
      aparPackages: [{ serialNo: '', position: null }],
      showDialog: false,
      isAddStatus: true,
      positionOptions: [],
      removePackages: [],
      views: {
        current: 1,
        max: 2,
      },
      queryString: {},
      configInfo: {
        deviceTypeQty: 0,
        totalAmount: 0,
        packagedQty: 0,
      },
      configColumns: [
        {
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
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        {
          align: 'center',
          name: 'isActive',
          label: '上架狀態',
          field: 'isActive',
        },
        {
          align: 'left',
          name: 'createTimeDate',
          label: '建立時間',
          field: (val) => this.$utilsFilters.dateStr(val.createTimeDate),
        },
        { headerStyle: 'width: 10%;', align: 'center', name: 'action', label: '', field: 'action' },
      ],
      configValues: [],
      pagination: {
        sortBy: 'desc',
        descending: false,
        page: 0,
        rowsPerPage: 10,
        totalPages: 1,
      },
      isTableLoading: false,
      isActionLoading: false,
      tableConfigSelected: [],
    };
  },
  computed: {
    orginalSelectConfig() {
      return this.tableConfigSelected[0] || [];
    },
    selectConfig() {
      const division = this.$store.getters.divisionById(this.orginalSelectConfig.divisionId);
      return {
        ...this.orginalSelectConfig,
        divisionName: division?.name,
      };
    },
    pickConcept() {
      const pickDetails = [
        {
          key: 'configCode',
          label: '包盤編號',
        },
        {
          key: 'configName',
          label: '包盤名稱',
        },
        {
          key: 'divisionName',
          label: '科別',
        },
      ];
      const pickConfig = pickDetails.map((item) => ({
        label: item.label,
        value: this.selectConfig[item.key],
      }));
      const updateAmountInfo = this.isAddStatus
        ? ''
        : { label: '已完成數量', value: this.configInfo.packagedQty || 0 };
      return {
        title: '包盤類型摘要',
        items: [
          ...pickConfig,
          { label: '器械類型數', value: this.configInfo.deviceTypeQty || 0 },
          { label: '器械總數', value: this.configInfo.deviceQty || 0 },
          { label: '庫存總量', value: this.configInfo.totalQty || 0 },
          updateAmountInfo,
        ],
      };
    },
    actionStatus() {
      if (!this.isAddStatus) {
        return this.aparPackages.length > 0;
      }
      return this.tableConfigSelected.length === 1;
    },
    requestAparPackages() {
      return this.aparPackages
        .filter((pk) => pk.status === -1 || pk.status === undefined)
        .reduce((acc, current) => {
          const { serialNo, position, id } = current;
          const reqId = id ? { id } : {};
          const reqPos = position ? { position } : {};
          const otherRequest = { ...reqPos, ...reqId };
          return current.serialNo
            ? [...acc, { serialNo: `${this.configInfo?.configCode}-${serialNo}`, ...otherRequest }]
            : acc;
        }, []);
    },
  },
  methods: {
    // 取得所有包盤種類
    async queryPackageConfig(page = 1) {
      this.isTableLoading = true;
      try {
        const res = await apiQueryPackageConf({ page: page - 1, ...this.queryString });
        const { content, totalPages } = res.data;
        this.pagination.page = page;
        this.pagination.totalPages = totalPages;
        this.configValues = content;
      } catch (error) {
        const msg = error.data.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.isTableLoading = false;
      }
    },
    // 取得特定資訊
    async queryPackageConfigDetail(configId = '') {
      try {
        if (!configId) return;
        const res = await apiGetPkConfig({ configId });
        const { deviceTypeQty, totalQty, deviceQty, configCode, configName, divisionId } = res.data;
        this.configInfo = {
          ...this.configInfo,
          configCode,
          configName,
          divisionId,
          deviceTypeQty,
          totalQty,
          deviceQty,
        };
      } catch (error) {
        const msg = error.data.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      }
    },
    // 新增 / 編輯數量
    async tunePackageAppliesQty() {
      try {
        const newRequestData = {
          packages: this.requestAparPackages,
          configId: this.selectConfig.configId,
        };
        const rmPk = this.removePackages;
        const removePk = rmPk.length > 0 ? { removePackages: rmPk } : {};
        const updateRequstData = {
          id: this.aparId,
          packages: this.requestAparPackages,
          ...removePk,
        };
        const data = this.isAddStatus ? newRequestData : updateRequstData;
        if (this.isAddStatus) {
          await apiAddPkApplyQty(data);
        } else {
          await apiUpdatePkApplyQty(data);
        }
        this.showDialog = false;
        this.$emit('tuneApplyQuantity');
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    // 取得包盤種類申請資訊
    async getConfigApplyDetail(id) {
      try {
        const res = await apiGetPkApplyInfo({ id });
        const { packagedQty, applyQty, configId, packages } = res.data;
        this.configInfo = {
          ...this.configInfo,
          configId,
          packagedQty,
          applyQty,
        };
        this.aparPackages = packages;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      }
    },
    async chagneView(status) {
      const { current, max } = this.views;
      if (status === 'last' && current !== 1) {
        this.views.current -= 1;
      } else if (status === 'next') {
        this.isActionLoading = true;
        if (current !== max) {
          await this.queryPackageConfigDetail(this.selectConfig.configId);
          this.views.current += 1;
        } else {
          if (this.requestAparPackages.length === 0) {
            this.$gNotifyError('請至少填寫完整一筆包盤序號資訊');
            return;
          }
          this.tunePackageAppliesQty();
        }
        this.isActionLoading = false;
      }
    },
    async queryPositionAttr() {
      try {
        const res = await apiQueryAttr('packagePosition');
        this.positionOptions = res.data;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    addPackages() {
      this.aparPackages.push({ serialNo: '', position: null });
    },
    deleteAparPackages(item) {
      const { id, idx } = item;
      if (id) {
        this.removePackages.push(id);
      }
      this.aparPackages.splice(idx, 1);
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
    closeDialog() {
      this.configValues = [];
      this.showDialog = false;
    },
    reset() {
      this.tableConfigSelected = [];
      this.removePackages = [];
      this.queryString = {};
      this.views.current = 1;
    },
    async $refEditInit(aparId) {
      if (aparId) {
        this.reset();
        this.isAddStatus = false;
        this.aparId = aparId;
        await this.queryPositionAttr();
        await this.getConfigApplyDetail(aparId);
        await this.queryPackageConfigDetail(this.configInfo.configId);
        const { configId, configCode, configName, divisionId } = this.configInfo;
        this.tableConfigSelected.push({
          configId,
          configCode,
          configName,
          divisionId,
        });
        this.aparPackages = this.aparPackages.map(({ serialNo, ...rest }) => ({
          ...rest,
          serialNo: serialNo.replace(`${this.configInfo?.configCode}-`, ''),
        }));
        this.views.current = 2;
      }
    },
    async $refInit() {
      this.isAddStatus = true;
      this.queryString = {};
      this.aparPackages = [{ serialNo: '', position: null }];
      await this.queryPackageConfig(1);
      await this.queryPositionAttr();
      this.reset();
    },
  },
};
</script>
