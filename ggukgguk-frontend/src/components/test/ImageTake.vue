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
// const accessToken = computed(() => {
//     return store.getters['auth/accessToken'];
// });

const error = ref('');
const videoEl = ref(null);
const canvasEl = ref(null);
const pictureDataUri = ref('');
let videoStream = null;

const width = ref(0);
const height = ref(0);

const pictureBlob = ref(null);

const constraints = reactive({
  audio: false,
  video: {
    facingMode: 'environment'
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
  width.value = videoTracks[0].getSettings().width;
  height.value = videoTracks[0].getSettings().height;

  console.log('Got stream with constraints:', constraints);
  console.log(`Using video device: ${videoTracks[0].label}`);
  videoEl.value.srcObject = stream;
  videoEl.value.play();
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

function clearphoto() {
  const context = canvasEl.value.getContext("2d");
  context.fillStyle = "#AAA";
  context.fillRect(0, 0, canvasEl.value.width, canvasEl.value.height);

  const data = canvasEl.value.toDataURL("image/jpg");
  pictureDataUri.value = data;
}

function takePicture() {
  const context = canvasEl.value.getContext("2d");
  
  if (width.value && height.value) {
    canvasEl.value.width = width.value;
    canvasEl.value.height = height.value;
    context.drawImage(videoEl.value, 0, 0)
    const dataUri = canvasEl.value.toDataURL("image/jpeg");
    pictureDataUri.value = dataUri;
    canvasEl.value.toBlob((blob) => {
      pictureBlob.value = blob;
    }, 'image/jpeg')
  } else {
    clearphoto();
  }
}

function uploadVideo() {
    console.log("Upload button clicked");

    const formData = new FormData();
    formData.append('recordComment', '이미지 조각 업로드 테스트중');
    formData.append('memberId', memberId.value);
    formData.append('mediaFile', pictureBlob.value);
    axios.post('/record', formData, {
        // headers: {
        //     Authorization: `Bearer ${accessToken.value}`
        // }
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
    <video :height="height" :width="width" ref="videoEl" autoplay playsinline></video>
    <div>{{ error }}</div>
    <canvas :height="height" :width="width" ref="canvasEl" />
    <v-btn @click="takePicture">사진촬영</v-btn>
    <img :src="pictureDataUri" width="500">
    <v-btn @click="uploadVideo">업로드</v-btn>
</template>