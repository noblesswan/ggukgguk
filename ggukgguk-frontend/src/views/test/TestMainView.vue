<script setup>
import { onMounted, ref } from 'vue';
import apiFactory from '@/api/apiFactory';
import ImageTake from '@/components/test/ImageTake.vue';
import AudioRecord from '@/components/test/AudioRecord.vue';
import ImageTakeUsingInput from '@/components/test/ImageTakeUsingInput.vue';
import VideoRecord from '@/components/test/VideoRecord.vue';

// const targetSource = "https://localhost:8443/api/record/media/3588f4cd-034a-4da8-afe0-27f83e6d7725?mediaType=video"
const targetSource = "3588f4cd-034a-4da8-afe0-27f83e6d7725"
const dataUri = ref('');
const axios = apiFactory.getInstance();

onMounted(() => {
    axios.get("/record/media/" + targetSource + "?mediaType=video", {
        responseType: 'blob'
    })
    .then((response) => {
        console.log(response.data);
        dataUri.value = URL.createObjectURL(response.data);
    })
})
</script>

<template>
    <video v-if="dataUri !== ''" :src="dataUri" controls/>


    <h1>꾹꾹 기술검증</h1>
    <h2>카메라 사용 테스트</h2>
    <image-take />

    <video-record />
    
    <h2>마이크 사용 테스트</h2>
    <audio-record />

    <h2>디바이스 자체 Input 태그를 통한 미디어 캡쳐 및 업로드</h2>
    <image-take-using-input />
</template>