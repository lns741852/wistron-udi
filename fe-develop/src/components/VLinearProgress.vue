<template>
  <div class="row no-wrap items-center">
    <div class="col-8 col-md-9">
      <q-linear-progress size="10px" :value="value" color="blue" />
    </div>
    <div class="col-4 col-md-3">
      <p class="text-right q-pl-sm text-secondary">{{ content }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'linearProgress',
  props: {
    value: Number,
    content: [String, Number],
  },
};
</script>
