<template>
  <VBreadCrumb backTitle="庫存區" />
  <DeliverBatch
    processActionName="reSterilizedDeliver"
    :currentStationId="getInventoryStation"
    :deliverStationId="getPackageStation"
  />
</template>
<script>
import DeliverBatch from '@/components/BatchPackageDeliver.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  inject: ['getInventoryStation', 'getPackageStation'],
  components: {
    DeliverBatch,
    VBreadCrumb,
  },
};
</script>
