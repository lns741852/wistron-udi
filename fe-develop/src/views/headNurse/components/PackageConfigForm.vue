<template>
  <v-form ref="form" @submit="submitForm">
    <div class="q-px-lg q-pt-lg q-pb-md" style="border-bottom: 1px solid #e5e6e6">
      <v-field
        name="科別"
        class="q-pa-md"
        rules="required"
        v-model.number="dId"
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
        name="包盤編號"
        rules="required|alpha_num|max:15"
        v-model="code"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 包盤編號 <span class="text-red">*</span> </label>
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
        name="包盤名稱"
        class="q-pa-md"
        rules="required|max:30"
        v-model="name"
        v-slot="{ errorMessage, value, field }"
      >
        <label class="text-subtitle2"> 包盤名稱<span class="text-red">*</span> </label>
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
    configCode: String,
    configName: String,
    divisionId: Number,
  },
  data() {
    return {
      code: '',
      name: '',
      dId: null,
    };
  },
  computed: {
    divisions() {
      const enableStatus = divisionStatus({ value: 'enable' })?.status;
      return this.$store.getters.divisionByStatus(enableStatus);
    },
  },
  methods: {
    submitForm() {
      this.$emit('submitForm', {
        configCode: this.code,
        configName: this.name,
        divisionId: this.dId,
      });
    },
    init() {
      this.$refs.form.resetForm();
    },
  },
  watch: {
    configCode(val) {
      this.code = val;
    },
    configName(val) {
      this.name = val;
    },
    divisionId(val) {
      this.dId = val;
    },
  },
};
</script>
