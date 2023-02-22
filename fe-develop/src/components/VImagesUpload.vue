<template>
  <div class="uploadImg">
    <q-file
      dense
      borderless
      v-model="files"
      accept=".jpg,.jpeg,.png"
      :disable="disabled"
      :max-file-size="filesMaxSize"
      @update:model-value="fileUpload"
      @rejected="onRejected"
    >
    </q-file>
  </div>
</template>

<script>
import { apiUpload } from '@/api';

export default {
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['uploadSuccess'],
  data() {
    return {
      files: [],
      filesMaxSize: '2097152',
    };
  },
  methods: {
    async fileUpload(file) {
      const formData = new FormData();
      formData.append('file', file);
      const res = await apiUpload(formData);
      const { fileName, fileId } = res.data;
      this.files = [];
      this.$emit('uploadSuccess', { path: `/images/${fileName}`, id: fileId });
    },
    onRejected(rejectedEntries) {
      const validationTypes = [{ name: 'max-file-size', msg: '請上傳圖片小於2MB' }];
      rejectedEntries.forEach((item) => {
        const rejectType = validationTypes.find((type) => type.name === item.failedPropValidation);
        const msg = rejectType ? rejectType.msg : '上傳發生錯誤';
        this.$q.notify({
          type: 'negative',
          message: msg,
        });
      });
      this.files = [];
    },
  },
};
</script>
<style lang="scss">
.uploadImg {
  width: 146px;
  height: 110px;
  border-radius: 5px;
  border: dashed 3px #25a69a;
  background-size: 70%;
  background-repeat: no-repeat;
  background-position: center;
  z-index: 1;
  background-image: url('https://i.imgur.com/yKAdUFy.png');
  .q-field__control-container {
    height: 110px;
  }
}
</style>
