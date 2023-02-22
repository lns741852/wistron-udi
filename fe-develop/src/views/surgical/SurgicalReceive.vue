<template>
  <VBreadCrumb backTitle="手術管理區" />
  <SurgicalApplyCheck :surgicalStatus="2" checkDeviceMode="surgical" />
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
