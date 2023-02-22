<template>
  <VBreadCrumb backTitle="人員管理" />
  <div class="bg-white q-pa-lg">
    <!-- 搜尋條件
      圖只有名稱：
      API: 名稱、角色
    -->
    <div class="row items-start justify-between">
      <SearchBar :searchConfig="['userName']" @queryContentArray="queryContentArray" />
      <q-btn
        color="info"
        icon="add_circle_outline"
        label="新增使用者"
        class="q-mb-sm"
        @click="$refs.userAdd.showDialog = true"
      />
    </div>
    <VTable
      rowKey="id"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      @changePage="changePage"
    />

    <UserAdd ref="userAdd" @addFinish="getUserList(1)" />
  </div>
</template>

<script>
import { apiQueryUserList } from '@/api';
import SearchBar from '@/components/SearchBar.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import UserAdd from './components/UserAdd.vue';

export default {
  name: 'UserAdminList',
  components: {
    VBreadCrumb,
    SearchBar,
    UserAdd,
  },
  data() {
    return {
      columns: [
        {
          headerStyle: 'width: 20%;',
          align: 'left',
          name: 'name',
          label: '使用者名稱',
          field: 'name',
        },
        // { align: 'left', name: 'configName', label: '會員帳號', field: 'configName' }, // 畫面有但api沒有
        { align: 'left', name: 'roleName', label: '權限', field: 'roleName' },
        {
          align: 'left',
          name: 'lastLoginTime',
          label: '最後登入時間',
          field: (val) => val.lastLoginTime ?? '無登入紀錄',
        },
        { align: 'left', name: 'createTime', label: '建立時間', field: 'createTime' },
        // { align: 'left', name: 'action', label: '功能', field: 'action' }, // 有delete的api嗎
      ],
      values: [],
      currentPage: 1,
      totalPages: 0,
      isLoading: false,
      queryString: {},
    };
  },
  methods: {
    async getUserList(page = 1) {
      try {
        this.isLoading = true;
        const params = {
          ...this.queryString,
          page: page - 1,
        };
        const res = await apiQueryUserList(params);
        const { content, totalPages } = res.data;
        this.values = content;
        this.currentPage = page;
        this.totalPages = totalPages;
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
      this.getUserList();
    },
    changePage(page) {
      this.currentPage = page;
      this.getUserList(page);
    },
  },
  created() {
    this.getUserList();
  },
};
</script>
