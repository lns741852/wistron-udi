<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="q-px-lg q-pb-lg q-pt-sm shadow-1 bg-grey-2" style="max-width: 700px; width: 100%">
      <div class="row justify-between items-center q-mb-sm">
        <h1 class="text-h6">新增器械</h1>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="q-pa-md bg-white q-mb-lg">
        <p class="text-weight-bold q-mb-sm">器械資訊</p>
        <v-form @submit="handleSubmit" id="form" ref="form">
          <div class="row q-col-gutter-sm">
            <div class="col-4">
              <v-field
                name="科別"
                rules="required"
                v-model.number="deviceForm.division"
                v-slot="{ errorMessage, value, field }"
              >
                <label class="text-subtitle2"> 科別<span class="text-red">*</span> </label>
                <q-select
                  dense
                  menu-self="top start"
                  outlined
                  :model-value="value"
                  v-bind="field"
                  option-label="name"
                  option-value="id"
                  :options="divisions"
                  emit-value
                  map-options
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                  class="q-mr-xs"
                ></q-select>
              </v-field>
            </div>
            <div class="col-4 input-nowrap">
              <VSelectInput
                title="廠牌"
                :options="brandsOptions"
                :value="deviceForm.brand"
                :maxLength="50"
                @updateVal="(val) => (deviceForm.brand = val)"
              />
            </div>
            <div class="col-4 input-nowrap">
              <VSelectInput
                title="型號"
                :disable="!deviceForm.brand"
                :options="menuFaturesOptions"
                :maxLength="20"
                @updateVal="(val) => (deviceForm.manufactureId = val)"
              />
            </div>
            <div class="col-4">
              <v-field
                name="成本"
                rules="required|integer|max:8"
                v-model="deviceForm.cost"
                v-slot="{ errorMessage, value, field }"
              >
                <label class="text-subtitle2"> 成本<span class="text-red">*</span> </label>
                <q-input
                  dense
                  outlined
                  maxlength="8"
                  :model-value="value"
                  v-bind="field"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                >
                  <template v-slot:prepend>
                    <q-icon name="attach_money" />
                  </template>
                </q-input>
              </v-field>
            </div>
            <div class="col-4">
              <v-field
                name="qrcode"
                rules="required|alpha_num||max:64"
                v-model="deviceForm.qrcode"
                v-slot="{ errorMessage, value, field }"
              >
                <label class="text-subtitle2"> QR Code <span class="text-red">*</span> </label>
                <q-input
                  dense
                  outlined
                  maxlength="64"
                  :model-value="value"
                  v-bind="field"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                >
                  <template v-slot:append>
                    <q-icon name="qr_code_scanner" />
                  </template>
                </q-input>
              </v-field>
            </div>
            <div class="col-4">
              <v-field
                name="UDI"
                rules="alpha_num|max:32"
                v-model="deviceForm.udi"
                v-slot="{ errorMessage, value, field }"
              >
                <label class="text-subtitle2">
                  UDI
                </label>
                <q-input
                  dense
                  outlined
                  maxlength="32"
                  :model-value="value"
                  v-bind="field"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                >
                  <template v-slot:append>
                    <q-icon name="qr_code_scanner" />
                  </template>
                </q-input>
              </v-field>
            </div>
          </div>
          <div class="flex justify-end">
            <q-btn color="info" label="確定新增" class="q-mb-sm" type="submit" />
          </div>
        </v-form>
      </div>
      <div class="q-pa-lg bg-white">
        <p>
          已新增的器械
          <span class="bg-red rounded-borders q-px-sm text-white">{{ values.length }}</span>
        </p>
        <q-table
          :columns="columns"
          :rows="values"
          :loading="isLoading"
          :bordered="false"
          :flat="true"
          row-key="qrcode"
          hide-pagination
          :pagination="{ rowsPerPage: 0 }"
        >
          <template v-slot:body-cell-action="props">
            <q-td :props="props">
              <q-btn
                dense
                round
                flat
                color="grey"
                icon="visibility"
                @click="viewDetail(props.row)"
              ></q-btn>
              <q-btn dense round flat color="grey" icon="delete"></q-btn>
            </q-td>
          </template>
        </q-table>
      </div>
    </div>
  </q-dialog>
</template>
<script>
import { apiAddDeviceItem, apiQueryDeviceBrands } from '@/api';
import { divisionStatus } from '@/common/statusFilters';
import VSelectInput from '@/components/VSelectInput.vue';

export default {
  props: {
    typeId: {
      type: [Number, String],
    },
  },
  components: {
    VSelectInput,
  },
  data() {
    return {
      showDialog: false,
      deviceForm: {
        udi: '',
        qrcode: '',
        cost: 1,
        division: null,
        brand: null,
        manufactureId: null,
      },
      columns: [
        { align: 'left', name: 'qrcode', label: 'QRcode', field: 'qrcode' },
        { align: 'left', name: 'udi', label: 'UDI', field: 'udi' },
        { align: 'left', name: 'cost', label: '成本', field: 'cost' },
        {
          align: 'left',
          name: 'division',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.division)?.name,
        },
      ],
      values: [],
      isLoading: false,
      originalBrands: [],
    };
  },
  computed: {
    divisions() {
      const enableStatus = divisionStatus({ value: 'enable' })?.status;
      return this.$store.getters.divisionByStatus(enableStatus);
    },
    menuFaturesOptions() {
      return this.dropDownBrands.find((b) => b.brand === this.deviceForm.brand)?.items || [];
    },
    brandsOptions() {
      return this.dropDownBrands.map((b) => b.brand);
    },
    dropDownBrands() {
      return this.originalBrands.reduce((acc, current) => {
        const idx = acc.findIndex((b) => b.brand === current.brand);
        if (idx === -1) {
          return [
            ...acc,
            {
              brand: current.brand,
              items: [current.manufactureId],
            },
          ];
        }
        acc[idx].items.push(current.manufactureId);
        return acc;
      }, []);
    },
  },
  methods: {
    async handleSubmit() {
      this.$gLoading(true);
      try {
        const { brand, manufactureId, ...rest } = this.deviceForm;
        if (!brand || !manufactureId) {
          this.$gNotifyError('需填寫廠牌及型號');
          return;
        }
        const deviceModel = this.originalBrands.find(
          (item) => item.brand === brand && item.manufactureId === manufactureId,
        );
        const form = deviceModel
          ? { deviceModelId: deviceModel.modelId, ...rest }
          : { ...rest, deviceModel: { brand, manufactureId } };
        const data = { devices: [form], typeId: this.typeId };
        const res = await apiAddDeviceItem(data);
        const resDeviceInfo = res.data.map((item) => ({
          udi: item.udi,
          qrcode: item.qrcode,
          cost: item.cost,
          division: item.division,
          id: item.id,
        }));
        this.values = [...resDeviceInfo, ...this.values];
        this.deviceForm.udi = '';
        this.deviceForm.qrcode = '';
        this.$refs.form.resetForm();
        this.$q.notify({
          type: 'success',
          message: '器械新增成功!',
        });
        this.$emit('addItemQty');
      } catch (error) {
        const msg = error.data.msg || error.data || '系統發生異常!';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async queryBrandDevices() {
      if (!this.typeId) return;
      try {
        const res = await apiQueryDeviceBrands(this.typeId, { info: false });
        this.originalBrands = res.data;
      } catch (error) {
        const msg = error.data;
        if (msg) {
          this.$gNotifyError(msg);
        } else {
          this.$gNotifyError('系統發生異常');
        }
      }
    },
    async $refInit() {
      await this.queryBrandDevices();
      this.deviceForm = {
        udi: '',
        qrcode: '',
        cost: 1,
        division: null,
        brand: null,
        manufactureId: null,
      };
      this.values = [];
      this.showDialog = true;
    },
  },
};
</script>

<style lang="scss">
.input-nowrap {
  .q-field__native {
    flex-wrap: nowrap;
  }
}
</style>
