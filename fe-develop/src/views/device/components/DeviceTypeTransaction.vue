<template>
  <q-dialog v-model="showDialog" no-backdrop-dismiss>
    <div class="q-px-lg q-pb-lg q-pt-sm shadow-1 bg-grey-2" style="max-width: 800px; width: 100%">
      <div class="row justify-between items-center q-mb-md">
        <h1 class="text-h6">{{ id ? '異動' : '新增' }}器械類型</h1>
        <q-btn flat round icon="close" v-close-popup />
      </div>
      <div class="bg-white q-pa-md">
        <p class="text-weight-bold q-mb-lg">新增/編輯器械種類</p>
        <v-form @submit="beforeSubmit" id="form" class="bg-white q-mb-md " ref="form">
          <div class="row q-col-gutter-sm">
            <div v-for="item in form" :key="item.key" :class="item.style">
              <v-field
                :name="item.label"
                :rules="item.rule"
                v-model="item.value"
                v-slot="{ errorMessage, value, field }"
              >
                <label class="text-subtitle2">
                  {{ item.label }}
                </label>
                <q-input
                  :dense="item.type === 'text'"
                  outlined
                  v-bind="field"
                  :type="item.type"
                  :model-value="value"
                  :error-message="errorMessage"
                  :error="!!errorMessage"
                  rows="3"
                />
              </v-field>
            </div>
          </div>

          <div class="row">
            <div
              class="typeImages"
              :class="{ selected: mainImgId === image.id }"
              v-for="image in images"
              :key="image.id"
            >
              <img :src="image.path" alt="thumbnails" @click="mainImgId = image.id" />
              <div class="typeImages__wrapIcon">
                <p v-if="image.isDel" class="typeImages__icon" @click="deleteUploadImage(image.id)">
                  <q-icon name="delete" />
                </p>
                <p
                  class="typeImages__icon"
                  @click="toggleUploadImage({ isDel: !image.isDel, id: image.id })"
                >
                  <q-icon :name="image.isDel ? 'keyboard_return' : 'close'" />
                </p>
              </div>
            </div>
            <VImagesUpload
              @uploadSuccess="uploadSuccess"
              :disabled="images.length >= limitImagesLength"
            />
          </div>
          <!-- <p class="text-subtitle2 q-mt-sm">單隻器械類型圖片上限為{{ limitImagesLength }}張</p> -->
          <div class="text-right">
            <q-btn type="submit" color="blue" dense class="q-px-md">
              確定新增
            </q-btn>
          </div>
        </v-form>
      </div>
    </div>
  </q-dialog>
</template>

<script>
import { apiAddDeviceType, apiEditDeviceType, apiFileDelete } from '@/api';
import VImagesUpload from '@/components/VImagesUpload.vue';

export default {
  components: {
    VImagesUpload,
  },
  props: {
    detail: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      showDialog: false,
      id: '',
      mainImgId: '',
      limitImagesLength: 10,
      form: [
        {
          key: 'name',
          label: '器械名稱(中文)',
          value: '',
          rule: { required: true, max: 50 },
          style: 'col-6',
          type: 'text',
        },
        {
          key: 'nameScientific',
          label: '器械名稱(英文)',
          value: '',
          rule: { required: true, max: 100, regex: /^[A-Za-z0-9- "/]+$/ },
          style: 'col-6',
          type: 'text',
        },
        {
          key: 'spec',
          label: '規格',
          rule: { required: true, max: 150 },
          value: '',
          style: 'col-12',
          type: 'textarea',
        },
        {
          key: 'desc',
          label: '說明',
          rule: { max: 300 },
          style: 'col-12',
          type: 'textarea',
        },
      ],
      images: [],
      keepDeleteFileEditMode: [],
    };
  },
  computed: {
    finalImages() {
      return this.images.reduce((acc, current) => {
        if (current.isDel) {
          return acc;
        }
        return [
          ...acc,
          {
            id: current.id,
            isMain: current.id === this.mainImgId,
          },
        ];
      }, []);
    },
  },
  methods: {
    uploadSuccess(res) {
      this.images.push({
        id: res.id,
        path: this.$utilsFilters.imagePath(res.path),
        isDel: false,
      });
      if (!this.mainImgId) {
        this.mainImgId = res.id;
      }
    },
    async beforeSubmit() {
      const hasMainImage = this.finalImages.findIndex((item) => item.isMain);
      if (this.finalImages.length === 0) {
        this.$q.notify({
          type: 'negative',
          message: '請上傳一張照片，並選擇主圖',
        });
      } else if (hasMainImage === -1) {
        this.$q.notify({
          type: 'negative',
          message: '請選擇主圖',
        });
      } else {
        const isDelImgIds = this.images.filter((img) => img.isDel).map((item) => item.id);
        if (this.id) {
          await this.deleteFile({ fileIds: isDelImgIds, checkMapping: true });
          await this.deleteFile({ fileIds: this.keepDeleteFileEditMode, checkMapping: true });
        } else {
          await this.deleteFile({ fileIds: isDelImgIds, checkMapping: false });
        }
        this.images = this.images.filter((item) => !item.isDel);
        this.handleSubmit();
      }
    },
    async deleteFile({ fileIds, checkMapping }) {
      try {
        await apiFileDelete({ fileIds, checkMapping });
      } catch (error) {
        const msg = error.data.msg || '系統發生異常';
        this.$gNotifyError(msg);
      }
      return { message: 'done' };
    },
    async handleSubmit() {
      try {
        this.$gLoading(true);
        // 整理輸入框資料
        const form = this.form.reduce(
          (acc, current) => ({ ...acc, [current.key]: current.value }),
          {},
        );
        const data = {
          ...form,
          id: this.id,
          images: this.finalImages,
        };
        if (this.id) {
          await apiEditDeviceType(data);
        } else {
          await apiAddDeviceType(data);
        }
        this.$q.notify({
          type: 'success',
          message: `器械${this.id ? '編輯' : '新增'}成功!`,
        });
        this.$refs.form.resetForm();
        this.showDialog = false;
        this.$emit('modifySuccess');
      } catch (error) {
        this.$q.notify({
          type: 'negative',
          message: error.data,
        });
      } finally {
        this.$gLoading(false);
      }
    },
    toggleUploadImage({ isDel = false, id }) {
      const idx = this.images.findIndex((image) => image.id === id);
      this.images[idx].isDel = isDel;
      this.checkMainImage({ isDel, checkId: id });
    },
    deleteUploadImage(id) {
      const idx = this.images.findIndex((image) => image.id === id);
      this.images.splice(idx, 1);
      this.checkMainImage({ isDel: true, checkId: id });
      if (!this.id) {
        this.deleteFile({ fileIds: [id], checkMapping: Boolean(this.id) });
      } else {
        this.keepDeleteFileEditMode.push(id);
      }
    },
    checkMainImage({ isDel, checkId }) {
      if (!isDel) {
        if (!this.mainImgId) {
          const idx = this.images.findIndex((img) => img.id === checkId);
          this.images[idx].isMain = true;
          this.mainImgId = this.images[idx].id;
        }
      } else if (isDel && this.mainImgId === checkId) {
        if (this.finalImages.length > 0) {
          const { id } = this.finalImages[0];
          const idx = this.images.findIndex((img) => img.id === id);
          this.images[idx].isMain = true;
          this.mainImgId = this.images[idx].id;
        } else {
          this.mainImgId = '';
        }
      }
    },
    editInit() {
      this.form.forEach((item, idx) => {
        this.form[idx].value = this.detail[item.key];
      });
      this.mainImgId = this.detail.images?.find((item) => item.isMain)?.id;
      this.images = this.detail.images.map((item) => ({ ...item, isDel: false }));
      this.id = this.detail.id;
    },
    $refEditType() {
      this.showDialog = true;
      this.editInit();
    },
    $refCreateType() {
      this.showDialog = true;
      this.$refs.form.resetForm();
      this.mainImgId = '';
      this.images = [];
    },
  },
  watch: {
    detail() {
      this.editInit();
    },
  },
};
</script>
