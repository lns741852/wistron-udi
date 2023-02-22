<template>
  <label class="text-subtitle2" v-if="title">
    {{ title || '標題' }}<span class="text-red">*</span>
  </label>
  <q-select
    dense
    outlined
    v-model="tempModel"
    use-input
    use-chips
    :options="tempOptions"
    @filter="filterOptions"
    hide-dropdown-icon
    emit-value
    map-options
    input-debounce="0"
    new-value-mode="add"
    :disable="disable"
    @update:model-value="(val) => checkVal(val)"
  >
  </q-select>
</template>

<script>
export default {
  name: 'selectInput',
  props: {
    title: {
      type: String,
    },
    maxLength: {
      type: Number,
    },
    options: {
      type: Array,
      required: true,
    },
    value: {
      type: String,
    },
    disable: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['updateVal'],
  data() {
    return {
      tempModel: null,
      tempOptions: [],
      originalOptions: [],
      optionLabel: '',
      optionValue: '',
    };
  },
  methods: {
    filterOptions(val, update) {
      if (val === '') {
        update(() => {
          this.tempOptions = this.originalOptions;
        });
        return;
      }
      update(() => {
        const needle = val.toLowerCase();
        this.tempOptions = this.originalOptions.filter((v) => v.toLowerCase().indexOf(needle) > -1);
      });
    },
    checkVal(val) {
      if (!val) {
        this.$emit('updateVal', val);
        return;
      }
      if (this.maxLength !== null && Number(val.length) >= this.maxLength) {
        this.tempModel = null;
        this.$gNotifyError(`長度限制${this.maxLength}`);
      } else {
        this.$emit('updateVal', val);
      }
    },
  },
  watch: {
    options(val) {
      this.originalOptions = val;
    },
    value(val) {
      this.tempModel = val;
    },
  },
  mounted() {
    this.originalOptions = this.options;
    this.tempModel = this.value;
  },
};
</script>
