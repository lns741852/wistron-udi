<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="q-pt-md shadow-1 bg-white" style="max-width: 600px; width: 100%">
      <div class="row justify-between items-center q-mb-md q-px-lg">
        <h1 class="text-h6">選擇數量</h1>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <q-separator />
      <div class="q-pt-sm q-pb-md q-px-lg">
        <q-badge
          transparent
          align="middle"
          class="q-pa-xs q-mb-sm"
          :color="tempItem.pickQty === 0 ? 'grey' : 'secondary'"
        >
          {{ tempItem.pickQty === 0 ? '尚未選擇' : '清單已選擇' }}
        </q-badge>
        <div class="column justify-between">
          <div class="q-ml-lg q-mb-md">
            <h6 class="text-weight-bold q-mb-sm">包盤名稱:{{ tempItem.configName }}</h6>
            <p class="text-body1 text-grey">包盤編號:{{ tempItem.configCode }}</p>
          </div>
          <div class="self-center ">
            <div class="row items-center">
              <q-btn
                round
                size="small"
                color="grey"
                icon="remove"
                class="q-mr-md"
                :disable="selectQuantity <= 1"
                @click="selectQuantity -= 1"
              />
              <q-input
                v-model.number="selectQuantity"
                type="number"
                dense
                outlined
                min="1"
                :max="maxVolume"
                @update:model-value="changeQuantity"
                input-style="width: 50px"
              />
              <q-btn
                round
                class="q-ml-md"
                color="grey"
                icon="add"
                :disable="isMaxSize"
                @click="selectQuantity += 1"
              />
            </div>
          </div>
        </div>
      </div>
      <q-separator />
      <div class="flex justify-end q-pa-lg">
        <q-btn
          color="blue"
          label="確定"
          type="button"
          icon="img:/icons/check.png"
          @click="checkQuantity"
        />
      </div>
    </div>
  </q-dialog>
</template>

<script>
export default {
  name: 'packagePickQuantity',
  props: {
    item: {
      type: Object,
      require: true,
    },
  },
  emits: ['addQuantity'],
  data() {
    return {
      showDialog: false,
      selectQuantity: 1,
      tempItem: {},
      maxVolume: 99,
    };
  },
  computed: {
    isMaxSize() {
      return this.selectQuantity >= this.maxVolume;
    },
  },
  methods: {
    checkQuantity() {
      this.$emit('addQuantity', { ...this.tempItem, pickQty: this.selectQuantity });
      this.showDialog = false;
      this.selectQuantity = 1;
    },
    changeQuantity(val) {
      if (this.isMaxSize) {
        this.selectQuantity = this.maxVolume;
      } else if (val <= 0) {
        this.selectQuantity = 1;
      }
    },
  },
  watch: {
    item(val) {
      this.tempItem = { ...val };
      this.selectQuantity = this.tempItem.pickQty || 1;
    },
  },
};
</script>
