<template>
  <div class="row items-center justify-between q-mb-lg">
    <div class="row items-center">
      <h5 class="text-weight-bold text-grey-9 q-mr-md">
        {{ title }}
      </h5>
    </div>
    <q-breadcrumbs>
      <q-breadcrumbs-el :label="backTitle" :to="backPath" />
      <q-breadcrumbs-el class="text-info text-weight-bold" :label="title" />
    </q-breadcrumbs>
  </div>
</template>

<script>
export default {
  name: 'vBreadCrumb',
  props: {
    backTitle: {
      type: String,
    },
    backPath: {
      type: String,
    },
  },
  data() {
    return {
      title: '',
    };
  },
  mounted() {
    this.title = this.$route.meta?.title;
  },
};
</script>
