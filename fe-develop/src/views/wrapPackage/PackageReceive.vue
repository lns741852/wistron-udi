<template>
  <VBreadCrumb backTitle="配盤區" />
  <ReceiveBatch processActionName="reSterilizedReceive" :currentStationId="getPackageStation" />
</template>
<script>
import ReceiveBatch from '@/components/BatchPackageReceive.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  inject: ['getPackageStation'],
  components: {
    ReceiveBatch,
    VBreadCrumb,
  },
};
</script>
