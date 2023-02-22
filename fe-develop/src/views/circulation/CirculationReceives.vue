<template>
  <VBreadCrumb backTitle="回收清洗區" />
  <SurgicalApplyCheck :surgicalStatus="4" checkDeviceMode="recycle" />
</template>

<script>
import SurgicalApplyCheck from '@/components/SurgicalApplyCheck.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  components: {
    SurgicalApplyCheck,
    VBreadCrumb,
  },
};
</script>
