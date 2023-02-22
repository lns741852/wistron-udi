<template>
  <div class="row q-col-gutter-lg q-pa-lg">
    <div class="col-8">
      <div class="row items-center justify-between">
        <VGetPackagesByCode
          class="q-mr-sm"
          :scanType="scanType"
          :showDevices="true"
          :label="scanType === 'qrcode' ? '掃描包盤' : '掃描標牌'"
          @result="getPackageInfo"
        />
        <slot name="fireButton"></slot>
      </div>
      <q-table
        :style="values?.length > 5 ? 'height: 62vh' : 'height: auto'"
        :columns="col"
        :rows="values"
        :loading="isLoading"
        :bordered="false"
        flat
        row-key="id"
        hide-pagination
        :pagination="{ rowsPerPage: 0 }"
        virtual-scroll
      >
        <template v-slot:body-cell-action="props">
          <q-td :props="props">
            <q-btn dense round flat class="q-pa-lg" color="grey" @click="deleteItem(props.row.id)"
              ><q-icon name="delete"
            /></q-btn>
          </q-td>
        </template>
        <template v-slot:body-cell-division="props">
          <q-td :props="props">
            {{ $store.getters.divisionById(props.row.divisionId)?.name }}
          </q-td>
        </template>
      </q-table>
    </div>
    <div class="col-4">
      <VList :content="vListInfo" :dense="true" />
      <slot name="finishBtn"></slot>
    </div>
  </div>
</template>

<script>
import VGetPackagesByCode from '@/components/VGetPackagesByCode.vue';
import VList from '@/components/VList.vue';

export default {
  name: 'selectPickPackages',
  emits: ['updatePackages'],
  props: {
    scanType: {
      type: String,
      default: 'qrcode',
      validator: (value) => ['qrcode', 'packageCode'].indexOf(value) !== -1,
      // qrcode:包盤 package:標牌
    },
    selectPackages: {
      type: Array,
      default: () => [],
    },
    vListInfo: {
      type: Object,
    },
    validPackageStatus: {
      type: Array,
      required: true,
    },
    addColumn: {
      type: Array,
      default: () => [],
      validator: (arr) => arr.every((item) => item.name && item.field && item.label),
    },
  },
  components: {
    VGetPackagesByCode,
    VList,
  },
  data() {
    return {
      columns: [
        {
          headerStyle: 'width: 20%;',
          align: 'left',
          name: 'configCode',
          label: '包盤編號',
          field: 'configCode',
        },
        { align: 'left', name: 'configName', label: '包盤名稱', field: 'configName' },
        { align: 'left', name: 'division', label: '科別', field: 'divisionId' },
        { align: 'left', name: 'qrcode', label: 'QRcode', field: (val) => val.qrcode || '-' },
      ],

      values: [],
      isLoading: false,
    };
  },
  computed: {
    col() {
      const action = {
        align: 'left',
        name: 'action',
        label: '',
        field: 'action',
        headerStyle: 'width: 5%;',
      };
      return [...this.columns, ...this.addColumn, action];
    },
  },
  methods: {
    getPackageInfo(result) {
      const { status: pkStatus, id: pkId } = result;
      if (!this.validPackageStatus.includes(pkStatus)) {
        this.$gNotifyError('狀態不符合');
        return;
      }
      const isScanned = this.values.findIndex((item) => item.id === pkId) !== -1;
      if (isScanned) return;
      this.values.push(result);
      this.$emit('updatePackages', this.values);
    },
    deleteItem(id) {
      const idx = this.values.findIndex((item) => item.id === id);
      this.values.splice(idx, 1);
      this.$emit('updatePackages', this.values);
    },
  },
  watch: {
    selectPackages(val) {
      this.values = val;
    },
  },
  mounted() {
    this.values = this.selectPackages;
  },
};
</script>
