<script setup>
import { ref } from 'vue';
import { useStore } from 'vuex';

const noticeTitle = ref('');
const noticeContent = ref('');

const store = useStore();

function uploadNotice() {
    store.dispatch("admin/uploadNotice", {noticeTitle: noticeTitle.value, noticeContent: noticeContent.value})
    .then((response) => {
        alert('성공');
        console.log(response);
    })
    .catch((error) => {
        console.error('실패');
        console.error(error);
    })
}
</script>

<template>
    {{ noticeTitle }} <br>
    {{ noticeContent }}

    <v-text-field label="제목" v-model="noticeTitle"></v-text-field>
    <v-textarea label="내용" v-model="noticeContent"></v-textarea>
    <v-btn @click="uploadNotice">게시</v-btn>
</template>