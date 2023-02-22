<template>
  <VBreadCrumb backTitle="管理區" />
  <div class="bg-white q-pa-lg">
    <div class="row items-start justify-between">
      <SearchBar
        :searchConfig="[
          { key: 'status', name: 'applyPackageStatus' },
          { key: 'divisionId', name: 'division' },
          'configName',
        ]"
        @queryContentArray="queryContentArray"
      />
      <q-btn
        color="info"
        icon="add_circle_outline"
        label="製作包盤申請"
        class="q-mb-sm"
        @click="addPackageAppliesQty"
      />
    </div>

    <VTable
      rowKey="id"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :bodySlots="['status', 'action']"
      @changePage="queryPackageApplies"
    >
      <template #status="props">
        <q-badge
          transparent
          align="middle"
          class="q-pa-xs"
          :color="`${$statusFilters.packageApply(props.row.status)?.color}`"
        >
          {{ $statusFilters.packageApply(props.row.status)?.name }}
        </q-badge>
      </template>

      <template #action="props">
        <q-btn
          v-if="isAllowUpdateStatus.includes(props.row.status)"
          dense
          round
          flat
          class="q-pr-lg"
          color="grey"
          icon="edit"
          @click="updateApplyQty(props.row.id)"
        />
        <q-btn
          v-if="isAllowDeleteStatus.includes(props.row.status)"
          dense
          round
          flat
          color="grey"
          icon="delete"
          @click="checkDeleteApplyQty({ title: props.row.configName, id: props.row.id })"
        />
      </template>
    </VTable>
  </div>
  <PackageApplyDialog ref="packageApplyAdd" @tuneApplyQuantity="queryPackageApplies" />
</template>
<script>
import { apiQueryPackageApplies, apiDelPkApply } from '@/api';
import PackageApplyDialog from '@/components/PackageApplyTransaction.vue';
import SearchBar from '@/components/SearchBar.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  components: {
    PackageApplyDialog,
    SearchBar,
    VBreadCrumb,
  },
  inject: ['getInventoryStation'],
  data() {
    return {
      queryString: {}, // status / ConfigName
      columns: [
        {
          headerStyle: 'width: 20%;',
          align: 'left',
          name: 'configCode',
          label: '包盤編號',
          field: 'configCode',
        },
        { align: 'left', name: 'configName', label: '包盤名稱', field: 'configName' },
        {
          align: 'left',
          name: 'division',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.divisionId)?.name,
        },
        { align: 'right', name: 'qty', label: '申請數量', field: 'qty' },
        { align: 'right', name: 'packagedQty', label: '已完成數量', field: 'packagedQty' },
        {
          align: 'center',
          name: 'status',
          label: '狀態',
          field: 'status',
        },
        { align: 'left', name: 'action', label: '功能', field: 'action' },
      ],
      values: [],
      currentPage: 0,
      totalPages: 0,
      isLoading: false, // table
      isAllowUpdateStatus: [1, 2],
      isAllowDeleteStatus: [1],
    };
  },
  methods: {
    // 取得申請列表
    async queryPackageApplies(page = 1) {
      this.isLoading = true;
      try {
        const data = {
          ...this.queryString,
          orderBy: 'desc',
        };
        const res = await apiQueryPackageApplies({ page: page - 1, data });
        const { content, totalPages } = res.data;
        this.currentPage = page;
        this.totalPages = totalPages;
        this.values = content;
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      } finally {
        this.isLoading = false;
        this.$gLoading(false);
      }
    },
    async deletePacakgeApplyQty(id) {
      try {
        await apiDelPkApply({ id });
        this.queryPackageApplies();
      } catch (error) {
        const msg = error.data?.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    // emit: 搜尋條件異動
    queryContentArray(content) {
      this.queryString = content.reduce(
        (acc, current) => ({
          ...acc,
          [current.key]: current.value,
        }),
        {},
      );
      this.queryPackageApplies();
    },
    // dialog
    checkDeleteApplyQty({ id, title = '' }) {
      this.$q
        .dialog({
          title: '確認通知',
          message: `確認取消<strong>${title}</strong>申請`,
          html: true,
          ok: {
            color: 'negative',
            flat: true,
          },
          cancel: {
            flat: true,
          },
          persistent: true,
        })
        .onOk(() => {
          this.deletePacakgeApplyQty(id);
        });
    },
    // 新增申請數量
    addPackageAppliesQty() {
      this.$refs.packageApplyAdd.showDialog = true;
      this.$refs.packageApplyAdd.$refInit();
    },
    // 調整申請數量
    updateApplyQty(id) {
      this.$refs.packageApplyAdd.showDialog = true;
      this.$refs.packageApplyAdd.$refEditInit(id);
    },
    init() {
      this.$gLoading(true);
      const routerPage = this.$route.params.page;
      const page = routerPage || 1;
      this.queryPackageApplies(page);
    },
  },
  created() {
    this.init();
  },
};
</script>
