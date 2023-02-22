<template>
  <VBreadCrumb backTitle="器械類型列表" />
  <div class="bg-white q-pa-lg shadow-1 q-mb-md">
    <div class="row items-center">
      <div
        v-if="deviceType.images.length > 0"
        @click="showLightBoxImages(mainImages.id)"
        class="q-mr-md"
      >
        <q-img
          spinner-color="white"
          class="q-pr-lg cursor-pointer"
          :src="mainImages.path"
          alt="主要圖片"
          width="360px"
          fit="contain"
          :ratio="1 / 1"
        />
        <q-btn icon="image_search" class="absolute-bottom-right" round color="secondary" />
      </div>
      <div class="q-pl-lg col-grow">
        <div class="row items-center">
          <h3 class="text-h5 text-weight-bold">{{ deviceType.nameScientific }}</h3>
          <q-btn @click.prevent="$refs.typeDiaglog.$refEditType()" flat style="z-index:2">
            <q-icon name="edit" size="20px" />
          </q-btn>
        </div>
        <p class="text-grey q-mb-md text-weight-bold">{{ deviceType.name }}</p>
        <ul>
          <li class="list">
            <p class="list__title">規格</p>
            <p class="list__text">{{ deviceType.spec || '-' }}</p>
          </li>
          <li class="list">
            <p class="list__title">說明</p>
            <p class="list__text">{{ deviceType.desc || '-' }}</p>
          </li>
          <li class="list">
            <p class="list__title q-mb-sm">看更多</p>
            <div class="scroll text-no-wrap">
              <q-img
                v-for="img in deviceType.images"
                :key="img.id"
                spinner-color="white"
                class="q-mr-lg rounded-borders cursor-pointer"
                :src="`${img.path}`"
                alt="小圖"
                style="height: 80px; max-width: 80px"
                fit="cover"
                @click="imagesSelectedId = img.id"
              />
            </div>
          </li>
        </ul>
      </div>
    </div>
    <div class="absolute-top text-right q-pa-lg">
      <q-btn
        color="info"
        icon="add_circle_outline"
        label="新增器械"
        class="q-mb-sm"
        @click="$refs.deviceDialog.$refInit()"
      />
    </div>
  </div>
  <div class="shadow-1 q-pa-md bg-white">
    <q-select
      outlined
      bg-color="grey-8"
      color="white"
      dark
      v-model="queryParams.divisionId"
      option-label="name"
      option-value="id"
      :options="$store.getters.divisionsWithAll"
      dense
      options-dense
      emit-value
      map-options
      class="q-mr-md q-mb-sm"
      style="width: 150px"
    >
      <template v-slot:prepend>
        <q-icon name="img:/icons/division.png" />
      </template>
    </q-select>

    <VTable
      rowKey="modelId"
      :columns="columns"
      :rows="values"
      :loading="isLoading"
      :hasPage="false"
    />
  </div>
  <DeviceItemDialog :typeId="queryParams.typeId" ref="deviceDialog" @addItemQty="init" />
  <DeviceTypeDialog
    ref="typeDiaglog"
    :typeId="queryParams.typeId"
    :detail="deviceType"
    @modifySuccess="init"
  />

  <VueEasyLightbox
    scrollDisabled
    escDisabled
    moveDisabled
    loop
    :visible="lightBoxVisible"
    :imgs="lightBoxImages"
    :index="lightBoxImgIndex"
    @hide="lightBoxVisible = false"
  >
    <template v-slot:prev-btn="{ prev }">
      <q-btn
        icon="navigate_next"
        class="absolute-bottom-right"
        round
        color="secondary"
        @click="prev"
        style="top: 50vh"
      />
    </template>

    <template v-slot:next-btn="{ next }">
      <q-btn
        icon="navigate_before"
        class="absolute-bottom-left"
        round
        color="secondary"
        @click="next"
        style="top: 50vh"
      />
    </template>
  </VueEasyLightbox>
</template>

<script>
import emitter from '@/common/emitter';
import { apiGetDeviceType, apiQueryDeviceBrands } from '@/api';
import DeviceItemDialog from './components/DeviceItemTransaction.vue';
import DeviceTypeDialog from './components/DeviceTypeTransaction.vue';
import VBreadCrumb from '@/components/VBreadCrumb.vue';

export default {
  name: 'DeviceTypesDetail',
  components: {
    DeviceTypeDialog,
    VBreadCrumb,
    DeviceItemDialog,
  },
  data() {
    return {
      imagesSelectedId: '', // 主圖現在顯示
      lightBoxVisible: false,
      lightBoxImgIndex: 0, // default: 0,
      deviceType: {
        id: '',
        name: '',
        manufactureId: '',
        nameScientific: '',
        brand: '',
        division: [],
        images: [],
      },
      queryParams: {
        info: true,
        divisionId: 'all',
        typeId: '',
      },
      columns: [
        {
          align: 'left',
          name: 'brand',
          label: '廠牌',
          field: 'brand',
        },
        { align: 'left', name: 'manufactureId', label: '型號', field: 'manufactureId' },
        {
          headerStyle: 'width: 15%',
          align: 'left',
          name: 'division',
          label: '科別',
          field: (val) => this.$store.getters.divisionById(val.division)?.name,
        },
        { headerStyle: 'width: 15%', name: 'totalQty', label: '總支數', field: 'totalQty' },
        { headerStyle: 'width: 15%', name: 'repairQty', label: '維修數', field: 'repairQty' },
        { headerStyle: 'width: 15%', name: 'unusedQty', label: '全新', field: 'unusedQty' },
        { headerStyle: 'width: 15%', name: 'usedQty', label: '已使用數', field: 'usedQty' },
        { headerStyle: 'width: 15%', name: 'scrappedQty', label: '報廢數', field: 'scrappedQty' },
      ],
      values: [],
      isLoading: false,
      queryDeviceQty: {},
    };
  },
  computed: {
    lightBoxImages() {
      return this.deviceType.images.map((img) => ({
        src: img.path,
        id: img.id,
      }));
    },
    mainImages() {
      return this.deviceType.images?.find((item) => item.id === this.imagesSelectedId);
    },
  },
  methods: {
    async queryTypeDetail(typeId) {
      try {
        const res = await apiGetDeviceType(typeId);
        const { id, name, desc, spec, nameScientific, images, division } = res.data;
        const totalQty = division.reduce((acc, item) => acc + item.qty, 0);
        const modfiyDivision = [{ id: 'all', name: '全部', qty: totalQty }, ...division];
        // 圖片順序調整
        images.sort((a, b) => a.isMain - b.isMain);
        this.imagesSelectedId = images.find((item) => item.isMain)?.id;
        const modifyImages = images.map((item) => ({
          ...item,
          path: this.$utilsFilters.imagePath(item.path),
        }));
        this.deviceType = {
          id,
          name,
          desc,
          spec,
          nameScientific,
          images: modifyImages,
          division: modfiyDivision,
        };
      } catch (error) {
        this.$gNotifyError('系統發生異常');
        this.$router.push('/devices');
      }
    },
    async queryBrandDevices() {
      try {
        const { typeId, divisionId, info } = this.queryParams;
        const params = divisionId === 'all' ? { info } : { divisionId, info };
        const res = await apiQueryDeviceBrands(typeId, params);
        const brands = res.data;
        this.values = brands;
      } catch (error) {
        const msg = error.data.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
    },
    async init() {
      this.$gLoading(true);
      try {
        const { typeId } = this.queryParams;
        if (typeId) {
          await this.queryTypeDetail(typeId);
          await this.queryBrandDevices(this.queryParams);
        } else {
          this.$router.push('devices');
        }
      } catch (e) {
        this.$gLoading(false);
      } finally {
        this.$gLoading(false);
      }
    },
    showLightBoxImages(id) {
      const idx = this.deviceType.images.findIndex((img) => img.id === id);
      this.lightBoxImgIndex = idx;
      this.lightBoxVisible = true;
    },
  },
  watch: {
    'queryParams.divisionId'() {
      this.queryBrandDevices(this.queryParams);
    },
  },
  mounted() {
    this.queryParams.typeId = Number(this.$route.params.typeId);
    emitter.on('deviceDetail-updateBrands', () => this.queryBrandDevices(this.queryParams));
    this.init();
  },
  unmounted() {
    emitter.off('deviceDetail-updateBrands', () => this.queryBrandDevices(this.queryParams));
  },
};
</script>
