<script setup>
import { computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import RecordMap from './RecordMap.vue';

const store = useStore();

const BASE_URI = window.baseURI;

const memberId = computed(() => {
            return store.getters['auth/memberInfo'].memberId;
        })

const unacceptedRecordList = computed(() => {
            return store.getters['record/unacceptedRecordList'];
})

function getUnacceptedRecordList() {
  store.dispatch("record/getUnacceptedRecordList", memberId.value)
  .catch((error) => {
        console.error('조각 리스트 조회 실패');
        console.error(error);
  })
}

function acceptRecord(record) {
  store.dispatch("record/updateUnaccepted", { recordId: record.recordId, recordShareTo: record.recordShareTo })
  .then(() => {
    console.log("성공");
    alert("교환일기가 수락되었습니다.");
    getUnacceptedRecordList();
  })
}

// 맨위로
function scrollToTop() {
  window.scrollTo({
    top: 10,
    behavior: "smooth"
  });
}

onMounted(() => {
  getUnacceptedRecordList();
})

</script>

<template>
  <v-overlay
    v-model="isLoading"
    scroll-strategy="block"
    persistent
  >
    <v-progress-circular
      color="primary"
      indeterminate
      size="64"
    ></v-progress-circular>
  </v-overlay>

  <h1>교환일기 |&nbsp;&nbsp;&nbsp;({{ memberId }})</h1>
  <div
      v-if="unacceptedRecordList.length === 0" 
      style="display: flex; justify-content: center;">
    <v-chip
      variant="outlined"
      color="primary"
    >
      미수락 교환일기가 없습니다.
    </v-chip>
    </div>
  <v-container>
    <v-row>
      <v-col
        v-for="record in unacceptedRecordList"
        :key="record.recordId"
        :id="record.recordId"
        cols="12"
        xs="12"
      >
          <v-card class="card">
            <v-row>
                <v-col cols="5">
                  <v-card-text>{{ record.memberId }}에게서 온 교환편지</v-card-text>
                </v-col>
                <v-col cols="6">
                  <v-card-text>{{ record.recordCreatedAt }}</v-card-text>
                </v-col>
                <v-col cols="1">
                  <v-card-text v-if="record.recordIsOpen">공개</v-card-text>
                  <v-card-text v-if="!record.recordIsOpen">비공개</v-card-text>
                </v-col>
            </v-row>
          <v-card v-if="record.mediaTypeId || record.recordLocationX !==0" class="card" :style="{ borderColor: record.mainColor, borderWidth: '2px', display: 'flex', justifyContent: 'center' }">
            <video v-if="record.mediaTypeId==='video'" :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" autoplay playsinline controls class="media"></video>
            <img v-if="record.mediaTypeId==='image'" :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" class="media">
            <audio v-if="record.mediaTypeId==='audio'" controls :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" class="media"></audio>
            <record-map v-if="record.recordLocationX !== 0" :recordLocationX="record.recordLocationX" :recordLocationY="record.recordLocationY" class="media map-container"></record-map>
          </v-card>
            <v-card-text style="display: flex; justify-content: center;">{{ record.recordComment }}</v-card-text>
          <v-col class="d-flex justify-end">
            <v-btn
            class= "button"
            @click="acceptRecord(record)">수락</v-btn>
          </v-col>
        </v-card>
      </v-col>
    </v-row>
    
  </v-container>
    <v-btn
    @click="scrollToTop"
    style="position: fixed; bottom: 80px; right: 20px;"
  >
    <v-icon>mdi-chevron-up</v-icon>
  </v-btn>
</template>

<style scoped>

.msg {
  text-align: center;
}

.card {
  margin: 20px;
}

.media {
  margin: 20px;
}

.button {
  margin: 8px;
}

.map-container {
  height: 400px;
  width: 400px;
}

.v-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>