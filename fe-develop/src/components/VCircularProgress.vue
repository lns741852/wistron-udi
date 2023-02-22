<template>
  <q-card class="q-py-md q-mb-md">
    <div class="column justify-center">
      <q-circular-progress
        show-value
        font-size="40px"
        class="text-secondary q-ma-md self-center"
        :value="(progress.current / progress.total) * 100"
        size="150px"
        :thickness="0.2"
        color="secondary"
        track-color="grey-3"
      >
        {{ ((progress.current / progress.total) * 100)?.toFixed(0) || 0 }}%
      </q-circular-progress>
      <ul class="row justify-around">
        <li class="text-center">
          <h4 class="text-weight-bold q-mb-xs">{{ progress.total }}</h4>
          <p class="text-grey">{{ title }}總數</p>
        </li>
        <li class="text-center">
          <h4 class="text-secondary text-weight-bold q-mb-xs">
            {{ progress.current }}
          </h4>
          <p class="text-secondary">已掃描{{ title }}</p>
        </li>
      </ul>
    </div>
  </q-card>
</template>
<script>
export default {
  name: 'VCircularProgress',
  props: {
    title: {
      type: String,
      default: '包盤',
    },
    progress: {
      type: Object,
    },
  },
};
</script>
