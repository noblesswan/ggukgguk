<script setup>
import { onMounted, computed } from 'vue';
import { useStore } from 'vuex';

const store = useStore();

const noticeList = computed(() => {
    return store.getters['admin/noticeList'];
});

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
});

onMounted(() => {
    getNoticeList();
});

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

function formatDate(date) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = date.getMinutes();

    const halfHour = hour > 12 ? hour - 12 : hour;
    const hourPrefix = hour >= 12 ? '오후' : '오전';

    return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day} ${hourPrefix} ${halfHour}시 ${minute}분`;
}

</script>

<template>
    <v-container>
        <h1>공지사항</h1>

        <v-expansion-panels>
            <v-expansion-panel
                v-for="notice in noticeList"
                :key="notice.noticeId"
            >
                <v-expansion-panel-title>
                    {{ notice.noticeTitle }}
                </v-expansion-panel-title>

                <v-expansion-panel-text>
                    <div class="mb-8" style="font-size: 85%;">{{ formatDate(new Date(notice.noticeCreatedAt)) }}</div>
                    <div style="white-space: pre-wrap">{{ notice.noticeContent }}</div>
                </v-expansion-panel-text>
            </v-expansion-panel>
        </v-expansion-panels>

        <v-pagination v-model="noticeOption.page" :total-visible="5" :length="totalPage"></v-pagination>
    </v-container>
</template>