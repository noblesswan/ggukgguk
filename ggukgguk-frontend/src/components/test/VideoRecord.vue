<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue';
import apiFactory from "../../api/apiFactory";
import store from '@/store';

onMounted(() => {
    getMedia();
})

const axios = apiFactory.getInstance();

const memberId = computed(() => {
    return store.getters['auth/memberInfo'].memberId;
});
const accessToken = computed(() => {
    return store.getters['auth/accessToken'];
});

const error = ref('');
const videoEl = ref(null);
const canvasEl = ref(null);
let videoStream = null;

const width = ref(1200);
const height = ref(0);

const shouldStop = ref(false);
const stopped = ref(true);
const recordedVideoBlob = ref(null);
const recordedVideoSrc = ref('');
const elapsedSeconds = ref(0);

const durationLimit = 60;

const constraints = reactive({
  audio: true,
  video: {
    facingMode: 'environment',
    width: 1280,
    height: 720,
  }
});

watch(constraints, () => {
    if (videoStream !== null) {
        videoStream.getTracks().forEach((track) => {
            track.stop();
            getMedia();
        });
    }
})

function handleSuccess(stream) {
  const videoTracks = stream.getVideoTracks();
  console.log('Got stream with constraints:', constraints);
  console.log(`Using video device: ${videoTracks[0].label}`);
  videoEl.value.srcObject = stream;
  videoEl.value.play();
}

function whenVideoCanPlay() {
  height.value = (videoEl.value.videoHeight / videoEl.value.videoWidth) * width.value;
}

function handleError(error) {
  if (error.name === 'OverconstrainedError') {
    const v = constraints.video;
    errorMsg(`The resolution ${v.width.exact}x${v.height.exact} px is not supported by your device.`);
  } else if (error.name === 'NotAllowedError') {
    errorMsg('Permissions have not been granted to use your camera and ' +
      'microphone, you need to allow the page access to your devices in ' +
      'order for the demo to work.');
  }
  errorMsg(`getUserMedia error: ${error.name}`, error);
}

function errorMsg(msg, error) {
  error.value = msg;
  if (typeof error !== 'undefined') {
    console.error(error);
  }
}

async function getMedia() {
  videoStream = null;

  try {
    videoStream = await navigator.mediaDevices.getUserMedia(constraints);
    handleSuccess(videoStream);
  } catch (err) {
    handleError(err);
  }
}

function toggleFacingMode() {
    const facingMode = constraints.video.facingMode;
    if (facingMode === 'environment') constraints.video.facingMode = 'user';
    else if (facingMode === 'user') constraints.video.facingMode = 'environment';
}

function recordVideo() {
  console.log('비디오 레코딩 시작');
  shouldStop.value = false;
  stopped.value = false;
  elapsedSeconds.value = 0;

  let mimeType;
  MediaRecorder.isTypeSupported('video/webm') ?
    mimeType = 'video/webm' :
    mimeType = 'video/mp4';

  const options = { 
    mimeType,
    videoBitsPerSecond: 2500000
  };
  const recordedChunks = [];
  const mediaRecorder = new MediaRecorder(videoStream, options);
  console.log(mediaRecorder.videoBitsPerSecond);
  mediaRecorder.addEventListener('dataavailable', (e) => {
    console.log('dataavailable');

    if (e.data.size > 0) {
      recordedChunks.push(e.data);
    }

    elapsedSeconds.value++;
    if (elapsedSeconds.value >= durationLimit) shouldStop.value = true; // 60초 경과시 자동 중지

    if(shouldStop.value === true && stopped.value === false) {
      console.log('MediaRecorder Stop 로직 진입');
      mediaRecorder.stop();
      stopped.value = true;
    }
  });

  mediaRecorder.addEventListener('stop', () => {
    recordedVideoBlob.value = new Blob(recordedChunks, { type: mimeType })
    recordedVideoSrc.value = URL.createObjectURL(recordedVideoBlob.value);
  });

  console.log('미디어 레코더 시작');
  mediaRecorder.start(1000);
}

function stopRecord() {
  console.log('비디오 레코딩 중지');
  shouldStop.value = true;
}

function uploadVideo() {
    console.log("Upload button clicked");

    const formData = new FormData();
    formData.append('recordComment', '동영상 조각 업로드 테스트중');
    formData.append('memberId', memberId.value);
    formData.append('mediaFile', recordedVideoBlob.value);
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
    <v-btn @click="toggleFacingMode">전면 후면 토글</v-btn><br>
    <video :height="height" :width="width" @canplay="whenVideoCanPlay" ref="videoEl" autoplay playsinline></video>
    <div>{{ error }}</div>
    <canvas :height="height" :width="width" ref="canvasEl" />

    촬영 시간: {{ elapsedSeconds }}

    <v-btn @click="recordVideo">촬영 시작</v-btn>
    <v-btn @click="stopRecord">촬영 중지</v-btn>

    <video v-if="recordedVideoSrc != ''" :height="height" :width="width"  :src="recordedVideoSrc"  autoplay playsinline controls></video>

    <v-btn @click="uploadVideo">업로드</v-btn>
</template>