<template>
  <div class="bg-white q-pa-lg">
    <p class="text-weight-bold q-mb-md">手術資訊</p>
    <div class="q-mb-md">
      <ul class="q-mb-lg row" v-if="result.applyId">
        <li class="list col-6">
          <p class="list__title">手術編號</p>
          <p class="list__text">{{ result.operatingNumber || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">訂單號碼</p>
          <p class="list__text">{{ orderNum || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">科別</p>
          <p class="list__text">
            {{ $store.getters.divisionById(result.divisionId)?.name || '-' }}
          </p>
        </li>
        <li class="list col-6">
          <p class="list__title">病歷號</p>
          <p class="list__text">{{ result.medicalRecordNumber || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">手術名稱</p>
          <p class="list__text">{{ result.surgeryName || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">醫生</p>
          <p class="list__text">{{ result.doctor || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">手術室</p>
          <p class="list__text">{{ result.operatingRoom || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">手術日期</p>
          <p class="list__text">{{ result.operatingDate || '-' }}</p>
        </li>
        <li class="list col-6">
          <p class="list__title">排刀順序</p>
          <p class="list__text">{{ result.operatingOrder || '-' }}</p>
        </li>
      </ul>
      <p v-else>無手術使用紀錄</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'surgicalTracking',
  props: {
    result: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    orderNum() {
      const { orders = [] } = this.result;
      return orders.length > 0 ? orders[0].id : null;
    },
  },
};
</script>
