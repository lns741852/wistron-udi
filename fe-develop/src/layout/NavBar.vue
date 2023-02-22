<template>
  <q-drawer v-model="isOpen" :width="240" show-if-above bordered class="bg-primary text-white">
    <div class="q-pa-md logo" @click="$router.push('/')">
      <img src="/icons/logo.png" class="cursor-pointer" />
    </div>
    <q-list>
      <div class="q-mb-md" v-for="(item, key) in navbar" :key="`menu${key}`">
        <q-item-label header class="text-subtitle1 text-weight-bolder text-white">{{
          item.name
        }}</q-item-label>
        <div v-for="(group, idx) in item.groups" :key="`group${idx}`">
          <!-- 有下一層 -->
          <template v-if="group.contents?.length > 0">
            <q-expansion-item
              :icon="group.icon"
              :label="group.name"
              :header-style="{ paddingTop: 0, paddingBottom: 0 }"
              :key="`group${idx}`"
            >
              <q-list>
                <q-item
                  v-for="(content, idx) in group.contents"
                  active-class="q-item-no-link-highlighting"
                  :key="`content${idx}`"
                  :to="{ name: content.routerName }"
                >
                  <q-item-section>
                    <q-item-label>{{ content.name }}</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-expansion-item>
          </template>
          <!-- 一般 -->
          <q-list v-if="group.routerName">
            <q-item
              active-class="q-item-no-link-highlighting"
              :key="`content${idx}`"
              :to="{ name: group.routerName }"
            >
              <q-item-section avatar>
                <q-icon :name="group.icon" />
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ group.name }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </div>
      </div>
    </q-list>
  </q-drawer>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'Navbar',
  data() {
    return {
      isOpen: true,
    };
  },
  computed: {
    ...mapGetters(['menus']),
    navbar() {
      return this.menus.filter((m) => m.hasItem);
    },
  },
  methods: {
    toggle() {
      this.isOpen = !this.isOpen;
    },
  },
};
</script>
<style lang="scss" scope>
@import '@/styles/quasar.scss';
.q-drawer__content {
  padding-top: 1rem;
  padding-bottom: 1rem;
  .logo {
    text-align: center;
    img {
      width: 60%;
    }
  }
  .q-item__label--header {
    padding-left: 1.25rem;
    padding-right: 1.25rem;
  }
  .q-expansion-item {
    font-size: 1rem;
    &__content {
      .q-item__label {
        padding-left: 2.3rem;
        font-size: 0.9rem;
      }
    }
  }
  .q-item {
    padding-left: 1.5rem;
    padding-right: 1.5rem;
    font-size: 0.9rem;
    .q-item__section--main ~ .q-item__section--side {
      padding-left: 0;
    }
    .q-item__section--avatar {
      padding-left: 0.2rem;
      padding-right: 0.5rem;
      min-width: 0;
    }
  }
  .q-router-link--exact-active {
    color: $secondary;
  }
}
</style>
