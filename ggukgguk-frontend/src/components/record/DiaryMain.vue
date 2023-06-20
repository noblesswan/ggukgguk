<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useStore } from 'vuex';
import { useRouter} from 'vue-router';

const store = useStore();

const memberId = computed(() => {
            return store.getters['auth/memberInfo'].memberId;
        })

const friendId = computed(() => {
            return store.getters['record/recordOption'].friendId;
})

const diaryYear = computed(() => {
            return store.getters['diary/diaryOption'].diaryYear;
})

const diaryList = computed(() => {
    return store.getters['diary/diaryList']
})

function setDiaryYear(year) {
    if(year == null) {
        year = new Date().getFullYear();
    } 
    store.commit('diary/setDiaryYear', year);
}

function setDiaryMonth(month){
    store.commit('diary/setDiaryMonth', month);
}

async function getDiaryList() {
    if(friendId.value !== null && friendId.value !== undefined){
      await store.dispatch("diary/getDiaryList", friendId.value)
     .catch((error) => {
        console.error('다이어리 리스트 조회 실패');
        console.error(error);
      })
    } else {
      await store.dispatch("diary/getDiaryList", memberId.value)
      .catch((error) => {
          console.error('다이어리 리스트 조회 실패');
          console.error(error);
      })
    }   
}



const router = useRouter();


const selectedYear = ref(diaryYear);
const selectedMonth = ref(null);
const years = [new Date().getFullYear(), new Date().getFullYear()-1, new Date().getFullYear()-2];
const months = [12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1];

watch(selectedYear, () => {
    setDiaryYear(selectedYear.value);
    setDiaryMonth(null);
    getDiaryList();
})

watch(selectedMonth, async () => {
    setDiaryMonth(selectedMonth.value);
    await getDiaryList();
    router.push('/calendar');
})

function clickDiary(diary) {
    selectedMonth.value = diary.diaryMonth;
}

onMounted(() => {
  setDiaryYear(selectedYear.value)
  setDiaryMonth(null)
  getDiaryList();
})

</script>

<template>
    <v-row>
      <v-col cols="6">
        <v-select
          v-model="selectedYear"
          :items="years"
          label="Year"
        ></v-select>
      </v-col>
      <v-col cols="6">
        <v-select
          v-model="selectedMonth"
          :items="months"
          label="Month"
        ></v-select>
      </v-col>
    </v-row>
    <div
      v-if="diaryList.length === 0" 
      style="display: flex; justify-content: center;">
    <v-chip
      variant="outlined"
      color="primary"
    >
      해당 연도의 다이어리가 없습니다.
    </v-chip>
    </div>
    <v-container v-if="diaryList !== undefined" align="center" class="mt-15">
    <v-row 
      v-for="diary in diaryList"
      :key="diary.diaryId"
      align="center" justify="center"
    >
      <v-col
        cols="12"
        class="d-flex justify-center"
      >
        <v-card
          :width="300"
          :title="diary.diaryMonth"
          :style="{ borderColor: diary.mainColor, borderWidth: '1px', backgroundColor: diary.mainColor }"
          v-on:click="clickDiary(diary)"
        >
          <v-card-text
            :style="{ backgroundColor: 'white', color: diary.mainColor, padding: '10px 10px 10px 10px' }">{{ diary.mainKeyword }}</v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>