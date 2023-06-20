<script setup>
import { computed, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';

const store = useStore();

const router = useRouter();

const memberId = computed(() => {
  return store.getters['auth/memberInfo'].memberId;
})

const isLoading = ref(false);

const dialog = ref(false);

const keyword = ref(null);

const calAttrs = ref([
  {
    key: 'today',
    highlight: true,
    dates: new Date(),
  },
]);

function setStartDateStr() {
  store.commit('record/setStartDateStr', null);
}

function setKeyword(keyword) {
  store.commit('record/setKeyword', keyword);
}

function handleSearch() {
  setStartDateStr();
  setKeyword(keyword.value);
  store.dispatch('record/getRecordList', memberId.value);
  keyword.value = null;
  window.scrollTo({
    top: 10,
    behavior: "smooth"
  });
}

const route = useRoute();

// 날짜 클릭하면 해당 날짜기준 조각 조회
function handleDateClick({ date }) {
  if (date > new Date()) {
    alert('미래는 조회할 수 없습니다.');
    return;
  }

  dialog.value = false;
  setKeyword(null);
  const clickedDate = new Date(date)
  const dateStr = formatDate(clickedDate);
  store.commit('record/setStartDateStr', dateStr);
  route.name === "recordMain" ? getRecordList() : router.push('/record');
}

function formatDate(date) {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
}

function getRecordList() {
  isLoading.value = true;

  store.dispatch("record/getRecordList", memberId.value)
    .then(() => {
      isLoading.value = false;
    })
    .catch((error) => {
      console.error('조각 리스트 조회 실패');
      console.error(error);
    });
}

function goToRecord() {
  router.push('/record');
}

function goToDiary() {
  router.push('/diary');
}

function goToCalendar() {
  router.push('/calendar');
}

function goToExchangeRecord() {
  router.push('/record/unaccepted');
}

</script>

<template>
  <v-overlay class="loading-overlay" v-model="isLoading" scroll-strategy="block" persistent>
    <v-progress-circular color="primary" indeterminate size="64"></v-progress-circular>
  </v-overlay>
  <v-app-bar class="text-center align-content-center w-full" :elevation="0" color="#E0E3DA">
    <template v-slot:prepend>
      <v-dialog v-model="dialog" max-width="290" @click:outside="dialog = false">
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props">
            <v-icon>mdi-calendar</v-icon>
          </v-btn>
        </template>
        <VCalendar @dayclick="handleDateClick" :attributes="calAttrs" />
      </v-dialog>
    </template>
    <input v-model="keyword"
      class="v-input v-text-field__input v-text-field__input--enclosed v-text-field__input--placeholder v-text-field__input--solo w-full"
      type="text" placeholder="검색" @keydown.enter="handleSearch" style="min-width: 150px; color:#111111;" />
    <v-btn icon @click="handleSearch">
      <v-icon>mdi-magnify</v-icon>
    </v-btn>
    <template v-slot:append>
      <v-menu left>
        <template v-slot:activator="{ props }">
          <v-btn icon v-bind="props">
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item value="records">
            <v-list-item-title @click="goToRecord">
              조각
            </v-list-item-title>
          </v-list-item>
          <v-list-item value="diarys">
            <v-list-item-title @click="goToDiary">
              다이어리
            </v-list-item-title>
          </v-list-item>
          <v-list-item value="calendar">
            <v-list-item-title @click="goToCalendar">
              캘린더
            </v-list-item-title>
          </v-list-item>
          <v-list-item value="exchange">
            <v-list-item-title @click="goToExchangeRecord">
              요청된 교환일기
            </v-list-item-title>
          </v-list-item>
        </v-list>
    </v-menu>
    </template>
  </v-app-bar>
</template>

<style scoped>
.loading-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
}

input::placeholder {
  color: #111111;
}

.v-app-bar {
  border-bottom-style: double;
  border-bottom-width: 3px;
  border-bottom-color: #111111;
}
</style>