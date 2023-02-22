<template>
  <q-card>
    <q-tabs
      :model-value="tabName"
      class="text-grey bg-white shadow-6"
      active-color="blue"
      indicator-color="blue"
      align="justify"
      @update:model-value="changeTab"
    >
      <q-tab
        class="q-py-sm"
        v-for="(tab, idx) in temptabList"
        :name="tab.tabName"
        :label="tab.label"
        :disable="idx !== 0 && currentTabNumber !== idx + 1"
        :key="`${tab.name}${idx}`"
      >
        <p class="text-body1">{{ tab.name }}</p>
      </q-tab>
    </q-tabs>
    <q-separator />
    <section>
      <slot
        name="content"
        :handleForwardTab="handleForwardTab"
        :handleBackwordTab="handleBackwordTab"
        :currentTabNumber="currentTabNumber"
      />

      <div
        class="column justify-between items-center q-mb-lg q-py-lg"
        v-show="currentTabNumber === totalTabNumer"
        :disable="currentTabNumber !== totalTabNumer"
      >
        <q-img src="img/finish.png" width="50px" :ratio="1 / 1" class="q-mb-sm" />
        <h5 class="text-weight-bold q-mb-lg">完成</h5>
        <p class="text-grey q-mb-lg" v-show="finishWord">{{ finishWord }}</p>
        <q-btn color="blue" label="回到第一步" @click="pressFinal"></q-btn>
      </div>
    </section>
  </q-card>
</template>

<script>
export default {
  name: 'tabs',
  props: {
    finishWord: {
      type: String,
    },
    tabList: {
      type: Array,
      required: true,
    },
    showFinishTabs: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      labelTitle: ['ㄧ', '二', '三', '四', '五'],
      currentTabNumber: 1,
      totalTabNumer: 1,
      tabName: '',
    };
  },
  computed: {
    temptabList() {
      const tabs = this.tabList.map((tab, idx) => ({
        tabName: tab.name,
        name: tab.title,
        label: idx <= 4 ? `第${this.labelTitle[idx]}步` : '-',
      }));
      const fullContent = [
        ...tabs,
        {
          tabName: '$tabLast',
          name: '完成送出',
          label: `第${this.labelTitle[this.tabList.length]}步`,
        },
      ];
      return !this.showFinishTabs ? tabs : fullContent;
    },
  },
  methods: {
    handleForwardTab() {
      if (this.currentTabNumber < this.totalTabNumer) {
        this.currentTabNumber += 1;
      }
    },
    handleBackwordTab() {
      if (this.currentTabNumber > 1) {
        this.currentTabNumber -= 1;
      }
    },
    pressFinal() {
      this.currentTabNumber = 1;
      this.$emit('pressFinal');
    },
    changeTab(val) {
      this.tabName = val;
      this.currentTabNumber = this.temptabList.findIndex((i) => i.tabName === this.tabName) + 1;
    },
    init() {
      this.currentTabNumber = 1;
      this.totalTabNumer = this.temptabList.length;
      this.tabName = this.temptabList[this.currentTabNumber - 1].tabName;
    },
  },
  watch: {
    currentTabNumber(val) {
      this.tabName = this.temptabList[val - 1].tabName;
    },
  },
  created() {
    this.init();
  },
};
</script>
