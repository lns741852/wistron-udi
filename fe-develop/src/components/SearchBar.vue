<template>
  <div class="q-mb-sm">
    <div class="row items-center q-mb-sm">
      <div v-for="item in searchItems" :key="item.key">
        <q-input
          v-if="item.type === 'input'"
          v-model.trim="item.value"
          :placeholder="item.placeholder"
          :input-style="{ width: '100px' }"
          @keydown.enter.prevent="configSearch(item)"
          outlined
          class="q-mr-sm"
          dense
        >
          <template v-slot:append>
            <q-icon
              name="search"
              @click="configSearch(item)"
              style="cursor: pointer; z-index:4"
            ></q-icon>
          </template>
        </q-input>
        <q-select
          v-if="item.type === 'select'"
          outlined
          v-model="item.value"
          option-label="name"
          @update:model-value="(val) => configSearch(item)"
          :option-value="item.optionKey"
          :options="item.options"
          dense
          options-dense
          emit-value
          map-options
          class="q-mr-md"
          style="width: 140px"
        >
        </q-select>
      </div>
    </div>
    <div class="row items-center" v-if="searchItemBadges.length > 0">
      <p class="q-mr-sm text-grey-8 text-subtitle2">搜尋條件</p>
      <ul class="row">
        <li
          class="bg-positive text-white q-px-sm q-py-xs rounded-borders q-mr-sm"
          v-for="(item, idx) in searchItemBadges"
          :key="idx"
        >
          <span class="q-mr-md">{{ item.label }}{{ item.value }}</span>
          <q-icon name="clear" @click="deleteSearchItem(idx)" class="cursor-pointer"></q-icon>
        </li>
      </ul>
      <p v-if="isShowUndo" @click.prevent="searchDefault" class="cursor-pointer">
        <q-icon name="undo" />
        清除
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SearchBar',
  props: {
    searchConfig: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['queryContentArray'],
  data() {
    return {
      configItems: [
        { key: 'userName', label: '使用者-', placeholder: '請輸入使用者', type: 'input' },
        { key: 'spec', label: '規格-', placeholder: '請輸入規格', type: 'input' },
        { key: 'nameScientific', label: '', placeholder: '請輸入器械名稱', type: 'input' }, // 可能可以移除
        { key: 'brand', label: '廠牌-', placeholder: '請輸入廠牌', type: 'input' }, // 可能可以移除
        { key: 'manufactureId', label: '型號-', placeholder: '請輸入器械型號', type: 'input' },
        { key: 'configName', label: '名稱-', placeholder: '請輸入包盤名稱', type: 'input' },
        { key: 'configCode', label: '包盤編號-', placeholder: '請輸入包盤編號', type: 'input' },
        { key: 'dispatchName', label: '批次名稱-', placeholder: '請輸入批次名稱', type: 'input' },
        { key: 'qrcode', label: 'qrcode-', placeholder: '請掃描qrcode', type: 'input' },
        {
          key: 'division',
          label: '',
          type: 'select',
          optionKey: 'id',
          options: this.$store.getters.divisionsWithAll,
          defaultVal: 'all',
        },
        {
          key: 'applyPackageStatus',
          label: '狀態/',
          type: 'select',
          optionKey: 'status',
          options: [
            {
              status: 'all',
              name: '預設',
            },
            ...this.$statusFilters.packageApply('all'),
          ],
          defaultVal: 'all',
        },
        {
          key: 'sterilizedType',
          label: '',
          type: 'select',
          optionKey: 'status',
          options: [
            {
              status: 'all',
              name: '預設',
            },
            ...this.$statusFilters.sterilized('all'),
          ],
          defaultVal: 'all',
        },
        {
          key: 'washingType',
          label: '',
          type: 'select',
          optionKey: 'status',
          options: [
            {
              status: 'all',
              name: '預設',
            },
            ...this.$statusFilters.washing('all'),
          ],
          defaultVal: 'all',
        },
      ],
      searchItems: [],
      searchConfigArr: [],
    };
  },
  computed: {
    isShowUndo() {
      return this.searchItemBadges.length > 0;
    },
    searchItemBadges() {
      return this.searchConfigArr.filter((item) => item.type !== 'select');
    },
  },
  methods: {
    // Main Handler
    modifyItems(val) {
      this.searchItems = val.reduce((acc, current) => {
        const searchKey = current.name || current;
        const item = this.configItems.find((config) => searchKey === config.key);
        if (!item) return acc;
        return [
          ...acc,
          {
            ...item,
            value: current.value ?? item.defaultVal ?? '',
            emitKey: current.key || current,
          },
        ];
      }, []);
    },
    configSearch({ key, emitKey, value, label, type }) {
      if (value === '') return;
      const idx = this.searchConfigArr.findIndex((item) => item.key === key);
      if (idx !== -1) {
        this.searchConfigArr.splice(idx, 1);
      }
      this.searchConfigArr.push({ key, emitKey, value, label, type });
      this.generateQueryParams();
    },
    // Emits
    generateQueryParams() {
      const filtersItem = [
        'division=all',
        'applyPackageStatus=all',
        'sterilizedType=all',
        'washingType=all',
      ];
      const emitContentArray = this.searchConfigArr
        .filter((config) => !filtersItem.includes(`${config.key}=${config.value}`))
        .map((item) => ({ key: item.emitKey, value: item.value }));
      this.$emit('queryContentArray', emitContentArray);
    },
    deleteSearchItem(idx) {
      this.searchConfigArr.splice(idx, 1);
      this.generateQueryParams();
    },
    searchDefault() {
      this.searchConfigArr = [];
      this.generateQueryParams();
    },
  },
  mounted() {
    this.modifyItems(this.searchConfig);
  },
};
</script>
