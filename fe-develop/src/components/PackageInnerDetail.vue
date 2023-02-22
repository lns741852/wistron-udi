<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-grey-2 q-pt-md q-px-lg q-pb-xl" style="max-width: 800px; width: 100%">
      <div class="row justify-between items-center q-mb-md">
        <h1 class="text-h6">包盤查看</h1>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="bg-white q-mb-md q-pa-md">
        <div class="q-pl-lg">
          <div class="row items-start justify-between">
            <ul class="row">
              <li class="list col-12">
                <p class="list__title">{{ detail.configName }}</p>
                <p class="list__text">
                  {{ detail.configCode }}
                </p>
              </li>
              <li class="list col-12" v-if="detail.divisionId">
                <p class="list__title">科別</p>
                <p class="list__text">
                  {{ $store.getters.divisionById(detail.divisionId)?.name || '-' }}
                </p>
              </li>
              <li class="list col-12" v-if="totalCost">
                <p class="list__title">總成本</p>
                <p class="list__text">$ {{ totalCost }}</p>
              </li>
              <li class="list col-12" v-if="detail.expireTime">
                <p class="list__title">到期日期</p>
                <p class="list__text">
                  {{ $utilsFilters.dateStr(detail.expireTime).slice(0, 10) }}
                </p>
              </li>
            </ul>
            <q-badge
              transparent
              align="middle"
              class="q-pa-xs q-mb-sm"
              :color="`${$statusFilters.inventoryPackage(detail.status)?.color}`"
            >
              {{ $statusFilters.inventoryPackage(detail.status)?.name }}
            </q-badge>
          </div>
        </div>
      </div>
      <div class="bg-white q-px-md">
        <VTable
          rowKey="id"
          style="max-height: 50vh"
          :columns="columns"
          :rows="detail.devices"
          :loading="isLoading"
          :hasPage="false"
          :bodySlots="['typeInfo']"
        >
          <template #typeInfo="props">
            <div class="row items-center">
              <q-img
                v-if="props.row.images"
                :src="$utilsFilters.imagePath(props.row.images[0].path)"
                width="60px"
                :ratio="4 / 3"
              />
              <div class="q-pl-md">
                <h6 class="text-grey-9 text-subtitle1 text-weight-bold">
                  {{ props.row.nameScientific }}
                </h6>
                <p>{{ props.row.name }}</p>
              </div>
            </div>
          </template>
        </VTable>
      </div>
    </div>
  </q-dialog>
</template>

<script>
export default {
  name: 'packageInnerDetail',
  props: {
    detail: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      showDialog: false,
      isLoading: false,
      columns: [
        {
          headerStyle: 'width: 40%;',
          align: 'left',
          name: 'typeInfo',
          label: '器械',
          field: 'typeInfo',
        },
        { align: 'left', name: 'spec', label: '規格', field: (val) => val.spec ?? '-' },
        { align: 'left', name: 'brand', label: '品牌', field: 'brand' },
        { align: 'left', name: 'manufactureId', label: '型號', field: 'manufactureId' },
        { align: 'left', name: 'qrcode', label: 'QRCode', field: 'qrcode' },
      ],
    };
  },
  computed: {
    highLevelRight() {
      const [first] = this.detail?.devices || [{}];
      if (!first) return {};
      return Reflect.has(first, 'cost');
    },
    totalCost() {
      return this.highLevelRight
        ? this.detail.devices.reduce((acc, device) => acc + device.cost, 0)
        : null;
    },
  },
  watch: {
    highLevelRight(val) {
      if (val) {
        this.columns.push({ align: 'left', name: 'cost', label: '成本', field: 'cost' });
      }
    },
  },
};
</script>
