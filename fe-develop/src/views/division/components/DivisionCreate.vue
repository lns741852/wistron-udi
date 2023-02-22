<template>
  <VDialog v-model:show="showDialog" title="新增科別" dialogWidth="max-width: 500px; width: 100%">
    <template #content>
      <v-form @submit="handleSubmit" id="form" ref="form">
        <v-field
          name="科別名稱"
          rules="required|max:32"
          v-model="form.name"
          v-slot="{ errorMessage, value, field }"
        >
          <label class="text-subtitle2">科別名稱<span class="text-red">*</span> </label>
          <q-input
            dense
            outlined
            maxlength="32"
            :model-value="value"
            v-bind="field"
            :error-message="errorMessage"
            :error="!!errorMessage"
          />
        </v-field>

        <v-field
          name="科別代號"
          rules="required|max:20"
          v-model="form.code"
          v-slot="{ errorMessage, value, field }"
        >
          <label class="text-subtitle2"> 科別代號 <span class="text-red">*</span> </label>
          <q-input
            dense
            outlined
            maxlength="20"
            :model-value="value"
            v-bind="field"
            :error-message="errorMessage"
            :error="!!errorMessage"
          />
        </v-field>

        <div class="flex justify-end">
          <q-btn color="info" label="確定新增" class="q-mb-sm" type="submit" />
        </div>
      </v-form>
    </template>
  </VDialog>
</template>

<script>
import { apiDivisionCreate } from '@/api';

export default {
  data() {
    return {
      form: {
        name: '',
        code: '',
      },
      showDialog: false,
    };
  },
  methods: {
    async handleSubmit() {
      this.$gLoading(true);
      try {
        await apiDivisionCreate(this.form);
        this.$gNotifySuccess('新增成功');
        this.$emit('addFinish');
        this.init();
        this.showDialog = false;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    init() {
      this.form.name = '';
      this.form.code = '';
      this.$refs.form.resetForm();
    },
  },
};
</script>
