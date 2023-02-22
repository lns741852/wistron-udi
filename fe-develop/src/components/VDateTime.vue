<template>
  <div class="row">
    <q-input
      mask="####-##-##"
      :rules="[(v) => !Number.isNaN(new Date(v).valueOf())]"
      dense
      outlined
      today-btn
      v-model="tempDate"
      class="q-mr-sm"
    >
      <template v-slot:append>
        <q-icon name="event" class="cursor-pointer">
          <q-popup-proxy transition-show="scale" transition-hide="scale">
            <q-date v-model="tempDate" mask="YYYY-MM-DD">
              <div class="row items-center justify-end">
                <q-btn v-close-popup label="Close" color="primary" flat />
              </div>
            </q-date>
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>
    <q-input mask="time" :rules="['time']" dense outlined v-model="tempTime">
      <template v-slot:append>
        <q-icon name="access_time" class="cursor-pointer">
          <q-popup-proxy transition-show="scale" transition-hide="scale">
            <q-time v-model="tempTime" mask="HH:mm">
              <div class="row items-center justify-end">
                <q-btn v-close-popup label="Close" color="primary" flat />
              </div>
            </q-time>
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>
  </div>
</template>

<script>
export default {
  props: {
    date: String,
    time: String,
  },
  data() {
    return {
      tempDate: '',
      tempTime: '',
    };
  },
  watch: {
    tempDate(val) {
      if (val !== this.date) {
        this.$emit('changeDate', val);
      }
    },
    tempTime(val) {
      if (val !== this.time) {
        this.$emit('changeTime', val);
      }
    },
  },
  created() {
    this.tempDate = this.date;
    this.tempTime = this.time;
  },
};
</script>
