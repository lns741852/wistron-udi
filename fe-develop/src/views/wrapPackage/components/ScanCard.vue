<template>
  <q-card
    flat
    bordered
    class="column justify-between q-pt-xs q-pb-md q-px-md full-height cursor-pointer"
    @click="clickSelf"
  >
    <div class="row justify-between items-center q-mb-md">
      <p class="text-weight-bold text-body2">
        {{ title }}
        <slot name="caption"></slot>
      </p>
      <q-img :src="imgIcon" width="50px" />
    </div>
    <q-input
      autofocus
      ref="inputField"
      :disable="disable"
      :filled="filled"
      v-model="text"
      type="search"
      dense
      outlined
      style="margin-bottom: 5px"
      @keyup.enter="fireEnter"
    >
      <template v-slot:append>
        <q-icon name="qr_code_scanner" />
      </template>
    </q-input>
  </q-card>
</template>

<script>
export default {
  name: 'scanCard',
  props: {
    title: String,
    imgIcon: String,
    disable: {
      type: Boolean,
      default: false,
    },
    filled: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      text: '',
    };
  },
  methods: {
    fireEnter() {
      this.$emit('fireEnter', { text: this.text });
      this.text = '';
    },
    clickSelf() {
      this.$emit('clickSelf');
      this.focusInput();
    },
    focusInput() {
      this.$nextTick(() => {
        this.$refs.inputField.focus();
      });
    },
  },
};
</script>
