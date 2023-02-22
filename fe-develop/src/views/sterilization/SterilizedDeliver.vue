<template>
  <VBreadCrumb backTitle="滅菌區" />
  <DeliverBatch
    processActionName="sterilizedDeliver"
    :currentStationId="getSterilizedStation"
    :deliverStationId="getInventoryStation"
  />
</template>
<script>
import DeliverBatch from '@/components/BatchPackageDeliver.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  inject: ['getSterilizedStation', 'getInventoryStation'],
  components: {
    DeliverBatch,
    VBreadCrumb,
  },
};
</script>
