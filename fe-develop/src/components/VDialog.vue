<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss @before-hide="close">
    <div class="bg-white" :style="dialogStyle">
      <div class="q-px-lg q-pt-sm">
        <div class="row justify-between items-center">
          <h1 class="text-h6" v-if="title">{{ title }}</h1>
          <q-btn flat round icon="close" @click="close" />
        </div>
      </div>
      <q-separator />
      <div class="dialog-content q-pa-md bg-white">
        <slot name="content" />
      </div>
      <q-separator spaced v-if="$slots.hasOwnProperty('footer')" />
      <div class="row justify-end q-pa-sm" v-if="$slots.hasOwnProperty('footer')">
        <slot name="footer" />
      </div>
    </div>
  </q-dialog>
</template>

<script>
export default {
  name: 'vDialog',
  props: {
    title: String,
    dialogWidth: String,
    show: Boolean,
  },
  emits: ['update:show'],
  data() {
    return {
      showDialog: false,
    };
  },
  watch: {
    show(val) {
      this.showDialog = val;
    },
  },
  computed: {
    dialogStyle() {
      return this.dialogWidth ?? 'max-width: 900px; width: 100%';
    },
  },
  methods: {
    close() {
      this.showDialog = false;
      this.$emit('update:show', false);
    },
  },
};
</script>
