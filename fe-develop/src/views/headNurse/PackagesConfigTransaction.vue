<template>
  <VBreadCrumb backTitle="管理區" />
  <SwitchTabs ref="switchTabs" :tabList="tabList" @pressFinal="init">
    <template #content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <PackageConfigForm
          ref="form"
          :code="form.configCode"
          :name="form.configName"
          :divisionId="form.divisionId"
          @submitForm="createBaseConfig"
        />
      </div>
      <div v-show="slotProps.currentTabNumber === 2">
        <div class="row q-col-gutter-lg q-pa-lg">
          <div class="col-8">
            <SearchBar :searchConfig="['nameScientific']" @queryContentArray="queryContentArray" />
            <VTable
              rowKey="id"
              :style="deivceTypeValues.length > 0 ? 'height: 62vh' : ''"
              :columns="deivceTypeColumns"
              :rows="deivceTypeValues"
              :loading="isLoading"
              :currentPage="currentPage"
              :totalPages="totalPages"
              :bodySlots="['typeInfo', 'action']"
              @changePage="queryDeviceTypes"
            >
              <template #typeInfo="props">
                <div class="row items-center no-wrap">
                  <q-img
                    :src="$utilsFilters.imagePath(props.row.mainImagePath)"
                    width="60px"
                    :ratio="4 / 3"
                  />
                  <div class="q-pl-md">
                    <h6 class="text-grey-9 text-subtitle1 text-weight-bold">
                      {{ props.row.nameScientific }}
                    </h6>
                    <p>{{ props.row.name }}</p>
                  </div>
                </div>
              </template>

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
            <VList :content="deviceConcept" :dense="true" />
            <VList
              v-show="pickValues.length > 0"
              :content="pickConcept"
              :border="false"
              valueColor="text-secondary"
            />
          </div>
        </div>
        <q-separator />
        <div class="row justify-between items-center q-px-lg q-py-md">
          <p class="cursor-pointer" @click="slotProps.handleBackwordTab">←上一步</p>
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="下一步"
            class="q-mb-sm"
            type="button"
            @click="handleForwardTab('packageCheck')"
          />
        </div>
      </div>
      <div v-show="slotProps.currentTabNumber === 3">
        <div class="row q-col-gutter-lg q-mb-md q-py-md q-px-lg">
          <div class="col-8">
            <VTable
              rowKey="id"
              :style="pickValues.length > 0 ? 'height: 50vh' : ''"
              :columns="pickColumns"
              :rows="pickValues"
              :loading="isLoading"
              :bodySlots="['typeInfo', 'pickQty', 'action']"
              :hasPage="false"
            >
              <template #typeInfo="props">
                <div class="row items-center no-wrap">
                  <q-img
                    :src="$utilsFilters.imagePath(props.row.mainImagePath)"
                    width="60px"
                    :ratio="4 / 3"
                  />
                  <div class="q-pl-md">
                    <h6 class="text-grey-9 text-subtitle1 text-weight-bold">
                      {{ props.row.nameScientific }}
                    </h6>
                    <p>{{ props.row.name }}</p>
                  </div>
                </div>
              </template>

              <template #pickQty="props">
                <q-input
                  v-model.number="props.row.pickQty"
                  type="number"
                  @update:model-value="
                    (val) => {
                      if (val >= props.row.totalQty) {
                        props.row.pickQty = props.row.totalQty;
                      } else if (val <= 0) {
                        props.row.pickQty = 1;
                      }
                    }
                  "
                  dense
                  outlined
                  min="1"
                  :max="props.row.totalQty"
                />
              </template>

              <template #action="props">
                <q-btn
                  dense
                  round
                  flat
                  class="q-pa-lg"
                  color="grey"
                  :disable="props.row.totalQty === 0"
                  @click="deleteQty(props.row)"
                  ><q-icon name="delete"
                /></q-btn>
              </template>
            </VTable>
          </div>

          <div class="col-4">
            <VList :content="deviceConcept" />
          </div>
        </div>
        <q-separator />
        <div class="row justify-between items-center q-px-lg q-py-md">
          <p class="cursor-pointer" @click="slotProps.handleBackwordTab">←上一步</p>
          <q-btn
            color="blue"
            icon="check_box"
            label="確認新增"
            class="q-mb-sm"
            type="button"
            :disable="pickValues.length === 0"
            @click="createPackageConfig"
          />
        </div>
      </div>
    </template>
  </SwitchTabs>

  <DevicePickQuantityDialog
    ref="pickDeviceQty"
    :deviceItem="targetItem"
    @addQuantity="addQuantity"
  />
</template>
<script>
import { apiQueryDeviceTypes, apiCreatePackageConf } from '@/api';
import SearchBar from '@/components/SearchBar.vue';
import SwitchTabs from '@/components/SwitchTabs.vue';
import VList from '@/components/VList.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import DevicePickQuantityDialog from './components/DevicePickQuantity.vue';
import PackageConfigForm from './components/PackageConfigForm.vue';

export default {
  name: 'packagesTransaction',
  components: {
    SearchBar,
    SwitchTabs,
    VList,
    VBreadCrumb,
    DevicePickQuantityDialog,
    PackageConfigForm,
  },
  data() {
    return {
      tabList: [
        {
          name: 'packageInfo',
          title: '包盤資訊',
        },
        {
          name: 'chooseDevice',
          title: '選擇器械',
        },
        {
          name: 'packageCheck',
          title: '包盤確認',
        },
      ],
      form: {
        configCode: '',
        configName: '',
        divisionId: null,
      },
      currentPage: 1,
      totalPages: 1,
      queryString: {},
      pickColumns: [
        {
          headerStyle: 'width: 40%;',
          align: 'left',
          name: 'typeInfo',
          label: '器械類型',
          field: 'typeInfo',
        },
        { align: 'left', name: 'spec', label: '規格', field: 'spec' },
        { align: 'left', name: 'pickQty', label: '數量', field: 'pickQty' },
        { align: 'left', name: 'action', label: '功能', field: 'action' },
      ],
      pickValues: [],
      deivceTypeColumns: [
        {
          headerStyle: 'width: 40%;',
          align: 'left',
          name: 'typeInfo',
          label: '器械類型',
          field: 'typeInfo',
        },
        { align: 'left', name: 'spec', label: '規格', field: 'spec' },
        { align: 'left', name: 'action', label: '選擇', field: 'action' },
      ],
      deivceTypeValues: [],
      targetItem: {},
      isLoading: false,
    };
  },
  computed: {
    deviceConcept() {
      return {
        title: '包盤類型摘要',
        items: [
          {
            label: '包盤編號',
            value: this.form.configCode,
          },
          {
            label: '包盤名稱',
            value: this.form.configName,
          },
          {
            label: '科別',
            value: this.$store.getters.divisionById(this.form.divisionId)?.name || '-',
          },
          {
            label: '器械類型數',
            value: this.selectDeviceTypeQty,
          },
          {
            label: '器械總數',
            value: this.selectDeviceItemsQty,
          },
        ],
      };
    },
    selectDeviceTypeQty() {
      return this.pickValues.length;
    },
    selectDeviceItemsQty() {
      return this.pickValues.reduce((acc, device) => acc + device.pickQty, 0);
    },
    pickConcept() {
      const items = this.pickValues.map((device) => ({
        label: device.nameScientific,
        caption: device.name,
        value: `+${device.pickQty}`,
      }));
      return {
        title: '已選器械',
        items,
      };
    },
  },
  methods: {
    async queryDeviceTypes(page = 1) {
      this.$gLoading(true);
      this.isLoading = true;
      try {
        const { divisionId } = this.form;
        const division = divisionId === '' || divisionId === 'all' ? {} : { divisionId };
        const params = {
          page: page - 1,
          ...division,
          ...this.queryString,
        };
        const res = await apiQueryDeviceTypes(params);
        const { content, totalPages } = res.data;
        this.currentPage = page;
        this.totalPages = totalPages;
        this.deivceTypeValues = content;
      } catch (error) {
        this.$gNotifyError('發生異常');
      } finally {
        this.isLoading = false;
        this.$gLoading(false);
      }
    },
    async createPackageConfig() {
      this.$gLoading(true);
      try {
        const data = {
          ...this.form,
          deviceTypes: this.pickValues.map((item) => ({ id: item.id, qty: item.pickQty })),
        };
        await apiCreatePackageConf(data);
        this.pickValues = [];
        this.targetItem = {};
        this.$refs.switchTabs.handleForwardTab();
      } catch (error) {
        const msg = error.data || '系統發生異常!';
        this.$q.notify({
          type: 'negative',
          message: msg,
        });
      } finally {
        this.$gLoading(false);
      }
    },
    handleForwardTab(name) {
      if (name === 'packageCheck' && this.selectDeviceTypeQty === 0) {
        this.$q.notify({
          type: 'negative',
          message: '請至少選擇一種器械',
        });
        return;
      }
      this.$refs.switchTabs.handleForwardTab();
    },
    addQuantity(item) {
      const pickedDeviceIdx = this.pickValues.findIndex((device) => device.id === item.id);
      if (pickedDeviceIdx === -1) {
        this.pickValues.push(item);
      } else {
        this.pickValues[pickedDeviceIdx].pickQty = item.pickQty;
      }
      this.targetItem = {};
    },
    fireSelectQtyAction(item) {
      if (item.totalQty === 0) return;
      const pickedDevice = this.pickValues.find((device) => device.id === item.id);
      const pickQty = pickedDevice ? pickedDevice.pickQty : 0;
      this.targetItem = { ...item, pickQty };
      this.$refs.pickDeviceQty.showDialog = true;
    },
    deleteQty(item) {
      const idx = this.pickValues.findIndex((device) => device.id === item.id);
      this.pickValues.splice(idx, 1);
    },
    queryContentArray(content) {
      this.queryString = content.reduce(
        (acc, current) => ({
          ...acc,
          [current.key]: current.value,
        }),
        {},
      );
      this.queryDeviceTypes();
    },
    async createBaseConfig({ configCode, configName, divisionId }) {
      const isSame = this.$utilsFilters.objectEquals(
        { configCode, configName, divisionId },
        this.form,
      );
      if (!isSame) {
        this.form.configCode = configCode;
        this.form.configName = configName;
        this.form.divisionId = divisionId;
        await this.queryDeviceTypes();
        this.pickValues = [];
        this.targetItem = {};
      }
      this.$refs.switchTabs.handleForwardTab();
    },
    init() {
      this.form = {
        configCode: '',
        configName: '',
        divisionId: null,
      };
      this.$refs.form.init();
    },
  },
};
</script>
