<template>
  <q-card class="q-ma-md">
    <q-banner inline-actions rounded class="bg-primary text-white">
      <template v-slot:avatar>
        <q-icon name="warning" color="orange" />
      </template>
      醫療器械功能資訊 -
    </q-banner>
    <p class="q-pa-lg">-</p>
  </q-card>
  <!-- <a href="#" class="nav-link" @click.prevent="handleLogout">登出</a> -->
</template>

<script>
import JWT from '@/api/cookies';

export default {
  name: 'Home',
  data() {
    return {
      isAuthenticated: false,
    };
  },
  methods: {
    async handleLogout() {
      this.$gLoading(true);
      try {
        JWT.removeToken();
        this.isAuthenticated = false;
        this.$router.push('/login');
      } catch (error) {
        this.$gNotifyError('系統發生異常');
      } finally {
        this.$gLoading(false);
      }
    },
  },
};
</script>
