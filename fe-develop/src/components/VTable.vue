<template>
  <p class="text-weight-bold q-ml-sm q-mb-sm" v-if="title">{{ title }}</p>
  <q-table
    :columns="columns"
    :rows="rows"
    :loading="loading"
    :bordered="false"
    flat
    :row-key="rowKey"
    hide-pagination
    :virtual-scroll="virtualScroll"
    :pagination="pagination"
    :style="style"
  >
    <template v-slot:body="props" v-if="rowSlot">
      <slot name="slotRow" :row="props.row" />
    </template>

    <template v-for="slot in bodySlots" :key="slot" #[`body-cell-${slot}`]="props">
      <q-td :props="props">
        <slot :name="slot" :row="props.row" />
      </q-td>
    </template>

    <template v-slot:bottom v-if="hasPage">
      <div class="row items-center justify-end full-width">
        <q-pagination
          v-model="pagination.page"
          direction-links
          unelevated
          round
          active-color="secondary"
          active-text-color="white"
          :max="pagination.totalPages"
          @update:model-value="changePage"
          size="sm"
        />
      </div>
    </template>
    <template v-slot:no-data>
      <div class="full-width row flex-center">
        <p class="text-weight-bold">{{ noData ?? '目前無資料' }}</p>
      </div>
    </template>
  </q-table>
</template>

<script>
export default {
  name: 'VTable',
  props: {
    rowSlot: Boolean,
    bodySlots: Array,
    title: String,
    columns: {
      type: Array,
      required: true,
    },
    rows: Array,
    loading: {
      type: Boolean,
      default: false,
    },
    rowKey: {
      type: String,
      required: true,
    },
    currentPage: Number,
    totalPages: Number,
    hasPage: {
      type: Boolean,
      default: true,
    },
    noData: String,
    virtualScroll: {
      type: Boolean,
      default: false,
    },
    style: [Object, String],
  },
  emits: ['changePage'],
  computed: {
    pagination() {
      if (this.hasPage) {
        return {
          sortBy: 'desc',
          descending: false,
          page: this.currentPage,
          rowsPerPage: 10,
          totalPages: this.totalPages,
        };
      }
      return { rowsPerPage: 0 };
    },
  },
  methods: {
    changePage(page) {
      this.$emit('changePage', page);
      this.pagination.page = page;
    },
  },
};
</script>
