<template>
  <div class="q-pb-md">
    <VTable
      rowKey="id"
      :columns="columns"
      :rows="contents"
      :loading="isLoading"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :bodySlots="['typeInfo']"
      :hasPage="false"
      :virtualScroll="true"
      :style="contents.length > 5 ? 'height: 50vh' : ''"
    >
      <template #typeInfo="props">
        <div class="row items-center no-wrap">
          <q-img :src="imagesPath(props.row.images)" width="100px" :ratio="4 / 3" />
          <div class="q-pl-md">
            <h6 class="text-grey-9 q-mb-xs">{{ props.row.nameScientific }}</h6>
            <p>{{ props.row.name }}</p>
          </div>
        </div>
      </template>
    </VTable>
  </div>
</template>

<script>
export default {
  props: {
    contents: Array,
    default: () => [],
  },
  data() {
    return {
      isLoading: false,
      queryString: {},
      columns: [
        {
          headerStyle: 'width: 40%;',
          align: 'left',
          name: 'typeInfo',
          label: '器械類型',
          field: 'typeInfo',
        },
        { align: 'left', name: 'spec', label: '規格', field: 'spec' },
        { name: 'qty', label: '數量', field: 'qty' },
      ],
      currentPage: 1,
      totalPages: 1,
    };
  },
  methods: {
    imagesPath(imagesArr) {
      return imagesArr && imagesArr.length > 0
        ? this.$utilsFilters.imagePath(imagesArr[0].path)
        : '';
    },
  },
};
</script>
