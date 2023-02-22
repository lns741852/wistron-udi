<template>
  <q-header elevated class="bg-white">
    <q-toolbar>
      <q-btn flat dense round @click="$emit('toggle')" aria-label="Menu" icon="menu" color="dark" />
      <q-input
        v-if="$route.meta?.scan"
        :placeholder="placeholder"
        filled
        dense
        debounce="200"
        maxlength="64"
        v-model.trim="selectedQrcode"
        @keyup.enter="headerScan"
      >
        <template v-slot:append>
          <q-icon name="qr_code_scanner" />
        </template>
      </q-input>

      <q-space />

      <p class="text-primary">{{ accountName }}</p>
      <q-btn round flat>
        <q-avatar size="26px">
          <img src="https://cdn.quasar.dev/img/boy-avatar.png" />
        </q-avatar>
        <q-menu>
          <q-list style="min-width: 50px">
            <q-card class="text-center no-shadow no-border">
              <q-btn @click="logout" label="登出" flat />
            </q-card>
          </q-list>
        </q-menu>
      </q-btn>
    </q-toolbar>
  </q-header>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'v-header',
  data() {
    return {
      selectedQrcode: '',
    };
  },
  computed: {
    ...mapGetters({ accountName: 'name' }),
    placeholder() {
      const types = [
        { type: 'box', name: '器械盒' },
        { type: 'device', name: '器械' },
        { type: 'package', name: '包盤' },
      ];
      const scanType = this.$route.meta?.scan;
      const item = scanType ? types.find((t) => t.type === scanType) : {};
      return Object.keys(item).length > 0 ? `請掃描${item.name}` : '-';
    },
  },
  methods: {
    headerScan() {
      if (this.selectedQrcode) {
        this.$emit('headerInputQuery', this.selectedQrcode);
        this.selectedQrcode = '';
      }
    },
    async logout() {
      this.$gLoading(true);
      await this.$store.dispatch('user/logout');
      this.$nextTick(() => {
        window.location.reload();
      });
    },
  },
};
</script>
