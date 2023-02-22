<template>
  <VBreadCrumb backTitle="回收清洗區" />
  <SwitchTabs ref="switchTabs" :tabList="tabList" @pressFinal="init">
    <template #content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <v-form @submit="slotProps.handleForwardTab" ref="form">
          <div class="q-px-lg q-pt-lg q-pb-md" style="border-bottom: 1px solid #e5e6e6">
            <v-field
              name="批次名稱"
              rules="required|max:50"
              v-model="form.batchName"
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
            <div class="q-mb-md">
              <label class="text-subtitle2"> 清洗機 <span class="text-red">*</span> </label>
              <VSelectInput
                :options="washingMachineOptions"
                :value="form.washingMachine"
                :maxLength="15"
                @updateVal="(val) => (form.washingMachine = val)"
              />
            </div>

            <label class="text-subtitle2"> 開始時間 <span class="text-red">*</span> </label>
            <div class="row items-center">
              <v-field
                name="開始日期"
                rules="required"
                v-model="form.date"
                v-slot="{ errorMessage, value, field }"
              >
                <q-input
                  mask="date"
                  :rules="[(v) => !Number.isNaN(new Date(v).valueOf())]"
                  dense
                  outlined
                  today-btn
                  :model-value="value"
                  v-bind="field"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                  class="q-mr-sm"
                >
                  <template v-slot:append>
                    <q-icon name="event" class="cursor-pointer">
                      <q-popup-proxy transition-show="scale" transition-hide="scale">
                        <q-date v-model="form.date">
                          <div class="row items-center justify-end">
                            <q-btn v-close-popup label="Close" color="primary" flat />
                          </div>
                        </q-date>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </v-field>
              <v-field
                name="開始時間"
                rules="required"
                v-model="form.time"
                v-slot="{ errorMessage, value, field }"
              >
                <q-input
                  mask="time"
                  :rules="['time']"
                  dense
                  outlined
                  :model-value="value"
                  v-bind="field"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                >
                  <template v-slot:append>
                    <q-icon name="access_time" class="cursor-pointer">
                      <q-popup-proxy transition-show="scale" transition-hide="scale">
                        <q-time v-model="form.time">
                          <div class="row items-center justify-end">
                            <q-btn v-close-popup label="Close" color="primary" flat />
                          </div>
                        </q-time>
                      </q-popup-proxy>
                    </q-icon>
                  </template>
                </q-input>
              </v-field>
            </div>
          </div>
          <div class="flex justify-end q-px-lg q-py-md">
            <q-btn
              color="blue"
              icon="exit_to_app"
              label="下一步"
              class="q-mb-sm"
              type="submit"
              :disable="!form.washingMachine"
            />
          </div>
        </v-form>
      </div>
      <div v-show="slotProps.currentTabNumber === 2">
        <SelectPickPackages
          ref="selectPackages"
          scanType="packageCode"
          :validPackageStatus="targetProcess.packgesStatus"
          :vListInfo="deviceConcept"
          @updatePackages="updateSelectedPackages"
        />
        <q-separator />
        <div class="row justify-between items-center q-px-lg q-py-md">
          <p class="cursor-pointer" @click="slotProps.handleBackwordTab">←上一步</p>
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="建立批次"
            class="q-mb-sm"
            type="button"
            @click="createBatch"
            :disable="handlePackages.length === 0"
          />
        </div>
      </div>
    </template>
  </SwitchTabs>
</template>

<script>
import { apiCreateWashingBatch, apiQueryAttr } from '@/api';
import SelectPickPackages from '@/views/SelectPickPackages.vue';
import SwitchTabs from '@/components/SwitchTabs.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import VSelectInput from '@/components/VSelectInput.vue';

export default {
  components: {
    SelectPickPackages,
    SwitchTabs,
    VBreadCrumb,
    VSelectInput,
  },
  data() {
    return {
      tabList: [
        {
          name: 'createWashing',
          title: '建立清洗批次',
        },
        {
          name: 'choosePackage',
          title: '選擇包盤',
        },
      ],
      form: {
        batchName: '',
        date: '',
        time: '',
        washingMachine: null,
      },
      washingMachineOptions: [],
      handlePackages: [],
    };
  },
  computed: {
    deviceConcept() {
      return {
        title: '清洗批次資訊',
        items: [
          {
            label: '清洗批次名稱',
            value: this.form.batchName,
          },
          {
            label: '包盤數量',
            value: this.handlePackages.length,
          },
        ],
      };
    },
    currentDateString() {
      return this.$utilsFilters.dateStr().replace(/[-: ]/gi, '');
    },
    targetProcess() {
      return {
        actionName: 'packageDeliver', // 配盤區 => 滅菌區
        label: '建立鍋次',
        packgesStatus: [10, 15],
      };
    },
  },
  methods: {
    async createBatch() {
      this.$gLoading(true);
      try {
        const { date, time, batchName, washingMachine } = this.form;
        const startTime = `${date} ${time}:59`.replaceAll('/', '-');
        const data = {
          batchName,
          washingMachine,
          startTime,
          packages: this.handlePackages.map((pk) => pk.id),
        };
        await apiCreateWashingBatch(data);
        this.$gNotifySuccess('交付成功');
        this.handlePackages = [];
        this.$refs.selectPackages.values = [];
        this.$refs.switchTabs.handleForwardTab();
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async queryWashingMachineAttr() {
      try {
        const res = await apiQueryAttr('washingMachine');
        this.washingMachineOptions = res.data;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    updateSelectedPackages(val) {
      this.handlePackages = val;
    },
    init() {
      this.form.batchName = `${this.targetProcess?.label}-${this.currentDateString}`;
      this.queryWashingMachineAttr();
    },
  },
  created() {
    this.init();
    this.form.date = this.$utilsFilters.dateStr().slice(0, 10);
    this.form.time = this.$utilsFilters.dateStr().slice(11, 16);
  },
};

/**

*/
</script>
