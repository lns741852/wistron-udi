<template>
  <div class="flex justify-between q-mb-md">
    <h4 class="text-weight-bold">{{ $route.meta?.title }}</h4>
    <VDateRange @updateDate="(val) => (date = val)" :dateRange="date" />
  </div>
  <router-view />
</template>

<script>
import { computed } from 'vue';
import VDateRange from '@/components/VDateRange.vue';

export default {
  name: 'report',
  components: {
    VDateRange,
  },
  provide() {
    return {
      reportDate: computed(() => this.date),
    };
  },
  data() {
    return {
      date: null,
    };
  },
  created() {
    const startDate = new Date().getTime() - 7 * 864e5;
    const from = this.$utilsFilters.dateStr(startDate).slice(0, 10);
    const to = this.$utilsFilters.dateStr().slice(0, 10);
    this.date = { from, to };
  },
};
</script>
