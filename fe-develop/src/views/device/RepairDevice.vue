<template>
  <VBreadCrumb backTitle="器械管理" />
  <SwitchTabs ref="switchTabs" :tabList="tabList">
    <template v-slot:content="slotProps">
      <div v-show="slotProps.currentTabNumber === 1">
        <v-form @submit="slotProps.handleForwardTab" ref="form">
          <div class="q-px-lg q-pt-lg q-pb-md" style="border-bottom: 1px solid #e5e6e6">
            <div>
              <v-field
                name="項目"
                class="q-pa-md"
                rules="required"
                v-model="form.item"
                v-slot="{ value, field }"
              >
                <label class="text-subtitle2"> 項目<span class="text-red">*</span> </label>
                <q-radio
                  v-for="item in repairItem"
                  :key="item.label"
                  v-bind="field"
                  :model-value="value"
                  :val="item.value"
                  :label="item.label"
                  name="type"
                ></q-radio>
              </v-field>
            </div>
            <div>
              <v-field
                name="類型"
                class="q-pa-md"
                rules="required"
                v-model.number="form.type"
                v-slot="{ value, field }"
              >
                <label class="text-subtitle2"> 類型<span class="text-red">*</span> </label>
                <q-radio
                  v-for="item in typeList"
                  :key="item.status"
                  v-bind="field"
                  :model-value="value"
                  :val="item.status"
                  :label="item.name"
                  name="type"
                />
              </v-field>
            </div>
          </div>
          <div class="flex justify-end q-px-lg q-py-md">
            <q-btn color="blue" icon="exit_to_app" label="下一步" type="submit" />
          </div>
        </v-form>
      </div>
      <div v-show="slotProps.currentTabNumber === 2">
        <div class="q-px-lg q-pt-lg q-pb-md">
          <div class="row items-center q-mb-md">
            <h4 class="text-h6  q-mr-md">
              {{ currentRepairItem.label }}
              轉為 <span class="text-secondary">{{ currentRepairStatus.name }}</span>
            </h4>
            <q-input
              ref="scanInput"
              v-model="scanText"
              type="search"
              dense
              outlined
              style="width: 200px"
              :disable="disableInput"
              @keyup.enter="getScanInfo"
            >
              <template v-slot:append>
                <q-icon name="qr_code_scanner" />
              </template>
            </q-input>
          </div>
          <q-separator />
          <ul class="q-py-md" v-if="scanItems.length > 0">
            <li v-for="(item, idx) in scanItems" :key="item.id" class="row items-center">
              <p>{{ idx + 1 }}.</p>
              <p class="q-mr-sm">{{ item.title }}</p>
              <p class="q-mr-sm text-grey text-subtitle2">
                /
                <span class="material-icons q-mr-sm"> qr_code_scanner </span>{{ item.code }}
              </p>
              <div v-if="form.type === 4 || form.type === 9" class="row items-center">
                <p class="text-subtitle2 q-mr-sm">/ 說明</p>
                <q-input v-model="item.comment" dense outlined class="q-mr-sm" />
              </div>
              <q-btn flat round @click="deleteScanItem(item.id)">x</q-btn>
            </li>
          </ul>
          <p class="q-py-md text-grey text-subtitle2" v-else>請掃描qrcode</p>
        </div>
        <q-separator />
        <div class="flex justify-end q-px-lg q-py-md">
          <q-btn
            color="blue"
            icon="exit_to_app"
            label="送出"
            @click="fireAction"
            :disable="scanItems.length === 0"
          />
        </div>
      </div>
    </template>
  </SwitchTabs>
</template>

<script>
import {
  apiDeviceRepair,
  apiDeviceScrapped,
  apiDeviceRepairReturn,
  apiDeviceReturn,
  apiDeviceBoxRepair,
  apiDeviceBoxScrapped,
  apiDeviceBoxReturn,
  apiGetDeviceByCode,
  apiGetBoxByCode,
} from '@/api';
import SwitchTabs from '@/components/SwitchTabs.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  name: 'repairDevice',
  components: {
    SwitchTabs,
    VBreadCrumb,
  },
  data() {
    return {
      tabList: [
        {
          name: 'repairInfo',
          title: '維修資訊',
        },
        {
          name: 'scanItem',
          title: '掃描',
        },
      ],
      form: {
        item: 'device', // box
        type: 4,
      },
      repairItem: [
        { label: '器械', value: 'device' },
        { label: '器械盒', value: 'box' },
      ],
      columns: [
        { align: 'left', name: 'code', label: 'QRCode', field: 'code' },
        { align: 'left', name: 'title', label: '項目', field: 'title' },
      ],
      scanText: '',
      scanStatus: '',
      scanItems: [],
    };
  },
  computed: {
    disableInput() {
      if (this.form.type === 2 && this.scanItems.length > 0) {
        return true;
      }
      return false;
    },
    currentRepairItem() {
      return this.repairItem.find((item) => item.value === this.form.item);
    },
    currentRepairStatus() {
      return this.typeList.find((item) => item.status === this.form.type);
    },
    validTypeList() {
      const data = [
        { currentStatus: 0, validType: [4, 9] },
        { currentStatus: 2, validType: [4, 9] },
        { currentStatus: 4, validType: [5, 9] },
        { currentStatus: 6, validType: [2] },
        { currentStatus: 7, validType: [2] },
      ];
      const idx = data.findIndex((item) => item.currentStatus === this.scanStatus);
      return idx === -1 ? [] : data[idx].validType;
    },
    repairStatus() {
      const repairStandard = [4, 5, 9];
      const isDeviceRepair = this.form.item !== 'device';
      return isDeviceRepair ? repairStandard : [2, ...repairStandard];
    },
    typeList() {
      const deviceAll = this.$statusFilters.surgeryDevice('all');
      return deviceAll
        .filter((item) => this.repairStatus.includes(item.status))
        .map(({ status, value, name }) => {
          if (status === 2) {
            return { status, value, name: '返還' };
          }
          return { status, value, name };
        });
    },
    apiInstance() {
      const { item, type } = this.form;
      if (item === 'device') {
        switch (type) {
          case 2:
            return (data) => apiDeviceReturn(data);
          case 4:
            return (data) => apiDeviceRepair(data);
          case 5:
            return (data) => apiDeviceRepairReturn(data);
          case 9:
            return (data) => apiDeviceScrapped(data);
          default:
            return null;
        }
      } else {
        switch (type) {
          case 4:
            return (data) => apiDeviceBoxRepair(data);
          case 5:
            return (data) => apiDeviceBoxReturn(data);
          case 9:
            return (data) => apiDeviceBoxScrapped(data);
          default:
            return null;
        }
      }
    },
  },
  methods: {
    async getScanInfo() {
      const qrcode = this.scanText;
      const idx = this.scanItems.findIndex((item) => item.code === qrcode);
      if (idx !== -1) {
        this.$gNotifyError('此器械已掃描');
      }
      if (!qrcode || idx !== -1) {
        this.scanText = '';
        return;
      }
      try {
        const allowItems = ['device', 'box'];
        if (allowItems.includes(this.form.item)) {
          const handleContentItem = this.form.item;
          let res = '';
          if (handleContentItem === 'device') {
            res = await apiGetDeviceByCode({ qrcode });
          } else {
            res = await apiGetBoxByCode({ qrcode });
          }
          const item = res.data;
          this.scanStatus = item.status;
          this.generateScanInfo({
            typeName: handleContentItem === 'device' ? '器械' : '器械盒',
            id: item.id,
            code: item.qrcode,
            title: handleContentItem === 'device' ? item.nameScientific : '器械盒',
            status: item.status,
            nextStatus: this.form.type,
          });
        }
      } catch (error) {
        this.$gNotifyError('查無此qrcode');
      } finally {
        this.scanText = '';
      }
    },
    fireAction() {
      this.$gLoading(true);
      const request = this.scanItems.map((i) => {
        const data = this.generateRequestData({
          ...i,
          formItem: this.form.item,
          formType: this.form.type,
        });
        return this.apiInstance(data);
      });

      Promise.all(request)
        .then(() => {
          this.$refs.switchTabs.handleForwardTab();
        })
        .catch((error) => {
          const msg = error.data.msg || '系統發生異常';
          this.$gNotifyError(msg);
        })
        .finally(() => {
          this.$gLoading(false);
          this.scanItems = [];
        });
    },
    generateRequestData({ id, code, comment, formItem, formType }) {
      let data = null;
      if (formItem === 'device') {
        if (formType === 2) {
          data = { qrcode: code };
        } else {
          data = { deviceId: id, description: comment };
        }
      } else {
        data = { qrcode: code, comments: comment };
      }
      return data;
    },
    deleteScanItem(id) {
      const idx = this.scanItems.findIndex((item) => item.id === id);
      this.scanItems.splice(idx, 1);
    },
    generateScanInfo(item) {
      const { id, typeName, nextStatus } = item;
      if (!id) {
        this.$gNotifyError(`查無此${typeName}`);
      } else {
        const { isAllowed, name } = this.checkNextStatusValid(nextStatus);
        this.scanInfo = {
          ...item,
          isAllowed,
        };
        if (!isAllowed) {
          this.$gNotifyError(`當前${typeName}無法"${name}"`);
        } else {
          this.scanItems.push({ ...item, comment: '' });
        }
      }
    },
    checkNextStatusValid(status) {
      const isAllowed = this.validTypeList.includes(status);
      const repaireTypeItem = this.typeList.find((item) => item.status === status);
      const repairStatusName = repaireTypeItem ? repaireTypeItem.name : '-';
      return {
        isAllowed,
        name: repairStatusName,
        status,
      };
    },
  },
  watch: {
    'form.item'(val) {
      const { type } = this.form;
      if (val === 'box' && type === 2) {
        this.form.type = 4;
      }
    },
  },
};
</script>
