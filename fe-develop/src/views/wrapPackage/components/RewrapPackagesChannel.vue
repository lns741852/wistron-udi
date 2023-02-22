<template>
  <div class="q-pa-md bg-blue-grey-1 full-height">
    <div class="row items-center justify-between text-center q-mb-md">
      <p v-if="title">{{ title }}({{ devices.length }})</p>
      <div class="flex items-center">
        <q-input
          type="search"
          :label="inputLabel"
          v-model.trim="scanText"
          dense
          outlined
          class="q-mr-xs"
          style="margin-bottom: 5px"
          @keyup.enter="fire"
        >
          <template v-slot:append>
            <q-icon name="qr_code_scanner" />
          </template>
        </q-input>
        <slot name="btnToggle" />
      </div>
    </div>
    <ul v-if="devices.length > 0">
      <VDeviceCard
        class="q-mb-sm"
        htmlTag="li"
        v-for="device in devices"
        :key="device.id"
        :device="device"
        :hasAddOnButton="device.hasAddOnButton"
        :showBadge="device.showDeviceBadge"
        @changeStatus="(val) => this.$emit('changeStatus', val)"
      />
    </ul>
    <p v-else class="text-center">----</p>
  </div>
</template>

<script>
import VDeviceCard from '@/components/VDeviceCard.vue';

export default {
  name: 'rewrapPackages',
  components: {
    VDeviceCard,
  },
  props: {
    devices: {
      type: Array,
      default: () => [],
    },
    title: {
      type: String,
    },
    inputLabel: {
      type: String,
      default: '請掃描',
    },
  },
  data() {
    return {
      scanText: '',
    };
  },
  methods: {
    fire() {
      if (this.scanText) {
        this.$emit('scanText', this.scanText);
        this.scanText = '';
      }
    },
  },
};
</script>
