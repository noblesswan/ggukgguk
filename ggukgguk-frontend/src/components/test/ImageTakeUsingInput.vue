<script setup>
import { ref, computed } from 'vue';
// import { ref, computed, reactive, onMounted, watch } from 'vue';
import apiFactory from "../../api/apiFactory";
import store from '@/store';

const previewImg = ref(null);
const imgFile = ref(null);
const axios = apiFactory.getInstance();

const memberId = computed(() => {
    return store.getters['auth/memberInfo'].memberId;
});
const accessToken = computed(() => {
    return store.getters['auth/accessToken'];
});

function readUrl(e) {
  const input = e.target;
  if (input.files && input.files[0]) {
    var reader = new FileReader();
    reader.onload = function(e) {
      previewImg.value.src = e.target.result;
    };
    reader.readAsDataURL(input.files[0]);
  } else {
    previewImg.value.src = "";
  }
}

function onChangeFile(e) {
  readUrl(e)
  const input = e.target;
  if (input.files && input.files[0]) {
    imgFile.value = input.files[0];
  }
}

function uploadRecord() {
    console.log("Upload button clicked");

    const formData = new FormData();
    formData.append('recordComment', '이미지 조각 업로드 테스트중');
    formData.append('memberId', memberId.value);
    formData.append('mediaFile', imgFile.value);
    axios.post('/record', formData, {
        headers: {
            Authorization: `Bearer ${accessToken.value}`
        }
    }, {
        withCredentials: true
    })
    .then((response) => {
      alert('성공');
        console.log(response);
    })
    .catch((error) => {
        console.error(error);
    });
}
</script>

<template>
    <input type="file" accept="image/*" capture="environment" @change="onChangeFile">
    <img ref="previewImg"><br>
    <v-btn @click="uploadRecord">전송</v-btn>
</template>