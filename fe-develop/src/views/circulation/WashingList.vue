<template>
  <VBreadCrumb backTitle="管理區" />
  <WashingInfo mode="view" />
</template>

<script>
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import WashingInfo from './components/WashingInfo.vue';

export default {
  components: {
    VBreadCrumb,
    WashingInfo,
  },
};
</script>
