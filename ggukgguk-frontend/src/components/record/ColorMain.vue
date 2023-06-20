<script setup>
import { computed, onMounted, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

const store = useStore();
const router = useRouter();


const mainColor = computed(() => {
    return store.getters['diary/diaryList'].mainColor;
})

const colorList = computed(() => {
            return store.getters['diary/colorList'];
})

const selectedColor = ref(mainColor.value);

async function getColorList() {
    await store.dispatch("diary/getColorList")
    .catch((error) => {
        console.error('컬러 리스트 조회 실패');
        console.error(error);
    })
}

async function updateMainColor() {
    await store.dispatch("diary/updateMainColor", selectedColor.value);
    router.push("/calendar");
}

onMounted(() => {
  getColorList();
})

</script>

<template>
  <v-container align="center" class="mt-15">
    {{ selectedColor }}
    <v-select
      v-model="selectedColor"
      :items="colorList"
      label="Color"
    ></v-select>
    <v-card
      :style="{ backgroundColor: selectedColor, marginBottom: '20px' }"
      class="py-4"
    >
        <v-color-picker
        v-model="selectedColor"
        />
    </v-card>
    <v-btn
      :style="{ border: '2px solid ' + selectedColor, color: selectedColor }"
      class= "button"
      @click="updateMainColor()">메인색상 등록</v-btn>
  </v-container>

</template>

<style scoped>

</style>