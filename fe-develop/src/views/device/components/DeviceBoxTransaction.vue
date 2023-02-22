<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-grey-2 q-pt-xs q-px-lg q-pb-xl" style="max-width: 800px; width: 100%">
      <div class="row justify-between items-center q-mb-md">
        <h1 class="text-h6">新增器械盒</h1>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <v-form @submit="handleSubmit" id="form" class="bg-white q-pa-md q-mb-lg" ref="form">
        <ul class="row items-bottom">
          <li v-for="item in formInfo" :key="item.key" :class="item.colClass">
            <div class="q-px-md">
              <v-field
                :name="item.name"
                class="q-pa-md"
                :rules="item.rules"
                v-model="item.value"
                v-slot="{ errorMessage, value, field }"
              >
                <q-input
                  dense
                  maxlength="32"
                  :model-value="value"
                  v-bind="field"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                >
                  <template v-slot:prepend v-if="item.prepend">
                    <div v-html="item.prepend"></div>
                  </template>
                  <template v-slot:append v-if="item.append">
                    <div v-html="item.append"></div>
                  </template>
                </q-input>
              </v-field>
            </div>
          </li>
          <li class="col-md-3 text-right">
            <q-btn
              color="primary"
              icon="add_circle_outline"
              label="送出"
              class="q-mb-sm q-mr-md"
              type="submit"
            />
          </li>
        </ul>
      </v-form>
      <div v-if="values.length > 0" class="q-pa-lg bg-white">
        <p>目前新增的項目</p>
        <q-table
          style="max-height: 30vh; width: 100%"
          :columns="columns"
          :rows="values"
          :loading="isLoading"
          :bordered="false"
          :flat="true"
          row-key="qrcode"
          hide-pagination
          :pagination="{ rowsPerPage: 0 }"
          virtual-scroll
        >
        </q-table>
      </div>
    </div>
  </q-dialog>
</template>

<script>
import { apiAddDeviceBox } from '@/api';

export default {
  name: 'DeviceBoxTransaction',
  data() {
    return {
      showDialog: false,
      formInfo: [
        {
          colClass: 'col-12 col-md-6',
          name: 'UDI',
          key: 'udi',
          value: '',
          rules: 'alpha_num|max:32',
          prepend: '<p class="text-subtitle2 q-px-xs">UDI</p>',
          append: '<q-icon name="qr_code_scanner" />',
        },
        {
          colClass: 'col-12 col-md-6',
          name: '成本',
          key: 'cost',
          value: '',
          rules: 'required|integer',
          prepend: `<p class="text-subtitle2 q-px-xs">成本</p>
                    <q-icon name="attach_money" class="text-h6" />`,
          append: null,
        },
        {
          colClass: 'col-12 col-md-9',
          name: 'qrcode',
          key: 'qrcode',
          value: 1,
          rules: 'required|alpha_num|max:64',
          prepend: '<p class="text-subtitle2 q-px-xs">QRcode</p>',
          append: '<q-icon name="qr_code_scanner" />',
        },
      ],
      columns: [
        { align: 'left', name: 'qrcode', label: 'QRcode', field: 'qrcode' },
        { align: 'left', name: 'udi', label: 'UDI', field: 'udi' },
        { align: 'left', name: 'cost', label: '成本', field: 'cost' },
      ],
      values: [],
      isLoading: false,
    };
  },
  methods: {
    async handleSubmit() {
      this.$gLoading(true);
      try {
        const data = this.formInfo.reduce(
          (acc, formItem) => ({ ...acc, [formItem.key]: formItem.value }),
          {},
        );
        await apiAddDeviceBox(data);
        this.values.unshift(data);
        this.$refs.form.resetForm();
        this.$emit('updateDeiveBoxItemQty');
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
    $refInit() {
      this.formInfo.forEach((item, idx) => {
        this.formInfo[idx].value = '';
      });
      this.showDialog = true;
    },
  },
};
</script>
