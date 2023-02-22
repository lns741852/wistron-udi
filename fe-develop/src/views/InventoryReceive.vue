<template>
  <VBreadCrumb backTitle="庫存區" />
  <ReceiveBatch processActionName="inventoryReceive" :currentStationId="getInventoryStation" />
</template>
<script>
import ReceiveBatch from '@/components/BatchPackageReceive.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  inject: ['getInventoryStation'],
  components: {
    ReceiveBatch,
    VBreadCrumb,
  },
};
</script>
