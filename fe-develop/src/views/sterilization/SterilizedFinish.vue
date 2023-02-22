<template>
  <VBreadCrumb backTitle="滅菌區" />
  <SterilizedInfo mode="action" />
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
