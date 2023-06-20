<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';


const store = useStore();

const router = useRouter();

const memberId = computed(() => {
            return store.getters['auth/memberInfo'].memberId;
        })

const friendId = computed(() => {
            return store.getters['record/recordOption'].friendId;
})

const diaryYear = computed(() => {
            return store.getters['diary/diaryOption'].diaryYear;
})

const diaryMonth = computed(() => {
            return store.getters['diary/diaryOption'].diaryMonth;
})

const diaryList = computed(() => {
            return store.getters['diary/diaryList'];
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

function setRecordCount(){
    // diaryRecordList의 각 객체를 순회하면서 date 배열을 만들어 attributes에 추가
  for (const record of diaryList.value.diaryRecordList) {
    const date = new Date(diaryYear.value, diaryMonth.value - 1, record.recordDay);
    const dots = [];
    for (let i = 0; i < record.recordCount; i++) {
      dots.push({ dot: { style : { backgroundColor : mainColor.value} }, dates: [date] });
    }
    attributes.value = attributes.value.concat(dots);
  }
}



const selectedYear = ref(diaryYear.value);
const selectedMonth = ref(diaryMonth.value);
const years = [new Date().getFullYear(), new Date().getFullYear()-1, new Date().getFullYear()-2];
const months = [12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1];

watch(selectedYear, () => {
    setDiaryYear(selectedYear.value);
    setDiaryMonth(null);
    router.push('/diary');
})

watch(selectedMonth, async () => {
    selectMsg.value = false;
    setDiaryMonth(selectedMonth.value);
    await getDiaryList();
    if(diaryList.value !== undefined){
      diaryVal.value = true;
      foundKeyword = diaryList.value.diaryKeywordList.find(
      keyword => keyword.diaryKeyword === diaryList.value.mainKeyword
  );
      setRecordCount();
    }
})

// 캘린더

const calendar = ref(null);


// attributes 변수를 빈 배열로 초기화
const attributes = ref([]);

// mainColor 값 가져오기
const mainColor = computed(() => diaryList.value.mainColor);

// 날짜 클릭하면 해당 날짜기준 조각 조회
function handleDateClick({date}) {
  const clickedDate = new Date(date)
  const dateStr = formatDate(clickedDate);
  store.commit('record/setStartDateStr', dateStr);
  router.push('/record');
}

function formatDate(date) {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
}



function goToColor(){
  router.push('/color'); 
}

let foundKeyword = null;


// 키워드를 빈도수에 따라 다른 크기로 지정.
function getChipStyle(freq) {
      let fontSize = 10+20*(freq/foundKeyword.diaryFreq);
      return {
        fontSize: `${fontSize}px`
      };
}

// // v-chip 사이즈 설정
// function getChipSize(freq) {
//       if (freq === foundKeyword.diaryFreq) {
//         return "x-large"; // 가장 큰 크기로 설정
//       } else if (freq >= foundKeyword.diaryFreq / 2) {
//         return "large"; // 중간 크기로 설정
//       } else {
//         return "small"; // 작은 크기로 설정
//       }
// }

const selectMsg = ref(false);
const diaryVal = ref(false);

async function onMountedHandler() {
  if(diaryMonth.value === null){
    selectMsg.value = true;
  } else {
    await getDiaryList();
    if(diaryList.value !== undefined){
      diaryVal.value = true;
      foundKeyword = diaryList.value.diaryKeywordList.find(
      keyword => keyword.diaryKeyword === diaryList.value.mainKeyword
      );
      setRecordCount();
      console.log(diaryList.value.diaryKeywordList);
    }
    selectMsg.value = false;
  }
}

onMounted(onMountedHandler)

</script>

<template>
    <v-row>
      <v-col cols="6">
        <v-select
          class="my-select"
          v-model="selectedYear"
          :items="years"
          label="Year"
        ></v-select>
      </v-col>
      <v-col cols="6">
        <v-select
          class="my-select"
          v-model="selectedMonth"
          :items="months"
          label="Month"
        ></v-select>
      </v-col>
    </v-row>
    <div
      v-if="selectMsg" 
      style="display: flex; justify-content: center;">
    <v-chip
      variant="outlined"
      color="primary"
    >
      월을 선택해주세요
    </v-chip>
    </div>
    <div
      v-if="diaryList === undefined" 
      style="display: flex; justify-content: center;">
    <v-chip
      variant="outlined"
      color="primary"
    >
      해당 월의 다이어리가 없습니다.
    </v-chip>
    </div>
  <v-container v-if="diaryVal">
  <v-row>
    <v-col class="text-center">
        <span 
          :style="{ color : mainColor }"
          class="text-h3">
            {{ diaryList.mainKeyword }}
          </span>
    </v-col>
  </v-row>
    <v-row>
      <VCalendar expanded
        ref="calendar"
        :attributes="attributes"
        @dayclick="handleDateClick"
        :style="{ borderColor: mainColor, borderRadius: '20px' }"
        class="calendar"
      />
    </v-row>
    <v-row>
      <v-col>
      <v-chip-group class="center-align">
        <v-chip
          v-for="keyword in diaryList.diaryKeywordList"
          :key="keyword.diaryKeywordId"
          :id="keyword.diaryKeywordId"
          cols="1"
          variant="text"
          :style="{
            ...getChipStyle(keyword.diaryFreq),
            borderColor : mainColor}"
        >
          {{ keyword.diaryKeyword }}
        </v-chip>
      </v-chip-group>
      </v-col> 
    </v-row>
  </v-container>
  <v-icon
    v-if="diaryVal && !friendId"
    :style="{ color: mainColor }"
    @click="goToColor"
  >
    mdi-palette
  </v-icon>
</template>

<style scoped>
.center-align {
  display: flex;
  justify-content: center;
  flex-wrap: wrap; /* 만약 chips가 너비를 초과하면 여러 줄로 나누기 위해 flex-wrap을 설정합니다 */
}
</style>