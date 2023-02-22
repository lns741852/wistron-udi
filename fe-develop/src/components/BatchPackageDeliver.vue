<template>
  <SwitchTabs ref="switchTabs" :tabList="tabList" @pressFinal="init">
    <template #content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <v-form ref="form" @submit="slotProps.handleForwardTab">
          <div class="q-px-lg q-pt-lg q-pb-md" style="border-bottom: 1px solid #e5e6e6">
            <v-field
              name="交付批號"
              rules="required|max:50"
              v-model="form.batchTitle"
              v-slot="{ errorMessage, value, field }"
            >
              <label class="text-subtitle2"> 交付批號 <span class="text-red">*</span> </label>
              <q-input
                dense
                outlined
                :model-value="value"
                v-bind="field"
                :error-message="errorMessage"
                :error="!!errorMessage"
              />
            </v-field>
          </div>
          <div class="flex justify-end q-px-lg q-py-md">
            <q-btn color="blue" icon="exit_to_app" label="下一步" class="q-mb-sm" type="submit" />
          </div>
        </v-form>
      </div>
      <div v-show="slotProps.currentTabNumber === 2">
        <SelectPickPackages
          ref="selectPackages"
          :selectPackages="handlePackages"
          :validPackageStatus="targetProcess.packgesStatus"
          :vListInfo="deviceConcept"
          :addColumn="[{ align: 'left', name: 'position', label: '位置', field: 'position' }]"
          @updatePackages="updateSelectedPackages"
        />
        <q-separator />
        <div class="row justify-between items-center q-px-lg q-py-md">
          <p class="cursor-pointer" @click="slotProps.handleBackwordTab">←上一步</p>
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="交付"
            class="q-mb-sm"
            type="button"
            @click="createDeliverBatch"
            :disable="handlePackages.length === 0"
          />
        </div>
      </div>
    </template>
  </SwitchTabs>
</template>

<script>
import { apiDeliverBatch } from '@/api';
import SelectPickPackages from '@/views/SelectPickPackages.vue';
import SwitchTabs from '@/components/SwitchTabs.vue';

export default {
  components: {
    SelectPickPackages,
    SwitchTabs,
  },
  props: {
    currentStationId: {
      type: Number,
    },
    deliverStationId: {
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
      form: {
        batchTitle: '',
        stationId: '',
      },
      packagesProcess: [
        {
          actionName: 'packageDeliver', // 配盤區 => 滅菌區
          label: '交付至滅菌區',
          packgesStatus: [0],
          packageScanType: 'qrcode',
        },
        {
          actionName: 'sterilizedDeliver', // 滅菌區 => 庫存區
          label: '交付至庫存區',
          packgesStatus: [4],
          packageScanType: 'qrcode',
        },
        {
          actionName: 'reSterilizedDeliver', // 重新滅菌 => 配盤區
          label: '交付至配盤區',
          packgesStatus: [6],
          packageScanType: 'qrcode',
        },
      ],
      handlePackages: [],
    };
  },
  computed: {
    deviceConcept() {
      return {
        title: '交付批次資訊',
        items: [
          {
            label: '交付批號名稱',
            value: this.form.batchTitle,
          },
          {
            label: '交付區',
            value: this.targetProcess?.label,
          },
          {
            label: '包盤數量',
            value: this.handlePackages.length,
          },
        ],
      };
    },
    targetProcess() {
      return this.packagesProcess.find((item) => item.actionName === this.processActionName);
    },
    currentDateString() {
      return this.$utilsFilters.dateStr().replace(/[-: ]/gi, '');
    },
  },
  methods: {
    async createDeliverBatch() {
      this.$gLoading(true);
      try {
        const data = {
          batchTitle: this.form.batchTitle,
          from: this.currentStationId,
          to: this.form.stationId,
          packages: this.handlePackages.map((pk) => pk.id),
        };
        await apiDeliverBatch(data);
        this.$gNotifySuccess('交付成功');
        this.handlePackages = [];
        this.$refs.switchTabs.handleForwardTab();
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    updateSelectedPackages(val) {
      this.handlePackages = val;
    },
    init() {
      this.form.batchTitle = `${this.targetProcess.label}-${this.currentDateString}`;
    },
  },
  watch: {
    deliverStationId(val) {
      this.form.stationId = val;
    },
    'form.stationId'(newVal, oldVal) {
      if (oldVal) {
        this.handlePackages = [];
      }
    },
  },
  mounted() {
    this.form.batchTitle = `${this.targetProcess.label}-${this.currentDateString}`;
    if (!Number.isNaN(this.deliverStationId)) {
      this.form.stationId = this.deliverStationId;
    }
  },
};

/**

*/
</script>
