<template>
  <v-form ref="form" @submit="submitForm">
    <div class="q-px-lg q-pt-lg q-pb-md" style="border-bottom: 1px solid #e5e6e6">
      <v-field
        name="科別"
        class="q-pa-md"
        rules="required"
        v-model.number="form.divisionId"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 科別<span class="text-red">*</span> </label>
        <q-select
          dense
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

      <v-field
        name="手術室"
        rules="required"
        v-model="form.operatingRoom"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 手術室 <span class="text-red">*</span> </label>
        <q-input
          dense
          outlined
          :model-value="value"
          v-bind="field"
          :error-message="errorMessage"
          :error="!!errorMessage"
        />
      </v-field>

      <v-field
        name="手術日期"
        rules="required"
        v-model="form.operatingDate"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 手術日期 <span class="text-red">*</span> </label>
        <q-input
          dense
          outlined
          :model-value="value"
          v-bind="field"
          :error-message="errorMessage"
          :error="!!errorMessage"
          mask="####-##-##"
          :rules="[(v) => !Number.isNaN(new Date(v).valueOf())]"
        >
          <template v-slot:append>
            <q-icon name="event" class="cursor-pointer">
              <q-popup-proxy transition-show="scale" transition-hide="scale">
                <q-date v-model="form.operatingDate" mask="YYYY-MM-DD" today-btn>
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
        name="手術排序"
        class="q-pa-md"
        rules="required"
        v-model="form.operatingOrder"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 手術排序<span class="text-red">*</span> </label>
        <q-input
          dense
          outlined
          :model-value="value"
          v-bind="field"
          :error-message="errorMessage"
          :error="!!errorMessage"
        />
      </v-field>

      <v-field
        name="手術名稱"
        class="q-pa-md"
        rules="required"
        v-model="form.surgeryName"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 手術名稱<span class="text-red">*</span> </label>
        <q-input
          dense
          outlined
          :model-value="value"
          v-bind="field"
          :error-message="errorMessage"
          :error="!!errorMessage"
        />
      </v-field>

      <v-field
        name="醫生"
        class="q-pa-md"
        rules="required"
        v-model="form.doctor"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 醫生<span class="text-red">*</span> </label>
        <q-input
          dense
          outlined
          :model-value="value"
          v-bind="field"
          :error-message="errorMessage"
          :error="!!errorMessage"
        />
      </v-field>

      <v-field
        name="病歷號"
        class="q-pa-md"
        rules="required"
        v-model="form.medicalRecordNumber"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 病歷號<span class="text-red">*</span> </label>
        <q-input
          dense
          outlined
          :model-value="value"
          v-bind="field"
          :error-message="errorMessage"
          :error="!!errorMessage"
        />
      </v-field>

      <v-field
        name="手術單"
        class="q-pa-md"
        rules="required"
        v-model="form.operatingNumber"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 手術單<span class="text-red">*</span> </label>
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
      <q-btn
        ref="formBtn"
        color="blue"
        icon="exit_to_app"
        label="下一步"
        class="q-mb-sm"
        type="submit"
      />
    </div>
  </v-form>
</template>

<script>
import { divisionStatus } from '@/common/statusFilters';

export default {
  name: 'PackageConfigForm',
  emits: ['submitForm'],
  props: {
    apply: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      // form: {
      //   divisionId: 1,
      //   operatingRoom: 'TestOR0922_3',
      //   operatingDate: '2021-12-31',
      //   operatingOrder: 1,
      //   surgeryName: '內視鏡檢查手術0922003',
      //   medicalRecordNumber: 'TestMRN0922003',
      //   doctor: 'Dr. Jack',
      //   // operatingNumber: 'ON09220003', // 手術單號
      // },
      form: {},
    };
  },
  computed: {
    divisions() {
      const enableStatus = divisionStatus({ value: 'enable' })?.status;
      return this.$store.getters.divisionByStatus(enableStatus);
    },
  },
  methods: {
    generateForm() {
      return {
        divisionId: null,
        operatingRoom: '',
        operatingDate: '',
        operatingOrder: 1,
        surgeryName: '',
        medicalRecordNumber: '',
        doctor: '',
        operatingNumber: '', // 手術單號
      };
    },
    submitForm() {
      this.$emit('submitForm', { ...this.form });
    },
    init() {
      this.form = this.generateForm();
      this.$refs.form.resetForm();
    },
  },
  created() {
    this.form = this.generateForm();
  },
};
</script>
