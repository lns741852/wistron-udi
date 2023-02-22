<template>
  <div class="rounded-borders border row items-center justify-between q-px-sm bg-white">
    <p class="text-body2 q-mr-md" :class="dense ? '' : 'q-py-sm'">{{ dateValue }}</p>
    <q-icon name="event" class="cursor-pointer">
      <q-popup-proxy transition-show="scale" transition-hide="scale">
        <q-date dense v-model="date" range mask="YYYY-MM-DD">
          <div class="row items-center justify-end ">
            <q-btn v-close-popup label="Close" color="primary" flat />
          </div>
        </q-date>
      </q-popup-proxy>
    </q-icon>
  </div>
</template>

<script>
export default {
  name: 'vDateRange',
  props: {
    dense: {
      type: Boolean,
      default: true,
    },
    dateRange: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      date: null,
    };
  },
  computed: {
    dateValue() {
      if (!this.date) return '';
      if (typeof this.date === 'string') {
        return `${this.date}-${this.date}`;
      }
      const { from, to } = this.date;
      return `${from}-${to}`;
    },
  },
  watch: {
    dateValue() {
      this.$emit('updateDate', this.date);
    },
  },
  methods: {
    init() {
      const from = this.$utilsFilters.dateStr().slice(0, 10);
      const to = this.$utilsFilters.dateStr().slice(0, 10);
      this.date = { from, to };
    },
  },
  created() {
    const { from, to } = this.dateRange;
    if (from && to) {
      this.date = { ...this.dateRange };
    } else {
      this.init();
    }
  },
};
</script>
