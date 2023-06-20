<script setup>
import { computed, onMounted, watch, ref } from 'vue';
import { useStore } from 'vuex';
import { admin } from '@/api'

const store = useStore(); // vuex 스토어 사용

onMounted(() => {
    store.commit('admin/setContentPage', 1);
    getContentList();
})

const isLoading = ref(false);

const showRequest = ref(false);

const MAX_COMMENT_LENGTH = 1024;

function changeView() {
    showRequest.value = !showRequest.value;
    if (showRequest.value) {
        store.commit('admin/updateMediaFileRecheckRequestOption', {
            page: 1,
            size: 10,
            mediaFileId: null,
            mediaFileRecheckRequestStatus: null
        })
        getClaimList();
    } else {
        store.commit('admin/setContentPage', 1);
        getContentList();
    }
}

////////////////
// 미디어 및 조각 조회
const detailContentDiagVisible = ref(false);
const detailContentDaigContent = ref({});

const contentList = computed(() => {
    return store.getters['admin/contentList'];
})

const contentOption = computed(() => {
    return store.getters['admin/contentOption'];
});

const contentCurrentPage = computed(() => {
    return store.getters['admin/contentOption'].page;
});

const contentTotalPage = computed(() => {
    const totalItem = store.getters['admin/contentTotal'];
    const pageSize = store.getters['admin/contentOption'].size;
    const computed = Math.ceil(totalItem / pageSize);

    if (!computed) { // NaN인 경우 페이지네이션 컴포넌트의 Validation 로직에 걸리게 되므로 한 번 걸러준다
        return 1;
    }
    return computed;
})

watch(contentCurrentPage, () => {
    getContentList();
})

function getContentList() {
    isLoading.value = true;
    store.dispatch("admin/getContentList")
        .then(() => {
            isLoading.value = false;
            console.log('성공');
        })
        .catch((error) => {
            isLoading.value = false;
            console.error('컨텐츠 리스트 조회 실패');
            console.error(error);
        })
}

function showContentDetailDiag(item) {
    detailContentDaigContent.value = item;
    detailContentDiagVisible.value = true;
}

////////////////
// 이의제기 조회
const detailRequestDiagVisible = ref(false);
const detailRequestDaigContent = ref({});
const detailRequestDaigContentMediaUrl = ref('');

const requestReply = ref('');
const replyLength = computed(() => {
    return requestReply.value.length;
});
const STATUS = [{
        label: '처리전',
        value: 'BEFORE'
    },
    {
        label: '검토중',
        value: 'PROCEEDING'
    },
    {
        label: '거절',
        value: 'REJECTED'
    },
    {
        label: '인용',
        value: 'PASSED'
}];
const selectedStatus = ref('');

const claimList = computed(() => {
    return store.getters['admin/mediaFileRecheckRequest'];
})

const claimOption = computed(() => {
    return store.getters['admin/mediaFileRecheckRequestOption'];
});

const claimCurrentPage = computed(() => {
    return store.getters['admin/mediaFileRecheckRequestOption'].page;
});

const claimTotalPage = computed(() => {
    const totalItem = store.getters['admin/mediaFileRecheckRequestTotal'];
    const pageSize = store.getters['admin/mediaFileRecheckRequestOption'].size;
    const computed = Math.ceil(totalItem / pageSize);

    if (!computed) { // NaN인 경우 페이지네이션 컴포넌트의 Validation 로직에 걸리게 되므로 한 번 걸러준다
        return 1;
    }
    return computed;
})

watch(claimCurrentPage, () => {
    getClaimList();
})

function getClaimList() {
    isLoading.value = true;
    store.dispatch("admin/getMediaFileRecheckRequest")
        .then(() => {
            isLoading.value = false;
            console.log('성공');
        })
        .catch((error) => {
            isLoading.value = false;
            console.error('이의제기 리스트 조회 실패');
            console.error(error);
        })
}

function updateRequest() {
    if (replyLength.value > MAX_COMMENT_LENGTH) {
        alert(MAX_COMMENT_LENGTH + '를 초과하였습니다.')
    }

    const {
        mediaFileRecheckRequestId, mediaFileId, mediaTypeId,
        mediaFileRecheckRequestClaim, mediaFileRecheckRequestCreatedAt,
        memberId, memberName, memberEmail, memberPhone
    } = detailRequestDaigContent.value

    const mediaFileRecheckRequestReply = requestReply.value;
    const mediaFileRecheckRequestStatus = selectedStatus.value;

    admin.editMediaFileRecheckRequest({
        mediaFileRecheckRequestId, mediaFileId, mediaTypeId,
        mediaFileRecheckRequestClaim, mediaFileRecheckRequestReply,
        mediaFileRecheckRequestStatus, mediaFileRecheckRequestCreatedAt,
        memberId, memberName, memberEmail, memberPhone
    })
        .then(() => {
            isLoading.value = false;
            console.log('이의제기 업데이트 성공');

            // 성공시 모달을 닫고 새 값을 로드
            getClaimList();
            detailRequestDiagVisible.value = false;
        })
        .catch((error) => {
            isLoading.value = false;
            console.error('이의제기 업데이트 실패');
            console.error(error);

            alert('수정에 실패하였습니다. 다시 한 번 시도해주세요.')
        })
}

function showRequestDetailDiag(item) {
    detailRequestDaigContent.value = item;
    selectedStatus.value = item.mediaFileRecheckRequestStatus;
    requestReply.value = item.mediaFileRecheckRequestReply;
    detailRequestDaigContentMediaUrl.value = '';
    admin.getMediaFileWithCredential({
        mediaFileId: detailRequestDaigContent.value.mediaFileId,
        mediaType: detailRequestDaigContent.value.mediaTypeId
    })
    .then((response) => {
        console.log('미디어 파일 요청 성공');
        console.log(response);

        const mediaFile = new File([response.data], 'mediaFile');

        if (detailRequestDaigContent.value.mediaTypeId === 'image') {
            const reader = new FileReader();
            reader.onload = ev => {
                const previewImage = String(ev.target?.result)
                detailRequestDaigContentMediaUrl.value = previewImage;
            };
            reader.readAsDataURL(mediaFile);
        } else { // video
            detailRequestDaigContentMediaUrl.value = URL.createObjectURL(mediaFile)
        }
    })
    .catch((error) => {
        console.error('미디어 파일 요청 실패');
        console.error(error);
    })

    detailRequestDiagVisible.value = true;
}
</script>

<template>
    <v-overlay class="loading-overlay" v-model="isLoading" scroll-strategy="block" persistent>
        <v-progress-circular color="primary" indeterminate size="64"></v-progress-circular>
    </v-overlay>

    <v-dialog v-model="detailContentDiagVisible" width="auto">
        <v-card>
            <v-card-title>
                조각 및 미디어 정보
            </v-card-title>
            <v-card-text>
                <ul>
                    <li>조각 ID: {{ detailContentDaigContent.recordId }}</li>
                    <li>작성자 ID: {{ detailContentDaigContent.memberId }}</li>
                    <li>작성 일시: {{ detailContentDaigContent.recordCreatedAt }}</li>
                    <li>미디어 ID: {{ detailContentDaigContent.mediaFileId }}</li>
                    <li>미디어 타입: {{ detailContentDaigContent.mediaTypeId }}</li>
                    <li>모니터링 여부: {{ detailContentDaigContent.mediaFileChecked === 1 ? 'TRUE' : 'FALSE' }}</li>
                    <li>차단 여부: {{ detailContentDaigContent.mediaFileBlocked === 1 ? 'TRUE' : 'FALSE' }}</li>
                </ul>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" block @click="detailContentDiagVisible = false">닫기</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <v-dialog v-model="detailRequestDiagVisible" width="auto">
        <v-card>
            <v-card-title>
                이의제기 처리
            </v-card-title>
            <v-card-text>
                <img v-if="detailRequestDaigContent.mediaTypeId == 'image'"
                    :src="detailRequestDaigContentMediaUrl"
                    style="max-width: 600px">
                <video v-if="detailRequestDaigContent.mediaTypeId == 'video'"
                    :src="detailRequestDaigContentMediaUrl"
                    style="max-width: 600px"
                    controls muted></video>
                <ul>
                    <li>이의제기 ID: {{ detailRequestDaigContent.mediaFileRecheckRequestId }}</li>
                    <li>미디어 파일 ID: {{ detailRequestDaigContent.mediaFileId }}</li>
                    <li>미디어 타입: {{ detailRequestDaigContent.mediaTypeId }}</li>
                    <li>요청 일시: {{ detailRequestDaigContent.mediaFileRecheckRequestCreatedAt }}</li>
                    <li>요청 본문: {{ detailRequestDaigContent.mediaFileRecheckRequestClaim }}</li>
                    <li>회원 ID: {{ detailRequestDaigContent.memberId }}</li>
                    <li>회원 이름: {{ detailRequestDaigContent.memberName }}</li>
                    <li>회원 이메일: {{ detailRequestDaigContent.memberEmail }}</li>
                    <li>회원 연락처: {{ detailRequestDaigContent.memberPhone }}</li>
                </ul>

                <div class="mt-12">
                    <div class="mb-8">
                        <v-textarea
                            v-model="requestReply"
                            label="재확인 결과"
                            variant="outlined" />
                        <div class="comment-length">{{ replyLength }} / {{ MAX_COMMENT_LENGTH }}</div>
                    </div>

                    <div>
                        <div style="font-size: 80%;">
                            * '인용'으로 상태를 변경하는 동시에 해당 미디어의 차단은 해제됩니다
                        </div>
                        <v-select v-model="selectedStatus"
                            :items="STATUS"
                            item-title="label"
                            item-value="value"
                            label="현황 선택"></v-select>
                    </div>

                    <v-btn color="primary" @click="updateRequest">처리 현황 업데이트 및 이메일통보</v-btn>
                </div>
            </v-card-text>
            <v-card-actions>
                <v-btn color="secondaty" block @click="detailRequestDiagVisible = false">닫기</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <v-col class="mt-8">
        <v-row>
            <h2 class="section-title">조각 및 미디어 조회</h2>
        </v-row>
        <v-row>
            <v-btn @click="changeView" class="my-8">
                {{ showRequest ? '조각 및 미디어 일반 조회' : '차단 미디어 이의제기 조회' }}
            </v-btn>
        </v-row>
        <v-row class="d-flex flex-column align-center justify-center" v-if="!showRequest">
            <v-table style="width: 100%">
                <thead>
                    <tr>
                        <th class="text-left">
                            조각 ID
                        </th>
                        <th class="text-left">
                            작성자 ID
                        </th>
                        <th class="text-left">
                            작성 일시
                        </th>
                        <th class="text-left">
                            미디어 ID
                        </th>
                        <th class="text-left">
                            미디어 타입
                        </th>
                        <th class="text-left">
                            모니터링 여부
                        </th>
                        <th class="text-left">
                            차단 여부
                        </th>
                        <th class="text-left">
                            세부 정보
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in contentList" :key="item.recordId">
                        <td>{{ item.recordId }}</td>
                        <td>{{ item.memberId }}</td>
                        <td>{{ item.recordCreatedAt }}</td>
                        <td>{{ item.mediaFileId }}</td>
                        <td>{{ item.mediaTypeId }}</td>
                        <td>{{ item.mediaFileChecked === 1 ? 'TRUE' : 'FALSE' }}</td>
                        <td>{{ item.mediaFileBlocked === 1 ? 'TRUE' : 'FALSE' }}</td>
                        <td>
                            <v-btn @click="showContentDetailDiag(item)">보기</v-btn>
                        </td>
                    </tr>
                </tbody>
            </v-table>
            <v-pagination v-model="contentOption.page" :total-visible="6" :length="contentTotalPage"></v-pagination>
        </v-row>
        <v-row class="d-flex flex-column align-center justify-center" v-if="showRequest">
            <v-table style="width: 100%">
                <thead>
                    <tr>
                        <th class="text-left">
                            미디어 ID
                        </th>
                        <th class="text-left">
                            처리 현황
                        </th>
                        <th class="text-left">
                            요청 일시
                        </th>
                        <th class="text-left">
                            회원 ID
                        </th>
                        <th class="text-left">
                            회원 이름
                        </th>
                        <th class="text-left">
                            회원 이메일
                        </th>
                        <th class="text-left">
                            회원 연락처
                        </th>
                        <th class="text-left">
                            처리
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in claimList" :key="item.recordId">
                        <td>{{ item.mediaFileId }}</td>
                        <td>{{ item.mediaFileRecheckRequestStatus }}</td>
                        <td>{{ item.mediaFileRecheckRequestCreatedAt }}</td>
                        <td>{{ item.memberId }}</td>
                        <td>{{ item.memberName }}</td>
                        <td>{{ item.memberEmail }}</td>
                        <td>{{ item.memberPhone }}</td>
                        <td>
                            <v-btn @click="showRequestDetailDiag(item)">보기</v-btn>
                        </td>
                    </tr>
                </tbody>
            </v-table>
            <v-pagination v-model="claimOption.page" :total-visible="6" :length="claimTotalPage"></v-pagination>
        </v-row>
    </v-col>
</template>

<style scoped>
.loading-overlay {
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>