<template>
  <div class="full-width full-height q-py-md text-center">
    <VChart
      class="chart"
      :option="barOptions"
      autoresize
      :styles="{ width: '220px', height: '220px' }"
    />
    <ul>
      <li
        class="flex justify-between q-py-xs"
        style="border-bottom: 1px solid #e5e6e6"
        v-for="(item, idx) in tempContents"
        :key="item.name"
      >
        <div class="flex items-center">
          <span
            style="width:10px; height: 10px; display:block"
            :style="{ backgroundColor: barOptions.color[idx] }"
            class="q-mr-sm"
          ></span>
          <p class="text-body2">{{ item.name }}</p>
        </div>

        <p class="text-body2">{{ item.value }}</p>
      </li>
      <li class="flex justify-between q-py-xs" style="border-bottom: 1px solid #e5e6e6">
        <div class="flex items-center">
          <span style="width:10px; height: 10px; display:block" class="q-mr-sm bg-grey-1"></span>
          <p class="text-body2">Total</p>
        </div>
        <p class="text-body2">
          {{ tempContents.reduce((acc, current) => acc + current.value, 0) }}
        </p>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'PieChart',
  props: {
    content: {
      type: Array,
    },
  },
  data() {
    return {
      tempContents: [],
    };
  },
  computed: {
    barOptions() {
      return {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)',
        },
        color: ['#0acf97', '#ee4e5a', '#fcc100', '#727cf5'],
        series: [
          {
            type: 'pie',
            radius: ['60%', '90%'],
            avoidLabelOverlap: false,
            label: {
              position: 'inner',
              fontSize: 12,
              formatter: '{d}%',
            },
            data: this.tempContents,
          },
        ],
      };
    },
  },
  watch: {
    content() {
      this.tempContents = [...this.content];
    },
  },
  mounted() {
    this.tempContents = [...this.content];
  },
};
</script>
<style lang="scss">
.chart {
  height: 200px;
}
</style>
