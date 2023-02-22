<template>
  <VDialog v-model:show="showDialog" title="新增使用者" dialogWidth="max-width: 500px; width: 100%">
    <template #content>
      <v-form @submit="handleSubmit" id="form" ref="form">
        <v-field
          name="會員名稱"
          rules="required|max:50"
          v-model="form.userName"
          v-slot="{ errorMessage, value, field }"
        >
          <label class="text-subtitle2">名稱<span class="text-red">*</span> </label>
          <q-input
            dense
            outlined
            maxlength="50"
            :model-value="value"
            v-bind="field"
            :error-message="errorMessage"
            :error="!!errorMessage"
          />
        </v-field>

        <v-field
          name="會員帳號"
          rules="required|max:50"
          v-model="form.account"
          v-slot="{ errorMessage, value, field }"
        >
          <label class="text-subtitle2"> 帳號(登入使用) <span class="text-red">*</span> </label>
          <q-input
            dense
            outlined
            maxlength="50"
            :model-value="value"
            v-bind="field"
            :error-message="errorMessage"
            :error="!!errorMessage"
          />
        </v-field>
        <v-field
          name="權限"
          class="q-pa-md"
          rules="required"
          v-model.number="form.roleId"
          v-slot="{ errorMessage, value, field }"
        >
          <label class="text-subtitle2"> 權限<span class="text-red">*</span> </label>
          <q-select
            dense
            outlined
            :model-value="value"
            v-bind="field"
            option-label="name"
            option-value="id"
            :options="roles"
            emit-value
            map-options
            :error-message="errorMessage"
            :error="!!errorMessage"
            class="q-mr-xs"
          ></q-select>
        </v-field>

        <div class="flex justify-end">
          <q-btn color="info" label="確定新增" class="q-mb-sm" type="submit" />
        </div>
      </v-form>
    </template>
  </VDialog>
</template>

<script>
import { apiQueryRoleList, apiCreateUser } from '@/api';

export default {
  data() {
    return {
      form: {
        account: '',
        userName: '',
        roleId: 1,
      },
      showDialog: false,
      roles: [],
    };
  },
  methods: {
    async handleSubmit() {
      this.$gLoading(true);
      try {
        await apiCreateUser(this.form);
        this.$gNotifySuccess('新增成功');
        this.$emit('addFinish');
        this.form.account = '';
        this.form.userName = '';
        this.showDialog = false;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async queryRole() {
      try {
        const res = await apiQueryRoleList({ permissions: false });
        this.roles = res.data.map(({ id, name, status }) => ({ id, name, status }));
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
  },
  created() {
    this.queryRole();
  },
};
</script>
