<script setup>
import { onMounted, ref, reactive, watch, defineEmits } from 'vue';

const isMobile = ref(false);

onMounted(() => {
    if (navigator.userAgent.match(/Android/i) || navigator.userAgent.match(/webOS/i) || navigator.userAgent.match(/iPhone/i) ||
        navigator.userAgent.match(/iPad/i) || navigator.userAgent.match(/iPod/i) || navigator.userAgent.match(/BlackBerry/i) ||
        navigator.userAgent.match(/Windows Phone/i)) {
            isMobile.value = true;
    }
    getMedia();
})

const emit = defineEmits(['captured', 'aborted']);

const errorMsg = ref('');
const videoEl = ref(null);
let videoStream = null;

const width = ref(0);
const height = ref(0);

const shouldStop = ref(false);
const stopped = ref(true);
const recordedVideoBlob = ref(null);
const elapsedSeconds = ref(0);

const durationLimit = 60;

const constraints = reactive({
  audio: true,
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
    emitError(`The resolution ${v.width.exact}x${v.height.exact} px is not supported by your device.`);
  } else if (error.name === 'NotAllowedError') {
    emitError('Permissions have not been granted to use your camera and ' +
      'microphone, you need to allow the page access to your devices in ' +
      'order for the demo to work.');
  }
  emitError(`getUserMedia error: ${error.name}`, error);
}

function emitError(msg, error) {
  if (typeof error !== 'undefined') {
    errorMsg.value = msg;
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
    emit('captured', recordedVideoBlob.value);
  });

  console.log('미디어 레코더 시작');
  mediaRecorder.start(1000);
}

function stopRecord() {
  console.log('비디오 레코딩 중지');
  shouldStop.value = true;
}
</script>

<template>
    <div class="wrapper">
        <div class="close-btn">
            <div class="pr-12 pt-8">
              <v-btn icon @click="() => {emit('abort')}"><v-icon>mdi-window-close</v-icon></v-btn>
          </div>
        </div>

        <div class="inner">
          <video ref="videoEl" autoplay playsinline muted></video>
          <div>{{ errorMsg }}</div>
        </div>

        <div class="toolbar mt-8">
            <div>
                {{ elapsedSeconds }} / {{ durationLimit }}
            </div>

            <div>
                <v-btn @click="recordVideo" icon="mdi-record-circle-outline" v-if="stopped"></v-btn>
                <v-btn @click="stopRecord" icon="mdi-stop" v-if="!stopped"></v-btn>
            </div>

            <div>
                <v-btn @click="toggleFacingMode" icon="mdi-orbit-variant" v-if="isMobile && stopped"></v-btn>
            </div>
        </div>
    </div>
</template>

<style scoped>
    canvas {
        display: none;
    }

    video {
        width: 100vw;
        object-fit: contain;
    }

    .inner {
      text-align: center;
    }

    .close-btn {
      position: absolute;
      top: 0;
      right: 0;
      z-index: 1200;
    }

    .toolbar {
      position: fixed;
      left: 48vw;
      bottom: 20px;
    }

    .wrapper {
        background-color: #FFFFFF;
        position: absolute;
        top: 0;
        left: 0;
        height: 100vh;
        width: 100vw;
        z-index: 1100;
    }

    @media (min-width: 800px) {
      video {
        height: 93vh;
        object-fit: contain;
      }
    }
</style>