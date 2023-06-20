<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { useDisplay } from 'vuetify';
import RecordMap from './RecordMap.vue';
import DateSeparation from './DateSeparation.vue';

const deviceWidth = useDisplay().width;

const store = useStore();

const BASE_URI = window.baseURI;

const topElId = ref(0);
const OFFSET = 65;

const memberId = computed(() => {
            return store.getters['auth/memberInfo'].memberId;
        })

const friendId = computed(() => {
            return store.getters['record/recordOption'].friendId;
})

const memberNickname = computed(() => {
            return store.getters['auth/memberInfo'].memberNickname;
})


const friendNickname = computed(() => {
  let nick;
  friendListByRecord.value.forEach((item) => {
    if (item.memberId === friendId.value)
      nick = item.memberNickname;
  });
  return nick;
});

const friendListByRecord = computed(() => {
            return store.getters['record/friendListByRecord'];
})

function setFriendId(friendId) {
    store.commit('record/setFriendId', friendId);
}

function getFriendListByRecord() {
  console.log(memberId);
  store.dispatch('record/getFriendListByRecord', memberId)
  .then(() => {
    isLoading.value = false;
  })
  .catch((error) => {
    console.error('친구 리스트를 불러오던 중 오류가 발생했습니다.');
    console.error(error);
  })
}

function clickFriend(friendId) {
    console.log("클릭");
    setFriendId(friendId);
    setStartDateStr(formatDate(new Date()));
    noMoreDown.value = false;
    noMoreUp.value = false;
    getRecordList();
}

function formatDate(date, isCardHeader) {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();

  const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'];
  const week = WEEKDAYS[date.getDay()];

  if (isCardHeader) {
    const halfHour = hour > 12 ? hour - 12 : hour;
    const hourPrefix = hour >= 12 ? '오후' : '오전';
    return `${month}월 ${day}일 ${week}요일 ${hourPrefix} ${halfHour}시 ${minute}분`;
  } else {
    return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
  }
}


const keyword = computed(() => {
  return store.getters['record/recordOption'].keyword
});

function setKeyword(keyword) {
  store.commit('record/setKeyword', keyword);
}

const recordList = computed(() => {
    return store.getters['record/recordList']
});
watch(recordList, () => {
  noMoreDown.value = false;
  noMoreUp.value = false;
})

// eslint-disable-next-line
const recordCreatedAtByDayMap = computed(() => {
  const map = {};
  recordList.value.forEach(item => {
    if (!Array.isArray(map[formatDate(new Date(item.recordCreatedAt))])) {
      map[formatDate(new Date(item.recordCreatedAt))] = [];
    }

    const currentDateList = map[formatDate(new Date(item.recordCreatedAt))];
    currentDateList.push(item);
  })
  return map;
})

const startDate = ref(new Date());

// recordList 배열에서 가장 최근 recordCreatedAt 값을 추출
const latestRecordCreatedAt = computed(() => {
  return recordList.value.reduce((latest, record) => {
    const createdAt = new Date(record.recordCreatedAt);
    return createdAt > latest ? createdAt : latest;
  }, new Date(0));
})
// recordList 배열에서 가장 과거 recordCreatedAt 값을 추출
const earliestRecordCreatedAt = computed(() => {
    return recordList.value.reduce((earliest, record) => {
      const createdAt = new Date(record.recordCreatedAt);
      return createdAt < earliest ? createdAt : earliest;
    }, new Date());
})

const isLoadingForScrollEvent = ref(false);
const isLoading = ref(false);

const noMoreUp = ref(false);
const noMoreDown = ref(false);

const noData = computed(() => {
  if (recordList.value.length === 0) return true;
  else return false;
});

function setStartDateStr(startDateStr) {
    setKeyword(null);
    store.commit('record/setStartDateStr', startDateStr);
}

let lastScrollPosition = 0;
// const scrollThreshold = 5;
const scrollThreshold = 0; // 모바일에서 아예 상단 스크롤 감지가 되지 않는 문제 때문에 0으로 수정

function handleScroll() {
  const scrollY = window.scrollY || document.documentElement.scrollTop;
  const documentHeight = document.documentElement.scrollHeight;
  const windowHeight = window.innerHeight;

  // 스크롤이 맨 아래에 도달했는지 확인
  if (Math.ceil(scrollY + windowHeight) >= documentHeight && !isLoadingForScrollEvent.value) {
    if (noMoreDown.value || keyword.value !== null) {
      return;
    }

    isLoadingForScrollEvent.value = true;
    startDate.value = earliestRecordCreatedAt.value;
    startDate.value.setDate(startDate.value.getDate() - 1); // startDate를 5일 전으로 수정
    const startDateStr = formatDate(startDate.value);
    setStartDateStr(startDateStr);
    getRecordsDown();
  }

  // 스크롤이 맨 위에 도달했는지 확인
  if (scrollY === 0 && !isLoadingForScrollEvent.value && Math.abs(scrollY - lastScrollPosition) > scrollThreshold) {
    if (noMoreUp.value || keyword.value !== null) {
      return;
    }

    isLoadingForScrollEvent.value = true;
    startDate.value = latestRecordCreatedAt.value;
    startDate.value.setDate(startDate.value.getDate() + 5); // startDate를 5일 후로 수정
    console.log(startDate.value);
    const startDateStr = formatDate(startDate.value);
    setStartDateStr(startDateStr);
    console.log(startDateStr);
    topElId.value = recordList.value[0]?.recordId;
    getRecordsUp();
  }

  lastScrollPosition = scrollY;
}

window.addEventListener('scroll', handleScroll);

function setDiaryYear(year) {
    if(year == null) {
        year = new Date().getFullYear();
    } 
    store.commit('diary/setDiaryYear', year);
}

function setDiaryMonth(month){
    store.commit('diary/setDiaryMonth', month);
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
  })
}

function getRecordsUp() {
  isLoadingForScrollEvent.value = true;
  isLoading.value = true;

  store.dispatch("record/getRecordsUp", memberId.value)
  .then((data) => {
        isLoadingForScrollEvent.value = false;
        isLoading.value = false;
        setTimeout(() => {
          document.getElementById(topElId.value)?.scrollIntoView();
          document.documentElement.scrollTop = document.documentElement.scrollTop - OFFSET;
        }, 500) // 모든 DOM의 위치가 확정된 후 스크롤 위치가 변경될 수 있도록 비동기 처리
        if (data?.length === 0) noMoreUp.value = true;
  })
  .catch((error) => {
        console.error('조각 리스트 조회 실패');
        console.error(error);
  })
}

function getRecordsDown() {
  isLoadingForScrollEvent.value = true;
  isLoading.value = true;

  store.dispatch("record/getRecordsDown", memberId.value)
  .then((data) => {
        isLoadingForScrollEvent.value = false;
        isLoading.value = false;
        if (data?.length === 0) noMoreDown.value = true;
  })
  .catch((error) => {
        console.error('조각 리스트 조회 실패');
        console.error(error);
  })
}

async function getDiaryList() {
    isLoading.value = true;

    await store.dispatch("diary/getDiaryList", memberId.value)
    .then(() => {
      isLoading.value = false;
    })
    .catch((error) => {
        console.error('다이어리 리스트 조회 실패');
        console.error(error);
    })
}

const router = useRouter();

const selectedYear = ref(new Date().getFullYear());
const selectedMonth = ref(null);

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

// 레코드 수정

function setRecord(record) {
    store.commit('record/setRecord', record);
}

function goToRecordUpdate(record) {
    setRecord(record);
    router.push('/record/update');
}

// 레코드 삭제
function openDeleteRemoveDialog(record) {

    if (confirm("삭제하시겠습니까?")) {
      deleteRecord(record.recordId, record.memberId, record.recordCreatedAt);
    }
}


function deleteRecord(recordId, memberId, recordCreatedAt) {
    console.log("삭제 실행");
    store.dispatch('record/deleteRecord', { recordId, memberId } )
    .then(() => {
    console.log("삭제 성공");
    const startDateStr = formatDate(new Date(recordCreatedAt));
    setStartDateStr(startDateStr);
    getRecordList();
    })
    .catch((error) =>
    console.log(error));
}

// 댓글

// 댓글 추가
const newReplyContent = ref({});

function addReply(recordId) {
  store.dispatch("record/addReply", { 
    memberId : memberId.value, 
    recordId : recordId, 
    replyContent : newReplyContent.value[recordId] })
  .catch((error) => {
        console.error('댓글 추가 실패');
        console.error(error);
  })
}

// 댓글 수정
const editReplyForm = ref(false);
const editReplyContent = ref('');
const editReplyId =ref(0);

function openEditReplyForm(reply) {
  editReplyForm.value = true;
  editReplyId.value = reply.replyId;
  editReplyContent.value = reply.replyContent;
}

function cancelEditReply() {
  editReplyForm.value = false;
  editReplyId.value = 0;
  editReplyContent.value = '';
}

function editReply(reply) {
  store.dispatch("record/editReply", { 
    memberId : memberId.value, 
    recordId : reply.recordId,
    replyId : reply.replyId, 
    replyContent : editReplyContent.value })
  .then(() => {
        editReplyForm.value = false;
        editReplyId.value = 0;
        editReplyContent.value = '';
  })
  .catch((error) => {
        console.error('댓글 수정 실패');
        console.error(error);
  })
}

const deleteReplyId = ref(0);

// 댓글 삭제
function openDeleteReplyDialog(reply) {
  
  deleteReplyId.value = reply.replyId;

    if (confirm("삭제하시겠습니까?")) {
      deleteReply(reply);
  } else {
      deleteReplyId.value = 0;
  }
}

function deleteReply(reply) {
  console.log(reply);
  store.dispatch("record/deleteReply", {
    recordId : reply.recordId,
    replyId : reply.replyId,
    memberId : reply.replyMemberId })
  .then(() => {
        deleteReplyId.value = 0;
  })
  .catch((error) => {
        console.error('댓글 삭제 실패');
        console.error(error);
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
  getRecordList();
  getFriendListByRecord();
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  setStartDateStr(formatDate(new Date()));
})
</script>

<template>
    <v-overlay
      v-model="isLoading"
      scroll-strategy="block"
      persistent
      class="loading-overlay"
    >
      <v-progress-circular
        color="primary"
        indeterminate
        size="64"
      ></v-progress-circular>
    </v-overlay>

  <v-container>
    <h1 v-if="!friendId">조각들 |&nbsp;&nbsp;&nbsp;나 ({{ memberNickname }})</h1>
    <h1 v-if="friendId">조각들 |&nbsp;&nbsp;&nbsp;{{ friendNickname }} ({{ friendId }})</h1>
  </v-container>

  <v-container :class="deviceWidth < 800 ? 'ma-0 pa-0' : ''">
    <v-slide-group
          show-arrows
          class = "mt-15"
        >
          <v-slide-group-item
            v-for="friend in friendListByRecord"
            :key="friend.memberId"
            v-slot="{ isSelected, toggle }"
          >
            <v-btn
              class="ma-2"
              rounded
              :color="isSelected ? 'primary' : undefined"
              @click="() => { clickFriend(friend.memberId); toggle(); }"
              variant="outlined"
              elevation="2"
            >
              {{ friend.memberNickname }}
            </v-btn>
          </v-slide-group-item>
        </v-slide-group>
    <v-container :class="deviceWidth < 800 ? 'ma-0 pa-0' : ''">
      <div class="msg" v-if="noMoreUp">
        더 이상 불러올 데이터가 없습니다.
      </div>
      <!-- <v-row> -->
        <v-col
          v-for="record in recordList"
          :key="record.recordId"
          :id="record.recordId"
          cols="12"
          xs="12"
          style="display: flex; flex-direction: column; align-items: center;"
        >
            <date-separation
              v-if="recordCreatedAtByDayMap[formatDate(new Date(record.recordCreatedAt))][0].recordId === record.recordId"
              style="width: 100%;"
              class="my-8"
              :date="formatDate(new Date(record.recordCreatedAt))"
              :color="record.mainColor" />
            <!-- 조각 카드 -->
            <v-card class="card"
              :style="{
                borderColor: record.mainColor,
                borderWidth: '1px'
              }"
              variant="outlined"
            >
              <!-- 헤더 영역 -->
              <v-row class="card-header">
                  <v-col cols="10">
                    {{ formatDate(new Date(record.recordCreatedAt), true) }}
                  </v-col>
                  <v-col cols="2" class="d-flex justify-end">
                    <v-menu location="bottom">
                      <template v-slot:activator="{ props }">
                        <v-btn
                          variant="plain"
                          v-bind="props"
                        >
                          <v-icon icon="mdi-menu-down-outline"></v-icon>
                        </v-btn>
                      </template>

                      <v-list>
                        <v-list-item
                          v-if="memberId === record.memberId && !record.recordShareTo"
                          @click="goToRecordUpdate(record)">
                          <v-list-item-title><v-icon icon="mdi-pencil-outline"></v-icon> 수정</v-list-item-title>
                        </v-list-item>
                        <v-list-item
                          v-if="!friendId"
                          @click="openDeleteRemoveDialog(record)">
                          <v-list-item-title><v-icon icon="mdi-delete-outline"></v-icon> 삭제</v-list-item-title>
                        </v-list-item>
                      </v-list>
                    </v-menu>
                  </v-col>
              </v-row>
              <v-card-text v-if="!friendId && record.memberId !== memberId" :style="{ fontStyle: 'italic' }"> {{ record.memberNickname }}(으)로부터 </v-card-text>
              <v-card-text v-if="!friendId && record.recordShareTo && record.memberId === memberId" :style="{ fontStyle: 'italic' }"> {{ record.friendNickname }}(이)에게 </v-card-text>
              <v-card-text v-if="friendId && record.memberId !== friendId" :style="{ fontStyle: 'italic' }"> {{ record.memberNickname }}(으)로부터 </v-card-text>
              <v-card-text v-if="friendId && record.recordShareTo && record.memberId === friendId" :style="{ fontStyle: 'italic' }"> {{ record.friendNickname }}(이)에게 </v-card-text>

              <!-- 본문 영역 -->
              <v-card-text class="mb-8 record-comment" style="white-space: pre-wrap">
                {{ record.recordComment }}
              </v-card-text>

              <!-- 미디어 영역 -->
              <div v-if="record.mediaTypeId || record.recordLocationX !==0" class="media-wrapper">
                <record-map v-if="record.recordLocationX !== 0" :recordLocationX="record.recordLocationX" :recordLocationY="record.recordLocationY" class="media map-container"></record-map>
                <video v-if="record.mediaTypeId==='video'" :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" playsinline controls class="media"></video>
                <v-img v-if="record.mediaTypeId==='image'" :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" class="media" />
                <audio v-if="record.mediaTypeId==='audio'" controls :src="`${BASE_URI}record/media/${record.mediaFileId}?mediaType=${record.mediaTypeId}`" class="media"></audio>
                <v-card
                  v-if="record.mediaFileBlocked"
                  class="my-4 text-center"
                  text="이 미디어는 꾹꾹 커뮤니티 가이드라인에 의해 차단되었습니다"
                  color="primary"
                  @click="() => { router.push({ name: 'mediaRecheck', params: { mediaFileId: record.mediaFileId } }) }"
                >
                  <v-tooltip
                    activator="parent"
                    location="bottom"
                  >자세히 보기</v-tooltip>
                </v-card>
              </div>

              <v-divider></v-divider>

              <!-- 댓글 영역 -->
              <!-- 댓글이 없을 때에도 replyId가 0인 값이 반환되는 버그가 있어, -->
              <!-- 우선 리스트 마지막 값의 replyId가 0이면 보이지 않도록 해놓음  -->
              <div class="ma-2">댓글</div>
              <v-list 
                dense v-if="record?.replyList[record.replyList.length-1]?.replyId !== 0"
                >
                <v-list-item v-for="reply in record.replyList" :key="reply.replyId">
                  <v-list-item-title v-if="reply.replyId !== 0">
                    <div class="mb-2">
                      <div>
                        <div style="display: flex; justify-content: space-between;">
                          <div class="font-weight-bold mr-2">{{ reply.memberNickname }}</div>
                          <div v-if="reply.replyMemberId === memberId">
                            <span @click="openEditReplyForm(reply)" class="mr-1">수정 |</span>
                            <span @click="openDeleteReplyDialog(reply)">삭제</span>
                          </div>
                        </div>
                        <div>
                          <div style="white-space: normal">{{ reply.replyContent }}</div>
                        </div>
                      </div>
                      <v-form v-if="editReplyForm && reply.replyId === editReplyId">
                        <v-row>
                          <v-col>
                            <v-text-field v-model="editReplyContent" required></v-text-field>
                          </v-col>
                        </v-row>
                        <v-row>
                          <v-col>
                            <v-btn
                            :style="{ border: '2px solid ' + record.mainColor, color: record.mainColor }"
                            class= "button"
                            @click="editReply(reply)">수정</v-btn>
                            <v-btn 
                            :style="{ border: '2px solid ' + record.mainColor, color: record.mainColor }"
                            class= "button"
                            @click="cancelEditReply">취소</v-btn>
                          </v-col>
                        </v-row>
                      </v-form>
                    </div>
                  </v-list-item-title>
                </v-list-item>
              </v-list>

              <v-form class="d-flex align-center">
                <v-text-field class="ml-1" :value="newReplyContent[record.recordId]" @input="newReplyContent[record.recordId] = $event.target.value" required @keydown.enter.prevent="addReply(record.recordId); newReplyContent[record.recordId] = ''"></v-text-field>
                <v-btn 
                :style="{ border: '1px solid ' + record.mainColor, color: 'black', backgroundColor: record.mainColor }"
                class= "button"
                @click="addReply(record.recordId); newReplyContent[record.recordId] = ''" :disabled="newReplyContent === ''">등록</v-btn>
              </v-form>
          </v-card>
        </v-col>
      <!-- </v-row> -->
      <div class="msg" v-if="noMoreDown">
        더 이상 불러올 데이터가 없습니다.
      </div>
      <div class="msg" v-if="noData">
        {{ store.getters['record/recordOption']?.startDateStr }} 부터 5일간 작성된 조각을 찾을 수 없습니다.
      </div>
    </v-container>
      <v-btn
      @click="scrollToTop"
      style="position: fixed; bottom: 80px; right: 20px;"
    >
      <v-icon>mdi-chevron-up</v-icon>
    </v-btn>
  </v-container>
</template>

<style scoped>

.msg {
  text-align: center;
  margin-top: 12px;
}

.card {
  width: 100%;
  margin-top: 8px;
}

.media-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: auto;
}

.media {
  margin: 2px;
  width: 100%;
}

.button {
  margin: 6px;
}

.map-container {
  height: 150px;
  width: 100%;
}

.record-comment {
  font-size: 100%;
  line-height: 170%;
}

.loading-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 데스크탑 */
@media (min-width: 1200px) {
  .card {
    width: 800px;
  }

  .record-comment {
    font-size: 120%;
    text-align: left;
    line-height: 190%;
  }
}

.card-header {
  padding: 8px;
}
</style>