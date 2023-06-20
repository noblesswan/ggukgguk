<script setup>
import { ref, defineEmits } from 'vue';
import Recorder from '@/util/recorder';

const emit = defineEmits(['captured', 'abort']);

const format = ref('');
const audioSrc = ref('');
const pauseBtnLabel = ref('pause');
const nowRecording = ref(false);

const AudioContext = window.AudioContext || window.webkitAudioContext;
let rec;
let input;
const currentStream = ref(null);

function recordAudio(stream) {
    const audioContext = new AudioContext();
    format.value = "Format: 1 channel pcm @ " + audioContext.sampleRate/1000 + "kHz";

    currentStream.value = stream;

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
        pauseBtnLabel.value = 'resume';
	}else{
		rec.record();
        pauseBtnLabel.value = 'pause';
	}
}

function stopRecord() {
	console.log("Stop Button clicked");

  pauseBtnLabel.value = 'pause'; // 레이블 초기화
	
	rec.stop();

	currentStream.value.getAudioTracks()[0].stop();

	//create the wav blob and pass it on to createDownloadLink
	rec.exportWAV(bolbHandler);
  nowRecording.value = false;
}

function bolbHandler(blob) {
	audioSrc.value = URL.createObjectURL(blob);
  emit('captured', blob);
}
</script>

<template>
    <div class="wrapper">
      <div class="close-btn">
        <div class="pr-12 pt-8">
          <v-btn icon @click="() => {emit('abort')}"><v-icon>mdi-window-close</v-icon></v-btn>
        </div>
      </div>
      <div class="outer">
        <div class="inner">
          <div class="waveform-wrapper">
            <av-media :media="currentStream" type="wform">asdf</av-media>
          </div>
          <v-btn @click="startRecord" :disabled="nowRecording" icon="mdi-record" color="red"></v-btn>
          <v-btn @click="stopRecord" :disabled="!nowRecording" icon="mdi-stop" color="red"></v-btn>
          <v-btn @click="pauseRecord" :disabled="!nowRecording" :icon="pauseBtnLabel === 'pause' ? 'mdi-pause' : 'mdi-play-pause'"></v-btn>
        </div>
      </div>
    </div>
</template>

<style scoped>
    .close-btn {
      text-align: right;
    }

    .outer {
      display: flex;
      height: 85%;
      width: 100%;
      align-items: center;
    }

    .inner {
      text-align: center;
      width: 100%;
    }

    .wrapper {
        background-color: #FFFFFF;
        position: absolute;
        top: 0;
        left: 0;
        height: 100vh;
        width: 100vw;
        text-align: center;
        z-index: 1100;
    }
</style>