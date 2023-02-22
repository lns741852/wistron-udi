<template>
  <VBreadCrumb backTitle="滅菌區" />
  <SwitchTabs ref="switchTabs" :tabList="tabList" @pressFinal="init">
    <template #content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <v-form @submit="slotProps.handleForwardTab" ref="form">
          <div class="q-px-lg q-pt-lg q-pb-md" style="border-bottom: 1px solid #e5e6e6">
            <v-field
              name="鍋次名稱"
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
                maxlength="50"
                :error-message="errorMessage"
                :error="!!errorMessage"
              />
            </v-field>

            <div class="q-mb-md">
              <label class="text-subtitle2"> 消毒鍋 <span class="text-red">*</span> </label>
              <VSelectInput
                :options="sterilizerOptions"
                :value="form.sterilizer"
                :maxLength="15"
                @updateVal="(val) => (form.sterilizer = val)"
              />
            </div>

            <v-field
              name="培養皿"
              rules="required|max:30"
              v-model="form.petriDish"
              v-slot="{ errorMessage, value, field }"
            >
              <label class="text-subtitle2"> 培養皿 <span class="text-red">*</span> </label>
              <q-input
                dense
                outlined
                :model-value="value"
                v-bind="field"
                maxlength="15"
                :error-message="errorMessage"
                :error="!!errorMessage"
              />
            </v-field>
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
              :disable="isInValidBtn"
            />
          </div>
        </v-form>
      </div>
      <div v-show="slotProps.currentTabNumber === 2">
        <SelectPickPackages
          ref="selectPackages"
          :selectPackages="handlePackages"
          :validPackageStatus="targetProcess.packgesStatus"
          :vListInfo="deviceConcept"
          @updatePackages="updateSelectedPackages"
        >
          <template #fireButton>
            <q-btn
              class="q-mb-sm absolute-right"
              color="blue"
              label="選取批次包盤"
              type="button"
              @click="$refs.batchTransferDialog.showDialog = true"
            />
          </template>
        </SelectPickPackages>
        <q-separator />
        <div class="row justify-between items-center q-px-lg q-py-md">
          <p class="cursor-pointer" @click="slotProps.handleBackwordTab">←上一步</p>
          <q-btn
            color="blue"
            class="q-mb-sm"
            icon="exit_to_app"
            label="建立鍋次"
            type="button"
            :disable="handlePackages.length === 0"
            @click="createBatch"
          />
        </div>
      </div>
    </template>
  </SwitchTabs>

  <BatchTransferDialog
    ref="batchTransferDialog"
    :batchType="0"
    @selectBatch="transferBatchPackages"
  />
</template>

<script>
import { apiCreateSterilizedBatch, apiQueryAttr } from '@/api';
import BatchTransferDialog from '@/components/BatchTransferPackages.vue';
import SelectPickPackages from '@/views/SelectPickPackages.vue';
import SwitchTabs from '@/components/SwitchTabs.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import VSelectInput from '@/components/VSelectInput.vue';

export default {
  components: {
    BatchTransferDialog,
    SwitchTabs,
    SelectPickPackages,
    VBreadCrumb,
    VSelectInput,
  },
  data() {
    return {
      tabList: [
        {
          name: 'createSterilized',
          title: '建立滅菌鍋次',
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
        sterilizer: null,
        petriDish: '',
      },
      handlePackages: [],
      sterilizerOptions: [],
    };
  },
  computed: {
    isInValidBtn() {
      return !this.form.sterilizer || !this.form.petriDish;
    },
    deviceConcept() {
      return {
        title: '鍋次建立資訊',
        items: [
          {
            label: '鍋次批次名稱',
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
        packgesStatus: [2, 14],
      };
    },
  },
  methods: {
    async createBatch() {
      this.$gLoading(true);
      try {
        const { date, time, batchName, sterilizer, petriDish } = this.form;
        const startTime = `${date} ${time}:59`.replaceAll('/', '-');
        const data = {
          batchName,
          sterilizer,
          petriDish,
          startTime,
          packages: this.handlePackages.map((pk) => pk.id),
        };
        await apiCreateSterilizedBatch(data);
        this.$gNotifySuccess('建立滅菌');
        this.handlePackages = [];
        this.$refs.switchTabs.handleForwardTab();
      } catch (error) {
        const msg = error.data.msg || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async querySterilizerAttr() {
      try {
        const res = await apiQueryAttr('sterilizer');
        this.sterilizerOptions = res.data;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    transferBatchPackages(content) {
      content.forEach((item) => {
        item.packages.forEach((pk) => {
          const idx = this.handlePackages.findIndex((value) => value.id === pk.id);
          if (idx === -1) {
            this.handlePackages.push(pk);
          }
        });
      });
    },
    updateSelectedPackages(val) {
      this.handlePackages = val;
    },
    init() {
      this.form.batchName = `${this.targetProcess?.label}-${this.currentDateString}`;
      this.querySterilizerAttr();
    },
  },
  created() {
    this.init();
    this.form.date = this.$utilsFilters.dateStr().slice(0, 10);
    this.form.time = this.$utilsFilters.dateStr().slice(11, 16);
  },
};
</script>
