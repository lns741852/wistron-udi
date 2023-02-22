<template>
  <VBreadCrumb backTitle="人員管理" />
  <div class="bg-white q-pa-lg">
    <div class="row items-start justify-end">
      <q-btn
        color="info"
        icon="add_circle_outline"
        label="新增角色"
        class="q-mb-sm"
        @click="create"
      />
    </div>
    <VTable
      rowKey="id"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :hasPage="false"
      :bodySlots="['action']"
    >
      <template #action="props">
        <q-btn icon="edit" flat size="12px" class="q-px-md" @click="edit(props.row)" />
      </template>
    </VTable>
    <RoleAdd ref="roleAdd" :item="targetItem" @success="getRoleList" />
  </div>
</template>

<script>
import { apiQueryRoleList } from '@/api';
import RoleAdd from './components/RoleAdd.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  name: 'UserRoleList',
  components: {
    RoleAdd,
    VBreadCrumb,
  },
  data() {
    return {
      columns: [
        {
          align: 'left',
          name: 'name',
          label: '角色名稱',
          field: 'name',
        },
        { align: 'left', name: 'level', label: '等級', field: 'level' },
        {
          align: 'left',
          name: 'status',
          label: '狀態',
          field: (val) => (val.status === 0 ? '正常' : '異常'),
        },
        {
          align: 'left',
          name: 'createTime',
          label: '建立時間',
          field: 'createTime',
        },
        { align: 'center', name: 'action', label: '功能', field: 'action' },
      ],
      values: [],
      currentPage: 1,
      totalPages: 0,
      isLoading: false,
      queryString: {},
      targetItem: {},
    };
  },
  methods: {
    async getRoleList(page = 1) {
      try {
        this.isLoading = true;
        const params = {
          ...this.queryString,
          permissions: true,
          page: page - 1,
        };
        const res = await apiQueryRoleList(params);
        this.values = res.data;
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.isLoading = false;
      }
    },
    queryContentArray(content) {
      this.queryString = content.reduce(
        (acc, current) => ({
          ...acc,
          [current.key]: current.value,
        }),
        {},
      );
      this.getRoleList();
    },
    create() {
      this.targetItem = {};
      this.$refs.roleAdd.$refInit();
    },
    edit(item) {
      this.targetItem = { ...item };
      this.$refs.roleAdd.showDialog = true;
    },
  },
  created() {
    this.getRoleList();
  },
};
</script>
