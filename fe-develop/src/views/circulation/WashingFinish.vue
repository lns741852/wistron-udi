<template>
  <VBreadCrumb backTitle="管理區" />
  <WashingInfo mode="action" />
</template>

<script>
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import WashingInfo from '@/views/circulation/components/WashingInfo.vue';

export default {
  components: {
    VBreadCrumb,
    WashingInfo,
  },
};
</script>
