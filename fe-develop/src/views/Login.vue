<template>
  <q-layout class="bg-grey-1">
    <q-page-container>
      <q-page class="flex bg-image flex-center">
        <q-card v-bind:style="$q.screen.lt.sm ? { width: '80%' } : { width: '30%' }">
          <q-card-section>
            <q-avatar size="103px" class="absolute-center shadow-10 bg-primary">
              <img src="/icons/logo.png" style="object-fit: contain" />
            </q-avatar>
          </q-card-section>
          <q-card-section>
            <div class="text-center q-pt-lg">
              <div class="col text-h6 ellipsis">
                Log in
              </div>
            </div>
          </q-card-section>
          <q-card-section>
            <v-form @submit="handleSubmit" id="form" class="bg-white q-pa-md q-mb-md" ref="form">
              <v-field
                name="帳號"
                class="q-pa-md"
                rules="alpha_num"
                v-model="account"
                v-slot="{ errorMessage, value, field }"
              >
                <q-input
                  label="帳號"
                  dense
                  :model-value="value"
                  v-bind="field"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                />
              </v-field>
              <v-field
                name="密碼"
                class="q-pa-md"
                :rules="{ required: true, regex: /^[A-Za-z0-9_@-]+$/ }"
                v-model="password"
                v-slot="{ errorMessage, value, field }"
              >
                <q-input
                  label="密碼"
                  dense
                  :model-value="value"
                  v-bind="field"
                  type="password"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                />
              </v-field>
              <q-btn type="submit" id="login">
                送出
              </q-btn>
            </v-form>
          </q-card-section>
        </q-card>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script>
export default {
  name: 'login',
  data() {
    return {
      account: '',
      password: '',
    };
  },
  methods: {
    async handleSubmit() {
      this.$gLoading(true);
      try {
        const payload = {
          account: this.account,
          password: this.password,
        };
        await this.$store.dispatch('user/login', payload);
        this.$router.push('/');
      } catch (error) {
        this.$q.notify({
          type: 'negative',
          message: '帳號密碼輸入錯誤',
        });
      } finally {
        this.$gLoading(false);
      }
    },
  },
};
</script>
