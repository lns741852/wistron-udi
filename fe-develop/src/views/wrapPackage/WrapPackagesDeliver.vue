<template>
  <VBreadCrumb backTitle="配盤區" />
  <DeliverBatch
    processActionName="packageDeliver"
    :currentStationId="getPackageStation"
    :deliverStationId="getSterilizedStation"
  />
</template>
<script>
import DeliverBatch from '@/components/BatchPackageDeliver.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  inject: ['getPackageStation', 'getSterilizedStation'],
  components: {
    DeliverBatch,
    VBreadCrumb,
  },
};
</script>
