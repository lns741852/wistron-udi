<template>
  <VBreadCrumb backTitle="管理區" />
  <SterilizedInfo mode="view" />
</template>

<script>
import VBreadCrumb from '@/components/VBreadCrumb.vue';
import SterilizedInfo from './components/SterilizedInfo.vue';

export default {
  components: {
    VBreadCrumb,
    SterilizedInfo,
  },
};
</script>
