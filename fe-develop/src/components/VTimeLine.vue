<template>
  <div class="q-pa-md bg-white">
    <p class="text-weight-bold q-mb-md">
      <slot name="title"></slot>
    </p>
    <div :style="timeLineWidth">
      <q-timeline :layout="layout" color="secondary" side="right">
        <q-timeline-entry v-for="val in values" :key="val.id">
          <template #title>
            {{ val.title }}
          </template>
          <template #subtitle>
            {{ val.recordAt }}
          </template>
          <p class="text-grey-6 text-caption">
            {{ val.text }}
          </p>
        </q-timeline-entry>
      </q-timeline>
    </div>
  </div>
</template>

<script>
export default {
  name: 'vTimeLine',
  props: {
    values: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    timeLineWidth() {
      if (this.$q.screen.lt.sm) {
        return 'width: 100%';
      }
      if (this.$q.screen.lt.md) {
        return 'width: 80%';
      }
      return 'width: 60%';
    },
    layout() {
      if (this.$q.screen.lt.sm) {
        return 'dense';
      }
      if (this.$q.screen.lt.md) {
        return 'comfortable';
      }
      return 'loose';
    },
  },
};
</script>
