<template>
  <q-card flat :bordered="border" class="q-pa-none q-pb-sm">
    <q-card-section class="q-pa-sm">
      <h6 class="q-pa-md">{{ content.title }}</h6>
      <q-list :dense="dense" class="text-body2">
        <template v-for="(item, idx) in content.items" :key="`${item.label}${idx}`">
          <q-item class="q-pa-xs-md q-pa-md-lg">
            <q-item-section>
              <q-item-label :class="item.labelCss">
                {{ item.label }}
              </q-item-label>
              <q-item-label class="text-grey text-caption">
                {{ item.caption }}
              </q-item-label>
            </q-item-section>
            <q-item-section>
              <q-item-label
                :class="item.valueCss || valueColor"
                class="text-right"
                style="word-break: break-all;"
                >{{ item.value }}</q-item-label
              >
            </q-item-section>
          </q-item>
          <q-separator spaced inset />
        </template>
      </q-list>
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: 'vlist',
  props: {
    content: {
      type: Object,
    },
    dense: {
      type: Boolean,
      default: false,
    },
    border: {
      type: Boolean,
      default: true,
    },
    valueColor: {
      type: String,
      default: 'text-grey-7',
    },
  },
};
</script>
