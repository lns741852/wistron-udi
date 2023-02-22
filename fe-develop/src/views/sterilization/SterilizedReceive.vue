<template>
  <VBreadCrumb backTitle="滅菌區" />
  <ReceiveBatch processActionName="sterilizedReceive" :currentStationId="getSterilizedStation" />
</template>
<script>
import ReceiveBatch from '@/components/BatchPackageReceive.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  inject: ['getSterilizedStation'],
  components: {
    ReceiveBatch,
    VBreadCrumb,
  },
};
</script>
