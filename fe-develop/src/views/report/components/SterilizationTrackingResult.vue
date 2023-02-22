<template>
  <div class="bg-white q-pa-lg">
    <p class="text-weight-bold q-mb-md">滅菌資訊</p>
    <div class="q-mb-md">
      <ul class="q-mb-lg row">
        <li class="list col-6">
          <p class="list__title">鍋次名稱</p>
          <p class="list__text">{{ result.name || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">滅菌狀態</p>
          <p class="list__text">
            <q-badge
              v-if="result.status !== null"
              transparent
              align="middle"
              class="q-pa-xs"
              :color="result.status === 1 ? 'secondary' : result.status === 0 ? 'accent' : 'grey'"
            >
              {{ result.status === 1 ? '滅菌完成' : result.status === 0 ? '滅菌失敗' : '滅菌中' }}
            </q-badge>
          </p>
        </li>

        <li class="list col-12">
          <p class="list__title">消毒鍋</p>
          <p class="list__text">{{ result.sterilizer || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">入鍋時間</p>
          <p class="list__text">{{ result.startTime || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">出鍋時間</p>
          <p class="list__text">{{ result.finishTime || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">滅菌時間</p>
          <p class="list__text">{{ time.indicator || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">滅菌溫度</p>
          <p class="list__text">{{ temperature.indicator || '-' }}</p>
        </li>
      </ul>
      <ul>
        <li class="row q-py-sm border-bottom text-subtitle2">
          <p class="col-4 text-weight-bold">項目</p>
          <p class="col-6 text-weight-bold">指標</p>
          <p class="col-2 text-weight-bold text-center">結果</p>
        </li>
        <li
          class="row items-center q-py-sm border-bottom text-body2 text-weight-bold"
          v-for="test in tests"
          :key="test.type"
        >
          <p class="col-4 text-grey text-body2 text-weight-bold" data-type="0">
            {{ test.label }}
          </p>
          <p class="col-6 text-grey">{{ test.indicator }}</p>
          <div class="col-2 text-center">
            <p :class="test.result ? 'text-secondary' : 'text-accent'">
              {{ test.result === 1 ? '通過' : test.result === 0 ? '失敗' : '-' }}
            </p>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { sterilizedTests } from '@/common/statusFilters';

export default {
  name: 'sterilizationTrackingResult',
  props: {
    result: {
      type: Object,
      default: () => ({}),
    },
  },
  methods: {
    sterilizedTestLabel(type) {
      return sterilizedTests().find((t) => t.type === type) || {};
    },
  },
  computed: {
    time() {
      return this.result.monitorItems?.find((t) => t.type === 1) || {};
    },
    temperature() {
      return this.result.monitorItems?.find((t) => t.type === 2) || {};
    },
    tests() {
      if (!this.result.monitorItems) return [];
      return this.result?.monitorItems
        .filter((t) => t.type !== 1 && t.type !== 2)
        .map((item) => ({
          ...item,
          label: this.sterilizedTestLabel(item.type)?.label,
        }));
    },
  },
};
</script>
