<template>
  <VDialog
    v-model:show="showDialog"
    :title="`${temp.id ? '編輯' : '新增'}角色`"
    dialogWidth="max-width: 840px; width: 100%"
  >
    <template #content>
      <v-form @submit="handleSubmit" id="form" ref="form">
        <v-field
          name="角色名稱"
          rules="required|max:32"
          v-model="roleName"
          v-slot="{ errorMessage, value, field }"
        >
          <label class="text-subtitle2">角色名稱<span class="text-red">*</span> </label>
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
        <section>
          <ul>
            <li v-for="menu in menus" class="q-mb-sm" :key="menu.name">
              <h2 class="text-body1 text-weight-bold">{{ menu.name }}</h2>
              {{ item.result }}
              <q-checkbox
                v-for="item in menu.children"
                :key="item.name"
                v-model="item.result.selected"
                color="secondary"
                class="q-mb-xs q-mr-sm"
              >
                {{ item.title }}
              </q-checkbox>
            </li>
          </ul>
        </section>

        <q-separator />
        <div class="flex justify-end q-px-lg q-pt-md">
          <q-btn color="info" label="送出" class="q-mb-sm" type="submit" />
        </div>
      </v-form>
    </template>
  </VDialog>
</template>

<script>
import { apiCreateRole, apiQueryPermissionList } from '@/api';
import { modifyRoutes } from '@/router';

export default {
  props: {
    item: Object,
  },
  data() {
    return {
      temp: {},
      showDialog: false,
      roleName: '',
      permissionsList: [],
      menus: [],
      refPermissions: {},
    };
  },
  methods: {
    async handleSubmit() {
      const { level, id } = this.temp;
      this.$gLoading(true);
      try {
        const funcPermissions = Object.keys(this.refPermissions).reduce((acc, key) => {
          const item = this.refPermissions[key];
          if (item.selected) return [...acc, item.id];
          return acc;
        }, []);

        const data = {
          id,
          roleName: this.roleName,
          level: level || 3, // default
          funcPermissions,
        };

        await apiCreateRole(data);
        this.roleName = '';
        this.showDialog = false;
        this.$emit('success');
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      } finally {
        this.$gLoading(false);
      }
    },
    async getPermissionList() {
      try {
        const res = await apiQueryPermissionList();
        this.refPermissions = res.data.reduce(
          (acc, current) => ({ ...acc, [current.nameEN]: { id: current.id, selected: false } }),
          {},
        );
      } catch (e) {
        //
      }
    },
    generateMenus(routes = []) {
      const tree = [
        { key: 'dm', name: '器械管理', children: [] },
        { key: 'pm', name: '包盤管理', children: [] },
        { key: 'surgerym', name: '手術管理', children: [] },
        { key: 'statisticm', name: '統計分析', children: [] },
        { key: 'systemm', name: '系統設定', children: [] },
      ];
      const subGroupName = {
        pm_m: '管理區',
        pm_i: '庫存區',
        pm_p: '配盤區',
        pm_c: '回收清洗區',
        pm_s: '滅菌站',
        statisticm_p: '包盤',
        statisticm_s: '滅菌',
        statisticm_r: '清洗',
        systemm_m: '人員管理',
      };
      const report = routes.find((r) => r.name === 'report').children;
      const dealRoutes = routes.filter((r) => r.name !== 'report');
      const result = [...report, ...dealRoutes].map((r) => {
        const prefixName = r.meta?.route?.subGroup
          ? `${subGroupName[r.meta?.route?.subGroup]}-`
          : '';
        return {
          name: r.name,
          parent: r.meta?.route?.mainGroup,
          title: `${prefixName}${r.meta?.title}`,
          result: this.refPermissions[r.name],
        };
      });

      result.forEach((item) => {
        const idx = tree.findIndex((t) => t.key === item.parent);
        tree[idx].children.push(item);
      });
      return tree;
    },
    setMenusRecord(item) {
      const { name, permissions } = item;
      this.roleName = name;
      permissions.forEach((p) => {
        this.refPermissions[p.nameEN].selected = true;
      });
    },
    resetMenus() {
      Object.keys(this.refPermissions).forEach((key) => {
        this.refPermissions[key].selected = false;
      });
    },
    $refInit() {
      this.roleName = '';
      this.resetMenus();
      this.showDialog = true;
    },
  },
  watch: {
    item(val) {
      this.temp = { ...val };
      this.resetMenus();
      if (Object.keys(val).length > 0) {
        this.setMenusRecord(this.temp);
      }
    },
  },
  async created() {
    await this.getPermissionList();
    this.menus = this.generateMenus(modifyRoutes);
  },
};
</script>
