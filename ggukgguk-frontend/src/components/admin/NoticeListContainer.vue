<script setup>
import { computed, onMounted, watch, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { admin } from '@/api';

const store = useStore();
const router = useRouter();

onMounted(() => {
    getNoticeList();
})

const showDetail = ref(false);
const isEditMode = ref(false);
const newNotice = ref({
    noticeId: 0,
    noticeTitle: '',
    noticeContent: ''
})
const showWrite = ref(false);

const noticeDetail = ref({});

const noticeList = computed(() => {
    return store.getters['admin/noticeList'];
})

const noticeOption = computed(() => {
    return store.getters['admin/noticeOption'];
});

const totalPage = computed(() => {
    const totalItem = store.getters['admin/noticeTotal'];
    const pageSize = store.getters['admin/noticeOption'].size;

    const computed = Math.ceil(totalItem / pageSize);
    if (!computed) {
        return 1;
    }
    return computed;
})

watch(noticeOption, () => {
    getNoticeList();
},
    { deep: true });

function getNoticeList() {
    store.dispatch("admin/getNoticeList")
        .then(() => {
            console.log('성공');
        })
        .catch((error) => {
            console.error('공지사항 리스트 조회 실패');
            console.error(error);
        })
}

function showDetailDialog(notice) {
    noticeDetail.value = notice;
    newNotice.value = {
        noticeId: 0,
        noticeTitle: '',
        noticeContent: ''
    };
    isEditMode.value = false;
    showDetail.value = true;
}

function updateNotice() {
    admin.editNotice(newNotice.value)
        .then(() => {
            router.go(0);
        })
        .catch((error) => {
            console.error(error);
        })
}

function addNotice() {
    admin.addNotice(newNotice.value)
        .then(() => {
            router.go(0);
        })
        .catch((error) => {
            console.error(error);
        })
}

function formatDate(date) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = date.getMinutes();

    return `${year}년 ${month < 10 ? '0' + month : month}월 ${day < 10 ? '0' + day : day}일 ${hour}시 ${minute}분`;
}

function intoEditMode(originalNotice) {
    const { noticeId, noticeTitle, noticeContent } = originalNotice;

    newNotice.value = {
        noticeId, noticeTitle, noticeContent
    }
    isEditMode.value = true;
}

function showWriteDialog() {
    newNotice.value = {
        noticeId: 0,
        noticeTitle: '',
        noticeContent: ''
    };
    showWrite.value = true;
}

function deleteNotice(noticeId){
    admin.deleteNotice({noticeId})
        .then(() => {
            router.go(0);
        })
        .catch((error) => {
            console.error(error);
        })
}
</script>

<template>
    <v-dialog v-model="showDetail" width="auto">
        <v-card>
            <v-card-title>
                <span v-if="!isEditMode">{{ noticeDetail.noticeTitle }}</span>
                <span v-if="isEditMode">
                    <v-text-field v-model="newNotice.noticeTitle" label="공지 제목" variant="outlined" />
                </span>
            </v-card-title>
            <v-card-text>
                <ul>
                    <li v-if="!isEditMode">내용 : {{ noticeDetail.noticeContent }}</li>
                    <li v-if="isEditMode">
                        <v-textarea v-model="newNotice.noticeContent" label="공지 내용" variant="outlined" />
                    </li>
                    <li>작성일: {{ formatDate(new Date(noticeDetail.noticeCreatedAt)) }}</li>
                </ul>
            </v-card-text>
            <v-card-actions>
                <v-btn v-if="!isEditMode" color="secondary" @click="intoEditMode(noticeDetail)">수정</v-btn>
                <v-btn v-if="!isEditMode" color="primary" @click="deleteNotice(noticeDetail.noticeId)">삭제</v-btn>
                <v-btn v-if="isEditMode" color="secondary" @click="updateNotice">수정완료</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <v-col class="mt-8">
        <v-row class="d-flex flex-column align-center justify-center">
            <v-table style="width: 100%">
                <thead>
                    <tr>
                        <th class="text-center">
                            제목
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in noticeList" :key="item.noticeId" class="text-center" style="cursor:pointer">
                        <td @click="showDetailDialog(item)">{{ item.noticeTitle }}</td>
                    </tr>
                </tbody>
            </v-table>
        </v-row>
        <v-row class="d-flex flex-column align-center justify-center">
            <v-pagination v-model="noticeOption.page" :total-visible="5" :length="totalPage"></v-pagination>
        </v-row>
        <v-row class="d-flex flex-column align-end justify-end">
            <v-btn @click="showWriteDialog">작성</v-btn>
        </v-row>
    </v-col>
    <v-dialog v-model="showWrite" width="auto">
        <v-card>
            <v-card-title>
                <span>
                    <v-text-field v-model="newNotice.noticeTitle" label="제목" variant="outlined" />
                </span>
            </v-card-title>
            <v-card-text>
                <ul>
                    <li>
                        <v-textarea v-model="newNotice.noticeContent" label="내용" variant="outlined" />
                    </li>
                </ul>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="showWrite = false">닫기</v-btn>
                <v-btn color="secondary" @click="addNotice()">작성완료</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<style scoped>
li {
    list-style-type: none;
}
</style>