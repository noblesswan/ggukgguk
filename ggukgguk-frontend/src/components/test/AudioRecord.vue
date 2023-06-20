<script setup>
import { ref, computed } from 'vue';
import Recorder from '@/util/recorder';
import apiFactory from "../../api/apiFactory";
import store from '@/store';

const axios = apiFactory.getInstance();

const format = ref('');
const audioSrc = ref('');
const pauseBtnLabel = ref('일시 중지');
const nowRecording = ref(false);
const recordComment = ref('');

const memberId = computed(() => {
    return store.getters['auth/memberInfo'].memberId;
});
const accessToken = computed(() => {
    return store.getters['auth/accessToken'];
});

const AudioContext = window.AudioContext || window.webkitAudioContext;
let rec;
let input;
let currentStream;
let audioBlob;

function recordAudio(stream) {
    const audioContext = new AudioContext();
    format.value = "Format: 1 channel pcm @ " + audioContext.sampleRate/1000 + "kHz";

    currentStream = stream;

    input = audioContext.createMediaStreamSource(stream);

    rec = new Recorder(input, {numChannels: 1});
    rec.record();
    console.log("Recording started");
}

async function startRecord() {
    nowRecording.value = true;
    console.log("Record button clicked");
    audioSrc.value = '';
    const constraints = { audio: true, video:false };

    try {
        const audioStream = await navigator.mediaDevices.getUserMedia(constraints);
        recordAudio(audioStream);
    } catch(err) {
        console.error(err);
        nowRecording.value = false;
    }
}

function pauseRecord() {
	console.log("Pause Button clicked rec.recording=", rec.recording );
	if (rec.recording){
		rec.stop();
        pauseBtnLabel.value = '녹음 재개';
	}else{
		rec.record();
        pauseBtnLabel.value = '일시 중지';
	}
}

function stopRecord() {
	console.log("Stop Button clicked");

    pauseBtnLabel.value = '일시 중지'; // 레이블 초기화
	
	rec.stop();

	currentStream.getAudioTracks()[0].stop();

	//create the wav blob and pass it on to createDownloadLink
	rec.exportWAV(bolbHandler);
    nowRecording.value = false;
}

function bolbHandler(blob) {
	audioSrc.value = URL.createObjectURL(blob);
    audioBlob = blob;
}

function uploadRecord() {
    console.log("Upload button clicked");

    const formData = new FormData();
    formData.append('recordComment', recordComment.value);
    formData.append('memberId', memberId.value);
    formData.append('mediaFile', audioBlob);
    axios.post('/record', formData, {
        headers: {
            Authorization: `Bearer ${accessToken.value}`
        }
    }, {
        withCredentials: true
    })
    .then((response) => {
        console.log(response);
    })
    .catch((error) => {
        console.error(error);
    });
}
</script>

<template>
    <v-textarea v-model="recordComment"></v-textarea>
    <div>{{ format }}</div>
    <v-btn @click="startRecord" :disabled="nowRecording">녹음 시작</v-btn>
    <v-btn @click="stopRecord" :disabled="!nowRecording">녹음 종료</v-btn>
    <v-btn @click="pauseRecord" :disabled="!nowRecording">{{ pauseBtnLabel }}</v-btn>
    <audio :src="audioSrc" controls v-if="audioSrc != ''"></audio>
    <v-btn @click="uploadRecord">업로드</v-btn>
</template>