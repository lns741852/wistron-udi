<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="bg-white q-pa-lg shadow-1 q-mb-md" style="width: 600px">
      <div class="row justify-between items-start">
        <div class="q-pt-sm">
          <h2 class="text-h6 q-mb-md">鍋次名稱: {{ detail.name }}</h2>
        </div>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="q-mb-md">
        <p class="text-subtitle2 q-mb-sm">
          滅菌狀態:
          <q-badge align="middle" :color="detail.status ? 'secondary' : 'accent'">{{
            detail.status ? '成功' : '失敗'
          }}</q-badge>
        </p>
        <ul class="row q-mb-md text-body2">
          <li class="col-12 q-mb-xs">
            <p>開始時間: {{ detail.startTime }}</p>
            <p>結束時間: {{ detail.finishTime }}</p>
          </li>
          <li class="col-12 q-mb-xs" v-if="detail.expireTime">
            <p>有效期限: {{ detail.expireTime }}</p>
          </li>
          <li class="col-12 q-mb-xs">
            <p>滅菌時間: {{ time?.indicator || '-' }}</p>
          </li>
          <li class="col-12 q-mb-xs">
            <p>滅菌溫度: {{ temperature?.indicator || '-' }}</p>
          </li>
        </ul>

        <ul v-if="detail.monitorItems.length > 0">
          <li class="row q-py-sm border-bottom text-subtitle2">
            <p class="col-3 text-weight-bold">項目</p>
            <p class="col-7 text-weight-bold">指標</p>
            <p class="col-2 text-weight-bold text-center">結果</p>
          </li>
          <li
            v-for="item in tableTest"
            :key="item.type"
            class="row items-center q-py-sm border-bottom"
          >
            <p class="col-3 text-grey text-body2 text-weight-bold">
              {{ item.label || '-' }}
            </p>
            <p class="col-7 text-grey text-body2">{{ item.indicator || '-' }}</p>
            <p
              class="col-2 text-center text-subtitle2"
              :class="item.result ? 'text-secondary' : 'text-accent'"
            >
              {{ item.result ? '通過' : '未通過' }}
            </p>
          </li>
        </ul>
      </div>
    </div>
  </q-dialog>
</template>

<script>
export default {
  name: 'sterilizationView',
  props: {
    detail: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      showDialog: false,
      tests: [
        { type: 0, label: 'BD TEST' },
        { type: 1, label: '滅菌時間' },
        { type: 2, label: '滅菌溫度' },
        { type: 3, label: '包外指示劑' },
        { type: 4, label: '第五級整合型CI' },
        { type: 5, label: '生物培養' },
      ],
    };
  },
  computed: {
    tableTest() {
      return this.detail?.monitorItems
        .filter((t) => t.type !== 1 && t.type !== 2)
        .map((t) => {
          const label = this.tests.find((item) => item.type === t.type)?.label;
          return { ...t, label };
        });
    },
    time() {
      return this.detail?.monitorItems.find((t) => t.type === 1);
    },
    temperature() {
      return this.detail?.monitorItems.find((t) => t.type === 2);
    },
  },
};
</script>
