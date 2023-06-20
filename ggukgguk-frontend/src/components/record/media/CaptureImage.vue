<script setup>
import { onMounted, ref, defineEmits } from 'vue';

onMounted(() => {
    getMedia();
})

const emit = defineEmits(['captured', 'abort']);

const errorMsg = ref('');
const videoEl = ref(null);
const canvasEl = ref(null);
const pictureDataUri = ref('');
let videoStream = null;

const width = ref(0);
const height = ref(0);

const pictureBlob = ref(null);

const constraints = {
  audio: false,
  video: {
    facingMode: 'environment'
  }
};

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
      emit('captured', pictureBlob.value);
    }, 'image/jpeg');
  } else {
    clearphoto();
  }
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
          <video ref="videoEl" autoplay playsinline></video>
          <div>{{ errorMsg }}</div>
          <canvas :height="height" :width="width" ref="canvasEl" />
          <div class="toolbar">
            <v-btn @click="takePicture" icon="mdi-record-circle-outline"></v-btn>
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