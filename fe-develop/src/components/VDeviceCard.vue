<template>
  <q-card class="q-mb-sm" :tag="htmlTag">
    <q-card-section>
      <div class="row justify-between q-mb-sm">
        <div>
          <h6 class="text-body2 text-weight-bold q-mr-xs">
            {{ device.nameScientific }}
          </h6>
          <p class="text-caption ">{{ device.name }}</p>
        </div>
        <div v-if="hasAddOnButton">
          <q-btn color="grey-7" class="text-caption" round flat icon="more_vert">
            <q-menu cover auto-close>
              <q-list>
                <q-item
                  v-for="item in toggleStatusList"
                  :key="item.status"
                  clickable
                  @click="$emit('changeStatus', { id: device.id, status: item.status })"
                >
                  <q-item-section>{{ item.name }}</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-btn>
        </div>
      </div>
      <div class="row justify-between items-end">
        <p class="col-6 text-caption text-grey">{{ device.spec }}</p>
        <div class="col-6 self-end">
          <div class="column justify-end items-end">
            <q-badge
              v-if="showBadge"
              transparent
              align="middle"
              class="q-pa-xs q-mb-xs"
              :color="deviceStatus.color"
            >
              {{ deviceStatus.name }}
            </q-badge>
            <p class="text-grey text-caption">{{ device.qrcode }}</p>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import { surgeryDevice as deviceStatus } from '@/common/statusFilters';

export default {
  name: 'VDeviceCard',
  props: {
    device: {
      type: Object,
    },
    htmlTag: {
      type: String,
    },
    showBadge: {
      type: Boolean,
      default: true,
    },
    hasAddOnButton: {
      type: Boolean,
      default: false,
    },
    toggleStatusList: {
      type: Array,
      default: () => [
        {
          name: deviceStatus('UNSCANABLE')?.name,
          status: deviceStatus('UNSCANABLE')?.status,
        },
        {
          name: deviceStatus('MISSING')?.name,
          status: deviceStatus('MISSING')?.status,
        },
      ],
    },
  },
  data() {
    return {
      deviceStatusList: [],
    };
  },
  computed: {
    deviceStatus() {
      const dStatus = this.device.updateStatus || this.device.status;
      const device = this.deviceStatusList.find((d) => d.status === dStatus);
      return {
        color: device?.color,
        name: device?.name,
      };
    },
  },
  methods: {
    init() {
      this.deviceStatusList = [...this.$statusFilters.surgeryDevice('all')];
      const replaceIdx = this.deviceStatusList.findIndex((d) => d.value === 'REPLACED');
      const scanDoneIdx = this.deviceStatusList.findIndex((d) => d.value === 'RECEIVE_SCAN_DONE');
      this.deviceStatusList[replaceIdx].name = '待維修';
      this.deviceStatusList[scanDoneIdx].name = '已清點';
    },
  },
  created() {
    this.init();
  },
};
</script>
