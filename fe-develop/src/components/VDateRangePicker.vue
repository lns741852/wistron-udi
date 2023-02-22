<template>
  <div class="row q-gutter-x-md">
    <q-input
      dense
      outlined
      v-model="start"
      mask="####-##-##"
      :rules="[(v) => !Number.isNaN(new Date(v).valueOf())]"
    >
      <template v-slot:append>
        <q-icon name="event" class="cursor-pointer">
          <q-popup-proxy transition-show="scale" transition-hide="scale">
            <q-date v-model="start" mask="YYYY-MM-DD" :options="startOptions" today-btn>
              <div class="row items-center justify-end">
                <q-btn v-close-popup label="Close" color="primary" flat />
              </div>
            </q-date>
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>
    <!-- using .valueof method to return primitive value  -->
    <q-input
      dense
      outlined
      v-model="end"
      mask="####-##-##"
      :rules="[(v) => !Number.isNaN(new Date(v).valueOf())]"
    >
      <template v-slot:append>
        <q-icon name="event" class="cursor-pointer">
          <q-popup-proxy transition-show="scale" transition-hide="scale">
            <q-date v-model="end" mask="YYYY-MM-DD" :options="endOptions" today-btn>
              <div class="row items-center justify-end">
                <q-btn v-close-popup label="Close" color="primary" flat />
              </div>
            </q-date>
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>
  </div>
</template>

<script>
export default {
  name: 'VDateRangePicker',
  data() {
    return {
      limit: 120 * 24 * 60 * 60 * 1000,
      startOptions: (date) => {
        if (!this.end) return true;
        const time = new Date(this.end).getTime() - this.limit;
        return new Date(date) >= new Date(time) && new Date(date) <= new Date(this.end);
      },
      endOptions: (date) => {
        if (!this.start) return true;
        const start = `${this.start} 00:00:00`;
        const time = new Date(start).getTime() + this.limit;
        return new Date(date) < new Date(time) && new Date(date) >= new Date(start);
      },
      start: '',
      end: '',
    };
  },
  watch: {
    start(val) {
      this.$emit('start', val);
    },
    end(val) {
      this.$emit('end', val);
    },
  },
};
</script>
