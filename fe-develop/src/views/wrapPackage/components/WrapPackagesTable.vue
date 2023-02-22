<template>
  <q-card class="q-pa-sm">
    <VTable
      :style="values.length > 5 ? 'height: 62vh' : 'height: auto'"
      :columns="columns"
      :rows="values"
      rowKey="typeId"
      :hasPage="false"
      :virtualScroll="true"
      :rowSlot="true"
    >
      <template #slotRow="props">
        <q-tr :class="props.row.styleSelected ? 'bg-cyan-1 text-white' : 'bg-white text-black'">
          <q-td>
            <div class="row items-center no-wrap">
              <q-img :src="props.row.image" width="50px" :ratio="4 / 3" />
              <div class="q-pl-md">
                <h6 class="text-subtitle2 text-grey-9 q-mb-xs">
                  {{ props.row.nameScientific }}
                </h6>
                <p>{{ props.row.name }}</p>
              </div>
            </div>
          </q-td>
          <q-td>{{ props.row.spec }} </q-td>
          <q-td class="text-right">{{ props.row.qty }} </q-td>
          <q-td>
            <VLinearProgress
              :value="props.row.deviceIds.length / props.row.qty"
              :content="`+${props.row.deviceIds?.length || 0}`"
            />
          </q-td>
        </q-tr>
      </template>
    </VTable>
  </q-card>
</template>

<script>
import VLinearProgress from '@/components/VLinearProgress.vue';

export default {
  name: 'WrapPackagesTable',
  components: {
    VLinearProgress,
  },
  props: {
    values: Array,
  },
  data() {
    return {
      columns: [
        {
          align: 'left',
          name: 'deviceName',
          label: '器械類型',
          field: 'deviceName',
          headerStyle: 'width: 20%;',
        },
        {
          align: 'left',
          name: 'spec',
          label: '規格',
          field: 'spec',
        },
        { name: 'qty', label: '總數量', field: 'qty' },
        {
          align: 'left',
          name: 'action',
          label: '已存入器械數',
          field: 'action',
          headerStyle: 'width: 30%;',
        },
      ],
    };
  },
};
</script>
