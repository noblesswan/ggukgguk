<script setup>
import { onMounted, computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';

const route = useRoute();
const router = useRouter();
const store = useStore();
const isLoading = ref(false);
const alreadyPosted = ref(false);

const mediaFileRecheckRequestClaim = ref('');

const MAX_COMMENT_LENGTH = 1024;

const commentLength = computed(() => {
    return mediaFileRecheckRequestClaim.value.length;
});

const mediaFileId = computed(() => {
    return route.params.mediaFileId;
});

const mediaFileDetail = computed(() => {
    return store.getters['admin/mediaFileDetail'];
});

const complainList = computed(() => {
    return store.getters['admin/mediaFileRecheckRequest'];
});

function fetchRequestList() {
    console.log('옵션 변경');
    store.commit('admin/updateMediaFileRecheckRequestOption', {
        mediaFileId: mediaFileId.value,
        page: 1,
        size: 100
    });

    store.dispatch('admin/getMediaFileRecheckRequest')
        .then(() => {
            console.log('이의제기 불러오기 성공');
            if (complainList.value?.length > 0) {
                alreadyPosted.value = true;
            }
        })
        .catch(error => {
            console.error('이의제기 불러오기 실패');
            console.error(error);
        });
}

onMounted(() => {
    isLoading.value = true;

    store.dispatch('admin/getMediaFileDetail', mediaFileId.value)
    .then(() => {
        isLoading.value = false;
        console.log('미디어 파일 정보 불러오기 성공');
    })
    .catch((error) => {
        isLoading.value = false;
        console.error('미디어 파일 정보 불러오기 실패');
        console.error(error);
    })

    fetchRequestList();
})

function uploadRequest() {
    console.log('업로드 시작', mediaFileId.value, mediaFileRecheckRequestClaim.value);

    if(commentLength.value <= 0) {
        alert('본문 내용을 입력해주세요');
        return;
    }

    store.dispatch('admin/uploadMediaFileRecheckRequest', {
            mediaFileId: mediaFileId.value,
            mediaFileRecheckRequestClaim: mediaFileRecheckRequestClaim.value
    })
    .then(() => {
        isLoading.value = false;
        console.log('요청 작성 성공');
        router.go(0);
    })
    .catch((error) => {
        isLoading.value = false;
        console.error('요청 작성 실패');
        console.error(error);
    })
}
</script>

<template>
    <v-container>
        <h1>차단 미디어 상세정보</h1>
    </v-container>

    <v-container v-if="!isLoading">

        <h2>미디어 파일 정보</h2>
        <v-card class="ma-4 pa-4">
            <ul>
                <li>미디어 아이디: {{ mediaFileDetail.mediaFileId }}</li>
                <li>미디어 타입: {{ mediaFileDetail.mediaTypeId }}</li>
            </ul>
        </v-card>

        <h2 class="mt-12">모니터링 이력</h2>
        <v-card
            class="ma-4 pa-4"
            v-for="blockedHistory in mediaFileDetail.mediaFileBlockedHistory"
            :key="blockedHistory.mediaFileBlockedHistoryId">
            <ul>
                <li>감지 레이블: {{ blockedHistory.mediaFileDetectedLabel }}</li>
                <li>감지 정확도: {{ blockedHistory.mediaFileDetectedWeights }}</li>
                <li>감지 일시: {{ blockedHistory.mediaFileCheckedAt }}</li>
            </ul>
        </v-card>

        <h2 class="mt-12">미디어 파일 이의제기</h2>
        <div v-if="!alreadyPosted">
            <v-card
                class="ma-4 mb-8 pa-2 text-center"
                color="info">
                오인이라고 생각되는 경우 아래에서 이의제기를 하실 수 있습니다.
            </v-card>

            <v-textarea
            v-model="mediaFileRecheckRequestClaim"
            label="요청 내용 본문"
            variant="outlined" />

            <div class="mt-0">{{ commentLength }} / {{ MAX_COMMENT_LENGTH }}</div>

            <div class="my-12 text-center">
                <v-btn color="primary" @click="uploadRequest">제출</v-btn> 
            </div>
        </div>
        <div v-if="alreadyPosted">
            <v-card
                class="ma-4 mb-8 pa-2 text-center"
                color="info">
                이미 이의제기한 이력이 있습니다. 진행 결과가 업데이트되면 메일로 알려드리겠습니다.
            </v-card>

            <v-card
                class="ma-4 pa-4"
                v-for="complain in complainList"
                :key="complain.mediaFileRecheckRequestId">
                <ul>
                    <li>이의제기 본문: {{ complain.mediaFileRecheckRequestClaim }}</li>
                    <li v-if="complain.mediaFileRecheckRequestReply">재고 결과: {{ complain.mediaFileRecheckRequestReply }}</li>
                    <li>처리 현황: {{ complain.mediaFileRecheckRequestStatus }}</li>
                    <li>이의제기 일시: {{ complain.mediaFileRecheckRequestCreatedAt }}</li>
                </ul>
            </v-card>
        </div>
    </v-container>
</template>